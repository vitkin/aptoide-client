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
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import cm.aptoide.pt.R;
import cm.aptoide.pt.ScreenshotsViewer;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class ViewPagerAdapterScreenshots extends PagerAdapter {

	private Context context;
	ImageLoader imageLoader;
	private String[] images;
	ArrayList<String> url;
	private String hashCode;
	private boolean hd;
	DisplayImageOptions options;
	
	public ViewPagerAdapterScreenshots(Context context,String[] images2,ArrayList<String> imagesurl, String hashCode, boolean hd) {
		
		this.context=context;
		imageLoader = ImageLoader.getInstance();
		this.images=images2;
		this.url=imagesurl;
		this.hd=hd;
		this.hashCode=hashCode.hashCode()+"";
		System.out.println("hash:Method:"+hashCode + " " + this.hashCode);
		options = new DisplayImageOptions.Builder()
		 .displayer(new FadeInBitmapDisplayer(1000))
		 .resetViewBeforeLoading()
		 .cacheOnDisc()
		 .build();
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		final String hashCode=this.hashCode+"."+position;
		final View v = LayoutInflater.from(context).inflate(R.layout.screenshots, null);
		final ProgressBar pb = (ProgressBar) v.findViewById(R.id.screenshots_pb);
		imageLoader.displayImage(images[position],(ImageView) v.findViewById(R.id.screenshot),options, new ImageLoadingListener() {
			
			@Override
			public void onLoadingStarted() {
				pb.setVisibility(View.VISIBLE);
			}
			
			@Override
			public void onLoadingFailed(FailReason failReason) {
				((ImageView) v.findViewById(R.id.screenshot)).setImageResource(android.R.drawable.ic_delete);		
			}
			
			@Override
			public void onLoadingComplete(Bitmap loadedImage) {
				pb.setVisibility(View.GONE);
			}
			
			@Override
			public void onLoadingCancelled() {
				// TODO Auto-generated method stub
				
			}
		}, hashCode);
		container.addView(v);
		if(!hd){
			v.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					Intent i = new Intent(context,ScreenshotsViewer.class);
					i.putStringArrayListExtra("url", url);
					i.putExtra("position", position);
					i.putExtra("hashCode", hashCode+".hd");
					context.startActivity(i);
				}
			});
		}
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
