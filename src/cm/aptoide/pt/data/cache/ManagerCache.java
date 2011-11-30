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
import java.util.ArrayList;

import util.Md5Handler;
import android.util.Log;
import cm.aptoide.pt.data.Constants;

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
	
	public ViewCache getNewRepoViewCache(int repoHashid){
		return getNewViewCache(Constants.PATH_CACHE_REPOS+repoHashid);
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
	
	public boolean md5CheckOk(ViewCache cache){
		File file = new File(cache.getLocalPath());
		Md5Handler hash = new Md5Handler();
		if(cache.getMd5sum().equalsIgnoreCase(hash.md5Calc(file))){
			return true;
		}else{
			Log.d("Aptoide",cache.getMd5sum()+ " VS " + hash.md5Calc(file));	//TODO refactor log
			return false;
		}
	}
	
}
