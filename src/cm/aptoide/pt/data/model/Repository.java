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

 /**
 * Repository, models a repository
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class Repository {

	private ContentValues values;
	private boolean requiresLogin;
	private Login login;
	
	
	public Repository(String uri) {
		this.requiresLogin = false;
		this.values = new ContentValues(Constants.NUMBER_OF_COLUMNS_REPO);
		setUri(uri);	
	}

	
	public int getHashid() {
		return values.getAsInteger(Constants.KEY_REPO_HASHID);
	}
	
	private void setUri(String uri){
		values.put(Constants.KEY_REPO_URI, uri);
		values.put(Constants.KEY_REPO_HASHID, uri.hashCode());
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
	
	public void setIconsPath(String iconsPath){
		values.put(Constants.KEY_REPO_ICONS_PATH, iconsPath);
	}

	public String getIconsPath() {
		return values.getAsString(Constants.KEY_REPO_ICONS_PATH);
	}
	
	public void setScreensPath(String screensPath){
		values.put(Constants.KEY_REPO_SCREENS_PATH, screensPath);
	}

	public String getScreensPath() {
		return values.getAsString(Constants.KEY_REPO_SCREENS_PATH);
	}
	
	public void setSize(int size){
		values.put(Constants.KEY_REPO_SIZE, size);
	}

	public int getSize() {
		return values.getAsInteger(Constants.KEY_REPO_SIZE);
	}
	
	public void setDelta(String delta){
		values.put(Constants.KEY_REPO_DELTA, delta);
	}

	public String getDelta() {
		return values.getAsString(Constants.KEY_REPO_DELTA);
	}
	
	public void setInUse(boolean inUse){
		values.put(Constants.KEY_REPO_IN_USE, inUse?Constants.DB_TRUE:Constants.DB_FALSE);
	}

	public boolean getInUse() {
		return values.getAsInteger(Constants.KEY_REPO_SIZE)==Constants.DB_TRUE?true:false;
	}
	
	public ContentValues getValues(){
		return this.values;
	}
	
	
	public void setLogin(Login login){
		this.requiresLogin = true;
		this.login = login;
		this.login.setRepoHashid(getHashid());
	}
	
	public boolean requiresLogin() {
		return requiresLogin;
	}

	public Login getLogin() {
		return login;			//TODO test isrequired and return nullobject pattern if not
	}
	
	
	public void clean(){
		this.values = null;
		this.requiresLogin = false;
		this.login = null;
	}
	
	public void reuse(String uri) {
		this.requiresLogin = false;
		this.values = new ContentValues(Constants.NUMBER_OF_COLUMNS_REPO);
		setUri(uri);
	}
	
}
