/**
 * ViewListAppsDownload,		part of Aptoide's data model
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

package cm.aptoide.pt.data.webservices;

import java.util.ArrayList;
import java.util.HashMap;

 /**
 * ViewListAppsDownload, models a list of apps downloads
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class ViewListAppsDownload{

	private HashMap<Integer, ArrayList<Integer>> noInfoMap;
	private ArrayList<ViewDownload> downloadsList;
	
	/**
	 * ViewListIds Constructor, creates new empty list
	 */
	public ViewListAppsDownload() {
		this.noInfoMap = new HashMap<Integer,ArrayList<Integer>>();
		this.downloadsList = new ArrayList<ViewDownload>();
	}
	
	
	public void addNoInfoId(int repoHashid, int appHashid){
		if(!noInfoMap.containsKey(repoHashid)){
			noInfoMap.put(repoHashid, new ArrayList<Integer>());
		}
		noInfoMap.get(repoHashid).add(appHashid);
	}
	
//	public void removeNoInfoId(Integer id){
//		this.noInfoMap.remove(id);		
//	}
	
	public void addDownload(ViewDownload download){
		this.downloadsList.add(download);
	}
	
	public void removeDownload(ViewDownload download){
		this.downloadsList.remove(download);
	}
	
	
	public HashMap<Integer,ArrayList<Integer>> getNoInfoMap(){
		return this.noInfoMap;
	}
	
	public ArrayList<Integer> getNoInfoRepoAppsList(int repoHashid){
		return noInfoMap.get(repoHashid);
	}
	
	public ArrayList<ViewDownload> getDownloadsList(){
		return this.downloadsList;
	}
	
	public boolean noInfoListIsEmpty(){
		return this.noInfoMap.isEmpty();
	}
	
	public boolean downloadsListIsEmpty(){
		return this.downloadsList.isEmpty();
	}
	
	

	/**
	 * ViewListIds object reuse clean references
	 */
	public void clean(){
		this.noInfoMap = null;
		this.downloadsList = null;
	}
	
	/**
	 * ViewListIds object reuse reConstructor
	 */
	public void reuse() {
		this.noInfoMap = new HashMap<Integer, ArrayList<Integer>>();
		this.downloadsList = new ArrayList<ViewDownload>();
	}
	
	

	@Override
	public String toString() {
		return noInfoMap.toString()
				+"\n"+downloadsList;
	}
	
}
