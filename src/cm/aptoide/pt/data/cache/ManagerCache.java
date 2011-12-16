/**
 * ManagerCache,		auxilliary class to Aptoide's ServiceData
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

package cm.aptoide.pt.data.cache;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.StatFs;
import android.util.Log;
import cm.aptoide.pt.data.Constants;
import cm.aptoide.pt.data.util.Md5Handler;

/**
 * ManagerCache, models cache I/O
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class ManagerCache {

	/** Ongoing */
	private ArrayList<ViewCache> cacheViews;
	
	/** Object reuse pool */
	private ArrayList<ViewCache> cachePool;
	
		
	public ManagerCache() {
		this.cacheViews = new ArrayList<ViewCache>();
		this.cachePool = new ArrayList<ViewCache>();
		
		if(!isFreeSpaceInSdcard()){
			//TODO raise exception
		}
	}
	

	public synchronized ViewCache getNewViewCache(String localPath){
		if(cachePool.isEmpty()){
			return new ViewCache(localPath);
		}else{
			ViewCache viewCache = cachePool.remove(0);
			viewCache.reuse(localPath);
			return viewCache;
		}
	}	
	
	public synchronized ViewCache getNewViewCache(String localPath, String md5hash){
		if(cachePool.isEmpty()){
			return new ViewCache(localPath, md5hash);
		}else{
			ViewCache viewCache = cachePool.remove(0);
			viewCache.reuse(localPath, md5hash);
			return viewCache;
		}
	}
	
	public ViewCache getNewRepoBareViewCache(int repoHashid){
		return getNewViewCache(Constants.PATH_CACHE_REPOS+repoHashid+".bare"+".xml");
	}
	
	public ViewCache getNewRepoIconViewCache(int repoHashid){
		return getNewViewCache(Constants.PATH_CACHE_REPOS+repoHashid+".icon"+".xml");
	}
	
	public ViewCache getNewIconViewCache(int appHashid){
		return getNewViewCache(Constants.PATH_CACHE_ICONS+appHashid);
	}
	
	public ViewCache getNewRepoExtrasViewCache(int repoHashid){
		return getNewViewCache(Constants.PATH_CACHE_REPOS+repoHashid+".extras"+".xml");
	}
	
	
	public boolean isFreeSpaceInSdcard(){
		File sdcard_file = new File(Constants.PATH_SDCARD);
		if(!sdcard_file.exists() || !sdcard_file.canWrite()){
			
//			final AlertDialog upd_alrt = new AlertDialog.Builder(mctx).create();
//			upd_alrt.setIcon(android.R.drawable.ic_dialog_alert);
//			upd_alrt.setTitle(getText(R.string.remote_in_noSD_title));
//			upd_alrt.setMessage(getText(R.string.remote_in_noSD));
//			upd_alrt.setButton(getText(R.string.btn_ok), new OnClickListener() {
//				public void onClick(DialogInterface dialog, int which) {
//					finish();
//				}
//			});
//			upd_alrt.show();
			
			Log.d("Aptoide-ManagerCache","No writable SDCARD...");
			
			return false;
			
		}else{
		
			StatFs stat = new StatFs(sdcard_file.getPath());
			long blockSize = stat.getBlockSize();
			long totalBlocks = stat.getBlockCount();
			long availableBlocks = stat.getAvailableBlocks();
	
			long total = (blockSize * totalBlocks)/1024/1024;
			long avail = (blockSize * availableBlocks)/1024/1024;
			Log.d("Aptoide","* * * * * * * * * *");
			Log.d("Aptoide", "Total: " + total + " Mb");
			Log.d("Aptoide", "Available: " + avail + " Mb");
	
			if(avail < 10 ){
				Log.d("Aptoide","No space left on SDCARD...");
				Log.d("Aptoide","* * * * * * * * * *");
	
//				final AlertDialog upd_alrt = new AlertDialog.Builder(mctx).create();
//				upd_alrt.setIcon(android.R.drawable.ic_dialog_alert);
//				upd_alrt.setTitle(getText(R.string.remote_in_noSD_title));
//				upd_alrt.setMessage(getText(R.string.remote_in_noSDspace));
//				upd_alrt.setButton(getText(R.string.btn_ok), new OnClickListener() {
//					public void onClick(DialogInterface dialog, int which) {
//						finish();
//					}
//				});
//				upd_alrt.show();
				
				return false;
				
			}else{
				Log.d("Aptoide","Ok!");
				Log.d("Aptoide","* * * * * * * * * *");
	
				File cache_path = new File(Constants.PATH_CACHE);
				if(!cache_path.exists())
					cache_path.mkdir();
	
				File icon_path = new File(Constants.PATH_CACHE_ICONS);
				if(!icon_path.exists())
					icon_path.mkdir();
				
				File screens_path = new File(Constants.PATH_CACHE_SCREENS);
				if(!screens_path.exists())
					screens_path.mkdir();
				
				File repos_path = new File(Constants.PATH_CACHE_REPOS);
				if(!repos_path.exists())
					repos_path.mkdir();
				
				File apks_path = new File(Constants.PATH_CACHE_APKS);
				if(!apks_path.exists())
					apks_path.mkdir();
				
				return true;
			}
		}
	}
	
	/**
	 * clearCache, if it exists remove it
	 * 
	 * @param cache
	 */
	public void clearCache(ViewCache cache){
		 File file = new File(cache.getLocalPath());
		 if(file.exists()){
			 file.delete();
		 }
	}
	
	public boolean isIconCached(int appHashid){
		String iconPath = Constants.PATH_CACHE_ICONS+appHashid;
		File icon = new File(iconPath);
		return icon.exists();
	}
	
	public void cacheIcon(int appHashid, Bitmap icon){
		if(isIconCached(appHashid)){
			Log.d("Aptoide-ManagerCache", "installed app icon already exists: "+appHashid);
		}else{
			try {
				FileOutputStream out = new FileOutputStream(Constants.PATH_CACHE_ICONS+appHashid);
				icon.compress(Bitmap.CompressFormat.PNG, 90, out);
				Log.d("Aptoide-ManagerCache", "stored installed app icon in: "+Constants.PATH_CACHE_ICONS+appHashid);
			} catch (Exception e) {
				//TODO handle exception
				e.printStackTrace();
			}
		}
	}
	
	public void calculateMd5Hash(ViewCache cache){
		File file = new File(cache.getLocalPath());
		Md5Handler hash = new Md5Handler();
		
		cache.setMd5Sum(hash.md5Calc(file));		
	}
	
	public boolean md5CheckOk(ViewCache cache){
		File file = new File(cache.getLocalPath());
		Md5Handler hash = new Md5Handler();
		if(cache.getMd5sum().equalsIgnoreCase(hash.md5Calc(file))){
			return true;
		}else{
			Log.d("Aptoide-ManagerCache",cache.getMd5sum()+ " VS " + hash.md5Calc(file));	//TODO refactor log
			return false;
		}
	}
	
}