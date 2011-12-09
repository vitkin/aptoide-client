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


package cm.aptoide.pt.data.downloads;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import cm.aptoide.pt.data.AptoideServiceData;
import cm.aptoide.pt.data.Constants;
import cm.aptoide.pt.data.ViewClientStatistics;
import cm.aptoide.pt.data.cache.ManagerCache;
import cm.aptoide.pt.data.cache.ViewCache;
import cm.aptoide.pt.data.model.ViewLogin;
import cm.aptoide.pt.data.model.ViewRepository;
import cm.aptoide.pt.data.notifications.EnumNotificationTypes;
import cm.aptoide.pt.data.notifications.ViewNotification;
import cm.aptoide.pt.data.xml.EnumInfoType;

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
	private HashMap<Integer, ViewDownload> downloads;
	
	/** Object reuse pool */
	private ArrayList<ViewDownload> downloadPool;
	private ArrayList<ViewLogin> loginPool;

//	private final static int KBYTES_TO_BYTES = 1024;					// moved to constants.xml
//	private HashMap<Integer, HashMap<String, String>> notifications;	//TODO move to notifications within ServiceData
//	private NotificationManager notificationManager;					//TODO move to notifications within ServiceData
//	private Context context;											//TODO deprecate
//	private WakeLock keepScreenOn;										//moved to ServiceData
	
	public ManagerCache getManagerCache(){
		return this.managerCache;
	}
	
	public ViewDownload getDownload(int notificationHashid) {
		return downloads.get(notificationHashid);
	}
	
	private ViewClientStatistics getClientStatistics(){
		return serviceData.getStatistics();
	}
	
	
	public ManagerDownloads(AptoideServiceData serviceData) {
		this.serviceData = serviceData;
		managerCache = new ManagerCache();
		
		connectivityState = (ConnectivityManager)serviceData.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		this.downloads = new HashMap<Integer, ViewDownload>();
		this.downloadPool = new ArrayList<ViewDownload>();
		this.loginPool = new ArrayList<ViewLogin>();
		
		Log.d("Aptoide","******* \n Downloads will be made to: " + Constants.PATH_CACHE + "\n ********");
	}
		
	

	//TODO refactor all thie to reduce data redundancy and memory waste
	public synchronized ViewDownload getNewViewDownload(String remotePath, ViewCache cache, ViewNotification notification){
		ViewDownload download;
		if(downloadPool.isEmpty()){
			download = new ViewDownload(remotePath, cache, notification);
		}else{
			ViewDownload viewDownload = downloadPool.remove(Constants.FIRST_ELEMENT);
			viewDownload.reuse(remotePath, cache, notification);
			download = viewDownload;
		}
		downloads.put(notification.getNotificationHashid(), download);	//TODO check for concurrency issues
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
		downloads.put(notification.getNotificationHashid(), download);	//TODO check for concurrency issues
		return download;
	}
	
	public boolean isConnectionAvailable(){
		boolean connectionAvailable = false;
		try {
			connectionAvailable = connectivityState.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED;
			connectionAvailable = connectionAvailable || connectivityState.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED;
			connectionAvailable = connectionAvailable || connectivityState.getNetworkInfo(6).getState() == NetworkInfo.State.CONNECTED;
			
		} catch (Exception e) { }
		try {
			connectionAvailable = connectionAvailable || connectivityState.getNetworkInfo(9).getState() == NetworkInfo.State.CONNECTED;
			
		} catch (Exception e) { }
		
		return connectionAvailable;
	}
	
	public ViewCache startRepoBareDownload(ViewRepository repository){
		return startRepoDownload(repository, EnumInfoType.BARE);
	}
	
	public ViewCache startRepoIconDownload(ViewRepository repository){
		return startRepoDownload(repository, EnumInfoType.ICON);
	}
	
	public ViewCache startRepoExtraDownload(ViewRepository repository){
		return startRepoDownload(repository, EnumInfoType.EXTRAS);
	}
	
	
	public ViewCache startRepoDownload(ViewRepository repository, EnumInfoType infoType){
		ViewCache cache = null;
		ViewNotification notification;
		ViewDownload download;
		int retrysCount = 3;
		boolean downloadSuccess = false; 
		
		String repoName = repository.getUri().substring(Constants.SKIP_URI_PREFIX).split("\\.")[Constants.FIRST_ELEMENT];
		String xmlPath = null;
		
		switch (infoType) {
			case BARE:
//				xmlPath = repository.getUri()+Constants.PATH_REPO_INFO_XML+"?info=bare";	//TODO implement rest of args
				xmlPath = "http://aptoide.com/testing/xml/info.xml";
				cache = managerCache.getNewRepoBareViewCache(repository.getHashid());
				break;
			case ICON:
//				xmlPath = repository.getUri()+Constants.PATH_REPO_INFO_XML+"?info=icon";	//TODO implement rest of args
				xmlPath = "http://aptoide.com/testing/xml/info_icon.xml";
				cache = managerCache.getNewRepoIconViewCache(repository.getHashid());
				break;
			case DOWNLOAD:
//				xmlPath = repository.getUri()+Constants.PATH_REPO_INFO_XML+"?info=download";	//TODO implement rest of args
//				xmlPath = "http://aptoide.com/testing/xml/info_download.xml";
//				cache = managerCache.getNewRepoDownloadViewCache(repository.getHashid());
				break;
			case EXTRAS:
//				xmlPath = repository.getUri()+Constants.PATH_REPO_EXTRAS_XML;	//TODO implement rest of args
				xmlPath = "http://aptoide.com/testing/xml/extras.xml";
				cache = managerCache.getNewRepoExtrasViewCache(repository.getHashid());
				break;
				
			default:
				break;
		}
		notification = serviceData.getManagerNotifications().getNewViewNotification(EnumNotificationTypes.REPOS_UPDATE, repoName, repository.getHashid());
		if(repository.isLoginRequired()){	//TODO get login from pool
			download = getNewViewDownload(xmlPath, repository.getLogin(), cache, notification);
		}else{
			download = getNewViewDownload(xmlPath, cache, notification);
		}
		
		do{
			downloadSuccess = download(download.getNotification().getNotificationHashid(), true);
			retrysCount--;
		}while( !downloadSuccess && retrysCount > 0 );
		
//		if(!downloadSuccess){
//			//TODO raise exception
//		}
		
		return cache;
	}
	
