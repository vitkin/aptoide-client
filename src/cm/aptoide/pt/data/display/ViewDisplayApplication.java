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

import android.os.Parcel;
import android.os.Parcelable;
import cm.aptoide.pt.data.util.Constants;

 /**
 * ViewDisplayApplication, models an Application
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class ViewDisplayApplication implements Parcelable{

	private int appHashid;
	private String appName;
	private boolean isInstalled;
	private String installedVersionName;
	private boolean isUpdatable;
	private String upToDateVersionName;
	private boolean isDowngradable;
	private String downgradeVersionName;
	
	private String iconCachePath;
	private float stars;
	private int downloads;
	
	

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
		this.appHashid = appHashid;
		this.iconCachePath = Constants.PATH_CACHE_ICONS+appHashid;
		
		this.appName = appName;
		this.stars = stars;
		this.downloads = downloads;
		this.upToDateVersionName = upToDateVersionName;
				
		this.isInstalled = false;
		this.isUpdatable = false;
		this.isDowngradable = false;
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
		this.appHashid = appHashid;
		this.iconCachePath = Constants.PATH_CACHE_ICONS+appHashid;
		
		this.appName = appName;
		this.isInstalled = true;
		this.installedVersionName = installedVersionName;
		if( (this.isUpdatable = isUpdatable) ){
			this.upToDateVersionName = upToDateVersionName;
		}
		if( (this.isDowngradable = isDowngradable) ){
			this.downgradeVersionName = downgradeVersionName;
		}
				
		this.stars = Constants.EMPTY_INT;
		this.downloads = Constants.EMPTY_INT;
	}

	/**
	 * ViewDisplayApplication update Constructor
	 *
	 * @param appHashid
	 * @param appName
	 * @param installedVersionName
	 * @param upToDateVersionName
	 */
	public ViewDisplayApplication(int appHashid, String appName, String installedVersionName, String upToDateVersionName, float updatestars, int updateDownloads) {
		this.appHashid = appHashid;
		this.iconCachePath = Constants.PATH_CACHE_ICONS+appHashid;
		
		this.appName = appName;
		this.isInstalled = true;
		this.installedVersionName = installedVersionName;
		this.isUpdatable = true;
		this.upToDateVersionName = upToDateVersionName;
		this.stars = updatestars;
		this.downloads = updateDownloads;
		
		this.isDowngradable = false;
	}
	
	public int getAppHashid() {
		return this.appHashid;
	}

	public String getIconCachePath() {
		return this.iconCachePath;
	}

	public String getAppName() {
		return this.appName;
	}

	public float getStars() {
		return this.stars;
	}

	public int getDownloads() {
		return this.downloads;
	}
	
	public boolean isInstalled(){
		return this.isInstalled;
	}

	public String getInstalledVersionName() {
		return this.installedVersionName;
	}

	public boolean isUpdatable() {
		return this.isUpdatable;
	}

	public String getUpTodateVersionName() {
		return this.upToDateVersionName;
	}

	public boolean isDowngradeable() {
		return this.isDowngradable;
	}

	public String getDowngradeVersionName() {
		return this.downgradeVersionName;
	}
	
	

	/**
	 * ViewDisplayApplication object reuse clean references
	 *
	 * @param String uri
	 */
	public void clean(){
		this.appHashid = Constants.EMPTY_INT;
		this.appName = null;
		this.isInstalled = false;
		this.installedVersionName = null;
		this.isUpdatable = false;
		this.upToDateVersionName = null;
		this.isDowngradable = false;
		this.downgradeVersionName = null;
		
		this.iconCachePath = null;
		this.stars = Constants.EMPTY_INT;
		this.downloads = Constants.EMPTY_INT;
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
		this.appHashid = appHashid;
		this.iconCachePath = Constants.PATH_CACHE_ICONS+appHashid;
		
		this.appName = appName;
		this.stars = stars;
		this.downloads = downloads;
		this.upToDateVersionName = upToDateVersionName;
				
		this.isInstalled = false;
		this.isUpdatable = false;
		this.isDowngradable = false;
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
		this.appHashid = appHashid;
		this.iconCachePath = Constants.PATH_CACHE_ICONS+appHashid;
		
		this.appName = appName;
		this.isInstalled = true;
		this.installedVersionName = installedVersionName;
		if( (this.isUpdatable = isUpdatable) ){
			this.upToDateVersionName = upToDateVersionName;
		}
		if( (this.isDowngradable = isDowngradable) ){
			this.downgradeVersionName = downgradeVersionName;
		}
				
		this.stars = Constants.EMPTY_INT;
		this.downloads = Constants.EMPTY_INT;
	}

	/**
	 * ViewDisplayApplication update object reuse reConstructor
	 *
	 * @param appHashid
	 * @param appName
	 * @param installedVersionName
	 * @param upToDateVersionName
	 */
	public void reuse(int appHashid, String appName, String installedVersionName, String upToDateVersionName, float updatestars, int updateDownloads) {
		this.appHashid = appHashid;
		this.iconCachePath = Constants.PATH_CACHE_ICONS+appHashid;
		
		this.appName = appName;
		this.isInstalled = true;
		this.installedVersionName = installedVersionName;
		this.isUpdatable = true;
		this.upToDateVersionName = upToDateVersionName;
		this.stars = updatestars;
		this.downloads = updateDownloads;
		
		this.isDowngradable = false;
	}


	@Override
	public int hashCode() {
		return this.appHashid;
	}


	@Override
	public boolean equals(Object object) {
		if(object instanceof ViewDisplayApplication){
			ViewDisplayApplication app = (ViewDisplayApplication) object;
			if(app.hashCode() == this.hashCode()){
				return true;
			}
		}
		return false;
	}


	@Override
	public String toString() {
		StringBuilder description = new StringBuilder("AppHashid: "+appHashid+" Name: "+appName);
		if(isInstalled){
			description.append(" InstalledVersion: "+installedVersionName);
		}else{
			description.append(" Downloads: "+downloads+" Stars: "+stars);
		}
		if(isUpdatable){
			description.append(" UpToDateVersion: "+upToDateVersionName);
		}
		if(isDowngradable){
			description.append(" DowngradeVersion: "+downgradeVersionName);
		}
		
		return description.toString();
	}
	
	
	
	// Parcelable stuff //
	
	
	public static final Parcelable.Creator<ViewDisplayApplication> CREATOR = new Parcelable.Creator<ViewDisplayApplication>() {
		public ViewDisplayApplication createFromParcel(Parcel in) {
			return new ViewDisplayApplication(in);
		}

		public ViewDisplayApplication[] newArray(int size) {
			return new ViewDisplayApplication[size];
		}
	};

	/** 
	 * we're annoyingly forced to create this even if we clearly don't need it,
	 *  so we just use the default return 0
	 *  
	 *  @return 0
	 */
	@Override
	public int describeContents() {
		return 0;
	}

	private ViewDisplayApplication(Parcel in){
		readFromParcel(in);
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(appHashid);
		out.writeString(appName);
		out.writeValue(isInstalled);
		out.writeString(installedVersionName);
		out.writeValue(isUpdatable);
		out.writeString(upToDateVersionName);
		out.writeValue(isDowngradable);
		out.writeString(downgradeVersionName);
		out.writeString(iconCachePath);
		out.writeFloat(stars);
		out.writeInt(downloads);
	}

	public void readFromParcel(Parcel in) {
		appHashid = in.readInt();
		appName = in.readString();
		isInstalled = (Boolean) in.readValue(null);
		installedVersionName = in.readString();
		isUpdatable = (Boolean) in.readValue(null);
		upToDateVersionName = in.readString();
		isDowngradable = (Boolean) in.readValue(null);
		downgradeVersionName = in.readString();
		iconCachePath = in.readString();
		stars = in.readFloat();
		downloads = in.readInt();
	}

}
