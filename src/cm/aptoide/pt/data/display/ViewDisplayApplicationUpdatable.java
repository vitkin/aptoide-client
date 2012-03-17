/**
 * ViewDisplayApplicationUpdatable,		part of Aptoide's data model
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
 * ViewDisplayApplicationUpdatable, models an Updatable Application
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class ViewDisplayApplicationUpdatable extends ViewDisplayApplication{

	private String installedVersionName;
	private float stars;
	private int downloads;
	
	

	/**
	 * ViewDisplayApplicationUpdatable Constructor
	 *
	 * @param appHashid
	 * @param appName
	 * @param installedVersionName
	 * @param upToDateVersionName
	 * @param updatestars
	 * @param updateDownloads
	 */
	public ViewDisplayApplicationUpdatable(int appHashid, String appName, String installedVersionName, String upToDateVersionName, float updatestars, int updateDownloads) {
		super(appHashid, appName, upToDateVersionName);
		
		this.installedVersionName = installedVersionName;
		this.stars = updatestars;
		this.downloads = updateDownloads;
	}

	public float getStars() {
		return this.stars;
	}

	public int getDownloads() {
		return this.downloads;
	}

	public String getInstalledVersionName() {
		return this.installedVersionName;
	}
	

	/**
	 * ViewDisplayApplicationUpdatable object reuse clean references
	 *
	 * @param String uri
	 */
	public void clean(){
		super.clean();
		
		this.installedVersionName = null;
		this.stars = Constants.EMPTY_INT;
		this.downloads = Constants.EMPTY_INT;
	}

	/**
	 * ViewDisplayApplicationUpdatable object reuse reConstructor
	 *
	 * @param appHashid
	 * @param appName
	 * @param installedVersionName
	 * @param upToDateVersionName
	 */
	public void reuse(int appHashid, String appName, String installedVersionName, String upToDateVersionName, float updatestars, int updateDownloads) {
		super.reuse(appHashid, appName, upToDateVersionName);
		
		this.installedVersionName = installedVersionName;
		this.stars = updatestars;
		this.downloads = updateDownloads;
	}


	@Override
	public String toString() {
		return super.toString()+" InstalledVersion: "+installedVersionName+" Downloads: "+downloads+" Stars: "+stars;
	}
	
	
	
	// Parcelable stuff //
	
	
	public static final Parcelable.Creator<ViewDisplayApplicationUpdatable> CREATOR = new Parcelable.Creator<ViewDisplayApplicationUpdatable>() {
		public ViewDisplayApplicationUpdatable createFromParcel(Parcel in) {
			return new ViewDisplayApplicationUpdatable(in);
		}

		public ViewDisplayApplicationUpdatable[] newArray(int size) {
			return new ViewDisplayApplicationUpdatable[size];
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

	private ViewDisplayApplicationUpdatable(Parcel in){
		readFromParcel(in);
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(appHashid);
		out.writeString(appName);
		out.writeString(versionName);
		out.writeString(iconCachePath);
		out.writeString(installedVersionName);
		out.writeFloat(stars);
		out.writeInt(downloads);
	}

	@Override
	public void readFromParcel(Parcel in) {
		appHashid = in.readInt();
		appName = in.readString();
		versionName = in.readString();
		iconCachePath = in.readString();
		installedVersionName = in.readString();
		stars = in.readFloat();
		downloads = in.readInt();
	}

}
