/**
 * ViewScreenInfo,		part of Aptoide's data model
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
 * ViewScreenInfo, models an screen's download info
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class ViewScreenInfo {

	private ContentValues values;

	
	/**
	 * ViewScreenInfo Constructor
	 * 
	 * @param String screenRemotePathTail, screen's remote path tail (what comes after repository's screens path)
	 * @param int orderNumber, screen's app order number
	 * @param int applicationFullHashid, (applicationPackageName+'|'+applicationVersionCode+'|'+repositoryHashid).hashCode()
	 */
	public ViewScreenInfo(String screenRemotePathTail, int orderNumber, int applicationFullHashid) {
		this.values = new ContentValues(Constants.NUMBER_OF_COLUMNS_SCREEN_INFO);
		setScreenRemotePathTail(screenRemotePathTail);
		setAppFullHashid(applicationFullHashid);
		setOrderNumber(orderNumber);
	}
	
	
	private void setScreenRemotePathTail(String screenRemotePathTail){
		values.put(Constants.KEY_SCREEN_REMOTE_PATH_TAIL, screenRemotePathTail);		
	}
	
	public String getScreenRemotePathTail(){
		return values.getAsString(Constants.KEY_SCREEN_REMOTE_PATH_TAIL);
	}
	
	private void setOrderNumber(int orderNumber){
		values.put(Constants.KEY_SCREEN_ORDER_NUMBER, orderNumber);
	}
	
	public int getOrderNumber() {
		return values.getAsInteger(Constants.KEY_SCREEN_ORDER_NUMBER);
	}
	
	private void setAppFullHashid(int appFullHashid){
		values.put(Constants.KEY_SCREEN_APP_FULL_HASHID, appFullHashid);
	}
	
	public int getAppFullHashid() {
		return values.getAsInteger(Constants.KEY_SCREEN_APP_FULL_HASHID);
	}
	
	
	public ContentValues getValues(){
		return this.values;
	}

	

	/**
	 * ViewScreen object reuse, clean references
	 */
	public void clean(){
		this.values = null;
	}

	/**
	 * ViewScreen object reuse, reConstructor
	 * 
	 * @param String screenRemotePathTail, screen's remote path tail (what comes after repository's base path)
	 * @param int orderNumber, screen's app order number
	 * @param int applicationFullHashid, (applicationPackageName+'|'+applicationVersionCode+'|'+repositoryHashid).hashCode()
	 */
	public void reuse(String screenRemotePathTail, int orderNumber, int applicationFullHashid) {
		this.values = new ContentValues(Constants.NUMBER_OF_COLUMNS_SCREEN_INFO);
		setScreenRemotePathTail(screenRemotePathTail);
		setAppFullHashid(applicationFullHashid);
		setOrderNumber(orderNumber);
	}
	
}
