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
package cm.aptoide.pt2.views;

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
	private boolean isWiMax;
	private boolean isMobile;
	
	public ViewIconDownloadPermissions(boolean isWiFi, boolean isEthernet, boolean isWiMax, boolean isMobile) {
		this.isWiFi = isWiFi;
		this.isEthernet = isEthernet;
		this.isWiMax = isWiMax;
		this.isMobile = isMobile;
	}
	

	public boolean isWiFi() {
		return isWiFi;
	}

	public boolean isEthernet() {
		return isEthernet;
	}

	public boolean isWiMax() {
		return isWiMax;
	}

	public boolean isMobile() {
		return isMobile;
	}
	
	public boolean isNever() {
		return (!isWiFi && !isEthernet && !isWiMax && !isMobile );
	}

	@Override
	public String toString() {
		return " isWiFi: "+isWiFi+" isEthernet: "+isEthernet+" isWiMax: "+isWiMax+" isMobile: "+isMobile;
	}


	@Override
	public int hashCode() {
		return toString().hashCode();
	}
	
	
	@Override
	public boolean equals(Object object) {
		if(object instanceof ViewIconDownloadPermissions){
			ViewIconDownloadPermissions permissions = (ViewIconDownloadPermissions) object;
			if(permissions.hashCode() == this.hashCode()){
				return true;
			}
		}
		return false;
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
		out.writeValue(isWiMax);
		out.writeValue(isMobile);
	}
	
	public void readFromParcel(Parcel in) {
		isWiFi = (Boolean) in.readValue(null);
		isEthernet = (Boolean) in.readValue(null);
		isWiMax = (Boolean) in.readValue(null);
		isMobile = (Boolean) in.readValue(null);
	}
	
}
