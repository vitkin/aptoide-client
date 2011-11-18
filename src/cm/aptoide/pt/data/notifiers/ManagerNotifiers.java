/**
 * ManagerNotifiers,		auxilliary class to Aptoide's ServiceData
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


package cm.aptoide.pt.data.notifiers;

import java.io.File;
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

import cm.aptoide.pt.data.ServiceData;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

/**
 * ManagerNotifiers, centralizes all notifications' updates
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class ManagerNotifiers {

	private WakeLock keepScreenOn;
	private ServiceData serviceData;
	
	/** Ongoing */
	private Notifier globalNotification;
	private Notifier packageManagerSync;
	private Notifier reposUpdate;
	private Notifier gettingIcons;
	private Notifier gettingExtras;
	private ArrayList<Notifier> gettingApps;
	private ArrayList<Notifier> gettingUpdates;
	
	/** Object reuse pool */
	private ArrayList<Notifier> notificationPool;
	
//	private final static int KBYTES_TO_BYTES = 1024;					// moved to constants.xml
//	private NotificationManager notificationManager;					//TODO move to notifications within ServiceData
//	private Context context;											//TODO deprecate
//	private WakeLock keepScreenOn;										//moved to ServiceData
	
	
//TODO refactor
	
//	@Override
//	public void onCreate() {
//		super.onCreate();
//		
//		PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
//		keepScreenOn = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "Full Power");
//		Log.d("Aptoide-DowloadQueueService", "Created");
//	}
	
//-------------------	
	
	public ManagerNotifiers(ServiceData serviceData) {
		this.serviceData = serviceData;
		
		PowerManager powerManager = (PowerManager) serviceData.getSystemService(Context.POWER_SERVICE);
        keepScreenOn = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "Full Power");
        
		notificationPool = new ArrayList<Notifier>(10);
		gettingApps = new ArrayList<Notifier>(5);
		gettingUpdates = new ArrayList<Notifier>(5);
	}
		
	public void startNotification(Notifier notifier){
		switch (notifier.getNotifierType()) {
		case GLOBAL:
			
			break;

		default:
			break;
		}
	}

//TODO refactor
	
	private void setNotification(int uniqueId, int progress) {

		String appName = notifications.get(uniqueId).get("appName");
		int size = Integer.parseInt(notifications.get(uniqueId).get("intSize"));
		String version = notifications.get(uniqueId).get("version");
		
		RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.download_notification);
		contentView.setImageViewResource(R.id.download_notification_icon, R.drawable.ic_notification);
		StringBuilder textApp = new StringBuilder(getString(R.string.download_alrt)+" "+appName);
		if(version!=null){
			Log.d("Aptoide", "External download taking place. Unable to retrive version.");
			textApp.append(" v."+version);
		}
		contentView.setTextViewText(R.id.download_notification_name, textApp.toString());
		
		contentView.setProgressBar(R.id.download_notification_progress_bar, size*KBYTES_TO_BYTES, progress, false);	
		
    	Intent onClick = new Intent();
		onClick.setClassName("cm.aptoide.pt", "cm.aptoide.pt");
		onClick.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
		onClick.setAction("cm.aptoide.pt.FROM_NOTIFICATION");
    	
    	// The PendingIntent to launch our activity if the user selects this notification
    	PendingIntent onClickAction = PendingIntent.getActivity(context, 0, onClick, 0);

    	Notification notification = new Notification(R.drawable.ic_notification, getString(R.string.download_alrt)+" "+appName, System.currentTimeMillis());
    	notification.flags |= Notification.FLAG_NO_CLEAR|Notification.FLAG_ONGOING_EVENT;
		notification.contentView = contentView;


		// Set the info for the notification panel.
    	notification.contentIntent = onClickAction;
//    	notification.setLatestEventInfo(this, getText(R.string.app_name), getText(R.string.add_repo_text), contentIntent);


		notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    	// Send the notification.
    	// We use the position because it is a unique number.  We use it later to cancel.
    	notificationManager.notify(uniqueId, notification); 
    	
