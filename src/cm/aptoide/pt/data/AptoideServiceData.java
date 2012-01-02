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
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;
import cm.aptoide.pt.AIDLAppInfo;
import cm.aptoide.pt.AIDLAptoideInterface;
import cm.aptoide.pt.Aptoide;
import cm.aptoide.pt.R;
import cm.aptoide.pt.Splash;
import cm.aptoide.pt.data.cache.ManagerCache;
import cm.aptoide.pt.data.cache.ViewCache;
import cm.aptoide.pt.data.database.ManagerDatabase;
import cm.aptoide.pt.data.display.ViewDisplayAppVersionsInfo;
import cm.aptoide.pt.data.display.ViewDisplayListApps;
import cm.aptoide.pt.data.downloads.EnumDownloadType;
import cm.aptoide.pt.data.downloads.ManagerDownloads;
import cm.aptoide.pt.data.downloads.ViewDownloadStatus;
import cm.aptoide.pt.data.model.ViewRepository;
import cm.aptoide.pt.data.notifications.EnumNotificationTypes;
import cm.aptoide.pt.data.notifications.ManagerNotifications;
import cm.aptoide.pt.data.notifications.ViewNotification;
import cm.aptoide.pt.data.preferences.ManagerPreferences;
import cm.aptoide.pt.data.system.ManagerSystemSync;
import cm.aptoide.pt.data.system.ViewScreenDimensions;
import cm.aptoide.pt.data.xml.EnumInfoType;
import cm.aptoide.pt.data.xml.ManagerXml;
import cm.aptoide.pt.data.xml.RepoBareParser;
import cm.aptoide.pt.data.xml.ViewXmlParse;
import cm.aptoide.pt.debug.AptoideLog;
import cm.aptoide.pt.debug.InterfaceAptoideLog;

