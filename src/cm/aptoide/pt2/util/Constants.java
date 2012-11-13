/*
 * Constants, part of Aptoide
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
package cm.aptoide.pt2.util;

import android.os.Environment;

/**
 * Constants
 *
 * @author dsilveira
 *
 */
public class Constants {
	public static final String UTC_TIMEZONE = "UTC";
	public static final int MILISECONDS_TO_SECONDS = 1000;
	public static final int KILO_BYTE = 1024;
	
	public static final int DOWNLOAD_CHUNK_SIZE = 8*KILO_BYTE;
	public static final int MAX_PARALLEL_DOWNLOADS = 4;
	/** miliseconds **/
	public static final int DOWNLOAD_UPDATE_TRIGGER = 1000;
	
	/** miliseconds **/
	public static final int SERVER_CONNECTION_TIMEOUT = 10000;
	public static final int SERVER_READ_TIMEOUT = 30000;
	
	public static final String PATH_SDCARD = Environment.getExternalStorageDirectory().getPath();
	public static final String PATH_CACHE = PATH_SDCARD + "/.aptoide/";
	public static final String PATH_CACHE_REPOS = PATH_CACHE + "repos/";
	public static final String PATH_CACHE_APKS = PATH_CACHE + "apks/";
	public static final String PATH_CACHE_ICONS = PATH_CACHE + "icons/";
	public static final String PATH_CACHE_SCREENS = PATH_CACHE + "screens/";
	public static final String PATH_CACHE_MYAPPS = PATH_CACHE + "myapps/";

	public static final String APTOIDE_PACKAGE_NAME = "cm.aptoide.pt2";
	public static final String APTOIDE_CLASS_NAME = APTOIDE_PACKAGE_NAME+".MainActivity";
	public static final String SERVICE_DOWNLOAD_CLASS_NAME = APTOIDE_PACKAGE_NAME+".services.ServiceDownload";	
	
	public static final String FILE_PREFERENCES = "aptoide_preferences";
	
}
