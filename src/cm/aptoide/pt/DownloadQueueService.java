package cm.aptoide.pt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

public class DownloadQueueService extends Service {

	private final static int KBYTES_TO_BYTES = 1024;
	
	private HashMap<Integer, HashMap<String, String>> notifications;
	
	private NotificationManager notificationManager;
	private Context context;
	private WakeLock keepScreenOn;
	
	// This is the object that receives interactions from service clients.
    private final IBinder binder = new DownloadQueueBinder();
	
    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class DownloadQueueBinder extends Binder {
    	DownloadQueueService getService() {
            return DownloadQueueService.this;
        }
    }
    
	@Override
	public IBinder onBind(Intent intent) {
		Log.d("Aptoide-DowloadQueueService", "Bound");
		return binder;
	}

	
	
	@Override
	public void onCreate() {
		super.onCreate();
		notifications = new HashMap<Integer, HashMap<String,String>>();
		
		PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		keepScreenOn = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "Full Power");
		Log.d("Aptoide-DowloadQueueService", "Created");
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Log.d("Aptoide-DowloadQueueService", "Started");
	}

	
	public void startDownload(String localPath, DownloadNode downloadNode, String apkid, String[] login, Context context, boolean isUpdate){
		this.context = context;
		HashMap<String,String> notification = new HashMap<String,String>();
		notification.put("remotePath", downloadNode.repo + "/" + downloadNode.path);
		notification.put("md5hash", downloadNode.md5h);
		
		notification.put("apkid", apkid);
		notification.put("intSize", Integer.toString(downloadNode.size));
		notification.put("intProgress", "0");
		notification.put("localPath", localPath);
		notification.put("isUpdate", Boolean.toString(isUpdate));
		if(login != null){
			notification.put("loginRequired", "true");
			notification.put("username", login[0]);
			notification.put("password", login[1]);
		}else{
			notification.put("loginRequired", "false");
		}
		Log.d("Aptoide-DowloadQueueService", "download Started");
		notifications.put(apkid.hashCode(), notification);
		setNotification(apkid.hashCode(), 0);
		downloadFile(apkid.hashCode());
	}
	

	private void setNotification(int apkHash, int progress) {

		String apkid = notifications.get(apkHash).get("apkid");
		int size = Integer.parseInt(notifications.get(apkHash).get("intSize"));
		
		RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.download_notification);
		contentView.setImageViewResource(R.id.download_notification_icon, R.drawable.ic_notification);
		contentView.setTextViewText(R.id.download_notification_name, getString(R.string.download_alrt)+" "+apkid);
		contentView.setProgressBar(R.id.download_notification_progress_bar, size*KBYTES_TO_BYTES, progress, false);	
		
    	Intent onClick = new Intent();
		onClick.setClassName("cm.aptoide.pt", "cm.aptoide.pt");
		onClick.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
		onClick.setAction("cm.aptoide.pt.FROM_NOTIFICATION");
    	
    	// The PendingIntent to launch our activity if the user selects this notification
    	PendingIntent onClickAction = PendingIntent.getActivity(context, 0, onClick, 0);

    	Notification notification = new Notification(R.drawable.icon, getString(R.string.download_alrt)+" "+apkid, System.currentTimeMillis());
    	notification.flags |= Notification.FLAG_NO_CLEAR|Notification.FLAG_ONGOING_EVENT;
		notification.contentView = contentView;


		// Set the info for the notification panel.
    	notification.contentIntent = onClickAction;
//    	notification.setLatestEventInfo(this, getText(R.string.app_name), getText(R.string.add_repo_text), contentIntent);


		notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    	// Send the notification.
    	// We use the position because it is a unique number.  We use it later to cancel.
    	notificationManager.notify(apkHash, notification); 
    	
