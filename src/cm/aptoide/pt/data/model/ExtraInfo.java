/*
 * ExtraInfo		part of Aptoide's data model
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
 * ExtraInfo, models a download's info
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class ExtraInfo {

	private ContentValues values;

	
	/**
	 * DownloadInfo Constructor
	 * 
	 * @param int applicationFullHashid, (applicationPackageName+'|'+applicationVersionCode+'|'+repositoryHashid).hashCode()
	 */
	public ExtraInfo(int applicationFullHashid) {
		this.values = new ContentValues(Constants.NUMBER_OF_COLUMNS_DOWNLOAD);
		setAppFullHashid(applicationFullHashid);
	}
	
	
	private void setAppFullHashid(int appFullHashid){
		this.values.put(Constants.KEY_EXTRA_APP_FULL_HASHID, appFullHashid);
	}
	
	public int getAppFullHashid() {
		return values.getAsInteger(Constants.KEY_EXTRA_APP_FULL_HASHID);
	}
	
	public void setDescription(String description){
		this.values.put(Constants.KEY_EXTRA_DESCRIPTION, description);
	}
	
	public String getDescription(){
		return this.values.getAsString(Constants.KEY_EXTRA_DESCRIPTION);
	}
	
	public void setRating(int rating){
		this.values.put(Constants.KEY_EXTRA_RATING, rating);
	}
	
	public int getRating(){
		return this.values.getAsInteger(Constants.KEY_EXTRA_RATING);
	}
	
	public void setStars(int stars){
		this.values.put(Constants.KEY_EXTRA_STARS, stars);
	}
	
	public int getStars(){
		return this.values.getAsInteger(Constants.KEY_EXTRA_STARS);
	}
	
	public void setDownloads(int downloads){
		this.values.put(Constants.KEY_EXTRA_DOWNLOADS, downloads);
	}
	
	public int getDownloads(){
		return this.values.getAsInteger(Constants.KEY_EXTRA_DOWNLOADS);
	}
		
	
	public ContentValues getValues(){
		return this.values;
	}
	
	
	public void clean(){
		this.values = null;
	}
	
	public void reuse(int applicationFullHashid) {
		this.values = new ContentValues(Constants.NUMBER_OF_COLUMNS_DOWNLOAD);
		setAppFullHashid(applicationFullHashid);
	}
	
}
