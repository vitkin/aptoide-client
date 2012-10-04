package cm.aptoide.pt2.adapters;

import java.util.ArrayList;

import cm.aptoide.pt2.R;
import cm.aptoide.pt2.ScreenshotsViewer;
import cm.aptoide.pt2.contentloaders.ScreenshotsImageLoader;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class ViewPagerAdapterScreenshots extends PagerAdapter {

	private Context context;
	ScreenshotsImageLoader imageLoader;
	private String[] images;
	ArrayList<String> url;
	
	public ViewPagerAdapterScreenshots(Context context,String[] images2,ArrayList<String> imagesurl) {
		
		this.context=context;
		imageLoader = new ScreenshotsImageLoader(context);
		this.images=images2;
		this.url=imagesurl;
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		
		View v = LayoutInflater.from(context).inflate(R.layout.screenshots, null);
		imageLoader.DisplayImage(-1, images[position],(ImageView) v.findViewById(R.id.screenshot), context);
		container.addView(v);
		v.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Intent i = new Intent(context,ScreenshotsViewer.class);
				i.putStringArrayListExtra("url", url);
				i.putExtra("position", position);
				context.startActivity(i);
			}
		});
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
