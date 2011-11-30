/**
 * ViewDisplayApplication,		part of Aptoide's data model
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

package cm.aptoide.pt.data.display;

import java.util.HashMap;

import cm.aptoide.pt.data.Constants;
import cm.aptoide.pt.data.model.ViewApplication;

 /**
 * ViewDisplayApplication, models a repository
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class ViewDisplayApplication {

	private HashMap<String, Object> map;
	
	

	/**
	 * ViewDisplayApplication available Constructor
	 *
	 * @param appHashid
	 * @param appName
	 * @param stars
	 * @param downloads
	 * @param upToDateVersionName
	 */
	public ViewDisplayApplication(int appHashid, String appName, float stars, int downloads, String upToDateVersionName) {
		this.map = new HashMap<String, Object>(Constants.NUMBER_OF_DISPLAY_FIELDS_APP_AVAILABLE);
		setAppHashid(appHashid);
		setAppName(appName);
		setStars(stars);
		setDownloads(downloads);
		setUpTodateVersionName(upToDateVersionName);
	}
	
	/**
	 * ViewDisplayApplication installed Constructor
	 *
	 * @param appHashid
	 * @param appName
	 * @param installedVersionName
	 * @param isUpdatable
	 * @param upToDateVersionName
	 * @param isDowngradable
	 * @param DowngradeVersionName
	 */
	public ViewDisplayApplication(int appHashid, String appName, String installedVersionName, boolean isUpdatable, String upToDateVersionName, boolean isDowngradable, String downgradeVersionName) {
		this.map = new HashMap<String, Object>(Constants.NUMBER_OF_DISPLAY_FIELDS_APP_INSTALLED);
		setAppHashid(appHashid);
		setAppName(appName);
		setInstalledVersionName(installedVersionName);
//		setIsUpdatable(isUpdatable);
		if(isUpdatable){
			setUpTodateVersionName(upToDateVersionName);
		}
//		setIsDowngradable(isDowngradable);
		if(isDowngradable){
			setIsDowngradable(isDowngradable);
//			setDowngradeVersionName(downgradeVersionName);
		}
	}

	/**
	 * ViewDisplayApplication update Constructor
	 *
	 * @param appHashid
	 * @param appName
	 * @param installedVersionName
	 * @param upToDateVersionName
	 */
	public ViewDisplayApplication(int appHashid, String appName, String installedVersionName, String upToDateVersionName) {
		this.map = new HashMap<String, Object>(Constants.NUMBER_OF_DISPLAY_FIELDS_APP_UPDATE);
		setAppHashid(appHashid);
		setAppName(appName);
		setInstalledVersionName(installedVersionName);
		setUpTodateVersionName(upToDateVersionName);
	}
	
	
	private void setAppHashid(int appHashid){
		this.map.put(Constants.KEY_APPLICATION_HASHID, appHashid);
		this.map.put(Constants.DISPLAY_APP_ICON_CACHE_PATH, Constants.PATH_CACHE_ICONS+appHashid);
	}
	
	public int getAppHashid() {
		return (Integer)this.map.get(Constants.KEY_APPLICATION_HASHID);
	}

	public String getIconCachePath() {
		return (String)this.map.get(Constants.DISPLAY_APP_ICON_CACHE_PATH);
	}
	
	private void setAppName(String appName){
		this.map.put(Constants.KEY_APPLICATION_NAME, appName);
	}

	public String getAppName() {
		return (String)this.map.get(Constants.KEY_APPLICATION_NAME);
	}
	
	private void setStars(float stars){
		this.map.put(Constants.KEY_STATS_STARS, stars);
	}

	public float getStars() {
		return (Float)this.map.get(Constants.KEY_STATS_STARS);
	}
	
	private void setDownloads(int downloads){
		this.map.put(Constants.KEY_STATS_DOWNLOADS, downloads);
	}

	public int getDownloads() {
		return (Integer)this.map.get(Constants.KEY_STATS_DOWNLOADS);
	}
	
	private void setInstalledVersionName(String installedVersionName){
		this.map.put(Constants.DISPLAY_APP_INSTALLED_VERSION_NAME, installedVersionName);
	}

	public String getInstalledVersionName() {
		return (String)this.map.get(Constants.DISPLAY_APP_INSTALLED_VERSION_NAME);
	}
	
	private void setIsUpdatable(boolean isUpdatable){
		this.map.put(Constants.DISPLAY_APP_IS_UPDATABLE, isUpdatable);
	}

	public boolean isUpdatable() {
		return (Boolean)this.map.get(Constants.DISPLAY_APP_IS_UPDATABLE);
	}
	
	private void setUpTodateVersionName(String upToDateVersionName){
		this.map.put(Constants.DISPLAY_APP_UP_TO_DATE_VERSION_NAME, upToDateVersionName);
	}

	public String getUpTodateVersionName() {
		return (String)this.map.get(Constants.DISPLAY_APP_UP_TO_DATE_VERSION_NAME);
	}
	
	private void setIsDowngradable(boolean isDowngradable){
		this.map.put(Constants.DISPLAY_APP_IS_DOWNGRADABLE, isDowngradable);
	}

	public boolean isDowngradeable() {
		return (Boolean)this.map.get(Constants.DISPLAY_APP_IS_DOWNGRADABLE);
	}
	
	private void setDowngradeVersionName(String downgradeVersionName){
		this.map.put(Constants.DISPLAY_APP_DOWNGRADE_VERSION_NAME, downgradeVersionName);
	}

	public String getDowngradeVersionName() {
		return (String)this.map.get(Constants.DISPLAY_APP_DOWNGRADE_VERSION_NAME);
	}
	
	
	
	public HashMap<String, Object> getDiplayMap(){
		return this.map;
	}
	

	/**
	 * ViewDisplayApplication object reuse clean references
	 *
	 * @param String uri
	 */
	public void clean(){
		this.map = null;
	}
	/**
	 * ViewDisplayApplication available object reuse reConstructor
	 *
	 * @param appHashid
	 * @param appName
	 * @param stars
	 * @param downloads
	 * @param upToDateVersionName
	 */
	public void reuse(int appHashid, String appName, float stars, int downloads, String upToDateVersionName) {
		this.map = new HashMap<String, Object>(Constants.NUMBER_OF_DISPLAY_FIELDS_APP_AVAILABLE);
		setAppHashid(appHashid);
		setAppName(appName);
		setStars(stars);
		setDownloads(downloads);
		setUpTodateVersionName(upToDateVersionName);
	}
	
	/**
	 * ViewDisplayApplication installed object reuse reConstructor
	 *
	 * @param appHashid
	 * @param appName
	 * @param installedVersionName
	 * @param isUpdatable
	 * @param upToDateVersionName
	 * @param isDowngradable
	 * @param downgradeVersionName
	 */
	public void reuse(int appHashid, String appName, String installedVersionName, boolean isUpdatable, String upToDateVersionName, boolean isDowngradable, String downgradeVersionName) {
		this.map = new HashMap<String, Object>(Constants.NUMBER_OF_DISPLAY_FIELDS_APP_INSTALLED);
		setAppHashid(appHashid);
		setAppName(appName);
		setInstalledVersionName(installedVersionName);
		setIsUpdatable(isUpdatable);
		setUpTodateVersionName(upToDateVersionName);
		setIsDowngradable(isDowngradable);
		setDowngradeVersionName(downgradeVersionName);
	}

	/**
	 * ViewDisplayApplication update object reuse reConstructor
	 *
	 * @param appHashid
	 * @param appName
	 * @param installedVersionName
	 * @param upToDateVersionName
	 */
	public void reuse(int appHashid, String appName, String installedVersionName, String upToDateVersionName) {
		this.map = new HashMap<String, Object>(Constants.NUMBER_OF_DISPLAY_FIELDS_APP_UPDATE);
		setAppHashid(appHashid);
		setAppName(appName);
		setInstalledVersionName(installedVersionName);
		setUpTodateVersionName(upToDateVersionName);
	}


	@Override
	public int hashCode() {
		return this.getAppHashid();
	}


	@Override
	public boolean equals(Object object) {
		if(object instanceof ViewApplication){
			ViewApplication app = (ViewApplication) object;
			if(app.hashCode() == this.hashCode()){
				return true;
			}
		}
		return false;
	}


	@Override
	public String toString() {
		return "AppHashid: "+getAppHashid()+" Name: "+getAppName()+" InstalledVersion: "+getInstalledVersionName()+" isDowngradable: "+isDowngradeable()+" DowngradeVersion: "+getDowngradeVersionName()+" isUpdatable: "+isUpdatable()+" UpToDateVersion: "+getUpTodateVersionName()+" Downloads: "+getDownloads()+" Stars: "+getStars() ;
	}
		
}
