/**
 * ViewIconDownloadPermissions,	 auxiliary class to Aptoide's ServiceData
 * Copyright (C) 2011 Duarte Silveira
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
package cm.aptoide.pt.data.downloads;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * ViewIconDownloadPermissions, models the aptoide icon download permissions
 *
 * @author dsilveira
 * @since 3.0
 *
 */
public class ViewIconDownloadPermissions implements Parcelable{
	private boolean isWiFi;
	private boolean isEthernet;
	private boolean is4G;
	private boolean is3G;
	private boolean is2G;
	
	public ViewIconDownloadPermissions(boolean isWiFi, boolean isEthernet, boolean is3G, boolean is4G, boolean is2G) {
		this.isWiFi = isWiFi;
		this.isEthernet = isEthernet;
		this.is4G = is4G;
		this.is3G = is3G;
		this.is2G = is2G;
	}
	

	public boolean isWiFi() {
		return isWiFi;
	}

	public boolean isEthernet() {
		return isEthernet;
	}

	public boolean is4G() {
		return is4G;
	}

	public boolean is3G() {
		return is3G;
	}

	public boolean is2G() {
		return is2G;
	}
	
	public boolean isNever() {
		return (!isWiFi && !isEthernet && !is4G && !is3G && !is2G);
	}

	@Override
	public String toString() {
		return " isWiFi: "+isWiFi+" isEthernet: "+isEthernet+" is4G: "+is4G+" is3G: "+is3G+" is2G: "+is2G;
	}

	
	// Parcelable stuff //
	
	
	public static final Parcelable.Creator<ViewIconDownloadPermissions> CREATOR = new
			Parcelable.Creator<ViewIconDownloadPermissions>() {
			        public ViewIconDownloadPermissions createFromParcel(Parcel in) {
			            return new ViewIconDownloadPermissions(in);
			        }

			        public ViewIconDownloadPermissions[] newArray(int size) {
			            return new ViewIconDownloadPermissions[size];
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
	
	private ViewIconDownloadPermissions(Parcel in){
		readFromParcel(in);
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeValue(isWiFi);
		out.writeValue(isEthernet);
		out.writeValue(is4G);
		out.writeValue(is3G);
		out.writeValue(is2G);
	}
	
	public void readFromParcel(Parcel in) {
		isWiFi = (Boolean) in.readValue(null);
		isEthernet = (Boolean) in.readValue(null);
		is4G = (Boolean) in.readValue(null);
		is3G = (Boolean) in.readValue(null);
		is2G = (Boolean) in.readValue(null);
	}
	
}