//		Log.d("Aptoide-DownloadQueueService", "Notification Set");
    }
	
	private void setFinishedNotification(int apkHash, String localPath) {
		
		String apkid = notifications.get(apkHash).get("apkid");
		int size = Integer.parseInt(notifications.get(apkHash).get("intSize"));
		
		RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.download_notification);
		contentView.setImageViewResource(R.id.download_notification_icon, R.drawable.ic_notification);
		contentView.setTextViewText(R.id.download_notification_name, getString(R.string.finished_download_message)+" "+apkid);
		contentView.setProgressBar(R.id.download_notification_progress_bar, size*KBYTES_TO_BYTES, size*KBYTES_TO_BYTES, false);	
		
		Intent onClick = new Intent("pt.caixamagica.aptoide.INSTALL_APK", Uri.parse("apk:"+apkid));
		onClick.setClassName("cm.aptoide.pt", "cm.aptoide.pt.RemoteInTab");
		onClick.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
    	onClick.putExtra("localPath", localPath);
    	onClick.putExtra("apkid", apkid);
    	onClick.putExtra("apkHash", apkHash);
    	onClick.putExtra("isUpdate", Boolean.parseBoolean(notifications.get(apkid.hashCode()).get("isUpdate")));
		 Log.d("Aptoide-DownloadQueuService","finished notification apkHash: "+apkHash +" localPath: "+localPath);	
    	
    	// The PendingIntent to launch our activity if the user selects this notification
    	PendingIntent onClickAction = PendingIntent.getActivity(context, 0, onClick, 0);
				
    	Notification notification = new Notification(R.drawable.icon, getString(R.string.finished_download_alrt)+" "+apkid, System.currentTimeMillis());
    	notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.contentView = contentView;


		// Set the info for the notification panel.
    	notification.contentIntent = onClickAction;
//    	notification.setLatestEventInfo(this, getText(R.string.app_name), getText(R.string.add_repo_text), contentIntent);


		notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    	// Send the notification.
    	// We use the position because it is a unique number.  We use it later to cancel.
    	notificationManager.notify(apkHash, notification); 
    	
//		Log.d("Aptoide-DownloadQueueService", "Notification Set");
		
	}
	
	public void dismissAllNotifications(){
		for (Integer notificationKey : notifications.keySet()) {
			dismissNotification(notificationKey);
		}
	}
	
	public void dismissNotification(int apkHash){
		notificationManager.cancel(apkHash);
		notifications.remove(apkHash);
	}

	
//	private void installApk(String localPath, int position){
//
//    	Message downloadArguments = new Message();
//		downloadArguments.arg1 = 1;
//		downloadHandler.sendMessage(downloadArguments);
//    	
		//TODO Send a broadcast Intent with the data via sendBroadcast(), that the activity picks up with a BroadcastReceiver