//	public void startRepoDownloadAndProcessing(ViewRepository repository){
//		ViewCache cache;
//		ViewNotification notification;
//		ViewDownload download;
//		
//		String repoName = repository.getUri().substring(Constants.SKIP_URI_PREFIX).split(".")[Constants.FIRST_ELEMENT];
//		
////		String bareInfoXmlPath = repository.getUri()+Constants.PATH_REPO_INFO_XML+"?info=bare";
//		String bareInfoXmlPath = "http://aptoide.com/testing/xml/info.xml";
//		cache = managerCache.getNewRepoViewCache(repository.getHashid());
//		notification = serviceData.getManagerNotifications().getNewViewNotification(EnumNotificationTypes.REPOS_UPDATE, repoName, repository.getHashid(), 1);
//		if(repository.isLoginRequired()){	//TODO get login from pool
//			download = getNewViewDownload(bareInfoXmlPath, repository.getLogin(), cache, notification);
//		}else{
//			download = getNewViewDownload(bareInfoXmlPath, cache, notification);
//		}
//		download(download.getNotification().getNotificationHashid(), true);
//		//TODO set notification's progressCompletionTarget
//	}
	
	
	
	//TODO refactor magic numbers, logs and exceptions
	private boolean download(final int notificationHashid, final boolean overwriteCache){
//					if(!keepScreenOn.isHeld()){
//						keepScreenOn.acquire();
//					}
//					int threadApkidHash = apkidHash;
//					
//					String remotePath = notifications.get(threadApkidHash).get("remotePath");
//					String md5sum = notifications.get(threadApkidHash).get("md5sum");
//					
//					String localPath = notifications.get(threadApkidHash).get("localPath");
//					 Log.d("Aptoide-DownloadQueuService","thread apkidHash: "+threadApkidHash +" localPath: "+localPath);	

		ViewDownload download = getDownload(notificationHashid);
		ViewCache localCache = download.getCache();
		ViewNotification notification = download.getNotification();

		String localPath = localCache.getLocalPath();
		String remotePath = download.getRemotePath();
		int targetBytes;
		ViewClientStatistics clientStatistics = getClientStatistics();

		//					Message downloadArguments = new Message();
		try{

			getManagerCache().clearCache(localCache);

			FileOutputStream fileOutputStream = new FileOutputStream(localPath);
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(remotePath);

//						SharedPreferences sPref = context.getSharedPreferences("aptoide_prefs", Context.MODE_PRIVATE);
//						String myid = sPref.getString("myId", "NoInfo");
//						String myscr = sPref.getInt("scW", 0)+"x"+sPref.getInt("scH", 0);

//TODO refactor this user-agent string
//						mHttpGet.setHeader("User-Agent", "aptoide-" + context.getString(R.string.ver_str)+";"+ Configs.TERMINAL_INFO+";"+myscr+";id:"+myid+";"+sPref.getString(Configs.LOGIN_USER_NAME, ""));

			if(download.isLoginRequired()){
				URL url = new URL(remotePath);
				httpClient.getCredentialsProvider().setCredentials(
						new AuthScope(url.getHost(), url.getPort()),
						new UsernamePasswordCredentials(download.getLogin().getUsername(), download.getLogin().getPassword()));
			}

			HttpResponse httpResponse = httpClient.execute(httpGet);
//TODO check for gzip (code in remoteintab 1013 #531)
			if(httpResponse == null){
				Log.d("Aptoide","Problem in network... retry...");	
				httpResponse = httpClient.execute(httpGet);
				if(httpResponse == null){
					Log.d("Aptoide","Major network exception... Exiting!");
					/*msg_al.arg1= 1;
								 download_error_handler.sendMessage(msg_al);*/
					throw new TimeoutException();
				}
			}

			if(httpResponse.getStatusLine().getStatusCode() == 401){
				throw new TimeoutException();
			}else{
				if(download.isSizeKnown()){
					targetBytes = download.getSize()*Constants.KBYTES_TO_BYTES;
				}else{
					targetBytes = httpResponse.getAllHeaders().length;
					notification.setProgressCompletionTarget(targetBytes);
				}

				InputStream inputStream = httpResponse.getEntity().getContent();
				byte data[] = new byte[8096];
				int bytesRead;
				bytesRead = inputStream.read(data, 0, 8096);

//							int progressNotificationUpdate = 200;
//							int intermediateProgress = 0;
				while(bytesRead != -1) {
//								if(progressNotificationUpdate == 0){
//									if(!keepScreenOn.isHeld()){
//										keepScreenOn.acquire();
//									}
//									progressNotificationUpdate = 200;
//									Message progressArguments = new Message();
//									progressArguments.arg1 = threadApkidHash;
//									progressArguments.arg2 = intermediateProgress;
//									downloadProgress.sendMessage(progressArguments);
//									intermediateProgress = 0;
//								}else{
//									intermediateProgress += red;
//									progressNotificationUpdate--;
//								}
					notification.incrementProgress(bytesRead);
					fileOutputStream.write(data,0,bytesRead);
					bytesRead = inputStream.read(data, 0, 8096);
				}
				Log.d("Aptoide","Download done! notificationTargetName: "+notification.getActionsTargetName() +" localPath: "+localPath);
				notification.setCompleted(true);
				fileOutputStream.flush();
				fileOutputStream.close();
				inputStream.close();

//						if(keepScreenOn.isHeld()){
//							keepScreenOn.release();
//						}

				if(localCache.hasMd5Sum()){
					getManagerCache().md5CheckOk(localCache);
					//TODO md5check boolean return handle  by  raising exception
				}
				
				return true;
			}

		}catch (Exception e) { 
//						if(keepScreenOn.isHeld()){
//							keepScreenOn.release();
//						}
//						downloadArguments.arg1= 1;
//						downloadArguments.arg2 =threadApkidHash;
//						downloadErrorHandler.sendMessage(downloadArguments);
			
			//TODO handle exception
			e.printStackTrace();
			return false;
		}
	}
	
	
	
