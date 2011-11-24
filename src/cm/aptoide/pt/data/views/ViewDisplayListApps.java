/**
 * ViewListApps,		part of Aptoide's data model
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

package cm.aptoide.pt.data.views;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import android.os.Parcel;
import android.os.Parcelable;

 /**
 * ViewDisplayListApps, models a list of Apps,
 * 			 maintains insertion order
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class ViewDisplayListApps implements Parcelable,Serializable{

	private static final long serialVersionUID = -3819976171445335382L;
	private ArrayList<Map<String, Object>> appsList;

	
	/**
	 * ViewDisplayListApps Constructor
	 * 
	 * @param ViewDisplayApplication app
	 */
	public ViewDisplayListApps(ViewDisplayApplication app) {
		this(1);
		addApp(app);
	}
	
	/**
	 * ViewDisplayListApps Constructor
	 */
	public ViewDisplayListApps(int size) {
		this.appsList = new ArrayList<Map<String, Object>>(size);
	}
	
	
	public void addApp(ViewDisplayApplication app){
		this.appsList.add(app.getDiplayMap());		
	}
	
	public void removeApp(int index){
		this.appsList.remove(index);		
	}
	
	public Map<String, Object> getApp(int index){
		return this.appsList.get(index);
	}
	
	
	/**
	 * getList, retrieves apps list,
	 * 			maintains insertion order
	 * 
	 * @return ArrayList<Map<String, Object>> appsList
	 */
	public ArrayList<Map<String, Object>> getList(){
		return this.appsList;
	}

	

	/**
	 * ViewDisplayListApps object reuse, clean references
	 */
	public void clean(){
		this.appsList = null;
	}

	/**
	 * ViewDisplayListApps object reuse, reConstructor
	 * 
	 * @param int size
	 */
	public void reuse(int size) {
		this.appsList = new ArrayList<Map<String, Object>>(size);
	}

	/**
	 * ViewDisplayListApps object reuse, reConstructor
	 * 
	 * @param ViewDisplayApplication app
	 */
	public void reuse(ViewDisplayApplication app) {
		reuse(1);
		addApp(app);
	}


	@Override
	public String toString() {
		StringBuilder listApps = new StringBuilder("Apps: ");
		for (Map<String,Object> app : getList()) {
			listApps.append("app: ");
			for (Entry<String, Object> appDetail : app.entrySet()) {
				listApps.append(appDetail.getKey()+"-"+appDetail.getValue()+" ");
			}
		}
		return listApps.toString();
	}
	
	
	
	// Parcelable stuff //
	
	
	public static final Parcelable.Creator<ViewDisplayListApps> CREATOR = new
			Parcelable.Creator<ViewDisplayListApps>() {
		public ViewDisplayListApps createFromParcel(Parcel in) {
			return new ViewDisplayListApps(in);
		}

		public ViewDisplayListApps[] newArray(int size) {
			return new ViewDisplayListApps[size];
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

	private ViewDisplayListApps(Parcel in){
		readFromParcel(in);
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeSerializable(appsList);
	}

	public void readFromParcel(Parcel in) {
		appsList = (ArrayList<Map<String, Object>>) in.readSerializable();	
	}
	
}
