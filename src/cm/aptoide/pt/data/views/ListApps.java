/*
 * ListApps		part of Aptoide's data model
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

package cm.aptoide.pt.data.views;

import java.util.LinkedHashMap;

 /**
 * ListApps, models a list of Apps,
 * 			 maintains insertion order
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class ListApps {

	private LinkedHashMap<Integer, Application> appsList;

	
	/**
	 * ListApps Constructor
	 * 
	 * @param Application app
	 */
	public ListApps(Application app) {
		this();
		addApp(app);
	}
	
	public ListApps() {
		this.appsList = new LinkedHashMap<Integer, Application>(1);
	}
	
	
	public void addApp(Application app){
		this.appsList.put(app.getHashid(), app);		
	}
	
	public void removeApp(int appHashid){
		this.appsList.remove(appHashid);		
	}
	
	public void removeApp(Application app){
		this.appsList.remove(app.getHashid());		
	}
	
	public Application getApp(int appHashid){
		return this.appsList.get(appHashid);
	}
	
	
	/**
	 * getList, retrieves apps list,
	 * 			maintains insertion order
	 * 
	 * @return LinkedHashMap<Integer, Application> appsList
	 */
	public LinkedHashMap<Integer, Application> getList(){
		return this.appsList;
	}

	
	
	public void clean(){
		this.appsList = null;
	}
	
	public void reuse(Application app) {
		this.appsList = new LinkedHashMap<Integer, Application>(1);
		addApp(app);
	}
	
}
