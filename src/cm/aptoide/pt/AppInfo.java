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

import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import cm.aptoide.pt.data.AIDLAptoideServiceData;
import cm.aptoide.pt.data.AptoideServiceData;
import cm.aptoide.pt.data.cache.ViewCache;
import cm.aptoide.pt.data.display.ViewDisplayAppVersionInfo;
import cm.aptoide.pt.data.display.ViewDisplayAppVersionsInfo;
import cm.aptoide.pt.data.display.ViewDisplayComment;
import cm.aptoide.pt.data.util.Constants;
import cm.aptoide.pt.data.webservices.EnumServerStatus;
import cm.aptoide.pt.ifaceutil.ImageAdapter;
import cm.aptoide.pt.ifaceutil.StaticCommentsListAdapter;

/**
 * AppInfo, interface class to display the details of a specific application
 * 
 * @author dsilveira
 * @since 3.0
 * 
 */
public class AppInfo extends ListActivity {

	private int appHashid;
	private String appName;
	private int appDownloads;
	private String appSize;
	private float appStars;
	private String repoUri;
	private int appLikes;
	private int appDislikes;
	private String appDescription;

	private StaticCommentsListAdapter commentsAdapter;
	
	private ViewDisplayAppVersionsInfo appVersions;

	private ViewDisplayAppVersionInfo installedVersion = null;
	private ViewDisplayAppVersionInfo selectedVersion = null;
	
	private int scheduledVersion = Constants.EMPTY_INT;
	private int unscheduleVersion = Constants.EMPTY_INT;
	
	CheckBox later;
	private TextView appNameTextView;
	private TextView appDownloadsTextView;
	private TextView appSizeTextView;
	private RatingBar appStarsRating;
	private TextView repoUriTextView;
	private TextView appLikesTextView;
	private ImageView appLikesImageView;
	private TextView appDislikesTextView;
	private ImageView appDislikesImageView;
	private TextView appDescriptionTextView;
	private Spinner appMultiVersion;

	private TextView screens;
	Gallery galleryView;
	
	private TextView commentOnApp;
//	ListView comments;
	private TextView noComments;
	
	private Button install;
	private Button uninstall;
	
