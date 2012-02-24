/**
 * ViewHwFilters,	 auxiliary class to Aptoide's ServiceData
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
package cm.aptoide.pt.data.system;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * ViewHwFilters, models the aptoide client's screen dimensions
 *
 * @author dsilveira
 * @since 3.0
 *
 */
public class ViewHwFilters implements Parcelable{
	private int sdkVersion;
	private int screenSize;
	private float glEsVersion;
	
	
	/**
	 * ViewHwFilters Constructor
	 *
	 * @param int sdkVersion
	 * @param int screenSize
	 * @param float glEsVersion
	 */
	public ViewHwFilters(int sdkVersion, int screenSize, float glEsVersion) {
		this.sdkVersion = sdkVersion;
		this.screenSize = screenSize;
		this.glEsVersion = glEsVersion;
	}


	public int getSdkVersion() {
		return sdkVersion;
	}

	public int getScreenSize() {
		return screenSize;
	}

	public float getGlEsVersion() {
		return glEsVersion;
	}


	@Override
	public String toString() {
		return "sdkVersion: "+sdkVersion+" screenSize: "+screenSize+" glEsVersion: "+glEsVersion;
	}

	
	// Parcelable stuff //
	
	
	public static final Parcelable.Creator<ViewHwFilters> CREATOR = new
			Parcelable.Creator<ViewHwFilters>() {
			        public ViewHwFilters createFromParcel(Parcel in) {
			            return new ViewHwFilters(in);
			        }

			        public ViewHwFilters[] newArray(int size) {
			            return new ViewHwFilters[size];
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
	
	private ViewHwFilters(Parcel in){
		readFromParcel(in);
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(sdkVersion);
		out.writeInt(screenSize);
		out.writeFloat(glEsVersion);
	}
	
	public void readFromParcel(Parcel in) {
		sdkVersion = in.readInt();
		screenSize = in.readInt();
		glEsVersion = in.readFloat();
	}
	
}
