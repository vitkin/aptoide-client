/**
 * ManagerDownloads,		auxilliary class to Aptoide's ServiceData
 * Copyright (C) 2011  Duarte Silveira
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


package cm.aptoide.pt.data.webservices;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;
import cm.aptoide.pt.EnumAppsSorting;
import cm.aptoide.pt.R;
import cm.aptoide.pt.data.AptoideServiceData;
import cm.aptoide.pt.data.ViewClientStatistics;
import cm.aptoide.pt.data.cache.ManagerCache;
import cm.aptoide.pt.data.cache.ViewCache;
import cm.aptoide.pt.data.model.ViewAppDownloadInfo;
import cm.aptoide.pt.data.model.ViewLogin;
import cm.aptoide.pt.data.model.ViewRepository;
import cm.aptoide.pt.data.notifications.EnumNotificationTypes;
import cm.aptoide.pt.data.notifications.ViewNotification;
import cm.aptoide.pt.data.util.Constants;
import cm.aptoide.pt.data.xml.EnumInfoType;
import cm.aptoide.pt.debug.exceptions.AptoideExceptionDownload;
import cm.aptoide.pt.debug.exceptions.AptoideExceptionNotFound;

/**
 * ManagerDownloads, centralizes all download processes
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class ManagerDownloads {
	
	private AptoideServiceData serviceData;
	private ManagerCache managerCache;
	private ConnectivityManager connectivityState;
	
	/** Ongoing */
//	private HashMap<Integer, ViewDownload> downloads;
	
	/** Waiting **/
	private IconsDownloadManager iconsDownloadManager;
	private ScreensDownloadManager screensDownloadManager;
//	private apksDownloadManager apksDownloadManager;
	
	/** Object reuse pool */
	private ArrayList<ViewDownload> downloadPool;

