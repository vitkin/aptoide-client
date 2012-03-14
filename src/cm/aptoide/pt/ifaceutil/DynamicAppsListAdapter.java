/**
 * DynamicAppsListAdapter,		part of Aptoide's data model
 * Copyright (C) 2011  Duarte Silveira
 * duarte.silveira@caixamagica.pt
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

package cm.aptoide.pt.ifaceutil;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import cm.aptoide.pt.Aptoide;
import cm.aptoide.pt.EnumAppsLists;
import cm.aptoide.pt.EnumAptoideInterfaceTasks;
import cm.aptoide.pt.R;
import cm.aptoide.pt.data.AIDLAptoideServiceData;
import cm.aptoide.pt.data.AptoideServiceData;
import cm.aptoide.pt.data.display.ViewDisplayApplication;
import cm.aptoide.pt.data.display.ViewDisplayApplicationAvailable;
import cm.aptoide.pt.data.display.ViewDisplayCategory;
import cm.aptoide.pt.data.display.ViewDisplayListApps;
import cm.aptoide.pt.data.display.ViewDisplayListsDimensions;
import cm.aptoide.pt.debug.AptoideLog;

 /**
 * DynamicAppsListAdapter, models a dynamic loading apps list adapter
 * 							extends arrayAdapter
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class DynamicAppsListAdapter extends ArrayAdapter<ViewDisplayApplication>{	//TODO try using BaseAdapter instead
	
	private ListView listView;
	private LayoutInflater layoutInflater;
	
	private ViewDisplayListApps freshAvailableApps = null;
	private int availableAppsTrimAmount = 0;

	private AtomicInteger cacheListOffset;
	
	private AtomicInteger originalScrollPostition;
	private AtomicInteger originalPartialScrollPostition;	
	private AtomicInteger adjustAvailableDisplayOffset;
	private AtomicInteger availableDisplayOffsetAdjustments;
	
	private ViewDisplayListsDimensions displayListsDimensions;

	private ViewDisplayCategory category = null;
	
	private ViewDisplayListApps availableApps = null;

	private AvailableAppsManager availableAppsManager;
	
	private AIDLAptoideServiceData serviceDataCaller = null;

	private AtomicInteger scrollDirectionReference;
	        

	
	private Handler interfaceTasksHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	EnumAptoideInterfaceTasks task = EnumAptoideInterfaceTasks.reverseOrdinal(msg.what);
        	switch (task) {
				
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
	
				default:
					break;
			}
        }
    };
    
    

    
    
   
    
    private class AvailableAppsManager{
    	
    	private ExecutorService dataColector;


    	public AvailableAppsManager() {
    		dataColector = Executors.newSingleThreadExecutor();
    	}
		

		/** 
		 * scrollDown - MOVE FORWARD IN AVAILABLE APPS LIST
		 * 
		 */
		public void scrollDown(){
			dataColector.execute(new ScrollDown());
		}

		/** 
		 * scrollUp - MOVE BACKWARD IN AVAILABLE APPS LIST
		 * 
		 */
		public void scrollUp(){
			dataColector.execute(new ScrollUp());
		}

		/** 
		 * reload - RELOAD AVAILABLE APPS
		 * 
		 */
		public void reload(){
			dataColector.execute(new Reload());
		}

		/** 
		 * reset - RESET AVAILABLE APPS TO ZERO
		 * 
		 */
		public void reset(){
			dataColector.execute(new Reset());
		}
    	
		private class ScrollDown implements Runnable {

			@Override
			public void run() {
				int previousCacheOffset = cacheListOffset.getAndDecrement();
				int currentCacheOffset = cacheListOffset.get();
				
				int offset;
				int range;
				
				if(previousCacheOffset == -1){
//					offset = displayListsDimensions.getFastReset();
//					range = displayListsDimensions.getPageSize()-offset;
					previousCacheOffset = cacheListOffset.getAndIncrement();
					offset = displayListsDimensions.getFastReset();
					range = (displayListsDimensions.getPageSize()-offset)+displayListsDimensions.getPageSize();
				}else{
					offset = (previousCacheOffset+1)*displayListsDimensions.getPageSize();
					range = displayListsDimensions.getPageSize();
				}

				try {
					if( category != null && !category.hasChildren()){
						Log.d("Aptoide","advancing available list forward.  offset: "+offset+" range: "+range+" category: "+category);
						setFreshAvailableApps(serviceDataCaller.callGetAvailableAppsByCategory(offset, range, category.getCategoryHashid()));
					}else{
						Log.d("Aptoide","advancing available list forward.  offset: "+offset+" range: "+range);
						setFreshAvailableApps(serviceDataCaller.callGetAvailableApps(offset, range));
					}
					
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if(availableApps.size() >= (displayListsDimensions.getCacheSize())){
					availableAppsTrimAmount = range;
					interfaceTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.TRIM_APPEND_AND_UPDATE_AVAILABLE_LIST_DISPLAY.ordinal());
				}else{
					interfaceTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.APPEND_AND_UPDATE_AVAILABLE_LIST_DISPLAY.ordinal());
				}
				
			}
			
		}
    	
		private class ScrollUp implements Runnable {

			@Override
			public void run() {
				int previousCacheOffset = cacheListOffset.getAndIncrement();
				int currentCacheOffset = cacheListOffset.get();

				int offset;
				int range;
				
				offset = (currentCacheOffset-1)*displayListsDimensions.getPageSize();
				range = displayListsDimensions.getPageSize();
				
				try {
					
					if(category != null && !category.hasChildren()){
						Log.d("Aptoide","advancing available list backward.  offset: "+offset+" range: "+range+" category: "+category);
						setFreshAvailableApps(serviceDataCaller.callGetAvailableAppsByCategory(offset, range, category.getCategoryHashid()));
					}else{
						Log.d("Aptoide","advancing available list backward.  offset: "+offset+" range: "+range);
						setFreshAvailableApps(serviceDataCaller.callGetAvailableApps(offset, range));
					}	
					
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}					

				if(availableApps.size() >= (displayListsDimensions.getCacheSize())){
					availableAppsTrimAmount = range;
					interfaceTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.TRIM_PREPEND_AND_UPDATE_AVAILABLE_LIST_DISPLAY.ordinal());
				}else{
					interfaceTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.PREPEND_AND_UPDATE_AVAILABLE_LIST_DISPLAY.ordinal());							
				}
				
			}
			
		}
    	
		private class Reload implements Runnable {
			@Override
			public void run() {
				int offset;
				int range;
				
				if(cacheListOffset.get() == -1){
					offset = 0;
					range = displayListsDimensions.getFastReset();
				}else{
					offset = cacheListOffset.get()*displayListsDimensions.getPageSize();
					range = displayListsDimensions.getPageSize();
				}
				
				try {
					Log.d("Aptoide","resetting available list.  offset: "+offset+" range: "+range);
					setFreshAvailableApps(serviceDataCaller.callGetAvailableAppsByCategory(offset, range, category.getCategoryHashid()));
					interfaceTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.RESET_AVAILABLE_LIST_DISPLAY.ordinal());
					
//					updatableAppsManager.resetUpdatableApps();
					
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
    	
		private class Reset implements Runnable {

			@Override
			public void run() {
				cacheListOffset.set(-1);
				int offset = 0;
				int range = displayListsDimensions.getFastReset();
				try {
					if(category != null && !category.hasChildren()){
						Log.d("Aptoide","resetting available list.  offset: "+offset+" range: "+range+" "+category);
						setFreshAvailableApps(serviceDataCaller.callGetAvailableAppsByCategory(offset, range, category.getCategoryHashid()));
						interfaceTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.RESET_AVAILABLE_LIST_DISPLAY.ordinal());
//						availableAppsManager.request(EnumAvailableRequestType.INCREASE);
					}else{
						Log.d("Aptoide","resetting available list.  offset: "+offset+" range: "+range);
						setFreshAvailableApps(serviceDataCaller.callGetAvailableApps(offset, range));
						interfaceTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.RESET_AVAILABLE_LIST_DISPLAY.ordinal());
//						availableAppsManager.request(EnumAvailableRequestType.INCREASE);
					}
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}

    }
    
    
	private void detectMoreAppsNeeded(int position){
		if((position - scrollDirectionReference.get()) > 0 && position > displayListsDimensions.getIncreaseTrigger() &&){
			scrollDirectionReference.set(position);
		}
	}
	
	
	
	public static class AvailableRowViewHolder{
		int appHashid;
		
		ImageView app_icon;
		
		TextView app_name;
		TextView uptodate_versionname;
		
		TextView downloads;
		RatingBar stars;
	}
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		detectMoreAppsNeeded(position);
		
		AvailableRowViewHolder rowViewHolder;
		
		if(convertView == null){
			convertView = layoutInflater.inflate(R.layout.row_app_available, null);
			
			rowViewHolder = new AvailableRowViewHolder();
			rowViewHolder.app_icon = (ImageView) convertView.findViewById(R.id.app_icon);
			rowViewHolder.app_name = (TextView) convertView.findViewById(R.id.app_name);
			rowViewHolder.uptodate_versionname = (TextView) convertView.findViewById(R.id.uptodate_versionname);
			rowViewHolder.downloads = (TextView) convertView.findViewById(R.id.downloads);
			rowViewHolder.stars = (RatingBar) convertView.findViewById(R.id.stars);
			
			convertView.setTag(rowViewHolder);
		}else{
			rowViewHolder = (AvailableRowViewHolder) convertView.getTag();
		}
		
		
		File iconCache = new File(availableApps.get(position).getIconCachePath());
		if(iconCache.exists() && iconCache.length() > 0){
			rowViewHolder.app_icon.setImageURI(Uri.parse(availableApps.get(position).getIconCachePath()));
		}else{
			rowViewHolder.app_icon.setImageResource(android.R.drawable.sym_def_app_icon);
		}
		
		rowViewHolder.app_name.setText(availableApps.get(position).getAppName());
		rowViewHolder.uptodate_versionname.setText(availableApps.get(position).getVersionName());
		
		rowViewHolder.downloads.setText(((ViewDisplayApplicationAvailable) availableApps.get(position)).getDownloads());
		rowViewHolder.stars.setRating(((ViewDisplayApplicationAvailable) availableApps.get(position)).getStars());
		
		return convertView;
	}
	

