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

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
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
import cm.aptoide.pt.EnumAptoideInterfaceTasks;
import cm.aptoide.pt.R;
import cm.aptoide.pt.data.AIDLAptoideServiceData;
import cm.aptoide.pt.data.display.ViewDisplayApplication;
import cm.aptoide.pt.data.display.ViewDisplayApplicationAvailable;
import cm.aptoide.pt.data.display.ViewDisplayCategory;
import cm.aptoide.pt.data.display.ViewDisplayListApps;
import cm.aptoide.pt.data.display.ViewDisplayListsDimensions;

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

	private ViewDisplayListsDimensions displayListsDimensions;
	private ViewDisplayCategory category = null;
	
	private ViewDisplayListApps apps = null;
	private ViewDisplayListApps freshApps = null;
	private ViewDisplayListApps freshTopApps = null;
	private ViewDisplayListApps freshBottomApps = null;
	private AtomicInteger appsTrimTopAmount;
	private AtomicInteger appsTrimBottomAmount;
	
	private AtomicInteger cacheListOffset;
	private AtomicInteger cachePagesTrimmed;

	private AtomicInteger scrollDirectionReference;
		

	private AvailableAppsManager appsManager;
	
	private AIDLAptoideServiceData serviceDataCaller = null;

	
	private Handler interfaceTasksHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	EnumAptoideInterfaceTasks task = EnumAptoideInterfaceTasks.reverseOrdinal(msg.what);
        	switch (task) {
				
				case RESET_AVAILABLE_LIST_DISPLAY:
					resetDisplay();
					break;
					
				case TRIM_TOP_AND_UPDATE_AVAILABLE_LIST_DISPLAY:
					trimTopAppsList(appsTrimTopAmount.get());					
					break;
					
				case TRIM_BOTTOM_AND_UPDATE_AVAILABLE_LIST_DISPLAY:
					trimBottomAppsList(appsTrimBottomAmount.get());					
					break;
					
				case PREPEND_AND_UPDATE_AVAILABLE_LIST_DISPLAY:
					prependAndUpdateDisplay(freshTopApps);
					break;
					
				case APPEND_AND_UPDATE_AVAILABLE_LIST_DISPLAY:
					appendAndUpdateDisplay(freshBottomApps);
					break;
					
				case TRIM_PREPEND_AND_UPDATE_AVAILABLE_LIST_DISPLAY:
					trimPrependAndUpdateDisplay(freshTopApps, appsTrimBottomAmount.get());
					break;
					
				case TRIM_APPEND_AND_UPDATE_AVAILABLE_LIST_DISPLAY:
					trimAppendAndUpdateDisplay(appsTrimTopAmount.get(), freshBottomApps);
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
				int range = displayListsDimensions.getPageSize();
				
				boolean trim_top = (apps.size() >= (displayListsDimensions.getCacheSize()));
				if(trim_top){
					cachePagesTrimmed.incrementAndGet();
				}
				boolean append = true;
				int offset = cacheListOffset.incrementAndGet()*displayListsDimensions.getPageSize();

				try {
					if( category != null && !category.hasChildren()){
						Log.d("Aptoide","scrolling down available list.  offset: "+offset+" range: "+range+" category: "+category);
						addBottomFreshAvailableApps(serviceDataCaller.callGetAvailableAppsByCategory(offset, range, category.getCategoryHashid()));
					}else{
						Log.d("Aptoide","scrolling down available list.  offset: "+offset+" range: "+range);
						addBottomFreshAvailableApps(serviceDataCaller.callGetAvailableApps(offset, range));
					}
					
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if(freshBottomApps.size() == 0){
					append = false;
				}
				
				if(trim_top){
					appsTrimTopAmount.addAndGet(range);
					if(append){
						interfaceTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.TRIM_APPEND_AND_UPDATE_AVAILABLE_LIST_DISPLAY.ordinal());
					}else{
						interfaceTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.TRIM_TOP_AND_UPDATE_AVAILABLE_LIST_DISPLAY.ordinal());
					}
				}else{
					if(append){
						interfaceTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.APPEND_AND_UPDATE_AVAILABLE_LIST_DISPLAY.ordinal());
					}
				}
				
			}
			
		}
    	
		private class ScrollUp implements Runnable {

			@Override
			public void run() {
				int range = displayListsDimensions.getPageSize();
				
				boolean trim_bottom = (apps.size() >= (displayListsDimensions.getCacheSize()));
				if(trim_bottom){
					cacheListOffset.decrementAndGet();					
				}
				boolean prepend = (cachePagesTrimmed.get() > 0);
				if(prepend){
					int offset = cachePagesTrimmed.decrementAndGet()*displayListsDimensions.getPageSize();
					
					try {
						if(category != null && !category.hasChildren()){
							Log.d("Aptoide","scrolling up available list.  offset: "+offset+" range: "+range+" category: "+category);
							addTopFreshAvailableApps(serviceDataCaller.callGetAvailableAppsByCategory(offset, range, category.getCategoryHashid()));
						}else{
							Log.d("Aptoide","scrolling up available list.  offset: "+offset+" range: "+range);
							addTopFreshAvailableApps(serviceDataCaller.callGetAvailableApps(offset, range));
						}	
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				if(trim_bottom){
					appsTrimBottomAmount.addAndGet(range);
					if(prepend){
						interfaceTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.TRIM_PREPEND_AND_UPDATE_AVAILABLE_LIST_DISPLAY.ordinal());						
					}else{
						interfaceTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.TRIM_BOTTOM_AND_UPDATE_AVAILABLE_LIST_DISPLAY.ordinal());
					}
				}else{
					if(prepend){
						interfaceTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.PREPEND_AND_UPDATE_AVAILABLE_LIST_DISPLAY.ordinal());							
					}
				}
				
			}
			
		}
    	
		private class Reload implements Runnable {
			@Override
			public void run() {
				int offset = cacheListOffset.get()*displayListsDimensions.getPageSize();
				int range = displayListsDimensions.getPageSize();
								
				try {
					if(category != null && !category.hasChildren()){
						Log.d("Aptoide","reloading available list.  offset: "+offset+" range: "+range);
						setFreshAvailableApps(serviceDataCaller.callGetAvailableAppsByCategory(offset, range, category.getCategoryHashid()));
						interfaceTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.RESET_AVAILABLE_LIST_DISPLAY.ordinal());
					}else{
						Log.d("Aptoide","reloading available list.  offset: "+offset+" range: "+range);
						setFreshAvailableApps(serviceDataCaller.callGetAvailableApps(offset, range));
						interfaceTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.RESET_AVAILABLE_LIST_DISPLAY.ordinal());
					}
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
    	
		private class Reset implements Runnable {

			@Override
			public void run() {
				cacheListOffset.set(0);
				int offset = 0;
				int range = displayListsDimensions.getFastReset();
				try {
					if(category != null && !category.hasChildren()){
						Log.d("Aptoide","resetting available list.  offset: "+offset+" range: "+range+" "+category);
						setFreshAvailableApps(serviceDataCaller.callGetAvailableAppsByCategory(offset, range, category.getCategoryHashid()));
						interfaceTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.RESET_AVAILABLE_LIST_DISPLAY.ordinal());
					}else{
						Log.d("Aptoide","resetting available list.  offset: "+offset+" range: "+range);
						setFreshAvailableApps(serviceDataCaller.callGetAvailableApps(offset, range));
						interfaceTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.RESET_AVAILABLE_LIST_DISPLAY.ordinal());
					}
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}

    }
    
    
	private synchronized void detectMoreAppsNeeded(int position){
		if((position % displayListsDimensions.getPageSize()) == 0){
			if((position - scrollDirectionReference.get()) > 0){
				scrollDirectionReference.set(position);
				appsManager.scrollDown();
			}else{
				scrollDirectionReference.set(position);
				appsManager.scrollUp();			
			}
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
		
		
		File iconCache = new File(apps.get(position).getIconCachePath());
		if(iconCache.exists() && iconCache.length() > 0){
			rowViewHolder.app_icon.setImageURI(Uri.parse(apps.get(position).getIconCachePath()));
		}else{
			rowViewHolder.app_icon.setImageResource(android.R.drawable.sym_def_app_icon);
		}
		
		rowViewHolder.app_name.setText(apps.get(position).getAppName());
		rowViewHolder.uptodate_versionname.setText(apps.get(position).getVersionName());
		
		rowViewHolder.downloads.setText(((ViewDisplayApplicationAvailable) apps.get(position)).getDownloads());
		rowViewHolder.stars.setRating(((ViewDisplayApplicationAvailable) apps.get(position)).getStars());
		
		return convertView;
	}
	
	
	
	
	/**
	 * DynamicAppsListAdapter Constructor
	 *
	 * @param context
	 * @param textViewResourceId
	 */
	public DynamicAppsListAdapter(Context context, ListView listView, AIDLAptoideServiceData serviceDataCaller) {
		super(context, R.layout.row_app_available);
		
		this.serviceDataCaller = serviceDataCaller;
		
		apps = new ViewDisplayListApps();

		appsManager = new AvailableAppsManager();


		this.listView = listView;
		layoutInflater = LayoutInflater.from(context);
		
		cacheListOffset = new AtomicInteger(0);
		cachePagesTrimmed = new AtomicInteger(0);
		appsTrimTopAmount = new AtomicInteger(0);
		appsTrimBottomAmount = new AtomicInteger(0);
		
		scrollDirectionReference = new AtomicInteger(0);

        try {

            displayListsDimensions = serviceDataCaller.callGetDisplayListsDimensions();
        } catch (RemoteException e) {
			// TODO Auto-generated catch block
            e.printStackTrace();
        }
        
	} 
	
	
	
	public void resetDisplay(ViewDisplayCategory category){
		this.category = category;
		appsManager.reset();
	}	
    
	public void reloadDisplay(){
		appsManager.reload();
	}
	
	public void refreshAvailableDisplay(){
		notifyDataSetChanged();
	}
	
	
	
	private synchronized void setFreshAvailableApps(ViewDisplayListApps freshAvailableApps){
		Log.d("Aptoide-DynamicAppsListAdapter", "setFreshAvailableList");
		this.freshApps = freshAvailableApps;
	}
	
	private synchronized void addTopFreshAvailableApps(ViewDisplayListApps freshAvailableApps){
		Log.d("Aptoide-DynamicAppsListAdapter", "addTopFreshAvailableList");
		this.freshTopApps.addAll(freshAvailableApps);
	}
	
	private synchronized void addBottomFreshAvailableApps(ViewDisplayListApps freshAvailableApps){
		Log.d("Aptoide-DynamicAppsListAdapter", "addBottomFreshAvailableList");
		this.freshBottomApps.addAll(freshAvailableApps);
	}
	


	private void initDisplayAvailable(){
		listView.setAdapter(this);
    }
	
	private void resetDisplay(){
		Log.d("Aptoide-DynamicAppsListAdapter", "new AvailableList: "+freshApps.size());
		
		if(freshInstalledApps.size()==0){
			switchInstalledToEmpty();
		}else{
			switchInstalledToList();
		}

		if(currentAppsList.equals(EnumAppsLists.Installed)){
			showInstalledList();
		}
		
		int scrollRestorePosition = listView.getFirstVisiblePosition();
		int partialScrollRestorePosition = (listView.getChildAt(0)==null?0:listView.getChildAt(0).getTop());
		boolean newList = this.apps.isEmpty();
    	this.apps = freshApps;
    	if(newList){
    		initDisplayAvailable();
    	}else{
    		refreshAvailableDisplay();
    	}
    	listView.setSelectionFromTop(scrollRestorePosition, partialScrollRestorePosition);
    	updatableAppsManager.resetUpdatableApps();
	}
	
	private synchronized void trimTopAppsList(int trimAmount){
		int adjustAmount = trimAmount;
		Log.d("Aptoide-DynamicAppsListAdapter", "trimTopAvailableList: "+trimAmount);
		int scrollRestorePosition = listView.getFirstVisiblePosition();
		int partialScrollRestorePosition = (listView.getChildAt(0)==null?0:listView.getChildAt(0).getTop());
		Log.d("Aptoide-DynamicAppsListAdapter", "list size before: "+apps.size()+"   original scroll position: "+scrollRestorePosition);
		do{
			this.apps.removeFirst();
			trimAmount--;
		}while(trimAmount>0);
		Log.d("Aptoide-DynamicAppsListAdapter", "list size after: "+apps.size());
		Log.d("Aptoide-DynamicAppsListAdapter", "restoring scroll position, currentPosition:"+listView.getFirstVisiblePosition()+" firstVisiblePosition: "+(scrollRestorePosition-adjustAmount)+" top: "+partialScrollRestorePosition);
    	listView.setSelectionFromTop((scrollRestorePosition-adjustAmount), partialScrollRestorePosition);
	}
	
	private synchronized void trimBottomAppsList(int trimAmount){
		Log.d("Aptoide-DynamicAppsListAdapter", "trimEndAvailableList: "+trimAmount);
		do{
			this.apps.removeLast();
			trimAmount--;
		}while(trimAmount>0);
		Log.d("Aptoide-DynamicAppsListAdapter", "list size after: "+apps.size());
	}
	
	private void prependAndUpdateDisplay(ViewDisplayListApps freshAvailableApps){	
		Log.d("Aptoide-DynamicAppsListAdapter", "prepending freshAvailableList: "+freshAvailableApps.size());
		if(freshAvailableApps.isEmpty()){
			return;
		}
		
    	int adjustAmount = freshAvailableApps.size();
    	boolean newList = this.apps.isEmpty();
    	if(newList){
    		Log.d("Aptoide-DynamicAppsListAdapter", "prepending to empty available list");
    		this.apps = freshAvailableApps;
    		
    		initDisplayAvailable();
    	}else{	
    		int scrollRestorePosition = listView.getFirstVisiblePosition();
    		int partialScrollRestorePosition = (listView.getChildAt(0)==null?0:listView.getChildAt(0).getTop());
    		
    		this.apps.addAll(0,freshAvailableApps);
    		Log.d("Aptoide-DynamicAppsListAdapter", "new displayList size: "+this.apps.size());
    		
    		refreshAvailableDisplay();

        	listView.setSelectionFromTop(scrollRestorePosition+adjustAmount, partialScrollRestorePosition);
    	}
	}
	
	private void appendAndUpdateDisplay(ViewDisplayListApps freshAvailableApps){	
		Log.d("Aptoide-DynamicAppsListAdapter", "appending freshAvailableList: "+freshAvailableApps.size());
		if(freshAvailableApps.isEmpty()){
			return;
		}
		
		boolean newList = this.apps.isEmpty();
    	if(newList){
    		Log.d("Aptoide-DynamicAppsListAdapter", "appending to empty available list");
    		this.apps = freshAvailableApps;
    		
    		initDisplayAvailable();
    	}else{	
    		this.apps.addAll(freshAvailableApps);
    		Log.d("Aptoide-DynamicAppsListAdapter", "new displayList size: "+this.apps.size());
    		
    		refreshAvailableDisplay();
    	}
	}
	
	private void trimPrependAndUpdateDisplay(ViewDisplayListApps freshAvailableApps, int trimAmount){
		prependAndUpdateDisplay(freshAvailableApps);
		trimBottomAppsList(trimAmount);
	}
	
	private void trimAppendAndUpdateDisplay(int trimAmount, ViewDisplayListApps freshAvailableApps){
		trimTopAppsList(trimAmount);
		appendAndUpdateDisplay(freshAvailableApps);
	}
	
}
