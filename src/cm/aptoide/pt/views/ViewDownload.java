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
package cm.aptoide.pt.views;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * ViewDownload
 *
 * @author dsilveira
 *
 */
public class ViewDownload implements Parcelable{
	private String remotePath;
	
	private long progressTarget;
	private long progress;
	private int speedInKbps;
	
	private EnumDownloadStatus status;
	private EnumDownloadFailReason failReason;
	
	
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
		this.status = EnumDownloadStatus.SETTING_UP;
		this.failReason = EnumDownloadFailReason.NO_REASON;
	}
	
	
	public EnumDownloadStatus getStatus(){
		return status;
	}
	
	public void setStatus(EnumDownloadStatus status){
		this.status = status;
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
		if(progressTarget == 0){
			return 0;
		}else{
			return (int) (progress*100/progressTarget);			
		}
	}

	public void setProgress(long progress) {
		this.progress = progress;
	}
	
	public void incrementProgress(long progress){
		this.progress += progress;
	}
	
	public void setCompleted(){
		this.status = EnumDownloadStatus.COMPLETED;
	}
	
	public boolean isComplete(){
		return (status.equals(EnumDownloadStatus.COMPLETED));
	}
	
	public boolean isFailed(){
		return (status.equals(EnumDownloadStatus.FAILED));
	}
	
	public int getSpeedInKBps(){
		return speedInKbps;
	}
	
	public void setSpeedInKBps(int speedInKbps){
		this.speedInKbps = speedInKbps;
	}
	
	public void incrementSpeed(int speedInKbps){
		this.speedInKbps += speedInKbps;
	}

	public String getRemotePath() {
		return remotePath;
	}
	
	public void setRemotePath(String remoteUrl){
		this.remotePath = remoteUrl;
	}
	
	public String getRemotePathTail(){
		String[] remoteSplit = remotePath.split("/");
		return remoteSplit[remoteSplit.length-1];
	}
	
	public EnumDownloadFailReason getFailReason() {
		return failReason;
	}

	public void setFailReason(EnumDownloadFailReason failReason) {
		this.failReason = failReason;
		this.status = EnumDownloadStatus.FAILED;
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
		this.status = null;
		this.failReason = null;
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
		this.status = EnumDownloadStatus.SETTING_UP;
		this.failReason = EnumDownloadFailReason.NO_REASON;
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
		return " progress: "+getProgressPercentage()+" speed: "+speedInKbps+" remote: "+getRemotePathTail();
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
		out.writeInt(status.ordinal());
		out.writeInt(failReason.ordinal());
	}

	public void readFromParcel(Parcel in) {
		this.remotePath = in.readString();
		this.progressTarget = in.readLong();
		this.progress = in.readLong();
		this.speedInKbps = in.readInt();
		this.status = EnumDownloadStatus.reverseOrdinal(in.readInt());
		this.failReason = EnumDownloadFailReason.reverseOrdinal(in.readInt());
	}
	
}
