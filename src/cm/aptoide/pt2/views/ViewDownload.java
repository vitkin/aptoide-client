/*
 * ViewDownload, part of Aptoide
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
package cm.aptoide.pt2.views;

import android.os.Parcel;
import android.os.Parcelable;
import cm.aptoide.pt2.ServiceManager;

/**
 * ViewDownload
 *
 * @author dsilveira
 *
 */
public class ViewDownload implements Parcelable{
	String remoteUrl;
	
	int progressTarget;
	int progress;
	int speedInKbps;
	
	
	
	/**
	 * 
	 * ViewDownload Constructor
	 *
	 * @param remoteUrl
	 * @param cache
	 */
	public ViewDownload(String remoteUrl) {
		this.progressTarget = 0;
		this.progress = 0;
		this.speedInKbps = 0;
		this.remoteUrl = remoteUrl;
	}

	public int getProgressTarget() {
		return progressTarget;
	}

	public void setProgressTarget(int progressTarget) {
		this.progressTarget = progressTarget;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}
	
	/**
	 * incrementProgress, increments progress
	 * 
	 * @param progress
	 * @return increase in percentage of total
	 */
	public int incrementProgress(int progress){
		int oldProgress = this.progress;
		this.progress += progress;
		int newProgress = this.progress;
		
		return ((newProgress - oldProgress)*100/progressTarget);
	}
	
	public int getSpeedInKbps(){
		return speedInKbps;
	}
	
	public void setSpeedInKbps(int speedInKbps){
		this.speedInKbps = speedInKbps;
	}

	public String getRemoteUrl() {
		return remoteUrl;
	}
	
	public void setRemoteUrl(String remoteUrl){
		this.remoteUrl = remoteUrl;
	}
	
	
	
	/**
	 * ViewDownload object reuse clean references
	 *
	 */
	public void clean(){
		this.progressTarget = 0;
		this.progress = 0;
		this.speedInKbps = 0;
		this.remoteUrl = null;
	}
	
	
	/**
	 * ViewDownload object reuse reConstructor
	 *
	 * @param remoteUrl
	 * @param appInfo
	 * @param cache
	 */
	public void reuse(ServiceManager serviceManager, String remoteUrl, ViewCache cache) {
		this.progressTarget = 0;
		this.progress = 0;	
		this.speedInKbps = 0;
		this.remoteUrl = remoteUrl;
	}


	@Override
	public int hashCode() {
		return this.remoteUrl.hashCode();
	}


	@Override
	public boolean equals(Object object) {
		if(object instanceof ViewDownload){
			ViewDownload download = (ViewDownload) object;
			if(download.hashCode() == this.hashCode()){
				return true;
			}
		}
		return false;
	}


	@Override
	public String toString() {
		return " remoteUrl: "+remoteUrl+" progress: "+progress+" speed: "+speedInKbps;
	}
	
	
	
	// Parcelable stuff //
	
	
	public static final Parcelable.Creator<ViewDownload> CREATOR = new Parcelable.Creator<ViewDownload>() {
		public ViewDownload createFromParcel(Parcel in) {
			return new ViewDownload(in);
		}

		public ViewDownload[] newArray(int size) {
			return new ViewDownload[size];
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

	protected ViewDownload(Parcel in){
		readFromParcel(in);
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(remoteUrl);
		out.writeInt(progressTarget);
		out.writeInt(progress);
		out.writeInt(speedInKbps);
	}

	public void readFromParcel(Parcel in) {
		this.remoteUrl = in.readString();
		this.progressTarget = in.readInt();
		this.progress = in.readInt();
		this.speedInKbps = in.readInt();
	}
	
}
