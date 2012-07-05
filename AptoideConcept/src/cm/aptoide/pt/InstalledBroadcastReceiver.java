package cm.aptoide.pt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class InstalledBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, final Intent arg1) {
		
		if(arg1.getAction().equals(Intent.ACTION_PACKAGE_ADDED)){
			new Thread(new Runnable() {
				
				public void run() {
					DBHandler db = new DBHandler(context);
					db.open();
					db.beginTransation();
					try{
						PackageManager mPm = context.getPackageManager();
						PackageInfo pi = mPm.getPackageInfo(arg1.getData().getEncodedSchemeSpecificPart(), 0);
						db.insertInstalled(arg1.getData().getEncodedSchemeSpecificPart(),pi.versionCode,pi.versionName,(String) pi.applicationInfo.loadLabel(mPm));
						db.deleteScheduledDownload(arg1.getData().getEncodedSchemeSpecificPart(), pi.versionName);
					}catch (Exception e) {
						e.printStackTrace();
					}finally{
						db.endTransation();
						Intent i = new Intent("pt.caixamagica.aptoide.REDRAW");
						i.putExtra("apkid", arg1.getData().getEncodedSchemeSpecificPart());
						context.sendBroadcast(i);
					}
					
					
				}
			}).start();
			
		
		}else if (arg1.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)){
			new Thread(new Runnable() {
				public void run() {
					DBHandler db = new DBHandler(context);
					db.open();
					db.beginTransation();
					db.deleteInstalled(arg1.getData()
							.getEncodedSchemeSpecificPart());
					db.endTransation();
					Intent i = new Intent("pt.caixamagica.aptoide.REDRAW");
					i.putExtra("apkid", arg1.getData().getEncodedSchemeSpecificPart());
					context.sendBroadcast(i);
				}
			}).start();
			
		}

	}

}
