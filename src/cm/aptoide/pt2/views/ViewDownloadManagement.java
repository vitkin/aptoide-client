/*
 * ViewDownloadManagement, part of Aptoide
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

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import cm.aptoide.pt2.AIDLDownloadObserver;
import cm.aptoide.pt2.R;

/**
 * ViewDownloadManagement
 *
 * @author dsilveira
 *
 */
public class ViewDownloadManagement implements Parcelable{
	private AIDLDownloadObserver observer;
	
	private ViewApk appInfo;
	
	private ViewDownload viewDownload;
	private ViewCache cache;
	
	private boolean isLoginRequired;
	private ViewLogin login;
	
	private boolean isNull;
	
	
	
	/**
	 * 
	 * ViewDownloadManagement null object Constructor
	 *
	 */
	public ViewDownloadManagement(){
		this.isNull = true;
	}
	
	/**
	 * 
	 * ViewDownloadManagement Constructor
	 *
	 * @param url
	 * @param appInfo
	 * @param cache
	 */
	public ViewDownloadManagement(String remoteUrl, ViewApk appInfo, ViewCache cache) {
		this.isNull = false;
		this.viewDownload = new ViewDownload(remoteUrl);
		this.cache = cache;
		this.appInfo = appInfo;
	}
	
	/**
	 * 
	 * ViewDownloadManagement Constructor
	 *
	 * @param url
	 * @param appInfo
	 * @param cache
	 * @param login
	 */
	public ViewDownloadManagement(String remoteUrl, ViewApk appInfo, ViewCache cache, ViewLogin login) {
		this(remoteUrl, appInfo, cache);
		if(login != null){
			this.isLoginRequired = true;
			this.login = login;
		}
	}
	
	
	public boolean isNull(){
		return this.isNull;
	}

	/**
	 * getProgress, in percentage
	 * 
	 * @return percentage int
	 */
	public int getProgress() {
		return (viewDownload == null?0:viewDownload.getProgressPercentage());
	}

	/**
	 * getProgressString, in percentage
	 * 
	 * @return percentage string
	 */
	public String getProgressString() {
		return getProgress()+"%";
	}
	
	public void updateProgress(ViewDownload update){
		Log.d("Aptoide-ViewDownloadManagement", "*update* "+update+" downloadStatus: "+update.getStatus());
		this.viewDownload.setProgressTarget(update.getProgressTarget());
		this.viewDownload.setProgress(update.getProgress());
		this.viewDownload.setSpeedInKBps(update.getSpeedInKBps());
		this.viewDownload.setStatus(update.getStatus());
		if(viewDownload.getStatus().equals(EnumDownloadStatus.FAILED)){
			this.viewDownload.setFailReason(update.getFailReason());
		}
	}

//
//	/**
//	 * startDownloadManagement, starts the download of the apk for the app described within this view
//	 * 
//	 * @throws AptoideDownloadException (runtimeException)
//	 */
//	public void startDownload(){
//		serviceManager.startDownload(this);
//	}
//	public void pause(AIDLServiceDownloadManager.Stub serviceDownloadManagerCallReceiver){
//		viewDownload.setStatus(EnumDownloadStatus.PAUSED);
//		notifyObservers(EnumDownloadProgressUpdateMessages.PAUSED);
//		serviceManager.pauseDownload(hashCode());
//	}
//	
//	public void resume(){
//		viewDownload.setStatus(EnumDownloadStatus.RESUMING);
//		notifyObservers(EnumDownloadProgressUpdateMessages.RESUMING);
//		serviceManager.resumeDownload(hashCode());
//	}
//	
//	public void stop(){
//		viewDownload.setStatus(EnumDownloadStatus.STOPPED);
//		notifyObservers(EnumDownloadProgressUpdateMessages.STOPPED);
//		serviceManager.stopDownload(hashCode());
//	}
//	
//	public void restart(){
//		viewDownload.setStatus(EnumDownloadStatus.RESTARTING);
//		notifyObservers(EnumDownloadProgressUpdateMessages.RESTARTING);
//		serviceManager.restartDownload(hashCode());
//	}
	
	public EnumDownloadStatus getDownloadStatus(){
		return viewDownload.getStatus();
	}
	
	public int getSpeedInKBps(){
		return viewDownload.getSpeedInKBps();
	}
	
