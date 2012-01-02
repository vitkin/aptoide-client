/**
 * ViewAppComment,		part of Aptoide's data model
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
 * ViewAppComment, models an app's comment
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class ViewAppComment {

	private ContentValues values;

	
	/**
	 * ViewAppComment Constructor
	 * 
	 * @param int applicationFullHashid, (applicationPackageName+'|'+applicationVersionCode+'|'+repositoryHashid).hashCode()
	 * @param int commentId id from server
	 */
	public ViewAppComment(int applicationFullHashid, int commentId) {
		this.values = new ContentValues(Constants.NUMBER_OF_COLUMNS_APP_COMMENTS);
		setAppFullHashid(applicationFullHashid);
		setCommentId(commentId);
	}
	
	
	private void setAppFullHashid(int appFullHashid){
		this.values.put(Constants.KEY_APP_COMMENTS_APP_FULL_HASHID, appFullHashid);
	}
	
	public int getAppFullHashid() {
		return values.getAsInteger(Constants.KEY_APP_COMMENTS_APP_FULL_HASHID);
	}
	
	private void setCommentId(int commentId){
		this.values.put(Constants.KEY_APP_COMMENT_ID, commentId);
	}
	
	public int getCommentId(){
		return this.values.getAsInteger(Constants.KEY_APP_COMMENT_ID);
	}
	
	public void setComment(String comment){
		this.values.put(Constants.KEY_APP_COMMENT, comment);
	}
	
	public String getComment(){
		return this.values.getAsString(Constants.KEY_APP_COMMENT);
	}
		
	
	public ContentValues getValues(){
		return this.values;
	}
	

	/**
	 * ViewAppComment object reuse, clean references
	 */
	public void clean(){
		this.values = null;
	}

	/**
	 * ViewAppComment object reuse, reConstructor
	 * 
	 * @param int applicationFullHashid, (applicationPackageName+'|'+applicationVersionCode+'|'+repositoryHashid).hashCode()
	 * @param int commentId id from server
	 */
	public void reuse(int applicationFullHashid, int commentId) {
		this.values = new ContentValues(Constants.NUMBER_OF_COLUMNS_APP_COMMENTS);
		setAppFullHashid(applicationFullHashid);
		setCommentId(commentId);
	}
	
}
