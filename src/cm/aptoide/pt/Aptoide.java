/**
 * Aptoide, Alternative client-side Android Package Manager
 * from v3.0 Copyright (C) 2011 Duarte Silveira 
 * duarte.silveira@caixamagica.pt
 * 
 * derivative work of original Aptoide Copyright (C) 2009 Roberto Jacinto
 * roberto.jacinto@caixamágica.pt
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
package cm.aptoide.pt;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;
import java.util.concurrent.TimeoutException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;
import cm.aptoide.pt.data.Constants;
import cm.aptoide.pt.data.EnumServiceDataMessage;
import cm.aptoide.pt.data.ServiceData;
import cm.aptoide.pt.data.database.ManagerDatabase;
import cm.aptoide.pt.data.system.ScreenDimensions;


public class Aptoide extends Activity { 
	
	boolean serviceDataSeenRunning = false;
	
	Messenger serviceDataOutboundMessenger = null;

	boolean serviceDataIsBound;
	private ServiceConnection serviceDataConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			// This is called when the connection with the service has been
			// established, giving us the object we can use to
			// interact with the service.  We are communicating with the
			// service using a Messenger, so here we get a client-side
			// representation of that from the raw IBinder object.
			serviceDataOutboundMessenger = new Messenger(service);
			serviceDataIsBound = true;

			if(!serviceDataSeenRunning){
				Message syncInstalledPackages = Message.obtain(null, EnumServiceDataMessage.SYNC_INSTALLED_PACKAGES.ordinal());
				try {
		            serviceDataOutboundMessenger.send(syncInstalledPackages);
		        } catch (RemoteException e) {
		            e.printStackTrace();
		        }
			}
			
			DisplayMetrics displayMetrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
			ScreenDimensions screenDimensions = new ScreenDimensions(displayMetrics.widthPixels, displayMetrics.heightPixels);
			Message storeScreenDimensions = Message.obtain(null, EnumServiceDataMessage.STORE_SCREEN_DIMENSIONS.ordinal(), screenDimensions);
	        try {
	            serviceDataOutboundMessenger.send(storeScreenDimensions);
	        } catch (RemoteException e) {
	            e.printStackTrace();
	        }
		}

		public void onServiceDisconnected(ComponentName className) {
			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.
			serviceDataOutboundMessenger = null;
			serviceDataIsBound = false;
		}
	};
    
    class ServiceDataInboundHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case MessengerService.MSG_SET_VALUE:
//                    mCallbackText.setText("Received from service: " + msg.arg1);
//                    break;
//                default:
//                    super.handleMessage(msg);
//            }
        }
    }

    /**
     * Target we publish for clients to send messages to ServiceDataInboundHandler.
     */
    final Messenger serviceDataInboundMessenger = new Messenger(new ServiceDataInboundHandler());


    /** example service usage code */
    
//    public void sayHello(View v) {
//        if (!serviceDataIsBound) return;
//        // Create and send a message to the service, using a supported 'what' value
//        Message msg = Message.obtain(null, MessengerService.MSG_SAY_HELLO, 0, 0);
//        try {
//            serviceDataOutboundMessenger.send(msg);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//    }

	
//moved to Constants 
    
//	private static final int LOAD_TABS = 0;			//TODO Probably unneeded
//	private static final int UPDATE_SELF = 99;		//TODO Probably unneeded
//    private static final String TMP_MYAPP_FILE = Environment.getExternalStorageDirectory().getPath() + "/.aptoide/server";	//TODO unnecessary?
//    private static final String TMP_UPDATE_FILE = Environment.getExternalStorageDirectory().getPath() + "/.aptoide/aptoideUpdate.apk"; //TODO renamed
//	private static final String LATEST_VERSION_CODE_URI = "http://aptoide.com/latest_version.xml"; 
	
//-------------------	
	
    
//    private Vector<String> server_lst = null;	//TODO to dataService
//    private Vector<String[]> get_apps = null;	//TODO to dataService
    
//	private ManagerDatabase db = null;				//TODO to dataService
	
//	private Context mctx;						//TODO deprecate

//TODO to dataService
	
