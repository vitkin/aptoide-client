/**
 * AptoideServiceData, part of Aptoide
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;
import cm.aptoide.pt.AIDLAppInfo;
import cm.aptoide.pt.AIDLAptoideInterface;
import cm.aptoide.pt.AIDLReposInfo;
import cm.aptoide.pt.AIDLSelfUpdate;
import cm.aptoide.pt.Aptoide;
import cm.aptoide.pt.EnumAppsSorting;
import cm.aptoide.pt.ManageRepos;
import cm.aptoide.pt.R;
import cm.aptoide.pt.SelfUpdate;
import cm.aptoide.pt.Splash;
import cm.aptoide.pt.data.cache.ManagerCache;
import cm.aptoide.pt.data.cache.ViewCache;
import cm.aptoide.pt.data.database.ManagerDatabase;
import cm.aptoide.pt.data.display.ViewDisplayAppVersionsInfo;
import cm.aptoide.pt.data.display.ViewDisplayCategory;
import cm.aptoide.pt.data.display.ViewDisplayListApps;
import cm.aptoide.pt.data.display.ViewDisplayListRepos;
import cm.aptoide.pt.data.display.ViewDisplayListsDimensions;
import cm.aptoide.pt.data.downloads.EnumDownloadType;
import cm.aptoide.pt.data.downloads.ManagerDownloads;
import cm.aptoide.pt.data.downloads.ViewDownload;
import cm.aptoide.pt.data.downloads.ViewDownloadStatus;
import cm.aptoide.pt.data.listeners.ViewMyapp;
import cm.aptoide.pt.data.model.ViewApplication;
import cm.aptoide.pt.data.model.ViewRepository;
import cm.aptoide.pt.data.notifications.ManagerNotifications;
import cm.aptoide.pt.data.preferences.ManagerPreferences;
import cm.aptoide.pt.data.preferences.ViewSettings;
import cm.aptoide.pt.data.system.ManagerSystemSync;
import cm.aptoide.pt.data.system.ViewScreenDimensions;
import cm.aptoide.pt.data.util.Constants;
import cm.aptoide.pt.data.xml.EnumInfoType;
import cm.aptoide.pt.data.xml.ManagerXml;
import cm.aptoide.pt.data.xml.ViewLatestVersionInfo;
import cm.aptoide.pt.debug.AptoideLog;
import cm.aptoide.pt.debug.InterfaceAptoideLog;

/**
 * AptoideServiceData, Aptoide's data I/O manager for the activity classes
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class AptoideServiceData extends Service implements InterfaceAptoideLog {

	private final String TAG = "Aptoide-ServiceData";
	private boolean isRunning = false;
	
	private ViewDisplayListsDimensions displayListsDimensions;
	
	private ArrayList<Integer> reposInserting;
	private ArrayList<ViewMyapp> waitingMyapps;
	private ViewDisplayListRepos waitingMyappRepos;
	private ViewLatestVersionInfo waitingSelfUpdate;
	
	private AIDLSelfUpdate selfUpdateClient = null;
	private HashMap<EnumServiceDataCallback, AIDLAptoideInterface> aptoideClients;
	private HashMap<Integer, AIDLAppInfo> appInfoClients;
	private AIDLReposInfo reposInfoClient = null;
//	private AIDLSearch searchClient = null;	//TODO implement sort blocking in search when loading repo from bare

	private ManagerPreferences managerPreferences;
	private ManagerSystemSync managerSystemSync;
	private ManagerDatabase managerDatabase;
	private ManagerDownloads managerDownloads;
	private ManagerNotifications managerNotifications;
	private ManagerXml managerXml;
	
	private ExecutorService cachedThreadPool;		//TODO in the future turn this into a priorityThreadPool, with a highest priority thread able to pause execution of other threads
	private ExecutorService scheduledThreadPool;
    
	private AtomicBoolean syncingInstalledApps;
    
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
				Log.w("Aptoide-ServiceData", "Unexpected serviceData exception", e);
				throw e;
			}
		}

		@Override
		public void callRegisterSelfUpdateObserver(AIDLSelfUpdate selfUpdateClient) throws RemoteException {
			registerSelfUpdateObserver(selfUpdateClient);
		}

		@Override
		public void callAcceptSelfUpdate() throws RemoteException {
			downloadSelfUpdate(waitingSelfUpdate);
			waitingSelfUpdate = null;
		}

		@Override
		public void callRejectSelfUpdate() throws RemoteException {
			rejectSelfUpdate();
		}

		@Override
		public void callSyncInstalledApps() throws RemoteException {
	    	syncInstalledApps();			
		}

		@Override
		public String callGetAptoideVersionName() throws RemoteException {
			return managerSystemSync.getAptoideVersionNameInUse();
		}
		
		@Override
		public void callStoreScreenDimensions(ViewScreenDimensions screenDimensions) throws RemoteException {
			displayListsDimensions = new ViewDisplayListsDimensions(screenDimensions);
			Log.d("Aptoide-ServiceData","displayListsDimensions"+displayListsDimensions);
			storeScreenDimensions(screenDimensions);	
		}

		@Override
		public ViewDisplayListsDimensions callGetDisplayListsDimensions() throws RemoteException {
			return displayListsDimensions;
		}

		@Override
		public void callRegisterAvailableAppsObserver(AIDLAptoideInterface availableAppsObserver) throws RemoteException {
			registerAvailableDataObserver(availableAppsObserver);
		}
		
		@Override
		public void callRegisterInstalledAppsObserver(AIDLAptoideInterface installedAppsObserver) throws RemoteException {
			registerInstalledDataObserver(installedAppsObserver);
		}
		
		@Override
		public ViewDisplayListApps callGetInstalledApps() throws RemoteException {
			return getInstalledApps();
		}

		@Override
		public void callRegisterReposObserver(AIDLReposInfo reposInfoObserver) throws RemoteException {
			registerReposObserver(reposInfoObserver);
		}

		@Override
		public void callAddRepo(ViewRepository repository) throws RemoteException {
//			if(repoIsManaged(repository.getHashid())){
//				//TDO check for delta
//				updateAvailableLists();
//			}else{
				addRepoBare(repository);	//TODO serialize requests (handle one at a time)			
//			}
		}

		@Override
		public void callRemoveRepo(int repoHashid) throws RemoteException {
			removeRepo(repoHashid);
		}

		@Override
		public void callSetInUseRepo(int repoHashid) throws RemoteException {
			toggleInUseRepo(repoHashid, true);
		}

		@Override
		public void callUnsetInUseRepo(int repoHashid) throws RemoteException {
			toggleInUseRepo(repoHashid, false);
		}

		@Override
		public void callRemoveLogin(int repoHashid) throws RemoteException {
			removeLogin(repoHashid);
		}

		@Override
		public void callUpdateLogin(ViewRepository repo) throws RemoteException {
			updateLogin(repo);
		}

		@Override
		public ViewDisplayListRepos callGetRepos() throws RemoteException {
			return getRepos();
		}

		@Override
		public void callNoRepos() throws RemoteException {
			noAvailableListData();
		}

		@Override
		public void callLoadingRepos() throws RemoteException {
			loadingAvailableListData();
		}
		

		@Override
		public boolean callAreListsByCategory() throws RemoteException {
			return getShowApplicationsByCategory();
		}

		@Override
		public void callSetListsBy(boolean byCategory) throws RemoteException {
			setShowApplicationsByCategory(byCategory);
		}

		@Override
		public ViewDisplayCategory callGetCategories() throws RemoteException {
			return getCategories();
		}

		@Override
		public int callGetAppsSortingPolicy() throws RemoteException {
			return getAppsSortingPolicy();
		}

		@Override
		public void callSetAppsSortingPolicy(int sortingPolicy) throws RemoteException {
			setAppsSortingPolicy(sortingPolicy);
		}

		@Override
		public ViewDisplayListApps callGetAvailableAppsByCategory(int offset, int range, int categoryHashid) throws RemoteException {
			return getAvailableApps(offset, range, categoryHashid);
		}

		@Override
		public ViewDisplayListApps callGetAvailableApps(int offset, int range) throws RemoteException {
			return getAvailableApps(offset, range);
		}

		@Override
		public ViewDisplayListApps callGetUpdatableApps() throws RemoteException {
			return getUpdatableApps();
		}

		@Override
		public ViewDisplayListApps callGetAppSearchResults(String searchString) throws RemoteException {
			return getAppSearchResults(searchString);
		}

		@Override
		public void callRegisterAppInfoObserver(AIDLAppInfo appInfoObserver, int appHashid) throws RemoteException {
			registerAppInfoObserver(appInfoObserver, appHashid);
		}

		@Override
		public void CallFillAppInfo(int appHashid) throws RemoteException {
			fillAppInfo(appHashid);
			
		}

		@Override
		public ViewDisplayAppVersionsInfo callGetAppInfo(int appHashid) throws RemoteException {
			return getAppInfo(appHashid);
		}

		@Override
		public void callScheduleInstallApp(int appHashid) throws RemoteException {
			scheduleInstallApp(appHashid);
		}

		@Override
		public void callUnscheduleInstallApp(int appHashid) throws RemoteException {
			unscheduleInstallApp(appHashid);			
		}

		@Override
		public boolean callIsAppScheduledToInstall(int appHashid) throws RemoteException {
			return isAppScheduledToInstall(appHashid);
		}

		@Override
		public void callInstallApp(int appHashid) throws RemoteException {
			downloadApp(appHashid);
		}

		@Override
		public void callUninstallApp(int appHashid) throws RemoteException {
			uninstallApp(appHashid);
		}

		@Override
		public void callRegisterMyappReceiver(AIDLAptoideInterface myappObserver) throws RemoteException {
			registerMyappReceiver(myappObserver);
		}

		@Override
		public void callReceiveMyapp(String uriString) throws RemoteException {
			receiveMyapp(uriString);
		}

		@Override
		public ViewMyapp callGetWaitingMyapp() throws RemoteException {
			if(!waitingMyapps.isEmpty()){
				return waitingMyapps.remove(0);
			}else{
				return null;
			}
		}

		@Override
		public void callInstallMyapp(ViewMyapp myapp) throws RemoteException {
			downloadMyapp(myapp);
			manageMyappRepos();
		}

		@Override
		public void callRejectedMyapp() throws RemoteException {
			manageMyappRepos();
		}

		@Override
		public ViewDisplayListRepos callGetWaitingMyappRepos() throws RemoteException {
			return waitingMyappRepos;
		}

		@Override
		public ViewSettings callGetSettings() throws RemoteException {
			return managerPreferences.getSettings();
		}

		@Override
		public void callSetHwFilter(boolean on) throws RemoteException {
			setHwFilter(on);
		}

		@Override
		public void callResetAvailableApps() throws RemoteException {
			resetAvailableLists();
		}
		
	}; 
	
	public void registerSelfUpdateObserver(AIDLSelfUpdate selfUpdateClient){
		this.selfUpdateClient = selfUpdateClient;
	}

	public void registerAvailableDataObserver(AIDLAptoideInterface availableAppsObserver){
		aptoideClients.put(EnumServiceDataCallback.UPDATE_AVAILABLE_LIST, availableAppsObserver);
		checkIfAnyReposInUse();
    	AptoideLog.d(AptoideServiceData.this, "Registered Available Data Observer");
	}
	
	public void registerInstalledDataObserver(AIDLAptoideInterface installedAppsObserver){
		aptoideClients.put(EnumServiceDataCallback.UPDATE_INSTALLED_LIST, installedAppsObserver);
		if(!syncingInstalledApps.get()){
			try {
				installedAppsObserver.newInstalledListDataAvailable();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    	AptoideLog.d(AptoideServiceData.this, "Registered Installed Data Observer");
	}
	
	public void registerMyappReceiver(AIDLAptoideInterface myappObserver){
		aptoideClients.put(EnumServiceDataCallback.HANDLE_MYAPP, myappObserver);
    	AptoideLog.d(AptoideServiceData.this, "Registered Myapp Observer");		
	}
	
	
	public void registerAppInfoObserver(AIDLAppInfo appInfoObserver, int appHashid){
		appInfoClients.put(appHashid, appInfoObserver);
    	AptoideLog.d(AptoideServiceData.this, "Registered App Info Observer: "+appHashid);
	}
	
	
	public void registerReposObserver(AIDLReposInfo reposInfoObserver){
		reposInfoClient = reposInfoObserver;
	}
	
	
	private BroadcastReceiver installedAppsChangeListener = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context receivedContext, Intent receivedIntent) {
			if(receivedIntent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)){
				String packageName = receivedIntent.getData().getEncodedSchemeSpecificPart();
				Log.d("Aptoide-ServiceData", "installedAppsChangeListener - package added: "+packageName);
				addInstalledApp(packageName);
				
			}else if(receivedIntent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)){
				String packageName = receivedIntent.getData().getEncodedSchemeSpecificPart();
				Log.d("Aptoide-ServiceData", "installedAppsChangeListener - package removed: "+packageName);
				removeInstalledApp(packageName);
			}
		}
	};
		
	
	
	public ViewDisplayListsDimensions getDisplayListsDimensions(){
		return displayListsDimensions;
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
	    	
	    	reposInserting = new ArrayList<Integer>();
			
			waitingMyapps = new ArrayList<ViewMyapp>();
			waitingMyappRepos = new ViewDisplayListRepos(0);
			waitingSelfUpdate = null;
	    	
	    	cachedThreadPool = Executors.newCachedThreadPool();
	    	scheduledThreadPool = Executors.newScheduledThreadPool(Constants.MAX_PARALLEL_SERVICE_REQUESTS);
	    	
			aptoideClients = new HashMap<EnumServiceDataCallback, AIDLAptoideInterface>();
			appInfoClients = new HashMap<Integer, AIDLAppInfo>();
			
			managerPreferences = new ManagerPreferences(this);
			managerSystemSync = new ManagerSystemSync(this);
			managerDatabase = new ManagerDatabase(this);
			managerNotifications = new ManagerNotifications(this);
			managerDownloads = new ManagerDownloads(this);
			managerXml = new ManagerXml(this);
			
			syncingInstalledApps = new AtomicBoolean(false);
			
			IntentFilter installedAppsChangeFilter = new IntentFilter();
			installedAppsChangeFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
			installedAppsChangeFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
			installedAppsChangeFilter.addDataScheme(Constants.SCHEME_PACKAGE);
			registerReceiver(installedAppsChangeListener, installedAppsChangeFilter);
			
			checkForSelfUpdate();
			
//			Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
			
			isRunning = true;
			Log.d("Aptoide ServiceData", "Service started");

	    }
		super.onCreate();
	}


	@Override
	public void onDestroy() {	//TODO make sure to close all child threads
		managerNotifications.destroy();
		unregisterReceiver(installedAppsChangeListener);
		Toast.makeText(this, R.string.aptoide_stopped, Toast.LENGTH_LONG).show();
		stopSelf();
		Log.d("Aptoide ServiceData", "Service stopped");
		super.onDestroy();
	}

	
	public void checkForSelfUpdate(){	//TODO use NotificationManager class to load Splash Activity with it's progress bar as selfupdate activity
		cachedThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
				if(!managerDownloads.isConnectionAvailable()){
					AptoideLog.d(AptoideServiceData.this, "No connection");	//TODO raise exception to ask for what to do
				}
				if(!getManagerCache().isFreeSpaceInSdcard()){
					//TODO raise exception
				}
				Log.d("Aptoide-ServiceData", "Checking for self-update");
				ViewCache cache = managerDownloads.downloadLatestVersionInfo();
				managerXml.latestVersionInfoParse(cache);
				//TODO find some way to track global parsing completion status, probably in managerXml
			}
		});
	}
	
	public void parsingLatestVersionInfoFinished(final ViewLatestVersionInfo latestVersionInfo){
		cachedThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
				int currentVersion = managerSystemSync.getAptoideVersionInUse();
				if( currentVersion < latestVersionInfo.getVersionCode()){
					Log.d("Aptoide-ServiceData", "Using version "+currentVersion+", suggest update to "+latestVersionInfo.getVersionCode()+"!");
					waitingSelfUpdate = latestVersionInfo;
					handleSelfUpdate();
				}else{
					Log.d("Aptoide-ServiceData", "Using version "+currentVersion+", up to date!");
				}
			}
		});
	}
	
	public void rejectSelfUpdate(){
		cachedThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				if(selfUpdateClient != null){
					try {
						selfUpdateClient.cancelUpdateActivity();
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				selfUpdateClient = null;
				waitingSelfUpdate = null;
			}
		});
	}
	
	public void downloadSelfUpdate(final ViewLatestVersionInfo latesVersionInfo){
		scheduledThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				if(!managerDownloads.isConnectionAvailable()){
					AptoideLog.d(AptoideServiceData.this, "No connection");	//TODO raise exception to ask for what to do
				}
				if(!getManagerCache().isFreeSpaceInSdcard()){
					//TODO raise exception
				}
				
				String aptoide = getApplicationInfo().name;
				int aptoideHash = (aptoide+"|"+latesVersionInfo.getVersionCode()).hashCode();
				ViewDownload download = getManagerDownloads().prepareApkDownload( aptoideHash, aptoide
									, latesVersionInfo.getRemotePath(), latesVersionInfo.getSize(), latesVersionInfo.getMd5sum());
				ViewCache apk = managerDownloads.downloadApk(download);
				AptoideLog.d(AptoideServiceData.this, "installing from: "+apk.getLocalPath());	
				installApp(apk, aptoideHash);
				rejectSelfUpdate();
			}
		});		
	}
	
	public void checkIfAnyReposInUse(){
		cachedThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				if(managerDatabase.anyReposInUse()){
					resetAvailableLists();
					return;
				}else{
					manageRepos();  				
				}
			}
		});
	}
	
	public void storeScreenDimensions(final ViewScreenDimensions screenDimensions){
		cachedThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				managerPreferences.setScreenDimensions(screenDimensions);
				AptoideLog.d(AptoideServiceData.this, "Stored Screen Dimensions: "+managerPreferences.getScreenDimensions());
			}
		});
	}
	
	public void setHwFilter(final boolean on){
		cachedThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				managerPreferences.setHwFilter(on);
				AptoideLog.d(AptoideServiceData.this, "Setting hw filter: "+on);
			}
		});
	}
	
	public void syncInstalledApps(){
		syncingInstalledApps.set(true);
		cachedThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
				managerDatabase.insertInstalledApplications(managerSystemSync.getInstalledApps());
				AptoideLog.d(AptoideServiceData.this, "Sync'ed Installed Apps");
				
				syncingInstalledApps.set(false);
				managerSystemSync.cacheInstalledIcons();
			}
		});
	}	
	
	
	public void updateInstalledLists(){
		cachedThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				try {
					aptoideClients.get(EnumServiceDataCallback.UPDATE_INSTALLED_LIST).newInstalledListDataAvailable(); 
//					Looper.prepare();
//					Toast.makeText(getApplicationContext(), "installed list now available in next -> tab", Toast.LENGTH_LONG).show();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	
	public void refreshAvailableDisplay(){
		cachedThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				try {
					aptoideClients.get(EnumServiceDataCallback.UPDATE_AVAILABLE_LIST).refreshAvailableDisplay();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	
	public void updateAvailableLists(){
		cachedThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				try {
					aptoideClients.get(EnumServiceDataCallback.UPDATE_AVAILABLE_LIST).newAvailableListDataAvailable();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	
	public void resetAvailableLists(){
		cachedThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				try {
					aptoideClients.get(EnumServiceDataCallback.UPDATE_AVAILABLE_LIST).resetAvailableListData();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	
	public void noAvailableListData(){
		cachedThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				AptoideLog.d(AptoideServiceData.this, "No available apps!");
				try {
					aptoideClients.get(EnumServiceDataCallback.UPDATE_AVAILABLE_LIST).noAvailableListDataAvailable();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
		});
	}
	
	public void loadingAvailableListData(){
		cachedThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				AptoideLog.d(AptoideServiceData.this, "Loading available apps!");
				try {
					aptoideClients.get(EnumServiceDataCallback.UPDATE_AVAILABLE_LIST).loadingAvailableListDataAvailable();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
		});
	}
	
	public void updateReposLists(){
		cachedThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				try {
					reposInfoClient.updateReposBasicInfo();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
		});
	}
	
	public void insertedRepo(final int repoHashid){
		cachedThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				try {
					reposInfoClient.insertedRepo(repoHashid);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
		});
	}
	

	public ViewDisplayListRepos getRepos(){
		AptoideLog.d(AptoideServiceData.this, "Getting Repos");
		return managerDatabase.getReposDisplayInfo();
	}
	
	
	public boolean repoIsManaged(int repoHashid){
		return managerDatabase.isRepoManaged(repoHashid);
	}
	
	public void removeRepo(final int repoHashid){
		if(reposInserting.contains(repoHashid)){
			reposInserting.remove(Integer.valueOf(repoHashid));
		}
		cachedThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				AptoideLog.d(AptoideServiceData.this, "Removing repo: "+repoHashid);
				managerDatabase.removeRepository(repoHashid);
				resetAvailableLists();				
			}
		});
	}
	
	public void toggleInUseRepo(final int repoHashid, final boolean inUse){
		cachedThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				AptoideLog.d(AptoideServiceData.this, "Setting repo: "+repoHashid+" inUse: "+inUse);
				managerDatabase.toggleRepositoryInUse(repoHashid, inUse);
				resetAvailableLists();
				if(inUse){
					getDelta(repoHashid);
				}
			}
		});
	}
	
	public void removeLogin(final int repoHashid){
		cachedThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				AptoideLog.d(AptoideServiceData.this, "Setting repo to private: "+repoHashid);
				managerDatabase.removeLogin(repoHashid);
			}
		});
	}
	
	public void updateLogin(final ViewRepository repo){
		cachedThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				AptoideLog.d(AptoideServiceData.this, "updating repo's login: "+repo);
				managerDatabase.updateLogin(repo);
			}
		});
	}
	
	public void getDelta(final int repoHashid){
		cachedThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				ViewRepository repository = managerDatabase.getRepoIfUpdateNeeded(repoHashid);
				AptoideLog.d(AptoideServiceData.this, "updating repo: "+repository);
				if(repository != null){
					reposInserting.add(repoHashid);
					if(!managerDownloads.isConnectionAvailable()){
						AptoideLog.d(AptoideServiceData.this, "No connection");	//TODO raise exception to ask for what to do
					}
					if(!getManagerCache().isFreeSpaceInSdcard()){
						//TODO raise exception
					}
					ViewCache cache = null;
					if(reposInserting.contains(repoHashid)){
						cache = managerDownloads.startRepoDeltaDownload(repository);
					}
//					Looper.prepare();
//					Toast.makeText(getApplicationContext(), "finished downloading bare list", Toast.LENGTH_LONG).show();
					if(reposInserting.contains(repoHashid)){
						managerXml.repoDeltaParse(repository, cache);
					}
				}
			}
		});
	}
		
	public void parsingRepoDeltaFinished(ViewRepository repository){
		if(reposInserting.contains(repository.getHashid())){
			reposInserting.remove(Integer.valueOf(repository.getHashid()));
			resetAvailableLists();
			insertedRepo(repository.getHashid());
		}
	}
		
	
	public void addRepoBare(final ViewRepository originalRepository){
		disallowSortingPolicyChange();
		reposInserting.add(originalRepository.getHashid());
		cachedThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				ViewRepository repository = originalRepository;
				Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
				if(!managerDownloads.isConnectionAvailable()){
					AptoideLog.d(AptoideServiceData.this, "No connection");	//TODO raise exception to ask for what to do
				}
				if(!getManagerCache().isFreeSpaceInSdcard()){
					//TODO raise exception
				}
				ViewCache cache = null;
				if(reposInserting.contains(repository.getHashid())){
					cache = managerDownloads.startRepoBareDownload(repository);
				}
//				Looper.prepare();
//				Toast.makeText(getApplicationContext(), "finished downloading bare list", Toast.LENGTH_LONG).show();
				if(reposInserting.contains(repository.getHashid())){
					managerXml.repoBareParse(repository, cache);
				}
				//TODO find some way to track global parsing completion status, probably in managerXml
			}
		});
		
	}
	
	public void parsingRepoBareFinished(ViewRepository repository){
		if(reposInserting.size() == 1){	// If contains only the currently being parsed repo
			allowSortingPolicyChange();
		}
		if(reposInserting.contains(repository.getHashid())){
			if(managerPreferences.getShowApplicationsByCategory() || repository.getSize() < Constants.APPLICATIONS_IN_EACH_INSERT/2){
				resetAvailableLists();
			}
			insertedRepo(repository.getHashid());
			addRepoIconsInfo(repository);
		}
	}
	
	public void addRepoIconsInfo(final ViewRepository repository){
		cachedThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				Thread.currentThread().setPriority(Thread.NORM_PRIORITY);
				if(!managerDownloads.isConnectionAvailable()){
					AptoideLog.d(AptoideServiceData.this, "No connection");	//TODO raise exception to ask for what to do
				}
				if(!getManagerCache().isFreeSpaceInSdcard()){
					//TODO raise exception
				}
				ViewCache cache = null;
				if(reposInserting.contains(repository.getHashid())){
					cache = managerDownloads.startRepoIconDownload(repository);
				}
				if(reposInserting.contains(repository.getHashid())){
					managerXml.repoIconParse(repository, cache);
					//TODO find some way to track global parsing completion status, probably in managerXml
				}
			}
		});
	}
	
	public void parsingRepoIconsFinished(ViewRepository repository){
		if(reposInserting.contains(repository.getHashid())){
			getRepoIcons(new ViewDownloadStatus(repository, Constants.FIRST_ELEMENT, EnumDownloadType.ICON));
			addRepoStats(repository);
		}
	}
	
	public void getRepoIcons(final ViewDownloadStatus downloadStatus){
		
		if(downloadStatus.getRepository().getSize() < downloadStatus.getOffset()){
//			refreshAvailableDisplay();
			return;
		}else{
//			if(downloadStatus.getOffset() >  Constants.FIRST_ELEMENT){
//				refreshAvailableDisplay();
//			}
			
			scheduledThreadPool.execute(new Runnable() {
				@Override
				public void run() {
					Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
					if(!managerDownloads.isConnectionAvailable()){
						AptoideLog.d(AptoideServiceData.this, "No connection");	//TODO raise exception to ask for what to do
					}
					if(!getManagerCache().isFreeSpaceInSdcard()){
						//TODO raise exception
						return;
					}

					managerDownloads.getRepoIcons(downloadStatus, managerDatabase.getIconsDownloadInfo(downloadStatus.getRepository(), downloadStatus.getOffset(), displayListsDimensions.getCacheSize()));
					//TODO find some way to track global parsing completion status, probably in managerXml
				}
			});
		}
		
	}
	
	public void addRepoStats(final ViewRepository repository){
		scheduledThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
				if(!managerDownloads.isConnectionAvailable()){
					AptoideLog.d(AptoideServiceData.this, "No connection");	//TODO raise exception to ask for what to do
				}
				if(!getManagerCache().isFreeSpaceInSdcard()){
					//TODO raise exception
				}
				ViewCache cache = null;
				if(reposInserting.contains(repository.getHashid())){
					cache = managerDownloads.startRepoDownload(repository, EnumInfoType.STATS);
				}
				if(reposInserting.contains(repository.getHashid())){	
					managerXml.repoStatsParse(repository, cache);
					//TODO find some way to track global parsing completion status, probably in managerXml
				}
			}
		});
	}
	
	public void parsingRepoStatsFinished(ViewRepository repository){
		if(reposInserting.contains(repository.getHashid())){
			reposInserting.remove(Integer.valueOf(repository.getHashid()));
			updateAvailableLists();
//			Toast.makeText(AptoideServiceData.this, "app stats available", Toast.LENGTH_LONG).show();
		}
	}
	
	
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
	
	
	public void fillAppInfo(final int appHashid){
		final ViewRepository repository = managerDatabase.getAppRepo(appHashid);
		if(repository == null){
			return;
		}
		if(!managerDownloads.isIconCached(appHashid)){
			scheduledThreadPool.execute(new Runnable() {
				@Override
				public void run() {
					managerDownloads.getIcon(managerDatabase.getIconDownloadInfo(repository, appHashid), repository.isLoginRequired(), repository.getLogin());
					updateAppInfo(appHashid, EnumServiceDataCallback.REFRESH_ICON);
				}
			});
		}
		addRepoAppDownloadInfo(repository, appHashid);
		addRepoAppExtras(repository, appHashid);
		
		addRepoAppStats(repository, appHashid);
		//TODO parallel get Comments
	}
	
	
	public void addRepoAppDownloadInfo(final ViewRepository repository, final int appHashid){
	
		scheduledThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				//TODO check with database if info is present and only if not continue with the download and parse of this info
				Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
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
		});
					
	}
	
	public void parsingRepoAppDownloadInfoFinished(ViewRepository repository, int appHashid){
		updateAppInfo(appHashid, EnumServiceDataCallback.UPDATE_APP_DOWNLOAD_INFO);
	}
	
	
	public void addRepoAppStats(final ViewRepository repository, final int appHashid){

		scheduledThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
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
		});
					
	}
	
	public void parsingRepoAppStatsFinished(ViewRepository repository, int appHashid){
		updateAppInfo(appHashid, EnumServiceDataCallback.UPDATE_APP_STATS);
	}
	
	
	
	public void addRepoAppExtras(final ViewRepository repository, final int appHashid){
		
		scheduledThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				//TODO check with database if info is present and only if not continue with the download and parse of this info
				Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
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
		});
					
	}
	
	public void parsingRepoAppExtrasFinished(ViewRepository repository, int appHashid){
		updateAppInfo(appHashid, EnumServiceDataCallback.UPDATE_APP_EXTRAS);
		getAppScreens(repository, appHashid);
	}
	
	public void getAppScreens(final ViewRepository repository,final int appHashid){
		
			scheduledThreadPool.execute(new Runnable() {
				@Override
				public void run() {
					Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
					if(!managerDownloads.isConnectionAvailable()){
						AptoideLog.d(AptoideServiceData.this, "No connection");	//TODO raise exception to ask for what to do
					}
					if(!getManagerCache().isFreeSpaceInSdcard()){
						//TODO raise exception
						return;
					}

					managerDownloads.getAppScreens(repository, managerDatabase.getScreensDownloadInfo(repository, appHashid));
					//TODO find some way to track global parsing completion status, probably in managerXml
				}
			});
	}
	
	public void gettingAppScreensFinished(int appHashid){
		AptoideLog.d(AptoideServiceData.this, "Finished getting screens - appHashid: "+appHashid);
		updateAppInfo(appHashid, EnumServiceDataCallback.REFRESH_SCREENS);
	}
	
	public void updateAppInfo(int appHashid, EnumServiceDataCallback callBack){
		try {
			switch (callBack) {
				case REFRESH_ICON:
					appInfoClients.get(appHashid).refreshIcon();
					break;
					
				case UPDATE_APP_DOWNLOAD_INFO:
					appInfoClients.get(appHashid).newAppDownloadInfoAvailable();
					break;
					
				case UPDATE_APP_STATS:
					appInfoClients.get(appHashid).newStatsInfoAvailable();
					break;
					
				case UPDATE_APP_EXTRAS:
					appInfoClients.get(appHashid).newExtrasAvailable();
					break;
					
				case REFRESH_SCREENS:
					appInfoClients.get(appHashid).refreshScreens();
					break;
					
				default:
					break;
			}
			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public ViewDisplayAppVersionsInfo getAppInfo(int appHashid){
		AptoideLog.d(AptoideServiceData.this, "Getting App Versions Info: "+appHashid);
		return managerDatabase.getAppDisplayInfo(appHashid);
	}
	
	public boolean getShowApplicationsByCategory(){
		return managerPreferences.getShowApplicationsByCategory();
	}
	
	public void setShowApplicationsByCategory(final boolean byCategory){
		scheduledThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				managerPreferences.setShowApplicationsByCategory(byCategory);
			}
		});
	}
	
	public ViewDisplayCategory getCategories(){
		AptoideLog.d(AptoideServiceData.this, "Getting Categories");
		return managerDatabase.getCategoriesDisplayInfo();
	}
	
	public int getAppsSortingPolicy(){
		return managerPreferences.getAppsSortingPolicy();
	}
	
	public void allowSortingPolicyChange(){
		scheduledThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				try {
					aptoideClients.get(EnumServiceDataCallback.UPDATE_AVAILABLE_LIST).allowSortingPolicyChange();
//					searchClients //TODO implement sort blocking in search when loading repo from bare
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	
	public void disallowSortingPolicyChange(){
		scheduledThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				try {
					aptoideClients.get(EnumServiceDataCallback.UPDATE_AVAILABLE_LIST).disallowSortingPolicyChange();
//					searchClients //TODO implement sort blocking in search when loading repo from bare
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	
	public void setAppsSortingPolicy(final int sortingPolicy){
		scheduledThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				managerPreferences.setAppsSortingPolicy(sortingPolicy);
				resetAvailableLists();
			}
		});
	}
	
	
	public ViewDisplayListApps getInstalledApps(){
		AptoideLog.d(AptoideServiceData.this, "Getting Installed Apps");
		return managerDatabase.getInstalledAppsDisplayInfo();
	}
	
	public ViewDisplayListApps getAvailableApps(int offset, int range){
		AptoideLog.d(AptoideServiceData.this, "Getting Available Apps");
		return managerDatabase.getAvailableAppsDisplayInfo(offset, range, managerPreferences.isHwFilterOn(), EnumAppsSorting.reverseOrdinal(managerPreferences.getAppsSortingPolicy()));
	}
	
	public ViewDisplayListApps getAvailableApps(int offset, int range, int categoryHashid){
		AptoideLog.d(AptoideServiceData.this, "Getting Available Apps for category: "+categoryHashid);
		return managerDatabase.getAvailableAppsDisplayInfo(offset, range, categoryHashid, managerPreferences.isHwFilterOn(), EnumAppsSorting.reverseOrdinal(managerPreferences.getAppsSortingPolicy()));
	}
	
	public ViewDisplayListApps getUpdatableApps(){
		AptoideLog.d(AptoideServiceData.this, "Getting Updatable Apps");
		return managerDatabase.getUpdatableAppsDisplayInfo(managerPreferences.isHwFilterOn(), EnumAppsSorting.reverseOrdinal(managerPreferences.getAppsSortingPolicy()));
	}
	
	public ViewDisplayListApps getAppSearchResults(String searchString){
		AptoideLog.d(AptoideServiceData.this, "Getting App Search Results: "+searchString);
		return managerDatabase.getAppSearchResultsDisplayInfo(searchString, EnumAppsSorting.reverseOrdinal(managerPreferences.getAppsSortingPolicy()));
	}
	
	
	public void scheduleInstallApp(final int appHashid){
		scheduledThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				managerDatabase.insertApplicationToInstall(appHashid);
			}
		});
	}
	
	public void unscheduleInstallApp(final int appHashid){
		scheduledThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				managerDatabase.removeApplicationToInstall(appHashid);
			}
		});
	}
	
	public boolean isAppScheduledToInstall(final int appHashid){
		return managerDatabase.isApplicationScheduledToInstall(appHashid);
	}
	
	public void receiveMyapp(final String uriString){
		scheduledThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				AptoideLog.d(AptoideServiceData.this, "Receiving Myapp file: "+uriString);
				if(!managerDownloads.isConnectionAvailable()){
					AptoideLog.d(AptoideServiceData.this, "No connection");	//TODO raise exception to ask for what to do
				}
				if(!getManagerCache().isFreeSpaceInSdcard()){
					//TODO raise exception
				}
//				launchAptoide();
				String[] slashSplitUriString = uriString.split("/");
				String myappName = slashSplitUriString[slashSplitUriString.length-1];
				ViewCache cache;
				if(uriString.startsWith(Constants.SCHEME_FILE_PREFIX)){
					cache = managerDownloads.getManagerCache().cacheMyapp(uriString.substring(Constants.SCHEME_FILE_PREFIX.length()), myappName);
				}else{
					AptoideLog.d(AptoideServiceData.this, "Preparing download of Myapp file: "+myappName);
					cache = managerDownloads.downloadMyapp(uriString, myappName);
				}
				AptoideLog.d(AptoideServiceData.this, "Preparing parsing of Myapp file: "+cache.getLocalPath());
				
				managerXml.myappParse(cache, myappName);
				
			}
		});
	}
	
	public void parsingMyappFinished(ViewMyapp myapp, ViewDisplayListRepos newRepos){
		ViewDisplayListRepos notManagedRepos = managerDatabase.excludeManagedRepos(newRepos);
		if(!notManagedRepos.isEmpty()){
			waitingMyappRepos.addAll(notManagedRepos);
		}
		if(myapp != null){
			if(!managerDatabase.isApplicationInstalled(myapp.getPackageName())){
				waitingMyapps.add(myapp);
				scheduledThreadPool.execute(new Runnable() {
					@Override
					public void run() {
						try {
							aptoideClients.get(EnumServiceDataCallback.HANDLE_MYAPP).handleMyapp();
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}	
					}
				});
			}else{
				Toast.makeText(this, "Application "+myapp.getName()+" already installed!", Toast.LENGTH_LONG).show();
				manageMyappRepos();
			}
		}
	}
	
	public void downloadMyapp(final ViewMyapp myapp){
		scheduledThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				if(!managerDownloads.isConnectionAvailable()){
					AptoideLog.d(AptoideServiceData.this, "No connection");	//TODO raise exception to ask for what to do
				}
				if(!getManagerCache().isFreeSpaceInSdcard()){
					//TODO raise exception
				}
				ViewDownload download = getManagerDownloads().prepareApkDownload(myapp.hashCode(), myapp.getName()
										, myapp.getRemotePath(), myapp.getSize(), myapp.getMd5sum());
				ViewCache apk = managerDownloads.downloadApk(download);
				AptoideLog.d(AptoideServiceData.this, "installing from: "+apk.getLocalPath());	
				installApp(apk, myapp.hashCode());
			}
		});		
	}
	
	public void manageMyappRepos(){
		if(!waitingMyappRepos.isEmpty()){
			manageRepos(true);
		}
	}

	
	public void downloadApp(final int appHashid){
		scheduledThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				if(!managerDownloads.isConnectionAvailable()){
					AptoideLog.d(AptoideServiceData.this, "No connection");	//TODO raise exception to ask for what to do
				}
				if(!getManagerCache().isFreeSpaceInSdcard()){
					//TODO raise exception
				}
				ViewCache apk = managerDownloads.downloadApk(managerDatabase.getAppDownload(appHashid));
				AptoideLog.d(AptoideServiceData.this, "installing from: "+apk.getLocalPath());	
				installApp(apk, appHashid);
			}
		});
	}
	
	
	public void addInstalledApp(final String packageName){
		scheduledThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				ViewApplication installedApp = managerSystemSync.getInstalledApp(packageName);
				if(installedApp != null){
					managerDatabase.insertInstalledApplication(installedApp);
				}
			}
		});
	}
	
	public void removeInstalledApp(final String packageName){
		scheduledThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				managerDatabase.removeInstalledApplication(packageName);
			}
		});
	}
	
	public void installApp(ViewCache apk, int appHashid){
		if(isAppScheduledToInstall(appHashid)){
			unscheduleInstallApp(appHashid);
		}
		Intent install = new Intent(Intent.ACTION_VIEW);
		install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		install.setDataAndType(Uri.fromFile(apk.getFile()),"application/vnd.android.package-archive");
		AptoideLog.d(AptoideServiceData.this, "Installing app: "+appHashid);
		startActivity(install);
	}
	
	public void uninstallApp(int appHashid){
		Uri uri = Uri.fromParts("package", managerDatabase.getInstalledAppPackageName(appHashid), null);
		Intent remove = new Intent(Intent.ACTION_DELETE, uri);
		remove.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		AptoideLog.d(AptoideServiceData.this, "Removing app: "+appHashid);
		startActivity(remove);
	}
	
	
	public void splash(){
		Intent splash = new Intent(AptoideServiceData.this, Splash.class);
		splash.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(splash);    				
	}
	
	public void handleSelfUpdate(){
		Intent selfUpdate = new Intent(AptoideServiceData.this, SelfUpdate.class);
		selfUpdate.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(selfUpdate);    				
	}
	
	public void manageRepos(boolean myappReposWaiting){
		Intent manageRepos = new Intent(AptoideServiceData.this, ManageRepos.class);
		manageRepos.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
		if(myappReposWaiting){
			manageRepos.putExtra(Constants.MYAPP_NEW_REPOS_WAITING, true);
		}
		startActivity(manageRepos);  		
	}
	
	public void manageRepos(){
		manageRepos(false);
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
