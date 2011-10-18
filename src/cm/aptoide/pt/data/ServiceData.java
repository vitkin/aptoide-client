/*
 * ServiceData, part of Aptoide
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.widget.Toast;
import cm.aptoide.pt.Aptoide;
import cm.aptoide.pt.R;
import cm.aptoide.pt.data.preferences.ManagerPreferences;
import cm.aptoide.pt.data.system.ManagerSystemSync;

/**
 * ServiceData, Aptoide's data I/O manager for the activity classes
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class ServiceData extends Service {

	private NotificationManager notificationManager;
	private ArrayList<Messenger> serviceClients;
	private EnumServiceDataMessage latestRequest;

	private WakeLock keepScreenOn;
	
	private ManagerPreferences managerPreferences;
	private ManagerSystemSync managerSystemSync;
	
	
	class IncomingRequestHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
        	latestRequest = EnumServiceDataMessage.reverseOrdinal(msg.what);
            switch (latestRequest) {
//                case MSG_REGISTER_CLIENT:
//                    mClients.add(msg.replyTo);
//                    break;
//                case MSG_UNREGISTER_CLIENT:
//                    mClients.remove(msg.replyTo);
//                    break;
//                case MSG_SET_VALUE:
//                    mValue = msg.arg1;
//                    for (int i=mClients.size()-1; i>=0; i--) {
//                        try {
//                            mClients.get(i).send(Message.obtain(null,
//                                    MSG_SET_VALUE, mValue, 0));
//                        } catch (RemoteException e) {
//                            // The client is dead.  Remove it from the list;
//                            // we are going through the list from back to front
//                            // so this is safe to do inside the loop.
//                            mClients.remove(i);
//                        }
//                    }
//                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
	
    final Messenger serviceRequestReceiver = new Messenger(new IncomingRequestHandler());

	/**
	 * When binding to the service, we return an interface to our messenger
	 * allowing clients to send requests to the service.
	 */
	@Override
	public IBinder onBind(Intent intent) {
		Log.d("Aptoide ServiceData", "binding new client");
		return serviceRequestReceiver.getBinder();
	}
	
    	
	@Override
	public void onCreate() {
		notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		
		/** Compatibility with API level 4, ignore following try catches */
		try {
	        startForeground = getClass().getMethod("startForeground", startForegroundSignature);
	        stopForeground = getClass().getMethod("stopForeground", stopForegroundSignature);
	    } catch (NoSuchMethodException e) {
	        // Running on an older platform.
	        startForeground = stopForeground = null;
	        return;
	    }
	    try {
	        setForeground = getClass().getMethod("setForeground", setForegroundSignature);
	    } catch (NoSuchMethodException e) {
	        throw new IllegalStateException(
	                "OS doesn't have Service.startForeground OR Service.setForeground!");
	    }
		/*****************************************************************/
	    
		serviceClients = new ArrayList<Messenger>();
		
		managerPreferences = new ManagerPreferences(this);
		managerSystemSync = new ManagerSystemSync(this);
		
		
		
		
		startForegroundCompat(R.string.aptoide_started, getNotification());
		Log.d("Aptoide ServiceData", "Service started");
		super.onCreate();
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}
	
	@Override
	public void onDestroy() {
		stopForegroundCompat(R.string.aptoide_started);
		Toast.makeText(this, R.string.aptoide_stopped, Toast.LENGTH_LONG).show();
		stopSelf();
		Log.d("Aptoide ServiceData", "Service stopped");
		super.onDestroy();
	}

	
	public ManagerPreferences getManagerPreferences() {
		return managerPreferences;
	}
	
	public ManagerSystemSync getManagerSystemSync() {
		return managerSystemSync;
	}
	
	public ClientStatistics getStatistics(){
		ClientStatistics statistics = new ClientStatistics(managerSystemSync.getAptoideVersionNameInUse());
		managerPreferences.completeStatistics(statistics);
		return statistics;
	}
	
	
	private Notification getNotification() {
        // Set the icon, scrolling text and timestamp
        Notification notification = new Notification(R.drawable.ic_notification, getText(R.string.aptoide_started), System.currentTimeMillis());

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, Aptoide.class), 0);

        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(this, getText(R.string.app_name), managerSystemSync.getAptoideVersionNameInUse(), contentIntent);
        
        return notification; 
	}


	/**
	 * From this point forward is stuff needed TODO support for API level 4
	 * :P 
	 */
	
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
	        startForeground.invoke(this, startForegroundArgs);
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
	            stopForeground.invoke(this, stopForegroundArgs);
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
