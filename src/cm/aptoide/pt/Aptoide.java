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
import android.app.AlertDialog;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SimpleAdapter.ViewBinder;
import cm.aptoide.pt.data.AIDLAptoideServiceData;
import cm.aptoide.pt.data.AptoideServiceData;
import cm.aptoide.pt.data.display.ViewDisplayCategory;
import cm.aptoide.pt.data.display.ViewDisplayListApps;
import cm.aptoide.pt.data.system.ViewScreenDimensions;
import cm.aptoide.pt.data.util.Constants;
import cm.aptoide.pt.debug.AptoideLog;
import cm.aptoide.pt.debug.InterfaceAptoideLog;

/**
 * Aptoide, the main interface class
 * 			displays the available apps list
 * 
 * @author dsilveira
 *
 */
public class Aptoide extends Activity implements InterfaceAptoideLog, OnItemClickListener { 
	
	private final String TAG = "Aptoide";
	private boolean isRunning = false;
	
	private ScrollDetector scrollListener;
	private GestureDetector swypeDetector;
	private View.OnTouchListener swypeListener;
	private AtomicBoolean swyping = null;
	private Handler swypeDelayHandler = null;
	
	private ViewFlipper appsListFlipper = null;
	private View emptyAvailableAppsList;
	private View emptyInstalledAppsList;
	private View emptyUpdatableAppsList;
	private View loadingAvailableAppsList;
	private View loadingInstalledAppsList;
	private View loadingUpdatableAppsList;
	private ListView availableAppsListView = null;
	private ListView installedAppsListView = null;
	private ListView updatableAppsListView = null;
	private EnumAppsLists currentAppsList = null;
	
	private AtomicBoolean synchronizingInstalledApps = null;
	private String waitingSearchQuery = "";
	
	private enum EnumFlipperChildType{ EMPTY, LOADING, LIST };
	
	private ViewDisplayListApps freshAvailableApps = null;
	private int availableAppsTrimAmount = 0;	//TODO refactor as atomic, maybe try to integrate with adjustAvailableDisplayOffset
	private AtomicInteger originalScrollPostition;
	private AtomicInteger originalPartialScrollPostition;	
	private AtomicInteger adjustAvailableDisplayOffset;
	private AtomicInteger availableDisplayOffsetAdjustments;
	
	private int DISPLAY_LISTS_CACHE_SIZE;
	
	private ViewDisplayCategory category = null;
	private ViewDisplayCategory freshCategory = null;
	private ViewDisplayListApps availableApps = null;
	private ViewDisplayListApps freshInstalledApps = null;
	private ViewDisplayListApps installedApps = null;
	private ViewDisplayListApps freshUpdatableApps = null;
	private ViewDisplayListApps updatableApps = null;
	
	private boolean availableByCategory = true;
	
	private InstalledAppsManager staticListsManager;
	private AvailableAppsManager availableAppsManager;
	
