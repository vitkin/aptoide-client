/*
 * ListRepos		part of Aptoide's data model
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

import java.util.ArrayList;

 /**
 * ListRepos, models an list of Repos
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class ListRepos {

	private ArrayList<Repository> repoList;

	
	/**
	 * ListRepos Constructor
	 * 
	 * @param String repo
	 */
	public ListRepos(Repository repo) {
		this.repoList = new ArrayList<Repository>();
		addRepo(repo);
	}
	
	public ListRepos() {
		this.repoList = new ArrayList<Repository>();
	}
	
	
	public void addRepo(Repository repo){
		this.repoList.add(repo);		
	}
	
	public void removeRepo(Repository repo){
		this.repoList.remove(repo);		
	}
	
	
	public ArrayList<Repository> getList(){
		return this.repoList;
	}

	
	
	public void clean(){
		this.repoList = null;
	}
	
	public void reuse(Repository repo) {
		this.repoList = new ArrayList<Repository>();
		addRepo(repo);
	}
	
}
