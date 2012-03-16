/**
 * EnumAptoideAppsListsTasks,		part of aptoide
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

package cm.aptoide.pt;

/**
 * EnumAptoideAppsListsTasks, typeSafes Apps Lists Tasks management in Aptoide
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public enum EnumAptoideInterfaceTasks {
	SYNCHRONIZED_INSTALLED_LIST,
	RESET_INSTALLED_LIST_DISPLAY,
	UPDATE_INSTALLED_LIST_DISPLAY,
	RESET_CATEGORIES,
	RESET_AVAILABLE_LIST_DISPLAY,
	TRIM_BOTTOM_AND_UPDATE_AVAILABLE_LIST_DISPLAY,
	TRIM_TOP_AND_UPDATE_AVAILABLE_LIST_DISPLAY,
	TRIM_APPEND_AND_UPDATE_AVAILABLE_LIST_DISPLAY,
	TRIM_PREPEND_AND_UPDATE_AVAILABLE_LIST_DISPLAY,
	APPEND_AND_UPDATE_AVAILABLE_LIST_DISPLAY,
	PREPEND_AND_UPDATE_AVAILABLE_LIST_DISPLAY,
	REFRESH_AVAILABLE_DISPLAY,
	RESET_UPDATABLE_LIST_DISPLAY,
	REFRESH_UPDATABLE_DISPLAY,
	SWITCH_AVAILABLE_TO_PROGRESSBAR,
	AVAILABLE_PROGRESS_SET_COMPLETION_TARGET,
	AVAILABLE_PROGRESS_UPDATE,
	AVAILABLE_PROGRESS_INDETERMINATE,
	SWITCH_AVAILABLE_TO_NO_APPS,
	SWITCH_AVAILABLE_TO_LIST,
	SWITCH_INSTALLED_TO_PROGRESSBAR,
	SWITCH_INSTALLED_TO_NO_APPS,
	SWITCH_INSTALLED_TO_LIST,
	SWITCH_UPDATABLE_TO_PROGRESSBAR,
	SWITCH_UPDATABLE_TO_NO_APPS,
	SWITCH_UPDATABLE_TO_LIST,
	HANDLE_MYAPP;
	
	public static EnumAptoideInterfaceTasks reverseOrdinal(int ordinal){
		return values()[ordinal];
	}
}
