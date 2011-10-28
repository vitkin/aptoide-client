/*
 * Application		part of Aptoide's data model
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
 * Application, models an application
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class Application {

	private ContentValues values;
	private int categoryHashid;
	private int repoHashid;
	
	
	public Application(String appName, String packageName, String versionName, int versionCode) {
		this.values = new ContentValues(Constants.NUMBER_OF_COLUMNS_APPLICATION);
		setPackageName(packageName);
		setVersionCode(versionCode);
		setAppHashid(packageName, versionCode);	
	}
	
	
	private void setPackageName(String packageName){
		values.put(Constants.KEY_APPLICATION_PACKAGE_NAME, packageName);		
	}
	
	public String getPackageName(){
		return values.getAsString(Constants.KEY_APPLICATION_PACKAGE_NAME);
	}
	
	private void setVersionCode(int versionCode){
		values.put(Constants.KEY_APPLICATION_VERSION_CODE, versionCode);
	}
	
	public int getVersionCode(){
		return values.getAsInteger(Constants.KEY_APPLICATION_VERSION_CODE);
	}
	
	public int getHashid() {
		return values.getAsInteger(Constants.KEY_APPLICATION_HASHID);
	}
	
	private void setAppHashid(String packageName, int versionCode){
		values.put(Constants.KEY_APPLICATION_HASHID, (packageName+'|'+Integer.toString(versionCode)).hashCode());
	}

	
	
	public void clean(){
		this.values = null;
		this.categoryHashid = Constants.EMPTY_INT;
		this.repoHashid = Constants.EMPTY_INT;
	}
	
	public void reuse(String appName, String packageName, String versionName, int versionCode) {
	}
	
}
