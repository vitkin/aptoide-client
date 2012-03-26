/**
 * StaticScheduledAppsListAdapter,		part of Aptoide's data model
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
import java.util.ArrayList;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.data.display.ViewDisplayApplication;
import cm.aptoide.pt.data.display.ViewDisplayListApps;

 /**
 * StaticScheduledAppsListAdapter, models a static loading, scheduled apps list adapter
 * 									extends baseAdapter
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class StaticScheduledAppsListAdapter extends BaseAdapter{

//	private ListView listView;
	private LayoutInflater layoutInflater;

	private ViewDisplayListApps apps = null;
	
	private ArrayList<Boolean> selected = null;
	
//	private SearchResultsManager appsManager;
	
//	private AIDLAptoideServiceData serviceDataCaller = null;
	
	
//	private Handler interfaceTasksHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//        	EnumAptoideInterfaceTasks task = EnumAptoideInterfaceTasks.reverseOrdinal(msg.what);
//        	switch (task) {
//				
//				case RESET_INSTALLED_LIST_DISPLAY:
//					resetDisplay();
//					break;
//	
//				default:
//					break;
//			}
//        }
//    };
    
    

//    private class SearchResultsManager{
//    	private ExecutorService installedColectorsPool;
//    	
//    	public SearchResultsManager(){
//    		installedColectorsPool = Executors.newSingleThreadExecutor();
//    	}
//    	
//    	public void reset(){
//        	installedColectorsPool.execute(new GetInstalledApps());
//        }
//    	
//    	private class GetInstalledApps implements Runnable{
//
//			@Override
//			public void run() {
//				aptoideTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.SWITCH_INSTALLED_TO_PROGRESSBAR.ordinal());
//				try {
//					setFreshInstalledApps(serviceDataCaller.callGetInstalledApps());
//					interfaceTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.RESET_INSTALLED_LIST_DISPLAY.ordinal());
//				} catch (RemoteException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//
//			}
//    		
//    	}
//    }
	
	

	public static class ScheduledRowViewHolder{
		ImageView app_icon;
		
		TextView app_name;
		TextView version;
		
		CheckBox checkBox;
	}
	

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		ScheduledRowViewHolder rowViewHolder;
		
		if(convertView == null){
			convertView = layoutInflater.inflate(R.layout.row_app_scheduled, null);
			
			rowViewHolder = new ScheduledRowViewHolder();
			rowViewHolder.app_icon = (ImageView) convertView.findViewById(R.id.app_icon);
			rowViewHolder.app_name = (TextView) convertView.findViewById(R.id.app_name);
			rowViewHolder.version = (TextView) convertView.findViewById(R.id.version);
			rowViewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);
			
			convertView.setTag(rowViewHolder);
		}else{
			rowViewHolder = (ScheduledRowViewHolder) convertView.getTag();
		}
		File iconCache = new File(apps.get(position).getIconCachePath());
		if(iconCache.exists() && iconCache.length() > 0){
			rowViewHolder.app_icon.setImageURI(Uri.parse(apps.get(position).getIconCachePath()));
		}else{
			rowViewHolder.app_icon.setImageResource(android.R.drawable.sym_def_app_icon);
		}
		
		rowViewHolder.app_name.setText(apps.get(position).getAppName());
		rowViewHolder.version.setText(apps.get(position).getVersionName());
		
		rowViewHolder.checkBox.setChecked(selected.get(position));
		rowViewHolder.checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				selected.set(position, isChecked);
			}
		});
		
		
		return convertView;
	}
	
	
	@Override
	public int getCount() {
		return apps.size();
	}

	@Override
	public ViewDisplayApplication getItem(int position) {
		return apps.get(position);
	}

	@Override
	public long getItemId(int position) {
		return apps.get(position).getAppHashid();
	}
	
	
	/**
	 * StaticScheduledAppsListAdapter Constructor
	 *
	 * @param Context context
	 * @param ViewDisplayListApps scheduledApps
	 */
	public StaticScheduledAppsListAdapter(Context context, ViewDisplayListApps scheduledApps, ArrayList<Boolean> selected){//, ListView listView, AIDLAptoideServiceData serviceDataCaller, Handler aptoideTasksHandler) {
		
//		this.serviceDataCaller = serviceDataCaller;
//		this.aptoideTasksHandler = aptoideTasksHandler;
		
//		apps = new ViewDisplayListApps();
		this.apps = scheduledApps;
		
		this.selected = selected;
		
//		appsManager = new SearchResultsManager();


//		this.listView = listView;
		layoutInflater = LayoutInflater.from(context);
	} 
	
	
	
//	public void resetDisplayInstalled(){
//		appsManager.reset();
//	}
//	
//	public void refreshDisplayInstalled(){
//		notifyDataSetChanged();
//	}
//	
//	
//	
//    private void initDisplay(){
//		listView.setAdapter(this);    	
//    }
//	
//	private synchronized void setFreshInstalledApps(ViewDisplayListApps freshInstalledApps){
//		this.freshApps = freshInstalledApps;
//	}
//	
//	private void resetDisplay(){
//		if(freshApps == null || freshApps.isEmpty()){
//			aptoideTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.SWITCH_INSTALLED_TO_NO_APPS.ordinal());
//		}else{
//			aptoideTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.SWITCH_INSTALLED_TO_LIST.ordinal());
//		
//	    	this.apps = freshApps;
//			Log.d("Aptoide-StaticInstalledAppsListAdapter", "new InstalledList: "+getCount());
//	   		initDisplay();
//	    	refreshDisplayInstalled();
//	    	
//	    	aptoideTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.RESET_UPDATABLE_LIST_DISPLAY.ordinal());
//		}
//	}
	
}
