/**
 * ViewLogin,		auxilliary class to Aptoide's ServiceData
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

package cm.aptoide.pt.data.views;

import java.util.ArrayList;
import java.util.Map;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;
import cm.aptoide.pt.data.Constants;


 /**
 * ViewLogin, models a download's authentication
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class ViewLogin implements Parcelable{


	private ContentValues loginValues;
	

	/**
	 * ViewLogin Constructor
	 *
	 * @param username
	 * @param password
	 */
	public ViewLogin(String username, String password) {
		loginValues = new ContentValues(Constants.NUMBER_OF_COLUMNS_LOGIN);
		setUsername(username);
		setPassword(password);
	}

	private void setUsername(String username){
		loginValues.put(Constants.KEY_LOGIN_USERNAME, username);
	}

	private void setPassword(String password){
		loginValues.put(Constants.KEY_LOGIN_PASSWORD, password);
	}

	public String getUsername() {
		return loginValues.getAsString(Constants.KEY_LOGIN_USERNAME);
	}

	public String getPassword() {
		return loginValues.getAsString(Constants.KEY_LOGIN_PASSWORD);
	}
	
	public void setRepoHashid(int repoHashid){
		loginValues.put(Constants.KEY_LOGIN_REPO_HASHID, repoHashid);
	}
	
	public ContentValues getValues(){
		return loginValues;
	}
	

	/**
	 * ViewLogin object reuse clean references
	 */
	public void clean(){
		this.loginValues = null;
	}

	/**
	 * ViewLogin object reuse reConstructor
	 * 
	 * @param username
	 * @param password
	 */
	public void reuse(String username, String password){
		loginValues = new ContentValues(Constants.NUMBER_OF_COLUMNS_LOGIN);
		setUsername(username);
		setPassword(password);
	}

	@Override
	public String toString() {
		return "Username: "+getUsername()+" Password: "+getPassword();
	}
	
	
	
	// Parcelable stuff //
	
	
	public static final Parcelable.Creator<ViewLogin> CREATOR = new
			Parcelable.Creator<ViewLogin>() {

		@Override
		public ViewLogin createFromParcel(Parcel in) {
			return new ViewLogin(in);
		}

		public ViewLogin[] newArray(int size) {
			return new ViewLogin[size];
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

	private ViewLogin(Parcel in){
		readFromParcel(in);
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeParcelable(loginValues, 0);
	}

	public void readFromParcel(Parcel in) {
		loginValues = in.readParcelable(null);
	}
		
	
	
	
}