	private String token = null;
	
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
	
			
//			try {
//				Log.v("Aptoide-AppInfo", "Called for registering as AppInfo Observer");
//				serviceDataCaller.callRegisterAppInfoObserver(serviceDataCallback, appHashid);
//
//			} catch (RemoteException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}

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
			versionInfoManager.updateAppSize(appFullHashid);
		}

		@Override
		public void newStatsInfoAvailable(int appFullHashid) throws RemoteException {
			Log.v("Aptoide-AppInfo", "received newStatsInfoAvailable callback");
			versionInfoManager.updateAppStats(appFullHashid);
		}

		@Override
		public void newExtrasAvailable(int appFullHashid) throws RemoteException {
			Log.v("Aptoide-AppInfo", "received newExtrasAvailable callback");
			versionInfoManager.updateAppDescription(appFullHashid);
		}

		@Override
		public void newCommentsAvailable(int appFullHashid) throws RemoteException {
			Log.v("Aptoide-AppInfo", "received newCommentsAvailable callback");
			versionInfoManager.updateAppComments(appFullHashid);
		}

		@Override
		public void refreshScreens() throws RemoteException {
			Log.v("Aptoide-AppInfo", "received refreshScreens callback");
			interfaceTasksHandler.sendEmptyMessage(EnumAppInfoTasks.REFRESH_SCREENS.ordinal());
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
				
			case UPDATE_APP_COMMENTS:
				setVersionComments();
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
    	
    	private int getPosition(int appFullHashid){
			for (int position=0; position < appVersions.size(); position++) {
				if(appVersions.get(position).getAppFullHashid() == appFullHashid){
					return position;
				}
			}
			return 0;
    	}
    	
    	public void updateAppSize(int appFullHashid){
        	versionInfoColectorsPool.execute(new GetAppSize(appFullHashid, getPosition(appFullHashid)));
        }
    	
    	public void updateAppStats(int appFullHashid){
        	versionInfoColectorsPool.execute(new GetAppStats(appFullHashid, getPosition(appFullHashid)));
        }
    	
    	public void updateAppDescription(int appFullHashid){
        	versionInfoColectorsPool.execute(new GetAppDescription(appFullHashid, getPosition(appFullHashid)));
        }
    	
    	public void updateAppComments(int appFullHashid){
        	versionInfoColectorsPool.execute(new GetAppComments(appFullHashid, getPosition(appFullHashid)));
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
//					Log.d("Aptoide-AppInfo", "Called for registering as AppInfo Observer");
//					serviceDataCaller.callRegisterAppInfoObserver(serviceDataCallback, appHashid);
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
//					Log.d("Aptoide-AppInfo", "Called for registering as AppInfo Observer");
//					serviceDataCaller.callRegisterAppInfoObserver(serviceDataCallback, appHashid);
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
//					Log.d("Aptoide-AppInfo", "Called for registering as AppInfo Observer");
//					serviceDataCaller.callRegisterAppInfoObserver(serviceDataCallback, appHashid);
					appVersions.get(position).setExtras(serviceDataCaller.callGetAppExtras(appFullHashid));
					interfaceTasksHandler.sendEmptyMessage(EnumAppInfoTasks.UPDATE_APP_DESCRIPTION.ordinal());
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
    		
    	}
    	
    	private class GetAppComments implements Runnable{
    		int appFullHashid = Constants.EMPTY_INT;
    		int position = Constants.EMPTY_INT;
    		

			public GetAppComments(int appFullHashid, int position) {
				this.appFullHashid = appFullHashid;
				this.position = position;
			}


			@Override
			public void run() {
				try {
					
//					Log.d("Aptoide-AppInfo", "Called for registering as AppInfo Observer");
//					serviceDataCaller.callRegisterAppInfoObserver(serviceDataCallback, appHashid);
					appVersions.get(position).setComments(serviceDataCaller.callGetVersionComments(appFullHashid));
					interfaceTasksHandler.sendEmptyMessage(EnumAppInfoTasks.UPDATE_APP_COMMENTS.ordinal());
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

		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		
		appHashid = getIntent().getIntExtra("appHashid", 0);

		setContentView(R.layout.app_info);
	

		setIcon();
		
		if (!serviceDataIsBound) {
			bindService(new Intent(this, AptoideServiceData.class), serviceDataConnection, Context.BIND_AUTO_CREATE);
		}
		
		
		later = (CheckBox) findViewById(R.id.later);
		later.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				toggleVersionDownloadScheduleStatus(selectedVersion); 
			}
		});
//		later.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				toggleVersionDownloadScheduleStatus(selectedVersion); 
//			}
//		});
		appNameTextView = (TextView) findViewById(R.id.app_name);
		appDownloadsTextView = (TextView) findViewById(R.id.app_downloads);
		appSizeTextView = (TextView) findViewById(R.id.app_size);
		appStarsRating = (RatingBar) findViewById(R.id.app_rating);
		repoUriTextView = (TextView) findViewById(R.id.app_store);
		appLikesTextView = (TextView) findViewById(R.id.app_likes);
		appDislikesTextView = (TextView) findViewById(R.id.app_dislikes);
		appDescriptionTextView = (TextView) findViewById(R.id.app_description);
		appMultiVersion = ((Spinner) findViewById(R.id.spinner_multi_version));
		
		ImageView searchView = (ImageView) findViewById(R.id.search_button);
		searchView.setVisibility(View.GONE);

		install = (Button) findViewById(R.id.install);
		install.setTextColor(Color.DKGRAY);
		uninstall = (Button) findViewById(R.id.uninstall);
		uninstall.setTextColor(Color.DKGRAY);

		screens = (TextView) findViewById(R.id.screens);
		galleryView = (Gallery) findViewById(R.id.screens_gallery);
		
		commentOnApp = (TextView) findViewById(R.id.comment_on_app);
		commentOnApp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d("Aptoide-AppInfo", "Clicked on comment");

				if(selectedVersion.getRepoUri() != null){
					if(token == null){
						try {
							token = serviceDataCaller.callGetServerToken();
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if(token == null){
						Log.d("Aptoide-AppInfo", "No login set");
						DialogLogin loginComments = new DialogLogin(AppInfo.this, serviceDataCaller, DialogLogin.InvoqueNature.NO_CREDENTIALS_SET);
						loginComments.setOnDismissListener(new OnDismissListener() {
							@Override
							public void onDismiss(DialogInterface dialog) {
								addAppVersionComment();
							}
						});
						loginComments.show();
					}else{
						addAppVersionComment();						
					}
				}
			}
		});
		noComments = (TextView) findViewById(android.R.id.empty);
