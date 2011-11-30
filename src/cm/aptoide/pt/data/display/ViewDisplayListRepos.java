/**
 * ViewDisplayListRepos,		part of Aptoide's data model
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

package cm.aptoide.pt.data.display;

import java.util.ArrayList;
import java.util.Map;

/**
 * ViewDisplayListRepos, models a list of Repos,
 * 			 maintains insertion order
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class ViewDisplayListRepos {

	private ArrayList<Map<String, Object>> reposList;

	
	/**
	 * ViewDisplayListRepos Constructor
	 * 
	 * @param ViewDisplayRepository repo
	 */
	public ViewDisplayListRepos(ViewDisplayRepository repo) {
		this(1);
		addRepo(repo);
	}
	
	/**
	 * ViewDisplayListRepos Constructor
	 */
	public ViewDisplayListRepos(int size) {
		this.reposList = new ArrayList<Map<String,Object>>(size);
	}
	
	
	public void addRepo(ViewDisplayRepository repo){
		this.reposList.add(repo.getDiplayMap());
	}
	
	public void removeRepo(int index){
		this.reposList.remove(index);		
	}
	
	public Map<String, Object> getRepo(int index){
		return this.reposList.get(index);
	}
	
	
	/**
	 * getList, retrieves repos list,
	 * 			maintains insertion order
	 * 
	 * @return ArrayList<Map<String, Object>> reposList
	 */
	public ArrayList<Map<String, Object>> getList(){
		return this.reposList;
	}

	
	/**
	 * ViewDisplayListRepos object reuse clean references
	 */
	public void clean(){
		this.reposList = null;
	}
	
	/**
	 * ViewDisplayListRepos object reuse reConstructor
	 */
	public void reuse() {
		this.reposList = new ArrayList<Map<String, Object>>(1);
	}
	
	/**
	 * ViewDisplayListRepos object reuse reConstructor
	 * 
	 * @param ViewDisplayRepository repo
	 */
	public void reuse(ViewDisplayRepository repo) {
		reuse();
		addRepo(repo);
	}
	
}
