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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import cm.aptoide.pt.data.AIDLAptoideServiceData;
import cm.aptoide.pt.data.AptoideServiceData;
import cm.aptoide.pt.data.display.ViewDisplayAppVersionInfo;
import cm.aptoide.pt.data.display.ViewDisplayAppVersionsInfo;
import cm.aptoide.pt.data.util.Constants;
import cm.aptoide.pt.ifaceutil.ImageAdapter;

/**
 * AppInfo, interface class to display the details of a specific application
 * 
 * @author dsilveira
 * @since 3.0
 * 
 */
public class AppInfo extends Activity {

	private int appHashid;
	private String appName;
	private int appDownloads;
	private String appSize;
	private float appStars;
	private String repoUri;
	private int appLikes;
	private int appDislikes;
	private String appDescription;
	
	private ViewDisplayAppVersionsInfo appVersions;

	private ViewDisplayAppVersionInfo installedVersion = null;
	private ViewDisplayAppVersionInfo selectedVersion = null;
	
	CheckBox later;
	private TextView appNameTextView;
	private TextView appDownloadsTextView;
	private TextView appSizeTextView;
	private RatingBar appStarsRating;
	private TextView repoUriTextView;
	private TextView appLikesTextView;
	private TextView appDislikesTextView;
	private TextView appDescriptionTextView;
	private Spinner appMultiVersion;

	Gallery galleryView;
	
	private Button install;
	private Button uninstall;
	
	private ArrayAdapter<String> multiVersionSpinnerAdapter;
	
	private VersionInfoManager versionInfoManager;
	
	private AIDLAptoideServiceData serviceDataCaller = null;

	private boolean serviceDataIsBound = false;

	private ServiceConnection serviceDataConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			// This is called when the connection with the service has been
			// established, giving us the object we can use to
			// interact with the service. We are communicating with the
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

			setVersions();		
			
//			try {
//				serviceDataCaller.CallFillAppInfo(appHashid);
//			} catch (RemoteException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}

//			try {
//				if (serviceDataCaller.callIsAppScheduledToInstall(appHashid)) {
//					storedForLater = true;
//				} 
//				later.setChecked(storedForLater);
//			} catch (RemoteException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
			
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
		public void refreshIcon() throws RemoteException {
			Log.v("Aptoide-AppInfo", "received refreshIcon callback");
			interfaceTasksHandler.sendEmptyMessage(EnumAppInfoTasks.REFRESH_ICON.ordinal());
		}

		@Override
		public void newAppInfoAvailable(int appFullHashid) throws RemoteException {
//			Log.v("Aptoide-AppInfo", "received newAppInfoAvailable callback");
//			interfaceTasksHandler.sendEmptyMessage(EnumAppInfoTasks.UPDATE_APP_INFO.ordinal());
		}

		@Override
		public void newAppDownloadInfoAvailable(int appFullHashid) throws RemoteException {
			Log.v("Aptoide-AppInfo", "received newAppDownloadInfoAvailable callback");
			for (int position=0; position < appVersions.size(); position++) {
				if(appVersions.get(position).getAppFullHashid() == appFullHashid){
					versionInfoManager.updateAppSize(appFullHashid, position);
					break;
				}
			}
		}

		@Override
		public void newStatsInfoAvailable(int appFullHashid) throws RemoteException {
			Log.v("Aptoide-AppInfo", "received newStatsInfoAvailable callback");
			for (int position=0; position < appVersions.size(); position++) {
				if(appVersions.get(position).getAppFullHashid() == appFullHashid){
					versionInfoManager.updateAppStats(appFullHashid, position);
					break;
				}
			}
		}

		@Override
		public void newExtrasAvailable(int appFullHashid) throws RemoteException {
			Log.v("Aptoide-AppInfo", "received newExtrasAvailable callback");
			for (int position=0; position < appVersions.size(); position++) {
				if(appVersions.get(position).getAppFullHashid() == appFullHashid){
					versionInfoManager.updateAppDescription(appFullHashid, position);
					break;
				}
			}
		}

		@Override
		public void refreshScreens() throws RemoteException {
			Log.v("Aptoide-AppInfo", "received refreshScreens callback");
			interfaceTasksHandler.sendEmptyMessage(EnumAppInfoTasks.REFRESH_SCREENS.ordinal());
		}

