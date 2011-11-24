/**
 * ServiceData, part of Aptoide
 * Copyright (C) 2011 Duarte Silveira
 * duarte.silveira@caixamagica.pt
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/
package cm.aptoide.pt.data;

import java.util.HashMap;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;
import cm.aptoide.pt.AIDLAptoide;
import cm.aptoide.pt.Aptoide;
import cm.aptoide.pt.R;
import cm.aptoide.pt.data.database.ManagerDatabase;
import cm.aptoide.pt.data.notifications.ManagerNotifications;
import cm.aptoide.pt.data.preferences.ManagerPreferences;
import cm.aptoide.pt.data.system.ManagerSystemSync;
import cm.aptoide.pt.data.system.ScreenDimensions;
import cm.aptoide.pt.data.views.ViewDisplayListApps;
import cm.aptoide.pt.debug.AptoideLog;
import cm.aptoide.pt.debug.InterfaceAptoideLog;

/**
 * ServiceData, Aptoide's data I/O manager for the activity classes
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class AptoideServiceData extends Service implements InterfaceAptoideLog{

	private final String TAG = "Aptoide-ServiceData";
	private boolean isRunning = false;
	
	private HashMap<EnumServiceDataCallback, AIDLAptoide> serviceClients;
	private EnumServiceDataCall latestRequest;

	private ManagerPreferences managerPreferences;
	private ManagerSystemSync managerSystemSync;
	private ManagerDatabase managerDatabase;
	private ManagerNotifications managerNotifications;
	
	
	/**
	 * When binding to the service, we return an interface to our AIDL stub
	 * allowing clients to send requests to the service.
	 */
	@Override
	public IBinder onBind(Intent intent) {
		Log.d("Aptoide ServiceData", "binding new client");
		return aptoideServiceDataCallReceiver;
	}
	
	private final AIDLAptoideServiceData.Stub aptoideServiceDataCallReceiver = new AIDLAptoideServiceData.Stub() {
		
		@Override
		public void callSyncInstalledPackages() throws RemoteException {
	    	syncInstalledPackages();			
		}
		
		@Override
		public void callStoreScreenDimensions(ScreenDimensions screenDimensions) throws RemoteException {
			storeScreenDimensions(screenDimensions);	
		}
		
		@Override
		public void callRegisterInstalledPackagesObserver(AIDLAptoide installedPackagesObserver) throws RemoteException {
			registerInstalledDataObserver(installedPackagesObserver);
		}
		
		@Override
		public ViewDisplayListApps callGetInstalledPackages(int offset, int range) throws RemoteException {
			return getInstalledPackes(offset, range);
		}
	}; 
	
	public void registerInstalledDataObserver(AIDLAptoide installedPackagesObserver){
		serviceClients.put(EnumServiceDataCallback.UPDATE_INSTALLED_LIST, installedPackagesObserver);
    	AptoideLog.d(AptoideServiceData.this, "Registered Installed Data Observer");
	}
	
	
	public String getTag() {
		return TAG;
	}


	public ManagerPreferences getManagerPreferences() {
		return managerPreferences;
	}
	
	public ManagerSystemSync getManagerSystemSync() {
		return managerSystemSync;
	}	
	
	public ManagerDatabase getManagerDatabase() {
		return managerDatabase;
	}

	public ManagerNotifications getManagerNotifications() {
		return managerNotifications;
	}




	@Override
	public void onCreate() {
	    if(!isRunning){
			serviceClients = new HashMap<EnumServiceDataCallback, AIDLAptoide>();
			
			managerPreferences = new ManagerPreferences(this);
			managerSystemSync = new ManagerSystemSync(this);
			managerDatabase = new ManagerDatabase(this);
			managerNotifications = new ManagerNotifications(this);
			
			
			checkForSelfUpdate();
		    
			isRunning = true;
			Log.d("Aptoide ServiceData", "Service started");
	    }
		super.onCreate();
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		if(intent.getData() != null){
			launchAptoide();
			
			//TODO myapp handling section -- move partly to download/xml/ classes
		    
//			private static final int LOAD_TABS = 0;			//TODO Probably unneeded
//		    private Vector<String> server_lst = null;	//TODO to dataService
//		    private Vector<String[]> get_apps = null;	//TODO to dataService
			
//			private Handler startHandler = new Handler() {
		//
//				@Override
//				public void handleMessage(Message msg) {
//					switch(msg.what){
//					case LOAD_TABS:
//						Intent i = new Intent(Aptoide.this, RemoteInTab.class);
//						Intent get = getIntent();
//						if(get.getData() != null){
//							String uri = get.getDataString();
//								Log.d("Aptoide-startHandler", "receiving a myapp file");
//								downloadMyappFile(uri);
//								try {
//									parseMyappFile(TMP_MYAPP_FILE);
//									i.putExtra("repos", server_lst);
//									if(get_apps.size() > 0){
//										//i.putExtra("uri", TMP_SRV_FILE);
//										i.putExtra("apps", get_apps);
		//	
//									}
//								} catch (Exception e) {
//									Toast.makeText(mctx, mctx.getString(R.string.failed_install), Toast.LENGTH_LONG);
//									onCreate(savedInstanceState);
//								}
//						}
//						startActivityForResult(i,0);
//						break;
//					}
//					super.handleMessage(msg);
//				} 
//		    }; 

			
			
//			private void downloadMyappFile(String myappUri){
//				try{
//					keepScreenOn.acquire();
//					
//					BufferedInputStream getit = new BufferedInputStream(new URL(myappUri).openStream());
		//
//					File file_teste = new File(TMP_MYAPP_FILE);
//					if(file_teste.exists())
//						file_teste.delete();
//					
//					FileOutputStream saveit = new FileOutputStream(TMP_MYAPP_FILE);
//					BufferedOutputStream bout = new BufferedOutputStream(saveit,1024);
//					byte data[] = new byte[1024];
//					
//					int readed = getit.read(data,0,1024);
//					while(readed != -1) {
//						bout.write(data,0,readed);
//						readed = getit.read(data,0,1024);
//					}
//					
//					keepScreenOn.release();
//					
//					bout.close();
//					getit.close();
//					saveit.close();
//				} catch(Exception e){
//					AlertDialog p = new AlertDialog.Builder(this).create();
//					p.setTitle(getText(R.string.top_error));
//					p.setMessage(getText(R.string.aptoide_error));
//					p.setButton(getText(R.string.btn_ok), new DialogInterface.OnClickListener() {
//					      public void onClick(DialogInterface dialog, int which) {
//					          return;
//					        } });
//					p.show();
//				}
//			}
		//	
//			private void parseMyappFile(String file){
//				SAXParserFactory spf = SAXParserFactory.newInstance();
//			    try {
//			    	keepScreenOn.acquire();
//			    	
//			    	SAXParser sp = spf.newSAXParser();
//			    	XMLReader xr = sp.getXMLReader();
//			    	NewServerRssHandler handler = new NewServerRssHandler(this);
//			    	xr.setContentHandler(handler);
//			    	
//			    	InputStreamReader isr = new FileReader(new File(file));
//			    	InputSource is = new InputSource(isr);
//			    	xr.parse(is);
//			    	File xml_file = new File(file);
//			    	xml_file.delete();
//			    	server_lst = handler.getNewSrvs();
//			    	get_apps = handler.getNewApps();
//			    	
//			    	keepScreenOn.release();
//			    	
//			    } catch (IOException e) {
//			    	e.printStackTrace();
//			    } catch (SAXException e) {
//			    	e.printStackTrace();
//			    } catch (ParserConfigurationException e) {
//					e.printStackTrace();
//				}
//			}
		//	
//			private void parseXmlString(String file){
//				SAXParserFactory spf = SAXParserFactory.newInstance();
//			    try {
//			    	keepScreenOn.acquire();
//			    	
//			    	SAXParser sp = spf.newSAXParser();
//			    	XMLReader xr = sp.getXMLReader();
//			    	NewServerRssHandler handler = new NewServerRssHandler(this);
//			    	xr.setContentHandler(handler);
//			    	
//			    	InputSource is = new InputSource();
//			    	is.setCharacterStream(new StringReader(file));
//			    	xr.parse(is);
//			    	server_lst = handler.getNewSrvs();
//			    	get_apps = handler.getNewApps();
//			    	
//			    	keepScreenOn.release();
//			    	
//			    } catch (IOException e) {
//			    } catch (SAXException e) {
//			    } catch (ParserConfigurationException e) {
//				}
//			}
			
			
		//--------------------------------------------------------------------------

			
		}
		super.onStart(intent, startId);
		
	}


	@Override
	public void onDestroy() {
		managerNotifications.destroy();
		Toast.makeText(this, R.string.aptoide_stopped, Toast.LENGTH_LONG).show();
		stopSelf();
		Log.d("Aptoide ServiceData", "Service stopped");
		super.onDestroy();
	}

	
	public void checkForSelfUpdate(){	//TODO use NotificationManager class to load Splash Activity with it's progress bar as selfupdate activity
//   		try{
//			if( this.managerSystemSync.getAptoideVersionInUse() < Integer.parseInt( getXmlElement("versionCode") ) ){
//				Log.d("Aptoide-VersionCode", "Using version "+pkginfo.versionCode+", suggest update!");
//				requestUpdateSelf();
//			}else{
//				proceed();
//			}
//   		}catch(Exception e){
//   			e.printStackTrace();
//   			proceed();
//   		}
	}
	
	public void syncInstalledPackages(){
		managerDatabase.insertInstalledApplications(managerSystemSync.getInstalledApps());
    	AptoideLog.d(AptoideServiceData.this, "Sync'ed Installed Packages");
	}
	
//	public void newListInstalledAppsAvailable(){
//		try {
//			serviceClients.get(EnumServiceDataCallback.UPDATE_INSTALLED_LIST).newListDataAvailable();
//		} catch (RemoteException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		;
//	}
	
	public void storeScreenDimensions(ScreenDimensions screenDimensions){
		managerPreferences.setScreenDimensions(screenDimensions);
		AptoideLog.d(AptoideServiceData.this, "Stored Screen Dimensions: "+managerPreferences.getScreenDimensions());
	}
	
	public ViewDisplayListApps getInstalledPackes(int offset, int range){
		AptoideLog.d(AptoideServiceData.this, "Getting Installed Packages");
		return managerDatabase.getInstalledAppsDisplayInfo(offset, range);
	}
	
	 
	

	public void launchAptoide() {
		Intent aptoide = new Intent(this, Aptoide.class);
		aptoide.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(aptoide);
	}
	
	
	public ClientStatistics getStatistics(){
		ClientStatistics statistics = new ClientStatistics(managerSystemSync.getAptoideVersionNameInUse());
		managerPreferences.completeStatistics(statistics);
		return statistics;
	}

}