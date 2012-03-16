/**
 * ViewDisplayApplicationInstalled,		part of Aptoide's data model
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
 * ViewDisplayApplicationInstalled, models an Installed Application
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class ViewDisplayApplicationInstalled extends ViewDisplayApplication{

	private boolean isUpdatable;
	private boolean isDowngradable;
	
	
	/**
	 * ViewDisplayApplication installed Constructor
	 *
	 * @param appHashid
	 * @param appName
	 * @param installedVersionName
	 * @param isUpdatable
	 * @param isDowngradable
	 */
	public ViewDisplayApplicationInstalled(int appHashid, String appName, String installedVersionName, boolean isUpdatable, boolean isDowngradable) {
		super(appHashid, appName, installedVersionName);
		
		this.isUpdatable = isUpdatable;
		this.isDowngradable = isDowngradable;
	}
	
	public boolean isInstalled(){
		return true;
	}

	public boolean isUpdatable() {
		return this.isUpdatable;
	}

	public boolean isDowngradable() {
		return this.isDowngradable;
	}
	
	

	/**
	 * ViewDisplayApplication object reuse clean references
	 *
	 * @param String uri
	 */
	public void clean(){
		super.clean();
		
		this.isUpdatable = false;
		this.isDowngradable = false;
	}
	
	/**
	 * ViewDisplayApplication installed object reuse reConstructor
	 *
	 * @param appHashid
	 * @param appName
	 * @param installedVersionName
	 * @param isUpdatable
	 * @param isDowngradable
	 */
	public void reuse(int appHashid, String appName, String installedVersionName, boolean isUpdatable, boolean isDowngradable) {
		super.reuse(appHashid, appName, installedVersionName);
		
		this.isUpdatable = isUpdatable;
		this.isDowngradable = isDowngradable;
	}


	@Override
	public String toString() {
		return super.toString()+" isUpdatable: "+isUpdatable+" isDowngradable: "+isDowngradable;
	}
	
	
	
	// Parcelable stuff //
	
	
	public static final Parcelable.Creator<ViewDisplayApplicationInstalled> CREATOR = new Parcelable.Creator<ViewDisplayApplicationInstalled>() {
		public ViewDisplayApplicationInstalled createFromParcel(Parcel in) {
			return new ViewDisplayApplicationInstalled(in);
		}

		public ViewDisplayApplicationInstalled[] newArray(int size) {
			return new ViewDisplayApplicationInstalled[size];
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

	private ViewDisplayApplicationInstalled(Parcel in){
		readFromParcel(in);
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(appHashid);
		out.writeString(appName);
		out.writeString(versionName);
		out.writeString(iconCachePath);
		out.writeValue(isUpdatable);
		out.writeValue(isDowngradable);
	}

	@Override
	public void readFromParcel(Parcel in) {
		appHashid = in.readInt();
		appName = in.readString();
		versionName = in.readString();
		iconCachePath = in.readString();
		isUpdatable = (Boolean) in.readValue(null);
		isDowngradable = (Boolean) in.readValue(null);
	}

}
