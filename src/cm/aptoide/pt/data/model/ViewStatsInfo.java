/**
 * ViewStatsInfo,		part of Aptoide's data model
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
import cm.aptoide.pt.data.util.Constants;

 /**
 * ViewStatsInfo, models an app's stats info
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class ViewStatsInfo {

	private ContentValues values;

	
	/**
	 * ViewStatsInfo Constructor
	 * 
	 * @param int applicationFullHashid, (applicationPackageName+'|'+applicationVersionCode+'|'+repositoryHashid).hashCode()
	 */
	public ViewStatsInfo(int applicationFullHashid) {
		this.values = new ContentValues(Constants.NUMBER_OF_COLUMNS_STATS_INFO);
		setAppFullHashid(applicationFullHashid);
	}
	
	
	private void setAppFullHashid(int appFullHashid){
		this.values.put(Constants.KEY_STATS_APP_FULL_HASHID, appFullHashid);
	}
	
	public int getAppFullHashid() {
		return values.getAsInteger(Constants.KEY_STATS_APP_FULL_HASHID);
	}
	
	public void setDownloads(int downloads){
		this.values.put(Constants.KEY_STATS_DOWNLOADS, downloads);
	}
	
	public int getDownloads(){
		return this.values.getAsInteger(Constants.KEY_STATS_DOWNLOADS);
	}
	
	private void setStars(float stars){
		this.values.put(Constants.KEY_STATS_STARS, stars);
	}
	
	public float getStars(){
		return this.values.getAsFloat(Constants.KEY_STATS_STARS);
	}
	
	public void setLikesDislikes(int likes, int dislikes){
		setLikes(likes);
		setDislikes(dislikes);
		setStars((Float.valueOf(likes)/Float.valueOf(((likes+dislikes)==0?1:likes+dislikes)))*Constants.NUMBER_OF_STARS);
	}
	
	private void setLikes(int likes){
		this.values.put(Constants.KEY_STATS_LIKES, likes);
	}
	
	public int getLikes(){
		return this.values.getAsInteger(Constants.KEY_STATS_LIKES);
	}
	
	private void setDislikes(int dislikes){
		this.values.put(Constants.KEY_STATS_DISLIKES, dislikes);
	}
	
	public int getDislikes(){
		return this.values.getAsInteger(Constants.KEY_STATS_DISLIKES);
	}
		
	
	public ContentValues getValues(){
		return this.values;
	}
	
	/**
	 * ViewStatsInfo object reuse clean references
	 */
	public void clean(){
		this.values = null;
	}
	
	/**
	 * ViewStatsInfo object reuse reConstructor
	 * 
	 * @param int applicationFullHashid, (applicationPackageName+'|'+applicationVersionCode+'|'+repositoryHashid).hashCode()
	 */
	public void reuse(int applicationFullHashid) {
		this.values = new ContentValues(Constants.NUMBER_OF_COLUMNS_STATS_INFO);
		setAppFullHashid(applicationFullHashid);
	}
	
}
