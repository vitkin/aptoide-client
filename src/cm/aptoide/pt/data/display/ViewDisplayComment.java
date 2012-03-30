/**
 * ViewDisplayComment,		part of Aptoide's data model
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

package cm.aptoide.pt.data.display;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

 /**
 * ViewDisplayComment, models a Comment
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class ViewDisplayComment implements Parcelable{

	protected long commentId;
	protected String userHashid;
	protected String userName;
	protected long answerTo;
	protected String subject = null;
	protected String body;
	protected String timestamp;
	protected String language;
	
	
	private ViewDisplayComment(){
	}

	/**
	 * ViewDisplayComment Constructor
	 *
	 * @param long commentId
	 */
	public ViewDisplayComment(long commentId) {
		this.commentId = commentId;
	}
	

	public void setUserHashid(String userHashid) {
		this.userHashid = userHashid;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setAnswerTo(long answerTo) {
		this.answerTo = answerTo;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
	

	public long getCommentId() {
		return commentId;
	}

	public String getUserHashid() {
		return userHashid;
	}

	public String getUserName() {
		return userName;
	}

	public long getAnswerTo() {
		return answerTo;
	}

	public String getSubject() {
		return subject;
	}

	public String getBody() {
		return body;
	}
	
	public String getTimestampString(){
		return timestamp;
	}

	public Date getTimestamp() throws ParseException {
		return (new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(timestamp));
	}

	public String getLanguage() {
		return language;
	}


	@Override
	public int hashCode() {
		return Long.valueOf(this.commentId).intValue();
	}


	@Override
	public boolean equals(Object object) {
		if(object instanceof ViewDisplayComment){
			ViewDisplayComment comment = (ViewDisplayComment) object;
			if(comment.hashCode() == this.hashCode()){
				return true;
			}
		}
		return false;
	}


	@Override
	public String toString() {
		return " CommentId: "+commentId+" Subject: "+subject+"  Body: "+body+" Username: "+userName+" answerTo: "+answerTo+" language: "+language+" timestamp: "+timestamp;
	}
	
	
	
	// Parcelable stuff //
	
	
	public static final Parcelable.Creator<ViewDisplayComment> CREATOR = new Parcelable.Creator<ViewDisplayComment>() {
		public ViewDisplayComment createFromParcel(Parcel in) {
			return new ViewDisplayComment(in);
		}

		public ViewDisplayComment[] newArray(int size) {
			return new ViewDisplayComment[size];
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

	protected ViewDisplayComment(Parcel in){
		readFromParcel(in);
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeLong(commentId);
		out.writeString(userHashid);
		out.writeString(userName);
		out.writeLong(answerTo);
		out.writeString(subject);
		out.writeString(body);
		out.writeString(timestamp);
		out.writeString(language);
	}

	public void readFromParcel(Parcel in) {
		commentId = in.readLong();
		userHashid = in.readString();
		userName = in.readString();
		answerTo = in.readLong();
		subject = in.readString();
		body = in.readString();
		timestamp = in.readString();
		language = in.readString();
	}

}
