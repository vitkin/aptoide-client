/*******************************************************************************
 * Copyright (c) 2012 rmateus.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package cm.aptoide.pt.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import cm.aptoide.pt.R;

public class ViewPagerAdapter extends PagerAdapter {

	String[] titles;
	ArrayList<View> views = new ArrayList<View>();
	private Context context; 
	
	
	public ViewPagerAdapter(Context context, ArrayList<View> views) {
		this.context=context;
		this.views = views; 
		titles = new String[]{context.getString(R.string.featured_tab),context.getString(R.string.available_tab),
				context.getString(R.string.installed_tab), context.getString(R.string.updates_tab)};
		
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View v = views.get(position);
		container.addView(v);
		return v;
	}
	
	@Override
	public int getCount() {
		return views.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view.equals(object);
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
		return titles[position];
	}

}
