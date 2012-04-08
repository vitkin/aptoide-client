/**
 * ManagerSystemSync,		auxilliary class to Aptoide's ServiceData
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

package cm.aptoide.pt.data.system;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.RemoteException;
import cm.aptoide.pt.data.AptoideServiceData;
import cm.aptoide.pt.data.model.ViewApplication;

/**
 * ManagerSystemSync, manages data synchronization with the underlying android's package database 
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class ManagerSystemSync {

	AptoideServiceData serviceData;
	PackageManager packageManager;
	
	ExecutorService tasksPool = Executors.newSingleThreadExecutor();
	
	public ManagerSystemSync(AptoideServiceData serviceData){
		this.serviceData = serviceData;
		packageManager = serviceData.getPackageManager();
	}
	
	public int getAptoideVersionInUse(){
		PackageInfo aptoideInfo;
		try {
			aptoideInfo = packageManager.getPackageInfo("cm.aptoide.pt", 0);
   		} catch (NameNotFoundException e) {	
   			/** this should never happen */
   			return -1;
   		}
		return aptoideInfo.versionCode;
	}
	
	public String getAptoideVersionNameInUse(){
		PackageInfo aptoideInfo;
		try {
			aptoideInfo = packageManager.getPackageInfo("cm.aptoide.pt", 0);
   		} catch (NameNotFoundException e) {	
   			/** this should never happen */
   			return null;
   		}
		return aptoideInfo.versionName;
	}
	
	public String getAptoideAppName(){
		PackageInfo aptoideInfo;
		try {
			aptoideInfo = packageManager.getPackageInfo("cm.aptoide.pt", 0);
   		} catch (NameNotFoundException e) {	
   			/** this should never happen */
   			return null;
   		}
		return packageManager.getApplicationLabel(aptoideInfo.applicationInfo).toString();
	}
	
	public ArrayList<ViewApplication> getInstalledApps(){
		List<PackageInfo> systemInstalledList = packageManager.getInstalledPackages(0);
		ArrayList<ViewApplication> installedApps = new ArrayList<ViewApplication>(systemInstalledList.size());
		ViewApplication installedApp;
		for (PackageInfo installedAppInfo : systemInstalledList) {
			if(installedAppInfo.applicationInfo.sourceDir.split("[/]+")[1].equals("system")){
				continue;
				//TODO maybe show it but mark as system
			}
			installedApp = new ViewApplication((packageManager.getApplicationLabel(installedAppInfo.applicationInfo)).toString(), installedAppInfo.packageName, ((installedAppInfo.versionName==null)?Integer.toBinaryString(installedAppInfo.versionCode):installedAppInfo.versionName), installedAppInfo.versionCode, true);
			installedApps.add(installedApp);
		}
		return installedApps;
	}
	
	public ViewApplication getInstalledApp(String packageName){
		ViewApplication installedApp = null;
		try {
			PackageInfo installedAppInfo = packageManager.getPackageInfo(packageName, 0);
			
			installedApp = new ViewApplication((packageManager.getApplicationLabel(installedAppInfo.applicationInfo)).toString(), installedAppInfo.packageName, ((installedAppInfo.versionName==null)?Integer.toBinaryString(installedAppInfo.versionCode):installedAppInfo.versionName), installedAppInfo.versionCode, true);
			
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return installedApp;
	}
	
	public void cacheInstalledIcons(){
		 tasksPool.execute(new Runnable() {
			@Override
			public void run() {
				List<PackageInfo> systemInstalledList = packageManager.getInstalledPackages(0);
				for (PackageInfo installedAppInfo : systemInstalledList) {
					if(installedAppInfo.applicationInfo.sourceDir.split("[/]+")[1].equals("system")){
						continue;
						//TODO maybe show it but mark as system
					}
					serviceData.getManagerCache().cacheIcon((installedAppInfo.packageName+"|"+installedAppInfo.versionCode).hashCode(), ((BitmapDrawable)installedAppInfo.applicationInfo.loadIcon(packageManager)).getBitmap());
				}
//				serviceData.updateInstalledLists();				
			}
		});
	}
	
	public ViewHwFilters getHwFilters(){
		int sdkVersion = Build.VERSION.SDK_INT;
		int screenSize = serviceData.getResources().getConfiguration().screenLayout&Configuration.SCREENLAYOUT_SIZE_MASK;
		String glEsVersion = ((ActivityManager) serviceData.getSystemService(Context.ACTIVITY_SERVICE)).getDeviceConfigurationInfo().getGlEsVersion();
		
		return new ViewHwFilters(sdkVersion, screenSize, Float.parseFloat(glEsVersion));
	}
	
//	public Drawable getInstalledAppIcon(String packageName){
//		try {
//			return packageManager.getApplicationIcon(packageName);
//		} catch (NameNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return null;
//		}
//	}

}