//	private Bundle savedInstanceState;
//	
//	private Handler mHandler = new Handler();
//
//	
//	private Handler startHandler = new Handler() {
//
//		@Override
//		public void handleMessage(Message msg) {
//			switch(msg.what){
//			case LOAD_TABS:
//				Intent i = new Intent(Aptoide.this, RemoteInTab.class);
//				Intent get = getIntent();
//				if(get.getData() != null){
//					String uri = get.getDataString();
//					if(uri.startsWith("aptoiderepo")){
//						Log.d("Aptoide-startHandler", "aptoiderepo-scheme");
//						String repo = uri.substring(14);
//						i.putExtra("newrepo", repo);
//					}else if(uri.startsWith("aptoidexml")){
//						Log.d("Aptoide-startHandler", "aptoidexml-scheme");
//						String repo = uri.substring(13);
//						parseXmlString(repo);
//						i.putExtra("repos", server_lst);
//						if(get_apps.size() > 0){
//							//i.putExtra("uri", TMP_SRV_FILE);
//							i.putExtra("apps", get_apps);
//
//						}
//						//i.putExtra("linkxml", repo);
//					}else{
//						Log.d("Aptoide-startHandler", "receiving a myapp file");
//						downloadMyappFile(uri);
//						try {
//							parseMyappFile(TMP_MYAPP_FILE);
//							i.putExtra("repos", server_lst);
//							if(get_apps.size() > 0){
//								//i.putExtra("uri", TMP_SRV_FILE);
//								i.putExtra("apps", get_apps);
//	
//							}
//						} catch (Exception e) {
//							Toast.makeText(mctx, mctx.getString(R.string.failed_install), Toast.LENGTH_LONG);
//							onCreate(savedInstanceState);
//						}
//					}
//				}
//				startActivityForResult(i,0);
//				break;
//			}
//			super.handleMessage(msg);
//		} 
//    }; 
	
//-------------------
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        
//        mctx = this;	//TODO delete, leads to memory leaks, just use this when needed

        
        //TODO to ManagerNotifiers
//        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        keepScreenOn = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "Full Power"); 
        //------------------------
        
		makeSureServiceDataIsRunning();
		
		setFullScreen();        
				
    }
    
    private void makeSureServiceDataIsRunning(){
    	ActivityManager activityManager = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
    	for (RunningServiceInfo runningService : activityManager.getRunningServices(Integer.MAX_VALUE)) {
			if(runningService.service.getClassName().equals(Constants.SERVICE_DATA_CLASS_NAME)){
				this.serviceDataSeenRunning = true;
			}
		}
    	if(!serviceDataSeenRunning){
            startService(new Intent(this, ServiceData.class));
    	}
        bindService(new Intent(this, ServiceData.class), serviceDataConnection, Context.BIND_AUTO_CREATE);
    }
    
    private void setFullScreen(){
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);    	
    }


	//TODO to dataService      
    private void proceed(){
   		db = new ManagerDatabase(this);
   		
   		
   		/** TODO expand settings downloads options */
   		
//		if(sPref.getString("icdown", null) == null){
//			prefEdit.putString("icdown", "g3w");
//			prefEdit.commit();
//		}

		//TODO move to managersystemsync and Activitysplash
		
        setContentView(R.layout.start);
        
        mProgress = (ProgressBar) findViewById(R.id.pbar);
       
        new Thread(new Runnable() {		
            public void run() {
            	
            	Vector<ApkNode> apk_lst = db.getAll("abc");
            	mProgress.setMax(apk_lst.size());
        		PackageManager mPm;
        		PackageInfo pkginfo;
        		mPm = getPackageManager();
        		
        		keepScreenOn.acquire();
        		
        		//TODO iterate through getInstalledPackages(), it'll surely have much less iterations
        		for(ApkNode node: apk_lst){	
        			if(node.status == 0){
       				 try{
       					 pkginfo = mPm.getPackageInfo(node.apkid, 0);
       					 String vers = pkginfo.versionName;
       					 int verscode = pkginfo.versionCode;
       					 db.insertInstalled(node.apkid, vers, verscode);
       				 }catch(Exception e) {
       					 //Not installed anywhere... does nothing
       				 }
       			 }else{
       				 try{
       					 pkginfo = mPm.getPackageInfo(node.apkid, 0);
       					 String vers = pkginfo.versionName;
       					 int verscode = pkginfo.versionCode;
       					 db.UpdateInstalled(node.apkid, vers, verscode);
       				 }catch (Exception e){
       					 db.removeInstalled(node.apkid);
       				 }
       			 }
                    mProgressStatus++;
                    // Update the progress bar
                    mHandler.post(new Runnable() {
                        public void run() {
                            mProgress.setProgress(mProgressStatus);
                        }
                    });
                }
        		
        		keepScreenOn.release();
        		
                Message msg = new Message();
                msg.what = LOAD_TABS;
                startHandler.sendMessage(msg);
                
            }
        }).start();	
        
        //----------------------
    }			
