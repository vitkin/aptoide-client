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


import java.util.ArrayList;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.TabActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
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
import android.widget.TabHost;
import cm.aptoide.pt.data.Constants;
import cm.aptoide.pt.data.EnumServiceDataMessage;
import cm.aptoide.pt.data.ServiceData;
import cm.aptoide.pt.data.database.ManagerDatabase;
import cm.aptoide.pt.data.system.ScreenDimensions;
import cm.aptoide.pt.data.views.ViewApplication;
import cm.aptoide.pt.data.views.ViewCategory;
import cm.aptoide.pt.data.views.ViewDisplayListApps;
import cm.aptoide.pt.data.views.ViewDisplayListRepos;
import cm.aptoide.pt.data.views.ViewLogin;
import cm.aptoide.pt.data.views.ViewRepository;
import cm.aptoide.pt.debug.AptoideLog;
import cm.aptoide.pt.debug.InterfaceAptoideLog;


public class Aptoide extends Activity implements InterfaceAptoideLog{ 
	
	private final String TAG = "Aptoide-MainActivity";
	
	private TabHost tabHost;
		
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
		setFullScreen();     
		
//		tabHost = getTabHost();
				
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
    
    private void setFullScreen(){
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);    	
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
