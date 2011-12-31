/**
 * ViewDisplayAppVersions,		part of Aptoide's data model
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

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

 /**
 * ViewDisplayAppVersions, models all versions of an application
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class ViewDisplayAppVersionsInfo implements Parcelable{

	private ArrayList<ViewDisplayAppVersionInfo> versionsList;
	
	
	/**
	 * ViewDisplayAppVersions Constructor
	 *
	 */
	public ViewDisplayAppVersionsInfo() {
		this.versionsList = new ArrayList<ViewDisplayAppVersionInfo>();
	}
	
	/**
	 * ViewDisplayAppVersions Constructor
	 *
	 * @param appVersionInfo
	 */
	public ViewDisplayAppVersionsInfo(ViewDisplayAppVersionInfo appVersionInfo) {
		this();
		addAppVersionInfo(appVersionInfo);
	}
	
	public void addAppVersionInfo(ViewDisplayAppVersionInfo appVersionInfo){
		this.versionsList.add(appVersionInfo);
	}
	
	public ArrayList<ViewDisplayAppVersionInfo> getVersionsList(){
		return this.versionsList;
	}
	

	/**
	 * ViewDisplayApplication object reuse clean references
	 *
	 * @param String uri
	 */
	public void clean(){
		this.versionsList = null;
	}
	
	/**
	 * ViewDisplayApplication available object reuse reConstructor
	 *
	 * @param appVersionInfo
	 */
	public void reuse() {
		this.versionsList = new ArrayList<ViewDisplayAppVersionInfo>();
	}
	
	/**
	 * ViewDisplayApplication available object reuse reConstructor
	 *
	 * @param appVersionInfo
	 */
	public void reuse(ViewDisplayAppVersionInfo appVersionInfo) {
		reuse();
		addAppVersionInfo(appVersionInfo);
	}


	@Override
	public String toString() {
		StringBuilder string = new StringBuilder();
		
		for (ViewDisplayAppVersionInfo appVersion : versionsList) {
			string.append(appVersion.toString());
		}
		
		return string.toString(); 
	}
	
	
	
	// Parcelable stuff //
	
	
	public static final Parcelable.Creator<ViewDisplayAppVersionsInfo> CREATOR = new Parcelable.Creator<ViewDisplayAppVersionsInfo>() {
		public ViewDisplayAppVersionsInfo createFromParcel(Parcel in) {
			return new ViewDisplayAppVersionsInfo(in);
		}

		public ViewDisplayAppVersionsInfo[] newArray(int size) {
			return new ViewDisplayAppVersionsInfo[size];
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

	private ViewDisplayAppVersionsInfo(Parcel in){
		readFromParcel(in);
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {		//TODO deprecate serializables in favor of parcels which are supposed to be faster
//		out.writeList(versionsList);	
		out.writeSerializable(versionsList);
	}

	@SuppressWarnings("unchecked")
	public void readFromParcel(Parcel in) {
//		in.readList(versionsList, cm.aptoide.pt.data.display.ViewDisplayAppVersionInfo.class.getClassLoader());
		versionsList = (ArrayList<ViewDisplayAppVersionInfo>) in.readSerializable();
	}
		
}
