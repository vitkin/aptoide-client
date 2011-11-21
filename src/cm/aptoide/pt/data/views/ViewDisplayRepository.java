/**
 * ViewDisplayRepository,		part of Aptoide's data model
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

import java.util.HashMap;

import cm.aptoide.pt.data.Constants;

 /**
 * ViewDisplayRepository, models a repository
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class ViewDisplayRepository {

	private HashMap<String, Object> map;
	
	
	/**
	 * ViewDisplayRepository Constructor
	 *
	 * @param repoHashid
	 * @param uri
	 * @param inUse
	 * @param size
	 */
	public ViewDisplayRepository(int repoHashid, String uri, boolean inUse, int size) {
		this.map = new HashMap<String, Object>(Constants.NUMBER_OF_DISPLAY_FIELDS_REPO);
		this.map.put(Constants.DISPLAY_REPO_REQUIRES_LOGIN, false);
		setRepoHashid(repoHashid);
		setUri(uri);
		setInUse(inUse);
		setSize(size);
	}

	
	private void setRepoHashid(int repoHashid){
		this.map.put(Constants.KEY_REPO_HASHID, repoHashid);
	}
	
	public int getRepoHashid() {
		return (Integer)this.map.get(Constants.KEY_REPO_HASHID);
	}
	
	private void setUri(String uri){
		this.map.put(Constants.KEY_REPO_URI, uri);
	}

	public String getUri() {
		return (String)this.map.get(Constants.KEY_REPO_URI);
	}
	
	private void setSize(int size){
		this.map.put(Constants.KEY_REPO_SIZE, size);
	}

	public int getSize() {
		return (Integer)this.map.get(Constants.KEY_REPO_SIZE);
	}
	
	private void setInUse(boolean inUse){
		this.map.put(Constants.KEY_REPO_IN_USE, inUse);
	}

	public boolean getInUse() {
		return (Boolean)this.map.get(Constants.KEY_REPO_IN_USE);
	}
	
	public HashMap<String, Object> getDiplayMap(){
		return this.map;
	}
	
	
	public void setLogin(ViewLogin login){
		this.map.put(Constants.DISPLAY_REPO_REQUIRES_LOGIN, true);
		login.setRepoHashid(getRepoHashid());
		this.map.put(Constants.DISPLAY_REPO_LOGIN, login);
	}
	
	public boolean requiresLogin() {
		return (Boolean)this.map.get(Constants.DISPLAY_REPO_REQUIRES_LOGIN);
	}

	public ViewLogin getLogin() {
		return (ViewLogin)this.map.get(Constants.DISPLAY_REPO_LOGIN);			//TODO test isrequired and return nullobject pattern if not
	}
	

	/**
	 * ViewDisplayRepository object reuse clean references
	 *
	 * @param String uri
	 */
	public void clean(){
		this.map = null;
	}
	
	/**
	 * ViewDisplayRepository object reuse reConstructor
	 *
	 * @param repoHashid
	 * @param uri
	 * @param inUse
	 * @param size
	 */
	public void reuse(int repoHashid, String uri, boolean inUse, int size) {
		this.map = new HashMap<String, Object>(Constants.NUMBER_OF_DISPLAY_FIELDS_REPO);
		this.map.put(Constants.DISPLAY_REPO_REQUIRES_LOGIN, false);
		setRepoHashid(repoHashid);
		setUri(uri);
		setInUse(inUse);
		setSize(size);
	}


	@Override
	public int hashCode() {
		return this.getRepoHashid();
	}


	@Override
	public boolean equals(Object object) {
		if(object instanceof ViewDisplayRepository){
			ViewDisplayRepository repo = (ViewDisplayRepository) object;
			if(repo.hashCode() == this.hashCode()){
				return true;
			}
		}
		return false;
	}


	@Override
	public String toString() {
		return "RepoHashid: "+getRepoHashid()+" Uri: "+getUri()+" Size: "+getSize()+" InUse: "+getInUse()+ "Login+ "+getLogin().toString();
	}
		
}
