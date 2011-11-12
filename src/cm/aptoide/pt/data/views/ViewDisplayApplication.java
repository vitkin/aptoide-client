/*
 * ViewDisplayApplication		part of Aptoide's data model
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

package cm.aptoide.pt.data.views;

import java.util.HashMap;

import cm.aptoide.pt.data.Constants;

 /**
 * ViewDisplayApplication, models a repository
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class ViewDisplayApplication {

	private HashMap<String, Object> map;
	private int appHashid;
	
	

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
	 * @param upToDateVersionName
	 * @param downgradeAvailable
	 */
	public ViewDisplayApplication(int appHashid, String appName, String installedVersionName, String upToDateVersionName, boolean downgradeAvailable) {
		this.map = new HashMap<String, Object>(Constants.NUMBER_OF_DISPLAY_FIELDS_APP_INSTALLED);
		setAppHashid(appHashid);
		setAppName(appName);
		setInstalledVersionName(installedVersionName);
		setUpTodateVersionName(upToDateVersionName);
		setDowngradeAvailable(downgradeAvailable);
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
		this.appHashid = appHashid;
		map.put(Constants.DISPLAY_APP_ICON_CACHE_PATH, Constants.PATH_CACHE_ICONS+appHashid);
	}
	
	public int getAppHashid() {
		return this.appHashid;
	}

	public String getIconCachePath() {
		return (String)map.get(Constants.DISPLAY_APP_ICON_CACHE_PATH);
	}
	
	private void setAppName(String appName){
		map.put(Constants.KEY_APPLICATION_NAME, appName);
	}

	public String getAppName() {
		return (String)map.get(Constants.KEY_APPLICATION_NAME);
	}
	
	private void setStars(float stars){
		map.put(Constants.KEY_STATS_STARS, stars);
	}

	public float getStars() {
		return (Float)map.get(Constants.KEY_STATS_STARS);
	}
	
	private void setDownloads(int downloads){
		map.put(Constants.KEY_STATS_DOWNLOADS, downloads);
	}

	public int getDownloads() {
		return (Integer)map.get(Constants.KEY_STATS_DOWNLOADS);
	}
	
	private void setUpTodateVersionName(String upToDateVersionName){
		map.put(Constants.DISPLAY_APP_UP_TO_DATE_VERSION_NAME, upToDateVersionName);
	}

	public String getUpTodateVersionName() {
		return (String)map.get(Constants.DISPLAY_APP_UP_TO_DATE_VERSION_NAME);
	}
	
	private void setInstalledVersionName(String installedVersionName){
		map.put(Constants.DISPLAY_APP_INSTALLED_VERSION_NAME, installedVersionName);
	}

	public String getInstalledVersionName() {
		return (String)map.get(Constants.DISPLAY_APP_INSTALLED_VERSION_NAME);
	}
	
	private void setDowngradeAvailable(boolean downgradeAvailable){
		map.put(Constants.DISPLAY_APP_DOWNGRADE_AVAILABLE, downgradeAvailable);
	}

	public boolean getDowngradeAvailable() {
		return (Boolean)map.get(Constants.DISPLAY_APP_DOWNGRADE_AVAILABLE);
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
		this.appHashid = 0;
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
	 * @param upToDateVersionName
	 * @param downgradeAvailable
	 */
	public void reuse(int appHashid, String appName, String installedVersionName, String upToDateVersionName, boolean downgradeAvailable) {
		this.map = new HashMap<String, Object>(Constants.NUMBER_OF_DISPLAY_FIELDS_APP_INSTALLED);
		setAppHashid(appHashid);
		setAppName(appName);
		setInstalledVersionName(installedVersionName);
		setUpTodateVersionName(upToDateVersionName);
		setDowngradeAvailable(downgradeAvailable);
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
		return this.getAppName();
	}
		
}
