/**
 * ViewDisplayApplicationAvailable,		part of Aptoide's data model
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

import android.os.Parcel;
import android.os.Parcelable;
import cm.aptoide.pt.data.util.Constants;

 /**
 * ViewDisplayApplicationAvailable, models an Available Application
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class ViewDisplayApplicationAvailable extends ViewDisplayApplication{

	private float stars;
	private int downloads;
	
	

	/**
	 * ViewDisplayApplicationAvailable Constructor
	 *
	 * @param appHashid
	 * @param appName
	 * @param stars
	 * @param downloads
	 * @param versionName
	 */
	public ViewDisplayApplicationAvailable(int appHashid, String appName, float stars, int downloads, String versionName) {
		super(appHashid, appName, versionName);
		this.stars = stars;
		this.downloads = downloads;
	}
	

	public float getStars() {
		return this.stars;
	}

	public int getDownloads() {
		return this.downloads;
	}
	

	/**
	 * ViewDisplayApplicationAvailable object reuse clean references
	 *
	 * @param String uri
	 */
	public void clean(){
		super.clean();
		
		this.stars = Constants.EMPTY_INT;
		this.downloads = Constants.EMPTY_INT;
	}
	/**
	 * ViewDisplayApplicationAvailable object reuse reConstructor
	 *
	 * @param appHashid
	 * @param appName
	 * @param stars
	 * @param downloads
	 * @param versionName
	 */
	public void reuse(int appHashid, String appName, float stars, int downloads, String versionName) {
		super.reuse(appHashid, appName, versionName);
		
		this.stars = stars;
		this.downloads = downloads;
	}


	@Override
	public String toString() {
		return super.toString()+" Downloads: "+downloads+" Stars: "+stars;
	}
	
	
	
	// Parcelable stuff //
	
	
	public static final Parcelable.Creator<ViewDisplayApplicationAvailable> CREATOR = new Parcelable.Creator<ViewDisplayApplicationAvailable>() {
		public ViewDisplayApplicationAvailable createFromParcel(Parcel in) {
			return new ViewDisplayApplicationAvailable(in);
		}

		public ViewDisplayApplicationAvailable[] newArray(int size) {
			return new ViewDisplayApplicationAvailable[size];
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

	private ViewDisplayApplicationAvailable(Parcel in){
		readFromParcel(in);
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(appHashid);
		out.writeString(appName);
		out.writeString(versionName);
		out.writeString(iconCachePath);
		out.writeFloat(stars);
		out.writeInt(downloads);
	}

	@Override
	public void readFromParcel(Parcel in) {
		appHashid = in.readInt();
		appName = in.readString();
		versionName = in.readString();
		iconCachePath = in.readString();
		stars = in.readFloat();
		downloads = in.readInt();
	}

}
