package cm.aptoide.pt;

import java.io.File;
import java.util.LinkedList;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import cm.aptoide.pt.data.AIDLAptoideServiceData;
import cm.aptoide.pt.data.AptoideServiceData;
import cm.aptoide.pt.data.Constants;
import cm.aptoide.pt.data.EnumServiceDataCallback;
import cm.aptoide.pt.data.display.ViewDisplayAppVersionsInfo;
import cm.aptoide.pt.data.volatil.EnumUserTaste;

public class AppInfo extends Activity{
	
	
	private int appHashid;
	ViewDisplayAppVersionsInfo appVersions;
	
	private AIDLAptoideServiceData serviceDataCaller = null;

	private boolean serviceDataIsBound = false;

	
	private ServiceConnection serviceDataConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			// This is called when the connection with the service has been
			// established, giving us the object we can use to
			// interact with the service.  We are communicating with the
			// service using AIDL, so here we set the remote service interface.
			serviceDataCaller = AIDLAptoideServiceData.Stub.asInterface(service);
			serviceDataIsBound = true;
			
			Log.v("Aptoide-AppInfo", "Connected to ServiceData");
	        
	        try {
	            Log.v("Aptoide-AppInfo", "Called for registering as AppInfo Observer");
	            serviceDataCaller.callRegisterAppInfoObserver(serviceDataCallback, appHashid);
	           
	        } catch (RemoteException e) {
				// TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        
	        try {
				serviceDataCaller.CallFillAppInfo(appHashid);
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
			
			Log.v("Aptoide-AppInfo", "Disconnected from ServiceData");
		}
	};
	
	private AIDLAppInfo.Stub serviceDataCallback = new AIDLAppInfo.Stub() {
		
		@Override
		public void refreshScreens() throws RemoteException {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void refreshIcon() throws RemoteException {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void newStatsInfoAvailable() throws RemoteException {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void newExtrasAvailable() throws RemoteException {
			Log.v("Aptoide-AppInfo", "received newExtrasAvailable callback");
			serviceDataCallbackHandler.sendEmptyMessage(EnumServiceDataCallback.UPDATE_APP_EXTRAS.ordinal());
		}
		
		@Override
		public void newCommentsAvailable() throws RemoteException {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void newAppDownloadInfoAvailable() throws RemoteException {
			// TODO Auto-generated method stub
			
		}
	};

    
    private Handler serviceDataCallbackHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	EnumServiceDataCallback message = EnumServiceDataCallback.reverseOrdinal(msg.what);
        	switch (message) {
				case UPDATE_APP_EXTRAS:
					fillRest();
					break;
	
				default:
					break;
			}
        }
    };
	
	
    @Override
	protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);

		appHashid = Integer.parseInt(getIntent().getStringExtra("appHashid"));
		
		if(!serviceDataIsBound){
    		bindService(new Intent(this, AptoideServiceData.class), serviceDataConnection, Context.BIND_AUTO_CREATE);
    	}
		
		setContentView(R.layout.app_info);
	
		fillData();
		
		
//		Handler threadHandler = new Handler();
//		threadHandler.postDelayed(new Runnable() {
//            public void run() {
//            	fillRest();
//            }
//        }, 2000);
//		threadHandler.postDelayed(new Runnable() {
//            public void run() {
//            	fillRest();
//            }
//        }, 10000);
    }
	
	protected void fillData(){
		String icon_path = Constants.PATH_CACHE_ICONS+appHashid;
		ImageView icon = (ImageView) findViewById(R.id.icon);
		File test_icon = new File(icon_path);
		
		
		if(test_icon.exists() && test_icon.length() > 0){
			icon.setImageDrawable(new BitmapDrawable(icon_path));
		}else{
			icon.setImageResource(android.R.drawable.sym_def_app_icon);
		}
	}
	
	protected void fillRest(){
		try {
			appVersions = serviceDataCaller.callGetAppInfo(appHashid);
			Log.d("Aptoide-AppInfo", "Got app versions: "+appVersions);
			
			TextView description = (TextView) findViewById(R.id.description);
			description.setText(appVersions.toString());
			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	@Override
	public void finish() {
		if(serviceDataIsBound){
			unbindService(serviceDataConnection);
		}
		super.finish();
	}

}
