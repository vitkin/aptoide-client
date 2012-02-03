/**
 * ViewDisplayLogin,		auxilliary class to Aptoide's ServiceData
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

import java.io.Serializable;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;
import cm.aptoide.pt.data.Constants;
import cm.aptoide.pt.data.model.ViewLogin;


 /**
 * ViewDisplayLogin, models a download's authentication
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class ViewDisplayLogin implements Parcelable, Serializable{

	private static final long serialVersionUID = -3283950206281665375L;
	private String username;
	private String password;
	private int repoHashid;
	

	/**
	 * ViewLogin Constructor
	 *
	 * @param username
	 * @param password
	 */
	public ViewDisplayLogin(String username, String password) {
		setUsername(username);
		setPassword(password);
	}

	private void setUsername(String username){
		this.username = username;
	}

	private void setPassword(String password){
		this.password = password;
	}

	public String getUsername() {
		return this.username;
	}

	public String getPassword() {
		return this.password;
	}
	
	public void setRepoHashid(int repoHashid){
		this.repoHashid = repoHashid;
	}
	
	public ContentValues getValues(){
		ViewLogin login = new ViewLogin(this.username, this.password);
		login.setRepoHashid(this.repoHashid);
		return login.getValues();
	}
	

	/**
	 * ViewLogin object reuse clean references
	 */
	public void clean(){
		this.username = null;
		this.password = null;
		this.repoHashid = Constants.EMPTY_INT;
	}

	/**
	 * ViewLogin object reuse reConstructor
	 * 
	 * @param username
	 * @param password
	 */
	public void reuse(String username, String password){
		setUsername(username);
		setPassword(password);
	}

	@Override
	public String toString() {
		return "Username: "+getUsername()+" Password: "+getPassword();
	}
	
	
	
	// Parcelable stuff //
	
	
	public static final Parcelable.Creator<ViewDisplayLogin> CREATOR = new
			Parcelable.Creator<ViewDisplayLogin>() {

		@Override
		public ViewDisplayLogin createFromParcel(Parcel in) {
			return new ViewDisplayLogin(in);
		}

		public ViewDisplayLogin[] newArray(int size) {
			return new ViewDisplayLogin[size];
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

	private ViewDisplayLogin(Parcel in){
		readFromParcel(in);
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(this.username);
		out.writeString(this.password);
		out.writeInt(this.repoHashid);
	}

	public void readFromParcel(Parcel in) {
		this.username = in.readString();
		this.password = in.readString();
		this.repoHashid = in.readInt();
	}
		
	
	
	
}
