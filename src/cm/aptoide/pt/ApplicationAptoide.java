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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import cm.aptoide.pt.preferences.ManagerPreferences;
import cm.aptoide.pt.util.NetworkUtils;
import cm.aptoide.pt.views.ViewIconDownloadPermissions;

import com.nostra13.universalimageloader.cache.disc.DiscCacheAware;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FlushedInputStream;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

/**
 * ApplicationAptoide, centralizes, statically, calls to instantiated objects
 *
 * @author dsilveira
 *
 */
public class ApplicationAptoide extends Application {

	private ManagerPreferences managerPreferences;
	private static Context context;
	public static boolean DEBUG_MODE = false;
	
	@Override
	public void onCreate() {
		managerPreferences = new ManagerPreferences(getApplicationContext());
		setContext(getApplicationContext());
		 // Create global configuration and initialize ImageLoader with this configuration
		
		File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/.aptoide/debug.log");
		
		if(file.exists()){
			DEBUG_MODE = true;
		}
		
		DisplayImageOptions options = new DisplayImageOptions.Builder()
															 .displayer(new FadeInBitmapDisplayer(1000))
															 .showStubImage(android.R.drawable.sym_def_app_icon)
															 .resetViewBeforeLoading()
															 .cacheInMemory()
															 .cacheOnDisc()
															 .build();
		
		ImageLoaderConfiguration config;
		
		if(DEBUG_MODE){
			config = new ImageLoaderConfiguration.Builder(getApplicationContext())
																				  .defaultDisplayImageOptions(options)
																				  .discCache(new UnlimitedDiscCache(new File(Environment.getExternalStorageDirectory().getPath()+"/.aptoide/icons/")))
																				  .enableLogging()
																				  .imageDownloader(new ImageDownloaderWithPermissions())
																				  .build();
		}else{
			config = new ImageLoaderConfiguration.Builder(getApplicationContext())
																				  .defaultDisplayImageOptions(options)
																				  .discCache(new UnlimitedDiscCache(new File(Environment.getExternalStorageDirectory().getPath()+"/.aptoide/icons/")))
																				  .imageDownloader(new ImageDownloaderWithPermissions())
																				  .build();
		}
		
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
	
	public class ImageDownloaderWithPermissions extends ImageDownloader{
		
		/** {@value} */
		public static final int DEFAULT_HTTP_CONNECT_TIMEOUT = 5 * 1000; // milliseconds
		/** {@value} */
		public static final int DEFAULT_HTTP_READ_TIMEOUT = 20 * 1000; // milliseconds

		private int connectTimeout;
		private int readTimeout;

		public ImageDownloaderWithPermissions() {
			this(DEFAULT_HTTP_CONNECT_TIMEOUT, DEFAULT_HTTP_READ_TIMEOUT);
		}

		public ImageDownloaderWithPermissions(int connectTimeout, int readTimeout) {
			this.connectTimeout = connectTimeout;
			this.readTimeout = readTimeout;
		}

		@Override
		public InputStream getStreamFromNetwork(URI imageUri) throws IOException {
			
	        boolean download = NetworkUtils.isPermittedConnectionAvailable(context, managerPreferences.getIconDownloadPermissions());
			
	        if(download){
	        	URLConnection conn = imageUri.toURL().openConnection();
				conn.setConnectTimeout(connectTimeout);
				conn.setReadTimeout(readTimeout);
				return new FlushedInputStream(new BufferedInputStream(conn.getInputStream(), BUFFER_SIZE));
	        }else{
	        	return null;
	        }
		}

	}

	/**
	 * @param context the context to set
	 */
	public static void setContext(Context context) {
		ApplicationAptoide.context = context;
	}
}
