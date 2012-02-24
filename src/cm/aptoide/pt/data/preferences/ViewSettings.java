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
	private boolean isHwFilterOn;
	
	public ViewSettings(boolean isHwFilterOn) {
		this.isHwFilterOn = isHwFilterOn;
	}

	public boolean isHwFilterOn() {
		return isHwFilterOn;
	}

	@Override
	public String toString() {
		return "isHwFilterOn: "+isHwFilterOn;
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
		out.writeValue(isHwFilterOn);
	}
	
	public void readFromParcel(Parcel in) {
		isHwFilterOn = (Boolean) in.readValue(null);
	}
	
}
