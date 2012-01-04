/**
 * ManagerNotifications,		auxilliary class to Aptoide's ServiceData
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


package cm.aptoide.pt.data.notifications;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import cm.aptoide.pt.Aptoide;
import cm.aptoide.pt.Notifier;
import cm.aptoide.pt.R;
import cm.aptoide.pt.data.AptoideServiceData;

/**
 * ManagerNotifications, centralizes all notifications' updates
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class ManagerNotifications {

	private NotificationManager notificationManager;
	
	private WakeLock keepScreenOn;
	private AptoideServiceData serviceData;
	
	/** Ongoing */
	private ViewNotification globalNotification;
	private ViewNotification packageManagerSync;
	private ViewNotification repoUpdate;
	private ArrayList<ViewNotification> repoAppUpdates;
	private ViewNotification gettingIcons;
	private ViewNotification gettingExtras;
	private ArrayList<ViewNotification> gettingApps;
	private ArrayList<ViewNotification> gettingUpdates;
	
	/** Object reuse pool */
	private ArrayList<ViewNotification> notificationPool;
	
//	private final static int KBYTES_TO_BYTES = 1024;					// moved to constants.xml
//	private NotificationManager notificationManager;					//TODO move to notifications within ServiceData
//	private Context context;											//TODO deprecate
	
	
	public ManagerNotifications(AptoideServiceData serviceData) {
		this.serviceData = serviceData;

		notificationManager = (NotificationManager)serviceData.getSystemService(Context.NOTIFICATION_SERVICE);
		
		
		PowerManager powerManager = (PowerManager) serviceData.getSystemService(Context.POWER_SERVICE);
        keepScreenOn = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "Full Power");
        
		notificationPool = new ArrayList<ViewNotification>(15);
		gettingApps = new ArrayList<ViewNotification>(5);
		gettingUpdates = new ArrayList<ViewNotification>(5);
		repoAppUpdates = new ArrayList<ViewNotification>(5);
		
		/************ Compatibility with API level 4, ignore following lines (must be at bottom of constructor) *************/
		try {
	        startForeground = serviceData.getClass().getMethod("startForeground", startForegroundSignature);
	        stopForeground = serviceData.getClass().getMethod("stopForeground", stopForegroundSignature);
	    } catch (NoSuchMethodException e) {
	        // Running on an older platform.
	        startForeground = stopForeground = null;
	        return;
	    }
	    try {
	        setForeground = serviceData.getClass().getMethod("setForeground", setForegroundSignature);
	    } catch (NoSuchMethodException e) {
	        throw new IllegalStateException("OS doesn't have Service.startForeground OR Service.setForeground!");
	    }
	
		startForegroundCompat(R.string.aptoide_started, getNotification());
		

		/********************************************************************************************************************/
	}
		
	public void startNotification(ViewNotification notification){
		switch (notification.getNotificationType()) {
		case GLOBAL:
			
			break;

		default:
			break;
		}
	}
	
	public void startNotifier(){
		serviceData.startActivity(new Intent(serviceData, Notifier.class));
	}
	
	
	
	public synchronized ViewNotification getNewViewNotification(EnumNotificationTypes notificationType, String actionsTargetName, int targetsHashid, int progressCompletionTarget){
		ViewNotification notification;
		if(notificationPool.isEmpty()){
			notification = new ViewNotification(notificationType, actionsTargetName, targetsHashid, progressCompletionTarget);
		}else{
			ViewNotification viewNotification = notificationPool.remove(0);
			viewNotification.reuse(notificationType, actionsTargetName, targetsHashid, progressCompletionTarget);
			notification = viewNotification;
		}
		
		switch (notificationType) {
			case REPO_UPDATE:
				repoUpdate = notification;
				break;
				
			case REPO_APP_UPDATE:
				repoAppUpdates.add(notification);
				break;
	
			default:
				break;
		}
		
		return notification;
	}
	
	public ViewNotification getNewViewNotification(EnumNotificationTypes notificationType, String actionsTargetName, int targetsHashid){
		return getNewViewNotification(notificationType, actionsTargetName, targetsHashid, 1);
	}

//TODO refactor
	
