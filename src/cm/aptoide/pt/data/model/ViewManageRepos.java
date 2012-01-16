/**
 * ViewManageRepos,		part of Aptoide's data model
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

package cm.aptoide.pt.data.model;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;
import cm.aptoide.pt.data.display.ViewDisplayRepo;

/**
 * ViewManageRepos, models changes to Repos to commit to Database
 * 					, removals, insertions and toggleInUses
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class ViewManageRepos implements Parcelable{

	private ViewListIds reposToRemove;
	private ArrayList<ViewDisplayRepo> reposToInsert;
	private ViewListIds reposToSetInUse;
	private ViewListIds reposToUnsetInUse;
	
	/**
	 * ViewManageRepos Constructor
	 */
	public ViewManageRepos(ViewListIds reposToRemove, ArrayList<ViewDisplayRepo> reposToInsert, ViewListIds reposToSetInUse, ViewListIds reposToUnsetInUse) {
		this.reposToRemove = reposToRemove;
		this.reposToInsert = reposToInsert;
		this.reposToSetInUse = reposToSetInUse;
		this.reposToUnsetInUse = reposToUnsetInUse;
	}
	
	public ViewListIds getReposToRemove(){
		return reposToRemove;
	}
	
	public ArrayList<ViewDisplayRepo> getReposToInsert() {
		return reposToInsert;
	}
	
	public ViewListIds getReposToSetInUse() {
		return reposToSetInUse;
	}

	public ViewListIds getReposToUnsetInUse() {
		return reposToUnsetInUse;
	}

	
	
	
	
	// Parcelable stuff //
	

	public static final Parcelable.Creator<ViewManageRepos> CREATOR = new
			Parcelable.Creator<ViewManageRepos>() {
		public ViewManageRepos createFromParcel(Parcel in) {
			return new ViewManageRepos(in);
		}

		public ViewManageRepos[] newArray(int size) {
			return new ViewManageRepos[size];
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

	private ViewManageRepos(Parcel in){
		readFromParcel(in);
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeSerializable(reposToRemove);	//TODO use list or parcelable instead of serializable
		out.writeSerializable(reposToInsert);
		out.writeSerializable(reposToSetInUse);
		out.writeSerializable(reposToUnsetInUse);
	}

	@SuppressWarnings("unchecked")
	public void readFromParcel(Parcel in) {
		reposToRemove = (ViewListIds) in.readSerializable();
		reposToInsert = (ArrayList<ViewDisplayRepo>) in.readSerializable();
		reposToSetInUse = (ViewListIds) in.readSerializable();
		reposToUnsetInUse = (ViewListIds) in.readSerializable();
	}
	
}
