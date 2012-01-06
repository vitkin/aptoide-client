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
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter.ViewBinder;
import cm.aptoide.pt.data.AIDLAptoideServiceData;
import cm.aptoide.pt.data.AptoideServiceData;
import cm.aptoide.pt.data.Constants;
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
	private TextView emptyAvailableAppsList;
	private TextView emptyInstalledAppsList;
	private TextView emptyUpdatableAppsList;
	private ProgressBar loadingAvailableAppsList;
	private ProgressBar loadingInstalledAppsList;
	private ProgressBar loadingUpdatableAppsList;
	private ListView availableAppsListView = null;
	private ListView installedAppsListView = null;
	private ListView updatableAppsListView = null;
	private EnumAppsLists currentAppsList = null;
	
	private enum EnumFlipperChildType{ EMPTY, LOADING, LIST };
	
	private ViewDisplayListApps freshAvailableApps = null;
	private ViewDisplayListApps availableApps = null;
	private ViewDisplayListApps freshInstalledApps = null;
	private ViewDisplayListApps installedApps = null;
	private ViewDisplayListApps freshUpdatableApps = null;
	private ViewDisplayListApps updatableApps = null;
	
	private StaticListsManager staticListsManager;
	private AvailableAppsManager availableAppsManager;
	
	private SimpleAdapter availableAdapter = null;
	private SimpleAdapter installedAdapter = null;
	private SimpleAdapter updatableAdapter = null;
	
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

