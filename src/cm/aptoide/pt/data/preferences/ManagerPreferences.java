/**
 * ManagerPreferences,		auxilliary class to Aptoide's ServiceData
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

package cm.aptoide.pt.data.preferences;

import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;
import cm.aptoide.pt.EnumAppsSorting;
import cm.aptoide.pt.data.EnumConnectionLevels;
import cm.aptoide.pt.data.AptoideServiceData;
import cm.aptoide.pt.data.ViewClientStatistics;
import cm.aptoide.pt.data.downloads.EnumIconDownloadsPermission;
import cm.aptoide.pt.data.downloads.ViewIconDownloadPermissions;
import cm.aptoide.pt.data.system.ViewScreenDimensions;
import cm.aptoide.pt.data.util.Constants;
import cm.aptoide.pt.debug.AptoideLog;
import cm.aptoide.pt.debug.InterfaceAptoideLog;

/**
 * ManagerPreferences, manages aptoide's preferences I/O
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class ManagerPreferences implements InterfaceAptoideLog{
	
	private final String TAG = "Aptoide-ServiceData-ManagerPreferences";
	private SharedPreferences getPreferences;
	private SharedPreferences.Editor setPreferences;


	@Override
	public String getTag() {
		return TAG;
	}

	public ManagerPreferences(AptoideServiceData serviceData) {
		getPreferences = serviceData.getSharedPreferences(Constants.FILE_PREFERENCES, Context.MODE_PRIVATE);
		setPreferences = getPreferences.edit();
		AptoideLog.v(this, "gotSharedPreferences: "+Constants.FILE_PREFERENCES);
		if(getAptoideClientUUID() == null){
			setAptoideClientUUID( UUID.randomUUID().toString() );
		}
		
		if(getAuthorizedDownloadConnections() == EnumConnectionLevels.NONE){
			setAuthorizedDownloadConnections(EnumConnectionLevels.OTHER);
		}
	}

	
	public SharedPreferences getPreferences() {
		return getPreferences;
	}

	public SharedPreferences.Editor setPreferences() {
		return setPreferences;
	}

	
	private void setAptoideClientUUID(String uuid){
		setPreferences.putString(EnumPreferences.APTOIDE_CLIENT_UUID.name(), uuid);
		setPreferences.commit();
	}
	
	public String getAptoideClientUUID(){
		return getPreferences.getString(EnumPreferences.APTOIDE_CLIENT_UUID.name(), null);
	}
	
	public void setScreenDimensions(ViewScreenDimensions screenDimensions){
		setPreferences.putInt(EnumPreferences.SCREEN_WIDTH.name(), screenDimensions.getWidth());
		setPreferences.putInt(EnumPreferences.SCREEN_HEIGHT.name(), screenDimensions.getHeight());
		setPreferences.putFloat(EnumPreferences.SCREEN_DENSITY.name(), screenDimensions.getDensity());
		setPreferences.commit();
	}
	
	public ViewScreenDimensions getScreenDimensions(){
		return new ViewScreenDimensions(getPreferences.getInt(EnumPreferences.SCREEN_WIDTH.name(), Constants.NO_SCREEN), getPreferences.getInt(EnumPreferences.SCREEN_HEIGHT.name(), Constants.NO_SCREEN), getPreferences.getFloat(EnumPreferences.SCREEN_DENSITY.name(), Constants.NO_SCREEN));
	}
	
	public void completeStatistics(ViewClientStatistics statistics){
		statistics.completeStatistics(getAptoideClientUUID(), getScreenDimensions());
	}
	
	public void setAuthorizedDownloadConnections(EnumConnectionLevels connectionLevel){
		setPreferences.putInt(EnumPreferences.AUTHORIZED_DOWNLOAD_CONNECTIONS.name(), connectionLevel.ordinal());
		setPreferences.commit();
	}
	
	public EnumConnectionLevels getAuthorizedDownloadConnections(){
		return EnumConnectionLevels.reverseOrdinal(getPreferences.getInt(EnumPreferences.AUTHORIZED_DOWNLOAD_CONNECTIONS.name(), EnumConnectionLevels.NONE.ordinal()));
	}
	
	public boolean getShowApplicationsByCategory(){
		return getPreferences.getBoolean(EnumPreferences.SHOW_APPLICATIONS_BY_CATEGORY.name(), false);
	}
	
	public void setShowApplicationsByCategory(boolean byCategory){
		setPreferences.putBoolean(EnumPreferences.SHOW_APPLICATIONS_BY_CATEGORY.name(), byCategory);
		setPreferences.commit();
	}
	
	public int getAppsSortingPolicy(){
		return getPreferences.getInt(EnumPreferences.SORT_APPLICATIONS_BY.name(), EnumAppsSorting.ALPHABETIC.ordinal());
	}
	
	public void setAppsSortingPolicy(int sortingPolicy){
		setPreferences.putInt(EnumPreferences.SORT_APPLICATIONS_BY.name(), sortingPolicy);
		setPreferences.commit();
	}
	
	public void setHwFilter(boolean on){
		setPreferences.putBoolean(EnumPreferences.IS_HW_FILTER_ON.name(), on);
		setPreferences.commit();		
	}
	
	public boolean isHwFilterOn(){
		return getPreferences.getBoolean(EnumPreferences.IS_HW_FILTER_ON.name(), false);
	}
	
	public void setIconDownloadPermissions(ViewIconDownloadPermissions iconDownloadPermissions){
		setPreferences.putBoolean(EnumPreferences.DOWNLOAD_ICONS_.name()+EnumIconDownloadsPermission.WIFI.name(), iconDownloadPermissions.isWiFi());
		setPreferences.putBoolean(EnumPreferences.DOWNLOAD_ICONS_.name()+EnumIconDownloadsPermission.ETHERNET.name(), iconDownloadPermissions.isEthernet());
		setPreferences.putBoolean(EnumPreferences.DOWNLOAD_ICONS_.name()+EnumIconDownloadsPermission.WIMAX.name(), iconDownloadPermissions.isWiMax());
		setPreferences.putBoolean(EnumPreferences.DOWNLOAD_ICONS_.name()+EnumIconDownloadsPermission.MOBILE.name(), iconDownloadPermissions.isMobile());
		setPreferences.commit();		
	}
	
	public ViewIconDownloadPermissions getIconDownloadPermissions(){
		ViewIconDownloadPermissions permissions = new ViewIconDownloadPermissions(
													getPreferences.getBoolean(EnumPreferences.DOWNLOAD_ICONS_.name()+EnumIconDownloadsPermission.WIFI.name(), true)
													, getPreferences.getBoolean(EnumPreferences.DOWNLOAD_ICONS_.name()+EnumIconDownloadsPermission.ETHERNET.name(), true)
													, getPreferences.getBoolean(EnumPreferences.DOWNLOAD_ICONS_.name()+EnumIconDownloadsPermission.WIMAX.name(), true)
													, getPreferences.getBoolean(EnumPreferences.DOWNLOAD_ICONS_.name()+EnumIconDownloadsPermission.MOBILE.name(), true) );
		return permissions;
	}
	
	public void setAutomaticInstall(boolean on){
		setPreferences.putBoolean(EnumPreferences.AUTOMATIC_INSTALL.name(), on);
		setPreferences.commit();		
	}
	
	public boolean isAutomaticInstallOn(){
		return getPreferences.getBoolean(EnumPreferences.AUTOMATIC_INSTALL.name(), false);
	}
	
	
	public ViewSettings getSettings(){
		return new ViewSettings(getIconDownloadPermissions(), isHwFilterOn(), isAutomaticInstallOn());
	}
	
}
