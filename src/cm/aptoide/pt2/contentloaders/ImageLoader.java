package cm.aptoide.pt2.contentloaders;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cm.aptoide.pt2.Database;
import cm.aptoide.pt2.preferences.ManagerPreferences;
import cm.aptoide.pt2.util.NetworkUtils;
import cm.aptoide.pt2.util.Utils;
import cm.aptoide.pt2.views.ViewIconDownloadPermissions;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class ImageLoader {
    
    MemoryCache memoryCache=new MemoryCache();
    FileCache fileCache;
    private Map<ImageView, String> imageViews=Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    ExecutorService photoLoadThreadPool; 
    static Context context;
    boolean download = false;
    private static Database db;
	
    public ImageLoader(Context context, Database db){
        fileCache=new FileCache(context);
        ImageLoader.context=context;
        photoLoadThreadPool=Executors.newFixedThreadPool(5);
        ImageLoader.db=db;
        resetPermissions();
    }
    
    public void resetPermissions(){
//    	download = false;
    	ConnectivityManager connectivityState = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
    	ManagerPreferences preferences= new ManagerPreferences(context);
        ViewIconDownloadPermissions permissions = preferences.getIconDownloadPermissions();
        download = NetworkUtils.isPermittedConnectionAvailable(context, permissions);
//        if(permissions.isWiFi()){
//			try {
//				download = download || connectivityState.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED;
//				Log.d("ManagerDownloads", "isPermittedConnectionAvailable wifi: "+download);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		} 
//		if(permissions.isWiMax()){
//			try {
//				download = download || connectivityState.getNetworkInfo(6).getState() == NetworkInfo.State.CONNECTED;
//				Log.d("ManagerDownloads", "isPermittedConnectionAvailable wimax: "+download);
//			} catch (Exception e) { e.printStackTrace();}
//		} 
//		if(permissions.isMobile()){
//			try {
//				download = download || connectivityState.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED;
//				Log.d("ManagerDownloads", "isPermittedConnectionAvailable mobile: "+download);
//			} catch (Exception e) { e.printStackTrace();}
//		}
//		if(permissions.isEthernet()){
//			try {
//				download = download || connectivityState.getNetworkInfo(9).getState() == NetworkInfo.State.CONNECTED;
//				Log.d("ManagerDownloads", "isPermittedConnectionAvailable ethernet: "+download);
//			} catch (Exception e) { e.printStackTrace();}
//		}
    }
    
    public void DisplayImage(String url, ImageView imageView,Context context, String hashCode)
    {
    	ImageLoader.context=context;
        imageViews.put(imageView, url);
        Bitmap bitmap=memoryCache.get(url);
        if(bitmap!=null){
            imageView.setImageBitmap(bitmap);
        }else {
        	
        	queuePhoto(url, imageView,hashCode);
            imageView.setImageResource(android.R.drawable.sym_def_app_icon);
            
        }
    }
        
    private void queuePhoto(String url, ImageView imageView,String hashCode)
    {
        PhotoToLoad p=new PhotoToLoad(url, imageView);
        photoLoadThreadPool.submit(new PhotosLoader(p,hashCode));
    }
    
    private Bitmap getBitmap(String url,String hash) 
    {
    	
    	File f=fileCache.getFile(hash);
    	
        //from SD cache
        if(f.exists()){
        	Bitmap b = decodeFile(f);	
        	if(b!=null)
                return b;
        }
        
        
        
        if (download) {
			//from web
			try {
				Bitmap bitmap = null;
				URL imageUrl = new URL(url);
				NetworkUtils.setTimeout(30000);
				InputStream is = NetworkUtils.getInputStream(imageUrl, null, null, context);
				OutputStream os = new FileOutputStream(f);
				Utils.CopyStream(is, os);
				os.close();
				bitmap = decodeFile(f);
				return bitmap;
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		}else{
			return null;
		}
    }
    FileInputStream fis;
    //decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f){
        try {
        	fis = new FileInputStream(f);
            //decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(fis,null,o);
            
            //Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE=48;
            int width_tmp=o.outWidth, height_tmp=o.outHeight;
            int scale=1;
            while(true){
                if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
                    break;
                width_tmp/=2;
                height_tmp/=2;
                scale*=2;
            }
            
            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, null);
        } catch (FileNotFoundException e) {
        	e.printStackTrace();
        }
        return null;
    }
    
    //Task for the queue
    private class PhotoToLoad
    {
        public String url;
        public ImageView imageView;
        public PhotoToLoad(String u, ImageView i){
            url=u; 
            imageView=i;
        }
    }
    BitmapDisplayer bd;
    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;
        
        long repo_id;

		private String hash;
        PhotosLoader(PhotoToLoad photoToLoad,String hash){
        	this.hash=hash;
            this.photoToLoad=photoToLoad;
        }
        
        public void run() {
        	
            if(imageViewReused(photoToLoad))
                return;
            
            Bitmap bmp;
            	bmp=getBitmap(photoToLoad.url,hash.hashCode()+"");
            memoryCache.put(photoToLoad.url, bmp);
            if(imageViewReused(photoToLoad))
                return;
            bd=new BitmapDisplayer(bmp, photoToLoad);
            Activity a=(Activity)photoToLoad.imageView.getContext();
            a.runOnUiThread(bd);
        }
    }
    
    boolean imageViewReused(PhotoToLoad photoToLoad){
        String tag=imageViews.get(photoToLoad.imageView);
        if(tag==null || !tag.equals(photoToLoad.url))
            return true;
        return false;
    }
    
    //Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable
    {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;
        public BitmapDisplayer(Bitmap b, PhotoToLoad p){bitmap=b;photoToLoad=p;}
        public void run()
        {
            if(imageViewReused(photoToLoad))
                return;
            if(bitmap!=null){
                photoToLoad.imageView.setImageBitmap(bitmap);
                Drawable [] arrayDrawable = new Drawable[2];
                
				arrayDrawable[0] = context.getResources().getDrawable(android.R.drawable.sym_def_app_icon);
				arrayDrawable[1] = new BitmapDrawable(bitmap);
				TransitionDrawable transitionDrawable = new TransitionDrawable(arrayDrawable);
				transitionDrawable.setCrossFadeEnabled(true);
				photoToLoad.imageView.setImageDrawable(transitionDrawable);
				transitionDrawable.startTransition(250);
            }else{
            	photoToLoad.imageView.setImageResource(android.R.drawable.sym_def_app_icon);
            	
                
            }
        }
    }

    public void clearCache() {
        memoryCache.clear();
        fileCache.clear();
    }

    
    
    
    
    
    
    
    
    public ImageLoader(Context context){
    	ImageLoader.context = context;
//        fileCache=new CacheFile(context);
        photoLoadThreadPool=Executors.newFixedThreadPool(5);
    }
    
    public void DisplayImage(String url, ImageView imageView)//, Context context)
    {
//    	this.context=context;
        imageViews.put(imageView, url);
        Bitmap bitmap=memoryCache.get(url);
        if(bitmap!=null){
            imageView.setImageBitmap(bitmap);
        	
        }else
        {
            queuePhoto(url, imageView);
            imageView.setImageResource(android.R.drawable.sym_def_app_icon);
            
        }
    }
        
    private void queuePhoto(String url, ImageView imageView)
    {
        PhotoToLoad p=new PhotoToLoad(url, imageView);
        photoLoadThreadPool.execute(new PhotosCacheLoader(p));
    }
    
    private Bitmap getCacheBitmap(String url) 
    {
    	File f = new File(url);
        
        //from SD cache
        if(f.exists()){
        	Bitmap b = decodeFile(f);	
        	if(b!=null)
                return b;
        }
        return null;
    }
      
    
    class PhotosCacheLoader implements Runnable {
        PhotoToLoad photoToLoad;
        PhotosCacheLoader(PhotoToLoad photoToLoad){
            this.photoToLoad=photoToLoad;
        }
        
        public void run() {
            if(imageViewReused(photoToLoad))
                return;
            Bitmap bmp=getCacheBitmap(photoToLoad.url);
            memoryCache.put(photoToLoad.url, bmp);
            if(imageViewReused(photoToLoad))
                return;
            bd=new BitmapDisplayer(bmp, photoToLoad);
            Activity a=(Activity)photoToLoad.imageView.getContext();
            a.runOnUiThread(bd);
        }
    }
    
    static ImageLoader imageLoader;
    
    public static ImageLoader getInstance(Context context, Database db){
    	
    	if(imageLoader==null){
    		imageLoader = new ImageLoader(context, db);
    	}
    	return imageLoader;
    	
    }
    
    public static void reload(){
    	imageLoader = new ImageLoader(context, db);
    }
    
    
}
