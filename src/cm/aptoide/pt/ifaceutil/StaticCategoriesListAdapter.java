/**
 * StaticAppsListAdapter,		part of Aptoide's data model
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

package cm.aptoide.pt.ifaceutil;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import cm.aptoide.pt.data.display.ViewDisplayApplication;

 /**
 * StaticAppsListAdapter, models a static loading apps list adapter
 * 							extends arrayAdapter
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class StaticCategoriesListAdapter extends ArrayAdapter<ViewDisplayApplication>{

	/**
	 * DynamicAppsListAdapter Constructor
	 *
	 * @param context
	 * @param textViewResourceId
	 * @param objects
	 */
	public StaticCategoriesListAdapter(Context context, int textViewResourceId, List<ViewDisplayApplication> objects) {
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
	} 

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return super.getView(position, convertView, parent);
	}
	
}