//TODO refactor	
	
//	public void startDownload(DownloadNode downloadNode){
//		HashMap<String,String> notification = new HashMap<String,String>();
//		notification.put("remotePath", downloadNode.getRemotePath());				//
//		notification.put("md5sum", downloadNode.getMd5sum());						//
//		
//		notification.put("packageName", downloadNode.getPackageName());				//
//		notification.put("appName", downloadNode.getAppName());						//
//		notification.put("intSize", Integer.toString(downloadNode.getSize()));		//
//		notification.put("intProgress", "0");										//
//		notification.put("version", downloadNode.version);							//
//		notification.put("localPath", downloadNode.getLocalPath());					//
//		notification.put("isUpdate", Boolean.toString(downloadNode.isUpdate()));	//
//		if(downloadNode.isRepoPrivate()){
//			notification.put("loginRequired", "true");
//			notification.put("username", downloadNode.getLogins()[0]);
//			notification.put("password", downloadNode.getLogins()[1]);
//		}else{
//			notification.put("loginRequired", "false");
//		}
//		Log.d("Aptoide-DowloadQueueService", "download Started");
//		notifications.put(downloadNode.getPackageName().hashCode(), notification);
//		setNotification(downloadNode.getPackageName().hashCode(), 0);
//		downloadFile(downloadNode.getPackageName().hashCode());
//	}
	
