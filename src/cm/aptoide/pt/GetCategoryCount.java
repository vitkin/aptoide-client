package cm.aptoide.pt;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class GetCategoryCount {

	private ExecutorService executorService;
	private Context context;
	private DBHandler db;
	MemoryCache memoryCache = new MemoryCache();
	private Map<TextView, String> imageViews=Collections.synchronizedMap(new WeakHashMap<TextView, String>());
	boolean secondaryCategory = false;
	public GetCategoryCount(Context context) {
		executorService = Executors.newFixedThreadPool(5);
		db = new DBHandler(context);
		db.open();
	}

	public void getCount(String category, TextView textView, Context context) {
		this.context = context;
		imageViews.put(textView, category);
		String count = memoryCache.get(category);

		if (count != null) {
			textView.setText(count);
		} else {
			queueCount(category, textView);

		}
	}
	
	

	private void queueCount(String category, TextView textView) {
		PhotoToLoad p = new PhotoToLoad(category, textView);
		executorService.submit(new PhotosLoader(p));
	}

	boolean imageViewReused(PhotoToLoad photoToLoad){
        String tag=imageViews.get(photoToLoad.textView);
        if(tag==null || !tag.equals(photoToLoad.url))
            return true;
        return false;
    }
	
	class BitmapDisplayer implements Runnable
    {
        String bitmap;
        PhotoToLoad photoToLoad;
        public BitmapDisplayer(String b, PhotoToLoad p){bitmap=b;photoToLoad=p;}
        public void run()
        {
            if(imageViewReused(photoToLoad))
                return;
            if(bitmap!=null){
            	photoToLoad.textView.setText(bitmap + " apps");
            	photoToLoad.textView.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in));
            }
        }
    }
	BitmapDisplayer bd;
	class PhotosLoader implements Runnable {
    	DBHandler db = new DBHandler(context);
        PhotoToLoad photoToLoad;
        
        PhotosLoader(PhotoToLoad photoToLoad){
            this.photoToLoad=photoToLoad;
        }
        
        public void run() {
        	
            if(imageViewReused(photoToLoad))
                return;
            
            String bmp;
            
            bmp=db.getCategoryCount(photoToLoad.url,secondaryCategory)+"";
            
            memoryCache.put(photoToLoad.url, bmp);
            if(imageViewReused(photoToLoad))
                return;
            bd=new BitmapDisplayer(bmp, photoToLoad);
            
            ((Activity) context).runOnUiThread(bd);
        }
    }
	
	private class PhotoToLoad {
		public String url;
		public TextView textView;

		public PhotoToLoad(String u, TextView i) {
			url = u;
			textView = i;
		}
	}

	public class MemoryCache {

		private static final String TAG = "MemoryCache";
		private Map<String, String> cache = Collections
				.synchronizedMap(new LinkedHashMap<String, String>(10, 1.5f,
						true));// Last argument true for LRU ordering
		private long size = 0;// current allocated size
		private long limit = 1000000;// max memory in bytes

		public MemoryCache() {
			// use 25% of available heap size
			setLimit(Runtime.getRuntime().maxMemory() / 4);
		}

		public void setLimit(long new_limit) {
			limit = new_limit;
			Log.i(TAG, "MemoryCache will use up to " + limit / 1024. / 1024.
					+ "MB");
		}

		public String get(String id) {
			if (!cache.containsKey(id))
				return null;
			return cache.get(id);
		}

		public void put(String id, String count) {
			try {
				cache.put(id, count);
			} catch (Throwable th) {
				th.printStackTrace();
			}
		}

		public void clear() {
			cache.clear();
		}

		long getSizeInBytes(Bitmap bitmap) {
			if (bitmap == null)
				return 0;
			return bitmap.getRowBytes() * bitmap.getHeight();
		}
	}
}
