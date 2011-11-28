/**
 * Download,		auxilliary class to Aptoide's ServiceData
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

package cm.aptoide.pt.data.downloads;

import cm.aptoide.pt.data.cache.Cache;
import cm.aptoide.pt.data.notifications.ViewNotification;
import cm.aptoide.pt.data.views.ViewDownloadInfo;
import cm.aptoide.pt.data.views.ViewLogin;

 /**
 * Download, models a download
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class ViewDownload {

	private InterfaceDownloadInfo downloadInfo;
	private boolean bareInfo;
	private boolean loginRequired;
	private ViewLogin login;
	
	private Cache cache;
	private ViewNotification notifier;
	
	
	public ViewDownload(InterfaceDownloadInfo downloadInfo, Cache cache, ViewNotification notifier) {
		if(downloadInfo instanceof BareDownloadInfo){
			this.bareInfo = true;
		}else{
			this.bareInfo = false;
		}
		this.downloadInfo = downloadInfo;
		this.loginRequired = false;
		this.cache = cache;
		this.notifier = notifier;
	}
	
	public ViewDownload(InterfaceDownloadInfo downloadInfo, ViewLogin login, Cache cache, ViewNotification notifier) {
		if(downloadInfo instanceof BareDownloadInfo){
			this.bareInfo = true;
		}else{
			this.bareInfo = false;
		}
		this.downloadInfo = downloadInfo;
		this.loginRequired = true;
		this.login = login;
		this.cache = cache;
		this.notifier = notifier;
	}


	public InterfaceDownloadInfo getDownloadInfoBare() {
		return downloadInfo;
	}

	public ViewDownloadInfo getDownloadInfoFull() {
		if(!bareInfo){
			return (ViewDownloadInfo) downloadInfo;
		}else{
			return null;	//TODO throw exception instead or null object
		}
	}
	

	public boolean isLoginRequired() {
		return loginRequired;
	}

	public ViewLogin getLogin() {
		return login;			//TODO test isrequired and return nullobject pattern or exception if not 
	}

	public Cache getCache() {
		return cache;
	}

	public ViewNotification getNotifier() {
		return notifier;
	}
	
	
	public void clean(){
		this.downloadInfo = null;
		this.loginRequired = false;
		this.login = null;
		this.cache = null;
		this.notifier = null;
	}
	
	public void reuse(InterfaceDownloadInfo downloadInfo, Cache cache, ViewNotification notifier) {
		if(downloadInfo instanceof BareDownloadInfo){
			this.bareInfo = true;
		}else{
			this.bareInfo = false;
		}
		this.downloadInfo = downloadInfo;
		this.loginRequired = false;
		this.cache = cache;
		this.notifier = notifier;
	}
	
	public void reuse(InterfaceDownloadInfo downloadInfo, ViewLogin login, Cache cache, ViewNotification notifier) {
		if(downloadInfo instanceof BareDownloadInfo){
			this.bareInfo = true;
		}else{
			this.bareInfo = false;
		}
		this.downloadInfo = downloadInfo;
		this.loginRequired = true;
		this.login = login;
		this.cache = cache;
		this.notifier = notifier;
	}
	
}
