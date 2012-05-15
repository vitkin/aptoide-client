package cm.aptoide.pt;

import java.util.ArrayList;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class ViewPagerAdapterScreenshots extends PagerAdapter {

	private Context context;
	ImageLoader imageLoader;
	private String[] images;
	int position;

	public ViewPagerAdapterScreenshots(Context context,String[] images2) {
		
		this.context=context;
		imageLoader = new ImageLoader(context);
		this.images=images2;
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		
		View v = LayoutInflater.from(context).inflate(R.layout.screenshots, null);
		imageLoader.DisplayImage(-1, images[position],(ImageView) v.findViewById(R.id.screenshot), context);
		container.addView(v);
		
		return v;
		
	}
	@Override
	public int getCount() {
		return images.length;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0.equals(arg1);
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
		
	}
	
	

}
