/*
 * Download		auxilliary class to Aptoide's ServiceData
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
import cm.aptoide.pt.data.notifiers.Notifier;

 /**
 * Download, models a download
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class Download {

	private String remotePath;
	private boolean loginRequired;
	private Login login;
	
	private Cache cache;
	private Notifier notifier;
	
	
	public Download(String remotePath, boolean loginRequired, Login login, Cache cache, Notifier notifier) {
		this.remotePath = remotePath;
		this.loginRequired = loginRequired;
		this.login = login;
		this.cache = cache;
		this.notifier = notifier;
	}


	public String getRemotePath() {
		return remotePath;
	}

	public boolean isLoginRequired() {
		return loginRequired;
	}

	public Login getLogin() {
		return login;
	}

	public Cache getCache() {
		return cache;
	}

	public Notifier getNotifier() {
		return notifier;
	}
	
	
	public void clean(){
		this.remotePath = null;
		this.loginRequired = false;
		this.login = null;
		this.cache = null;
		this.notifier = null;
	}
	
	public void reuse(String remotePath, boolean loginRequired, Login login, Cache cache, Notifier notifier) {
		this.remotePath = remotePath;
		this.loginRequired = loginRequired;
		this.login = login;
		this.cache = cache;
		this.notifier = notifier;
	}
	
}
