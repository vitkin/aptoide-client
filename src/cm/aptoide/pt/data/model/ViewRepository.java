/**
 * ViewRepository,		part of Aptoide's data model
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
import android.os.Parcel;
import android.os.Parcelable;
import cm.aptoide.pt.data.Constants;

 /**
 * ViewRepository, models a repository
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class ViewRepository implements Parcelable{

	private ContentValues values;
	private boolean loginRequired;
	private ViewLogin login;
	
	
	/**
	 * ViewRepository Constructor
	 *
	 * @param String uri
	 */
	public ViewRepository(String uri) {
		this.loginRequired = false;
		this.values = new ContentValues(Constants.NUMBER_OF_COLUMNS_REPO);			
		setUri(uri);
		setInUse(true);
	}
	
	public ViewRepository(String uri, int size, String basePath, String iconsPath, String screensPath, String delta){
		this(uri);
		setSize(size);
		setBasePath(basePath);
		setIconsPath(iconsPath);
		setScreensPath(screensPath);
		setDelta(delta);
	}

	
	private void setUri(String uri){
		values.put(Constants.KEY_REPO_URI, uri);
		values.put(Constants.KEY_REPO_HASHID, uri.hashCode());
	}

	public String getUri() {
		return values.getAsString(Constants.KEY_REPO_URI);
	}
	
	public int getHashid() {
		return values.getAsInteger(Constants.KEY_REPO_HASHID);
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
	
	public void setLastSynchroTime(long lastSynchroTimeStamp){
		values.put(Constants.KEY_REPO_LAST_SYNCHRO, lastSynchroTimeStamp);
	}

	public long getLastSynchroTime() {
		return values.getAsLong(Constants.KEY_REPO_LAST_SYNCHRO);
	}
	
	public void setInUse(boolean inUse){
		values.put(Constants.KEY_REPO_IN_USE, inUse?Constants.DB_TRUE:Constants.DB_FALSE);
	}

	public boolean getInUse() {
		return (values.getAsInteger(Constants.KEY_REPO_IN_USE)==Constants.DB_TRUE?true:false);
	}
	
	public ContentValues getValues(){
		return this.values;
	}
	
	
	public void setLogin(ViewLogin login){
		this.loginRequired = true;
		this.login = login;
		this.login.setRepoHashid(getHashid());
	}
	
	public boolean isLoginRequired() {
		return loginRequired;
	}

	public ViewLogin getLogin() {
		return login;			//TODO test isrequired and return nullobject pattern if not
	}
	

	/**
	 * ViewRepository object reuse clean references
	 *
	 * @param String uri
	 */
	public void clean(){
		this.values = null;
		this.loginRequired = false;
		this.login = null;
	}

	/**
	 * ViewRepository object reuse reConstructor
	 *
	 * @param String uri
	 */
	public void reuse(String uri) {
		this.loginRequired = false;
		this.values = new ContentValues(Constants.NUMBER_OF_COLUMNS_REPO);
		setUri(uri);
		setInUse(true);
	}
	
	public void reuse(String uri, int size, String basePath, String iconsPath, String screensPath, String delta){
		reuse(uri);
		setSize(size);
		setBasePath(basePath);
		setIconsPath(iconsPath);
		setScreensPath(screensPath);
		setDelta(delta);
	}


	@Override
	public int hashCode() {
		return this.getHashid();
	}


	@Override
	public boolean equals(Object object) {
		if(object instanceof ViewRepository){
			ViewRepository repo = (ViewRepository) object;
			if(repo.hashCode() == this.hashCode()){
				return true;
			}
		}
		return false;
	}


	@Override
	public String toString() {
		StringBuilder description = new StringBuilder(" Uri: "+this.getUri()+" Hashid: "+this.getHashid());
		try {
			description.append(" Size: "+this.getSize());
			description.append(" Delta: "+this.getDelta());
			description.append(" BasePath: "+this.getBasePath());
			description.append(" IconsPath: "+this.getIconsPath());
			description.append(" ScreensPath: "+this.getScreensPath());
		} catch (NullPointerException e) {}
		
		if(loginRequired){
			description.append(" "+this.getLogin().toString());
		}
		
		return description.toString();
	}
	
	
	
	// Parcelable stuff //
	
	
	public static final Parcelable.Creator<ViewRepository> CREATOR = new Parcelable.Creator<ViewRepository>() {
		public ViewRepository createFromParcel(Parcel in) {
			return new ViewRepository(in);
		}

		public ViewRepository[] newArray(int size) {
			return new ViewRepository[size];
		}
	};

	/** 
	 * we're annoyingly forced to create this even if we clearly don't need it,
	 *  so we just use the default return 0
	 *  
	 *  @return 0
	 */
	@Override
	public int describeContents() {
		return 0;
	}

	private ViewRepository(Parcel in){
		readFromParcel(in);
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeParcelable(values, 0);
		out.writeValue(loginRequired);
		out.writeParcelable(login, 0);
	}

	public void readFromParcel(Parcel in) {
		values = in.readParcelable(null);
		loginRequired = (Boolean) in.readValue(null);
		login = in.readParcelable(ViewLogin.class.getClassLoader());
	}
		
}