//-------------------
	
	
//	public void startExternalDownload(String remotePath, String localPath, String apkName, Context context){
//		this.context = context;
//		HashMap<String,String> notification = new HashMap<String,String>();
//		notification.put("remotePath", remotePath);
//	}
	
//	public void setCurrentContext(Context context){
//		this.context = context;
//	}

	
//TODO moved to notifications, just check if there's something useful


//	private void setNotification(int apkidHash, int progress) {
//
//		String appName = notifications.get(apkidHash).get("appName");
//		int size = Integer.parseInt(notifications.get(apkidHash).get("intSize"));
//		String version = notifications.get(apkidHash).get("version");
//		
//		RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.download_notification);
//		contentView.setImageViewResource(R.id.download_notification_icon, R.drawable.ic_notification);
//		StringBuilder textApp = new StringBuilder(getString(R.string.download_alrt)+" "+appName);
//		if(version!=null){
//			Log.d("Aptoide", "External download taking place. Unable to retrive version.");
//			textApp.append(" v."+version);
//		}
//		contentView.setTextViewText(R.id.download_notification_name, textApp.toString());
//		
//		contentView.setProgressBar(R.id.download_notification_progress_bar, size*KBYTES_TO_BYTES, progress, false);	
//		
//    	Intent onClick = new Intent();
//		onClick.setClassName("cm.aptoide.pt", "cm.aptoide.pt");
//		onClick.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
//		onClick.setAction("cm.aptoide.pt.FROM_NOTIFICATION");
//    	
//    	// The PendingIntent to launch our activity if the user selects this notification
//    	PendingIntent onClickAction = PendingIntent.getActivity(context, 0, onClick, 0);
//
//    	Notification notification = new Notification(R.drawable.ic_notification, getString(R.string.download_alrt)+" "+appName, System.currentTimeMillis());
//    	notification.flags |= Notification.FLAG_NO_CLEAR|Notification.FLAG_ONGOING_EVENT;
//		notification.contentView = contentView;
//
//
//		// Set the info for the notification panel.
//    	notification.contentIntent = onClickAction;
////    	notification.setLatestEventInfo(this, getText(R.string.app_name), getText(R.string.add_repo_text), contentIntent);
//
//
//		notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
//    	// Send the notification.
//    	// We use the position because it is a unique number.  We use it later to cancel.
//    	notificationManager.notify(apkidHash, notification); 
//    	
////		Log.d("Aptoide-DownloadQueueService", "Notification Set");
//    }
//	
//	private void setFinishedNotification(int apkidHash, String localPath) {
//		
//		String packageName = notifications.get(apkidHash).get("packageName");
//		String appName = notifications.get(apkidHash).get("appName");
//		int size = Integer.parseInt(notifications.get(apkidHash).get("intSize"));
//		String version = notifications.get(apkidHash).get("version");
//		
//		RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.download_notification);
//		contentView.setImageViewResource(R.id.download_notification_icon, R.drawable.ic_notification);
//		contentView.setTextViewText(R.id.download_notification_name, getString(R.string.finished_download_message)+" "+appName+" v."+version);
//		contentView.setProgressBar(R.id.download_notification_progress_bar, size*KBYTES_TO_BYTES, size*KBYTES_TO_BYTES, false);	
//		
//		Intent onClick = new Intent("pt.caixamagica.aptoide.INSTALL_APK", Uri.parse("apk:"+packageName));
//		onClick.setClassName("cm.aptoide.pt", "cm.aptoide.pt.RemoteInTab");
//		onClick.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
//    	onClick.putExtra("localPath", localPath);
//    	onClick.putExtra("packageName", packageName);
//    	onClick.putExtra("apkidHash", apkidHash);
//    	onClick.putExtra("isUpdate", Boolean.parseBoolean(notifications.get(packageName.hashCode()).get("isUpdate")));
//    	/*Changed by Rafael Campos*/
//    	onClick.putExtra("version", version);
//		 Log.d("Aptoide-DownloadQueuService","finished notification apkidHash: "+apkidHash +" localPath: "+localPath);	
//    	
//    	// The PendingIntent to launch our activity if the user selects this notification
//    	PendingIntent onClickAction = PendingIntent.getActivity(context, 0, onClick, 0);
//				
//    	Notification notification = new Notification(R.drawable.ic_notification, getString(R.string.finished_download_alrt)+" "+appName, System.currentTimeMillis());
//    	notification.flags |= Notification.FLAG_AUTO_CANCEL;
//		notification.contentView = contentView;
//
//
//		// Set the info for the notification panel.
//    	notification.contentIntent = onClickAction;
////    	notification.setLatestEventInfo(this, getText(R.string.app_name), getText(R.string.add_repo_text), contentIntent);
//
//
//		notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
//    	// Send the notification.
//    	// We use the position because it is a unique number.  We use it later to cancel.
//    	notificationManager.notify(apkidHash, notification); 
//    	
////		Log.d("Aptoide-DownloadQueueService", "Notification Set");
//		
//	}


