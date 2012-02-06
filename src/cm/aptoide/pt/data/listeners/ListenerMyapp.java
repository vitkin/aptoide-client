package cm.aptoide.pt.data.listeners;

import cm.aptoide.pt.data.AptoideServiceData;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.sax.StartElementListener;
import android.util.Log;


/**
 * This is the class which gets called by AlarmManager and that in its turn calls
 *  the measuring slaves
 * @author dsilveira
 *
 */
public class ListenerMyapp extends BroadcastReceiver {	

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("Aptoide-myapp", "intent: "+intent);
		
		Intent serviceIntent = new Intent(context, AptoideServiceData.class);

		context.startService(serviceIntent);
		

	}

}