/**
 * ServiceData, Aptoide's data I/O manager for the activity classes
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class AptoideServiceData extends Service implements InterfaceAptoideLog {

	private final String TAG = "Aptoide-ServiceData";
	private boolean isRunning = false;
	
	private HashMap<EnumServiceDataCallback, AIDLAptoideInterface> aptoideClients;
	private HashMap<Integer, AIDLAppInfo> appInfoClients;
	private EnumServiceDataCall latestRequest;

	private ManagerPreferences managerPreferences;
	private ManagerSystemSync managerSystemSync;
	private ManagerDatabase managerDatabase;
	private ManagerDownloads managerDownloads;
	private ManagerNotifications managerNotifications;
	private ManagerXml managerXml;
	
	
	/**
	 * When binding to the service, we return an interface to our AIDL stub
	 * allowing clients to send requests to the service.
	 */
	@Override
	public IBinder onBind(Intent intent) {
		AptoideLog.d(AptoideServiceData.this, "binding new client");
		return aptoideServiceDataCallReceiver;
	}
	
	private final AIDLAptoideServiceData.Stub aptoideServiceDataCallReceiver = new AIDLAptoideServiceData.Stub() {
		
		@Override
		public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
			try {
				return super.onTransact(code, data, reply, flags);
			} catch (RuntimeException e) {
				Log.w("MyClass", "Unexpected remote exception", e);
				throw e;
			}
		}

		@Override
		public void callSyncInstalledPackages() throws RemoteException {
	    	syncInstalledPackages();			
		}
		
		@Override
		public void callStoreScreenDimensions(ViewScreenDimensions screenDimensions) throws RemoteException {
			storeScreenDimensions(screenDimensions);	
		}

		@Override
		public void callRegisterAvailablePackagesObserver(AIDLAptoideInterface availablePackagesObserver) throws RemoteException {
			registerAvailableDataObserver(availablePackagesObserver);
		}
		
		@Override
		public void callRegisterInstalledPackagesObserver(AIDLAptoideInterface installedPackagesObserver) throws RemoteException {
			registerInstalledDataObserver(installedPackagesObserver);
		}
		
		@Override
		public ViewDisplayListApps callGetInstalledPackages(int offset, int range) throws RemoteException {
			return getInstalledPackages(offset, range);
		}

		@Override
		public void callAddRepo(ViewRepository repository) throws RemoteException {
			addRepoBare(repository);			
		}

		@Override
		public ViewDisplayListApps callGetAvailablePackages(int offset, int range) throws RemoteException {
			return getAvailablePackages(offset, range);
		}

		@Override
		public ViewDisplayListApps callGetUpdatablePackages(int offset, int range) throws RemoteException {
			return getUpdatablePackages(offset, range);
		}

		@Override
		public void callRegisterAppInfoObserver(AIDLAppInfo appInfoObserver, int appHashid) throws RemoteException {
			registerAppInfoObserver(appInfoObserver, appHashid);	//TODO investigate why null pointer exception on appInfoObserver?? 
		}

		@Override
		public void CallFillAppInfo(int appHashid) throws RemoteException {
			fillAppInfo(appHashid);
			
		}

		@Override
		public ViewDisplayAppVersionsInfo callGetAppInfo(int appHashid) throws RemoteException {
			return getAppInfo(appHashid);
		}
	}; 

	public void registerAvailableDataObserver(AIDLAptoideInterface availablePackagesObserver){
		aptoideClients.put(EnumServiceDataCallback.UPDATE_AVAILABLE_LIST, availablePackagesObserver);
    	AptoideLog.d(AptoideServiceData.this, "Registered Available Data Observer");
	}
	
	public void registerInstalledDataObserver(AIDLAptoideInterface installedPackagesObserver){
		aptoideClients.put(EnumServiceDataCallback.UPDATE_INSTALLED_LIST, installedPackagesObserver);
    	AptoideLog.d(AptoideServiceData.this, "Registered Installed Data Observer");
	}
	
	
	public void registerAppInfoObserver(AIDLAppInfo appInfoObserver, int appHashid){
		appInfoClients.put(appHashid, appInfoObserver);
    	AptoideLog.d(AptoideServiceData.this, "Registered App Info Observer: "+appHashid);
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
	
	public ManagerDownloads getManagerDownloads() {
		return managerDownloads;
	}
	
	public ManagerCache getManagerCache() {
		return managerDownloads.getManagerCache();
	}

	public ManagerNotifications getManagerNotifications() {
		return managerNotifications;
	}

	public ManagerXml getManagerXml(){
		return managerXml;
	}



	@Override
	public void onCreate() {
	    if(!isRunning){
	    	splash();
	    	
			aptoideClients = new HashMap<EnumServiceDataCallback, AIDLAptoideInterface>();
			appInfoClients = new HashMap<Integer, AIDLAppInfo>();
			
			managerPreferences = new ManagerPreferences(this);
			managerSystemSync = new ManagerSystemSync(this);
			managerDatabase = new ManagerDatabase(this);
			managerDownloads = new ManagerDownloads(this);
			managerNotifications = new ManagerNotifications(this);
			managerXml = new ManagerXml(this);
			
			
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
	
	public void storeScreenDimensions(ViewScreenDimensions screenDimensions){
		managerPreferences.setScreenDimensions(screenDimensions);
		AptoideLog.d(AptoideServiceData.this, "Stored Screen Dimensions: "+managerPreferences.getScreenDimensions());
	}
	
	public void syncInstalledPackages(){
		new Thread(){
			public void run(){
				this.setPriority(Thread.MAX_PRIORITY);
				managerDatabase.insertInstalledApplications(managerSystemSync.getInstalledApps());
				AptoideLog.d(AptoideServiceData.this, "Sync'ed Installed Packages");
				
				managerSystemSync.cacheInstalledIcons();
			}
		}.start();
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
	
	public void updateInstalledLists(){
		try {
			aptoideClients.get(EnumServiceDataCallback.UPDATE_INSTALLED_LIST).newInstalledListDataAvailable();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public void refreshAvailableDisplay(){
		try {
			aptoideClients.get(EnumServiceDataCallback.UPDATE_AVAILABLE_LIST).refreshAvailableDisplay();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void updateAvailableLists(){
		try {
			aptoideClients.get(EnumServiceDataCallback.UPDATE_AVAILABLE_LIST).newAvailableListDataAvailable();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	public void addRepoBare(final ViewRepository originalRepository){
		try{

			new Thread(){
				public void run(){
					ViewRepository repository = originalRepository;
					this.setPriority(Thread.MAX_PRIORITY);
					if(!managerDownloads.isConnectionAvailable()){
						AptoideLog.d(AptoideServiceData.this, "No connection");	//TODO raise exception to ask for what to do
					}
					if(!getManagerCache().isFreeSpaceInSdcard()){
						//TODO raise exception
					}
					ViewCache cache = managerDownloads.startRepoBareDownload(repository);
					repository.setDelta(cache.getMd5sum());
					
					managerXml.repoBareParse(repository, cache);
					//TODO find some way to track global parsing completion status, probably in managerXml
				}
			}.start();


		} catch(Exception e){
			/** this should never happen */
			//TODO handle exception
			e.printStackTrace();
		}
	}
	
	public void parsingRepoBareFinished(ViewRepository repository){
		updateAvailableLists();
		addRepoIconsInfo(repository);
	}
	
	public void addRepoIconsInfo(final ViewRepository repository){
		try{

			new Thread(){
				public void run(){
					this.setPriority(Thread.MAX_PRIORITY);
					if(!managerDownloads.isConnectionAvailable()){
						AptoideLog.d(AptoideServiceData.this, "No connection");	//TODO raise exception to ask for what to do
					}
					if(!getManagerCache().isFreeSpaceInSdcard()){
						//TODO raise exception
					}
					ViewCache cache = managerDownloads.startRepoIconDownload(repository);
					
					managerXml.repoIconParse(repository, cache);
					//TODO find some way to track global parsing completion status, probably in managerXml
				}
			}.start();


		} catch(Exception e){
			/** this should never happen */
			//TODO handle exception
			e.printStackTrace();
		}
	}
	
	public void parsingRepoIconsFinished(ViewRepository repository){
		addRepoStats(repository);
		getRepoIcons(new ViewDownloadStatus(repository, Constants.FIRST_ELEMENT, EnumDownloadType.ICON));
	}
	
	public void addRepoStats(final ViewRepository repository){
		try{

			new Thread(){
				public void run(){
					this.setPriority(Thread.MAX_PRIORITY);
					if(!managerDownloads.isConnectionAvailable()){
						AptoideLog.d(AptoideServiceData.this, "No connection");	//TODO raise exception to ask for what to do
					}
					if(!getManagerCache().isFreeSpaceInSdcard()){
						//TODO raise exception
					}
					ViewCache cache = managerDownloads.startRepoDownload(repository, EnumInfoType.STATS);
					
					managerXml.repoStatsParse(repository, cache);
					//TODO find some way to track global parsing completion status, probably in managerXml
				}
			}.start();


		} catch(Exception e){
			/** this should never happen */
			//TODO handle exception
			e.printStackTrace();
		}
	}
	
	public void parsingRepoStatsFinished(ViewRepository repository){
		updateAvailableLists();
	}
	
	public void getRepoIcons(final ViewDownloadStatus downloadStatus){
		if(downloadStatus.getRepository().getSize() < downloadStatus.getOffset()){
			refreshAvailableDisplay();
			return;
		}else{
			if(downloadStatus.getOffset() > Constants.FIRST_ELEMENT){
				refreshAvailableDisplay();
			}

			try{

				new Thread(){
					public void run(){
						this.setPriority(Thread.MAX_PRIORITY);
						if(!managerDownloads.isConnectionAvailable()){
							AptoideLog.d(AptoideServiceData.this, "No connection");	//TODO raise exception to ask for what to do
						}
						if(!getManagerCache().isFreeSpaceInSdcard()){
							//TODO raise exception
						}

						managerDownloads.getRepoIcons(downloadStatus, managerDatabase.getIconsDownloadInfo(downloadStatus.getRepository(), downloadStatus.getOffset(), Constants.DISPLAY_LISTS_CACHE_SIZE));
						//TODO find some way to track global parsing completion status, probably in managerXml
					}
				}.start();


			} catch(Exception e){
				/** this should never happen */
				//TODO handle exception
				e.printStackTrace();
			}
			
		}
	}
	
//	public void getRepoIconsExtraordinarily(final ViewRepository repository, final int offset){
//		try{
//
//			new Thread(){
//				public void run(){
//					this.setPriority(Thread.MAX_PRIORITY);
//					if(!managerDownloads.isConnectionAvailable()){
//						AptoideLog.d(AptoideServiceData.this, "No connection");	//TODO raise exception to ask for what to do
//					}
//					if(!getManagerCache().isFreeSpaceInSdcard()){
//						//TODO raise exception
//					}
//					managerDownloads.getRepoIconsExtraordinarily(repository, managerDatabase.getIconsDownloadInfo(repository, offset, Constants.SIZE_CACHE_OF_DISPLAY_LISTS));
//					//TODO find some way to track global parsing completion status, probably in managerXml
//				}
//			}.start();
//
//
//		} catch(Exception e){
//			/** this should never happen */
//			//TODO handle exception
//			e.printStackTrace();
//		}
//	}
	
//	public void addRepoDownloadsInfo(final ViewRepository repository){
//		try{
//
//			new Thread(){
//				public void run(){
//					this.setPriority(Thread.MAX_PRIORITY);
//					if(!managerDownloads.isConnectionAvailable()){
//						AptoideLog.d(AptoideServiceData.this, "No connection");	//TODO raise exception to ask for what to do
//					}
//					if(!getManagerCache().isFreeSpaceInSdcard()){
//						//TODO raise exception
//					}
//					ViewCache cache = managerDownloads.startRepoAppDownloads(repository);
//					
//					managerXml.repoDownloadParse(repository, cache);
//					//TODO find some way to track global parsing completion status, probably in managerXml
//				}
//			}.start();
//
//
//		} catch(Exception e){
//			/** this should never happen */
//			//TODO handle exception
//			e.printStackTrace();
//		}
//	}
	
	
	public void fillAppInfo(final int appHashid){	/***************** HERE ***************************/
		ViewRepository repository = managerDatabase.getAppRepo(appHashid);
		if(repository == null){
			return;
		}
		//TODO parallel check icon, download immediately if necessary
		//TODO parallel check if the 3 types of info already exist, and only if not get them
		addRepoDownloadInfo(repository, appHashid);
		addRepoAppStats(repository, appHashid);
		addRepoAppExtras(repository, appHashid);
		//TODO parallel get Comments
	}
	
	
	public void addRepoDownloadInfo(final ViewRepository repository, final int appHashid){
		try{

			new Thread(){
				public void run(){
					this.setPriority(Thread.MAX_PRIORITY);
					if(!managerDownloads.isConnectionAvailable()){
						AptoideLog.d(AptoideServiceData.this, "No connection");	//TODO raise exception to ask for what to do
					}
					if(!getManagerCache().isFreeSpaceInSdcard()){
						//TODO raise exception
					}
					ViewCache cache = managerDownloads.startRepoAppDownload(repository, appHashid, EnumInfoType.DOWNLOAD);
					
					managerXml.repoAppDownloadParse(repository, cache, appHashid);
					//TODO find some way to track global parsing completion status, probably in managerXml
				}
			}.start();


		} catch(Exception e){
			/** this should never happen */
			//TODO handle exception
			e.printStackTrace();
		}
	}
	
	public void parsingRepoAppDownloadInfoFinished(ViewRepository repository, int appHashid){
		updateAppInfo(appHashid, EnumServiceDataCallback.UPDATE_APP_DOWNLOAD_INFO);
	}
	
	
	
	public void addRepoAppStats(final ViewRepository repository, final int appHashid){
		try{

			new Thread(){
				public void run(){
					this.setPriority(Thread.MAX_PRIORITY);
					if(!managerDownloads.isConnectionAvailable()){
						AptoideLog.d(AptoideServiceData.this, "No connection");	//TODO raise exception to ask for what to do
					}
					if(!getManagerCache().isFreeSpaceInSdcard()){
						//TODO raise exception
					}
					ViewCache cache = managerDownloads.startRepoAppDownload(repository, appHashid, EnumInfoType.STATS);
					
					managerXml.repoAppStatsParse(repository, cache, appHashid);
					//TODO find some way to track global parsing completion status, probably in managerXml
				}
			}.start();


		} catch(Exception e){
			/** this should never happen */
			//TODO handle exception
			e.printStackTrace();
		}
	}
	
	public void parsingRepoAppStatsFinished(ViewRepository repository, int appHashid){
		updateAppInfo(appHashid, EnumServiceDataCallback.UPDATE_APP_STATS);
	}
	
	
	
	public void addRepoAppExtras(final ViewRepository repository, final int appHashid){
		try{

			new Thread(){
				public void run(){
					this.setPriority(Thread.MAX_PRIORITY);
					if(!managerDownloads.isConnectionAvailable()){
						AptoideLog.d(AptoideServiceData.this, "No connection");	//TODO raise exception to ask for what to do
					}
					if(!getManagerCache().isFreeSpaceInSdcard()){
						//TODO raise exception
					}
					ViewCache cache = managerDownloads.startRepoAppDownload(repository, appHashid, EnumInfoType.EXTRAS);
					
					managerXml.repoAppExtrasParse(repository, cache, appHashid);
					//TODO find some way to track global parsing completion status, probably in managerXml
				}
			}.start();


		} catch(Exception e){
			/** this should never happen */
			//TODO handle exception
			e.printStackTrace();
		}
	}
	
	public void parsingRepoAppExtrasFinished(ViewRepository repository, int appHashid){
		updateAppInfo(appHashid, EnumServiceDataCallback.UPDATE_APP_EXTRAS);
	}
	
	public void updateAppInfo(int appHashid, EnumServiceDataCallback callBack){
//		try {
//			switch (callBack) {
//				case REFRESH_ICON:
//					appInfoClients.get(appHashid).refreshIcon();
//					break;
//					
//				case UPDATE_APP_DOWNLOAD_INFO:
//					appInfoClients.get(appHashid).newAppDownloadInfoAvailable();
//					break;
//					
//				case UPDATE_APP_STATS:
//					appInfoClients.get(appHashid).newStatsInfoAvailable();
//					break;
//					
//				case UPDATE_APP_EXTRAS:
//					appInfoClients.get(appHashid).newExtrasAvailable();
//					break;
//					
//				case REFRESH_SCREENS:
//					appInfoClients.get(appHashid).refreshScreens();
//					break;
//					
//				default:
//					break;
//			}
//			
//		} catch (RemoteException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	
	public ViewDisplayAppVersionsInfo getAppInfo(int appHashid){
		AptoideLog.d(AptoideServiceData.this, "Getting App Versions Info: "+appHashid);
		return managerDatabase.getAppDisplayInfo(appHashid);
	}
	
	
	public ViewDisplayListApps getInstalledPackages(int offset, int range){
		AptoideLog.d(AptoideServiceData.this, "Getting Installed Packages");
		return managerDatabase.getInstalledAppsDisplayInfo(offset, range);
	}
	
	public ViewDisplayListApps getAvailablePackages(int offset, int range){
		AptoideLog.d(AptoideServiceData.this, "Getting Available Packages");
		return managerDatabase.getAvailableAppsDisplayInfo(offset, range);
	}
	
	public ViewDisplayListApps getUpdatablePackages(int offset, int range){
		AptoideLog.d(AptoideServiceData.this, "Getting Updatable Packages");
		return managerDatabase.getUpdatableAppsDisplayInfo(offset, range);
	}
	
	
	public void splash(){
		new Thread() {
			public void run(){
	    		Intent splash = new Intent(AptoideServiceData.this, Splash.class);
	    		splash.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
	    		startActivity(splash);    				
			}
		}.start();
	}
	

	public void launchAptoide() {
		Intent aptoide = new Intent(this, Aptoide.class);
		aptoide.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(aptoide);
	}
	
	
	public ViewClientStatistics getStatistics(){
		ViewClientStatistics statistics = new ViewClientStatistics(managerSystemSync.getAptoideVersionNameInUse());
		managerPreferences.completeStatistics(statistics);
		return statistics;
	}

}