		@Override
		public void newCommentsAvailable(int appFullHashid) throws RemoteException {
			// TODO Auto-generated method stub

		}
	};

	private Handler interfaceTasksHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			EnumAppInfoTasks task = EnumAppInfoTasks.reverseOrdinal(msg.what);
			switch (task) {

			case REFRESH_ICON:
				setIcon();
				break;

//			case UPDATE_APP_INFO:
//				setVersions();
//				break;

			case UPDATE_APP_SIZE:
				setVersionSize();
				break;

			case UPDATE_APP_STATS:
				setVersionStats();
				break;

			case UPDATE_APP_DESCRIPTION:
				setVersionDescription();
				break;

			case REFRESH_SCREENS:
				setScreens();
				break;

			default:
				break;
			}
		}
	};
	

    private class VersionInfoManager{
    	private ExecutorService versionInfoColectorsPool;
    	
    	public VersionInfoManager(){
    		versionInfoColectorsPool = Executors.newCachedThreadPool();
    	}
    	
    	public void updateAppSize(int appFullHashid, int position){
        	versionInfoColectorsPool.execute(new GetAppSize(appFullHashid, position));
        }
    	
    	public void updateAppStats(int appFullHashid, int position){
        	versionInfoColectorsPool.execute(new GetAppStats(appFullHashid, position));
        }
    	
    	public void updateAppDescription(int appFullHashid, int position){
        	versionInfoColectorsPool.execute(new GetAppDescription(appFullHashid, position));
        }
    	
    	private class GetAppSize implements Runnable{
    		int appFullHashid = Constants.EMPTY_INT;
    		int position = Constants.EMPTY_INT;
    		

			public GetAppSize(int appFullHashid, int position) {
				this.appFullHashid = appFullHashid;
				this.position = position;
			}


			@Override
			public void run() {
				try {
					Log.d("Aptoide-AppInfo", "Called for registering as AppInfo Observer");
					serviceDataCaller.callRegisterAppInfoObserver(serviceDataCallback, appHashid);
					appVersions.get(position).setSize(serviceDataCaller.callGetAppVersionDownloadSize(appFullHashid));
					interfaceTasksHandler.sendEmptyMessage(EnumAppInfoTasks.UPDATE_APP_SIZE.ordinal());
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
    		
    	}
    	
    	private class GetAppStats implements Runnable{
    		int appFullHashid = Constants.EMPTY_INT;
    		int position = Constants.EMPTY_INT;
    		

			public GetAppStats(int appFullHashid, int position) {
				this.appFullHashid = appFullHashid;
				this.position = position;
			}


			@Override
			public void run() {
				try {
					Log.d("Aptoide-AppInfo", "Called for registering as AppInfo Observer");
					serviceDataCaller.callRegisterAppInfoObserver(serviceDataCallback, appHashid);
					appVersions.get(position).setStats(serviceDataCaller.callGetAppStats(appFullHashid));
					interfaceTasksHandler.sendEmptyMessage(EnumAppInfoTasks.UPDATE_APP_STATS.ordinal());
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
    		
    	}
    	
    	private class GetAppDescription implements Runnable{
    		int appFullHashid = Constants.EMPTY_INT;
    		int position = Constants.EMPTY_INT;
    		

			public GetAppDescription(int appFullHashid, int position) {
				this.appFullHashid = appFullHashid;
				this.position = position;
			}


			@Override
			public void run() {
				try {
					Log.d("Aptoide-AppInfo", "Called for registering as AppInfo Observer");
					serviceDataCaller.callRegisterAppInfoObserver(serviceDataCallback, appHashid);
					appVersions.get(position).setExtras(serviceDataCaller.callGetAppExtras(appFullHashid));
					interfaceTasksHandler.sendEmptyMessage(EnumAppInfoTasks.UPDATE_APP_DESCRIPTION.ordinal());
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
    		
    	}
    }
    
    

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		appHashid = getIntent().getIntExtra("appHashid", 0);

		setContentView(R.layout.app_info);
	

		setIcon();
		
		if (!serviceDataIsBound) {
			bindService(new Intent(this, AptoideServiceData.class), serviceDataConnection, Context.BIND_AUTO_CREATE);
		}
		
		
		later = (CheckBox) findViewById(R.id.later);
		later.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				toggleVersionDownloadScheduleStatus(selectedVersion); 
			}
		});
		appNameTextView = (TextView) findViewById(R.id.app_name);
		appDownloadsTextView = (TextView) findViewById(R.id.app_downloads);
		appSizeTextView = (TextView) findViewById(R.id.app_size);
		appStarsRating = (RatingBar) findViewById(R.id.app_rating);
		repoUriTextView = (TextView) findViewById(R.id.app_store);
		appLikesTextView = (TextView) findViewById(R.id.app_likes);
		appDislikesTextView = (TextView) findViewById(R.id.app_dislikes);
		appDescriptionTextView = (TextView) findViewById(R.id.app_description);
		appMultiVersion = ((Spinner) findViewById(R.id.spinnerMultiVersion));
		
		ImageView searchView = (ImageView) findViewById(R.id.search_button);
		searchView.setVisibility(View.GONE);

		install = (Button) findViewById(R.id.install);
		install.setTextColor(Color.DKGRAY);
		uninstall = (Button) findViewById(R.id.uninstall);
		uninstall.setTextColor(Color.DKGRAY);

		galleryView = (Gallery) findViewById(R.id.screens);
		
		versionInfoManager = new VersionInfoManager();

	}
	
	
	
	private void toggleVersionDownloadScheduleStatus(ViewDisplayAppVersionInfo appVersion){
		try {
			if(appVersion.isScheduled()){
				serviceDataCaller.callUnscheduleInstallApp(selectedVersion.getAppHashid());
			}else{
				serviceDataCaller.callScheduleInstallApp(selectedVersion.getAppHashid());
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		appVersion.setIsScheduled(!appVersion.isScheduled());
	}
	
	private void activateInstallButton(boolean activate){
		String label = getResources().getString(R.string.install);
		if(later.isChecked() && !selectedVersion.isScheduled()){
			label = getResources().getString(R.string.schedule);
		}
		if(activate){
			install.setText(label);
			install.setTextColor(Color.BLACK);			
			install.setOnClickListener(new OnClickListener() {
	
				@Override
				public void onClick(View view) {
					if(later.isChecked()){
						try {
							serviceDataCaller.callScheduleInstallApp(selectedVersion.getAppHashid());
							Toast.makeText(AppInfo.this, getResources().getText(R.string.scheduled).toString(), Toast.LENGTH_LONG).show();
							Log.d("Aptoide-AppInfo", "called schedule install app");
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else{
						try {
							if(selectedVersion.isScheduled()){
								serviceDataCaller.callUnscheduleInstallApp(selectedVersion.getAppHashid());
							}
							serviceDataCaller.callInstallApp(selectedVersion.getAppHashid());
//							Toast.makeText(AppInfo.this, getResources().getText(R.string.starting).toString()+" "
//														+getResources().getText(R.string.download).toString(), Toast.LENGTH_LONG).show();
							Log.d("Aptoide-AppInfo", "called install app");
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					finish();
				}
			});
		}else{
			install.setText(label);
			install.setTextColor(Color.WHITE);
			install.setOnClickListener(null);
		}
	}
	
	private void activateUninstallButton(boolean activate){
		String label = getResources().getString(R.string.uninstall);
		if(activate){
			uninstall.setText(label);
			uninstall.setTextColor(Color.BLACK);	
			uninstall.setOnClickListener(new OnClickListener() {
	
				@Override
				public void onClick(View view) {
					Log.d("Aptoide-AppInfo", "called remove app");
					try {
						serviceDataCaller.callUninstallApp(selectedVersion.getAppHashid());
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					finish();
				}
			});
		}else{
			if(selectedVersion.isScheduled()){
				label = getResources().getString(R.string.unschedule);
				uninstall.setText(label);
				uninstall.setTextColor(Color.BLACK);
				uninstall.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View view) {
						Log.d("Aptoide-AppInfo", "called unschedule app");
						try {
							serviceDataCaller.callUnscheduleInstallApp(selectedVersion.getAppHashid());
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						finish();
					}
				});
			}else{
				uninstall.setText(label);
				uninstall.setTextColor(Color.WHITE);
				uninstall.setOnClickListener(null);
			}
		}
	}
	
	private void activateUpdateButton(boolean activate){
		String label = getResources().getString(R.string.update);
		if(later.isChecked() && !selectedVersion.isScheduled()){
			label = getResources().getString(R.string.schedule);
		}
		if(activate){
			install.setText(label);
			install.setTextColor(Color.BLACK);			
			install.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {
					try {
						if (later.isChecked()) {
							serviceDataCaller.callScheduleInstallApp(selectedVersion.getAppHashid());
							Toast.makeText(AppInfo.this, getResources().getText(R.string.scheduled).toString(), Toast.LENGTH_LONG).show();
							Log.d("Aptoide-AppInfo", "called update app later");
						} else {
							if(selectedVersion.isScheduled()){
								serviceDataCaller.callUnscheduleInstallApp(selectedVersion.getAppHashid());
							}
							serviceDataCaller.callInstallApp(selectedVersion.getAppHashid());
//							Toast.makeText(AppInfo.this, getResources().getText(R.string.starting).toString()+" "
//														+getResources().getText(R.string.download).toString(), Toast.LENGTH_LONG).show();
							Log.d("Aptoide-AppInfo", "called update app");
						}
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					finish();
				}
			});
		}else{
			install.setText(label);
			install.setTextColor(Color.WHITE);
			install.setOnClickListener(null);
		}
		
	}
	
	private void activateDowngradeButton(boolean activate){
		String label = getResources().getString(R.string.downgrade);
		if(later.isChecked() && !selectedVersion.isScheduled()){
			label = getResources().getString(R.string.schedule);
		}
		if(activate){
			install.setText(label);
			install.setTextColor(Color.BLACK);			
			install.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {
					try {
						if (later.isChecked()) {
							serviceDataCaller.callScheduleInstallApp(selectedVersion.getAppHashid());
							Toast.makeText(AppInfo.this, getResources().getText(R.string.scheduled).toString(), Toast.LENGTH_LONG).show();
							Log.d("Aptoide-AppInfo", "called downgrade app later");
						} else {
							if(selectedVersion.isScheduled()){
								serviceDataCaller.callUnscheduleInstallApp(selectedVersion.getAppHashid());
							}
							serviceDataCaller.callInstallApp(selectedVersion.getAppHashid());
//							Toast.makeText(AppInfo.this, getResources().getText(R.string.starting).toString()+" "
//														+getResources().getText(R.string.download).toString(), Toast.LENGTH_LONG).show();
							Log.d("Aptoide-AppInfo", "called downgrade app");
						}
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					finish();
				}
			});
		}else{
			install.setText(label);
			install.setTextColor(Color.WHITE);
			install.setOnClickListener(null);
		}
		
	}
	
	private void selectVersion(ViewDisplayAppVersionInfo version){
		selectedVersion = version;
		Log.d("Aptoide-AppInfo", "Selected version: "+selectedVersion.getVersionName());
		
		if(installedVersion != null){
			if(selectedVersion.isInstalled()){
				activateUninstallButton(true);
				later.setVisibility(View.INVISIBLE);
				
				activateInstallButton(false);
			}else{ 
				activateUninstallButton(false);
				later.setVisibility(View.VISIBLE);
				
				if(selectedVersion.getVersionCode() > installedVersion.getVersionCode()){
					activateUpdateButton(true);
				}else if(selectedVersion.getVersionCode() < installedVersion.getVersionCode()){
					activateDowngradeButton(true);
				}
			}
		
		
		}else{
			activateUninstallButton(false);
			activateInstallButton(true);
		}
		
		try {

			Log.v("Aptoide-AppInfo", "Called for registering as AppInfo Observer");
			serviceDataCaller.callRegisterAppInfoObserver(serviceDataCallback, appHashid);
			
			if(version.getSize() == Constants.EMPTY_INT){
				serviceDataCaller.callAddVersionDownloadInfo(version.getAppHashid(), version.getRepoHashid());
				Log.d("Aptoide-AppInfo", "called addVersionDownloadInfo");
			}
			if(!version.isExtrasAvailable()){
				serviceDataCaller.callAddVersionExtraInfo(version.getAppHashid(), version.getRepoHashid());
				Log.d("Aptoide-AppInfo", "called AddVersionExtraInfo");
			}
			if(!version.isStatsAvailable()){
				serviceDataCaller.callAddVersionStatsInfo(version.getAppHashid(), version.getRepoHashid());
				Log.d("Aptoide-AppInfo", "called AddVersionStatsInfo");
			}
			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		setVersionRepo();
		setVersionSize();
		setVersionStats();
	}

	private void setIcon() {
		String icon_path = Constants.PATH_CACHE_ICONS + appHashid;
		ImageView icon = (ImageView) findViewById(R.id.icon);
		File test_icon = new File(icon_path);

		if (test_icon.exists() && test_icon.length() > 0) {
			icon.setImageDrawable(new BitmapDrawable(icon_path));
		} else {
			icon.setImageResource(android.R.drawable.sym_def_app_icon);
		}
	}

	private void setVersions() {
//		int selectedVersionHashid = 0;
//		if(selectedVersion != null){
//			selectedVersionHashid = selectedVersion.getAppHashid(); 
//		}
		try {
			appVersions = serviceDataCaller.callGetAppInfo(appHashid);

		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d("Aptoide-AppInfo", "appVersions: "+appVersions);

		ArrayList<String> versions = new ArrayList<String>();
		for (ViewDisplayAppVersionInfo versionInfo : appVersions) {
			versions.add(versionInfo.getVersionName());	
//			if(selectedVersionHashid == versionInfo.getAppHashid()){
//				selectedVersion = versionInfo;
//			}
			if(versionInfo.isInstalled()){
				installedVersion = versionInfo;
//				Log.d("Aptoide-AppInfo", "Installed version: "+installedVersion.getVersionName());
			}
//			else{
//				Log.d("Aptoide-AppInfo", "available version: "+versionInfo.getVersionName());				
//			}
		}
//		if(selectedVersion == null){
//		}
		
//		selectVersion(appVersions.get(0));
		selectedVersion = appVersions.get(0);
		

		multiVersionSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, versions);
		multiVersionSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		appMultiVersion.setAdapter(multiVersionSpinnerAdapter);

		appMultiVersion.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				selectVersion(appVersions.get(appMultiVersion.getSelectedItemPosition()));
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
		
		appName = selectedVersion.getAppName();
		appNameTextView.setText(appName);
		
		setVersionDescription();
		setScreens();
		
	}
	
	private void setVersionDescription() {
		if(selectedVersion.isExtrasAvailable()){
			//			appDescription = appVersions.toString();
			appDescription = selectedVersion.getExtras().getDescription();
			appDescriptionTextView.setText(""+appDescription);
		}
	}
	
	private void setVersionSize(){
		if(selectedVersion != null && selectedVersion.getSize() != Constants.EMPTY_INT){
			appSize = Integer.toString(selectedVersion.getSize());
			appSizeTextView.setText(getString(R.string.size)+": "+appSize+getString(R.string.kB));
		}else{
			appSizeTextView.setText(getString(R.string.size)+": ");
		}
	}
	
	private void setVersionRepo(){
		if(selectedVersion != null && selectedVersion.getRepoUri() != null){
			repoUri = selectedVersion.getRepoUri();
			repoUriTextView.setText(getString(R.string.store)+": "+repoUri);
		}else{
			repoUriTextView.setText(getString(R.string.store)+": "+getString(R.string.local));
		}
	}
	
	private void setVersionStats(){
		if(selectedVersion != null && selectedVersion.isStatsAvailable()){
			appDownloads = selectedVersion.getStats().getDownloads();
			appDownloadsTextView.setText(getString(R.string.downloads)+": "+appDownloads);

			appStars = selectedVersion.getStats().getStars();
			appStarsRating.setRating(new Float(appStars));

			appLikes = selectedVersion.getStats().getLikes();
			appLikesTextView.setText(""+appLikes);

			appDislikes = selectedVersion.getStats().getDislikes();
			appDislikesTextView.setText(""+appDislikes);
		}
	}

	private void setScreens() {
		ArrayList<Drawable> screensDrawables = new ArrayList<Drawable>();
		int orderNumber = 0;
		String screenPath = Constants.PATH_CACHE_SCREENS + appHashid + "." + orderNumber;
		File screen = null;
		do {
			Drawable screenDrawable = Drawable.createFromPath(screenPath);
			screensDrawables.add(screenDrawable);
			orderNumber++;
			screenPath = Constants.PATH_CACHE_SCREENS + appHashid + "." + orderNumber;
			screen = new File(screenPath);
		} while (screen.exists());
		galleryView.setAdapter(new ImageAdapter(AppInfo.this, screensDrawables, appName));
		galleryView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

				// Log.d("Aptoide","This view.....");
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
		if (serviceDataIsBound) {
			unbindService(serviceDataConnection);
		}
		super.finish();
	}

	

}
