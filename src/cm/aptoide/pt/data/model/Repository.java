/*
 * Repository		part of Aptoide's data model
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

package cm.aptoide.pt.data.model;

import android.content.ContentValues;
import cm.aptoide.pt.data.Constants;
import cm.aptoide.pt.data.downloads.Login;

 /**
 * Repository, models a repository
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class Repository {

	private ContentValues values;
	private boolean loginRequired;
	private Login login;
	
	
	public Repository(String uri) {
		this.loginRequired = false;
		this.values = new ContentValues(7);
		setUri(uri);
		setHashid(uri);		
	}

	
	private void setHashid(String uri){
		values.put(Constants.KEY_REPO_HASHID, uri.hashCode());
	}
	
	public int getHashid() {
		return values.getAsInteger(Constants.KEY_REPO_HASHID);
	}
	
	private void setUri(String uri){
		values.put(Constants.KEY_REPO_URI, uri);
	}

	public String getUri() {
		return values.getAsString(Constants.KEY_REPO_URI);
	}
	
	public void setBasePath(String basePath){
		values.put(Constants.KEY_REPO_BASE_PATH, basePath);
	}

	public String getBasePath() {
		return values.getAsString(Constants.KEY_REPO_BASE_PATH);
	}
	
	public void setSize(int size){
		values.put(Constants.KEY_REPO_SIZE, size);
	}

	public int getSize() {
		return values.getAsInteger(Constants.KEY_REPO_SIZE);
	}
	
	public void setUpdateTime(String updateTime){
		values.put(Constants.KEY_REPO_UPDATE_TIME, updateTime);
	}

	public String getUpdateTime() {
		return values.getAsString(Constants.KEY_REPO_UPDATE_TIME);
	}
	
	public void setDelta(String delta){
		values.put(Constants.KEY_REPO_DELTA, delta);
	}

	public String getDelta() {
		return values.getAsString(Constants.KEY_REPO_DELTA);
	}
	
	public void setInUse(boolean inUse){
		values.put(Constants.KEY_REPO_IN_USE, inUse?1:0);
	}

	public boolean getInUse() {
		return values.getAsInteger(Constants.KEY_REPO_SIZE)==1?true:false;
	}
	
	public ContentValues getValues(){
		return this.values;
	}
	
	
	public void setLogin(Login login){
		this.loginRequired = true;
		this.login = login;
	}
	
	public boolean isLoginRequired() {
		return loginRequired;
	}

	public Login getLogin() {
		return login;			//TODO test isrequired and return nullobject pattern if not
	}
	
	
	public void clean(){
		this.values = null;
		this.loginRequired = false;
		this.login = null;
	}
	
	public void reuse(String uri) {
		this.loginRequired = false;
		this.values = new ContentValues(7);
		setUri(uri);
	}
	
}
