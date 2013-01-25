/*
 * ApplicationAptoide, part of Aptoide
 * Copyright (C) 2012 Duarte Silveira
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

import java.io.File;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import cm.aptoide.pt.preferences.ManagerPreferences;

import com.nostra13.universalimageloader.cache.disc.DiscCacheAware;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

/**
 * ApplicationAptoide, centralizes, statically, calls to instantiated objects
 *
 * @author dsilveira
 *
 */
public class ApplicationAptoide extends Application {

	private ManagerPreferences managerPreferences;
	private static Context context;
	
	@Override
	public void onCreate() {
		managerPreferences = new ManagerPreferences(getApplicationContext());
		setContext(getApplicationContext());
		 // Create global configuration and initialize ImageLoader with this configuration
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		
															 .displayer(new FadeInBitmapDisplayer(1000))
															 .showStubImage(android.R.drawable.sym_def_app_icon)
															 .resetViewBeforeLoading()
															 .cacheInMemory()
															 .cacheOnDisc()
															 .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
        															  .defaultDisplayImageOptions(options)
        															  .enableLogging()
        															  .discCache(new UnlimitedDiscCache(new File(Environment.getExternalStorageDirectory().getPath()+"/.aptoide/icons/")))
        															  .build();
        
        
        ImageLoader.getInstance().init(config);
		super.onCreate();
	}
	
	public ManagerPreferences getManagerPreferences(){
		return managerPreferences;
	}

	/**
	 * @return the context
	 */
	public static Context getContext() {
		return context;
	}
	
	

	/**
	 * @param context the context to set
	 */
	public static void setContext(Context context) {
		ApplicationAptoide.context = context;
	}
}
