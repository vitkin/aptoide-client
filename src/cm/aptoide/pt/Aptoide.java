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
import android.os.Messenger;
import android.os.RemoteException;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;
import cm.aptoide.pt.data.Constants;
import cm.aptoide.pt.data.EnumServiceDataMessage;
import cm.aptoide.pt.data.EnumServiceDataReverseMessage;
import cm.aptoide.pt.data.ServiceData;
import cm.aptoide.pt.data.system.ScreenDimensions;
import cm.aptoide.pt.data.views.ViewDisplayListApps;
import cm.aptoide.pt.debug.AptoideLog;
import cm.aptoide.pt.debug.InterfaceAptoideLog;


public class Aptoide extends Activity implements InterfaceAptoideLog, OnItemClickListener{ 
	
	private final String TAG = "Aptoide-MainActivity";

	private ListView installedAppsList = null;
	private SimpleAdapter installedAdapter = null;
		
	private Messenger serviceDataOutboundMessenger = null;

	private boolean serviceDataSeenRunning = false;
	private boolean serviceDataIsBound = false;
	private ServiceConnection serviceDataConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			// This is called when the connection with the service has been
			// established, giving us the object we can use to
			// interact with the service.  We are communicating with the
			// service using a Messenger, so here we get a client-side
			// representation of that from the raw IBinder object.
			serviceDataOutboundMessenger = new Messenger(service);
			serviceDataIsBound = true;
			
			AptoideLog.v(Aptoide.this, "Connected to ServiceData");

			if(!serviceDataSeenRunning){
				Message syncInstalledPackages = Message.obtain(null, EnumServiceDataMessage.SYNC_INSTALLED_PACKAGES.ordinal());
				try {
		            serviceDataOutboundMessenger.send(syncInstalledPackages);
		            AptoideLog.v(Aptoide.this, "Called for a synchronization of installed Packages, because serviceData wasn't previously running");
		        } catch (RemoteException e) {
					// TODO Auto-generated catch block
		            e.printStackTrace();
		        }
			}
			
			DisplayMetrics displayMetrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
			ScreenDimensions screenDimensions = new ScreenDimensions(displayMetrics.widthPixels, displayMetrics.heightPixels);
			AptoideLog.d(Aptoide.this, screenDimensions.toString());
			Bundle screenDimensionsBundle = new Bundle();
			screenDimensionsBundle.putSerializable(EnumServiceDataMessage.STORE_SCREEN_DIMENSIONS.toString(), screenDimensions);
			Message storeScreenDimensions = Message.obtain(null, EnumServiceDataMessage.STORE_SCREEN_DIMENSIONS.ordinal());
			storeScreenDimensions.setData(screenDimensionsBundle);
	        try {
	            serviceDataOutboundMessenger.send(storeScreenDimensions);
	            AptoideLog.v(Aptoide.this, "Called for screenDimensions storage");
	        } catch (RemoteException e) {
				// TODO Auto-generated catch block
	            e.printStackTrace();
	        }
		}

		public void onServiceDisconnected(ComponentName className) {
			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.
			serviceDataOutboundMessenger = null;
			serviceDataIsBound = false;
			
			AptoideLog.v(Aptoide.this, "Disconnected from ServiceData");
		}
	};
    
    class ServiceDataInboundHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
        	EnumServiceDataReverseMessage message = EnumServiceDataReverseMessage.reverseOrdinal(msg.what);
        	switch (message) {
			case UPDATE_INSTALLED_LIST:
				Object bundleContent = msg.getData().getSerializable(EnumServiceDataReverseMessage.UPDATE_INSTALLED_LIST.toString());
            	if(bundleContent instanceof ViewDisplayListApps){
            		displayInstalled((ViewDisplayListApps)bundleContent);	
            		AptoideLog.d(Aptoide.this, "received bundle with installed apps");
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
    }

    /**
     * Target we publish for clients to send messages to ServiceDataInboundHandler.
     */
    final Messenger serviceDataInboundMessenger = new Messenger(new ServiceDataInboundHandler());


    /** example service usage code 
    
    public void sayHello(View v) {
        if (!serviceDataIsBound) return;
        // Create and send a message to the service, using a supported 'what' value
        Message msg = Message.obtain(null, MessengerService.MSG_SAY_HELLO, 0, 0);
        try {
            serviceDataOutboundMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    ****************************** */

    
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
		installedAdapter = new SimpleAdapter(Aptoide.this, installedApps.getList(), R.layout.apps_list, 
				new String[] {Constants.KEY_APPLICATION_HASHID, Constants.KEY_APPLICATION_NAME, Constants.DISPLAY_APP_UP_TO_DATE_VERSION_NAME, Constants.DISPLAY_APP_INSTALLED_VERSION_NAME, Constants.DISPLAY_APP_IS_DOWNGRADABLE, Constants.DISPLAY_APP_ICON_CACHE_PATH},
				new int[] {R.id.app_hashid, R.id.app_name, R.id.uptodate_versionname, R.id.isinst, R.id.isDowngradeAvailable, R.id.app_icon});
    }

	private void makeSureServiceDataIsRunning(){
    	ActivityManager activityManager = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
    	for (RunningServiceInfo runningService : activityManager.getRunningServices(Integer.MAX_VALUE)) {
			if(runningService.service.getClassName().equals(Constants.SERVICE_DATA_CLASS_NAME)){
				this.serviceDataSeenRunning = true;
				break;
			}
		}
    	if(!serviceDataSeenRunning){
            startService(new Intent(this, ServiceData.class));
    	}
    	if(!serviceDataIsBound){
    		bindService(new Intent(this, ServiceData.class), serviceDataConnection, Context.BIND_AUTO_CREATE);
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
	

	class InstalledListBinder implements ViewBinder
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