	public String getSpeedInKBpsString(Context context){
		switch (viewDownload.getStatus()) {
			case SETTING_UP:
				return context.getString(R.string.starting);
				
			case PAUSED:
				return context.getString(R.string.paused);
	
			case FAILED:
			case STOPPED:
				return context.getString(R.string.stopped);
				
			default:
				if(viewDownload.getSpeedInKBps() == 0){
//					return context.getString(R.string.slow);
					return "";
				}else{
					return viewDownload.getSpeedInKBps()+" KBps";
				}
		}
		
		
	}
	
	public boolean isComplete(){
		return viewDownload.isComplete();
	}
	
	public ViewDownload getDownload(){
		return this.viewDownload;
	}

	public ViewCache getCache() {
		return cache;
	}
	
	public void setCache(ViewCache cache){
		this.cache = cache;
	}

	public String getRemoteUrl() {
		return viewDownload.getRemotePath();
	}

	public ViewApk getAppInfo() {
		return appInfo;
	}
	
	public void setAppInfo(ViewApk appInfo){
		this.appInfo = appInfo;
	}
	
	public boolean isLoginRequired(){
		return isLoginRequired;
	}
	
	public ViewLogin getLogin() {
		return login;
	}

	public void setLogin(ViewLogin login) {
		if(login != null){
			this.isLoginRequired = true;
			this.login = login;
		}else{
			this.isLoginRequired = false;
			this.login = null;
		}
	}

	public void registerObserver(AIDLDownloadObserver observer){
		this.observer = observer;
	}

	public void unregisterObserver(){
		observer = null;
	}
	
	public AIDLDownloadObserver getObserver(){
		return observer;
	}

	
	
	
	/**
	 * ViewDownloadManagement object reuse clean references
	 *
	 */
	public void clean(){
		this.appInfo = null;
		this.cache = null;
	}
	
	/**
	 * ViewDownloadManagement object skeleton reuse reConstructor
	 *
	 */
	public void reuse() {
		this.isNull = true;
	}
	
	/**
	 * ViewDownloadManagement object reuse reConstructor
	 *
	 * @param remoteUrl
	 * @param appInfo
	 * @param cache
	 */
	public void reuse(String remoteUrl, ViewApk appInfo, ViewCache cache) {
		this.isNull = false;
		this.viewDownload = new ViewDownload(remoteUrl);
		this.cache = cache;
		this.appInfo = appInfo;
	}
	
	/**
	 * ViewDownloadManagement object reuse reConstructor
	 *
	 * @param remoteUrl
	 * @param appInfo
	 * @param cache
	 * @param login
	 */
	public void reuse(String remoteUrl, ViewApk appInfo, ViewCache cache, ViewLogin login) {
		reuse(remoteUrl, appInfo, cache);
		this.login = login;
	}


	@Override
	public int hashCode() {
		return this.appInfo.hashCode();
	}


	@Override
	public boolean equals(Object object) {
		if(object instanceof ViewDownloadManagement){
			ViewDownloadManagement download = (ViewDownloadManagement) object;
			if(download.hashCode() == this.hashCode()){
				return true;
			}
		}
		return false;
	}


	@Override
	public String toString() {
		return appInfo+" downloadStatus: "+viewDownload.getStatus()+viewDownload;
	}
	
	
	
	
	// Parcelable stuff //
	
	
	public static final Parcelable.Creator<ViewDownloadManagement> CREATOR = new Parcelable.Creator<ViewDownloadManagement>() {
		public ViewDownloadManagement createFromParcel(Parcel in) {
			return new ViewDownloadManagement(in);
		}

		public ViewDownloadManagement[] newArray(int size) {
			return new ViewDownloadManagement[size];
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

	protected ViewDownloadManagement(Parcel in){
		readFromParcel(in);
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeParcelable(appInfo, flags);
		out.writeParcelable(viewDownload, flags);
		out.writeParcelable(cache, flags);
		out.writeValue(isLoginRequired);
		if(isLoginRequired){
			out.writeParcelable(login, flags);
		}
		out.writeValue(isNull);
	}
	
	public void readFromParcel(Parcel in) {
		this.appInfo = in.readParcelable(ViewApk.class.getClassLoader());
		this.viewDownload = in.readParcelable(ViewDownload.class.getClassLoader());
		this.cache = in.readParcelable(ViewCache.class.getClassLoader());
		this.isLoginRequired = (Boolean) in.readValue(null);
		if(isLoginRequired){
			in.readParcelable(ViewLogin.class.getClassLoader());
		}
		this.isNull = (Boolean) in.readValue(null);
	}
	
}
