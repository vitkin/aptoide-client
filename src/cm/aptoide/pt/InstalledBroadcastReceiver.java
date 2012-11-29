/*******************************************************************************
 * Copyright (c) 2012 rmateus.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
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
			final Database db = Database.getInstance(context);
			new Thread(new Runnable() {
				
				public void run() {
					try{
						PackageManager mPm = context.getPackageManager();
						PackageInfo pi = mPm.getPackageInfo(arg1.getData().getEncodedSchemeSpecificPart(), 0);
						db.insertInstalled(arg1.getData().getEncodedSchemeSpecificPart(),pi.versionCode,pi.versionName,(String) pi.applicationInfo.loadLabel(mPm));
						db.deleteScheduledDownload(arg1.getData().getEncodedSchemeSpecificPart(), pi.versionName);
					}catch (Exception e) {
						e.printStackTrace();
					}finally{
						Intent i = new Intent("pt.caixamagica.aptoide.REDRAW");
						i.putExtra("apkid", arg1.getData().getEncodedSchemeSpecificPart());
						context.sendBroadcast(i);
					}
					
					
				}
			}).start();
			
		
		}else if (arg1.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)){
			final Database db = Database.getInstance(context);
			new Thread(new Runnable() {
				public void run() {
					db.deleteInstalled(arg1.getData().getEncodedSchemeSpecificPart());
					Intent i = new Intent("pt.caixamagica.aptoide.REDRAW");
					i.putExtra("apkid", arg1.getData().getEncodedSchemeSpecificPart());
					context.sendBroadcast(i);
				}
			}).start();
			
		}

	}

}
