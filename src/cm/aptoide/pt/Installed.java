/**
 * Installed, part of Aptoide
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


import java.io.File;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter.ViewBinder;
import cm.aptoide.pt.data.AIDLAptoideServiceData;
import cm.aptoide.pt.data.AptoideServiceData;
import cm.aptoide.pt.data.Constants;
import cm.aptoide.pt.data.EnumServiceDataCallback;
import cm.aptoide.pt.data.display.ViewDisplayListApps;
import cm.aptoide.pt.data.system.ViewScreenDimensions;
import cm.aptoide.pt.debug.AptoideLog;
import cm.aptoide.pt.debug.InterfaceAptoideLog;

/**
 * Installed, aptoide interface class which
 * 			displays the installed apps list
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class Installed extends Activity implements InterfaceAptoideLog, OnItemClickListener{ 
	
	private final String TAG = "Aptoide-Installed";

	private ListView installedAppsList = null;
	private SimpleAdapter installedAdapter = null;
		
	private AIDLAptoideServiceData serviceDataCaller = null;

	private boolean serviceDataSeenRunning = false;
	private boolean serviceDataIsBound = false;

	
	private ServiceConnection serviceDataConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			// This is called when the connection with the service has been
			// established, giving us the object we can use to
			// interact with the service.  We are communicating with the
			// service using AIDL, so here we set the remote service interface.
			serviceDataCaller = AIDLAptoideServiceData.Stub.asInterface(service);
			serviceDataIsBound = true;
			
			AptoideLog.v(Installed.this, "Connected to ServiceData");

			if(!serviceDataSeenRunning){
				try {
		            AptoideLog.v(Installed.this, "Called for a synchronization of installed Packages, because serviceData wasn't previously running");
		            serviceDataCaller.callSyncInstalledPackages();
		        } catch (RemoteException e) {
					// TODO Auto-generated catch block
		            e.printStackTrace();
		        }
			}
			
			
	        try {
	            AptoideLog.v(Installed.this, "Called for registering as InstalledPackages Observer");
	            serviceDataCaller.callRegisterInstalledPackagesObserver(serviceDataCallback);
	            
	            AptoideLog.v(Installed.this, "Called for getting InstalledPackages");
	            displayInstalled(serviceDataCaller.callGetInstalledPackages(0, 100));
	        } catch (RemoteException e) {
				// TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        
		}

		public void onServiceDisconnected(ComponentName className) {
			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.
			serviceDataCaller = null;
			serviceDataIsBound = false;
			
			AptoideLog.v(Installed.this, "Disconnected from ServiceData");
		}
	};
	
	private AIDLAptoideInterface.Stub serviceDataCallback = new AIDLAptoideInterface.Stub() {
		
		@Override
		public void newListDataAvailable() throws RemoteException {
			AptoideLog.v(Installed.this, "received newListDataAvailable callback");
			serviceDataCallbackHandler.sendEmptyMessage(EnumServiceDataCallback.UPDATE_INSTALLED_LIST.ordinal());
		}
	};
    
    private Handler serviceDataCallbackHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	EnumServiceDataCallback message = EnumServiceDataCallback.reverseOrdinal(msg.what);
        	switch (message) {
			case UPDATE_INSTALLED_LIST:
				try {
					displayInstalled(serviceDataCaller.callGetInstalledPackages(0, 100));
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
				break;

			default:
				break;
			}
//            switch (msg.what) {
//                case MessengerService.MSG_SET_VALUE:
//                    mCallbackText.setText("Received from service: " + msg.arg1);
//                    break;
//                default:
//                    super.handleMessage(msg);
//            }
        }
    };


    @Override
	public String getTag() {
		return TAG;
	}
    

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        
		makeSureServiceDataIsRunning();
		
		installedAppsList = new ListView(this);
		installedAppsList.setOnItemClickListener(this);
    }
    
    public void displayInstalled(ViewDisplayListApps installedApps){
    	AptoideLog.d(Installed.this, "InstalledList: "+installedApps);
		installedAdapter = new SimpleAdapter(Installed.this, installedApps.getList(), R.layout.app_row, 
				new String[] {Constants.KEY_APPLICATION_HASHID, Constants.KEY_APPLICATION_NAME, Constants.DISPLAY_APP_UP_TO_DATE_VERSION_NAME, Constants.DISPLAY_APP_INSTALLED_VERSION_NAME, Constants.DISPLAY_APP_IS_DOWNGRADABLE, Constants.DISPLAY_APP_ICON_CACHE_PATH},
				new int[] {R.id.app_hashid, R.id.app_name, R.id.uptodate_versionname, R.id.installed_versionname, R.id.isDowngradeAvailable, R.id.app_icon});
		
		installedAdapter.setViewBinder(new InstalledAppsListBinder());
		
		installedAppsList.setAdapter(installedAdapter);
		setContentView(installedAppsList);
		installedAppsList.setSelection(-1);
    }

	private void makeSureServiceDataIsRunning(){
    	ActivityManager activityManager = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
    	for (RunningServiceInfo runningService : activityManager.getRunningServices(Integer.MAX_VALUE)) {
			if(runningService.service.getClassName().equals(Constants.SERVICE_DATA_CLASS_NAME)){
				this.serviceDataSeenRunning = true;
				break;
			}
		}
//    	if(!serviceDataSeenRunning){
//    		new Thread() {
//    			public void run(){
//    	    		Intent splash = new Intent(Installed.this, Splash.class);
//    	    		splash.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
//    	    		startActivity(splash);    				
//    			}
//    		}.start();
//    		
////            startService(new Intent(this, AptoideServiceData.class));
//    	}
    	if(!serviceDataIsBound){
    		bindService(new Intent(this, AptoideServiceData.class), serviceDataConnection, Context.BIND_AUTO_CREATE);
    	}
    }

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//		final String pkg_id = ((LinearLayout)arg1).getTag().toString();
//
//		pos = arg2;
//
//		Intent apkinfo = new Intent(this,ApkInfo.class);
//		apkinfo.putExtra("name", db.getName(pkg_id));
//		apkinfo.putExtra("icon", this.getString(R.string.icons_path)+pkg_id);
//		apkinfo.putExtra("apk_id", pkg_id);
//		
//		String tmpi = db.getDescript(pkg_id);
//		if(!(tmpi == null)){
//			apkinfo.putExtra("about",tmpi);
//		}else{
//			apkinfo.putExtra("about",getText(R.string.app_pop_up_no_info));
//		}
//		
//
//		Vector<String> tmp_get = db.getApk(pkg_id);
//		apkinfo.putExtra("server", tmp_get.firstElement());
//		apkinfo.putExtra("version", tmp_get.get(1));
//		apkinfo.putExtra("dwn", tmp_get.get(4));
//		apkinfo.putExtra("rat", tmp_get.get(5));
//		apkinfo.putExtra("size", tmp_get.get(6));
//		apkinfo.putExtra("type", 1);
//		
//		startActivityForResult(apkinfo,30);
	}
	

	class InstalledAppsListBinder implements ViewBinder
	{
		public boolean setViewValue(View view, Object data, String textRepresentation)
		{
			if(view.getClass().toString().equalsIgnoreCase("class android.widget.RatingBar")){
				RatingBar tmpr = (RatingBar)view;
				tmpr.setRating(new Float(textRepresentation));
			}else if(view.getClass().toString().equalsIgnoreCase("class android.widget.TextView")){
				TextView tmpr = (TextView)view;
				tmpr.setText(textRepresentation);
			}else if(view.getClass().toString().equalsIgnoreCase("class android.widget.ImageView")){
				ImageView tmpr = (ImageView)view;	
				File icn = new File(textRepresentation);
				if(icn.exists() && icn.length() > 0){
					new Uri.Builder().build();
					tmpr.setImageURI(Uri.parse(textRepresentation));
				}else{
					tmpr.setImageResource(android.R.drawable.sym_def_app_icon);
				}
			}else if(view.getClass().toString().equalsIgnoreCase("class android.widget.LinearLayout")){
				LinearLayout tmpr = (LinearLayout)view;
				tmpr.setTag(textRepresentation);
			}else{
				return false;
			}
			return true;
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