//-------------------
	
//TODO refactor
	

//	
//	
//	/*
//	 * Notification UI Handlers
//	 * 
//	 */
//	private Handler downloadHandler = new Handler() {
//        @Override
//        public void handleMessage(Message downloadArguments) {
//        	if(downloadArguments.arg1 == 1){
//        		int apkidHash = downloadArguments.arg2;
//        		String localPath =  (String) downloadArguments.obj;
////        		notificationManager.cancel(downloadArguments.arg2);
//        		setFinishedNotification(apkidHash, localPath);
////   			 	notifications.remove(apkidHash);
//        	}else{ }
//        }
//	};
//	
//	 protected Handler downloadProgress = new Handler(){
//
//		 @Override
//		 public void handleMessage(Message progressArguments) {
//			 super.handleMessage(progressArguments);
//			 	
//			 int apkidHash = progressArguments.arg1;
//			 int intermediateProgress = progressArguments.arg2;
//			 //Log.d("Aptoide","Progress: " + pd.getProgress() + " Other: " +  (pd.getMax()*0.96) + " Adding: " + msg.what);
//			 Log.d("Aptoide-downloadQueue", "apkidHash: "+apkidHash+" current progress - "+notifications.get(apkidHash).get("intProgress") + "  additional - "+ intermediateProgress);
//			 int progress = Integer.parseInt(notifications.get(apkidHash).get("intProgress"))+intermediateProgress;
//			 notifications.get(apkidHash).put("intProgress", Integer.toString(progress));
//			 setNotification(apkidHash, progress);
//		 }
//	 };
//	
//
//	 private Handler downloadErrorHandler = new Handler() {
//		 @Override
//		 public void handleMessage(Message downloadArguments) {
//			 int apkHash = downloadArguments.arg2;
//			 notificationManager.cancel(apkHash);
////			 notifications.remove(apkHash);
//			 if(downloadArguments.arg1 == 1){
//				 Toast.makeText(context, getString(R.string.network_error), Toast.LENGTH_LONG).show();
//			 }else{
//				 Toast.makeText(context, getString(R.string.md5_error), Toast.LENGTH_LONG).show();
//			 }
//		 }
//	 };
		
//-------------------

}