	private SimpleAdapter categoriesAdapter = null;
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
				synchronizingInstalledApps.set(true);
				try {
		            
		            AptoideLog.v(Aptoide.this, "Called for a synchronization of installed Packages, because serviceData wasn't previously running");

//		            switchInstalledToProgressBar();
//		            showInstalledList();
		            
		            serviceDataCaller.callSyncInstalledApps();	
		        } catch (RemoteException e) {
					// TODO Auto-generated catch block
		            e.printStackTrace();
		        }
			}
			
            
            try {
                AptoideLog.v(Aptoide.this, "Called for registering as InstalledApps Observer");
				serviceDataCaller.callRegisterInstalledAppsObserver(serviceDataCallback);
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        
	        handleIncomingIntent(getIntent());
			
            
			DisplayMetrics displayMetrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
			ViewScreenDimensions screenDimensions = new ViewScreenDimensions(displayMetrics.widthPixels, displayMetrics.heightPixels);
			AptoideLog.d(Aptoide.this, screenDimensions.toString());
			DISPLAY_LISTS_CACHE_SIZE = ((screenDimensions.getHeight()>screenDimensions.getWidth()?screenDimensions.getHeight():screenDimensions.getWidth())/Constants.DISPLAY_SIZE_DIVIDER)*Constants.DISPLAY_LISTS_CACHE_SIZE_MULTIPLIER;
			AptoideLog.d(Aptoide.this, "DISPLAY_LISTS_CACHE_SIZE: "+DISPLAY_LISTS_CACHE_SIZE);
	        try {
	            AptoideLog.v(Aptoide.this, "Called for screenDimensions storage");
	            serviceDataCaller.callStoreScreenDimensions(screenDimensions);
	        } catch (RemoteException e) {
				// TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        
	        try {
	            AptoideLog.v(Aptoide.this, "Called for checking if AvailableApps are by Category");
	            availableByCategory = serviceDataCaller.callAreListsByCategory();	        	
	        	
	            AptoideLog.v(Aptoide.this, "Called for registering as AvailableApps Observer");
	            serviceDataCaller.callRegisterAvailableAppsObserver(serviceDataCallback);

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
			if(synchronizingInstalledApps.get()){
				synchronizingInstalledApps.set(false);
				if(!waitingSearchQuery.equals("")){
					startSearch(waitingSearchQuery, false, null, false);
					waitingSearchQuery = "";
				}
			}
			AptoideLog.v(Aptoide.this, "received newInstalledListDataAvailable callback");
			staticListsManager.resetInstalledApps();
		}
		
		@Override
		public void newAvailableListDataAvailable() throws RemoteException {
			AptoideLog.v(Aptoide.this, "received newAvailableListDataAvailable callback");
			availableAppsManager.request(EnumAvailableRequestType.RESET);
		}

		@Override
		public void resetAvailableListData() throws RemoteException {
			AptoideLog.v(Aptoide.this, "received resetAvailableListData callback");
			availableAppsManager.request(EnumAvailableRequestType.RESET_TO_ZERO);			
		}

		@Override
		public void refreshAvailableDisplay() throws RemoteException {
			AptoideLog.v(Aptoide.this, "received refreshAvailableDisplay callback");
			interfaceTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.REFRESH_AVAILABLE_DISPLAY.ordinal());
			
		}

		@Override
		public void noAvailableListDataAvailable() throws RemoteException {
			AptoideLog.v(Aptoide.this, "received noAvailableApps callback");
			interfaceTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.SWITCH_AVAILABLE_TO_NO_APPS.ordinal());
		}

		@Override
		public void loadingAvailableListDataAvailable() throws RemoteException {
			AptoideLog.v(Aptoide.this, "received loadingAvailableApps callback");
			interfaceTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.SWITCH_AVAILABLE_TO_PROGRESSBAR.ordinal());
		}

		@Override
		public void loadingInstalledListDataAvailable() throws RemoteException {
			AptoideLog.v(Aptoide.this, "received loadingInstalledApps callback");
			interfaceTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.SWITCH_INSTALLED_TO_PROGRESSBAR.ordinal());
		}
	};
	
    
    private Handler interfaceTasksHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	EnumAptoideInterfaceTasks task = EnumAptoideInterfaceTasks.reverseOrdinal(msg.what);
        	switch (task) {
	        	case RESET_INSTALLED_LIST_DISPLAY:
	        		resetDisplayInstalled();
					break;
					
	        	case RESET_CATEGORIES:
	        		resetDisplayCategories();
	        		break;
				
				case RESET_AVAILABLE_LIST_DISPLAY:
					resetDisplayAvailable();
					break;
					
				case TRIM_PREPEND_AND_UPDATE_AVAILABLE_LIST_DISPLAY:
					trimEndAvailableAppsList(availableAppsTrimAmount);
					prependAndUpdateDisplayAvailable(freshAvailableApps);
					break;
					
				case TRIM_APPEND_AND_UPDATE_AVAILABLE_LIST_DISPLAY:
					trimBeginningAvailableAppsList(availableAppsTrimAmount);
					appendAndUpdateDisplayAvailable(freshAvailableApps);
					break;
					
				case APPEND_AND_UPDATE_AVAILABLE_LIST_DISPLAY:
					appendAndUpdateDisplayAvailable(freshAvailableApps);
					break;
					
				case PREPEND_AND_UPDATE_AVAILABLE_LIST_DISPLAY:
					prependAndUpdateDisplayAvailable(freshAvailableApps);
					break;
					
				case REFRESH_AVAILABLE_DISPLAY:
					if(!availableByCategory || !category.hasChildren()){
						refreshAvailableDisplay();
					}
					break;
				
				case RESET_UPDATABLE_LIST_DISPLAY:
					resetDisplayUpdates();
					break;
					
				case REFRESH_UPDATABLE_DISPLAY:
					refreshUpdatableDisplay();
					break;
					
				case SWITCH_AVAILABLE_TO_PROGRESSBAR:
					switchAvailableToProgressBar();
					break;
					
				case SWITCH_INSTALLED_TO_PROGRESSBAR:
		    		switchInstalledToProgressBar();					
					break;
					
				case SWITCH_AVAILABLE_TO_NO_APPS:
					switchAvailableToEmpty();
					switch (currentAppsList) {
						case Available:
							showAvailableList();
							break;
							
						case Updates:
							showUpdatableList();
							break;
	
						default:
							break;
					}
					break;
	
				default:
					break;
			}
        }
    };
    
    
    
    private class InstalledAppsManager{
    	private ExecutorService installedColectorsPool;
    	
    	public InstalledAppsManager(){
    		installedColectorsPool = Executors.newSingleThreadExecutor();
    	}
    	
    	public void resetInstalledApps(){
        	installedColectorsPool.execute(new GetInstalledApps());
        }
    	
    	private class GetInstalledApps implements Runnable{

			@Override
			public void run() {
				interfaceTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.SWITCH_INSTALLED_TO_PROGRESSBAR.ordinal());
				try {
					setFreshInstalledApps(serviceDataCaller.callGetInstalledApps());
					interfaceTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.RESET_INSTALLED_LIST_DISPLAY.ordinal());
					if(!(availableApps.getList().size()==0)){
						setFreshUpdatableApps(serviceDataCaller.callGetUpdatableApps());
						interfaceTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.RESET_UPDATABLE_LIST_DISPLAY.ordinal());
					}
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
    		
    	}
    }
    
    
    private enum EnumAvailableRequestType { INCREASE, DECREASE, RESET, RESET_TO_ZERO };
    
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
				if(!getRequestFifo().isEmpty() && (getRequestFifo().getFirst().equals(EnumAvailableRequestType.RESET)
													|| getRequestFifo().getFirst().equals(EnumAvailableRequestType.RESET_TO_ZERO))){
					
					int offset;
					EnumAvailableRequestType request = getRequestFifo().removeFirst();
					if(availableByCategory){
						if(request.equals(EnumAvailableRequestType.RESET_TO_ZERO)){
							interfaceTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.SWITCH_AVAILABLE_TO_PROGRESSBAR.ordinal());
							try {
								if( category == null || category.getCategoryHashid() == Constants.TOP_CATEGORY || category.hasChildren() ){
									Log.d("Aptoide","resetting categories list.");
									setFreshCategories(serviceDataCaller.callGetCategories());
									interfaceTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.RESET_CATEGORIES.ordinal());
								}else{
									offset = 0;
									
									Log.d("Aptoide","resetting available list.  offset: "+offset+" range: "+DISPLAY_LISTS_CACHE_SIZE+" "+category);
									setFreshAvailableApps(serviceDataCaller.callGetAvailableAppsByCategory(offset, DISPLAY_LISTS_CACHE_SIZE, category.getCategoryHashid()));
									interfaceTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.RESET_AVAILABLE_LIST_DISPLAY.ordinal());
								}
								
								setFreshUpdatableApps(serviceDataCaller.callGetUpdatableApps());
								interfaceTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.RESET_UPDATABLE_LIST_DISPLAY.ordinal());
								
							} catch (RemoteException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}else{
							if(category != null && !category.hasChildren()){
								offset = cacheListOffset.get()*DISPLAY_LISTS_CACHE_SIZE;
								
								try {
									Log.d("Aptoide","resetting available list.  offset: "+offset+" range: "+DISPLAY_LISTS_CACHE_SIZE);
									setFreshAvailableApps(serviceDataCaller.callGetAvailableAppsByCategory(offset, DISPLAY_LISTS_CACHE_SIZE, category.getCategoryHashid()));
									interfaceTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.RESET_AVAILABLE_LIST_DISPLAY.ordinal());
									
									setFreshUpdatableApps(serviceDataCaller.callGetUpdatableApps());
									interfaceTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.RESET_UPDATABLE_LIST_DISPLAY.ordinal());
									
								} catch (RemoteException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					}else{
						if(request.equals(EnumAvailableRequestType.RESET_TO_ZERO)){
							interfaceTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.SWITCH_AVAILABLE_TO_PROGRESSBAR.ordinal());
							offset = 0;
						}else{
							offset = cacheListOffset.get()*DISPLAY_LISTS_CACHE_SIZE;
						}
						try {
							Log.d("Aptoide","resetting available list.  offset: "+offset+" range: "+DISPLAY_LISTS_CACHE_SIZE);
							setFreshAvailableApps(serviceDataCaller.callGetAvailableApps(offset, DISPLAY_LISTS_CACHE_SIZE));
							interfaceTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.RESET_AVAILABLE_LIST_DISPLAY.ordinal());
							
							setFreshUpdatableApps(serviceDataCaller.callGetUpdatableApps());
							interfaceTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.RESET_UPDATABLE_LIST_DISPLAY.ordinal());
							
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
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
						Log.d("Aptoide","advancing available list forward.  offset: "+(previousCacheOffset+1)*DISPLAY_LISTS_CACHE_SIZE+" range: "+DISPLAY_LISTS_CACHE_SIZE);
						setFreshAvailableApps(serviceDataCaller.callGetAvailableApps((previousCacheOffset+1)*DISPLAY_LISTS_CACHE_SIZE, DISPLAY_LISTS_CACHE_SIZE));
						
						if(availableApps.getList().size() > (2*DISPLAY_LISTS_CACHE_SIZE)){
							availableAppsTrimAmount = requestsSum*DISPLAY_LISTS_CACHE_SIZE;
							interfaceTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.TRIM_APPEND_AND_UPDATE_AVAILABLE_LIST_DISPLAY.ordinal());
						}else{
							interfaceTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.APPEND_AND_UPDATE_AVAILABLE_LIST_DISPLAY.ordinal());
						}
						
						
					}else{
						Log.d("Aptoide","advancing available list backward.  offset: "+(currentCacheOffset-1)*DISPLAY_LISTS_CACHE_SIZE+" range: "+(previousCacheOffset-1)*DISPLAY_LISTS_CACHE_SIZE);
						setFreshAvailableApps(serviceDataCaller.callGetAvailableApps((currentCacheOffset-1)*DISPLAY_LISTS_CACHE_SIZE, DISPLAY_LISTS_CACHE_SIZE));

						if(availableApps.getList().size() > (2*DISPLAY_LISTS_CACHE_SIZE)){
							availableAppsTrimAmount = (Math.abs(requestsSum)*DISPLAY_LISTS_CACHE_SIZE);
							interfaceTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.TRIM_PREPEND_AND_UPDATE_AVAILABLE_LIST_DISPLAY.ordinal());
						}else{
							interfaceTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.PREPEND_AND_UPDATE_AVAILABLE_LIST_DISPLAY.ordinal());							
						}
						
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
        if(!isRunning){
	        setContentView(R.layout.aptoide);
	       
			staticListsManager = new InstalledAppsManager();
			availableAppsManager = new AvailableAppsManager();
			
			emptyAvailableAppsList = LinearLayout.inflate(this, R.layout.list_apps_empty, appsListFlipper);
			emptyAvailableAppsList.setTag(EnumFlipperChildType.EMPTY);
			emptyInstalledAppsList = LinearLayout.inflate(this, R.layout.list_apps_empty, appsListFlipper);
			emptyInstalledAppsList.setTag(EnumFlipperChildType.EMPTY);
			emptyUpdatableAppsList = LinearLayout.inflate(this, R.layout.list_apps_empty, appsListFlipper);
			emptyUpdatableAppsList.setTag(EnumFlipperChildType.EMPTY);
			
			loadingAvailableAppsList = LinearLayout.inflate(this, R.layout.list_loading, appsListFlipper);
			loadingAvailableAppsList.setTag(EnumFlipperChildType.LOADING);
			loadingInstalledAppsList = LinearLayout.inflate(this, R.layout.list_loading, appsListFlipper);
			loadingInstalledAppsList.setTag(EnumFlipperChildType.LOADING);
			loadingUpdatableAppsList = LinearLayout.inflate(this, R.layout.list_loading, appsListFlipper);
			loadingUpdatableAppsList.setTag(EnumFlipperChildType.LOADING);
			
			installedApps = new ViewDisplayListApps();
			availableApps = new ViewDisplayListApps();
			updatableApps = new ViewDisplayListApps();
	
			swypeDetector = new GestureDetector(new SwypeDetector());
			swypeListener = new View.OnTouchListener() {
									@Override
									public boolean onTouch(View v, MotionEvent event) {
										return swypeDetector.onTouchEvent(event);
									}
								};
			swyping = new AtomicBoolean(false);
			originalScrollPostition = new AtomicInteger(0);
			originalPartialScrollPostition = new AtomicInteger(0);
			adjustAvailableDisplayOffset = new AtomicInteger(0);
			availableDisplayOffsetAdjustments = new AtomicInteger(0);
	
			swypeDelayHandler = new Handler();
			scrollListener = new ScrollDetector();
			
			appsListFlipper = (ViewFlipper) findViewById(R.id.list_flipper);
			
			availableAppsListView = new ListView(this);
			availableAppsListView.setOnTouchListener(swypeListener);
			availableAppsListView.setOnItemClickListener(this);
			availableAppsListView.setTag(EnumFlipperChildType.LIST);
			availableAppsListView.setPersistentDrawingCache(ViewGroup.PERSISTENT_ALL_CACHES);
	//		appsListFlipper.addView(availableAppsList);
			
			installedAppsListView = new ListView(this);
			installedAppsListView.setOnTouchListener(swypeListener);
			installedAppsListView.setOnItemClickListener(this);
			installedAppsListView.setTag(EnumFlipperChildType.LIST);
			installedAppsListView.setPersistentDrawingCache(ViewGroup.PERSISTENT_ALL_CACHES);
	//		appsListFlipper.addView(installedAppsList);
			
			updatableAppsListView = new ListView(this);
			updatableAppsListView.setOnTouchListener(swypeListener);
			updatableAppsListView.setOnItemClickListener(this);
			updatableAppsListView.setTag(EnumFlipperChildType.LIST);
			updatableAppsListView.setPersistentDrawingCache(ViewGroup.PERSISTENT_ALL_CACHES);
	//		appsListFlipper.addView(updatableAppsList);
	
			appsListFlipper.addView(emptyAvailableAppsList);
			appsListFlipper.addView(emptyInstalledAppsList);
			appsListFlipper.addView(emptyUpdatableAppsList);
			
			currentAppsList = EnumAppsLists.Available;
			
			synchronizingInstalledApps = new AtomicBoolean(false);
			
			makeSureServiceDataIsRunning();
			
			isRunning = true;
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
//    		startService(new Intent(this, AptoideServiceData.class));	//TODO uncomment this to make service independent of Aptoide's lifecycle
    		bindService(new Intent(this, AptoideServiceData.class), serviceDataConnection, Context.BIND_AUTO_CREATE);
    	}else{
    		handleIncomingIntent(getIntent());
    	}
    }
	
	
	@Override
	protected void onNewIntent(Intent intent) {
		handleIncomingIntent(intent);
		super.onNewIntent(intent);
	}
	
	private void cleanAptoideIntent(){
		Intent aptoide = new Intent(this, Aptoide.class);
		aptoide.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(aptoide);		
	}

	
	private void handleIncomingIntent(Intent incomingIntent){
        if(incomingIntent.getData() != null){
    		AptoideLog.d(this, "received intent: "+incomingIntent.getDataString()); 
        	if(incomingIntent.getType() != null && incomingIntent.getType().equals(Constants.MIMETYPE_MYAPP)){
        		AptoideLog.d(this, "received myapp: "+incomingIntent.getDataString());        		
		        try {
					serviceDataCaller.callReceiveMyapp(incomingIntent.getDataString());
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}else if(incomingIntent.getScheme().equals(Constants.SCHEME_MARKET) 
        			|| (incomingIntent.getScheme().equals(Constants.SCHEME_HTTPS) && incomingIntent.getData().getHost().equals(Constants.HOST_MARKET))){
        		String query = incomingIntent.getData().getQuery().split("&")[0].split("=")[1];
        		if(query.contains(":")){
        			query = query.split(":")[1];
        		}
        		AptoideLog.d(this, "received market query: "+query);
        		if(!synchronizingInstalledApps.get()){
        			startSearch(query, false, null, false);
        		}else{
        			waitingSearchQuery = query;
        		}
        	}
        	
        	cleanAptoideIntent();
        }
	}
	
	public void setAvailableListBy(boolean byCategory){
		AptoideLog.d(Aptoide.this, "setAvailableList ByCategory? "+byCategory);
		try {
			serviceDataCaller.callSetListsBy(byCategory);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void initDisplayCategories(){
		categoriesAdapter = new SimpleAdapter(Aptoide.this, category.getDisplayList(), R.layout.row_category 
         		, new String[] {Constants.KEY_CATEGORY_HASHID, Constants.KEY_CATEGORY_NAME, Constants.DISPLAY_CATEGORY_APPS}
				, new int[] {R.id.category_hashid, R.id.category_name, R.id.category_apps});
		
		categoriesAdapter.setViewBinder(new CategoryListBinder());
		availableAppsListView.setOnScrollListener(null);
		availableAppsListView.setAdapter(categoriesAdapter);
	}
	
	public synchronized void setFreshCategories(ViewDisplayCategory freshCategory){
		AptoideLog.d(Aptoide.this, "setFreshCategories");
		this.freshCategory = freshCategory;
		this.freshCategory.generateDisplayLists();
	}
	
	public void resetDisplayCategories(){
		if(freshCategory == null || (freshCategory.getCategoryHashid() == Constants.TOP_CATEGORY && !freshCategory.hasChildren())){
			switchAvailableToEmpty();
		}else{
			switchAvailableToList();
		}
		
		if(currentAppsList.equals(EnumAppsLists.Available)){
			showAvailableList();			
		}
		
    	AptoideLog.d(Aptoide.this, "new CategoriesList: "+freshCategory);
		boolean newList = this.category == null;
    	this.category = freshCategory;
    	initDisplayCategories();
    	if(!newList){
    		refreshCategoriesDisplay();
    	}
	}
	
	public void refreshCategoriesDisplay(){
		categoriesAdapter.notifyDataSetChanged();
	}
    

	public void initDisplayAvailable(){
		availableAdapter = new SimpleAdapter(Aptoide.this, availableApps.getList(), R.layout.row_app, 
				new String[] {Constants.KEY_APPLICATION_HASHID, Constants.KEY_APPLICATION_NAME, Constants.DISPLAY_APP_UP_TO_DATE_VERSION_NAME, Constants.KEY_STATS_DOWNLOADS,Constants.KEY_STATS_STARS,  Constants.DISPLAY_APP_ICON_CACHE_PATH},
				new int[] {R.id.app_hashid, R.id.app_name, R.id.uptodate_versionname, R.id.downloads, R.id.stars, R.id.app_icon});
		
		availableAdapter.setViewBinder(new AvailableAppsListBinder());
		availableAppsListView.setOnScrollListener(scrollListener);
		availableAppsListView.setAdapter(availableAdapter);
    }
	
	public synchronized void setFreshAvailableApps(ViewDisplayListApps freshAvailableApps){
		AptoideLog.d(Aptoide.this, "setFreshAvailableList");
		this.freshAvailableApps = freshAvailableApps;
	}
	
	public synchronized void trimBeginningAvailableAppsList(int trimAmount){
		int adjustAmount = -trimAmount;
		AptoideLog.d(Aptoide.this, "trimBeginningAvailableList: "+trimAmount);
		originalScrollPostition.set(availableAppsListView.getFirstVisiblePosition());
		originalPartialScrollPostition.set(availableAppsListView.getChildAt(0)==null?0:availableAppsListView.getChildAt(0).getTop());
		AptoideLog.d(this, "list size before: "+availableApps.getList().size());
		do{
			this.availableApps.getList().removeFirst();
			trimAmount--;
		}while(trimAmount>0);
		adjustAvailableDisplayOffset.set(adjustAmount);
		availableDisplayOffsetAdjustments.decrementAndGet();
		AptoideLog.d(this, "list size after: "+availableApps.getList().size());
	}
	
	public synchronized void trimEndAvailableAppsList(int trimAmount){
		AptoideLog.d(Aptoide.this, "trimEndAvailableList: "+trimAmount);
		do{
			this.availableApps.getList().removeLast();
			trimAmount--;
		}while(trimAmount>0);
	}
	
	public void resetDisplayAvailable(){
		if(freshAvailableApps.getList().size()==0){
			switchAvailableToEmpty();
		}else{
			switchAvailableToList();
		}
		
		if(currentAppsList.equals(EnumAppsLists.Available)){
			showAvailableList();			
		}
		
    	AptoideLog.d(Aptoide.this, "new AvailableList: "+freshAvailableApps);
    	originalScrollPostition.set(availableAppsListView.getFirstVisiblePosition());
		originalPartialScrollPostition.set(availableAppsListView.getChildAt(0)==null?0:availableAppsListView.getChildAt(0).getTop());
		boolean newList = this.availableApps.getList().isEmpty();
    	this.availableApps = freshAvailableApps;
    	initDisplayAvailable();
    	if(!newList){
    		refreshAvailableDisplay();
    	}
    	availableAppsListView.setSelectionFromTop((originalScrollPostition.get()+adjustAvailableDisplayOffset.get()), originalPartialScrollPostition.get());
	}
	
	public void appendAndUpdateDisplayAvailable(ViewDisplayListApps freshAvailableApps){	
    	AptoideLog.d(Aptoide.this, "appending freshAvailableList: "+freshAvailableApps+" size: "+freshAvailableApps.getList().size());
		boolean newList = this.availableApps.getList().isEmpty();
    	if(newList){
    		this.availableApps = freshAvailableApps;
    		initDisplayAvailable();
    	}else{	
    		AptoideLog.d(this, "available list not empty");
    		this.availableApps.getList().addAll(freshAvailableApps.getList());
    		AptoideLog.d(Aptoide.this, "new displayList size: "+this.availableApps.getList().size());
    		refreshAvailableDisplay();
    	}
    	if(adjustAvailableDisplayOffset.get() != 0){
			AptoideLog.d(this, "restoring scroll position, firstVisiblePosition: "+(originalScrollPostition.get()+adjustAvailableDisplayOffset.get())+" top: "+originalPartialScrollPostition.get());
	    	availableAppsListView.setSelectionFromTop((originalScrollPostition.get()+adjustAvailableDisplayOffset.get()), originalPartialScrollPostition.get());
    	}
	}
	
	public void prependAndUpdateDisplayAvailable(ViewDisplayListApps freshAvailableApps){	
    	AptoideLog.d(Aptoide.this, "prepending freshAvailableList: "+freshAvailableApps);
    	int adjustAmount = freshAvailableApps.getList().size();
    	boolean newList = this.availableApps.getList().isEmpty();
    	if(newList){
    		this.availableApps = freshAvailableApps;
    		initDisplayAvailable();
    	}else{	
    		int scrollRestorePosition = availableAppsListView.getFirstVisiblePosition();
    		int partialScrollRestorePosition = (availableAppsListView.getChildAt(0)==null?0:availableAppsListView.getChildAt(0).getTop());
    		AptoideLog.d(this, "available list not empty");
    		this.availableApps.getList().addAll(0,freshAvailableApps.getList());
    		AptoideLog.d(Aptoide.this, "new displayList size: "+this.availableApps.getList().size());
    		refreshAvailableDisplay();

    		adjustAvailableDisplayOffset.set(adjustAmount);
    		if(availableDisplayOffsetAdjustments.get()!=0){
    			availableDisplayOffsetAdjustments.incrementAndGet();
    		}
        	availableAppsListView.setSelectionFromTop(scrollRestorePosition+freshAvailableApps.getList().size(), partialScrollRestorePosition);
    	}
	}
	
	public void refreshAvailableDisplay(){
		availableAdapter.notifyDataSetChanged();
	}
    
    
    public void initDisplayInstalled(){
    	installedAdapter = new SimpleAdapter(Aptoide.this, installedApps.getList(), R.layout.row_app, 
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
		if(freshInstalledApps.getList().size()==0){
			switchInstalledToEmpty();
		}else{
			switchInstalledToList();
		}

		if(currentAppsList.equals(EnumAppsLists.Installed)){
			showInstalledList();
		}

		AptoideLog.d(Aptoide.this, "new InstalledList: "+freshInstalledApps);
		boolean newList = this.installedApps.getList().isEmpty();
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
	
//	public void updateDisplayInstalled(ViewDisplayListApps installedApps){
//    	AptoideLog.d(Aptoide.this, "InstalledList: "+installedApps);
//		boolean newList = this.installedApps.getList().isEmpty();
//    	if(newList){
//    		this.installedApps = installedApps;
//    		initDisplayInstalled();
//    	}else{		//TODO append new list elements on the end or the beginning depending on scroll direction, and clear the same number of elements on the other side of the list.
//    		AptoideLog.d(this, "installed list not empty");
//    		this.installedApps.getList().addAll(installedApps.getList());
//    		installedAdapter.notifyDataSetChanged();
//    	}
//		
//	}
    
    
    public void initDisplayUpdates(){
    	updatableAdapter = new SimpleAdapter(Aptoide.this, updatableApps.getList(), R.layout.row_app, 
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
		if(freshUpdatableApps.getList().size()==0){
			switchUpdatableToEmpty();
		}else{
			switchUpdatableToList();
		}

		if(currentAppsList.equals(EnumAppsLists.Updates)){
			showUpdatableList();			
		}

		AptoideLog.d(Aptoide.this, "new UpdatesList: "+freshUpdatableApps);
		boolean newList = this.updatableApps.getList().isEmpty();
    	this.updatableApps = freshUpdatableApps;
    	initDisplayUpdates();
    	if(!newList){
    		updatableAdapter.notifyDataSetChanged();
    	}
	}
	
//	public void updateDisplayUpdates(ViewDisplayListApps updatableApps){
//    	AptoideLog.d(Aptoide.this, "UpdatesList: "+updatableApps);
//		boolean newList = this.updatableApps.getList().isEmpty();
//    	if(newList){
//    		this.updatableApps = updatableApps;
//    		initDisplayUpdates();
//    	}else{	//TODO append new list elements on the end or the beginning depending on scroll direction, and clear the same number of elements on the other side of the list.
//    		AptoideLog.d(this, "update list not empty");
//    		this.updatableApps.getList().addAll(updatableApps.getList());
//    		updatableAdapter.notifyDataSetChanged();
//    	}
//		
//	}
	
	public void refreshUpdatableDisplay(){
		updatableAdapter.notifyDataSetChanged();
	}


	class CategoryListBinder implements ViewBinder	//TODO needs some improvements
	{
		public boolean setViewValue(View view, Object data, String textRepresentation)
		{
			if(view.getClass().toString().equalsIgnoreCase("class android.widget.TextView")){
				TextView tmpr = (TextView)view;
				tmpr.setText(textRepresentation);
			}else if(view.getClass().toString().equalsIgnoreCase("class android.widget.LinearLayout")){
				LinearLayout tmpr = (LinearLayout)view;
				tmpr.setTag(textRepresentation);
			}else{
				return false;
			}
			return true;
		}

	}
	
	

	class AvailableAppsListBinder implements ViewBinder		//TODO needs some improvements
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


	class InstalledAppsListBinder implements ViewBinder		//TODO needs some improvements
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


	class UpdatableAppsListBinder implements ViewBinder		//TODO needs some improvements
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
		AptoideLog.d(Aptoide.this, "switching available to progressBar");
		
        appsListFlipper.invalidate();
        appsListFlipper.removeViewAt(EnumAppsLists.Available.ordinal());
        appsListFlipper.addView(loadingAvailableAppsList, EnumAppsLists.Available.ordinal());
        
        if(currentAppsList.equals(EnumAppsLists.Available)){
        	showAvailableList();
        }
        
        switchUpdatableToProgressBar();
	}

	private void switchAvailableToList(){
        appsListFlipper.invalidate();
        appsListFlipper.removeViewAt(EnumAppsLists.Available.ordinal());
        appsListFlipper.addView(availableAppsListView, EnumAppsLists.Available.ordinal());
        
        if(updatableApps.getList().isEmpty()){
        	switchUpdatableToEmpty();
        }else{
        	switchUpdatableToList();
        }
	}
	
	private void switchAvailableToEmpty(){
		appsListFlipper.invalidate();
        appsListFlipper.removeViewAt(EnumAppsLists.Available.ordinal());
        appsListFlipper.addView(emptyAvailableAppsList, EnumAppsLists.Available.ordinal());
        
        switchUpdatableToEmpty();
	}

	private void switchInstalledToProgressBar(){
        appsListFlipper.invalidate();
        appsListFlipper.removeViewAt(EnumAppsLists.Installed.ordinal());
        appsListFlipper.addView(loadingInstalledAppsList, EnumAppsLists.Installed.ordinal());
        
        if(currentAppsList.equals(EnumAppsLists.Installed)){
        	showInstalledList();
        }
        
        switchUpdatableToProgressBar();
	}

	private void switchInstalledToList(){
        appsListFlipper.invalidate();
        appsListFlipper.removeViewAt(EnumAppsLists.Installed.ordinal());
        appsListFlipper.addView(installedAppsListView, EnumAppsLists.Installed.ordinal());
        
        if(updatableApps.getList().isEmpty()){
        	switchUpdatableToEmpty();
        }else{
        	switchUpdatableToList();
        }
	}
	
	private void switchInstalledToEmpty(){
		appsListFlipper.invalidate();
		appsListFlipper.removeViewAt(EnumAppsLists.Installed.ordinal());
        appsListFlipper.addView(emptyInstalledAppsList, EnumAppsLists.Installed.ordinal());
	}

	private void switchUpdatableToProgressBar(){
        appsListFlipper.invalidate();
        appsListFlipper.removeViewAt(EnumAppsLists.Updates.ordinal());
        appsListFlipper.addView(loadingUpdatableAppsList, EnumAppsLists.Updates.ordinal());
        
        if(currentAppsList.equals(EnumAppsLists.Updates)){
        	showUpdatableList();
        }
	}

	private void switchUpdatableToList(){
        appsListFlipper.invalidate();
        appsListFlipper.removeViewAt(EnumAppsLists.Updates.ordinal());
        appsListFlipper.addView(updatableAppsListView, EnumAppsLists.Updates.ordinal());
	}
	
	private void switchUpdatableToEmpty(){
		appsListFlipper.invalidate();
		appsListFlipper.removeViewAt(EnumAppsLists.Updates.ordinal());
        appsListFlipper.addView(emptyUpdatableAppsList, EnumAppsLists.Updates.ordinal());
	}
	
	
	
	private void showAvailableList(){
		switch (currentAppsList) {
			case Available:
				appsListFlipper.clearAnimation();
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
	
	private void showInstalledList(){
		switch (currentAppsList) {
			case Available:
				showNextList();
				break;
			
			case Updates:
				showPreviousList();
				break;
	
			case Installed:
				appsListFlipper.clearAnimation();
				appsListFlipper.showNext();
				appsListFlipper.showPrevious();
				break;
				
			default:
				break;
		}
	}
	
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
				appsListFlipper.clearAnimation();
				appsListFlipper.showPrevious();
				appsListFlipper.showNext();
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
    		Log.d("Aptoide","Scroll "+currentList+" discerning increase, display offset: "+currentDisplayOffset+" previousScrollRegion: "+previousScrollRegion+" offsetAdjustment: "+availableDisplayOffsetAdjustments.get()+" triggerMargin:  "+triggerMargin);
			
			if( (previousScrollRegion + availableDisplayOffsetAdjustments.get()) <  (( currentDisplayOffset / DISPLAY_LISTS_CACHE_SIZE) + 1)	
				&& (currentDisplayOffset % DISPLAY_LISTS_CACHE_SIZE) < (triggerMargin + Constants.DISPLAY_LISTS_PAGE_INCREASE_OFFSET_TRIGGER)					
				&& (currentDisplayOffset % DISPLAY_LISTS_CACHE_SIZE) > Constants.DISPLAY_LISTS_PAGE_INCREASE_OFFSET_TRIGGER ){
				
				scrollRegionOrigin.incrementAndGet();
				availableAppsManager.request(EnumAvailableRequestType.INCREASE);
				Log.d("Aptoide","Scroll "+currentList+" cache offset: "+availableAppsManager.getCacheOffset()+" requestFifo size: "+availableAppsManager.getRequestFifo().size());
				
			}
    	}
    	
    	private void detectAvailableAppsCacheDecrease(int triggerMargin, int currentDisplayOffset, int previousScrollRegion, EnumAppsLists currentList){
    		Log.d("Aptoide","Scroll "+currentList+" discerning decrease, display offset: "+currentDisplayOffset+" previousScrollRegion: "+previousScrollRegion+" offsetAdjustment: "+availableDisplayOffsetAdjustments.get()+" triggerMargin:  "+triggerMargin);
			
			if( (previousScrollRegion + availableDisplayOffsetAdjustments.get()) >  (( currentDisplayOffset / DISPLAY_LISTS_CACHE_SIZE) + 1)
				&& (currentDisplayOffset % DISPLAY_LISTS_CACHE_SIZE) > (Constants.DISPLAY_LISTS_PAGE_DECREASE_OFFSET_TRIGGER - triggerMargin)
				&& (currentDisplayOffset % DISPLAY_LISTS_CACHE_SIZE) < Constants.DISPLAY_LISTS_PAGE_DECREASE_OFFSET_TRIGGER ){
				
				scrollRegionOrigin.decrementAndGet();
				availableAppsManager.request(EnumAvailableRequestType.DECREASE);
				Log.d("Aptoide","Scroll "+currentList+" cache offset: "+availableAppsManager.getCacheOffset()+" requestFifo size: "+availableAppsManager.getRequestFifo().size());
				
			}
    	}
    	
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			if(!swyping.get()){
				if(adjustAvailableDisplayOffset.getAndSet(0) != 0){
					this.displayOffset.addAndGet(adjustAvailableDisplayOffset.get());
				}
				this.firstVisibleItem.set(firstVisibleItem);
				this.visibleItemCount.set(visibleItemCount);
//				Log.d("Aptoide","Scroll currentList: "+currentAppsList+" initialfirstVisibleItem: "+initialFirstVisibleItem+" firstVisibleItem: "+firstVisibleItem);
				this.differential.set(Math.abs(firstVisibleItem-this.initialFirstVisibleItem.get()));
				
				if(this.initialFirstVisibleItem.get()+visibleItemCount < firstVisibleItem){
					this.initialFirstVisibleItem.set(firstVisibleItem);
					Log.d("Aptoide","New Scroll down page");
					switch (currentAppsList) {
						case Available:

							int triggerMargin = this.visibleItemCount.get()*4;
							int currentDisplayOffset = this.displayOffset.addAndGet(this.differential.get());
							int previousScrollRegion = scrollRegionOrigin.get();
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

							int triggerMargin = this.visibleItemCount.get()*4;
							int currentDisplayOffset = this.displayOffset.addAndGet(-this.differential.get());
							int previousScrollRegion = scrollRegionOrigin.get();
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

	    		AptoideLog.d(Aptoide.this, "visibleItems: "+visibleItemCount.get());
				
				if((firstVisibleItem.get()-initialFirstVisibleItem.get())>0){
					switch (currentAppsList) {
						case Available:

							int triggerMargin = this.visibleItemCount.get()*2;
							int currentDisplayOffset = this.displayOffset.addAndGet(this.differential.get());
							int previousScrollRegion = scrollRegionOrigin.get();
							detectAvailableAppsCacheIncrease(triggerMargin, currentDisplayOffset, previousScrollRegion, currentAppsList);
							break;
		
						default:
							break;
					}
				}else{
					switch (currentAppsList) {
						case Available:

							int triggerMargin = this.visibleItemCount.get()*2;
							int currentDisplayOffset = this.displayOffset.addAndGet(-this.differential.get());
							int previousScrollRegion = scrollRegionOrigin.get();
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
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long positionLong) {
    	if(!swyping.get()){
    		if(availableByCategory){
    			if(category.hasChildren()){
    				final int categoryHashid = Integer.parseInt(((LinearLayout)view).getTag().toString());
    				category = category.getSubCategory(categoryHashid);
    				AptoideLog.d(this, "Onclick position: "+position+" categoryHashid: "+categoryHashid+" category: "+category);
    				if(category.hasChildren()){
    					initDisplayCategories();
    				}else{
    					availableAppsManager.request(EnumAvailableRequestType.RESET_TO_ZERO);
    				}
    			}else{
    	    		AptoideLog.d(this, "row height: "+view.getHeight());
    				final int appHashid = Integer.parseInt(((LinearLayout)view).getTag().toString());
    	    		AptoideLog.d(this, "Onclick position: "+position+" appHashid: "+appHashid);
    	    		Intent appInfo = new Intent(this,AppInfo.class);
    	    		appInfo.putExtra("appHashid", appHashid);
    	    		startActivity(appInfo);
    			}
    		}else{
	    		final int appHashid = Integer.parseInt(((LinearLayout)view).getTag().toString());
	    		AptoideLog.d(this, "Onclick position: "+position+" appHashid: "+appHashid);
	    		Intent appInfo = new Intent(this,AppInfo.class);
	    		appInfo.putExtra("appHashid", appHashid);
	    		startActivity(appInfo);
	    		//TODO click change color effect
    		}
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && availableByCategory && category != null && category.getCategoryHashid() != Constants.TOP_CATEGORY) {
			AptoideLog.d(this, "click back, new category: "+category.getParentCategory().getCategoryHashid());
			category = category.getParentCategory();
			initDisplayCategories();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
    

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		super.onCreateOptionsMenu(menu);
		switch (currentAppsList) {
		case Available:
			menu.add(Menu.NONE, EnumOptionsMenu.MANAGE_REPO.ordinal(), EnumOptionsMenu.MANAGE_REPO.ordinal(), R.string.manage_repos)
				.setIcon(android.R.drawable.ic_menu_agenda);
			menu.add(Menu.NONE, EnumOptionsMenu.DISPLAY_OPTIONS.ordinal(), EnumOptionsMenu.DISPLAY_OPTIONS.ordinal(), R.string.display_options)
				.setIcon(android.R.drawable.ic_menu_sort_by_size);
//			menu.add(Menu.NONE, EnumOptionsMenu.SEARCH_MENU.ordinal(),EnumOptionsMenu.SEARCH_MENU.ordinal(),R.string.menu_search)
//				.setIcon(android.R.drawable.ic_menu_search);
//			menu.add(Menu.NONE, EnumOptionsMenu.SETTINGS.ordinal(), EnumOptionsMenu.SETTINGS.ordinal(), R.string.menu_settings)
//				.setIcon(android.R.drawable.ic_menu_preferences);
//			menu.add(Menu.NONE, EnumOptionsMenu.ABOUT.ordinal(),EnumOptionsMenu.ABOUT.ordinal(),R.string.menu_about)
//				.setIcon(android.R.drawable.ic_menu_help);
//			menu.add(Menu.NONE,EnumOptionsMenu.SCHEDULED_DOWNLOADS.ordinal(),EnumOptionsMenu.SCHEDULED_DOWNLOADS.ordinal(),R.string.schDwnBtn).setIcon(R.drawable.ic_menu_scheduled);
			break;

		default:
			break;
		}
		return true;
	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		EnumOptionsMenu menuEntry = EnumOptionsMenu.reverseOrdinal(item.getItemId());
		Log.d("Aptoide-OptionsMenu", "menuOption: "+menuEntry+" itemid: "+item.getItemId());
		switch (menuEntry) {
			case MANAGE_REPO:
				Intent manageRepo = new Intent(this, ManageRepos.class);
				startActivity(manageRepo);
				return true;
			case DISPLAY_OPTIONS:
					//TODO refactor extract dialog management class
					LayoutInflater li = LayoutInflater.from(this);
					View view = li.inflate(R.layout.dialog_display_options, null);
					Builder alrt = new AlertDialog.Builder(this).setView(view);
					final AlertDialog sortDialog = alrt.create();
					sortDialog.setIcon(android.R.drawable.ic_menu_sort_by_size);
					sortDialog.setTitle(getString(R.string.display_options));
					
					// ***********************************************************
					// Categories
					final RadioButton byCategory = (RadioButton) view.findViewById(R.id.shw_ct);
					final RadioButton byAll = (RadioButton) view.findViewById(R.id.shw_all);
					if(availableByCategory){
						byCategory.setChecked(true);
					}else{
						byAll.setChecked(true);
					}
					final RadioGroup grp2 = (RadioGroup) view.findViewById(R.id.groupshow);
					grp2.setOnCheckedChangeListener(new OnCheckedChangeListener() {
						public void onCheckedChanged(RadioGroup group, int checkedId) {
//							if(checkedId == byCategory.getId()){
//								pop_change = true;
//								prefEdit.putBoolean("mode", true);
//							}else{
//								pop_change = true;
//								prefEdit.putBoolean("mode", false);
//							}
							
						}
					});

					// ***********************************************************
					
					// ***********************************************************
					// Order
//					final RadioButton ord_rct = (RadioButton) view.findViewById(R.id.org_rct);
//					final RadioButton ord_abc = (RadioButton) view.findViewById(R.id.org_abc);
//					final RadioButton ord_rat = (RadioButton) view.findViewById(R.id.org_rat);
//					final RadioButton ord_dwn = (RadioButton) view.findViewById(R.id.org_dwn);
//					
//					if(order_lst.equals("abc"))
//						ord_abc.setChecked(true);
//					else if(order_lst.equals("rct"))
//						ord_rct.setChecked(true);
//					else if(order_lst.equals("rat"))
//						ord_rat.setChecked(true);
//					else if(order_lst.equals("dwn"))
//						ord_dwn.setChecked(true);
//					
//					final RadioGroup grp1 = (RadioGroup) view.findViewById(R.id.groupbtn);
//					grp1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//						public void onCheckedChanged(RadioGroup group, int checkedId) {
//							if(checkedId == ord_rct.getId()){
//								pop_change = true;
//								order_lst = "rct";
//							}else if(checkedId == ord_abc.getId()){
//								pop_change = true;
//								order_lst = "abc";
//							}else if(checkedId == ord_rat.getId()){
//								pop_change = true;
//								order_lst = "rat";
//							}else if(checkedId == ord_dwn.getId()){
//								pop_change = true;
//								order_lst = "dwn";
//							}
//						}
//					});
					
					// ***********************************************************

					
					sortDialog.setButton(getString(R.string.done), new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							if(byCategory.isChecked() != availableByCategory){
								availableByCategory = byCategory.isChecked();
								setAvailableListBy(availableByCategory);
								availableAppsManager.request(EnumAvailableRequestType.RESET_TO_ZERO);
							}
							sortDialog.dismiss();
						}
					});
					
				sortDialog.show();
//				
//				new Thread(){
//					@Override
//					public void run() {
//						super.run();
//						while(p.isShowing()){
//							try {
//								Thread.sleep(1000);
//							} catch (InterruptedException e) {	}
//						}
//						if(sPref.getBoolean("pop_changes", false)){
//							prefEdit.remove("pop_changes");
//							prefEdit.commit();
//							if(sPref.getBoolean("mode", false)){
//								if(!(shown_now == null) || main_shown_now == 2){
//									handler_adpt = getGivenCatg(shown_now, main_shown_now);
//								}else{
//									handler_adpt = getRootCtg();
//								}
//								displayRefresh.sendEmptyMessage(0);
//							}else{
//								shown_now = null;
//								handler_adpt = null;
//								redrawHandler.sendEmptyMessage(0);
//								try {
//									Thread.sleep(1000);
//								} catch (InterruptedException e1) { }
//								while(sPref.getBoolean("redrawis", false)){
//									try {
//										Thread.sleep(500);
//									} catch (InterruptedException e) { }
//								}
//								displayRefresh.sendEmptyMessage(0);
//							}
//						}
//					}
//				}.start();
				return true;
				
//			case SEARCH_MENU:
//				onSearchRequested();
//				return true;
//			case ABOUT:
//				LayoutInflater li = LayoutInflater.from(this);
//				View view = li.inflate(R.layout.about, null);
//				TextView info = (TextView)view.findViewById(R.id.about11);
//				info.setText(mctx.getString(R.string.about_txt11, mctx.getString(R.string.ver_str)));
//				Builder p = new AlertDialog.Builder(this).setView(view);
//				final AlertDialog alrt = p.create();
//				alrt.setIcon(R.drawable.icon);
//				alrt.setTitle(R.string.aptoide);
//				alrt.setButton(getText(R.string.btn_chlog), new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int	whichButton) {
//						Uri uri = Uri.parse(getString(R.string.change_log_url));
//						startActivity(new Intent( Intent.ACTION_VIEW, uri));
//					}
//				});
//				alrt.show();
//				return true;
//			case SETTINGS:
//				Intent s = new Intent(RemoteInTab.this, Settings.class);
//				startActivityForResult(s,SETTINGS_FLAG);
//				return true;	
//			case SCHEDULED_DOWNLOADS:
//				Intent sch_download = new Intent(RemoteInTab.this,ScheduledDownload.class);
//				startActivity(sch_download);
//				return true;
		}
		return super.onOptionsItemSelected(item);
	}


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
