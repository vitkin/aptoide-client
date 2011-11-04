/*
 * Category		part of Aptoide's data model
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

import android.content.ContentValues;
import cm.aptoide.pt.data.Constants;

 /**
 * Category, models a category from applications categories
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class Category {

	private ContentValues values;
	private int parentHashid;
	private boolean hasChilds;
	private ArrayList<Category> subCategories;
	
	
	public Category(String categoryName) {
		this.hasChilds = false;
		this.values = new ContentValues(Constants.NUMBER_OF_COLUMNS_CATEGORY);
		setCategoryName(categoryName);
		subCategories = new ArrayList<Category>();
		this.parentHashid = Constants.TOP_CATEGORY;
	}
	
	
	public int getHashid() {
		return values.getAsInteger(Constants.KEY_CATEGORY_HASHID);
	}
	
	private void setCategoryName(String categoryName){
		values.put(Constants.KEY_CATEGORY_NAME, categoryName);
		values.put(Constants.KEY_CATEGORY_HASHID, categoryName.hashCode());
	}

	public String getCategoryName() {
		return values.getAsString(Constants.KEY_CATEGORY_NAME);
	}
	
	public ContentValues getValues(){
		return values;
	}
	
	
	public void setParentHashid(int parentHashid){
		this.parentHashid = parentHashid;
	}
	
	public int getParentHashid(){
		return this.parentHashid;
	}
	
	
	public boolean hasChilds(){
		return hasChilds;
	}
		
	public void addChild(Category subCategory){
		subCategory.setParentHashid(getHashid());
		subCategories.add(subCategory);
		if(!this.hasChilds){
			this.hasChilds = true;
		}
	}
	
	public ArrayList<Category> getSubCategories(){	//TODO return null Category object if !hasChilds
		return subCategories;
	}
	
	
	public void clean(){
		this.values = null;
		this.hasChilds = false;
		this.subCategories = null;
		this.parentHashid = Constants.TOP_CATEGORY;
	}
	
	public void reuse(String categoryName) {
		this.hasChilds = false;
		this.values = new ContentValues(Constants.NUMBER_OF_COLUMNS_CATEGORY);
		setCategoryName(categoryName);
		this.parentHashid = Constants.TOP_CATEGORY;
	}
	
}
