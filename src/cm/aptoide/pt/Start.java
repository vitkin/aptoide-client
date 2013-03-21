package cm.aptoide.pt;


import java.io.File;

import cm.aptoide.com.nostra13.universalimageloader.core.DisplayImageOptions;
import cm.aptoide.com.nostra13.universalimageloader.core.ImageLoader;
import cm.aptoide.com.nostra13.universalimageloader.core.assist.FailReason;
import cm.aptoide.com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import cm.aptoide.com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class Start extends Activity {
	
	private final String SDCARD = Environment.getExternalStorageDirectory()
			.getPath();
	private String LOCAL_PATH = SDCARD + "/.aptoide";

	/**
	 * The thread to process splash screen events
	 */
	private Thread mSplashThread;
	ImageView imageSplash;
	private ImageLoadingListener listener = new ImageLoadingListener() {
		
		@Override
		public void onLoadingStarted() { }
		
		@Override
		public void onLoadingFailed(FailReason failReason) {
			showSplash();
		}
		
		@Override
		public void onLoadingComplete(Bitmap loadedImage) {
			showSplash();
		}
		
		@Override
		public void onLoadingCancelled() {	}
	};
	private DisplayImageOptions options = new DisplayImageOptions.Builder().cacheOnDisc().displayer(new FadeInBitmapDisplayer(300)).build();
	
	
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		SetAptoideTheme.setAptoideTheme(this);
		super.onCreate(savedInstanceState);
		File file = new File(LOCAL_PATH + "/icons");
		if (!file.exists()) {
			file.mkdirs();
		}
		if(ApplicationAptoide.SPLASHSCREEN != null){

			// Splash screen view
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
			setContentView(R.layout.splash);
			imageSplash = (ImageView) findViewById(R.id.splashscreen);
			if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
				ImageLoader.getInstance().displayImage(ApplicationAptoide.SPLASHSCREEN_LAND, imageSplash, options, listener, "splash_land");
			}else{
				ImageLoader.getInstance().displayImage(ApplicationAptoide.SPLASHSCREEN, imageSplash, options, listener, "splash");
			}
			
		}else{
            finish();
			Intent intent = new Intent();
			intent.setClass(Start.this, MainActivity.class);
			startActivity(intent);

		}
	}

	private void showSplash() {
		final Start sPlashScreen = this;

		// The thread to wait for splash screen events
		mSplashThread =  new Thread(){
			@Override
			public void run(){
				try {
					synchronized(this){
						// Wait given period of time or exit on touch
						wait(3000);
					}
				}
				catch(InterruptedException ex){
				}

				finish();

				// Run next activity
				Intent intent = new Intent();
				intent.setClass(sPlashScreen, MainActivity.class);
				startActivity(intent);
				//                stop();
			}
		};

		mSplashThread.start();
	}

	/**
	 * Processes splash screen touch events
	 */
	@Override
	public boolean onTouchEvent(MotionEvent evt)
	{
		if(ApplicationAptoide.PARTNERID != null){
			if(evt.getAction() == MotionEvent.ACTION_DOWN)
			{
				synchronized(mSplashThread){
					mSplashThread.notifyAll();
				}
			}
		}
		return true;
	}
}
