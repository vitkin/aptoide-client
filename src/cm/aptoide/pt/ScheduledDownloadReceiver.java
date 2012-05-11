package cm.aptoide.pt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class ScheduledDownloadReceiver extends BroadcastReceiver {

	

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		DbHandler db=new DbHandler(arg0);
		final ConnectivityManager connMgr = (ConnectivityManager) 
				arg0.getSystemService(Context.CONNECTIVITY_SERVICE);

				final android.net.NetworkInfo wifi = 
				connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
				final android.net.NetworkInfo mobile = 
						connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		
		
		SharedPreferences sPref = arg0.getSharedPreferences("aptoide_prefs", 0);
		Editor prefEdit = sPref.edit();
		Log.d("",!db.getScheduledListNames().isEmpty()+"");
		if(wifi.getState()==NetworkInfo.State.CONNECTED){
			Log.d("Receiver", "Wireless Connected");
			if(sPref.getBoolean("schDwnBox", false)&&!db.getScheduledListNames().isEmpty()){
				Intent intent = new Intent(arg0,ScheduledDownload.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|-Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
				intent.putExtra("downloadAll", "");
				prefEdit.putBoolean("intentChanged", true);
				prefEdit.commit();
				Log.i("Reeceiver",sPref.getBoolean("intentChanged", true)+"");
				arg0.startActivity(intent);}}
//		if ( ((NetworkInfo)arg1.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO)).getState()==NetworkInfo.State.CONNECTED)
//			if(sPref.getBoolean("schDwnBox", false))
				
	}

}
