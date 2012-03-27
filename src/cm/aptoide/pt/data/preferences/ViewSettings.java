/**
 * ViewSettings,	 auxiliary class to Aptoide's ServiceData
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
package cm.aptoide.pt.data.preferences;

import cm.aptoide.pt.data.webservices.ViewIconDownloadPermissions;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * ViewSettings, models the aptoide client's settings
 *
 * @author dsilveira
 * @since 3.0
 *
 */
public class ViewSettings implements Parcelable{
	private ViewIconDownloadPermissions iconDownloadPermissions;
	private boolean isHwFilterOn;
	private boolean isAutomaticInstallOn;
	
	public ViewSettings(ViewIconDownloadPermissions iconDownloadPermissions, boolean isHwFilterOn, boolean isAutomaticInstallOn) {
		this.iconDownloadPermissions = iconDownloadPermissions;
		this.isHwFilterOn = isHwFilterOn;
		this.isAutomaticInstallOn = isAutomaticInstallOn;
	}
	
	public ViewIconDownloadPermissions getIconDownloadPermissions(){
		return iconDownloadPermissions;
	}

	public boolean isHwFilterOn() {
		return isHwFilterOn;
	}

	public boolean isAutomaticInstallOn() {
		return isAutomaticInstallOn;
	}

	@Override
	public String toString() {
		return iconDownloadPermissions+" isHwFilterOn: "+isHwFilterOn+" isAutomaticInstallOn: "+isAutomaticInstallOn;
	}

	
	// Parcelable stuff //
	
	
	public static final Parcelable.Creator<ViewSettings> CREATOR = new
			Parcelable.Creator<ViewSettings>() {
			        public ViewSettings createFromParcel(Parcel in) {
			            return new ViewSettings(in);
			        }

			        public ViewSettings[] newArray(int size) {
			            return new ViewSettings[size];
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
	
	private ViewSettings(Parcel in){
		readFromParcel(in);
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeParcelable(iconDownloadPermissions, flags);
		out.writeValue(isHwFilterOn);
		out.writeValue(isAutomaticInstallOn);
	}
	
	public void readFromParcel(Parcel in) {
		iconDownloadPermissions = in.readParcelable(ViewIconDownloadPermissions.class.getClassLoader());
		isHwFilterOn = (Boolean) in.readValue(null);
		isAutomaticInstallOn = (Boolean) in.readValue(null);
	}
	
}