//	private void setNotification(int uniqueId, int progress) {
//
//		String appName = notifications.get(uniqueId).get("appName");
//		int size = Integer.parseInt(notifications.get(uniqueId).get("intSize"));
//		String version = notifications.get(uniqueId).get("version");
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
//    	notificationManager.notify(uniqueId, notification); 
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
//
//	
//	public void dismissAllNotifications(){
//		for (Integer notificationKey : notifications.keySet()) {
//			dismissNotification(notificationKey);
//		}
//	}
//	
//	public void dismissNotification(int apkidHash){
//		notificationManager.cancel(apkidHash);
////		notifications.remove(apkidHash);
//	}
//
////-------------------
//
////TODO refactor	
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
	
	public void destroy(){
		stopForegroundCompat(R.string.aptoide_started);		
	}

	
	private Notification getNotification() {
        // Set the icon, scrolling text and timestamp
        Notification notification = new Notification(R.drawable.ic_notification, serviceData.getText(R.string.aptoide_started), System.currentTimeMillis());

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(serviceData, 0, new Intent(serviceData, Aptoide.class), 0);

        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(serviceData.getApplicationContext(), serviceData.getText(R.string.app_name), serviceData.getManagerSystemSync().getAptoideVersionNameInUse(), contentIntent);
        
        return notification; 
	}

	
	

	/****************************************************************************************************************************
	 * 		From this point forward is stuff needed only to provide support for API level 4	service foregrounding	:P			*
	 * 																															* 
	 ****************************************************************************************************************************/
	
	private static final Class<?>[] setForegroundSignature = new Class[] {
	    boolean.class};
	private static final Class<?>[] startForegroundSignature = new Class[] {
	    int.class, Notification.class};
	private static final Class<?>[] stopForegroundSignature = new Class[] {
	    boolean.class};

	private Method setForeground;
	private Method startForeground;
	private Method stopForeground;
	private Object[] setForegroundArgs = new Object[1];
	private Object[] startForegroundArgs = new Object[2];
	private Object[] stopForegroundArgs = new Object[1];

	void invokeMethod(Method method, Object[] args) {
	    try {
	        method.invoke(serviceData, args);
	    } catch (InvocationTargetException e) {
	        // Should not happen.
	        Log.w("Aptoide ServiceData", "Unable to invoke method", e);
	    } catch (IllegalAccessException e) {
	        // Should not happen.
	        Log.w("Aptoide ServiceData", "Unable to invoke method", e);
	    }
	}

	/**
	 * This is a wrapper around the new startForeground method, using the older
	 * APIs if it is not available.
	 */
	void startForegroundCompat(int id, Notification notification) {
	    // If we have the new startForeground API, then use it.
	    if (startForeground != null) {
	        startForegroundArgs[0] = Integer.valueOf(id);
	        startForegroundArgs[1] = notification;
	        invokeMethod(startForeground, startForegroundArgs);
	        return;
	    }

	    // Fall back on the old API.
	    setForegroundArgs[0] = Boolean.TRUE;
	    invokeMethod(setForeground, setForegroundArgs);
	    notificationManager.notify(id, notification);
	}

	/**
	 * This is a wrapper around the new stopForeground method, using the older
	 * APIs if it is not available.
	 */
	void stopForegroundCompat(int id) {
	    // If we have the new stopForeground API, then use it.
	    if (stopForeground != null) {
	        stopForegroundArgs[0] = Boolean.TRUE;
	        try {
	            stopForeground.invoke(serviceData, stopForegroundArgs);
	        } catch (InvocationTargetException e) {
	            // Should not happen.
	            Log.w("Aptoide ServiceData", "Unable to invoke stopForeground", e);
	        } catch (IllegalAccessException e) {
	            // Should not happen.
	            Log.w("Aptoide ServiceData", "Unable to invoke stopForeground", e);
	        }
	        return;
	    }

	    // Fall back on the old API.  Note to cancel BEFORE changing the
	    // foreground state, since we could be killed at that point.
	    notificationManager.cancel(id);
	    setForegroundArgs[0] = Boolean.FALSE;
	    invokeMethod(setForeground, setForegroundArgs);
	}
	
}
