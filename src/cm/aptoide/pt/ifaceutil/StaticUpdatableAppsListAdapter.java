/**
 * StaticUpdatableAppsListAdapter,		part of Aptoide's data model
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

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import cm.aptoide.pt.EnumAptoideInterfaceTasks;
import cm.aptoide.pt.R;
import cm.aptoide.pt.data.AIDLAptoideServiceData;
import cm.aptoide.pt.data.display.ViewDisplayApplication;
import cm.aptoide.pt.data.display.ViewDisplayApplicationUpdatable;
import cm.aptoide.pt.data.display.ViewDisplayListApps;

 /**
 * StaticUpdatableAppsListAdapter, models a static loading, Updatable apps list adapter
 * 									extends baseAdapter
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class StaticUpdatableAppsListAdapter extends BaseAdapter{

	private ListView listView;
	private LayoutInflater layoutInflater;

	private ViewDisplayListApps apps = null;
	private ViewDisplayListApps freshApps = null;
	
	private UpdatableAppsManager appsManager;
	
	private AIDLAptoideServiceData serviceDataCaller = null;
	
	private Handler aptoideTasksHandler;

	
	private Handler interfaceTasksHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	EnumAptoideInterfaceTasks task = EnumAptoideInterfaceTasks.reverseOrdinal(msg.what);
        	switch (task) {
				
				case RESET_UPDATABLE_LIST_DISPLAY:
					resetDisplay();
					break;
	
				default:
					break;
			}
        }
    };
    
    
    private class UpdatableAppsManager{
    	private ExecutorService updatableColectorsPool;
    	
    	public UpdatableAppsManager(){
    		updatableColectorsPool = Executors.newSingleThreadExecutor();
    	}
    	
    	public void reset(){
        	updatableColectorsPool.execute(new GetUpdatableApps());
        }
    	
    	private class GetUpdatableApps implements Runnable{

			@Override
			public void run() {
				aptoideTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.SWITCH_UPDATABLE_TO_PROGRESSBAR.ordinal());
				try {
					setFreshUpdatableApps(serviceDataCaller.callGetUpdatableApps());
					interfaceTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.RESET_UPDATABLE_LIST_DISPLAY.ordinal());
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
    		
    	}
    }
	
	
	
	public static class UpdatableRowViewHolder{
		ImageView app_icon;
		
		TextView app_name;
		TextView installed_versionname;
		TextView uptodate_versionname;
		
		TextView downloads;
		RatingBar stars;
	}
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		UpdatableRowViewHolder rowViewHolder;
		
		if(convertView == null){
			convertView = layoutInflater.inflate(R.layout.row_app_updatable, null);
			
			rowViewHolder = new UpdatableRowViewHolder();
			rowViewHolder.app_icon = (ImageView) convertView.findViewById(R.id.app_icon);
			rowViewHolder.app_name = (TextView) convertView.findViewById(R.id.app_name);
			rowViewHolder.installed_versionname = (TextView) convertView.findViewById(R.id.installed_versionname);
			rowViewHolder.uptodate_versionname = (TextView) convertView.findViewById(R.id.uptodate_versionname);
			rowViewHolder.downloads = (TextView) convertView.findViewById(R.id.downloads);
			rowViewHolder.stars = (RatingBar) convertView.findViewById(R.id.stars);
			
			convertView.setTag(rowViewHolder);
		}else{
			rowViewHolder = (UpdatableRowViewHolder) convertView.getTag();
		}
		
		File iconCache = new File(apps.get(position).getIconCachePath());
		if(iconCache.exists() && iconCache.length() > 0){
			rowViewHolder.app_icon.setImageURI(Uri.parse(apps.get(position).getIconCachePath()));
		}else{
			rowViewHolder.app_icon.setImageResource(android.R.drawable.sym_def_app_icon);
		}
		
		rowViewHolder.app_name.setText(apps.get(position).getAppName());
		rowViewHolder.uptodate_versionname.setText(apps.get(position).getVersionName());
		
		rowViewHolder.installed_versionname.setText(((ViewDisplayApplicationUpdatable) apps.get(position)).getInstalledVersionName());
				
		rowViewHolder.downloads.setText(Integer.toString(((ViewDisplayApplicationUpdatable) apps.get(position)).getDownloads()));
		rowViewHolder.stars.setRating(((ViewDisplayApplicationUpdatable) apps.get(position)).getStars());
		
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
	 * StaticUpdatableAppsListAdapter Constructor
	 *
	 * @param context
	 * @param textViewResourceId
	 */
	public StaticUpdatableAppsListAdapter(Context context, ListView listView, AIDLAptoideServiceData serviceDataCaller, Handler aptoideTasksHandler) {
		
		this.serviceDataCaller = serviceDataCaller;
		this.aptoideTasksHandler = aptoideTasksHandler;
		
		apps = new ViewDisplayListApps();

		appsManager = new UpdatableAppsManager();


		this.listView = listView;
		layoutInflater = LayoutInflater.from(context);
	} 
	
	
	
	public void resetDisplayUpdatable(){
		appsManager.reset();
	}
	
	public void refreshDisplayUpdatable(){
		notifyDataSetChanged();
	}
	
	
	
    private void initDisplay(){
		listView.setAdapter(this);    	
    }
	
	private synchronized void setFreshUpdatableApps(ViewDisplayListApps freshUpdatableApps){
		this.freshApps = freshUpdatableApps;
	}
	
	private void resetDisplay(){
		if(freshApps == null || freshApps.isEmpty()){
			aptoideTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.SWITCH_UPDATABLE_TO_NO_APPS.ordinal());
		}else{
			aptoideTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.SWITCH_UPDATABLE_TO_LIST.ordinal());

			Log.d("Aptoide-StaticUpdatableAppsListAdapter", "new UpdatableList: "+freshApps.size());
	    	this.apps = freshApps;
	   		initDisplay();
	    	refreshDisplayUpdatable();

		}
	}
	
}
