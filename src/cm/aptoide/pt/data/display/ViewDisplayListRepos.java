/**
 * ViewDisplayListRepos,		part of Aptoide's data model
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
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * ViewDisplayListRepos, models a list of Repos' display info,
 * 			 maintains insertion order
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class ViewDisplayListRepos implements Parcelable{

	private ArrayList<Map<String, Object>> reposList;
	
	private HashMap<Integer, ViewDisplayRepo> reposHash;

	
	/**
	 * ViewDisplayListReposConstructor
	 * 
	 * @param ViewDisplayRepo store
	 */
	public ViewDisplayListRepos(ViewDisplayRepo repo) {
		this(1);
		addRepo(repo);
	}
	
	/**
	 * ViewDisplayListRepos Constructor
	 */
	public ViewDisplayListRepos(int size) {
		this.reposList = new ArrayList<Map<String,Object>>(size);
		this.reposHash = new HashMap<Integer, ViewDisplayRepo>(size);
	}
	
	
	public void addRepo(ViewDisplayRepo repo){
		this.reposHash.put(repo.getRepoHashid(), repo);
		this.reposList.add(repo.getDiplayMap());
	}
	
	public void addAll(ViewDisplayListRepos repos){
		this.reposHash.putAll(repos.getHashMap());
		this.reposList.addAll(repos.getList());
	}
	
	public void removeRepo(int repoHashid){
		ViewDisplayRepo repo = this.reposHash.remove(repoHashid);
		this.reposList.remove(repo.getArrayIndex());		
	}
	
	public Map<String, Object> getRepoMap(int index){
		return this.reposList.get(index);
	}
	
	public ViewDisplayRepo getRepo(int repoHashid){
		return this.reposHash.get(repoHashid);
	}
	
	
	public void regenerateList(){
		ArrayList<Map<String, Object>> newList = new ArrayList<Map<String,Object>>();
		
		for (ViewDisplayRepo repo : this.reposHash.values()) {
			newList.add(repo.getDiplayMap());
		}
		
		this.reposList = newList;
	}
	
	
	
	public HashMap<Integer, ViewDisplayRepo> getHashMap(){
		return this.reposHash;
	}
	
	
	/**
	 * getList, retrieves repos list,
	 * 			maintains insertion order
	 * 
	 * @return ArrayList<Map<String, Object>> reposList
	 */
	public ArrayList<Map<String, Object>> getList(){
		return this.reposList;
	}

	
	/**
	 * ViewDisplayListRepos object reuse clean references
	 */
	public void clean(){
		this.reposList = null;
	}
	
	/**
	 * ViewDisplayListRepos object reuse reConstructor
	 */
	public void reuse() {
		this.reposList = new ArrayList<Map<String, Object>>(1);
	}
	
	/**
	 * ViewDisplayListRepos object reuse reConstructor
	 * 
	 * @param ViewDisplayRepo repo
	 */
	public void reuse(ViewDisplayRepo repo) {
		reuse();
		addRepo(repo);
	}


	@Override
	public String toString() {
		StringBuilder listRepos = new StringBuilder("Repos: ");
		for (Map<String,Object> repo : getList()) {
			listRepos.append("repo: ");
			for (Entry<String, Object> appDetail : repo.entrySet()) {
				listRepos.append(appDetail.getKey()+"-"+appDetail.getValue()+" ");
			}
		}
		return listRepos.toString();
	}
	
	
	
	// Parcelable stuff //
	
	
	public static final Parcelable.Creator<ViewDisplayListRepos> CREATOR = new
			Parcelable.Creator<ViewDisplayListRepos>() {
		public ViewDisplayListRepos createFromParcel(Parcel in) {
			return new ViewDisplayListRepos(in);
		}

		public ViewDisplayListRepos[] newArray(int size) {
			return new ViewDisplayListRepos[size];
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

	private ViewDisplayListRepos(Parcel in){
		readFromParcel(in);
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeSerializable(reposList);	//TODO use list or parcelable instead of serializable
		out.writeSerializable(reposHash);
	}

	@SuppressWarnings("unchecked")
	public void readFromParcel(Parcel in) {
		reposList = (ArrayList<Map<String, Object>>) in.readSerializable();
		reposHash = (HashMap<Integer, ViewDisplayRepo>) in.readSerializable();
	}
	
}
