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
package cm.aptoide.pt.views;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.Log;
import cm.aptoide.pt.AIDLDownloadObserver;
import cm.aptoide.pt.ApplicationAptoide;
import cm.aptoide.pt.R;

/**
 * ViewDownloadManagement
 *
 * @author dsilveira
 *
 */
public class ViewDownloadManagement implements Parcelable{

    private boolean isObb = false;
    //    private ViewObb obb;
    private AIDLDownloadObserver observer;

	private ViewApk appInfo;

	private ViewDownload viewDownload;

	private ViewCache cache;

	private boolean isLoginRequired;
	private ViewLogin login;

	private boolean isNull;

    public ViewObb getObb() {
        return obb;
    }

    public ViewDownloadManagement getMainObb() {
        return mainObb;
    }

    private ViewDownloadManagement parent;
    private ViewDownloadManagement mainObb;
    private ViewDownloadManagement patchObb;

    private ViewObb obb;


    /**
	 *
	 * ViewDownloadManagement null object Constructor
	 *
	 */
	public ViewDownloadManagement(){
		this.isNull = true;
        this.viewDownload = new ViewDownload("");
    }

	/**
	 *
	 * ViewDownloadManagement Constructor
	 *
	 * @param remoteUrl
	 * @param appInfo
	 * @param cache
	 */
	public ViewDownloadManagement(String remoteUrl, ViewApk appInfo, ViewCache cache, ViewObb obb) {



		this.isNull = false;
		this.viewDownload = new ViewDownload(remoteUrl);

        if(obb!=null){
            this.obb = obb;

            ViewApk obbInfo = new ViewApk(obb.getMainCache().getAppHashId(), appInfo.getApkid()+".obb", appInfo.getName() + " - " + ApplicationAptoide.getContext().getString(R.string.main_obb), appInfo.getVercode(), "", "", "", "", "", appInfo.getRepo_id());
            this.mainObb = new ViewDownloadManagement(obb.getMainUrl(), obbInfo, obb.getMainCache(), null, true);
            mainObb.setParent(this);

            if(obb.getPatchCache()!=null){
                obbInfo = new ViewApk(obb.getPatchCache().getAppHashId(), appInfo.getApkid()+".obb2", appInfo.getName() + " -" + ApplicationAptoide.getContext().getString(R.string.patch_obb), appInfo.getVercode(), "", "", "", "", "", appInfo.getRepo_id());
                this.patchObb = new ViewDownloadManagement(obb.getPatchUrl(), obbInfo, obb.getPatchCache(), null, true);
                patchObb.setParent(this);
            }


        }

		this.cache = cache;
		this.appInfo = appInfo;



	}

	/**
	 *
	 * ViewDownloadManagement Constructor
	 *
	 * @param remoteUrl
	 * @param appInfo
	 * @param cache
	 * @param login
	 */
	public ViewDownloadManagement(String remoteUrl, ViewApk appInfo, ViewCache cache, ViewLogin login, ViewObb obb) {
		this(remoteUrl, appInfo, cache, obb);
		if(login != null){
			this.isLoginRequired = true;
			this.login = login;
		}
	}

    public ViewDownloadManagement(String remoteUrl, ViewApk appInfo, ViewCache cache, ViewLogin login, boolean isObb) {
        this(remoteUrl, appInfo, cache, null);
        this.isObb = isObb;
        if(login != null){
            this.isLoginRequired = true;
            this.login = login;
        }
    }


	public boolean isNull(){
        return isNull;

	}

	/**
	 * getProgress, in percentage
	 *
	 * @return percentage int
	 */
	public int getProgress() {

        if(mainObb!=null && patchObb!=null){

            return (viewDownload.getProgressPercentage() + mainObb.getDownload().getProgressPercentage() + patchObb.getDownload().getProgressPercentage())/3;
        }else if(mainObb!=null){
            return (viewDownload.getProgressPercentage() + mainObb.getDownload().getProgressPercentage())/2;
        }

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

        if(parent!=null){
            if(parent.getDownloadStatus().equals(EnumDownloadStatus.COMPLETED)){
                parent.updateProgress(parent.getDownload());
            }

        }

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

        if(mainObb!=null && !mainObb.getDownloadStatus().equals(EnumDownloadStatus.COMPLETED)  && !patchObb.getDownloadStatus().equals(EnumDownloadStatus.PAUSED)){
            return mainObb.getDownloadStatus();
        }

        if(patchObb!=null && !patchObb.getDownloadStatus().equals(EnumDownloadStatus.COMPLETED) && !patchObb.getDownloadStatus().equals(EnumDownloadStatus.PAUSED)){
            return patchObb.getDownloadStatus();
        }

		return viewDownload.getStatus();
	}

	public int getSpeedInKBps(){

        if(mainObb!=null && patchObb!=null){

            return (viewDownload.getSpeedInKBps() + mainObb.getDownload().getSpeedInKBps() + patchObb.getDownload().getSpeedInKBps())/3;
        }else if(mainObb!=null){
            return (viewDownload.getSpeedInKBps() + mainObb.getDownload().getSpeedInKBps())/2;
        }


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

//        if(isObb){
//            return ((ViewCacheObb)cache).getParentCache();
//        }
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

	public void registerObserver(AIDLDownloadObserver observer) throws RemoteException {
		this.observer = observer;

        if(mainObb!=null){

            mainObb.registerObserver(observer);
//            observer.updateDownloadStatus(mainObb.getDownload());
        }

        if(patchObb!=null){

            patchObb.registerObserver(observer);
//            observer.updateDownloadStatus(patchObb.getDownload());
        }

        observer.updateDownloadStatus(viewDownload);


	}

	public void unregisterObserver(){
		observer = null;

        if(mainObb!=null){

            mainObb.unregisterObserver();

        }

        if(patchObb!=null){

            patchObb.unregisterObserver();

        }

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
	public void reuse(String remoteUrl, ViewApk appInfo, ViewCache cache, ViewObb obb) {
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
	public void reuse(String remoteUrl, ViewApk appInfo, ViewCache cache, ViewLogin login, ViewObb obb) {
		reuse(remoteUrl, appInfo, cache, obb);
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
		if(isNull){
			return "isNull";
		}else{
			return appInfo+" downloadStatus: "+viewDownload.getStatus()+viewDownload;
		}
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
        out.writeParcelable(mainObb,flags);
        out.writeParcelable(patchObb,flags);
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
        this.mainObb = in.readParcelable(ViewDownload.class.getClassLoader());
        this.patchObb = in.readParcelable(ViewDownload.class.getClassLoader());
	}


    public boolean isObb() {
        return isObb;
    }

    public boolean isInstall() {

        boolean mainObbCompleted , patchObbCompleted = true;

        if(parent!=null){
            return parent.isInstall();
        }else{

            if(getMainObb()!=null){
                mainObbCompleted = this.getMainObb().getDownloadStatus().equals(EnumDownloadStatus.COMPLETED);
            }else{
                return true;
            }
            if(getPatchObb()!=null){
                patchObbCompleted = this.getPatchObb().getDownloadStatus().equals(EnumDownloadStatus.COMPLETED);
            }


            return getDownloadStatus().equals(EnumDownloadStatus.COMPLETED) && mainObbCompleted && patchObbCompleted;

        }


    }

    public ViewDownloadManagement getPatchObb() {
        return patchObb;
    }

    public ViewDownloadManagement getParent() {
        return parent;
    }

    public void setParent(ViewDownloadManagement parent) {
        this.parent = parent;
    }
}
