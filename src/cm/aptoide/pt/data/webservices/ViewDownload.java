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

package cm.aptoide.pt.data.webservices;

import cm.aptoide.pt.data.cache.ViewCache;
import cm.aptoide.pt.data.model.ViewLogin;
import cm.aptoide.pt.data.notifications.ViewNotification;

 /**
 * Download, models a download
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class ViewDownload {

	private String remotePath;
	private boolean loginRequired;
	private ViewLogin login;
	private boolean sizeIsKnown;
	private int size;
	
	private ViewCache cache;
	private ViewNotification notification;
	
	
	public ViewDownload(String remotePath, ViewCache cache, ViewNotification notification) {
		this.remotePath = remotePath;
		this.loginRequired = false;
		this.cache = cache;
		this.notification = notification;
		this.sizeIsKnown = false;
	}
	
	public ViewDownload(String remotePath, ViewLogin login, ViewCache cache, ViewNotification notification) {
		this(remotePath, cache, notification);
		this.loginRequired = true;
		this.login = login;
		this.sizeIsKnown = false;
	}
	
	public ViewDownload(String remotePath, int size, ViewCache cache, ViewNotification notification) {
		this.remotePath = remotePath;
		this.loginRequired = false;
		this.cache = cache;
		this.notification = notification;
		this.sizeIsKnown = true;
		this.size = size;
	}
	
	public ViewDownload(String remotePath, int size, ViewLogin login, ViewCache cache, ViewNotification notification) {
		this(remotePath, cache, notification);
		this.loginRequired = true;
		this.login = login;
		this.sizeIsKnown = true;
		this.size = size;
	}


	public String getRemotePath() {
		return remotePath;
	}	

	public boolean isLoginRequired() {
		return loginRequired;
	}
	
	public void setLogin(ViewLogin login){
		this.loginRequired = true;
		this.login = login;
	}
	
	public boolean isSizeKnown(){
		return sizeIsKnown;
	}
	
	public int getSize(){
		return this.size;
	}

	public ViewLogin getLogin() {
		return login;			//TODO test isrequired and return nullobject pattern or exception if not 
	}

	public ViewCache getCache() {
		return cache;
	}

	public ViewNotification getNotification() {
		return notification;
	}
	
	
	public void clean(){
		this.remotePath = null;
		this.loginRequired = false;
		this.login = null;
		this.cache = null;
		this.notification = null;
		this.sizeIsKnown = false;
	}
	
	public void reuse(String remotePath, ViewCache cache, ViewNotification notification) {
		this.remotePath = remotePath;
		this.loginRequired = false;
		this.cache = cache;
		this.notification = notification;
	}
	
	public void reuse(String remotePath, ViewLogin login, ViewCache cache, ViewNotification notification) {
		reuse(remotePath, cache, notification);
		this.loginRequired = true;
		this.login = login;
	}
	
	public void reuse(String remotePath, int size, ViewLogin login, ViewCache cache, ViewNotification notification) {
		reuse(remotePath, login, cache, notification);
		this.sizeIsKnown = true;
		this.size = size;
	}
	
	public void reuse(String remotePath, int size, ViewCache cache, ViewNotification notification) {
		reuse(remotePath, cache, notification);
		this.sizeIsKnown = true;
		this.size = size;
	}
	
}
