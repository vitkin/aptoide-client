package cm.aptoide.pt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class InstalledBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent arg1) {
		
		if(arg1.getAction().equals(Intent.ACTION_PACKAGE_ADDED)){
			DBHandler db = new DBHandler(context);
			db.open();
			db.beginTransation();
			try{
				PackageManager mPm = context.getPackageManager();
				PackageInfo pi = mPm.getPackageInfo(arg1.getData().getEncodedSchemeSpecificPart(), 0);
				db.insertInstalled(arg1.getData().getEncodedSchemeSpecificPart(),pi.versionCode,pi.versionName,(String) pi.applicationInfo.loadLabel(mPm));
			}catch (Exception e) {
				e.printStackTrace();
			}
			db.endTransation();
			context.sendBroadcast(new Intent("pt.caixamagica.aptoide.REDRAW"));
		
		}else if (arg1.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)){
			DBHandler db = new DBHandler(context);
			db.open();
			db.beginTransation();
			db.deleteInstalled(arg1.getData().getEncodedSchemeSpecificPart());
			db.endTransation();
			context.sendBroadcast(new Intent("pt.caixamagica.aptoide.REDRAW"));
		}

	}

}
