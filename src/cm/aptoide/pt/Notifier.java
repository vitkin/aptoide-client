/**
 * Notifier, part of Aptoide
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
package cm.aptoide.pt;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ProgressBar;

/**
 * Notifier, shows all running notifications
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class Notifier extends Activity {

//	private Handler mHandler = new Handler();
//	private ProgressBar mProgress;
//	private int mProgressStatus = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);	//TODO only on syncInstalledPackages
		
	}
	
//	public updateProgress(){
//		mProgress = (ProgressBar) findViewById(R.id.pbar);
//	       
//        new Thread(new Runnable() {		
//            public void run() {
//            	
//            	Vector<ApkNode> apk_lst = db.getAll("abc");
//            	mProgress.setMax(apk_lst.size());
//        		PackageManager mPm;
//        		PackageInfo pkginfo;
//        		mPm = getPackageManager();
//        		
//        		keepScreenOn.acquire();
//        		
//        		//TODO iterate through getInstalledPackages(), it'll surely have much less iterations
//        		for(ApkNode node: apk_lst){	
//        			if(node.status == 0){
//       				 try{
//       					 pkginfo = mPm.getPackageInfo(node.apkid, 0);
//       					 String vers = pkginfo.versionName;
//       					 int verscode = pkginfo.versionCode;
//       					 db.insertInstalled(node.apkid, vers, verscode);
//       				 }catch(Exception e) {
//       					 //Not installed anywhere... does nothing
//       				 }
//       			 }else{
//       				 try{
//       					 pkginfo = mPm.getPackageInfo(node.apkid, 0);
//       					 String vers = pkginfo.versionName;
//       					 int verscode = pkginfo.versionCode;
//       					 db.UpdateInstalled(node.apkid, vers, verscode);
//       				 }catch (Exception e){
//       					 db.removeInstalled(node.apkid);
//       				 }
//       			 }
//                    mProgressStatus++;
//                    // Update the progress bar
//                    mHandler.post(new Runnable() {
//                        public void run() {
//                            mProgress.setProgress(mProgressStatus);
//                        }
//                    });
//                }
//        		
//        		keepScreenOn.release();
//        		
//                Message msg = new Message();
//                msg.what = LOAD_TABS;
//                startHandler.sendMessage(msg);
//                
//            }
//        }).start();	
//	}

	

}
