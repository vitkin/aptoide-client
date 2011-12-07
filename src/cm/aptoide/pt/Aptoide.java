/**
 * Aptoide, Alternative client-side Android Package Manager
 * from v3.0 Copyright (C) 2011 Duarte Silveira 
 * duarte.silveira@caixamagica.pt
 * 
 * derivative work of original Aptoide Copyright (C) 2009 Roberto Jacinto
 * roberto.jacinto@caixamágica.pt
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/
package cm.aptoide.pt;


import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.ViewFlipper;
import cm.aptoide.pt.data.AIDLAptoideServiceData;
import cm.aptoide.pt.data.AptoideServiceData;
import cm.aptoide.pt.data.Constants;
import cm.aptoide.pt.data.EnumServiceDataCallback;
import cm.aptoide.pt.data.display.ViewDisplayListApps;
import cm.aptoide.pt.data.model.ViewRepository;
import cm.aptoide.pt.data.system.ViewScreenDimensions;
import cm.aptoide.pt.debug.AptoideLog;
import cm.aptoide.pt.debug.InterfaceAptoideLog;

/**
 * Aptoide, the main interface class
 * 			displays the available apps list
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class Aptoide extends Activity implements InterfaceAptoideLog, OnItemClickListener { 
	
	private final String TAG = "Aptoide";
	
	private ScrollDetector scrollListener;
	private GestureDetector swypeDetector;
	private View.OnTouchListener swypeListener;
	private AtomicBoolean swyping = null;
	private Handler swypeDelayHandler = null;
	
	private ViewFlipper appsListFlipper = null;
	private ListView availableAppsList = null;
	private ListView installedAppsList = null;
	private ListView updatesAppsList = null;
	private EnumAppsLists currentAppsList = null;
	
	private ViewDisplayListApps availableApps = null;
	private ViewDisplayListApps installedApps = null;
	private ViewDisplayListApps updatableApps = null;
	
	private SimpleAdapter availableAdapter = null;
	private SimpleAdapter installedAdapter = null;
	private SimpleAdapter updatesAdapter = null;
		
	private AIDLAptoideServiceData serviceDataCaller = null;

	private boolean serviceDataSeenRunning = false;
	private boolean serviceDataIsBound = false;

	
	private ServiceConnection serviceDataConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			// This is called when the connection with the service has been
			// established, giving us the object we can use to
			// interact with the service.  We are communicating with the
			// service using AIDL, so here we set the remote service interface.
			serviceDataCaller = AIDLAptoideServiceData.Stub.asInterface(service);
			serviceDataIsBound = true;
			
			AptoideLog.v(Aptoide.this, "Connected to ServiceData");

			if(!serviceDataSeenRunning){
				try {
		            AptoideLog.v(Aptoide.this, "Called for a synchronization of installed Packages, because serviceData wasn't previously running");
		            serviceDataCaller.callSyncInstalledPackages();
		        } catch (RemoteException e) {
					// TODO Auto-generated catch block
		            e.printStackTrace();
		        }
			}
			
			DisplayMetrics displayMetrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
			ViewScreenDimensions screenDimensions = new ViewScreenDimensions(displayMetrics.widthPixels, displayMetrics.heightPixels);
			AptoideLog.d(Aptoide.this, screenDimensions.toString());
	        try {
	            AptoideLog.v(Aptoide.this, "Called for screenDimensions storage");
	            serviceDataCaller.callStoreScreenDimensions(screenDimensions);
	        } catch (RemoteException e) {
				// TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        
	        try {
	            AptoideLog.v(Aptoide.this, "Called for registering as AvailablePackages Observer");
	            serviceDataCaller.callRegisterAvailablePackagesObserver(serviceDataCallback);
	            
	            AptoideLog.v(Aptoide.this, "Called for registering as InstalledPackages Observer");
	            serviceDataCaller.callRegisterInstalledPackagesObserver(serviceDataCallback);

	            AptoideLog.v(Aptoide.this, "Called for registering as UpdatablePackages Observer");
	            serviceDataCaller.callRegisterUpdatablePackagesObserver(serviceDataCallback);
	            
	        } catch (RemoteException e) {
				// TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        
	        try {
				serviceDataCaller.callAddRepo(new ViewRepository("http://dsilveira.bazaarandroid.com/"));
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void onServiceDisconnected(ComponentName className) {
			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.
			serviceDataCaller = null;
			serviceDataIsBound = false;
			
			AptoideLog.v(Aptoide.this, "Disconnected from ServiceData");
		}
	};
	
	
	
	private AIDLAptoideInterface.Stub serviceDataCallback = new AIDLAptoideInterface.Stub() {
		
		@Override
		public void newListDataAvailable() throws RemoteException {
			AptoideLog.v(Aptoide.this, "received newListDataAvailable callback");
			serviceDataCallbackHandler.sendEmptyMessage(EnumServiceDataCallback.UPDATE_AVAILABLE_LIST.ordinal());
		}
	};
    
    private Handler serviceDataCallbackHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	EnumServiceDataCallback message = EnumServiceDataCallback.reverseOrdinal(msg.what);
        	switch (message) {
			case UPDATE_AVAILABLE_LIST:
				try {
					updateDisplayAvailable(serviceDataCaller.callGetAvailablePackages(0, 100));
					
					installedApps = serviceDataCaller.callGetInstalledPackages(0, 100);
					displayInstalled();

					updatableApps = serviceDataCaller.callGetUpdatablePackages(0, 100);
					displayUpdates();
					
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
				break;

			default:
				break;
			}
        }
    };

    

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aptoide);
       
		makeSureServiceDataIsRunning();

		swypeDetector = new GestureDetector(new SwypeDetector());
		swypeListener = new View.OnTouchListener() {
								@Override
								public boolean onTouch(View v, MotionEvent event) {
									return swypeDetector.onTouchEvent(event);
								}
							};
		swyping = new AtomicBoolean(false);
		swypeDelayHandler = new Handler();
		scrollListener = new ScrollDetector();
		
		appsListFlipper = ((ViewFlipper) Aptoide.this.findViewById(R.id.list_flipper));
		
		availableAppsList = new ListView(this);
		availableAppsList.setOnTouchListener(swypeListener);
		availableAppsList.setOnScrollListener(scrollListener);
		availableAppsList.setOnItemClickListener(this);
		appsListFlipper.addView(availableAppsList);
		
		installedAppsList = new ListView(this);
		installedAppsList.setOnTouchListener(swypeListener);
		installedAppsList.setOnScrollListener(scrollListener);
		installedAppsList.setOnItemClickListener(this);
		appsListFlipper.addView(installedAppsList);
		
		updatesAppsList = new ListView(this);
		updatesAppsList.setOnTouchListener(swypeListener);
		updatesAppsList.setOnScrollListener(scrollListener);
		updatesAppsList.setOnItemClickListener(this);
		appsListFlipper.addView(updatesAppsList);

		currentAppsList = EnumAppsLists.AVAILABLE;
    }
    
    

	public void initDisplayAvailable(){
		availableAdapter = new SimpleAdapter(Aptoide.this, availableApps.getList(), R.layout.app_row, 
				new String[] {Constants.KEY_APPLICATION_HASHID, Constants.KEY_APPLICATION_NAME, Constants.DISPLAY_APP_UP_TO_DATE_VERSION_NAME, Constants.KEY_STATS_DOWNLOADS,Constants.KEY_STATS_STARS,  Constants.DISPLAY_APP_ICON_CACHE_PATH},
				new int[] {R.id.app_hashid, R.id.app_name, R.id.uptodate_versionname, R.id.downloads, R.id.stars, R.id.app_icon});
		
		availableAdapter.setViewBinder(new AvailableAppsListBinder());
		availableAppsList.setAdapter(availableAdapter);
    }
	
	public void updateDisplayAvailable(ViewDisplayListApps availableApps){
    	AptoideLog.d(Aptoide.this, "AvailableList: "+availableApps);
		boolean newList = availableApps.getList().isEmpty();
    	if(newList){
    		this.availableApps = availableApps;
    		initDisplayAvailable();
    	}else{		//TODO append new list elements on the end or the beginning depending on scroll direction, and clear the same number of elements on the other side of the list.
    		this.availableApps.getList().addAll(availableApps.getList());
    		availableAdapter.notifyDataSetChanged();
    	}
		
	}
    
    
    public void displayInstalled(){
    	AptoideLog.d(Aptoide.this, "InstalledList: "+installedApps);
    	installedAdapter = new SimpleAdapter(Aptoide.this, installedApps.getList(), R.layout.app_row, 
				new String[] {Constants.KEY_APPLICATION_HASHID, Constants.KEY_APPLICATION_NAME, Constants.DISPLAY_APP_UP_TO_DATE_VERSION_NAME, Constants.DISPLAY_APP_INSTALLED_VERSION_NAME, Constants.DISPLAY_APP_IS_DOWNGRADABLE, Constants.DISPLAY_APP_ICON_CACHE_PATH},
				new int[] {R.id.app_hashid, R.id.app_name, R.id.uptodate_versionname, R.id.installed_versionname, R.id.isDowngradeAvailable, R.id.app_icon});
		
		installedAdapter.setViewBinder(new InstalledAppsListBinder());
		installedAppsList.setAdapter(installedAdapter);
    }
    
    
    public void displayUpdates(){
    	AptoideLog.d(Aptoide.this, "UpdatesList: "+updatableApps);
    	if(!updatableApps.getList().isEmpty()){
			updatesAdapter = new SimpleAdapter(Aptoide.this, updatableApps.getList(), R.layout.app_row, 
					new String[] {Constants.KEY_APPLICATION_HASHID, Constants.KEY_APPLICATION_NAME, Constants.DISPLAY_APP_UP_TO_DATE_VERSION_NAME, Constants.KEY_STATS_DOWNLOADS,Constants.KEY_STATS_STARS,  Constants.DISPLAY_APP_ICON_CACHE_PATH},
					new int[] {R.id.app_hashid, R.id.app_name, R.id.uptodate_versionname, R.id.downloads, R.id.stars, R.id.app_icon});
			
			updatesAdapter.setViewBinder(new UpdatesAppsListBinder());
			updatesAppsList.setAdapter(updatesAdapter);
    	}
    }

	private void makeSureServiceDataIsRunning(){
    	ActivityManager activityManager = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
    	for (RunningServiceInfo runningService : activityManager.getRunningServices(Integer.MAX_VALUE)) {
			if(runningService.service.getClassName().equals(Constants.SERVICE_DATA_CLASS_NAME)){
				this.serviceDataSeenRunning = true;
				break;
			}
		}

    	if(!serviceDataIsBound){
    		bindService(new Intent(this, AptoideServiceData.class), serviceDataConnection, Context.BIND_AUTO_CREATE);
    	}
    }


	class AvailableAppsListBinder implements ViewBinder
	{
		public boolean setViewValue(View view, Object data, String textRepresentation)
		{
			if(view.getClass().toString().equalsIgnoreCase("class android.widget.RatingBar")){
				RatingBar tmpr = (RatingBar)view;
				tmpr.setRating(new Float(textRepresentation));
			}else if(view.getClass().toString().equalsIgnoreCase("class android.widget.TextView")){
				TextView tmpr = (TextView)view;
				tmpr.setText(textRepresentation);
			}else if(view.getClass().toString().equalsIgnoreCase("class android.widget.ImageView")){
				ImageView tmpr = (ImageView)view;	
				File icn = new File(textRepresentation);
				if(icn.exists() && icn.length() > 0){
					new Uri.Builder().build();
					tmpr.setImageURI(Uri.parse(textRepresentation));
				}else{
					tmpr.setImageResource(android.R.drawable.sym_def_app_icon);
				}
			}else if(view.getClass().toString().equalsIgnoreCase("class android.widget.LinearLayout")){
				LinearLayout tmpr = (LinearLayout)view;
				tmpr.setTag(textRepresentation);
			}else{
				return false;
			}
			return true;
		}
	}


	class InstalledAppsListBinder implements ViewBinder
	{
		public boolean setViewValue(View view, Object data, String textRepresentation)
		{
			if(view.getClass().toString().equalsIgnoreCase("class android.widget.RatingBar")){
				RatingBar tmpr = (RatingBar)view;
				tmpr.setRating(new Float(textRepresentation));
			}else if(view.getClass().toString().equalsIgnoreCase("class android.widget.TextView")){
				TextView tmpr = (TextView)view;
				tmpr.setText(textRepresentation);
			}else if(view.getClass().toString().equalsIgnoreCase("class android.widget.ImageView")){
				ImageView tmpr = (ImageView)view;	
				File icn = new File(textRepresentation);
				if(icn.exists() && icn.length() > 0){
					new Uri.Builder().build();
					tmpr.setImageURI(Uri.parse(textRepresentation));
				}else{
					tmpr.setImageResource(android.R.drawable.sym_def_app_icon);
				}
			}else if(view.getClass().toString().equalsIgnoreCase("class android.widget.LinearLayout")){
				LinearLayout tmpr = (LinearLayout)view;
				tmpr.setTag(textRepresentation);
			}else{
				return false;
			}
			return true;
		}
	}


	class UpdatesAppsListBinder implements ViewBinder
	{
		public boolean setViewValue(View view, Object data, String textRepresentation)
		{
			if(view.getClass().toString().equalsIgnoreCase("class android.widget.RatingBar")){
				RatingBar tmpr = (RatingBar)view;
				tmpr.setRating(new Float(textRepresentation));
			}else if(view.getClass().toString().equalsIgnoreCase("class android.widget.TextView")){
				TextView tmpr = (TextView)view;
				tmpr.setText(textRepresentation);
			}else if(view.getClass().toString().equalsIgnoreCase("class android.widget.ImageView")){
				ImageView tmpr = (ImageView)view;	
				File icn = new File(textRepresentation);
				if(icn.exists() && icn.length() > 0){
					new Uri.Builder().build();
					tmpr.setImageURI(Uri.parse(textRepresentation));
				}else{
					tmpr.setImageResource(android.R.drawable.sym_def_app_icon);
				}
			}else if(view.getClass().toString().equalsIgnoreCase("class android.widget.LinearLayout")){
				LinearLayout tmpr = (LinearLayout)view;
				tmpr.setTag(textRepresentation);
			}else{
				return false;
			}
			return true;
		}
	}

    
    
    
    class SwypeDetector extends SimpleOnGestureListener {

    	private static final int SWIPE_MIN_DISTANCE = 80;
    	private static final int SWIPE_MAX_OFF_PATH = 250;
    	private static final int SWIPE_THRESHOLD_VELOCITY = 150;

		@Override
    	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//    		Toast.makeText( Aptoide.this, availableAdapter.getItem( availableAppsList.pointToPosition(Math.round(e1.getX()), Math.round(e1.getY()) )).toString(), Toast.LENGTH_LONG );
    		if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH){
    			return false;
    		}else{
    			swyping.set(true);
	    		if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
	        		Log.d("Aptoide","Swype right");
	        		if(EnumAppsLists.getNext(currentAppsList).equals(currentAppsList)){
	        			appsListFlipper.startAnimation(AnimationUtils.loadAnimation(Aptoide.this, R.anim.flip_resist_next));
	        		}else{
		        		appsListFlipper.setOutAnimation(AnimationUtils.loadAnimation(Aptoide.this, R.anim.flip_out_next));
		    			appsListFlipper.setInAnimation(AnimationUtils.loadAnimation(Aptoide.this, R.anim.flip_in_next));
		    			appsListFlipper.showNext();
		    			currentAppsList = EnumAppsLists.getNext(currentAppsList);
	        		}
	    		} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
	        		Log.d("Aptoide","Swype left");
	        		if(EnumAppsLists.getPrevious(currentAppsList).equals(currentAppsList)){
	        			appsListFlipper.startAnimation(AnimationUtils.loadAnimation(Aptoide.this, R.anim.flip_resist_previous));
	        		}else{
		        		appsListFlipper.setOutAnimation(AnimationUtils.loadAnimation(Aptoide.this, R.anim.flip_out_previous));
		        		appsListFlipper.setInAnimation(AnimationUtils.loadAnimation(Aptoide.this, R.anim.flip_in_previous));
		        		appsListFlipper.showPrevious();
		        		currentAppsList = EnumAppsLists.getPrevious(currentAppsList);
	        		}
	    		}
	    		new Thread(){
	    			public void run(){
	    				swypeDelayHandler.postDelayed(new Runnable() {
	    	                public void run() {
	    	                	swyping.set(false);
	    	                }
	    	            }, 500);
	    			}
	    		}.start();
	    		
				
	    		return super.onFling(e1, e2, velocityX, velocityY);
    		}
    	}
		
    }
    
    class ScrollDetector implements OnScrollListener{

    	AtomicInteger initialFirstVisileItem = new AtomicInteger(0);
    	AtomicInteger firstVisibleItem = new AtomicInteger(0);
    	AtomicInteger visibleItemCount = new AtomicInteger(0);
    	
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			this.firstVisibleItem.set(firstVisibleItem);
			this.visibleItemCount.set(visibleItemCount);
			if(this.initialFirstVisileItem.get()+visibleItemCount < firstVisibleItem){
				this.initialFirstVisileItem.set(firstVisibleItem);
				Log.d("Aptoide","New Scroll down page");
			}else if(this.initialFirstVisileItem.get()-visibleItemCount > firstVisibleItem){
				this.initialFirstVisileItem.set(firstVisibleItem);
				Log.d("Aptoide","New Scroll up page");
			}
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if(scrollState == SCROLL_STATE_IDLE){
				initialFirstVisileItem.set(firstVisibleItem.get());
				Log.d("Aptoide","Scroll currentList: "+currentAppsList+" offset: "+firstVisibleItem+" range: "+visibleItemCount);				
			}
		}
    	
    }
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return swypeDetector.onTouchEvent(event);
	}

    @Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
    	if(!swyping.get()){
    		AptoideLog.d(this, "Onclick");
    	}
	}
	
	
//	@Override
//	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//		final String pkg_id = ((LinearLayout)arg1).getTag().toString();
//
//		pos = arg2;
//
//		Intent apkinfo = new Intent(this,ApkInfo.class);
//		apkinfo.putExtra("name", db.getName(pkg_id));
//		apkinfo.putExtra("icon", this.getString(R.string.icons_path)+pkg_id);
//		apkinfo.putExtra("apk_id", pkg_id);
//		
//		String tmpi = db.getDescript(pkg_id);
//		if(!(tmpi == null)){
//			apkinfo.putExtra("about",tmpi);
//		}else{
//			apkinfo.putExtra("about",getText(R.string.app_pop_up_no_info));
//		}
//		
//
//		Vector<String> tmp_get = db.getApk(pkg_id);
//		apkinfo.putExtra("server", tmp_get.firstElement());
//		apkinfo.putExtra("version", tmp_get.get(1));
//		apkinfo.putExtra("dwn", tmp_get.get(4));
//		apkinfo.putExtra("rat", tmp_get.get(5));
//		apkinfo.putExtra("size", tmp_get.get(6));
//		apkinfo.putExtra("type", 1);
//		
//		startActivityForResult(apkinfo,30);
//	}


    @Override
	public String getTag() {
		return TAG;
	}
    

	@Override
	protected void onDestroy() {
		if (serviceDataIsBound) {
            unbindService(serviceDataConnection);
            serviceDataIsBound = false;
        }
		super.onDestroy();
	}
	
}
