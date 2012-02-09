/**
 * ViewDisplayAppVersionInfo,		part of Aptoide's data model
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

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;
import cm.aptoide.pt.data.util.Constants;

 /**
 * ViewDisplayAppVersionInfo, models a single version of an application
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class ViewDisplayAppVersionInfo implements Parcelable, Serializable{

	private static final long serialVersionUID = -1659281166184815653L;
	private String appName;
	private String versionName;
	private int versionCode;
	private int appFullHashid;
	
	private String localIconPath;
	
	private boolean isInstalled = false;
	
	private boolean statsAvailable = false;
	private ViewDisplayAppVersionStats stats;
	
	private boolean extrasAvailable = false;
	private ViewDisplayAppVersionExtras extras;
	

	/**
	 * ViewDisplayAppVersionInfo Constructor
	 *
	 * @param String appName
	 * @param String versionName
	 * @param int versionCode
	 * @param int appFullHashid
	 * @param boolean isInstalled
	 */
	public ViewDisplayAppVersionInfo(String appName, String versionName, int versionCode, int appFullHashid, boolean isInstalled){
		this.appName = appName;
		this.versionName = versionName;
		this.versionCode = versionCode;
		this.appFullHashid = appFullHashid;
		this.isInstalled = isInstalled;
		
		this.statsAvailable = false;
		this.extrasAvailable = false;
	}
	
	

	public String getAppName() {
		return appName;
	}

	public String getVersionName() {
		return versionName;
	}

	public int getVersionCode() {
		return versionCode;
	}

	public int getAppFullHashid() {
		return appFullHashid;
	}

	public String getLocalIconPath() {
		return localIconPath;
	}
	
	public boolean isInstalled(){
		return isInstalled;
	}

	public boolean isStatsAvailable() {
		return statsAvailable;
	}

	public void setStats(ViewDisplayAppVersionStats stats) {
		this.stats = stats;
		this.statsAvailable = true;
	}

	public ViewDisplayAppVersionStats getStats() {
		return stats;
	}

	public boolean isExtrasAvailable() {
		return extrasAvailable;
	}

	public void setExtras(ViewDisplayAppVersionExtras extras) {
		this.extras = extras;
		this.extrasAvailable = true;
	}

	public ViewDisplayAppVersionExtras getExtras() {
		return extras;
	}



	/**
	 * ViewDisplayAppVersionInfo object reuse clean references
	 *
	 * @param String uri
	 */
	public void clean(){
		this.appName = null;
		this.versionName = null;
		this.versionCode = Constants.EMPTY_INT;
		this.appFullHashid = Constants.EMPTY_INT;
		
		this.localIconPath = null;
		
		this.isInstalled = false;
		
		this.statsAvailable = false;
		this.extrasAvailable = false;
	}
	
	/**
	 * ViewDisplayAppVersionInfo object reuse reConstructor
	 *
	 * @param String appName
	 * @param String versionName
	 * @param int versionCode
	 * @param int appFullHashid
	 * @param boolean isInstalled
	 */
	public void reuse(String appName, String versionName, int versionCode, int appFullHashid, boolean isInstalled){
		this.appName = appName;
		this.versionName = versionName;
		this.versionCode = versionCode;
		this.appFullHashid = appFullHashid;
		this.isInstalled = isInstalled;
		
		this.statsAvailable = false;
		this.extrasAvailable = false;
	}


	@Override
	public int hashCode() {
		return this.appFullHashid;
	}


	@Override
	public boolean equals(Object object) {
		if(object instanceof ViewDisplayAppVersionInfo){
			ViewDisplayAppVersionInfo app = (ViewDisplayAppVersionInfo) object;
			if(app.hashCode() == this.hashCode()){
				return true;
			}
		}
		return false;
	}


	@Override
	public String toString() {
		StringBuilder string = new StringBuilder(" Name: "+appName+" Version: "+versionName+" VersionCode: "+versionCode
														+" AppFullHashid: "+appFullHashid+" isInstalled: "+isInstalled);//+" localIconPath: "+localIconPath
		if(statsAvailable){
			string.append("\n\n"+stats.toString());
		}
		if(extrasAvailable){
			string.append("\n\n"+extras.toString());
		}
		return string.toString();
	}
	
	
	
	// Parcelable stuff //
	
	
		public static final Parcelable.Creator<ViewDisplayAppVersionInfo> CREATOR = new Parcelable.Creator<ViewDisplayAppVersionInfo>() {
			public ViewDisplayAppVersionInfo createFromParcel(Parcel in) {
				return new ViewDisplayAppVersionInfo(in);
			}

			public ViewDisplayAppVersionInfo[] newArray(int size) {
				return new ViewDisplayAppVersionInfo[size];
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

		private ViewDisplayAppVersionInfo(Parcel in){
			readFromParcel(in);
		}

		@Override
		public void writeToParcel(Parcel out, int flags) {
			out.writeString(appName);
			out.writeString(versionName);
			out.writeInt(versionCode);
			out.writeInt(appFullHashid);
			
			out.writeString(localIconPath);
			
			out.writeByte(isInstalled?(byte)1:(byte)0);
			
			out.writeByte(statsAvailable?(byte)1:(byte)0);
			if(statsAvailable){
				out.writeParcelable(stats, flags);
			}
			
			out.writeByte(extrasAvailable?(byte)1:(byte)0);
			if(extrasAvailable){
				out.writeParcelable(extras, flags);
			}
		}

		public void readFromParcel(Parcel in) {
			appName = in.readString();
			versionName = in.readString();
			versionCode = in.readInt();
			appFullHashid = in.readInt();
			
			localIconPath = in.readString();
			
			isInstalled = in.readByte()==1?true:false;
			
			statsAvailable = in.readByte()==1?true:false;
			if(statsAvailable){
				stats = in.readParcelable(cm.aptoide.pt.data.display.ViewDisplayAppVersionStats.class.getClassLoader());
			}
			
			extrasAvailable = in.readByte()==1?true:false;
			if(extrasAvailable){
				extras = in.readParcelable(cm.aptoide.pt.data.display.ViewDisplayAppVersionExtras.class.getClassLoader());
			}
		}
		
}
