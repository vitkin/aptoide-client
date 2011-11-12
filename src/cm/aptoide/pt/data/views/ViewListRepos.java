/*
 * ViewListRepos		part of Aptoide's data model
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
 * ViewListRepos, models a list of Repos,
 * 			 maintains insertion order
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class ViewListRepos {

	private LinkedHashMap<Integer, ViewDisplayRepository> reposList;

	
	/**
	 * ViewListRepos Constructor
	 * 
	 * @param ViewRepository repo
	 */
	public ViewListRepos(ViewDisplayRepository repo) {
		this();
		addRepo(repo);
	}
	
	/**
	 * ViewListRepos Constructor
	 */
	public ViewListRepos() {
		this.reposList = new LinkedHashMap<Integer, ViewDisplayRepository>(1);
	}
	
	
	public void addRepo(ViewDisplayRepository repo){
		this.reposList.put(repo.getRepoHashid(), repo);
	}
	
	public void removeRepo(int repoHashid){
		this.reposList.remove(repoHashid);		
	}
	
	public void removeRepo(ViewDisplayRepository repo){
		this.reposList.remove(repo.getRepoHashid());		
	}
	
	public ViewDisplayRepository getRepo(int repoHashid){
		return this.reposList.get(repoHashid);
	}
	
	
	/**
	 * getList, retrieves repos list,
	 * 			maintains insertion order
	 * 
	 * @return LinkedHashMap<Integer, ViewDisplayRepository> reposList
	 */
	public LinkedHashMap<Integer, ViewDisplayRepository> getList(){
		return this.reposList;
	}

	
	/**
	 * ViewListRepos object reuse clean references
	 */
	public void clean(){
		this.reposList = null;
	}
	
	/**
	 * ViewListRepos object reuse reConstructor
	 * 
	 * @param ViewDisplayRepository repo
	 */
	public void reuse(ViewDisplayRepository repo) {
		this.reposList = new LinkedHashMap<Integer, ViewDisplayRepository>(1);
		addRepo(repo);
	}
	
}
