/**
 * ViewXmlParse,		auxilliary class to Aptoide's ServiceData
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

package cm.aptoide.pt.data.xml;

import cm.aptoide.pt.data.cache.ViewCache;
import cm.aptoide.pt.data.model.ViewRepository;
import cm.aptoide.pt.data.notifications.ViewNotification;

 /**
 * ViewXmlParse, models a xml parse
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class ViewXmlParse {

	private String localPath;
	
	private ViewRepository repository;
	private ViewNotification notification;
	
	
	public ViewXmlParse(ViewRepository repository, ViewCache cache, ViewNotification notification) { //TODO enum xml type (info, extra, myapp)
		this.repository = repository;
		this.localPath = cache.getLocalPath();
		this.notification = notification;
	}


	public String getLocalPath() {
		return localPath;
	}
	
	public ViewRepository getRepository(){
		return this.repository;
	}
	
	public ViewNotification getNotification() {
		return notification;
	}
	
	
	public void clean(){
		this.repository = null;
		this.localPath = null;
		this.notification = null;
	}
	
	public void reuse(ViewRepository repository, ViewCache cache, ViewNotification notification) {
		this.repository = repository;
		this.localPath = cache.getLocalPath();
		this.notification = notification;
	}
	
}