//-------------------


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode){
			case UPDATE_SELF:
				proceed();
				break;
			default:
				super.onActivityResult(requestCode, resultCode, data);
				try{
					db.clodeDb();
				}catch (Exception e) {Log.d("Aptoide","Exit error: " + e.toString());	}
				finally{
					this.finish();
				}
			break;
		}
	}
    
	private void downloadMyappFile(String myappUri){
		try{
			keepScreenOn.acquire();
			
			BufferedInputStream getit = new BufferedInputStream(new URL(myappUri).openStream());

			File file_teste = new File(TMP_MYAPP_FILE);
			if(file_teste.exists())
				file_teste.delete();
			
			FileOutputStream saveit = new FileOutputStream(TMP_MYAPP_FILE);
			BufferedOutputStream bout = new BufferedOutputStream(saveit,1024);
			byte data[] = new byte[1024];
			
			int readed = getit.read(data,0,1024);
			while(readed != -1) {
				bout.write(data,0,readed);
				readed = getit.read(data,0,1024);
			}
			
			keepScreenOn.release();
			
			bout.close();
			getit.close();
			saveit.close();
		} catch(Exception e){
			AlertDialog p = new AlertDialog.Builder(this).create();
			p.setTitle(getText(R.string.top_error));
			p.setMessage(getText(R.string.aptoide_error));
			p.setButton(getText(R.string.btn_ok), new DialogInterface.OnClickListener() {
			      public void onClick(DialogInterface dialog, int which) {
			          return;
			        } });
			p.show();
		}
	}
	
	private void parseMyappFile(String file){
		SAXParserFactory spf = SAXParserFactory.newInstance();
	    try {
	    	keepScreenOn.acquire();
	    	
	    	SAXParser sp = spf.newSAXParser();
	    	XMLReader xr = sp.getXMLReader();
	    	NewServerRssHandler handler = new NewServerRssHandler(this);
	    	xr.setContentHandler(handler);
	    	
	    	InputStreamReader isr = new FileReader(new File(file));
	    	InputSource is = new InputSource(isr);
	    	xr.parse(is);
	    	File xml_file = new File(file);
	    	xml_file.delete();
	    	server_lst = handler.getNewSrvs();
	    	get_apps = handler.getNewApps();
	    	
	    	keepScreenOn.release();
	    	
	    } catch (IOException e) {
	    	e.printStackTrace();
	    } catch (SAXException e) {
	    	e.printStackTrace();
	    } catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	private void parseXmlString(String file){
		SAXParserFactory spf = SAXParserFactory.newInstance();
	    try {
	    	keepScreenOn.acquire();
	    	
	    	SAXParser sp = spf.newSAXParser();
	    	XMLReader xr = sp.getXMLReader();
	    	NewServerRssHandler handler = new NewServerRssHandler(this);
	    	xr.setContentHandler(handler);
	    	
	    	InputSource is = new InputSource();
	    	is.setCharacterStream(new StringReader(file));
	    	xr.parse(is);
	    	server_lst = handler.getNewSrvs();
	    	get_apps = handler.getNewApps();
	    	
	    	keepScreenOn.release();
	    	
	    } catch (IOException e) {
	    } catch (SAXException e) {
	    } catch (ParserConfigurationException e) {
		}
	}

	@Override
	protected void onDestroy() {
		if (serviceDataIsBound) {
            unbindService(serviceDataConnection);
            serviceDataIsBound = false;
        }
		super.onDestroy();
	}
	
	
	
}