//		            switchInstalledToProgressBar();
//		            showInstalledList();
		            
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
	            serviceDataCaller.callRegisterAvailableAppsObserver(serviceDataCallback);
	            
	            AptoideLog.v(Aptoide.this, "Called for registering as InstalledPackages Observer");
	            serviceDataCaller.callRegisterInstalledAppsObserver(serviceDataCallback);

	        } catch (RemoteException e) {
				// TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        
	        try {
				serviceDataCaller.callAddRepo(new ViewRepository("http://apps.bazaarandroid.com/"));
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
			staticListsManager.resetInstalledApps();
		}
		
		@Override
		public void newAvailableListDataAvailable() throws RemoteException {
			AptoideLog.v(Aptoide.this, "received newAvailableListDataAvailable callback");
			availableAppsManager.request(EnumAvailableRequestType.RESET);
		}

		@Override
		public void refreshAvailableDisplay() throws RemoteException {
			AptoideLog.v(Aptoide.this, "received refreshAvailableDisplay callback");
			serviceDataCallbackHandler.sendEmptyMessage(EnumAptoideAppsListsTasks.REFRESH_AVAILABLE_DISPLAY.ordinal());
			
		}
	};
	
    
    private Handler serviceDataCallbackHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	EnumAptoideAppsListsTasks message = EnumAptoideAppsListsTasks.reverseOrdinal(msg.what);
        	switch (message) {
	        	case RESET_INSTALLED_LIST_DISPLAY:
	        		resetDisplayInstalled();
					break;
				
				case RESET_AVAILABLE_LIST_DISPLAY:
					resetDisplayAvailable();
					break;
					
				case UPDATE_AVAILABLE_LIST_DISPLAY:
					updateDisplayAvailable();
					break;
					
				case REFRESH_AVAILABLE_DISPLAY:
					refreshAvailableDisplay();
					break;
				
				case RESET_UPDATABLE_LIST_DISPLAY:
					resetDisplayUpdates();
					break;
					
				case REFRESH_UPDATABLE_DISPLAY:
					refreshUpdatableDisplay();
					break;
	
				default:
					break;
			}
        }
    };
    
    
    
    private class StaticListsManager{
    	private ExecutorService installedColectorsPool;
    	
    	public StaticListsManager(){
    		installedColectorsPool = Executors.newSingleThreadExecutor();
    	}
    	
    	public void resetInstalledApps(){
        	installedColectorsPool.execute(new GetInstalledApps());
        }
    	
    	private class GetInstalledApps implements Runnable{

			@Override
			public void run() {
				try {
					setFreshInstalledApps(serviceDataCaller.callGetInstalledApps());
					serviceDataCallbackHandler.sendEmptyMessage(EnumAptoideAppsListsTasks.RESET_INSTALLED_LIST_DISPLAY.ordinal());
					if(!(availableApps.getList().size()==0)){
						setFreshUpdatableApps(serviceDataCaller.callGetUpdatableApps());
						serviceDataCallbackHandler.sendEmptyMessage(EnumAptoideAppsListsTasks.RESET_UPDATABLE_LIST_DISPLAY.ordinal());
					}
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
    		
    	}
    }
    
    
    private enum EnumAvailableRequestType { INCREASE, DECREASE, RESET };
    
    private class AvailableAppsManager{
    	
    	private LinkedList<EnumAvailableRequestType> requestFifo;
    	private AtomicInteger cacheListOffset;
    	private ExecutorService dataColectorsPool;

		
		private synchronized LinkedList<EnumAvailableRequestType> getRequestFifo(){
			return requestFifo;
		}
		
		//TODO lock access to requestFifo while suming
		public synchronized int getRequestsSum(){
			Log.d("Aptoide", "requestFifo: "+requestFifo);
			int requestsSum = 0;
			while( !getRequestFifo().isEmpty() ) {
				switch (getRequestFifo().removeFirst()) {
					case INCREASE:
						requestsSum++;
						break;
						
					case DECREASE:
						requestsSum--;
						break;
						
					case RESET:
						break;
	
					default:
						break;
				}
			}
			return requestsSum;
		}

		
		public AvailableAppsManager() {
    		requestFifo = new LinkedList<Aptoide.EnumAvailableRequestType>();
    		cacheListOffset = new AtomicInteger(0);
    		dataColectorsPool = Executors.newSingleThreadExecutor();
    	}
		
		public int getCacheOffset(){
			return this.cacheListOffset.get();
		}
		
		public void request(EnumAvailableRequestType requestType){
			getRequestFifo().addLast(requestType);
			dataColectorsPool.execute(new AvailableDataCollector());
		}
    	
		
        private class AvailableDataCollector implements Runnable {

			@Override
			public void run() {
				if(!getRequestFifo().isEmpty() && getRequestFifo().getFirst().equals(EnumAvailableRequestType.RESET)){
					getRequestFifo().removeFirst();
					int cacheOffset = cacheListOffset.get();
					try {
						Log.d("Aptoide","resetting available list.  offset: "+cacheOffset*Constants.DISPLAY_LISTS_CACHE_SIZE+" range: "+(availableApps.getList().size()==0?cacheOffset*Constants.DISPLAY_LISTS_CACHE_SIZE+Constants.DISPLAY_LISTS_CACHE_SIZE:availableApps.getList().size()));
						setFreshAvailableApps(serviceDataCaller.callGetAvailableApps(cacheOffset*Constants.DISPLAY_LISTS_CACHE_SIZE
												, (availableApps.getList().size()==0?cacheOffset*Constants.DISPLAY_LISTS_CACHE_SIZE+Constants.DISPLAY_LISTS_CACHE_SIZE:availableApps.getList().size()) ));
						serviceDataCallbackHandler.sendEmptyMessage(EnumAptoideAppsListsTasks.RESET_AVAILABLE_LIST_DISPLAY.ordinal());
						
						setFreshUpdatableApps(serviceDataCaller.callGetUpdatableApps());
						serviceDataCallbackHandler.sendEmptyMessage(EnumAptoideAppsListsTasks.RESET_UPDATABLE_LIST_DISPLAY.ordinal());
						
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return;
				}
				int requestsSum = getRequestsSum();
				if(requestsSum==0){
					return;
				}
				int previousCacheOffset = cacheListOffset.getAndAdd(requestsSum);
				int currentCacheOffset = cacheListOffset.get();
				try {
					if(requestsSum>0){
						Log.d("Aptoide","adding to available list.  offset: "+(previousCacheOffset+1)*Constants.DISPLAY_LISTS_CACHE_SIZE+" range: "+(currentCacheOffset+1)*Constants.DISPLAY_LISTS_CACHE_SIZE);
						setFreshAvailableApps(serviceDataCaller.callGetAvailableApps((previousCacheOffset+1)*Constants.DISPLAY_LISTS_CACHE_SIZE, (currentCacheOffset+1)*Constants.DISPLAY_LISTS_CACHE_SIZE));
						
						if(currentCacheOffset>2){
							if(previousCacheOffset<2){
								requestsSum -= (2-previousCacheOffset);
								//TODO remove requestsSum*Constants.DISPLAY_LISTS_CACHE_SIZE elements from beginning of availableappsList
							}
						}
						
						serviceDataCallbackHandler.sendEmptyMessage(EnumAptoideAppsListsTasks.UPDATE_AVAILABLE_LIST_DISPLAY.ordinal());
						
					}else{
//						addBeginningFreshAvailableApps(serviceDataCaller.callGetAvailablePackages(previousCacheOffset*Constants.DISPLAY_LISTS_CACHE_SIZE, currentCacheOffset*Constants.DISPLAY_LISTS_CACHE_SIZE));
//						
//						if((currentCacheOffset*Constants.DISPLAY_LISTS_CACHE_SIZE)>(availableApps.getList().size()-Constants.DISPLAY_LISTS_CACHE_SIZE*2) && requestsSum<0){
//							if((previousCacheOffset*Constants.DISPLAY_LISTS_CACHE_SIZE)>(availableApps.getList().size()-Constants.DISPLAY_LISTS_CACHE_SIZE*2)){
//								requestsSum += 2;
//								//TODO remove requestsSum*Constants.DISPLAY_LISTS_CACHE_SIZE elements from end of availableappsList
//							}
//						}
//						
//						serviceDataCallbackHandler.sendEmptyMessage(EnumServiceDataCallback.RESET_AVAILABLE_LIST_DISPLAY.ordinal());
					}
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
        	
        }

    }
    

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aptoide);
       
		staticListsManager = new StaticListsManager();
		availableAppsManager = new AvailableAppsManager();
		
		makeSureServiceDataIsRunning();
		
		
		emptyAvailableAppsList = new TextView(this);
		emptyAvailableAppsList.setBackgroundColor(Color.WHITE);
		emptyAvailableAppsList.setTextColor(Color.BLACK);
		emptyAvailableAppsList.setTextSize(24);
		emptyAvailableAppsList.setText(R.string.loading_available_apps);
		emptyAvailableAppsList.setTag(EnumFlipperChildType.EMPTY);
		
		emptyInstalledAppsList = new TextView(this);
		emptyInstalledAppsList.setBackgroundColor(Color.WHITE);
		emptyInstalledAppsList.setTextColor(Color.BLACK);
		emptyInstalledAppsList.setTextSize(24);
		emptyInstalledAppsList.setText(R.string.loading_installed_apps);
		emptyInstalledAppsList.setTag(EnumFlipperChildType.EMPTY);
		
		emptyUpdatableAppsList = new TextView(this);
		emptyUpdatableAppsList.setBackgroundColor(Color.WHITE);
		emptyUpdatableAppsList.setTextColor(Color.BLACK);
		emptyUpdatableAppsList.setTextSize(24);
		emptyUpdatableAppsList.setText(R.string.loading_updatable_apps);
		emptyUpdatableAppsList.setTag(EnumFlipperChildType.EMPTY);
		
		loadingAvailableAppsList = new ProgressBar(this);
		loadingAvailableAppsList.setTag(EnumFlipperChildType.LOADING);
		
		loadingInstalledAppsList = new ProgressBar(this);
		loadingInstalledAppsList.setTag(EnumFlipperChildType.LOADING);
		
		loadingUpdatableAppsList = new ProgressBar(this);
		loadingUpdatableAppsList.setTag(EnumFlipperChildType.LOADING);
		
		
		installedApps = new ViewDisplayListApps(Constants.DISPLAY_LISTS_CACHE_SIZE);
		availableApps = new ViewDisplayListApps(Constants.DISPLAY_LISTS_CACHE_SIZE);
		updatableApps = new ViewDisplayListApps(Constants.DISPLAY_LISTS_CACHE_SIZE);

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
		
		appsListFlipper = (ViewFlipper) findViewById(R.id.list_flipper);
		
		availableAppsListView = new ListView(this);
		availableAppsListView.setOnTouchListener(swypeListener);
		availableAppsListView.setOnScrollListener(scrollListener);
		availableAppsListView.setOnItemClickListener(this);
		availableAppsListView.setTag(EnumFlipperChildType.LIST);
//		appsListFlipper.addView(availableAppsList);
		
		installedAppsListView = new ListView(this);
		installedAppsListView.setOnTouchListener(swypeListener);
		installedAppsListView.setOnScrollListener(scrollListener);
		installedAppsListView.setOnItemClickListener(this);
		installedAppsListView.setTag(EnumFlipperChildType.LIST);
//		appsListFlipper.addView(installedAppsList);
		
		updatableAppsListView = new ListView(this);
		updatableAppsListView.setOnTouchListener(swypeListener);
		updatableAppsListView.setOnScrollListener(scrollListener);
		updatableAppsListView.setOnItemClickListener(this);
		updatableAppsListView.setTag(EnumFlipperChildType.LIST);
//		appsListFlipper.addView(updatableAppsList);

		appsListFlipper.addView(emptyAvailableAppsList);
		appsListFlipper.addView(emptyInstalledAppsList);
		appsListFlipper.addView(emptyUpdatableAppsList);
		
		currentAppsList = EnumAppsLists.Available;

    }
    

	public void initDisplayAvailable(){
		availableAdapter = new SimpleAdapter(Aptoide.this, availableApps.getList(), R.layout.app_row, 
				new String[] {Constants.KEY_APPLICATION_HASHID, Constants.KEY_APPLICATION_NAME, Constants.DISPLAY_APP_UP_TO_DATE_VERSION_NAME, Constants.KEY_STATS_DOWNLOADS,Constants.KEY_STATS_STARS,  Constants.DISPLAY_APP_ICON_CACHE_PATH},
				new int[] {R.id.app_hashid, R.id.app_name, R.id.uptodate_versionname, R.id.downloads, R.id.stars, R.id.app_icon});
		
		availableAdapter.setViewBinder(new AvailableAppsListBinder());
		availableAppsListView.setAdapter(availableAdapter);
    }
	
	public synchronized void setFreshAvailableApps(ViewDisplayListApps freshAvailableApps){
		AptoideLog.d(Aptoide.this, "setFreshAvailableList");
		this.freshAvailableApps = freshAvailableApps;
	}
	
	public synchronized void addBeginningFreshAvailableApps(ViewDisplayListApps freshAvailableApps){
		AptoideLog.d(Aptoide.this, "addBeginningFreshAvailableList");
		this.freshAvailableApps = freshAvailableApps;
		this.freshAvailableApps.getList().addAll(availableApps.getList());
	}
	
	public void resetDisplayAvailable(){
//		if(availableApps.getList().size()==0){
			switchAvailableToList();
//			showAvailableList();			
//		}
    	AptoideLog.d(Aptoide.this, "new AvailableList: "+freshAvailableApps);
		boolean newList = this.updatableApps.getList().isEmpty();
		int scrollRestorePosition = availableAppsListView.getFirstVisiblePosition();
		int partialScrollRestorePosition = (availableAppsListView.getChildAt(0)==null?0:availableAppsListView.getChildAt(0).getTop());
    	this.availableApps = freshAvailableApps;
    	initDisplayAvailable();
    	if(!newList){
    		refreshAvailableDisplay();
    	}
    	availableAppsListView.setSelectionFromTop(scrollRestorePosition, partialScrollRestorePosition);
	}
	
	public void updateDisplayAvailable(ViewDisplayListApps freshAvailableApps){	
    	AptoideLog.d(Aptoide.this, "freshAvailableList: "+freshAvailableApps);
		boolean newList = this.availableApps.getList().isEmpty();
    	if(newList){
    		this.availableApps = freshAvailableApps;
    		initDisplayAvailable();
    	}else{		//TODO append new list elements on the end or the beginning depending on scroll direction, and clear the same number of elements on the other side of the list.
    		AptoideLog.d(this, "available list not empty");
    		this.availableApps.getList().addAll(freshAvailableApps.getList());
    		refreshAvailableDisplay();
    	}
		
	}
	
	public void updateDisplayAvailable(){
		updateDisplayAvailable(this.freshAvailableApps);
	}
	
	public void refreshAvailableDisplay(){
		availableAdapter.notifyDataSetChanged();
	}
    
    
    public void initDisplayInstalled(){
    	installedAdapter = new SimpleAdapter(Aptoide.this, installedApps.getList(), R.layout.app_row, 
				new String[] {Constants.KEY_APPLICATION_HASHID, Constants.KEY_APPLICATION_NAME, Constants.DISPLAY_APP_UP_TO_DATE_VERSION_NAME
    						, Constants.DISPLAY_APP_INSTALLED_VERSION_NAME, Constants.DISPLAY_APP_IS_DOWNGRADABLE, Constants.DISPLAY_APP_ICON_CACHE_PATH},
				new int[] {R.id.app_hashid, R.id.app_name, R.id.uptodate_versionname, R.id.installed_versionname, R.id.isDowngradeAvailable, R.id.app_icon});
		
		installedAdapter.setViewBinder(new InstalledAppsListBinder());
		installedAppsListView.setAdapter(installedAdapter);
    }
	
	public synchronized void setFreshInstalledApps(ViewDisplayListApps freshInstalledApps){
		this.freshInstalledApps = freshInstalledApps;
	}
	
	public void resetDisplayInstalled(){
//		boolean bootingUp = false;
//		if(installedApps.getList().size()==0){
			switchInstalledToList();
//	    	showInstalledList();
//	    	bootingUp = true;
//		}
    	AptoideLog.d(Aptoide.this, "new InstalledList: "+freshInstalledApps);
		boolean newList = this.updatableApps.getList().isEmpty();
    	this.installedApps = freshInstalledApps;
    	initDisplayInstalled();
    	if(!newList){
    		installedAdapter.notifyDataSetChanged();
    	}
//    	if(bootingUp && availableApps.getList().size()==0){
//			switchAvailableToProgressBar();
//			showAvailableList();
//		}
	}
	
	public void updateDisplayInstalled(ViewDisplayListApps installedApps){
    	AptoideLog.d(Aptoide.this, "InstalledList: "+installedApps);
		boolean newList = this.installedApps.getList().isEmpty();
    	if(newList){
    		this.installedApps = installedApps;
    		initDisplayInstalled();
    	}else{		//TODO append new list elements on the end or the beginning depending on scroll direction, and clear the same number of elements on the other side of the list.
    		AptoideLog.d(this, "installed list not empty");
    		this.installedApps.getList().addAll(installedApps.getList());
    		installedAdapter.notifyDataSetChanged();
    	}
		
	}
    
    
    public void initDisplayUpdates(){
    	updatableAdapter = new SimpleAdapter(Aptoide.this, updatableApps.getList(), R.layout.app_row, 
    			new String[] {Constants.KEY_APPLICATION_HASHID, Constants.KEY_APPLICATION_NAME, Constants.DISPLAY_APP_UP_TO_DATE_VERSION_NAME
    						, Constants.KEY_STATS_DOWNLOADS, Constants.KEY_STATS_STARS, Constants.DISPLAY_APP_ICON_CACHE_PATH},
    		new int[] {R.id.app_hashid, R.id.app_name, R.id.uptodate_versionname, R.id.downloads, R.id.stars, R.id.app_icon});

    	updatableAdapter.setViewBinder(new UpdatableAppsListBinder());
    	updatableAppsListView.setAdapter(updatableAdapter);
    }
	
	public synchronized void setFreshUpdatableApps(ViewDisplayListApps freshUpdatableApps){
		this.freshUpdatableApps = freshUpdatableApps;
	}
	
	public void resetDisplayUpdates(){
//		if(updatableApps.getList().size()==0){
		switchUpdatableToList();
//		showUpdatableList();			
//	}
    	AptoideLog.d(Aptoide.this, "new UpdatesList: "+freshUpdatableApps);
		boolean newList = this.updatableApps.getList().isEmpty();
    	this.updatableApps = freshUpdatableApps;
    	initDisplayUpdates();
    	if(!newList){
    		updatableAdapter.notifyDataSetChanged();
    	}
	}
	
	public void updateDisplayUpdates(ViewDisplayListApps updatableApps){
    	AptoideLog.d(Aptoide.this, "UpdatesList: "+updatableApps);
		boolean newList = this.updatableApps.getList().isEmpty();
    	if(newList){
    		this.updatableApps = updatableApps;
    		initDisplayUpdates();
    	}else{	//TODO append new list elements on the end or the beginning depending on scroll direction, and clear the same number of elements on the other side of the list.
    		AptoideLog.d(this, "update list not empty");
    		this.updatableApps.getList().addAll(updatableApps.getList());
    		updatableAdapter.notifyDataSetChanged();
    	}
		
	}
	
	public void refreshUpdatableDisplay(){
		updatableAdapter.notifyDataSetChanged();
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


	class UpdatableAppsListBinder implements ViewBinder
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

	private void switchAvailableToProgressBar(){
        appsListFlipper.invalidate();
        appsListFlipper.removeViewAt(EnumAppsLists.Available.ordinal());
        appsListFlipper.addView(loadingAvailableAppsList, EnumAppsLists.Available.ordinal());
	}

	private void switchAvailableToList(){
        appsListFlipper.invalidate();
        appsListFlipper.removeViewAt(EnumAppsLists.Available.ordinal());
        appsListFlipper.addView(availableAppsListView, EnumAppsLists.Available.ordinal());
	}

	private void switchInstalledToProgressBar(){
        appsListFlipper.invalidate();
        appsListFlipper.removeViewAt(EnumAppsLists.Installed.ordinal());
        appsListFlipper.addView(loadingInstalledAppsList, EnumAppsLists.Installed.ordinal());
	}

	private void switchInstalledToList(){
        appsListFlipper.invalidate();
        appsListFlipper.removeViewAt(EnumAppsLists.Installed.ordinal());
        appsListFlipper.addView(installedAppsListView, EnumAppsLists.Installed.ordinal());
	}

	private void switchUpdatableToList(){
        appsListFlipper.invalidate();
        appsListFlipper.removeViewAt(EnumAppsLists.Updates.ordinal());
        appsListFlipper.addView(updatableAppsListView, EnumAppsLists.Updates.ordinal());
	}
	
	private void showAvailableList(){
		switch (currentAppsList) {
			case Available:
				appsListFlipper.setAnimation(null);
				appsListFlipper.showNext();
				appsListFlipper.showPrevious();
				break;
		
			case Installed:
				showPreviousList();
				break;
			
			case Updates:
				showPreviousList();
				showPreviousList();
				break;
	
			default:
				break;
		}
	}
	
	@SuppressWarnings("unused")
	private void showInstalledList(){
		switch (currentAppsList) {
			case Available:
				showNextList();
				break;
			
			case Updates:
				showPreviousList();
				break;
	
			case Installed:
				appsListFlipper.setAnimation(null);
				appsListFlipper.showNext();
				appsListFlipper.showPrevious();
				break;
				
			default:
				break;
		}
	}
	
	@SuppressWarnings("unused")
	private void showUpdatableList(){
		switch (currentAppsList) {
			case Available:
				showNextList();
				showNextList();
				break;
			
			case Installed:
				showNextList();
				break;
			
			case Updates:
				appsListFlipper.setAnimation(null);
				appsListFlipper.showNext();
				appsListFlipper.showPrevious();
				break;
	
			default:
				break;
		}
	}
	
    
	private void showNextList(){
		appsListFlipper.setOutAnimation(AnimationUtils.loadAnimation(Aptoide.this, R.anim.flip_out_next));
		appsListFlipper.setInAnimation(AnimationUtils.loadAnimation(Aptoide.this, R.anim.flip_in_next));
		appsListFlipper.showNext();
		currentAppsList = EnumAppsLists.getNext(currentAppsList);
	}
	
	private void showPreviousList(){
		appsListFlipper.setOutAnimation(AnimationUtils.loadAnimation(Aptoide.this, R.anim.flip_out_previous));
		appsListFlipper.setInAnimation(AnimationUtils.loadAnimation(Aptoide.this, R.anim.flip_in_previous));
		appsListFlipper.showPrevious();
		currentAppsList = EnumAppsLists.getPrevious(currentAppsList);
	}
    
    
    class SwypeDetector extends SimpleOnGestureListener {

    	private static final int SWIPE_MIN_DISTANCE = 80;
    	private static final int SWIPE_MAX_OFF_PATH = 250;
    	private static final int SWIPE_THRESHOLD_VELOCITY = 150;
    	
    	private ExecutorService scrollBlocker = Executors.newSingleThreadExecutor();

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
		        		showNextList();
	        		}
	    		} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
	        		Log.d("Aptoide","Swype left");
	        		if(EnumAppsLists.getPrevious(currentAppsList).equals(currentAppsList)){
	        			appsListFlipper.startAnimation(AnimationUtils.loadAnimation(Aptoide.this, R.anim.flip_resist_previous));
	        		}else{
		        		showPreviousList();
	        		}
	    		}
	    		scrollBlocker.execute(new Swyping());
				
	    		return super.onFling(e1, e2, velocityX, velocityY);
    		}
    	}
		
		class Swyping implements Runnable{
			@Override
			public void run() {
				swypeDelayHandler.postDelayed(new Runnable() {
	                public void run() {
	                	swyping.set(false);
	                }
	            }, 500);
			}
		}
		
    }
    
    class ScrollDetector implements OnScrollListener{

    	AtomicInteger initialFirstVisibleItem = new AtomicInteger(0);
    	AtomicInteger firstVisibleItem = new AtomicInteger(0);
    	AtomicInteger visibleItemCount = new AtomicInteger(0);
    	AtomicInteger differential = new AtomicInteger(0);
    	AtomicInteger displayOffset = new AtomicInteger(0);
    	AtomicInteger scrollRegionOrigin = new AtomicInteger(0);
    	
    	private void detectAvailableAppsCacheIncrease(int triggerMargin, int currentDisplayOffset, int previousScrollRegion, EnumAppsLists currentList){
    		Log.d("Aptoide","Scroll "+currentList+" discerning increase, display offset: "+currentDisplayOffset+" previousScrollRegion: "+previousScrollRegion+" triggerMargin:  "+triggerMargin);
			
			if( previousScrollRegion <  (( currentDisplayOffset / Constants.DISPLAY_LISTS_CACHE_SIZE) + 1)	
				&& (currentDisplayOffset % Constants.DISPLAY_LISTS_CACHE_SIZE) < (triggerMargin + Constants.DISPLAY_LISTS_PAGE_INCREASE_OFFSET_TRIGGER)					
				&& (currentDisplayOffset % Constants.DISPLAY_LISTS_CACHE_SIZE) > Constants.DISPLAY_LISTS_PAGE_INCREASE_OFFSET_TRIGGER ){
				
				this.scrollRegionOrigin.incrementAndGet();
				availableAppsManager.request(EnumAvailableRequestType.INCREASE);
				Log.d("Aptoide","Scroll "+currentList+" cache offset: "+availableAppsManager.getCacheOffset()+" requestFifo size: "+availableAppsManager.getRequestFifo().size());
				
			}
    	}
    	
    	private void detectAvailableAppsCacheDecrease(int triggerMargin, int currentDisplayOffset, int previousScrollRegion, EnumAppsLists currentList){
    		Log.d("Aptoide","Scroll "+currentList+" discerning decrease, display offset: "+currentDisplayOffset+" previousScrollRegion: "+previousScrollRegion+" triggerMargin:  "+triggerMargin);
			
			if( previousScrollRegion >  (( currentDisplayOffset / Constants.DISPLAY_LISTS_CACHE_SIZE) + 1)
				&& (currentDisplayOffset % Constants.DISPLAY_LISTS_CACHE_SIZE) > (Constants.DISPLAY_LISTS_PAGE_DECREASE_OFFSET_TRIGGER - triggerMargin)
				&& (currentDisplayOffset % Constants.DISPLAY_LISTS_CACHE_SIZE) < Constants.DISPLAY_LISTS_PAGE_DECREASE_OFFSET_TRIGGER ){
				
				this.scrollRegionOrigin.decrementAndGet();
				availableAppsManager.request(EnumAvailableRequestType.DECREASE);
				Log.d("Aptoide","Scroll "+currentList+" cache offset: "+availableAppsManager.getCacheOffset()+" requestFifo size: "+availableAppsManager.getRequestFifo().size());
				
			}
    	}
    	
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			if(!swyping.get()){
				this.firstVisibleItem.set(firstVisibleItem);
				this.visibleItemCount.set(visibleItemCount);
//				Log.d("Aptoide","Scroll currentList: "+currentAppsList+" initialfirstVisibleItem: "+initialFirstVisibleItem+" firstVisibleItem: "+firstVisibleItem);
				this.differential.set(Math.abs(firstVisibleItem-this.initialFirstVisibleItem.get()));
				
				if(this.initialFirstVisibleItem.get()+visibleItemCount < firstVisibleItem){
					this.initialFirstVisibleItem.set(firstVisibleItem);
					Log.d("Aptoide","New Scroll down page");
					switch (currentAppsList) {
						case Available:
//							if(availableApps.increaseDisplayRange(this.differential.get()) == EnumOffsetChange.increase){
//								try {
//									updateDisplayAvailable(serviceDataCaller.callGetAvailablePackages(availableApps.getCacheOffset(), availableApps.getCacheRange()));
//								} catch (RemoteException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								}
//							}

							int triggerMargin = this.visibleItemCount.get()*2;
							int currentDisplayOffset = this.displayOffset.addAndGet(this.differential.get());
							int previousScrollRegion = this.scrollRegionOrigin.get();
							detectAvailableAppsCacheIncrease(triggerMargin, currentDisplayOffset, previousScrollRegion, currentAppsList);
							break;
		
						default:
							break;
					}
				}else if(this.initialFirstVisibleItem.get()-visibleItemCount > firstVisibleItem){
					this.initialFirstVisibleItem.set(firstVisibleItem);
					Log.d("Aptoide","New Scroll up page");
					switch (currentAppsList) {
						case Available:														
//							availableApps.decreaseDisplayRange(this.differential.get());

							int triggerMargin = this.visibleItemCount.get()*2;
							int currentDisplayOffset = this.displayOffset.addAndGet(-this.differential.get());
							int previousScrollRegion = this.scrollRegionOrigin.get();
							detectAvailableAppsCacheDecrease(triggerMargin, currentDisplayOffset, previousScrollRegion, currentAppsList);
							break;
		
						default:
							break;
					}
				}
			}
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if( !swyping.get() && scrollState == SCROLL_STATE_IDLE){
//				Log.d("Aptoide","Scroll currentList: "+currentAppsList+" initialFirstVisibleItem: "+initialFirstVisibleItem+" firstVisibleItem: "+firstVisibleItem);
				this.differential.set(Math.abs(firstVisibleItem.get()-this.initialFirstVisibleItem.get()));
				
				
				if((firstVisibleItem.get()-initialFirstVisibleItem.get())>0){
					switch (currentAppsList) {
						case Available:
//							if(availableApps.increaseDisplayRange(this.differential.get()) == EnumOffsetChange.increase){
//								try {
//									updateDisplayAvailable(serviceDataCaller.callGetAvailablePackages(availableApps.getCacheOffset(), availableApps.getCacheRange()));
//								} catch (RemoteException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								}
//							}

							int triggerMargin = this.visibleItemCount.get()*2;
							int currentDisplayOffset = this.displayOffset.addAndGet(this.differential.get());
							int previousScrollRegion = this.scrollRegionOrigin.get();
							detectAvailableAppsCacheIncrease(triggerMargin, currentDisplayOffset, previousScrollRegion, currentAppsList);
							break;
		
						default:
							break;
					}
				}else{
					switch (currentAppsList) {
						case Available:
//							availableApps.decreaseDisplayRange(differential.get());

							int triggerMargin = this.visibleItemCount.get()*2;
							int currentDisplayOffset = this.displayOffset.addAndGet(-this.differential.get());
							int previousScrollRegion = this.scrollRegionOrigin.get();
							detectAvailableAppsCacheDecrease(triggerMargin, currentDisplayOffset, previousScrollRegion, currentAppsList);
							break;
		
						default:
							break;
					}
				}
				initialFirstVisibleItem.set(firstVisibleItem.get());
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
    		final String appHashid = ((LinearLayout)arg1).getTag().toString();
    		AptoideLog.d(this, "Onclick position: "+arg2+" appHashid: "+appHashid);
    		Intent appInfo = new Intent(this,AppInfo.class);
    		appInfo.putExtra("appHashid", appHashid);
    		startActivity(appInfo);
    		//TODO click change color effect
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
