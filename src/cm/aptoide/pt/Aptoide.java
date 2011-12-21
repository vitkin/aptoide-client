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

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.SimpleAdapter.ViewBinder;
import cm.aptoide.pt.data.AIDLAptoideServiceData;
import cm.aptoide.pt.data.AptoideServiceData;
import cm.aptoide.pt.data.Constants;
import cm.aptoide.pt.data.EnumServiceDataCallback;
import cm.aptoide.pt.data.display.ViewDisplayListApps;
import cm.aptoide.pt.data.model.ViewRepository;
import cm.aptoide.pt.data.system.ViewScreenDimensions;
import cm.aptoide.pt.debug.AptoideLog;
import cm.aptoide.pt.debug.InterfaceAptoideLog;
import cm.aptoide.pt.ui.ViewPagerIndicator;



/**
 * Aptoide, the main interface class
 * 			displays the available apps list
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class Aptoide extends FragmentActivity implements InterfaceAptoideLog{ //, OnItemClickListener { 

	private final String TAG = "Aptoide";

	private ScrollDetector scrollListener;
	private GestureDetector swypeDetector;
	private View.OnTouchListener swypeListener;
	private AtomicBoolean swyping = null;
	private Handler swypeDelayHandler = null;

	private PagerAdapter pagerAdapter;
	private ViewPager  appsListsPager;
	private ViewPagerIndicator titleBar;
//	private static int NUM_VIEWS = 3;
	
//	private ViewFlipper appsListFlipper = null;
	private static ListView availableAppsList = null;
	private static ListView installedAppsList = null;
	private static ListView updatableAppsList = null;
	private static ListView topAppsList = null;
	private EnumAppsLists currentAppsList = null;

	private static ViewDisplayListApps availableApps = null;
	private static ViewDisplayListApps installedApps = null;
	private static ViewDisplayListApps updatableApps = null;

	private static SimpleAdapter availableAdapter = null;
	private static SimpleAdapter installedAdapter = null;
	private static SimpleAdapter updatableAdapter = null;

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
		public void newInstalledListDataAvailable() throws RemoteException {
			AptoideLog.v(Aptoide.this, "received newInstalledListDataAvailable callback");
			serviceDataCallbackHandler.sendEmptyMessage(EnumServiceDataCallback.UPDATE_INSTALLED_LIST.ordinal());
		}

		@Override
		public void newAvailableListDataAvailable() throws RemoteException {
			AptoideLog.v(Aptoide.this, "received newAvailableListDataAvailable callback");
			serviceDataCallbackHandler.sendEmptyMessage(EnumServiceDataCallback.UPDATE_AVAILABLE_LIST.ordinal());
		}

		@Override
		public void refreshAvailableDisplay() throws RemoteException {
			// TODO Auto-generated method stub
			
		}
	};

	private Handler serviceDataCallbackHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			EnumServiceDataCallback message = EnumServiceDataCallback.reverseOrdinal(msg.what);
			switch (message) {
			case UPDATE_INSTALLED_LIST:
				try {
					updateDisplayInstalled(serviceDataCaller.callGetInstalledPackages(0, 100));

					//					updateDisplayUpdatable(serviceDataCaller.callGetUpdatablePackages(0, 100));

				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
				break;
			case UPDATE_AVAILABLE_LIST:
				try {
					updateDisplayInstalled(serviceDataCaller.callGetInstalledPackages(0, 100));

					updateDisplayAvailable(serviceDataCaller.callGetAvailablePackages(0, 100));

					updateDisplayUpdates(serviceDataCaller.callGetUpdatablePackages(0, 100));

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
	
	static Context context;
	
	
	public static ViewDisplayListApps getAvailableApps() {
		return availableApps;
	}

	public static void setAvailableApps(ViewDisplayListApps availableApps) {
		Aptoide.availableApps = availableApps;
	}

	public static ViewDisplayListApps getInstalledApps() {
		return installedApps;
	}

	public static void setInstalledApps(ViewDisplayListApps installedApps) {
		Aptoide.installedApps = installedApps;
	}

	public static ViewDisplayListApps getUpdatableApps() {
		return updatableApps;
	}

	public static void setUpdatableApps(ViewDisplayListApps updatableApps) {
		Aptoide.updatableApps = updatableApps;
	}



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aptoide);

		context = this;
		
		makeSureServiceDataIsRunning();

		installedApps = new ViewDisplayListApps(100);
		availableApps = new ViewDisplayListApps(100);
		updatableApps = new ViewDisplayListApps(100);

//		swypeDetector = new GestureDetector(new SwypeDetector());
//		swypeListener = new View.OnTouchListener() {
//								@Override
//								public boolean onTouch(View v, MotionEvent event) {
//									return swypeDetector.onTouchEvent(event);
//								}
//							};
//		swyping = new AtomicBoolean(false);
//		swypeDelayHandler = new Handler();
//		scrollListener = new ScrollDetector();

        // Create our custom adapter to supply pages to the viewpager.
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        appsListsPager = (ViewPager)findViewById(R.id.pager);
        appsListsPager.setAdapter(pagerAdapter);
        
        // Start at a custom position
        appsListsPager.setCurrentItem(0);
        
        // Find the indicator from the layout
        titleBar = (ViewPagerIndicator)findViewById(R.id.indicator);
		
        // Set the indicator as the pageChangeListener
        appsListsPager.setOnPageChangeListener(titleBar);
        
        // Initialize the indicator. 
        titleBar.init(0, pagerAdapter.getCount(), pagerAdapter);
		Resources res = getResources();
		Drawable prev = res.getDrawable(R.drawable.indicator_prev_arrow);
		Drawable next = res.getDrawable(R.drawable.indicator_next_arrow);
		titleBar.setFocusedTextColor(new int[]{255, 0, 0});
		
		// Set images for previous and next arrows.
		titleBar.setArrows(prev, next);
		
		titleBar.setOnClickListener(new OnIndicatorClickListener());


		//Bind the title indicator to the adapter
		//TitlePageIndicator titleIndicator = (TitlePageIndicator)findViewById(R.id.titles);
		//titleIndicator.setViewPager(myPager);

//		appsListFlipper = ((ViewFlipper) Aptoide.this.findViewById(R.id.list_flipper));

//		availableAppsList = new ListView(this);
//		availableAppsList = (ListView) 
//		availableAppsList.setBackgroundColor(Color.WHITE);
//		availableAppsList.setCacheColorHint(Color.TRANSPARENT);
//		availableAppsList.setOnTouchListener(swypeListener);
//		availableAppsList.setOnScrollListener(scrollListener);
//		availableAppsList.setOnItemClickListener(this);
//		appsListFlipper.addView(availableAppsList);
//		appsListsPager.addView(availableAppsList);
//
//		installedAppsList = new ListView(this);
//		installedAppsList.setBackgroundColor(Color.WHITE);
//		installedAppsList.setCacheColorHint(Color.TRANSPARENT);
//		installedAppsList.setOnTouchListener(swypeListener);
//		installedAppsList.setOnScrollListener(scrollListener);
//		installedAppsList.setOnItemClickListener(this);
//		appsListFlipper.addView(installedAppsList);
//		appsListsPager.addView(installedAppsList);
//
//		updatableAppsList = new ListView(this);
//		updatableAppsList.setBackgroundColor(Color.WHITE);
//		updatableAppsList.setCacheColorHint(Color.TRANSPARENT);
//
//		updatableAppsList.setOnTouchListener(swypeListener);
//		updatableAppsList.setOnScrollListener(scrollListener);
//		updatableAppsList.setOnItemClickListener(this);
//		appsListFlipper.addView(updatableAppsList);
//		appsListsPager.addView(updatableAppsList);
//
//		topAppsList = new ListView(this);
//		topAppsList.setBackgroundColor(Color.WHITE);
//		topAppsList.setCacheColorHint(Color.TRANSPARENT);
//	
//		TextView header = (TextView) findViewById(R.id.topappbanner);
//		topAppsList.addHeaderView(header);
//		
//		currentAppsList = EnumAppsLists.Available;


	}



	public static ListAdapter initDisplayAvailable(){
		availableAdapter = new SimpleAdapter(context, availableApps.getList(), R.layout.app_row, 
				new String[] {Constants.KEY_APPLICATION_HASHID, Constants.KEY_APPLICATION_NAME, Constants.DISPLAY_APP_UP_TO_DATE_VERSION_NAME, Constants.KEY_STATS_DOWNLOADS,Constants.KEY_STATS_STARS,  Constants.DISPLAY_APP_ICON_CACHE_PATH},
				new int[] {R.id.app_hashid, R.id.app_name, R.id.uptodate_versionname, R.id.downloads, R.id.stars, R.id.app_icon});

		availableAdapter.setViewBinder(new AvailableAppsListBinder());
		return availableAdapter;
//		availableAppsList.setAdapter(availableAdapter);
	}

	public void updateDisplayAvailable(ViewDisplayListApps availableApps){
		AptoideLog.d(Aptoide.this, "AvailableList: "+availableApps);
		boolean newList = getAvailableApps().getList().isEmpty();
		if(newList){
			setAvailableApps(availableApps);
//			initDisplayAvailable();
		}else{		//TODO append new list elements on the end or the beginning depending on scroll direction, and clear the same number of elements on the other side of the list.
			AptoideLog.d(this, "available list not empty");
			getAvailableApps().getList().addAll(availableApps.getList());
			availableAdapter.notifyDataSetChanged();
		}

	}


	public static ListAdapter initDisplayInstalled(){
		installedAdapter = new SimpleAdapter(context, installedApps.getList(), R.layout.app_row, 
				new String[] {Constants.KEY_APPLICATION_HASHID, Constants.KEY_APPLICATION_NAME, Constants.DISPLAY_APP_UP_TO_DATE_VERSION_NAME, Constants.DISPLAY_APP_INSTALLED_VERSION_NAME, Constants.DISPLAY_APP_IS_DOWNGRADABLE, Constants.DISPLAY_APP_ICON_CACHE_PATH},
				new int[] {R.id.app_hashid, R.id.app_name, R.id.uptodate_versionname, R.id.installed_versionname, R.id.isDowngradeAvailable, R.id.app_icon});

		installedAdapter.setViewBinder(new InstalledAppsListBinder());
		return installedAdapter;
//		installedAppsList.setAdapter(installedAdapter);
	}

	public void updateDisplayInstalled(ViewDisplayListApps installedApps){
		AptoideLog.d(Aptoide.this, "InstalledList: "+installedApps);
		boolean newList = getInstalledApps().getList().isEmpty();
		if(newList){
			setInstalledApps(installedApps);
//			initDisplayInstalled();
		}else{		//TODO append new list elements on the end or the beginning depending on scroll direction, and clear the same number of elements on the other side of the list.
			AptoideLog.d(this, "installed list not empty");
			getInstalledApps().getList().addAll(installedApps.getList());
			installedAdapter.notifyDataSetChanged();
		}

	}


	public static ListAdapter initDisplayUpdates(){
		if(!updatableApps.getList().isEmpty()){
			updatableAdapter = new SimpleAdapter(context, updatableApps.getList(), R.layout.app_row, 
					new String[] {Constants.KEY_APPLICATION_HASHID, Constants.KEY_APPLICATION_NAME, Constants.DISPLAY_APP_UP_TO_DATE_VERSION_NAME, Constants.KEY_STATS_DOWNLOADS,Constants.KEY_STATS_STARS,  Constants.DISPLAY_APP_ICON_CACHE_PATH},
					new int[] {R.id.app_hashid, R.id.app_name, R.id.uptodate_versionname, R.id.downloads, R.id.stars, R.id.app_icon});

			updatableAdapter.setViewBinder(new UpdatableAppsListBinder());
//			updatableAppsList.setAdapter(updatableAdapter);
		}
		return updatableAdapter;
	}

	public void updateDisplayUpdates(ViewDisplayListApps updatableApps){
		AptoideLog.d(Aptoide.this, "UpdatesList: "+updatableApps);
		boolean newList = getUpdatableApps().getList().isEmpty();
		if(newList){
			setUpdatableApps(updatableApps);
//			initDisplayUpdates();
		}else{	//TODO append new list elements on the end or the beginning depending on scroll direction, and clear the same number of elements on the other side of the list.
			AptoideLog.d(this, "update list not empty");
			getUpdatableApps().getList().addAll(updatableApps.getList());
			updatableAdapter.notifyDataSetChanged();
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


	static class AvailableAppsListBinder implements ViewBinder
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


	static class InstalledAppsListBinder implements ViewBinder
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


	static class UpdatableAppsListBinder implements ViewBinder
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




//	class SwypeDetector extends SimpleOnGestureListener {
//
//		private static final int SWIPE_MIN_DISTANCE = 80;
//		private static final int SWIPE_MAX_OFF_PATH = 250;
//		private static final int SWIPE_THRESHOLD_VELOCITY = 150;
//
//		@Override
//		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//			//    		Toast.makeText( Aptoide.this, availableAdapter.getItem( availableAppsList.pointToPosition(Math.round(e1.getX()), Math.round(e1.getY()) )).toString(), Toast.LENGTH_LONG );
//			if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH){
//				return false;
//			}else{
//				swyping.set(true);
//				if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
//					Log.d("Aptoide","Swype right");
//					if(EnumAppsLists.getNext(currentAppsList).equals(currentAppsList)){
//						appsListFlipper.startAnimation(AnimationUtils.loadAnimation(Aptoide.this, R.anim.flip_resist_next));
//					}else{
//						appsListFlipper.setOutAnimation(AnimationUtils.loadAnimation(Aptoide.this, R.anim.flip_out_next));
//						appsListFlipper.setInAnimation(AnimationUtils.loadAnimation(Aptoide.this, R.anim.flip_in_next));
//						appsListFlipper.showNext();
//						currentAppsList = EnumAppsLists.getNext(currentAppsList);
//					}
//				} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
//					Log.d("Aptoide","Swype left");
//					if(EnumAppsLists.getPrevious(currentAppsList).equals(currentAppsList)){
//						appsListFlipper.startAnimation(AnimationUtils.loadAnimation(Aptoide.this, R.anim.flip_resist_previous));
//					}else{
//						appsListFlipper.setOutAnimation(AnimationUtils.loadAnimation(Aptoide.this, R.anim.flip_out_previous));
//						appsListFlipper.setInAnimation(AnimationUtils.loadAnimation(Aptoide.this, R.anim.flip_in_previous));
//						appsListFlipper.showPrevious();
//						currentAppsList = EnumAppsLists.getPrevious(currentAppsList);
//					}
//				}
//				new Thread(){
//					public void run(){
//						swypeDelayHandler.postDelayed(new Runnable() {
//							public void run() {
//								swyping.set(false);
//							}
//						}, 500);
//					}
//				}.start();
//
//
//				return super.onFling(e1, e2, velocityX, velocityY);
//			}
//		}
//
//	}

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

//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		return swypeDetector.onTouchEvent(event);
//	}
//
//	@Override
//	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//		if(!swyping.get()){
//			AptoideLog.d(this, "Onclick");
//		}
//	}


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


	/**
	 * 
	 * Class for the Title Indicator
	 *
	 */
	class OnIndicatorClickListener implements ViewPagerIndicator.OnClickListener{
		@Override
		public void onCurrentClicked(View v) {
			Toast.makeText(Aptoide.this, "Hello", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onNextClicked(View v) {
			appsListsPager.setCurrentItem(Math.min(pagerAdapter.getCount() - 1, titleBar.getCurrentPosition() + 1));
		}

		@Override
		public void onPreviousClicked(View v) {
			appsListsPager.setCurrentItem(Math.max(0, titleBar.getCurrentPosition() - 1));
		}

	}
	
	/**
	 * 
	 * Class for the Page Adapter
	 *
	 */
	class PagerAdapter extends FragmentPagerAdapter implements ViewPagerIndicator.PageInfoProvider{
		public PagerAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		@Override
		public Fragment getItem(int position) {
//			String title = new String();
			return ItemFragment.newInstance(position);
		}

		@Override
		public int getCount() {
			return EnumAppsLists.getCount();
//			return NUM_VIEWS;
		}

		@Override
		public String getTitle(int position){
			return EnumAppsLists.reverseOrdinal(position).toString();
		}
		
//		@Override
//		public void destroyItem(ViewGroup container, int position, Object object) {
//			container.removeViewAt(position);
//		}
		
	}

	/**
	 * 
	 * Class to put the arguments in the Title Bar and add the Lists
	 *
	 */
	public static class ItemFragment extends ListFragment {
		EnumAppsLists appsListCurrent;
		
		static ItemFragment newInstance(int position) {
			ItemFragment appsList = new ItemFragment();

			// Supply num input as an argument.
			Bundle args = new Bundle();
			
			
			args.putInt("position", position);
			
			appsList.setArguments(args);
			
			return appsList;
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			this.appsListCurrent = EnumAppsLists.reverseOrdinal(getArguments().getInt("position"));
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View appsList = inflater.inflate(R.layout.apps_list, container, false);
//			/*View tv = v.findViewById(R.id.text);
//			((TextView)tv).setText(title);*/
			
			return appsList;
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
//			setListAdapter(new ArrayAdapter<String>(getActivity(),
//					android.R.layout.simple_list_item_1, list));
			switch (appsListCurrent) {
				case Available:
	//				availableAppsList.setOnListItemClickListener(this);
	//				return availableAppsList;
					setListAdapter(initDisplayAvailable());
					break;
					
				case Installed:
	//				installedAppsList.setOnListItemClickListener(this);
	//				return installedAppsList;
					setListAdapter(initDisplayInstalled());
					break;
					
				case Update:
	//				updatableAppsList.setOnListItemClickListener(this);
	//				return updatableAppsList;
					setListAdapter(initDisplayUpdates());
					break;
					
//				case Top:
	//				topAppsList.setOnListItemClickListener(this);
	//				return topAppsList;
//					break;
					
				default:
					break;
			}
		}

//		@Override
//		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
////			if(!swyping.get()){
//				Log.d("Aptoide", "Onclick");
////			}
//		}
		
		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			Log.i("FragmentList", "Item clicked: " + id);
		}
	}


}
