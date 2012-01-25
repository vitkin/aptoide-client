/**
 * AppInfo,		part of Aptoide
 * 
 * from v3.0 Copyright (C) 2011 Duarte Silveira 
 * duarte.silveira@caixamagica.pt
 * 
 * derivative work of ApkInfo from earlier Aptoide's versions with
 * Copyright (C) 2009 Roberto Jacinto
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
import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.data.AIDLAptoideServiceData;
import cm.aptoide.pt.data.AptoideServiceData;
import cm.aptoide.pt.data.Constants;
import cm.aptoide.pt.data.display.ViewDisplayAppVersionsInfo;
import cm.aptoide.pt.ifaceutil.ImageAdapter;

/**
 * AppInfo, interface class to display the details
 * 			of a specific application
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class AppInfo extends Activity{
	
	
	private int appHashid;
	private String appName;
	private ViewDisplayAppVersionsInfo appVersions;
	Gallery galleryView;
	
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
			Log.v("Aptoide-AppInfo", "received refreshScreens callback");
			interfaceTasksHandler.sendEmptyMessage(EnumAppInfoTasks.REFRESH_SCREENS.ordinal());
		}
		
		@Override
		public void refreshIcon() throws RemoteException {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void newAppDownloadInfoAvailable() throws RemoteException {
			Log.v("Aptoide-AppInfo", "received newAppDownloadInfoAvailable callback");
			interfaceTasksHandler.sendEmptyMessage(EnumAppInfoTasks.UPDATE_APP_DOWNLOAD_INFO.ordinal());
		}
		
		@Override
		public void newStatsInfoAvailable() throws RemoteException {
			Log.v("Aptoide-AppInfo", "received newStatsInfoAvailable callback");
			interfaceTasksHandler.sendEmptyMessage(EnumAppInfoTasks.UPDATE_APP_STATS.ordinal());
		}
		
		@Override
		public void newExtrasAvailable() throws RemoteException {
			Log.v("Aptoide-AppInfo", "received newExtrasAvailable callback");
			interfaceTasksHandler.sendEmptyMessage(EnumAppInfoTasks.UPDATE_APP_EXTRAS.ordinal());
		}
		
		@Override
		public void newCommentsAvailable() throws RemoteException {
			// TODO Auto-generated method stub
			
		}
	};

    
    private Handler interfaceTasksHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	EnumAppInfoTasks task = EnumAppInfoTasks.reverseOrdinal(msg.what);
        	switch (task) {
        	
	    		case UPDATE_APP_DOWNLOAD_INFO:
	    			setVersionsDescription();
	    			break;
        	
        		case UPDATE_APP_STATS:
        			setVersionsDescription();
        			break;
        			
				case UPDATE_APP_EXTRAS:
					setVersionsDescription();
					break;
					
				case REFRESH_SCREENS:
					setScreens();
					break;
	
				default:
					break;
			}
        }
    };
	
	
    @Override
	protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);

		appHashid = getIntent().getIntExtra("appHashid", 0);
		
		if(!serviceDataIsBound){
    		bindService(new Intent(this, AptoideServiceData.class), serviceDataConnection, Context.BIND_AUTO_CREATE);
    	}
		
		setContentView(R.layout.app_info);
		
		final Button install = (Button) findViewById(R.id.install);
		install.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				Log.d("Aptoide-AppInfo", "called install app");
				try {
					serviceDataCaller.callInstallApp(appHashid);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				finish();
			}
		});
		
		final Button uninstall = (Button) findViewById(R.id.uninstall);
		uninstall.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				Log.d("Aptoide-AppInfo", "called remove app");
				try {
					serviceDataCaller.callUninstallApp(appHashid);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				finish();
			}
		});

		galleryView = (Gallery) findViewById(R.id.screens);
		
		setIcon();
		
		
		
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
	
	protected void setIcon(){
		String icon_path = Constants.PATH_CACHE_ICONS+appHashid;
		ImageView icon = (ImageView) findViewById(R.id.icon);
		File test_icon = new File(icon_path);
		
		
		if(test_icon.exists() && test_icon.length() > 0){
			icon.setImageDrawable(new BitmapDrawable(icon_path));
		}else{
			icon.setImageResource(android.R.drawable.sym_def_app_icon);
		}
	}
	
	protected void setVersionsDescription(){
		try {
			appVersions = serviceDataCaller.callGetAppInfo(appHashid);
			Log.d("Aptoide-AppInfo", "Got app versions: "+appVersions);
			
			TextView description = (TextView) findViewById(R.id.description);
			description.setText(appVersions.toString());
			appName = appVersions.getVersionsList().get(0).getAppName();
			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	protected void setScreens(){
		ArrayList<Drawable> screensDrawables = new ArrayList<Drawable>();
		int orderNumber = 0;
		String screenPath = Constants.PATH_CACHE_SCREENS+appHashid+"."+orderNumber;
		File screen = null;
		do{
			Drawable screenDrawable = Drawable.createFromPath(screenPath);
			screensDrawables.add(screenDrawable);
			orderNumber++;
			screenPath = Constants.PATH_CACHE_SCREENS+appHashid+"."+orderNumber;
			screen = new File(screenPath);
		}while(screen.exists());
		galleryView.setAdapter(new ImageAdapter(AppInfo.this, screensDrawables, appName));
		galleryView.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	        	
	        	//Log.d("Aptoide","This view.....");
	    		final Dialog dialog = new Dialog(AppInfo.this);

	    		dialog.setContentView(R.layout.screenshot);
	    		dialog.setTitle(appName);

	    		ImageView image = (ImageView) dialog.findViewById(R.id.image);
	    		ImageView fetch = (ImageView) v;
	    		image.setImageDrawable(fetch.getDrawable());
	    		image.setOnClickListener(new OnClickListener() {
	    			public void onClick(View v) {
	    				dialog.dismiss();
	    			}
	    		});
	    		
	    		dialog.setCanceledOnTouchOutside(true);
	    		
	    		dialog.show();
	    		
	        }
	    });
		galleryView.setVisibility(View.VISIBLE);
	}
	
	
	
	@Override
	public void finish() {
		if(serviceDataIsBound){
			unbindService(serviceDataConnection);
		}
		super.finish();
	}

}