//		comments = (ListView) findViewById(R.id.list_comments);
				
//		commentsAdapter = new StaticCommentsListAdapter(this, comments, serviceDataCaller);
		
		versionInfoManager = new VersionInfoManager();
		
		
		appLikesImageView = ((ImageView) findViewById(R.id.likesImage));
		appLikesImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d("Aptoide-AppInfo", "Clicked on like");

				if(selectedVersion.getRepoUri() != null){
					if(token == null){
						try {
							token = serviceDataCaller.callGetServerToken();
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if(token == null){
						Log.d("Aptoide-AppInfo", "No login set");
						DialogLogin loginLikes = new DialogLogin(AppInfo.this, serviceDataCaller, DialogLogin.InvoqueNature.NO_CREDENTIALS_SET);
						loginLikes.setOnDismissListener(new OnDismissListener() {
							@Override
							public void onDismiss(DialogInterface dialog) {
								addAppVersionLike(true);
							}
						});
						loginLikes.show();
					}else{
						addAppVersionLike(true);
					}
				}
			}
		});
		
		appDislikesImageView = ((ImageView) findViewById(R.id.dislikesImage));
		appDislikesImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d("Aptoide-AppInfo", "Clicked on dislike");

				if(selectedVersion.getRepoUri() != null){
					if(token == null){
						try {
							token = serviceDataCaller.callGetServerToken();
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if(token == null){
						Log.d("Aptoide-AppInfo", "No login set");
						DialogLogin loginLikes = new DialogLogin(AppInfo.this, serviceDataCaller, DialogLogin.InvoqueNature.NO_CREDENTIALS_SET);
						loginLikes.setOnDismissListener(new OnDismissListener() {
							@Override
							public void onDismiss(DialogInterface dialog) {
								addAppVersionLike(false);
							}
						});
						loginLikes.show();
					}else{
						addAppVersionLike(false);						
					}
				}
			}
		});
		
