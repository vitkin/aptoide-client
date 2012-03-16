/**
 * StaticAppsListAdapter,		part of Aptoide's data model
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
import cm.aptoide.pt.Aptoide;
import cm.aptoide.pt.EnumAppsLists;
import cm.aptoide.pt.EnumAptoideInterfaceTasks;
import cm.aptoide.pt.R;
import cm.aptoide.pt.data.AIDLAptoideServiceData;
import cm.aptoide.pt.data.display.ViewDisplayApplication;
import cm.aptoide.pt.data.display.ViewDisplayApplicationAvailable;
import cm.aptoide.pt.data.display.ViewDisplayCategory;
import cm.aptoide.pt.data.display.ViewDisplayListApps;
import cm.aptoide.pt.data.display.ViewDisplayListsDimensions;
import cm.aptoide.pt.debug.AptoideLog;

 /**
 * StaticAppsListAdapter, models a static loading apps list adapter
 * 							extends arrayAdapter
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class StaticAppsListAdapter extends ArrayAdapter<ViewDisplayApplication>{

	private ListView listView;
	private LayoutInflater layoutInflater;

	private ViewDisplayListApps apps = null;
	private ViewDisplayListApps freshApps = null;
	
	private InstalledAppsManager appsManager;
	
	private AIDLAptoideServiceData serviceDataCaller = null;

	
	private Handler interfaceTasksHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	EnumAptoideInterfaceTasks task = EnumAptoideInterfaceTasks.reverseOrdinal(msg.what);
        	switch (task) {
				
				case RESET_INSTALLED_LIST_DISPLAY:
					resetDisplay();
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
    	
    	public void reset(){
        	installedColectorsPool.execute(new GetInstalledApps());
        }
    	
    	private class GetInstalledApps implements Runnable{

			@Override
			public void run() {
				interfaceTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.SWITCH_INSTALLED_TO_PROGRESSBAR.ordinal());
				try {
					setFreshInstalledApps(serviceDataCaller.callGetInstalledApps());
					interfaceTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.RESET_INSTALLED_LIST_DISPLAY.ordinal());
//					if(!(availableApps.getList().size()==0)){
//						updatableAppsManager.resetUpdatableApps();
//					}
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

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
	 * StaticAppsListAdapter Constructor
	 *
	 * @param context
	 * @param textViewResourceId
	 */
	public StaticAppsListAdapter(Context context, ListView listView, AIDLAptoideServiceData serviceDataCaller) {
		super(context, R.layout.row_app_available);
		
		this.serviceDataCaller = serviceDataCaller;
		
		apps = new ViewDisplayListApps();

		appsManager = new InstalledAppsManager();


		this.listView = listView;
		layoutInflater = LayoutInflater.from(context);
	} 
	
	
	
	public void resetDisplay(){
		appsManager.reset();
	}
	
	public void refreshDisplay(){
		notifyDataSetChanged();
	}
	
	
	

    
	
	
	
    
    public void initDisplayInstalled(){
		listView.setAdapter(this);    	
    }
	
	public synchronized void setFreshInstalledApps(ViewDisplayListApps freshInstalledApps){
		this.freshApps = freshInstalledApps;
	}
	
	public void resetDisplayInstalled(){
		if(freshInstalledApps.size()==0){
			switchInstalledToEmpty();
		}else{
			switchInstalledToList();
		}

		if(currentAppsList.equals(EnumAppsLists.Installed)){
			showInstalledList();
		}

		AptoideLog.d(Aptoide.this, "new InstalledList: "+freshInstalledApps.size());
		boolean newList = this.installedApps.isEmpty();
    	this.installedApps = freshInstalledApps;
    	initDisplayInstalled();
    	if(!newList){
    		installedAdapter.notifyDataSetChanged();
    	}
    	
    	if(!(availableApps.size()==0)){
			updatableAppsManager.resetUpdatableApps();
		}
//    	if(bootingUp && availableApps.getList().size()==0){
//			switchAvailableToProgressBar();
//			showAvailableList();
//		}
	}

	
	private void reDisplay(){
		Log.d("Aptoide-DynamicAppsListAdapter", "new AvailableList: "+freshApps.size());
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
