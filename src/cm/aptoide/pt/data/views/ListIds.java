/*
 * ListIds		part of Aptoide's data model
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

import java.util.ArrayList;

 /**
 * ListIds, models an list of Ids
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class ListIds {

	private ArrayList<String> idList;

	
	/**
	 * Icon Constructor
	 * 
	 * @param String id
	 */
	public ListIds(String id) {
		this.idList = new ArrayList<String>();
		addId(id);
	}
	
	
	public void addId(String id){
		this.idList.add(id);		
	}
	
	public void removeId(String id){
		this.idList.remove(id);		
	}
	
	
	public ArrayList<String> getList(){
		return this.idList;
	}

	
	
	public void clean(){
		this.idList = null;
	}
	
	public void reuse(String id) {
		this.idList = new ArrayList<String>();
		addId(id);
	}
	
}
