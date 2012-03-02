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
	private ViewDisplayAppVersionInfo selectedVersion;
	
	private boolean storedForLater = false;
	
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
				if (serviceDataCaller.callIsAppScheduledToInstall(appHashid)) {
					storedForLater = true;
				} 
				later.setChecked(storedForLater);
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			try {
				Log.v("Aptoide-AppInfo",
						"Called for registering as AppInfo Observer");
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
		public void refreshIcon() throws RemoteException {
			Log.v("Aptoide-AppInfo", "received refreshIcon callback");
			interfaceTasksHandler.sendEmptyMessage(EnumAppInfoTasks.REFRESH_ICON.ordinal());
		}

		@Override
		public void newAppInfoAvailable() throws RemoteException {
			Log.v("Aptoide-AppInfo",
					"received newAppInfoAvailable callback");
			interfaceTasksHandler.sendEmptyMessage(EnumAppInfoTasks.UPDATE_APP_INFO.ordinal());
		}

		@Override
		public void newAppDownloadInfoAvailable() throws RemoteException {
			Log.v("Aptoide-AppInfo",
					"received newAppDownloadInfoAvailable callback");
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
		public void refreshScreens() throws RemoteException {
			Log.v("Aptoide-AppInfo", "received refreshScreens callback");
			interfaceTasksHandler.sendEmptyMessage(EnumAppInfoTasks.REFRESH_SCREENS.ordinal());
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

			case REFRESH_ICON:
				setIcon();
				break;

			case UPDATE_APP_INFO:
				setVersions();
				break;

			case UPDATE_APP_DOWNLOAD_INFO:
				setVersions();
				break;

			case UPDATE_APP_STATS:
				setVersions();
				break;

			case UPDATE_APP_EXTRAS:
				setVersions();
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

		setContentView(R.layout.app_info);
		later = (CheckBox) findViewById(R.id.later);
		later.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				selectVersion(selectedVersion);
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

		setIcon();
		
		if (!serviceDataIsBound) {
			bindService(new Intent(this, AptoideServiceData.class), serviceDataConnection, Context.BIND_AUTO_CREATE);
		}
	}
	
	private void activateInstallButton(boolean activate){
		String label = getResources().getString(R.string.install);
		if(later.isChecked() && !storedForLater){
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
							if(storedForLater){
								serviceDataCaller.callUnscheduleInstallApp(selectedVersion.getAppHashid());
							}
							serviceDataCaller.callInstallApp(selectedVersion.getAppHashid());
							Toast.makeText(AppInfo.this, getResources().getText(R.string.starting).toString()+" "
														+getResources().getText(R.string.download).toString(), Toast.LENGTH_LONG).show();
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
			if(storedForLater){
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
		if(later.isChecked() && !storedForLater){
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
							if(storedForLater){
								serviceDataCaller.callUnscheduleInstallApp(selectedVersion.getAppHashid());
							}
							serviceDataCaller.callInstallApp(selectedVersion.getAppHashid());
							Toast.makeText(AppInfo.this, getResources().getText(R.string.starting).toString()+" "
														+getResources().getText(R.string.download).toString(), Toast.LENGTH_LONG).show();
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
		if(later.isChecked() && !storedForLater){
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
							if(storedForLater){
								serviceDataCaller.callUnscheduleInstallApp(selectedVersion.getAppHashid());
							}
							serviceDataCaller.callInstallApp(selectedVersion.getAppHashid());
							Toast.makeText(AppInfo.this, getResources().getText(R.string.starting).toString()+" "
														+getResources().getText(R.string.download).toString(), Toast.LENGTH_LONG).show();
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
		Log.d("Aptoide-AppInfo", "Selected version: "+selectedVersion);
		
		if(installedVersion != null){
			activateUninstallButton(true);
			if(selectedVersion.equals(installedVersion)){
				activateInstallButton(false);
			}else if(selectedVersion.getVersionCode() > installedVersion.getVersionCode()){
				activateUpdateButton(true);
			}else if(selectedVersion.getVersionCode() < installedVersion.getVersionCode()){
				activateDowngradeButton(true);
			}
		}else{
			activateUninstallButton(false);
			activateInstallButton(true);
		}
		
		setVersionDetails();
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
		try {
			appVersions = serviceDataCaller.callGetAppInfo(appHashid);
			Log.d("Aptoide-AppInfo", "Got app versions: " + appVersions);

		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (ViewDisplayAppVersionInfo versionInfo : appVersions.getVersionsList()) {
			if(versionInfo.isInstalled()){
				installedVersion = versionInfo;
				Log.d("Aptoide-AppInfo", "Installed version: "+installedVersion);
				break;
			}
		}
		
		selectVersion(appVersions.getVersionsList().get(0));
		
		appName = selectedVersion.getAppName();
		appNameTextView.setText(appName);
		
		ArrayList<String> versions = new ArrayList<String>();
		for (ViewDisplayAppVersionInfo versionInfo : appVersions.getVersionsList()) {
			versions.add(versionInfo.getVersionName());				
		}

		multiVersionSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, versions);
		multiVersionSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		appMultiVersion.setAdapter(multiVersionSpinnerAdapter);

		appMultiVersion.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				selectVersion(appVersions.getVersionsList().get(appMultiVersion.getSelectedItemPosition()));
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
		
		setVersionDescription();
		
	}
	
	private void setVersionDescription() {
		if(selectedVersion.isExtrasAvailable()){
			//			appDescription = appVersions.toString();
			appDescription = selectedVersion.getExtras().getDescription();
			appDescriptionTextView.setText(""+appDescription);
		}
	}
	
	private void setVersionDetails(){
		if(selectedVersion != null && selectedVersion.getSize() != Constants.EMPTY_INT){
			appSize = Integer.toString(selectedVersion.getSize());
			appSizeTextView.setText("Size: "+appSize+"KB");
		}else{
			appSizeTextView.setText("Size: ");
		}
		
		if(selectedVersion != null && selectedVersion.getRepoUri() != null){
			repoUri = selectedVersion.getRepoUri();
			repoUriTextView.setText("Store: "+repoUri);
		}else{
			repoUriTextView.setText("Store: Local");
		}
	}
	
	private void setVersionStats(){
		if(selectedVersion != null && selectedVersion.isStatsAvailable()){
			appDownloads = selectedVersion.getStats().getDownloads();
			appDownloadsTextView.setText("Downloads: "+appDownloads);

			appStars = selectedVersion.getStats().getStars();
			appStarsRating.setRating(new Float(appStars));

			appLikes = selectedVersion.getStats().getLikes();
			appLikesTextView.setText("Likes: "+appLikes);

			appDislikes = selectedVersion.getStats().getDislikes();
			appDislikesTextView.setText("Don't likes: "+appDislikes);
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