//		
//	}
	
	private void downloadFile(final int apkHash){
			
		try{
			
			new Thread(){
				public void run(){
					if(!keepScreenOn.isHeld()){
						keepScreenOn.acquire();
					}
					int threadApkHash = apkHash;
					
					String remotePath = notifications.get(threadApkHash).get("remotePath");
					String md5hash = notifications.get(threadApkHash).get("md5hash");
					
					String localPath = notifications.get(threadApkHash).get("localPath");
					 Log.d("Aptoide-DownloadQueuService","thread apkHash: "+threadApkHash +" localPath: "+localPath);	
					
					
					Message downloadArguments = new Message();
					try{
						
						// If file exists, removes it...
						 File f_chk = new File(localPath);
						 if(f_chk.exists()){
							 f_chk.delete();
						 }
						 f_chk = null;
						
						FileOutputStream saveit = new FileOutputStream(localPath);
						DefaultHttpClient mHttpClient = new DefaultHttpClient();
						HttpGet mHttpGet = new HttpGet(remotePath);

						if(Boolean.parseBoolean(notifications.get(threadApkHash).get("loginRequired"))){
							URL mUrl = new URL(remotePath);
							mHttpClient.getCredentialsProvider().setCredentials(
									new AuthScope(mUrl.getHost(), mUrl.getPort()),
									new UsernamePasswordCredentials(notifications.get(threadApkHash).get("username"), notifications.get(threadApkHash).get("password") ));

						}

						HttpResponse mHttpResponse = mHttpClient.execute(mHttpGet);
						
						if(mHttpResponse == null){
							 Log.d("Aptoide","Problem in network... retry...");	
							 mHttpResponse = mHttpClient.execute(mHttpGet);
							 if(mHttpResponse == null){
								 Log.d("Aptoide","Major network exception... Exiting!");
								 /*msg_al.arg1= 1;
								 download_error_handler.sendMessage(msg_al);*/
								 throw new TimeoutException();
							 }
						 }
						
						if(mHttpResponse.getStatusLine().getStatusCode() == 401){
							throw new TimeoutException();
						}else{
							InputStream getit = mHttpResponse.getEntity().getContent();
							byte data[] = new byte[8096];
							int red;
							red = getit.read(data, 0, 8096);

							int progressNotificationUpdate = 200;
							int intermediateProgress = 0;
							while(red != -1) {
								if(progressNotificationUpdate == 0){
									if(!keepScreenOn.isHeld()){
										keepScreenOn.acquire();
									}
									progressNotificationUpdate = 200;
									Message progressArguments = new Message();
									progressArguments.arg1 = threadApkHash;
									progressArguments.arg2 = intermediateProgress;
									downloadProgress.sendMessage(progressArguments);
									intermediateProgress = 0;
								}else{
									intermediateProgress += red;
									progressNotificationUpdate--;
								}
																
								saveit.write(data,0,red);
								red = getit.read(data, 0, 8096);
							}
							Log.d("Aptoide","Download done! apkHash: "+threadApkHash +" localPath: "+localPath);
							saveit.flush();
							saveit.close();
							getit.close();
						}

						if(keepScreenOn.isHeld()){
							keepScreenOn.release();
						}

						File f = new File(localPath);
						Md5Handler hash = new Md5Handler();
						if(md5hash == null || md5hash.equalsIgnoreCase(hash.md5Calc(f))){
							downloadArguments.arg1 = 1;
							downloadArguments.arg2 = threadApkHash;
							downloadArguments.obj = localPath;
							downloadHandler.sendMessage(downloadArguments);
						}else{
							Log.d("Aptoide",md5hash + " VS " + hash.md5Calc(f));
							downloadArguments.arg1 = 0;
							downloadArguments.arg2 = threadApkHash;
							downloadErrorHandler.sendMessage(downloadArguments);
						}

					}catch (Exception e) { 
						if(keepScreenOn.isHeld()){
							keepScreenOn.release();
						}
						downloadArguments.arg1= 1;
						downloadArguments.arg2 =threadApkHash;
						downloadErrorHandler.sendMessage(downloadArguments);
					}
				}
			}.start();
			
			
		} catch(Exception e){	}
	}
	
	/*
	 * Notification UI Handlers
	 * 
	 */
	private Handler downloadHandler = new Handler() {
        @Override
        public void handleMessage(Message downloadArguments) {
        	if(downloadArguments.arg1 == 1){
        		int apkHash = downloadArguments.arg2;
        		String localPath =  (String) downloadArguments.obj;
//        		notificationManager.cancel(downloadArguments.arg2);
        		setFinishedNotification(apkHash, localPath);
//   			 	notifications.remove(apkHash);
        	}else{ }
        }
	};
	
	 protected Handler downloadProgress = new Handler(){

			@Override
			public void handleMessage(Message progressArguments) {
				super.handleMessage(progressArguments);

				int apkHash = progressArguments.arg1;
				int intermediateProgress = progressArguments.arg2;
				//Log.d("Aptoide","Progress: " + pd.getProgress() + " Other: " +  (pd.getMax()*0.96) + " Adding: " + msg.what);
				Log.d("Aptoide-downloadQueue", "apkHash: "+apkHash+" current progress - "+notifications.get(apkHash).get("intProgress") + "  additional - "+ intermediateProgress);
				int progress = Integer.parseInt(notifications.get(apkHash).get("intProgress"))+intermediateProgress;
				notifications.get(apkHash).put("intProgress", Integer.toString(progress));
				setNotification(apkHash, progress);
			}
		 };
	

	 private Handler downloadErrorHandler = new Handler() {
		 @Override
		 public void handleMessage(Message downloadArguments) {
			 int apkHash = downloadArguments.arg2;
			 notificationManager.cancel(apkHash);
//			 notifications.remove(apkHash);
			 if(downloadArguments.arg1 == 1){
				 Toast.makeText(context, getString(R.string.network_error), Toast.LENGTH_LONG).show();
			 }else{
				 Toast.makeText(context, getString(R.string.md5_error), Toast.LENGTH_LONG).show();
			 }
		 }
	 };

}
