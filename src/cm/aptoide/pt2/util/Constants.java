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
	public static final String PATH_SDCARD = Environment.getExternalStorageDirectory().getPath();
	public static final String PATH_CACHE = PATH_SDCARD + "/.aptoide/";
	public static final String PATH_CACHE_REPOS = PATH_CACHE + "repos/";
	public static final String PATH_CACHE_APKS = PATH_CACHE + "apks/";
	public static final String PATH_CACHE_ICONS = PATH_CACHE + "icons/";
	public static final String PATH_CACHE_SCREENS = PATH_CACHE + "screens/";
	public static final String PATH_CACHE_MYAPPS = PATH_CACHE + "myapps/";
}