//		like.setOnTouchListener(new OnTouchListener(){
//		      public boolean onTouch(View view, MotionEvent e) {
//		          switch(e.getAction())
//		          {
//		             case MotionEvent.ACTION_DOWN:
//		            	 
//		            	 if(sharedPreferences.getString(Configs.LOGIN_USER_NAME, null)==null || sharedPreferences.getString(Configs.LOGIN_PASSWORD, null)==null){				
//		            		LoginDialog loginComments = new LoginDialog(ApkInfo.this, LoginDialog.InvoqueNature.NO_CREDENTIALS_SET, like, 
//		            										dislike, apk_repo_str_raw , 
//		            										apk_id, apk_ver_str_raw, EnumUserTaste.LIKE, userTaste);
//							loginComments.setOnDismissListener(ApkInfo.this);
//							loginComments.show();
//						 }else{
//							
//							 new AddTaste(
//					 				ApkInfo.this, 
//					 				apk_repo_str_raw,
//					 				apk_id, 
//					 				apk_ver_str_raw, 
//					 				sharedPreferences.getString(Configs.LOGIN_USER_NAME, null), 
//					 				sharedPreferences.getString(Configs.LOGIN_PASSWORD, null), 
//					 				EnumUserTaste.LIKE, likes, dislikes, like, dislike, userTaste).submit();
//							
//						 } 
//		            	 break;
//		          }
//		          return false;  //means that the listener dosen't consume the event
//		      }
//		});
//		
//		this.dislike = ((ImageView)linearLayout.findViewById(R.id.dislikesImage));
//		dislike.setOnTouchListener(new OnTouchListener(){
//		      public boolean onTouch(View view, MotionEvent e) {
//		          switch(e.getAction())
//		          {
//		             case MotionEvent.ACTION_DOWN:
//		            	 
//		            	  if(sharedPreferences.getString(Configs.LOGIN_USER_NAME, null)==null || sharedPreferences.getString(Configs.LOGIN_PASSWORD, null)==null){				
//		            		  	LoginDialog loginComments = new LoginDialog(ApkInfo.this, LoginDialog.InvoqueNature.NO_CREDENTIALS_SET, like, dislike, 
//		            		  									apk_repo_str_raw, apk_id, apk_ver_str_raw, EnumUserTaste.DONTLIKE, userTaste);
//		            		  	loginComments.setOnDismissListener(ApkInfo.this);
//								loginComments.show();
//		            	  }else{
//		            		  
//		            		  new AddTaste(
//							 		ApkInfo.this, 
//							 		apk_repo_str_raw,
//							 		apk_id, 
//							 		apk_ver_str_raw, 
//							 		sharedPreferences.getString(Configs.LOGIN_USER_NAME, null), 
//							 		sharedPreferences.getString(Configs.LOGIN_PASSWORD, null), 
//							 		EnumUserTaste.DONTLIKE, likes, dislikes, like, dislike, userTaste).submit();
//							 
//		            	  }
//		                  break;
//		          }
//		          return false;  //means that the listener dosen't consume the event
//		      }
//		});

	}
	
	
	
	private void toggleVersionDownloadScheduleStatus(ViewDisplayAppVersionInfo appVersion){
//		try {
//			if(appVersion.isScheduled()){
//				serviceDataCaller.callUnscheduleInstallApp(selectedVersion.getAppHashid());
//			}else{
//				serviceDataCaller.callScheduleInstallApp(selectedVersion.getAppHashid());
//			}
//		} catch (RemoteException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		appVersion.setIsScheduled(!appVersion.isScheduled());
		if(appVersion.getAppHashid() == scheduledVersion){
			unscheduleVersion = scheduledVersion;
			scheduledVersion = Constants.EMPTY_INT;
		}else{
			scheduledVersion = appVersion.getAppHashid();
		}
		selectVersion(appVersion);
	}
	
	private void selectVersion(ViewDisplayAppVersionInfo version){
		selectedVersion = version;
		Log.d("Aptoide-AppInfo", "Selected version: "+selectedVersion.getVersionName());
		
		if(scheduledVersion == selectedVersion.getAppHashid() || (scheduledVersion == Constants.EMPTY_INT && version.isScheduled())){
			later.setChecked(true);
		}else{
			later.setChecked(false);
		}
		Log.d("Aptoide-AppInfo", "isScheduled: "+selectedVersion.isScheduled()+" later checked: "+later.isChecked());
		
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
			serviceDataCaller.callRegisterAppInfoObserver(serviceDataCallback, version.getAppHashid());
			if(version.getRepoHashid() != Constants.EMPTY_INT){
				if(version.getSize() == Constants.EMPTY_INT){
					serviceDataCaller.callAddVersionDownloadInfo(version.getAppHashid(), version.getRepoHashid());
					Log.d("Aptoide-AppInfo", "called addVersionDownloadInfo");
				}
				if(!version.isExtrasAvailable() || version.getExtras() != null){
					serviceDataCaller.callAddVersionExtraInfo(version.getAppHashid(), version.getRepoHashid());
					Log.d("Aptoide-AppInfo", "called AddVersionExtraInfo");
				}
				if(!version.isStatsAvailable() || version.getStats() != null){
					serviceDataCaller.callAddVersionStatsInfo(version.getAppHashid(), version.getRepoHashid());
					Log.d("Aptoide-AppInfo", "called AddVersionStatsInfo");
				}
				serviceDataCaller.callRetrieveVersionComments(version.getAppHashid(), version.getRepoHashid());
				Log.d("Aptoide-AppInfo", "called RetrieveVersionComments");
			}
			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		setVersionRepo();
		setVersionSize();
		setVersionStats();
		setVersionComments();
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
			if(appVersions == null){
				appVersions = serviceDataCaller.callGetAppInfo(appHashid);	
			}

		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d("Aptoide-AppInfo", "appVersions: "+appVersions);
		if(appVersions == null){
			finish();
			return;
		}

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
	
	private void setVersionSize(){
		if(selectedVersion != null && selectedVersion.getSize() != Constants.EMPTY_INT){
			appSize = Integer.toString(selectedVersion.getSize());
			appSizeTextView.setText(getString(R.string.size)+": "+appSize+getString(R.string.kB));
			appSizeTextView.setVisibility(View.VISIBLE);
		}else{
			if(selectedVersion.isInstalled()){
				appSizeTextView.setVisibility(View.GONE);
			}else{
				appSizeTextView.setText(getString(R.string.size)+": ");
				appSizeTextView.setVisibility(View.VISIBLE);
			}
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
			appDownloadsTextView.setVisibility(View.VISIBLE);

			appStars = selectedVersion.getStats().getStars();
			appStarsRating.setRating(new Float(appStars));
			appStarsRating.setVisibility(View.VISIBLE);
			
			appLikes = selectedVersion.getStats().getLikes();
			appLikesTextView.setText(""+appLikes);
			appLikesTextView.setVisibility(View.VISIBLE);
			appLikesImageView.setVisibility(View.VISIBLE);

			appDislikes = selectedVersion.getStats().getDislikes();
			appDislikesTextView.setText(""+appDislikes);
			appDislikesTextView.setVisibility(View.VISIBLE);
			appDislikesImageView.setVisibility(View.VISIBLE);
		}else{
			if(selectedVersion.isInstalled()){
				appDownloadsTextView.setVisibility(View.GONE);
				appStarsRating.setVisibility(View.INVISIBLE);
				appLikesImageView.setVisibility(View.GONE);
				appDislikesImageView.setVisibility(View.GONE);
				appLikesTextView.setVisibility(View.GONE);
				appDislikesTextView.setVisibility(View.GONE);				
			}else{
				appDownloadsTextView.setText(getString(R.string.downloads)+": ");
				appDownloadsTextView.setVisibility(View.VISIBLE);
				appStarsRating.setVisibility(View.VISIBLE);
				appLikesImageView.setVisibility(View.VISIBLE);
				appDislikesImageView.setVisibility(View.VISIBLE);	
				appLikesTextView.setVisibility(View.INVISIBLE);
				appDislikesTextView.setVisibility(View.INVISIBLE);			
			}
		}
	}
	
	private void setVersionDescription() {
		if(selectedVersion.isExtrasAvailable() && selectedVersion.getExtras()!=null){
			//			appDescription = appVersions.toString();
			appDescription = selectedVersion.getExtras().getDescription();
			appDescriptionTextView.setText(""+appDescription);
		}
	}
	
	private void setVersionComments(){
		if(selectedVersion.isCommentsAvailable() && selectedVersion.getComments() != null){
	//		commentsAdapter.resetDisplayComments(selectedVersion.getAppFullHashid());
	//		commentsAdapter.resetDisplayComments(selectedVersion.getComments());
			commentsAdapter = new StaticCommentsListAdapter(this, selectedVersion.getComments());
			setListAdapter(commentsAdapter);
			commentOnApp.setVisibility(View.VISIBLE);
			getListView().setVisibility(View.VISIBLE);
		}else{
			if(selectedVersion.isInstalled()){
				commentOnApp.setVisibility(View.GONE);
			}else{
				commentOnApp.setVisibility(View.VISIBLE);
			}
			setListAdapter(null);
//			getListView().setVisibility(View.INVISIBLE);
		}
	}

	private void setScreens() {
		ArrayList<Drawable> screensDrawables = new ArrayList<Drawable>();
		int orderNumber = 0;
		String screenPath = Constants.PATH_CACHE_SCREENS + appHashid + "." + orderNumber;
		File screen = new File(screenPath);
		if(screen.exists()){
			do {
				screensDrawables.add(Drawable.createFromPath(screenPath));
				orderNumber++;
				screenPath = Constants.PATH_CACHE_SCREENS + appHashid + "." + orderNumber;
				screen = new File(screenPath);
			} while (screen.exists());			
		}
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
		if(galleryView.getAdapter().getCount() == Constants.EMPTY_INT){
			screens.setVisibility(View.GONE);
			galleryView.setVisibility(View.GONE);
			
		}else{
			screens.setVisibility(View.VISIBLE);
			galleryView.setVisibility(View.VISIBLE);
		}
	}
	
	
	
	@Override
	protected void onListItemClick(ListView list, View view, int position, long id) {
		final int commentPosition = position;
//		Log.d("Aptoide-AppInfo", "click on comment, position: "+commentPosition+" comments: "+selectedVersion.getComments());
		if(token == null){
			try {
				token = serviceDataCaller.callGetServerToken();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(token == null){
			Log.d("Aptoide-AppInfo", "No login set");
			DialogLogin loginComments = new DialogLogin(AppInfo.this, serviceDataCaller, DialogLogin.InvoqueNature.NO_CREDENTIALS_SET);
			loginComments.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
					String repoName = selectedVersion.getRepoUri().substring(Constants.SKIP_URI_PREFIX).split("\\.")[Constants.FIRST_ELEMENT];
					DialogAddComment addComment = new DialogAddComment(AppInfo.this, repoName, selectedVersion.getAppHashid(), ((ViewDisplayComment) getListAdapter().getItem(commentPosition)).getCommentId(), ((ViewDisplayComment) getListAdapter().getItem(commentPosition)).getUserName());
					addComment.setOnDismissListener(new OnDismissListener() {
						@Override
						public void onDismiss(DialogInterface dialog) {
//							setListAdapter(null);
							try {
								serviceDataCaller.callRetrieveVersionComments(selectedVersion.getAppHashid(), selectedVersion.getRepoHashid());
							} catch (RemoteException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							Log.d("Aptoide-AppInfo", "called RetrieveVersionComments");
//							versionInfoManager.updateAppComments(selectedVersion.getAppFullHashid());
						}
					});
					addComment.show();
				}
			});
			loginComments.show();
		}else{
			String repoName = selectedVersion.getRepoUri().substring(Constants.SKIP_URI_PREFIX).split("\\.")[Constants.FIRST_ELEMENT];
			DialogAddComment addComment = new DialogAddComment(this, repoName, selectedVersion.getAppHashid(), ((ViewDisplayComment) getListAdapter().getItem(commentPosition)).getCommentId(), ((ViewDisplayComment) getListAdapter().getItem(commentPosition)).getUserName());
			addComment.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
//					setListAdapter(null);
					try {
						serviceDataCaller.callRetrieveVersionComments(selectedVersion.getAppHashid(), selectedVersion.getRepoHashid());
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Log.d("Aptoide-AppInfo", "called RetrieveVersionComments");
//					versionInfoManager.updateAppComments(selectedVersion.getAppFullHashid());
				}
			});
			addComment.show();
		}		
		
		super.onListItemClick(list, view, position, id);
	}



	private void addAppVersionComment(){
		if(token == null){
			try {
				token = serviceDataCaller.callGetServerToken();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(token != null){
			String repoName = selectedVersion.getRepoUri().substring(Constants.SKIP_URI_PREFIX).split("\\.")[Constants.FIRST_ELEMENT];
			DialogAddComment addComment = new DialogAddComment(this, repoName, selectedVersion.getAppHashid(), Constants.EMPTY_INT, null);
			addComment.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
//					setListAdapter(null);
					try {
						serviceDataCaller.callRetrieveVersionComments(selectedVersion.getAppHashid(), selectedVersion.getRepoHashid());
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Log.d("Aptoide-AppInfo", "called RetrieveVersionComments");
//					versionInfoManager.updateAppComments(selectedVersion.getAppFullHashid());
				}
			});
			addComment.show();
		}
	}
	
	private void addAppVersionLike(boolean like){
		if(token == null){
			try {
				token = serviceDataCaller.callGetServerToken();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(token != null){
			String repoName = selectedVersion.getRepoUri().substring(Constants.SKIP_URI_PREFIX).split("\\.")[Constants.FIRST_ELEMENT];
			new PostLike(repoName, appHashid, like).execute();
		}
	}
	
	
	
	private void activateInstallButton(boolean activate){
		String label = getResources().getString(R.string.install);
		if(scheduledVersion == selectedVersion.getAppHashid() && !selectedVersion.isScheduled()){
			label = getResources().getString(R.string.schedule);
		}
		if(activate){
			install.setText(label);
			install.setTextColor(Color.BLACK);			
			install.setOnClickListener(new OnClickListener() {
	
				@Override
				public void onClick(View view) {
					if(scheduledVersion == selectedVersion.getAppHashid() && !selectedVersion.isScheduled()){
						try {
							if(unscheduleVersion != Constants.EMPTY_INT){
								serviceDataCaller.callUnscheduleInstallApp(unscheduleVersion);
							}
							serviceDataCaller.callScheduleInstallApp(scheduledVersion);
							Toast.makeText(AppInfo.this, getResources().getText(R.string.scheduled).toString(), Toast.LENGTH_SHORT).show();
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
			if( selectedVersion.isScheduled()){
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
		if(scheduledVersion == selectedVersion.getAppHashid() && !selectedVersion.isScheduled()){
			label = getResources().getString(R.string.schedule);
		}
		if(activate){
			install.setText(label);
			install.setTextColor(Color.BLACK);			
			install.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {
					try {
						if (scheduledVersion == selectedVersion.getAppHashid() && !selectedVersion.isScheduled()) {
							if(unscheduleVersion != Constants.EMPTY_INT){
								serviceDataCaller.callUnscheduleInstallApp(unscheduleVersion);
							}
							serviceDataCaller.callScheduleInstallApp(scheduledVersion);
							Toast.makeText(AppInfo.this, getResources().getText(R.string.scheduled).toString(), Toast.LENGTH_SHORT).show();
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
		if(scheduledVersion == selectedVersion.getAppHashid() && !selectedVersion.isScheduled()){
			label = getResources().getString(R.string.schedule);
		}
		if(activate){
			install.setText(label);
			install.setTextColor(Color.BLACK);			
			install.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {
					try {
						if (scheduledVersion == selectedVersion.getAppHashid() && !selectedVersion.isScheduled()) {
							if(unscheduleVersion != Constants.EMPTY_INT){
								serviceDataCaller.callUnscheduleInstallApp(unscheduleVersion);
							}
							serviceDataCaller.callScheduleInstallApp(scheduledVersion);
							Toast.makeText(AppInfo.this, getResources().getText(R.string.scheduled).toString(), Toast.LENGTH_SHORT).show();
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

	
	
	@Override
	public void finish() {
//		commentsAdapter.shutdownNow();
		versionInfoManager.versionInfoColectorsPool.shutdownNow();
		if (serviceDataIsBound) {
			unbindService(serviceDataConnection);
		}
		super.finish();
	}

	
	
	public class DialogAddComment extends Dialog{
		private EditText subject;
		private EditText body;
		
		private String repoName;
		private int appHashid;
		
		private long answerTo;
		private String answerToUser;
		private ProgressDialog dialogProgress;
		
		public DialogAddComment(Context context, String repoName, int appHashid, long answerTo, String answerToUser) {
			super(context);
			
			this.repoName = repoName;
			this.appHashid = appHashid; 
			
			this.answerTo = answerTo;
			this.answerToUser = answerToUser;
		}
		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			this.setContentView(R.layout.dialog_add_comment);
			this.setTitle(getContext().getString(R.string.comment_on_app));

			Log.d("Aptoide-AppInfo", "comment answerTo: "+answerToUser);
			if(answerToUser!= null){
				TextView inresponse = ((TextView)findViewById(R.id.answer_to));
				inresponse.append(": "+answerToUser);
				inresponse.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
				inresponse.setLayoutParams(inresponse.getLayoutParams());
			}

			subject = ((EditText)findViewById(R.id.subject));
			body = ((EditText)findViewById(R.id.body));
			
			final Button submit = ((Button)findViewById(R.id.submit));
			
			submit.setOnClickListener(new View.OnClickListener(){
				public void onClick(View arg) {
					if(body.getText().toString().trim().equals("") ){
						Log.d("Aptoide-AppInfo", "comment body: "+body.getText().toString().trim());
						Toast.makeText(getContext(), getContext().getString(R.string.no_body), Toast.LENGTH_LONG).show();
					} else {
						dialogProgress = ProgressDialog.show(AppInfo.this, AppInfo.this.getString(R.string.submitting), AppInfo.this.getString(R.string.please_wait),true);
						dialogProgress.setIcon(android.R.drawable.ic_dialog_info);
						dialogProgress.setCancelable(true);
						dialogProgress.setOnDismissListener(new OnDismissListener() {
							@Override
							public void onDismiss(DialogInterface dialog) {
								dismiss();
							}
						});
						Log.d("Aptoide-AppInfo", "comment body: "+body.getText().toString().trim());
						new PostComment(repoName, appHashid, body.getText().toString(), subject.getText().toString(), answerTo).execute();
					}
				}
			});
			
		}
		
		class PostComment extends AsyncTask<Void, Void, EnumServerStatus>{
			private String repoName;
			private int appHashid;
			
			private String body; 
			private String subject;
			private long answerTo;
			
			
			public PostComment(String repoName, int appHashid, String body, String subject, long answerTo){
				this.repoName = repoName;
				this.appHashid = appHashid;
				
				this.body = body;
				this.subject = subject;
				this.answerTo = answerTo;

			}

			@Override
			protected EnumServerStatus doInBackground(Void... params) {
				try {
					return EnumServerStatus.reverseOrdinal(serviceDataCaller.callAddAppVersionComment(repoName, appHashid, body, subject, answerTo));
				} catch (RemoteException e){
					e.printStackTrace();
					return null;
				}
			}
			
			@Override
			protected void onPostExecute(EnumServerStatus status) {
				Log.d("Aptoide-AppInfo", "comment status: "+status);
				if(status!=null){
					if(status.equals(EnumServerStatus.SUCCESS)){
						Toast.makeText(getContext(), getContext().getString(R.string.submitted), Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(getContext(), status.toString(), Toast.LENGTH_SHORT).show();
					}
				}else{
					Toast.makeText(getContext(), getContext().getString(R.string.service_unavailable), Toast.LENGTH_SHORT).show();
				}
				dialogProgress.dismiss();
//				dismiss();
			}
			
		}
		
	}
	
	
	
	class PostLike extends AsyncTask<Void, Void, EnumServerStatus>{
		private String repoName;
		private int appHashid;
		
		private boolean like; 
		
		private ProgressDialog dialogProgress;
		
		public PostLike(String repoName, int appHashid, boolean like){
			this.repoName = repoName;
			this.appHashid = appHashid;
			
			this.like = like;
			
			dialogProgress = ProgressDialog.show(AppInfo.this, AppInfo.this.getString(R.string.submitting), AppInfo.this.getString(R.string.please_wait),true);
			dialogProgress.setIcon(android.R.drawable.ic_dialog_info);
			dialogProgress.setCancelable(true);
		}

		@Override
		protected EnumServerStatus doInBackground(Void... params) {
			try {
				return EnumServerStatus.reverseOrdinal(serviceDataCaller.callAddAppVersionLike(repoName, appHashid, like));
			} catch (RemoteException e){
				e.printStackTrace();
				return null;
			}
		}
		
		@Override
		protected void onPostExecute(EnumServerStatus status) {
			Log.d("Aptoide-AppInfo", "like status: "+status);
			if(status!=null){
				if(status.equals(EnumServerStatus.SUCCESS)){
					Toast.makeText(AppInfo.this, AppInfo.this.getString(R.string.submitted), Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(AppInfo.this, status.toString(), Toast.LENGTH_SHORT).show();
				}
			}else{
				Toast.makeText(AppInfo.this, AppInfo.this.getString(R.string.service_unavailable), Toast.LENGTH_SHORT).show();
			}
			dialogProgress.dismiss();
			try {
				serviceDataCaller.callAddVersionStatsInfo(selectedVersion.getAppHashid(), selectedVersion.getRepoHashid());
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.d("Aptoide-AppInfo", "called AddVersionStatsInfo");
//			versionInfoManager.updateAppStats(selectedVersion.getAppFullHashid());
		}
		
	}

}
