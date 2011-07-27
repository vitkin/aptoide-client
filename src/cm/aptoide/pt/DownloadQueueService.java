package cm.aptoide.pt;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

public class DownloadQueueService extends Service {

	private NotificationManager notificationManager;
	
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
		setNotification();
		Log.d("Aptoide-DowloadQueueService", "Created");
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Log.d("Aptoide-DowloadQueueService", "Started");
	}




	/**
	 * Set up the notification in the status bar, which can be used to restart the
	 * NetMeter main display activity.
	 */
	private void setNotification() {

		Log.d("Aptoide-DowloadQueueService", "Notification Started");

		notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		
		RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.download_notification);
		contentView.setImageViewResource(R.id.download_notification_icon, R.drawable.icon);			//TODO this drawable only for testing - change to dynamically load
		contentView.setTextViewText(R.id.download_notification_name, "Package X is being downloaded");	//TODO same for this text
		contentView.setProgressBar(R.id.download_notification_progress_bar, 100, 10, false);			//TODO same here		
		
    	Notification notification = new Notification(R.drawable.icon, "Doanloading", System.currentTimeMillis()); //TODO same here
    	notification.flags |= Notification.FLAG_NO_CLEAR|Notification.FLAG_ONGOING_EVENT;
		notification.contentView = contentView;


    	Intent downloadQueueIntent = new Intent();
		downloadQueueIntent.setClassName("cm.aptoide.pt", "cm.aptoide.pt.RemoteInSearch");
		downloadQueueIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
		downloadQueueIntent.setAction("cm.aptoide.pt.FROM_NOTIFICATION");
    	
    	// The PendingIntent to launch our activity if the user selects this notification
    	PendingIntent contentIntent = PendingIntent.getActivity(this, 0, downloadQueueIntent, 0);

    	// Set the info for the notification panel.
    	notification.contentIntent = contentIntent;
//    	notification.setLatestEventInfo(this, getText(R.string.app_name), getText(R.string.add_repo_text), contentIntent);

    	// Send the notification.
    	// We use a string id because it is a unique number.  We use it later to cancel.
    	notificationManager.notify(R.layout.download_notification, notification); 

    	setForeground(true);
    	
    }

}
