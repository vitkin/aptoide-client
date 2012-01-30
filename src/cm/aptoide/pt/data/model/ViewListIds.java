/**
 * ViewListIds,		part of Aptoide's data model
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

import java.io.Serializable;
import java.util.ArrayList;

 /**
 * ViewListIds, models an list of Ids
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class ViewListIds implements Serializable{

	private static final long serialVersionUID = -6067969538026377610L;
	private ArrayList<Integer> idList;
	
	/**
	 * ViewListIds Constructor, creates new empty list
	 */
	public ViewListIds() {
		this.idList = new ArrayList<Integer>();
	}
	
	/**
	 * ViewListIds Constructor, creates new list with id already inserted
	 * 
	 * @param String id
	 */
	public ViewListIds(Integer id) {
		this();
		addId(id);
	}
	
	
	public void addId(Integer id){
		this.idList.add(id);		
	}
	
	public void removeId(Integer id){
		this.idList.remove(id);		
	}
	
	
	public ArrayList<Integer> getList(){
		return this.idList;
	}
	
	public boolean isEmpty(){
		return this.idList.isEmpty();
	}
	
	

	/**
	 * ViewListIds object reuse clean references
	 */
	public void clean(){
		this.idList = null;
	}
	
	/**
	 * ViewListIds object reuse reConstructor
	 */
	public void reuse() {
		this.idList = new ArrayList<Integer>();
	}
	
	/**
	 * ViewListIds object reuse reConstructor
	 * 
	 * @param String id
	 */
	public void reuse(Integer id) {
		reuse();
		addId(id);
	}

	

	@Override
	public String toString() {
		return idList.toString();
	}
	
}
