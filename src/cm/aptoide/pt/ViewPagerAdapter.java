package cm.aptoide.pt;

import java.util.ArrayList;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.viewpagerindicator.TitleProvider;

public class ViewPagerAdapter extends PagerAdapter implements TitleProvider{
	Context context;
	String[] tabs;
	ArrayList<View> pages;
	
	public ViewPagerAdapter(Context context, ArrayList<View> pages) {
		this.context=context;
		tabs=new String[]{context.getString(R.string.available_tab),context.getString(R.string.installed_tab),context.getString(R.string.updates_tab)};
		this.pages=pages;
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		
		container.addView(pages.get(position));
		return pages.get(position);
	}
	
	@Override
	public int getCount() {
		return pages.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0.equals(arg1);
	}

	public String getTitle(int position) {
		return tabs[position];
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager)container).removeView((View) object);
	}

}