//    class ScrollDetector implements OnScrollListener{
//
//    	AtomicInteger initialFirstVisibleItem = new AtomicInteger(0);
//    	AtomicInteger firstVisibleItem = new AtomicInteger(0);
//    	AtomicInteger visibleItemCount = new AtomicInteger(0);
//    	AtomicInteger differential = new AtomicInteger(0);
//    	AtomicInteger displayOffset = new AtomicInteger(0);
//		AtomicInteger scrollRegionOrigin = new AtomicInteger(0);
//		AtomicBoolean readyForIncrease = new AtomicBoolean(false);
//		AtomicBoolean readyForDecrease = new AtomicBoolean(false);
//		AtomicBoolean detectingListChange = new AtomicBoolean(false);
//    	
//    	private void detectAvailableAppsCacheIncrease(int currentDisplayOffset, int previousScrollRegion, EnumAppsLists currentList){
//    		Log.d("Aptoide","Scroll "+currentList+" discerning increase, display offset: "+currentDisplayOffset+" previousScrollRegion: "+previousScrollRegion+" offsetAdjustment: "+availableDisplayOffsetAdjustments.get()+" triggerMargin:  "+displayListsDimensions.getTriggerMargin());
//
//    			// this ensures that the scroll hasn't started on the same cache region that it stopped
//			if( (previousScrollRegion <  (( currentDisplayOffset / displayListsDimensions.getPageSize()) + 1) || readyForIncrease.get())	
//				// these ensure that we're past the trigger and inside the triggerMargin
//				&& (currentDisplayOffset % displayListsDimensions.getPageSize()) < (displayListsDimensions.getTriggerMargin() + displayListsDimensions.getIncreaseTrigger())					
//				&& (currentDisplayOffset % displayListsDimensions.getPageSize()) >  displayListsDimensions.getIncreaseTrigger()){
//				
//				if(!readyForIncrease.get()){
//					scrollRegionOrigin.incrementAndGet();
//				}
//				readyForIncrease.set(false);
//				detectingListChange.set(false);
//				availableAppsManager.request(EnumAvailableRequestType.INCREASE);
//				Log.d("Aptoide","Scroll "+currentList+" cache offset: "+availableAppsManager.getCacheOffset()+" requestFifo size: "+availableAppsManager.getRequestFifo().size());
//				
//			}else{
//					// If we're not before the first increase trigger
//				if(!( previousScrollRegion == 0 && (previousScrollRegion == ( currentDisplayOffset / displayListsDimensions.getPageSize())) )
//					// but we're in a higher cache region than were we started the scroll 
//					&& (previousScrollRegion <  (( currentDisplayOffset / displayListsDimensions.getPageSize()) + 1))){
//					scrollRegionOrigin.incrementAndGet();
//					readyForIncrease.set(true);
//				}
//				detectingListChange.set(false);				
//			}
//    	}
//    	
//    	private void detectAvailableAppsCacheDecrease(int currentDisplayOffset, int previousScrollRegion, EnumAppsLists currentList){
//    		Log.d("Aptoide","Scroll "+currentList+" discerning decrease, display offset: "+currentDisplayOffset+" previousScrollRegion: "+previousScrollRegion+" offsetAdjustment: "+availableDisplayOffsetAdjustments.get()+" triggerMargin:  "+displayListsDimensions.getTriggerMargin());
//
//    			// this ensures that we only get new top apps if they're not already cached
//			if( ((previousScrollRegion > 2 ) || previousScrollRegion == 2 && readyForDecrease.get())
//				// this ensures that the scroll hasn't started on the same cache region that it stopped	
//				&& (previousScrollRegion >  (( currentDisplayOffset / displayListsDimensions.getPageSize()) + 1) || readyForDecrease.get())
//				// these ensure that we're past the trigger and inside the triggerMargin
//				&& (currentDisplayOffset % displayListsDimensions.getPageSize()) > (displayListsDimensions.getDecreaseTrigger() - displayListsDimensions.getTriggerMargin())
//				&& (currentDisplayOffset % displayListsDimensions.getPageSize()) <  displayListsDimensions.getDecreaseTrigger()){
//
//				if(!readyForDecrease.get()){
//					scrollRegionOrigin.decrementAndGet();
//				}
//				readyForDecrease.set(false);
//				detectingListChange.set(false);
//				availableAppsManager.request(EnumAvailableRequestType.DECREASE);
//				Log.d("Aptoide","Scroll "+currentList+" cache offset: "+availableAppsManager.getCacheOffset()+" requestFifo size: "+availableAppsManager.getRequestFifo().size());
//				
//			}else{
//					// if we're in a higher cache region than were we started the scroll 
//				if(previousScrollRegion  >  (( currentDisplayOffset / displayListsDimensions.getPageSize()) + 1)){
//					scrollRegionOrigin.decrementAndGet();
//					readyForDecrease.set(true);
//				}
//				detectingListChange.set(false);				
//			}
//    	}
//    	
//		@Override
//		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//			if(!swyping.get()){
//				int offsetAdjustment = adjustAvailableDisplayOffset.getAndSet(0); 
//				if(offsetAdjustment != 0){
//					this.displayOffset.addAndGet(offsetAdjustment);
//				}
//				this.firstVisibleItem.set(firstVisibleItem);
//				this.visibleItemCount.set(visibleItemCount);
////				Log.d("Aptoide","Scroll currentList: "+currentAppsList+" initialfirstVisibleItem: "+initialFirstVisibleItem+" firstVisibleItem: "+firstVisibleItem);
//				this.differential.set(Math.abs(firstVisibleItem-this.initialFirstVisibleItem.get()));
//				
//				if(this.initialFirstVisibleItem.get()+visibleItemCount < firstVisibleItem){
//					this.initialFirstVisibleItem.set(firstVisibleItem);
//					Log.d("Aptoide","New Scroll down page, offset: "+firstVisibleItem+" range: "+visibleItemCount+" readyForIncrease: "+readyForIncrease.get());
//					switch (currentAppsList) {
//						case Available:
//
//							int currentDisplayOffset = this.displayOffset.addAndGet(this.differential.get());
//							int previousScrollRegion = scrollRegionOrigin.get();
//							if(!detectingListChange.get()){
//								detectingListChange.set(true);
//								detectAvailableAppsCacheIncrease(currentDisplayOffset, previousScrollRegion, currentAppsList);
//							}
//							break;
//		
//						default:
//							break;
//					}
//				}else if(this.initialFirstVisibleItem.get()-visibleItemCount > firstVisibleItem){
//					this.initialFirstVisibleItem.set(firstVisibleItem);
//					Log.d("Aptoide","New Scroll up page, offset: "+firstVisibleItem+" range: "+visibleItemCount+" readyForDecrease: "+readyForDecrease.get());
//					switch (currentAppsList) {
//						case Available:														
//
//							int currentDisplayOffset = this.displayOffset.addAndGet(-this.differential.get());
//							int previousScrollRegion = scrollRegionOrigin.get();
//							if(!detectingListChange.get()){
//								detectingListChange.set(true);
//								detectAvailableAppsCacheDecrease(currentDisplayOffset, previousScrollRegion, currentAppsList);
//							}
//							break;
//		
//						default:
//							break;
//					}
//				}
//			}
//		}
//
//		@Override
//		public void onScrollStateChanged(AbsListView view, int scrollState) {
//			if( !swyping.get() && scrollState == SCROLL_STATE_IDLE){
////				Log.d("Aptoide","Scroll currentList: "+currentAppsList+" initialFirstVisibleItem: "+initialFirstVisibleItem+" firstVisibleItem: "+firstVisibleItem);
//				this.differential.set(Math.abs(firstVisibleItem.get()-this.initialFirstVisibleItem.get()));
//
//	    		AptoideLog.d(Aptoide.this, "visibleItems: "+visibleItemCount.get());
//				
//				if((firstVisibleItem.get()-initialFirstVisibleItem.get())>0){
//					switch (currentAppsList) {
//						case Available:
//
//							Log.d("Aptoide","Scroll down, offset: "+firstVisibleItem+" range: "+visibleItemCount+" readyForIncrease: "+readyForIncrease.get());
//							int currentDisplayOffset = this.displayOffset.addAndGet(this.differential.get());
//							int previousScrollRegion = scrollRegionOrigin.get();
//							if(!detectingListChange.get()){
//								detectingListChange.set(true);
//								detectAvailableAppsCacheIncrease(currentDisplayOffset, previousScrollRegion, currentAppsList);
//							}
//							break;
//		
//						default:
//							break;
//					}
//				}else{
//					switch (currentAppsList) {
//						case Available:
//
//							Log.d("Aptoide","Scroll up, offset: "+firstVisibleItem+" range: "+visibleItemCount+" readyForDecrease: "+readyForDecrease.get());
//							int currentDisplayOffset = this.displayOffset.addAndGet(-this.differential.get());
//							int previousScrollRegion = scrollRegionOrigin.get();
//							if(!detectingListChange.get()){
//								detectingListChange.set(true);
//								detectAvailableAppsCacheDecrease(currentDisplayOffset, previousScrollRegion, currentAppsList);
//							}
//							break;
//		
//						default:
//							break;
//					}
//				}
//				initialFirstVisibleItem.set(firstVisibleItem.get());
//			}
//		}
//    	
//    }
	
	
	
	
	/**
	 * DynamicAppsListAdapter Constructor
	 *
	 * @param context
	 * @param textViewResourceId
	 */
	public DynamicAppsListAdapter(Context context, ListView listView, AIDLAptoideServiceData serviceDataCaller) {
		super(context, R.layout.row_app_available);
		
		this.serviceDataCaller = serviceDataCaller;
		
		availableApps = new ViewDisplayListApps();

		availableAppsManager = new AvailableAppsManager();


		this.listView = listView;
		layoutInflater = LayoutInflater.from(context);
		
		cacheListOffset = new AtomicInteger(0);
		
		originalScrollPostition = new AtomicInteger(0);
		originalPartialScrollPostition = new AtomicInteger(0);
		adjustAvailableDisplayOffset = new AtomicInteger(0);
		availableDisplayOffsetAdjustments = new AtomicInteger(0);
		
		scrollDirectionReference = new AtomicInteger(0);

        try {

            displayListsDimensions = serviceDataCaller.callGetDisplayListsDimensions();
        } catch (RemoteException e) {
			// TODO Auto-generated catch block
            e.printStackTrace();
        }
	} 
	
	
	
	public void setCategory(ViewDisplayCategory category){
		this.category = category;
	}
	
	public void noCategory(){
		this.category = null;
	}

    

	public void initDisplayAvailable(){
		listView.setAdapter(this);
    }
	
	public synchronized void setFreshAvailableApps(ViewDisplayListApps freshAvailableApps){
		AptoideLog.d(Aptoide.this, "setFreshAvailableList");
		this.freshAvailableApps = freshAvailableApps;
	}
	
	public synchronized void trimBeginningAvailableAppsList(int trimAmount){
		int adjustAmount = trimAmount;
		AptoideLog.d(Aptoide.this, "trimBeginningAvailableList: "+trimAmount);
		originalScrollPostition.set(availableAppsListView.getFirstVisiblePosition());
		originalPartialScrollPostition.set(availableAppsListView.getChildAt(0)==null?0:availableAppsListView.getChildAt(0).getTop());
		AptoideLog.d(this, "list size before: "+availableApps.size()+"   original scroll position: "+originalScrollPostition.get());
		availableDisplayOffsetAdjustments.decrementAndGet();
		do{
			this.availableApps.removeFirst();
			trimAmount--;
		}while(trimAmount>0);
		adjustAvailableDisplayOffset.set(adjustAmount);
		AptoideLog.d(this, "list size after: "+availableApps.size());
	}
	
	public synchronized void trimEndAvailableAppsList(int trimAmount){
		AptoideLog.d(Aptoide.this, "trimEndAvailableList: "+trimAmount);
		availableDisplayOffsetAdjustments.incrementAndGet();
		do{
			this.availableApps.removeLast();
			trimAmount--;
		}while(trimAmount>0);
		AptoideLog.d(this, "list size after: "+availableApps.size());
	}
	
	public void resetDisplayAvailable(){
		if(freshAvailableApps.size()==0){
			switchAvailableToEmpty();
		}else{
			switchAvailableToList();
		}
		
		// fixes viewFlipper non-refresh bug
		if(currentAppsList.equals(EnumAppsLists.Available)){
			showAvailableList();			
		}
		
    	AptoideLog.d(Aptoide.this, "new AvailableList: "+freshAvailableApps.size());
    	originalScrollPostition.set(availableAppsListView.getFirstVisiblePosition());
		originalPartialScrollPostition.set(availableAppsListView.getChildAt(0)==null?0:availableAppsListView.getChildAt(0).getTop());
		boolean newList = this.availableApps.isEmpty();
    	this.availableApps = freshAvailableApps;
    	initDisplayAvailable();
    	if(!newList){
    		refreshAvailableDisplay();
    	}
    	availableAppsListView.setSelectionFromTop((originalScrollPostition.get()+adjustAvailableDisplayOffset.get()), originalPartialScrollPostition.get());
    	updatableAppsManager.resetUpdatableApps();
	}
	
	public void appendAndUpdateDisplayAvailable(ViewDisplayListApps freshAvailableApps){	
    	AptoideLog.d(Aptoide.this, "appending freshAvailableList: "+freshAvailableApps.size());
		boolean newList = this.availableApps.isEmpty();
    	if(newList){
    		this.availableApps = freshAvailableApps;
    		initDisplayAvailable();
    	}else{	
    		AptoideLog.d(this, "available list not empty");
    		this.availableApps.addAll(freshAvailableApps);
    		AptoideLog.d(Aptoide.this, "new displayList size: "+this.availableApps.size());
    		refreshAvailableDisplay();
    	}
    	//After trimming the top, adjust the display offset
    	if(adjustAvailableDisplayOffset.get() != 0){
			AptoideLog.d(this, "restoring scroll position, currentPosition:"+availableAppsListView.getFirstVisiblePosition()+" firstVisiblePosition: "+(originalScrollPostition.get()+adjustAvailableDisplayOffset.get())+" top: "+originalPartialScrollPostition.get());
	    	availableAppsListView.setSelectionFromTop((originalScrollPostition.get()-adjustAvailableDisplayOffset.get()), originalPartialScrollPostition.get());
    	}
	}
	
	public void prependAndUpdateDisplayAvailable(ViewDisplayListApps freshAvailableApps){	
    	AptoideLog.d(Aptoide.this, "prepending freshAvailableList: "+freshAvailableApps.size());
    	int adjustAmount = freshAvailableApps.size();
    	boolean newList = this.availableApps.isEmpty();
    	if(newList){
    		this.availableApps = freshAvailableApps;
    		initDisplayAvailable();
    	}else{	
    		int scrollRestorePosition = availableAppsListView.getFirstVisiblePosition();
    		int partialScrollRestorePosition = (availableAppsListView.getChildAt(0)==null?0:availableAppsListView.getChildAt(0).getTop());
    		AptoideLog.d(this, "available list not empty");
    		this.availableApps.addAll(0,freshAvailableApps);
    		AptoideLog.d(Aptoide.this, "new displayList size: "+this.availableApps.size());
    		refreshAvailableDisplay();

    		adjustAvailableDisplayOffset.set(-adjustAmount);
//    		if(availableDisplayOffsetAdjustments.get()!=0){
//    			availableDisplayOffsetAdjustments.incrementAndGet();
//    		}
        	availableAppsListView.setSelectionFromTop(scrollRestorePosition+freshAvailableApps.size(), partialScrollRestorePosition);
    	}
	}
	
	public void refreshAvailableDisplay(){
		availableAdapter.notifyDataSetChanged();
	}
	
}
