/**
 * ViewDisplayAppVersionExtras,		part of Aptoide's data model
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
import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;
import cm.aptoide.pt.data.util.Constants;

 /**
 * ViewDisplayAppVersionExtras, models a single version of an application's extras
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class ViewDisplayAppVersionExtras implements Parcelable, Serializable{

	private static final long serialVersionUID = -3516586429861083636L;
	private int appFullHashid;
	private String description;
//	private boolean screensAvailable = false;
//	private ArrayList<String> localScreensPath;
	
	
	/**
	 * ViewDisplayAppVersionExtras Constructor
	 *
	 * @param appFullHashid
	 * @param description
	 */
	public ViewDisplayAppVersionExtras(int appFullHashid, String description){
//		this.localScreensPath = new ArrayList<String>();
		this.appFullHashid = appFullHashid;
		this.description = description;
	}
	
	
	public int getAppFullHashid() {
		return appFullHashid;
	}

	public String getDescription() {
		return description;
	}

//	public boolean isScreensAvailable() {
//		return screensAvailable;
//	}

//	public void addLocalScreenPath(String localScreenPath){
//		if(!screensAvailable){
//			screensAvailable = true;
//		}
//		localScreensPath.add(localScreenPath);
//	}
//	
//	public ArrayList<String> getLocalScreensPath() {
//		return localScreensPath;
//	}



	/**
	 * ViewDisplayAppVersionExtras object reuse clean references
	 *
	 * @param String uri
	 */
	public void clean(){
		this.appFullHashid = Constants.EMPTY_INT;
		this.description = null;
//		this.screensAvailable = false;
//		this.localScreensPath = null;
	}
	
	/**
	 * ViewDisplayAppVersionExtras object reuse reConstructor
	 *
	 * @param appFullHashid
	 * @param description
	 */
	public void reuse(int appFullHashid, String description){
//		this.screensAvailable = false;
//		this.localScreensPath = new ArrayList<String>();
		this.appFullHashid = appFullHashid;
		this.description = description;
	}


	@Override
	public int hashCode() {
		return this.appFullHashid;
	}


	@Override
	public boolean equals(Object object) {
		if(object instanceof ViewDisplayAppVersionExtras){
			ViewDisplayAppVersionExtras app = (ViewDisplayAppVersionExtras) object;
			if(app.hashCode() == this.hashCode()){
				return true;
			}
		}
		return false;
	}


	@Override
	public String toString() {
		StringBuilder string = new StringBuilder(" AppFullHashid: "+appFullHashid+" Description: "+description);

//		if(screensAvailable){
//			for (String localScreenPath : localScreensPath) {
//				string.append(" Screen: "+localScreenPath);
//			}
//		}
		
		return string.toString();
	}
	
	
	
	// Parcelable stuff //
	
	
		public static final Parcelable.Creator<ViewDisplayAppVersionExtras> CREATOR = new Parcelable.Creator<ViewDisplayAppVersionExtras>() {
			public ViewDisplayAppVersionExtras createFromParcel(Parcel in) {
				return new ViewDisplayAppVersionExtras(in);
			}

			public ViewDisplayAppVersionExtras[] newArray(int size) {
				return new ViewDisplayAppVersionExtras[size];
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

		private ViewDisplayAppVersionExtras(Parcel in){
			readFromParcel(in);
		}

		@Override
		public void writeToParcel(Parcel out, int flags) {
			out.writeInt(appFullHashid);
			out.writeString(description);
			
//			out.writeByte(screensAvailable?(byte)1:(byte)0);
//			if(screensAvailable){
//				out.writeStringList(localScreensPath);
//			}
		}

		public void readFromParcel(Parcel in) {
			appFullHashid = in.readInt();
			description = in.readString();
			
//			screensAvailable = in.readByte()==1?true:false;
//			if(screensAvailable){
//				in.readStringList(localScreensPath);
//			}
		}
		
}
