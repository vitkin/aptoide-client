/**
 * ViewDownloadInfo,		part of Aptoide's data model
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
 * ViewDownloadInfo, models a download's info
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class ViewAppDownloadInfo {

	private ContentValues values;

	
	/**
	 * ViewDownloadInfo Constructor
	 * 
	 * @param String remotePathTail, what comes after repository's base path
	 * @param int applicationFullHashid, (applicationPackageName+'|'+applicationVersionCode+'|'+repositoryHashid).hashCode()
	 */
	public ViewAppDownloadInfo(String remotePathTail, int applicationFullHashid) {
		this.values = new ContentValues(Constants.NUMBER_OF_COLUMNS_DOWNLOAD_INFO);
		setRemotePathTail(remotePathTail);
		setAppFullHashid(applicationFullHashid);
	}
	
	
	private void setRemotePathTail(String remotePathTail){
		this.values.put(Constants.KEY_DOWNLOAD_REMOTE_PATH_TAIL, remotePathTail);		
	}
	
	public String getRemotePathTail(){
		return this.values.getAsString(Constants.KEY_DOWNLOAD_REMOTE_PATH_TAIL);
	}
	
	private void setAppFullHashid(int appFullHashid){
		this.values.put(Constants.KEY_DOWNLOAD_APP_FULL_HASHID, appFullHashid);
	}
	
	public int getAppFullHashid() {
		return values.getAsInteger(Constants.KEY_DOWNLOAD_APP_FULL_HASHID);
	}
	
	public void setMd5hash(int md5hash){
		this.values.put(Constants.KEY_DOWNLOAD_MD5HASH, md5hash);
	}
	
	public int getMd5hash(){
		return this.values.getAsInteger(Constants.KEY_DOWNLOAD_MD5HASH);
	}
	
	public void setSize(int size){
		this.values.put(Constants.KEY_DOWNLOAD_SIZE, size);
	}
	
	public int getSize(){
		return this.values.getAsInteger(Constants.KEY_DOWNLOAD_SIZE);
	}
		
	
	public ContentValues getValues(){
		return this.values;
	}
	

	/**
	 * ViewDownloadInfo object reuse, clean references
	 */
	public void clean(){
		this.values = null;
	}

	/**
	 * ViewDownloadInfo object reuse, reConstructor
	 * 
	 * @param String remotePathTail, what comes after repository's base path
	 * @param int applicationFullHashid, (applicationPackageName+'|'+applicationVersionCode+'|'+repositoryHashid).hashCode()
	 */
	public void reuse(String iconRemotePathTail, int applicationFullHashid) {
		this.values = new ContentValues(Constants.NUMBER_OF_COLUMNS_DOWNLOAD_INFO);
		setRemotePathTail(iconRemotePathTail);
		setAppFullHashid(applicationFullHashid);
	}
	
}