//	private final static int KBYTES_TO_BYTES = 1024;					// moved to constants.xml
//	private HashMap<Integer, HashMap<String, String>> notifications;	//TODO move to notifications within ServiceData
//	private NotificationManager notificationManager;					//TODO move to notifications within ServiceData
//	private Context context;											//TODO deprecate
//	private WakeLock keepScreenOn;										//moved to ServiceData
	
    private class IconsDownloadManager{
    	private ExecutorService iconGettersPool;
    	private AtomicInteger iconsDownloadedCounter;
    	
    	public IconsDownloadManager(){
    		iconGettersPool = Executors.newFixedThreadPool(Constants.MAX_PARALLEL_DOWNLOADS);
    		iconsDownloadedCounter = new AtomicInteger(0);
    	}
    	
    	public void executeDownload(ViewDownload downloadInfo){
    		iconGettersPool.execute(new GetIcon(downloadInfo));
        }
    	
    	private class GetIcon implements Runnable{

    		private ViewDownload iconDownload;
    		
			public GetIcon(ViewDownload iconDownloadInfo) {
				this.iconDownload = iconDownloadInfo;
			}

			@Override
			public void run() {
//				downloads.put(download.getNotification().getNotificationHashid(), download);
				Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
				
				try {
					download(iconDownload, false);
				} catch (Exception e) {
					try {
						download(iconDownload, false);
					} catch (Exception e2) { }
				}	
				
				recicleViewDownload(iconDownload);
				if(iconsDownloadedCounter.incrementAndGet() >= Constants.ICONS_REFRESH_INTERVAL){
					iconsDownloadedCounter.set(0);
					serviceData.refreshAvailableDisplay();
				}
			}
    		
    	}
    }
    
    private class ScreensDownloadManager{
    	private ExecutorService screenGettersPool;
    	private AtomicInteger screensDownloadedCounter;
    	
    	public ScreensDownloadManager(){
    		screenGettersPool = Executors.newFixedThreadPool(Constants.MAX_PARALLEL_DOWNLOADS);
    		screensDownloadedCounter = new AtomicInteger(0);
    	}
    	
    	public void executeDownload(ViewDownload downloadInfo, int screensNumber, int orderNumber){
    		screenGettersPool.execute(new GetScreen(downloadInfo, screensNumber, orderNumber));
        }
    	
    	private class GetScreen implements Runnable{

    		private ViewDownload screenDownload;
        	private int screensNumber;
        	private int orderNumber;
    		
			public GetScreen(ViewDownload screenDownload, int screensNumber, int orderNumber) {
				this.screenDownload = screenDownload;
	    		this.screensNumber = screensNumber;
	    		this.orderNumber = orderNumber;
			}

			@Override
			public void run() {
//				downloads.put(download.getNotification().getNotificationHashid(), download);
				Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

				try {
					download(screenDownload, false);
				} catch (Exception e) {
					try {
						download(screenDownload, false);
					} catch (Exception e2) { }
				}				
				
				if(orderNumber >= screensNumber){
					serviceData.gettingAppScreensFinished(screenDownload.getNotification().getTargetsHashid());
				}
				recicleViewDownload(screenDownload);
			}
    		
    	}
    }
	
	
	public ManagerCache getManagerCache(){
		return this.managerCache;
	}
	
	private ViewClientStatistics getClientStatistics(){
		return serviceData.getStatistics();
	}
	
	private String getServerUsername(){
		return serviceData.getManagerPreferences().getServerLogin().getUsername();
	}
	
	private String getUserAgentString(){
		ViewClientStatistics clientStatistics = getClientStatistics();
		return String.format(Constants.USER_AGENT_FORMAT
				, clientStatistics.getAptoideVersionNameInUse(), clientStatistics.getScreenDimensions().getFormattedString()
				, clientStatistics.getAptoideClientUUID(), getServerUsername());
	}
	
	
	public ManagerDownloads(AptoideServiceData serviceData) {
		this.serviceData = serviceData;
		managerCache = new ManagerCache();
		
		connectivityState = (ConnectivityManager)serviceData.getSystemService(Context.CONNECTIVITY_SERVICE);

		iconsDownloadManager = new IconsDownloadManager();
		screensDownloadManager = new ScreensDownloadManager();
		
		this.downloadPool = new ArrayList<ViewDownload>(Constants.MAX_PARALLEL_DOWNLOADS);
		
		Log.d("Aptoide","******* \n Downloads will be made to: " + Constants.PATH_CACHE + "\n ********");
	}
		
	

	//TODO refactor all this to reduce data redundancy and memory waste
	public synchronized ViewDownload getNewViewDownload(String remotePath, ViewCache cache, ViewNotification notification){
		ViewDownload download;
		if(downloadPool.isEmpty()){
			download = new ViewDownload(remotePath, cache, notification);
		}else{
			ViewDownload viewDownload = downloadPool.remove(Constants.FIRST_ELEMENT);
			viewDownload.reuse(remotePath, cache, notification);
			download = viewDownload;
		}
//		downloads.put(notification.getNotificationHashid(), download);	//TODO check for concurrency issues
		return download;
	}
	
	public synchronized ViewDownload getNewViewDownload(String remotePath, ViewLogin login, ViewCache cache, ViewNotification notification){
		ViewDownload download;
		if(downloadPool.isEmpty()){
			download = new ViewDownload(remotePath, login, cache, notification);
		}else{
			ViewDownload viewDownload = downloadPool.remove(Constants.FIRST_ELEMENT);
			viewDownload.reuse(remotePath, login, cache, notification);
			download = viewDownload;
		}
//		downloads.put(notification.getNotificationHashid(), download);	//TODO check for concurrency issues
		return download;
	}
	
	public synchronized ViewDownload getNewViewDownload(String remotePath, int size, ViewLogin login, ViewCache cache, ViewNotification notification){
		ViewDownload download;
		if(downloadPool.isEmpty()){
			download = new ViewDownload(remotePath, size, login, cache, notification);
		}else{
			ViewDownload viewDownload = downloadPool.remove(Constants.FIRST_ELEMENT);
			viewDownload.reuse(remotePath, size, login, cache, notification);
			download = viewDownload;
		}
//		downloads.put(notification.getNotificationHashid(), download);	//TODO check for concurrency issues
		return download;
	}
	
	public synchronized ViewDownload getNewViewDownload(String remotePath, int size, ViewCache cache, ViewNotification notification){
		ViewDownload download;
		if(downloadPool.isEmpty()){
			download = new ViewDownload(remotePath, size, cache, notification);
		}else{
			ViewDownload viewDownload = downloadPool.remove(Constants.FIRST_ELEMENT);
			viewDownload.reuse(remotePath, size, cache, notification);
			download = viewDownload;
		}
//		downloads.put(notification.getNotificationHashid(), download);	//TODO check for concurrency issues
		return download;
	}
	
	public synchronized void recicleViewDownload(ViewDownload download){
//		serviceData.getManagerNotifications().recycleNotification(download.getNotification());
		download.clean();
		downloadPool.add(download);
	}
	
	public boolean isConnectionAvailable(){
		boolean connectionAvailable = false;
		try {
			connectionAvailable = connectivityState.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED;
			Log.d("ManagerDownloads", "isConnectionAvailable mobile: "+connectionAvailable);	
		} catch (Exception e) { }
		try {
			connectionAvailable = connectionAvailable || connectivityState.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED;
			Log.d("ManagerDownloads", "isConnectionAvailable wifi: "+connectionAvailable);
		} catch (Exception e) { }
		try {
			connectionAvailable = connectionAvailable || connectivityState.getNetworkInfo(6).getState() == NetworkInfo.State.CONNECTED;
			Log.d("ManagerDownloads", "isConnectionAvailable wimax: "+connectionAvailable);
		} catch (Exception e) { }
		try {
			connectionAvailable = connectionAvailable || connectivityState.getNetworkInfo(9).getState() == NetworkInfo.State.CONNECTED;
			Log.d("ManagerDownloads", "isConnectionAvailable ethernet: "+connectionAvailable);
		} catch (Exception e) { }
		
		return connectionAvailable;
	}
	
	public boolean isPermittedConnectionAvailable(ViewIconDownloadPermissions permissions){
		boolean connectionAvailable = false;
		if(permissions.isWiFi()){
			try {
				connectionAvailable = connectionAvailable || connectivityState.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED;
				Log.d("ManagerDownloads", "isPermittedConnectionAvailable wifi: "+connectionAvailable);
			} catch (Exception e) { }
		} 
		if(permissions.isWiMax()){
			try {
				connectionAvailable = connectionAvailable || connectivityState.getNetworkInfo(6).getState() == NetworkInfo.State.CONNECTED;
				Log.d("ManagerDownloads", "isPermittedConnectionAvailable wimax: "+connectionAvailable);
			} catch (Exception e) { }
		} 
		if(permissions.isMobile()){
			try {
				connectionAvailable = connectionAvailable || connectivityState.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED;
				Log.d("ManagerDownloads", "isPermittedConnectionAvailable mobile: "+connectionAvailable);
			} catch (Exception e) { }
		}
		if(permissions.isEthernet()){
			try {
				connectionAvailable = connectionAvailable || connectivityState.getNetworkInfo(9).getState() == NetworkInfo.State.CONNECTED;
				Log.d("ManagerDownloads", "isPermittedConnectionAvailable ethernet: "+connectionAvailable);
			} catch (Exception e) { }
		}

		Log.d("ManagerDownloads", "isPermittedConnectionAvailable: "+connectionAvailable+"  permissions: "+permissions);
		return connectionAvailable;
	}
	

	
	public ViewCache downloadLatestVersionInfo(){
		ViewCache cache = managerCache.getNewLatestVersionDownloadViewCache();
		ViewNotification notification = serviceData.getManagerNotifications().getNewViewNotification(EnumNotificationTypes.GET_UPDATE
									, serviceData.getString(R.string.self_update), R.string.self_update);
		ViewDownload download = getNewViewDownload(Constants.URI_LATEST_VERSION_XML, cache, notification);

		try {
			download(download, true);
		} catch (Exception e) {
			download(download, true);
		}

		return cache;
	}
	
	
	
	public boolean isIconCached(int appHashid){
		return managerCache.isIconCached(appHashid);
	}
	
	public void getIcon(ViewDownloadInfo iconInfo, boolean isLoginRequired, ViewLogin login){
			ViewDownload download;
			ViewCache cache = managerCache.getNewIconViewCache(iconInfo.getAppHashid());
			ViewNotification notification = serviceData.getManagerNotifications().getNewViewNotification(EnumNotificationTypes.GET_ICONS, iconInfo.getAppName(), iconInfo.getAppHashid());
			
			if(isLoginRequired){
				download = getNewViewDownload(iconInfo.getRemotePath(), login, cache, notification);
			}else{
				download = getNewViewDownload(iconInfo.getRemotePath(), cache, notification);
			}
			iconsDownloadManager.executeDownload(download);		
	}
	

	public void getRepoIcons(ViewDownloadStatus downloadStatus, ArrayList<ViewDownloadInfo> iconsInfo){
		Log.d("Aptoide-ManagerDownloads", "getRepoIcons ");
		
		for (ViewDownloadInfo iconInfo : iconsInfo) {
			if(isIconCached(iconInfo.getAppHashid())){
				continue;
			}else{
				getIcon(iconInfo, downloadStatus.getRepository().isLoginRequired(), downloadStatus.getRepository().getLogin());
			}
		}
		
		downloadStatus.incrementOffset(serviceData.getDisplayListsDimensions().getCacheSize());
		serviceData.getRepoIcons(downloadStatus);
		
	}
	
	public boolean isScreenCached(int appHashid, int orderNumber){
		return managerCache.isScreenCached(appHashid, orderNumber);
	}
	
	public void getScreen(ViewDownloadInfo screenInfo, boolean isLoginRequired, ViewLogin login, int screensNumber, int orderNumber){
			ViewDownload download;
			ViewCache cache = managerCache.getNewScreenViewCache(screenInfo.getAppHashid(), orderNumber);
			ViewNotification notification = serviceData.getManagerNotifications().getNewViewNotification(EnumNotificationTypes.GET_SCREENS, screenInfo.getAppName(), screenInfo.getAppHashid());
			
			if(isLoginRequired){
				download = getNewViewDownload(screenInfo.getRemotePath(), login, cache, notification);
			}else{
				download = getNewViewDownload(screenInfo.getRemotePath(), cache, notification);
			}
			screensDownloadManager.executeDownload(download, screensNumber, orderNumber);		
	}
	
	public void getAppScreens(ViewRepository repository, ArrayList<ViewDownloadInfo> screensInfo){
		if(screensInfo == null || !(screensInfo.size()>0)){
			return;
		}
		int orderNumber = 1;
		for (ViewDownloadInfo screenInfo : screensInfo) {
			Log.d("Aptoide-ManagerDownloads", "getAppScreens screen: "+screenInfo);
			if(isScreenCached(screenInfo.getAppHashid(), orderNumber)){
				orderNumber++;
				if(orderNumber > screensInfo.size()){
					serviceData.gettingAppScreensFinished(screenInfo.getAppHashid());
				}
				continue;
			}else{
				getScreen(screenInfo, repository.isLoginRequired(), repository.getLogin(), screensInfo.size(), orderNumber);
				orderNumber++;
				if(orderNumber > screensInfo.size()){
					serviceData.gettingAppScreensFinished(screenInfo.getAppHashid());
				}
			}
			
		}
	}
	

	
	public ViewCache startRepoDeltaDownload(ViewRepository repository){
		return startRepoDownload(repository, EnumInfoType.DELTA);
	}
	
	public ViewCache startRepoBareDownload(ViewRepository repository){
		return startRepoDownload(repository, EnumInfoType.BARE);
	}
	
	public ViewCache startRepoDownloadDownload(ViewRepository repository){
		return startRepoDownload(repository, EnumInfoType.DOWNLOAD);
	}
	
	public ViewCache startRepoIconDownload(ViewRepository repository){
		return startRepoDownload(repository, EnumInfoType.ICON);
	}
	