//		Log.d("Aptoide-DownloadQueueService", "Notification Set");
    }

	private void setFinishedNotification(int apkidHash, String localPath) {
		
		String packageName = notifications.get(apkidHash).get("packageName");
		String appName = notifications.get(apkidHash).get("appName");
		int size = Integer.parseInt(notifications.get(apkidHash).get("intSize"));
		String version = notifications.get(apkidHash).get("version");
		
		RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.download_notification);
		contentView.setImageViewResource(R.id.download_notification_icon, R.drawable.ic_notification);
		contentView.setTextViewText(R.id.download_notification_name, getString(R.string.finished_download_message)+" "+appName+" v."+version);
		contentView.setProgressBar(R.id.download_notification_progress_bar, size*KBYTES_TO_BYTES, size*KBYTES_TO_BYTES, false);	
		
		Intent onClick = new Intent("pt.caixamagica.aptoide.INSTALL_APK", Uri.parse("apk:"+packageName));
		onClick.setClassName("cm.aptoide.pt", "cm.aptoide.pt.RemoteInTab");
		onClick.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
    	onClick.putExtra("localPath", localPath);
    	onClick.putExtra("packageName", packageName);
    	onClick.putExtra("apkidHash", apkidHash);
    	onClick.putExtra("isUpdate", Boolean.parseBoolean(notifications.get(packageName.hashCode()).get("isUpdate")));
    	/*Changed by Rafael Campos*/
    	onClick.putExtra("version", version);
		 Log.d("Aptoide-DownloadQueuService","finished notification apkidHash: "+apkidHash +" localPath: "+localPath);	
    	
    	// The PendingIntent to launch our activity if the user selects this notification
    	PendingIntent onClickAction = PendingIntent.getActivity(context, 0, onClick, 0);
				
    	Notification notification = new Notification(R.drawable.ic_notification, getString(R.string.finished_download_alrt)+" "+appName, System.currentTimeMillis());
    	notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.contentView = contentView;


		// Set the info for the notification panel.
    	notification.contentIntent = onClickAction;
//    	notification.setLatestEventInfo(this, getText(R.string.app_name), getText(R.string.add_repo_text), contentIntent);


		notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    	// Send the notification.
    	// We use the position because it is a unique number.  We use it later to cancel.
    	notificationManager.notify(apkidHash, notification); 
    	
//		Log.d("Aptoide-DownloadQueueService", "Notification Set");
		
	}

	
	public void dismissAllNotifications(){
		for (Integer notificationKey : notifications.keySet()) {
			dismissNotification(notificationKey);
		}
	}
	
	public void dismissNotification(int apkidHash){
		notificationManager.cancel(apkidHash);
//		notifications.remove(apkidHash);
	}

//-------------------

//TODO refactor	
	
	/*
	 * Notification UI Handlers
	 * 
	 */
	private Handler downloadHandler = new Handler() {
        @Override
        public void handleMessage(Message downloadArguments) {
        	if(downloadArguments.arg1 == 1){
        		int apkidHash = downloadArguments.arg2;
        		String localPath =  (String) downloadArguments.obj;
//        		notificationManager.cancel(downloadArguments.arg2);
        		setFinishedNotification(apkidHash, localPath);
//   			 	notifications.remove(apkidHash);
        	}else{ }
        }
	};
	
	 protected Handler downloadProgress = new Handler(){

		 @Override
		 public void handleMessage(Message progressArguments) {
			 super.handleMessage(progressArguments);
			 	
			 int apkidHash = progressArguments.arg1;
			 int intermediateProgress = progressArguments.arg2;
			 //Log.d("Aptoide","Progress: " + pd.getProgress() + " Other: " +  (pd.getMax()*0.96) + " Adding: " + msg.what);
			 Log.d("Aptoide-downloadQueue", "apkidHash: "+apkidHash+" current progress - "+notifications.get(apkidHash).get("intProgress") + "  additional - "+ intermediateProgress);
			 int progress = Integer.parseInt(notifications.get(apkidHash).get("intProgress"))+intermediateProgress;
			 notifications.get(apkidHash).put("intProgress", Integer.toString(progress));
			 setNotification(apkidHash, progress);
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
		
//-------------------

}
