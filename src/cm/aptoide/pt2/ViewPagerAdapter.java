package cm.aptoide.pt2;

import java.util.ArrayList;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class ViewPagerAdapter extends PagerAdapter {

	String[] titles = new String[]{"Featured", "Available","Installed", "Updates"};
	ArrayList<View> views = new ArrayList<View>();
	private Context context; 
	
	
	public ViewPagerAdapter(Context context, ArrayList<View> views) {
		this.context=context;
		this.views = views; 
		
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
