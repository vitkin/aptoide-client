/**
 * ScreenDimensions,	 auxiliary class to Aptoide's ServiceData
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
package cm.aptoide.pt.data.display;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * ScreenDimensions, models the aptoide client's screen dimensions
 *
 * @author dsilveira
 * @since 3.0
 *
 */
public class ViewDisplayListsDimensions implements Parcelable{
	private int increaseTrigger;
	private int decreaseTrigger;
	private int pageSize;
	private int cacheSize;
	
	public ViewDisplayListsDimensions(int width, int height, float density) {
		this.width = width;
		this.height = height;
		this.density = density;
	}

	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public float getDensity(){
		return density;
	}

	@Override
	public String toString() {
		return "Width: "+width+" Height: "+height+" Density: "+density;
	}

	
	// Parcelable stuff //
	
	
	public static final Parcelable.Creator<ViewDisplayListsDimensions> CREATOR = new
			Parcelable.Creator<ViewDisplayListsDimensions>() {
			        public ViewDisplayListsDimensions createFromParcel(Parcel in) {
			            return new ViewDisplayListsDimensions(in);
			        }

			        public ViewDisplayListsDimensions[] newArray(int size) {
			            return new ViewDisplayListsDimensions[size];
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
	
	private ViewDisplayListsDimensions(Parcel in){
		readFromParcel(in);
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(width);
		out.writeInt(height);
	}
	
	public void readFromParcel(Parcel in) {
		width = in.readInt();
		height = in.readInt();
	}
	
}
