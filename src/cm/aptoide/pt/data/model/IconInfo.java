/*
 * IconInfo		part of Aptoide's data model
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
 * IconInfo, models an icon's download info
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class IconInfo {

	private ContentValues values;

	
	/**
	 * Icon Constructor
	 * 
	 * @param String iconRemotePathTail, icon's remote path tail (what comes after repository's base path)
	 * @param int applicationFullHashid, (applicationPackageName+'|'+applicationVersionCode+'|'+repositoryHashid).hashCode()
	 */
	public IconInfo(String iconRemotePathTail, int applicationFullHashid) {
		this.values = new ContentValues(Constants.NUMBER_OF_COLUMNS_ICON);
		setRemotePathTail(iconRemotePathTail);
		setAppFullHashid(applicationFullHashid);
	}
	
	
	private void setRemotePathTail(String remotePathTail){
		values.put(Constants.KEY_ICON_REMOTE_PATH_TAIL, remotePathTail);		
	}
	
	public String getRemotePathTail(){
		return values.getAsString(Constants.KEY_ICON_REMOTE_PATH_TAIL);
	}
	
	private void setAppFullHashid(int appFullHashid){
		values.put(Constants.KEY_ICON_APP_FULL_HASHID, appFullHashid);
	}
	
	public int getAppFullHashid() {
		return values.getAsInteger(Constants.KEY_ICON_APP_FULL_HASHID);
	}
	
	
	public ContentValues getValues(){
		return this.values;
	}

	
	
	public void clean(){
		this.values = null;
	}
	
	public void reuse(String iconRemotePathTail, int applicationFullHashid) {
		this.values = new ContentValues(Constants.NUMBER_OF_COLUMNS_ICON);
		setRemotePathTail(iconRemotePathTail);
		setAppFullHashid(applicationFullHashid);
	}
	
}
