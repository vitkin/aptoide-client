/**
 * EnumServiceDataCallback,		part of aptoide
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

package cm.aptoide.pt.data;

/**
 * EnumServiceDataCallback, typeSafes ServiceData Callbacks in Aptoide
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public enum EnumServiceDataCallback {
	UPDATE_REPOS_INFO,
	UPDATE_INSTALLED_LIST,
	UPDATE_AVAILABLE_LIST,
	REFRESH_ICON,
	UPDATE_APP_DOWNLOAD_INFO,
	UPDATE_APP_STATS,
	UPDATE_APP_EXTRAS,
	UPDATE_APP_COMMENTS,
	REFRESH_SCREENS;
	
	public static EnumServiceDataCallback reverseOrdinal(int ordinal){
		return values()[ordinal];
	}
}