//	public ViewCache startRepoAppDownloads(ViewRepository repository){
//		return startRepoDownload(repository, EnumInfoType.DOWNLOAD);
//	}
	
//	public ViewCache startRepoExtraDownload(ViewRepository repository){
//		return startRepoDownload(repository, EnumInfoType.EXTRAS);
//	}
	
	public ViewCache startRepoStatsDownload(ViewRepository repository){
		return startRepoDownload(repository, EnumInfoType.STATS);
	}
	
	
	public ViewCache startRepoDownload(ViewRepository repository, EnumInfoType infoType){
		ViewCache cache = null;
		ViewNotification notification = null;
		ViewDownload download;
		
		String repoName = repository.getRepoName();
		String xmlRemotePath = null;
		
		switch (infoType) {
			case DELTA:															//TODO info=bare+icon&
				xmlRemotePath = repository.getUri()+Constants.PATH_REPO_INFO_XML+"info=bare+icon&unix_timestamp=true&show_apphashid=true&hash="+repository.getDelta();
				cache = managerCache.getNewRepoDeltaViewCache(repository.getHashid());
				break;
		
			case BARE:
				xmlRemotePath = repository.getUri()+Constants.PATH_REPO_INFO_XML+"info=bare&unix_timestamp=true";
//				xmlRemotePath = "http://aptoide.com/teste/apps.xml";
//				xmlRemotePath = "http://aptoide.com/testing/xml/info.xml";
				cache = managerCache.getNewRepoBareViewCache(repository.getHashid());
				notification = serviceData.getManagerNotifications().getNewViewNotification(EnumNotificationTypes.REPO_BARE_DOWNLOAD, repoName, repository.getHashid());
				break;
		
			case DOWNLOAD:
				xmlRemotePath = repository.getUri()+Constants.PATH_REPO_INFO_XML+"info=download&show_apphashid=true";
				cache = managerCache.getNewRepoDownloadViewCache(repository.getHashid());
				notification = serviceData.getManagerNotifications().getNewViewNotification(EnumNotificationTypes.REPO_UPDATE, repoName, repository.getHashid());
				break;
				
			case ICON:
				xmlRemotePath = repository.getUri()+Constants.PATH_REPO_INFO_XML+"info=icon&show_apphashid=true";
//				xmlRemotePath = "http://aptoide.com/testing/xml/info_icon.xml";
				cache = managerCache.getNewRepoIconViewCache(repository.getHashid());
				break;
				
//			case DOWNLOAD:
////				xmlPath = repository.getUri()+Constants.PATH_REPO_INFO_XML+"?info=download";	//TODO implement rest of args
//				xmlRemotePath = "http://aptoide.com/testing/xml/info_download.xml";
//				cache = managerCache.getNewRepoDownloadViewCache(repository.getHashid());
//				break;
				
			case STATS:
				xmlRemotePath = repository.getUri()+Constants.PATH_REPO_STATS_XML+"show_apphashid=true";
//				xmlRemotePath = "http://aptoide.com/testing/xml/stats.xml";
				cache = managerCache.getNewRepoStatsViewCache(repository.getHashid());
				break;	
				
//			case EXTRAS:
////				xmlPath = repository.getUri()+Constants.PATH_REPO_EXTRAS_XML;	//TODO implement rest of args
//				xmlRemotePath = "http://aptoide.com/testing/xml/extras.xml";
//				cache = managerCache.getNewRepoExtrasViewCache(repository.getHashid());
//				break;
				
			default:
				break;
		}
		if(!infoType.equals(EnumInfoType.BARE)){
			notification = serviceData.getManagerNotifications().getNewViewNotification(EnumNotificationTypes.REPO_UPDATE, repoName, repository.getHashid());
		}
		if(!infoType.equals(EnumInfoType.DELTA)){
			EnumAppsSorting bareSortingPolicy = EnumAppsSorting.reverseOrdinal(serviceData.getManagerPreferences().getAppsSortingPolicy());
			switch (bareSortingPolicy) {
				case ALPHABETIC:
					xmlRemotePath += "&order_by=alphabetic&order_direction=ascending";
					break;
				case FRESHNESS:
					xmlRemotePath += "&order_by=freshness&order_direction=descending";
					break;
				case STARS:
					xmlRemotePath += "&order_by=rating&order_direction=descending";
					break;
				case DOWNLOADS:
					xmlRemotePath += "&order_by=downloads&order_direction=descending";
					break;
				

				default:
					break;
			}
		}
		if(repository.isLoginRequired()){
			xmlRemotePath += "&username="+repository.getLogin().getUsername()+"&password="+repository.getLogin().getPassword();
		}
//		if(repository.isLoginRequired()){
//			download = getNewViewDownload(xmlRemotePath, repository.getLogin(), cache, notification);
//		}else{
			download = getNewViewDownload(xmlRemotePath, cache, notification);
//		}
		
		try {
			download(download, true);
		} catch (Exception e) {
			download(download, true);
		}
		
		return cache;
	}
	
	
	public ViewCache startRepoAppDownload(ViewRepository repository, int appHashid, EnumInfoType infoType){
		ViewCache cache = null;
		ViewNotification notification;
		ViewDownload download;
		
		String repoName = repository.getUri().substring(Constants.SKIP_URI_PREFIX).split("\\.")[Constants.FIRST_ELEMENT];
		String xmlRemotePath = null;
		
		switch (infoType) {
			case DOWNLOAD:
				String infoRequest = "info=download";
				if(!isIconCached(appHashid)){
					infoRequest += "+icon";
				}
				xmlRemotePath = repository.getUri()+Constants.PATH_REPO_INFO_XML+infoRequest+"&show_apphashid=true&apphashid="+appHashid;
//				xmlRemotePath = "http://aptoide.com/testing/xml/info_download.xml";
				cache = managerCache.getNewRepoAppDownloadViewCache(repository.getHashid(), appHashid);
				break;
				
			case EXTRAS:
				xmlRemotePath = repository.getUri()+Constants.PATH_REPO_EXTRAS_XML+"show_apphashid=true&apphashid="+appHashid;
//				xmlRemotePath = "http://aptoide.com/testing/xml/extras.xml";
				cache = managerCache.getNewRepoAppExtrasViewCache(repository.getHashid(), appHashid);
				break;
			
			case STATS:
				xmlRemotePath = repository.getUri()+Constants.PATH_REPO_STATS_XML+"show_apphashid=true&apphashid="+appHashid;
//				xmlRemotePath = "http://aptoide.com/testing/xml/stats.xml";
				cache = managerCache.getNewRepoAppStatsViewCache(repository.getHashid(), appHashid);
				break;
				
			case COMMENTS:
				xmlRemotePath = String.format(Constants.URI_FORMAT_COMMENTS_WS, URLEncoder.encode(repository.getRepoName()), URLEncoder.encode(Integer.toString(appHashid)));
				cache = managerCache.getNewRepoAppCommentsViewCache(repository.getHashid(), appHashid);
				break;
	
			default:
				break;
		}
		Log.d("Aptoide-ManagerDownloads", "xmlRemotePath: "+xmlRemotePath);
		
		notification = serviceData.getManagerNotifications().getNewViewNotification(EnumNotificationTypes.REPO_APP_UPDATE, repoName, appHashid);
		if(repository.isLoginRequired() && !infoType.equals(EnumInfoType.COMMENTS)){
			xmlRemotePath += "&username="+repository.getLogin().getUsername()+"&password="+repository.getLogin().getPassword();
		}
//			download = getNewViewDownload(xmlRemotePath, repository.getLogin(), cache, notification);
//		}else{
			download = getNewViewDownload(xmlRemotePath, cache, notification);
//		}
		
		try {
			download(download, true);
		} catch (Exception e) {
			download(download, true);
		}
		
		return cache;
	}
	
	
	public ViewCache repoAppDownload(ViewRepository repository, int appHashid){
		ViewCache cache = null;
		String xmlRemotePath = repository.getUri()+Constants.PATH_REPO_INFO_XML+"info=download&show_apphashid=true&apphashid="+appHashid;
		
		Log.d("Aptoide-ManagerDownloads repoAppDownload: ", xmlRemotePath);

		try {
			URL endpoint = new URL(xmlRemotePath);
			HttpURLConnection connection = (HttpURLConnection) endpoint.openConnection(); //Careful with UnknownHostException. Throws MalformedURLException, IOException
			
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Accept", "application/xml");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
//			connection.setConnectTimeout(TIME_OUT);

//			connection.setRequestProperty("User-Agent", getUserAgentString());
			
			ViewAppDownloadInfo downloadInfo = serviceData.getManagerXml().dom.parseRepoAppDownloadXml(connection, repository.getHashid());
			ViewDownload download = null;
			if(repository.isLoginRequired()){	//TODO getAppName and appHashid (take it out of ViewAppDownloadInfo where it doesn't belong)
				download = prepareApkDownload(downloadInfo.getAppHashid(), "update "+downloadInfo.getAppHashid(), repository.getBasePath()+downloadInfo.getRemotePathTail()
											, repository.getLogin(), downloadInfo.getSize(), downloadInfo.getMd5hash());
			}else{
				download = prepareApkDownload(downloadInfo.getAppHashid(), "update "+downloadInfo.getAppHashid(), repository.getBasePath()+downloadInfo.getRemotePathTail()
						, downloadInfo.getSize(), downloadInfo.getMd5hash());
			}
			cache = downloadApk(download);
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return cache;
	}
	
	
	public ViewCache downloadMyapp(String uriString, String myappName){
		ViewCache cache = managerCache.getNewMyappDownloadViewCache(myappName);
		if(!managerCache.isMyAppCached(myappName)){
			ViewNotification notification = serviceData.getManagerNotifications().getNewViewNotification(EnumNotificationTypes.GET_MYAPP, "myapp", myappName.hashCode());
			ViewDownload download = getNewViewDownload(uriString, cache, notification);

			try {
				download(download, false);
			} catch (Exception e) {
				download(download, false);
			}
			
		}
		return cache;
	}
	
	public ViewDownload prepareApkDownload(int appHashid, String appName, String remotePath, ViewLogin login, int size, String md5Hash){
		ViewCache cache = managerCache.getNewAppViewCache(appHashid, md5Hash);
		ViewNotification notification = serviceData.getManagerNotifications().getNewViewNotification(EnumNotificationTypes.GET_APP, appName, appHashid, size);
		return getNewViewDownload(remotePath, size, login, cache, notification);
	}
	
	public ViewDownload prepareApkDownload(int appHashid, String appName, String remotePath, int size, String md5Hash){
		ViewCache cache = managerCache.getNewAppViewCache(appHashid, md5Hash);
		ViewNotification notification = serviceData.getManagerNotifications().getNewViewNotification(EnumNotificationTypes.GET_APP, appName, appHashid, size);
		return getNewViewDownload(remotePath, size, cache, notification);
	}
	
	public ViewCache downloadApk(ViewDownload download){
//		Log.d("Aptoide-ManagerDownloads", "apk download: "+download.getCache());
		if(!getManagerCache().isApkCached(download.getNotification().getTargetsHashid()) || !getManagerCache().md5CheckOk(download.getCache())){
			try {
				download(download, false);
			} catch (Exception e) {
				try {
					download(download, false);
				} catch (Exception e2) {
					download(download, false);
				}
			}
		}
		Log.d("Aptoide-ManagerDownloads", "apk download: "+download.getNotification().getTargetsHashid());
		
		return download.getCache();
	}
	
	
	//TODO refactor magic numbers, logs and exceptions
	private void download(ViewDownload download, boolean overwriteCache){
		ViewCache localCache = download.getCache();
		ViewNotification notification = download.getNotification();

		String localPath = localCache.getLocalPath();
		String remotePath = download.getRemotePath();
		int targetBytes;
		
		FileOutputStream fileOutputStream = null;
		
		try{
			fileOutputStream = new FileOutputStream(localPath, !overwriteCache);
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(remotePath);
			Log.d("Aptoide-download","downloading from: "+remotePath+" to: "+localPath);
//			Log.d("Aptoide-download","downloading with: "+getUserAgentString()+" login: "+download.isLoginRequired());

//			httpGet.setHeader("User-Agent", getUserAgentString());	//TODO is consistently getting 404 from server
			
			String resumeLength = Long.toString(download.getCache().getFile().length());
			int resumeLengthInt = Integer.parseInt(resumeLength);
			if(!overwriteCache){
				Log.d("Aptoide-download","downloading from [bytes]: "+resumeLength);
				httpGet.setHeader("Range", "bytes="+resumeLength+"-");
				notification.incrementProgress(resumeLengthInt);
			}

			if(download.isLoginRequired()){		//TODO refactor using username/password args when using webservices (only exception left is when getting hard-disk files)
				URL url = new URL(remotePath);
				httpClient.getCredentialsProvider().setCredentials(
						new AuthScope(url.getHost(), url.getPort()),
						new UsernamePasswordCredentials(download.getLogin().getUsername(), download.getLogin().getPassword()));
			}

			HttpResponse httpResponse = httpClient.execute(httpGet);
			if(httpResponse == null){
				Log.d("Aptoide-ManagerDownloads","Problem in network... retry...");	
				httpResponse = httpClient.execute(httpGet);
				if(httpResponse == null){
					Log.d("Aptoide-ManagerDownloads","Major network exception... Exiting!");
					/*msg_al.arg1= 1;
						 download_error_handler.sendMessage(msg_al);*/
					throw new TimeoutException();
				}
			}

			if(httpResponse.getStatusLine().getStatusCode() == 401){
				Log.d("Aptoide-ManagerDownloads","401 Time out!");
				fileOutputStream.close();
				managerCache.clearCache(download.getCache());
				throw new TimeoutException();
			}else if(httpResponse.getStatusLine().getStatusCode() == 404){
				fileOutputStream.close();
				managerCache.clearCache(download.getCache());
				throw new AptoideExceptionNotFound("404 Not found!");
			}else{
				
				Log.d("Aptoide-ManagerDownloads", "Download target size: "+notification.getProgressCompletionTarget());
				
//				if(download.isSizeKnown()){
//					targetBytes = download.getSize()*Constants.KBYTES_TO_BYTES;	//TODO check if server sends kbytes or bytes
//					notification.setProgressCompletionTarget(targetBytes);
//				}else{

				if(httpResponse.containsHeader("Content-Length") && resumeLengthInt != 0){
					targetBytes = Integer.parseInt(httpResponse.getFirstHeader("Content-Length").getValue());
					Log.d("Aptoide-ManagerDownloads","targetBytes: "+targetBytes);
//					notification.setProgressCompletionTarget(targetBytes);
				}
//				}
				

				InputStream inputStream= null;
				
				if((httpResponse.getEntity().getContentEncoding() != null) && (httpResponse.getEntity().getContentEncoding().getValue().equalsIgnoreCase("gzip"))){

					Log.d("Aptoide-ManagerDownloads","with gzip");
					inputStream = new GZIPInputStream(httpResponse.getEntity().getContent());

				}else{

//					Log.d("Aptoide-ManagerDownloads","No gzip");
					inputStream = httpResponse.getEntity().getContent();

				}
				
				byte data[] = new byte[8096];
				int bytesRead;

				while((bytesRead = inputStream.read(data, 0, 8096)) > 0) {
					notification.incrementProgress(bytesRead);
					fileOutputStream.write(data,0,bytesRead);
				}
				Log.d("Aptoide-ManagerDownloads","Download done! Name: "+notification.getActionsTargetName()+" localPath: "+localPath);
				notification.setCompleted(true);
				fileOutputStream.flush();
				fileOutputStream.close();
				inputStream.close();

				if(localCache.hasMd5Sum()){
					getManagerCache().md5CheckOk(localCache);
					//TODO md5check boolean return handle  by  raising exception
				}

			}
		}catch (Exception e) {
			try {
				fileOutputStream.flush();
				fileOutputStream.close();	
			} catch (Exception e1) { }		
			e.printStackTrace();
			if(notification.getNotificationType().equals(EnumNotificationTypes.GET_APP) && download.getCache().getFile().length() > 0){
				notification.setCompleted(true);
				serviceData.scheduleInstallApp(notification.getTargetsHashid());
			}
			throw new AptoideExceptionDownload(e);
		}
	}
	
}
