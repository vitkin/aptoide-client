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

	protected int appHashid;
	protected String appName;
	protected String versionName;
	
	protected String iconCachePath;
	
	
	protected ViewDisplayApplication(){
		
	}

	/**
	 * ViewDisplayApplication Constructor
	 *
	 * @param appHashid
	 * @param appName
	 * @param versionName
	 */
	public ViewDisplayApplication(int appHashid, String appName, String versionName) {
		this.appHashid = appHashid;
		this.iconCachePath = Constants.PATH_CACHE_ICONS+appHashid;
		
		this.appName = appName;
		this.versionName = versionName;
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

	public String getVersionName() {
		return this.versionName;
	}
	
	

	/**
	 * ViewDisplayApplication object reuse clean references
	 *
	 * @param String uri
	 */
	public void clean(){
		this.appHashid = Constants.EMPTY_INT;
		this.appName = null;
		this.versionName = null;
		
		this.iconCachePath = null;
	}
	
	/**
	 * ViewDisplayApplication available object reuse reConstructor
	 *
	 * @param appHashid
	 * @param appName
	 * @param versionName
	 */
	public void reuse(int appHashid, String appName, String versionName) {
		this.appHashid = appHashid;
		this.iconCachePath = Constants.PATH_CACHE_ICONS+appHashid;
		
		this.appName = appName;
		this.versionName = versionName;
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
		return " AppHashid: "+appHashid+" Name: "+appName+"  VersionName: "+versionName;
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

	protected ViewDisplayApplication(Parcel in){
		readFromParcel(in);
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(appHashid);
		out.writeString(appName);
		out.writeString(versionName);
		out.writeString(iconCachePath);
	}

	public void readFromParcel(Parcel in) {
		appHashid = in.readInt();
		appName = in.readString();
		versionName = in.readString();
		iconCachePath = in.readString();
	}

}
