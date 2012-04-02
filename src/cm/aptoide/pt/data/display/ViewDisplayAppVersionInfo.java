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
	private int appHashid;
	private int size = Constants.EMPTY_INT;

	private int repoHashid = Constants.EMPTY_INT;
	private String repoUri = null;
	
	private boolean isInstalled = false;
	private boolean isScheduled = false;
	
	private boolean statsAvailable = false;
	private ViewDisplayAppVersionStats stats;
	
	private boolean extrasAvailable = false;
	private ViewDisplayAppVersionExtras extras;
	
	private boolean commentsAvailable = false;
	private ViewDisplayListComments comments;
	

	/**
	 * ViewDisplayAppVersionInfo Constructor
	 *
	 * @param String appName
	 * @param String versionName
	 * @param int versionCode
	 * @param int appFullHashid
	 * @param int appHashid
	 * @param boolean isInstalled
	 * @param boolean isScheduled
	 */
	public ViewDisplayAppVersionInfo(String appName, String versionName, int versionCode, int appFullHashid, int appHashid, boolean isInstalled, boolean isScheduled){
		this.appName = appName;
		this.versionName = versionName;
		this.versionCode = versionCode;
		this.appFullHashid = appFullHashid;
		this.appHashid = appHashid;
		
		this.isInstalled = isInstalled;
		this.isScheduled = isScheduled;
		
		this.statsAvailable = false;
		this.extrasAvailable = false;
		this.commentsAvailable = false;
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

	public int getAppHashid() {
		return appHashid;
	}
	
	public void setSize(int size) {
		this.size = size;
	}

	public int getSize() {
		return size;
	}
	
	public void setRepoInfo(int repoHashid, String repoUri){
		this.repoHashid = repoHashid;
		this.repoUri = repoUri;
	}

	public int getRepoHashid() {
		return repoHashid;
	}

	public String getRepoUri() {
		return repoUri;
	}

	public boolean isInstalled(){
		return isInstalled;
	}
	
	public void setIsScheduled(boolean isScheduled){
		this.isScheduled = isScheduled;
	}
	
	public boolean isScheduled(){
		return isScheduled;
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
	
	public boolean isCommentsAvailable(){
		return commentsAvailable;
	}

	public void setComments(ViewDisplayListComments comments) {
		this.comments = comments;
		this.commentsAvailable = true;
	}

	public ViewDisplayListComments getComments() {
		return comments;
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
		this.appHashid = Constants.EMPTY_INT;
		this.size = Constants.EMPTY_INT;
		this.repoHashid = Constants.EMPTY_INT;
		this.repoUri = null;
		
		this.isInstalled = false;
		this.isScheduled = false;
		
		this.statsAvailable = false;
		this.stats = null;
		this.extrasAvailable = false;
		this.extras = null;
		this.commentsAvailable = false;
		this.comments = null;
	}
	
	/**
	 * ViewDisplayAppVersionInfo object reuse reConstructor
	 *
	 * @param String appName
	 * @param String versionName
	 * @param int versionCode
	 * @param int appFullHashid
	 * @param int appHashid
	 * @param boolean isInstalled
	 */
	public void reuse(String appName, String versionName, int versionCode, int appFullHashid, int appHashid, boolean isInstalled, boolean isScheduled){
		this.appName = appName;
		this.versionName = versionName;
		this.versionCode = versionCode;
		this.appFullHashid = appFullHashid;
		this.appHashid = appHashid;
		
		this.isInstalled = isInstalled;
		this.isScheduled = isScheduled;
		
		this.statsAvailable = false;
		this.extrasAvailable = false;
		this.commentsAvailable = false;
	}


	@Override
	public int hashCode() {
		return this.appHashid;
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
		StringBuilder string = new StringBuilder(" Name: "+appName+" Version: "+versionName+" VersionCode: "+versionCode+" AppFullHashid: "+appFullHashid+" AppHashid: "+appHashid
												+" Size: "+size+" RepoUri: "+repoUri+" isInstalled: "+isInstalled+" isScheduled: "+isScheduled);//+" localIconPath: "+localIconPath
		if(statsAvailable){
			string.append("\n"+stats);
		}
		if(extrasAvailable){
			string.append("\n"+extras);
		}
		if(commentsAvailable){
			string.append("\n"+comments);
		}
		string.append("\n\n");
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
			out.writeInt(appHashid);
			out.writeInt(size);
			out.writeInt(repoHashid);
			out.writeString(repoUri);
			
			out.writeValue(isInstalled);
			out.writeValue(isScheduled);

			out.writeValue(statsAvailable);
			if(statsAvailable){
				out.writeParcelable(stats, flags);
			}

			out.writeValue(extrasAvailable);
			if(extrasAvailable){
				out.writeParcelable(extras, flags);
			}
			
			out.writeValue(commentsAvailable);
			if(commentsAvailable){
				out.writeParcelable(comments, flags);
			}
		}

		public void readFromParcel(Parcel in) {
			appName = in.readString();
			versionName = in.readString();
			versionCode = in.readInt();
			appFullHashid = in.readInt();
			appHashid = in.readInt();
			size = in.readInt();
			repoHashid = in.readInt();
			repoUri = in.readString();
			
			isInstalled = (Boolean) in.readValue(null);
			isScheduled = (Boolean) in.readValue(null);
			
			statsAvailable = (Boolean) in.readValue(null);
			if(statsAvailable){
				stats = in.readParcelable(cm.aptoide.pt.data.display.ViewDisplayAppVersionStats.class.getClassLoader());
			}
			
			extrasAvailable = (Boolean) in.readValue(null);
			if(extrasAvailable){
				extras = in.readParcelable(cm.aptoide.pt.data.display.ViewDisplayAppVersionExtras.class.getClassLoader());
			}
			
			commentsAvailable = (Boolean) in.readValue(null);
			if(commentsAvailable){
				comments = in.readParcelable(cm.aptoide.pt.data.display.ViewDisplayListComments.class.getClassLoader());
			}
			
		}
		
}
