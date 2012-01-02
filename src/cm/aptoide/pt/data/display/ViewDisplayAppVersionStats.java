/**
 * ViewDisplayAppVersionStats,		part of Aptoide's data model
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

import android.os.Parcel;
import android.os.Parcelable;
import cm.aptoide.pt.data.Constants;

 /**
 * ViewDisplayAppVersionStats, models a single version of an application's stats
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class ViewDisplayAppVersionStats implements Parcelable, Serializable{

	private static final long serialVersionUID = 1871565658004707259L;
	private int appFullHashid;
	private int likes;
	private int dislikes;
	private float stars;
	private int downloads;
	

	/**
	 * ViewDisplayAppVersionStatsConstructor
	 *
	 * @param appFullHashid
	 * @param likes
	 * @param dislikes
	 * @param stars
	 * @param downloads
	 */
	public ViewDisplayAppVersionStats(int appFullHashid, int likes, int dislikes, float stars, int downloads){
		this.appFullHashid = appFullHashid;
		this.likes = likes;
		this.dislikes = dislikes;
		this.stars = stars;
		this.downloads = downloads;
	}
	
	

	public int getAppFullHashid() {
		return appFullHashid;
	}

	public int getLikes() {
		return likes;
	}

	public int getDislikes() {
		return dislikes;
	}

	public float getStars() {
		return stars;
	}

	public int getDownloads() {
		return downloads;
	}



	/**
	 * ViewDisplayAppVersionStats object reuse clean references
	 *
	 * @param String uri
	 */
	public void clean(){
		this.appFullHashid = Constants.EMPTY_INT;
		this.likes = Constants.EMPTY_INT;
		this.dislikes = Constants.EMPTY_INT;
		this.stars = Constants.EMPTY_INT;
		this.downloads = Constants.EMPTY_INT;
	}
	
	/**
	 * ViewDisplayAppVersionStats object reuse reConstructor
	 *
	 * @param appFullHashid
	 * @param likes
	 * @param dislikes
	 * @param stars
	 * @param downloads
	 */
	public void reuse(int appFullHashid, int likes, int dislikes, float stars, int downloads){
		this.appFullHashid = appFullHashid;
		this.likes = likes;
		this.dislikes = dislikes;
		this.stars = stars;
		this.downloads = downloads;
	}


	@Override
	public int hashCode() {
		return this.appFullHashid;
	}


	@Override
	public boolean equals(Object object) {
		if(object instanceof ViewDisplayAppVersionStats){
			ViewDisplayAppVersionStats stats = (ViewDisplayAppVersionStats) object;
			if(stats.hashCode() == this.hashCode()){
				return true;
			}
		}
		return false;
	}


	@Override
	public String toString() {
		return "  AppFullHashid: "+appFullHashid+" Likes: "+likes+" Dislikes: "+dislikes+" Stars: "+stars+" Downloads: "+downloads;
	}
	
	
	
	// Parcelable stuff //
	
	
		public static final Parcelable.Creator<ViewDisplayAppVersionStats> CREATOR = new Parcelable.Creator<ViewDisplayAppVersionStats>() {
			public ViewDisplayAppVersionStats createFromParcel(Parcel in) {
				return new ViewDisplayAppVersionStats(in);
			}

			public ViewDisplayAppVersionStats[] newArray(int size) {
				return new ViewDisplayAppVersionStats[size];
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

		private ViewDisplayAppVersionStats(Parcel in){
			readFromParcel(in);
		}

		@Override
		public void writeToParcel(Parcel out, int flags) {
			out.writeInt(appFullHashid);
			out.writeInt(likes);
			out.writeInt(dislikes);
			out.writeFloat(stars);
			out.writeInt(downloads);
		}

		public void readFromParcel(Parcel in) {
			appFullHashid = in.readInt();
			likes = in.readInt();
			dislikes = in.readInt();
			stars = in.readFloat();
			downloads = in.readInt();
		}
		
}
