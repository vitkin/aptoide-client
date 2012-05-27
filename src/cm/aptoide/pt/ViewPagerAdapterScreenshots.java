package cm.aptoide.pt;

import java.util.ArrayList;

import org.json.JSONArray;

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
	String url;
	
	public ViewPagerAdapterScreenshots(Context context,String[] images2,String uri) {
		
		this.context=context;
		imageLoader = new ScreenshotsImageLoader(context);
		this.images=images2;
		this.url=uri;
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		
		View v = LayoutInflater.from(context).inflate(R.layout.screenshots, null);
		imageLoader.DisplayImage(-1, images[position],(ImageView) v.findViewById(R.id.screenshot), context);
		container.addView(v);
		v.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Intent i = new Intent(context,ScreenshotsViewer.class);
				i.putExtra("url", url);
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
