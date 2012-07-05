package cm.aptoide.pt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.TimeoutException;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

public class ScheduledDownloadReceiver extends BroadcastReceiver {
	DBHandler db;
	Context context;

	
	ArrayList<HashMap<String, String>> failedDownloads;
	
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		context = arg0;
		db=new DBHandler(arg0);
		db.open();
		final ConnectivityManager connMgr = (ConnectivityManager) 
				arg0.getSystemService(Context.CONNECTIVITY_SERVICE);

				final android.net.NetworkInfo wifi = 
				connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
				final android.net.NetworkInfo mobile = 
						connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		
		
		SharedPreferences sPref = arg0.getSharedPreferences("aptoide_prefs", 0);
		Editor prefEdit = sPref.edit();
		
		if(wifi.getState()==NetworkInfo.State.CONNECTED){
			Log.d("Receiver", "Wireless Connected");
			if(sPref.getBoolean("schDwnBox", false)&&db.getScheduledDownloads().getCount()!=0){
				Intent intent = new Intent(arg0,ScheduledDownload.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|-Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
				intent.putExtra("downloadAll", "");
				prefEdit.putBoolean("intentChanged", true);
				prefEdit.commit();
				Log.i("Reeceiver",sPref.getBoolean("intentChanged", true)+"");
				arg0.startActivity(intent);
			}
			if (db.getFailedDownloads().size()!=0){
				Intent intent = new Intent(arg0,FailedDownloader.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|-Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
				context.startActivity(intent);
			}
		}
				
	}
	
	

}
