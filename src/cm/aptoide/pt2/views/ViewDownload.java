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
import cm.aptoide.pt2.ApplicationServiceManager;

/**
 * ViewDownload
 *
 * @author dsilveira
 *
 */
public class ViewDownload implements Parcelable{
	String remotePath;
	
	long progressTarget;
	long progress;
	int speedInKbps;
	
	
	
	/**
	 * 
	 * ViewDownload Constructor
	 *
	 * @param remoteUrl
	 */
	public ViewDownload(String remoteUrl) {
		this.progressTarget = 0;
		this.progress = 0;
		this.speedInKbps = 0;
		this.remotePath = remoteUrl;
	}

	public long getProgressTarget() {
		return progressTarget;
	}

	public void setProgressTarget(long progressTarget) {
		this.progressTarget = progressTarget;
	}

	public long getProgress() {
		return progress;
	}
	
	public int getProgressPercentage(){
		return (int) (progress*100/progressTarget);
	}

	public void setProgress(long progress) {
		this.progress = progress;
	}
	
	public void incrementProgress(long progress){
		this.progress += progress;
	}
	
	public void setCompleted(){
		this.progress = this.progressTarget;
	}
	
	public int getSpeedInKbps(){
		return speedInKbps;
	}
	
	public void setSpeedInKbps(int speedInKbps){
		this.speedInKbps = speedInKbps;
	}

	public String getRemotePath() {
		return remotePath;
	}
	
	public void setRemotePath(String remoteUrl){
		this.remotePath = remoteUrl;
	}
	
	
	
	/**
	 * ViewDownload object reuse clean references
	 *
	 */
	public void clean(){
		this.progressTarget = 0;
		this.progress = 0;
		this.speedInKbps = 0;
		this.remotePath = null;
	}
	
	
	/**
	 * ViewDownload object reuse reConstructor
	 *
	 * @param remoteUrl
	 */
	public void reuse(String remoteUrl) {
		this.progressTarget = 0;
		this.progress = 0;	
		this.speedInKbps = 0;
		this.remotePath = remoteUrl;
	}


	@Override
	public int hashCode() {
		return this.remotePath.hashCode();
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
		return " remoteUrl: "+remotePath+" progress: "+progress+" speed: "+speedInKbps;
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
		out.writeString(remotePath);
		out.writeLong(progressTarget);
		out.writeLong(progress);
		out.writeInt(speedInKbps);
	}

	public void readFromParcel(Parcel in) {
		this.remotePath = in.readString();
		this.progressTarget = in.readLong();
		this.progress = in.readLong();
		this.speedInKbps = in.readInt();
	}
	
}
