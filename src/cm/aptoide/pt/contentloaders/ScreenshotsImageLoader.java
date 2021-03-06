/*******************************************************************************
 * Copyright (c) 2012 rmateus.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package cm.aptoide.pt.contentloaders;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import cm.aptoide.pt.Database;
import cm.aptoide.pt.util.Utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScreenshotsImageLoader {

    MemoryCache memoryCache=MemoryCache.getInstance();
    FileCache fileCache;
    private static Map<ImageView, String> imageViews=Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    ExecutorService executorService;
    Context context;
    boolean download = true;

    private ScreenshotsImageLoader(Context context){
        fileCache=new FileCache(context);
        executorService=Executors.newFixedThreadPool(5);
        SharedPreferences sPref = context.getSharedPreferences("aptoide_prefs", Context.MODE_PRIVATE);
        ConnectivityManager netstate = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        if(sPref.getString("icdown", "nd").equalsIgnoreCase("nd")){
//			download=false;
//		}else if((sPref.getString("icdown", "nd").equalsIgnoreCase("wo")) && (netstate.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED)){
//			download=false;
//		}else{
//			download=true;
//		}
//        System.out.println("ICDOWN " + sPref.getString("icdown", "nasdd"));
    }

    public void DisplayImage(long l, String url, ImageView imageView,Context context, String hashCode)
    {
    	this.context=context;
        imageViews.put(imageView, url);
        Bitmap bitmap=memoryCache.get(url);

        if(bitmap!=null){
            imageView.setImageBitmap(bitmap);

        }else
        {
        	queuePhoto(l,url, imageView,hashCode);
//            imageView.setImageResource(android.R.drawable.sym_def_app_icon);

        }
    }

    private void queuePhoto(long l, String url, ImageView imageView,String hashCode)
    {
        PhotoToLoad p=new PhotoToLoad(url, imageView);
        executorService.submit(new PhotosLoader(p,l,hashCode));
    }

    private Bitmap getBitmap(String url, String hashCode)
    {
        File f=fileCache.getFile(hashCode);

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
				HttpURLConnection conn = (HttpURLConnection) imageUrl
						.openConnection();
				conn.setConnectTimeout(30000);
				conn.setReadTimeout(30000);
				conn.setInstanceFollowRedirects(true);
				InputStream is = conn.getInputStream();
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
//            BitmapFactory.decodeStream(fis,null,o);
//
//            //Find the correct scale value. It should be the power of 2.
//            final int REQUIRED_SIZE=10000;
//            int width_tmp=o.outWidth, height_tmp=o.outHeight;
//            int scale=1;
//            while(true){
//                if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
//                    break;
//                width_tmp/=2;
//                height_tmp/=2;
//                scale*=2;
//            }
//
//            //decode with inSampleSize
//            BitmapFactory.Options o2 = new BitmapFactory.Options();
//            o2.inSampleSize=scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, null);
        } catch (FileNotFoundException e) {}
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
    	Database db = Database.getInstance();
        PhotoToLoad photoToLoad;
        String hashCode;
        long repo_id;
        PhotosLoader(PhotoToLoad photoToLoad,long l, String hashCode){
        	this.hashCode=hashCode;
            this.photoToLoad=photoToLoad;
            this.repo_id=l;
        }

        public void run() {

            if(imageViewReused(photoToLoad))
                return;

            Bitmap bmp;

            bmp=getBitmap(photoToLoad.url,hashCode);

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
//                Drawable [] arrayDrawable = new Drawable[2];

//				arrayDrawable[0] = context.getResources().getDrawable(android.R.drawable.sym_def_app_icon);
//				arrayDrawable[1] = new BitmapDrawable(bitmap);
//				TransitionDrawable transitionDrawable = new TransitionDrawable(arrayDrawable);
//				transitionDrawable.setCrossFadeEnabled(true);
				photoToLoad.imageView.setImageBitmap(bitmap);
				photoToLoad.imageView.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in));

//				transitionDrawable.startTransition(250);
            }else{
            	photoToLoad.imageView.setImageResource(android.R.drawable.sym_def_app_icon);


            }
        }
    }

    public void clearCache() {
        memoryCache.clear();
        fileCache.clear();
    }

}
