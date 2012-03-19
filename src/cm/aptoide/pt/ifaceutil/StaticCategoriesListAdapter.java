/**
 * StaticCategoriesListAdapter,		part of Aptoide's data model
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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import cm.aptoide.pt.EnumAptoideInterfaceTasks;
import cm.aptoide.pt.R;
import cm.aptoide.pt.data.AIDLAptoideServiceData;
import cm.aptoide.pt.data.display.ViewDisplayApplication;
import cm.aptoide.pt.data.display.ViewDisplayCategory;
import cm.aptoide.pt.data.display.ViewDisplayListApps;
import cm.aptoide.pt.data.model.ViewCategory;
import cm.aptoide.pt.data.util.Constants;

 /**
 * StaticCategoriesListAdapter, models a static loading apps list adapter
 * 								extends baseAdapter
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class StaticCategoriesListAdapter extends BaseAdapter{

	private ListView listView;
	private LayoutInflater layoutInflater;
	
	private ViewDisplayCategory category = null;
	private ViewDisplayCategory freshCategory = null;

	private CategoriesManager appsManager;
	
	private AIDLAptoideServiceData serviceDataCaller = null;
	
	private Handler aptoideTasksHandler;

	
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
    
    

    private class CategoriesManager{
    	private ExecutorService installedColectorsPool;
    	
    	public CategoriesManager(){
    		installedColectorsPool = Executors.newSingleThreadExecutor();
    	}
    	
    	public void reset(){
        	installedColectorsPool.execute(new GetInstalledApps());
        }
    	
    	private class GetInstalledApps implements Runnable{

			@Override
			public void run() {
				

						interfaceTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.SWITCH_AVAILABLE_TO_PROGRESSBAR.ordinal());
						try {
							if( category == null || category.getCategoryHashid() == Constants.TOP_CATEGORY || category.hasChildren() ){
								// RESET CATEGORIES TO ZERO
								Log.d("Aptoide","resetting categories list.");
								setFreshCategories(serviceDataCaller.callGetCategories());
								interfaceTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.RESET_CATEGORIES.ordinal());
							}
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				
				aptoideTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.SWITCH_INSTALLED_TO_PROGRESSBAR.ordinal());
				try {
					setFreshInstalledApps(serviceDataCaller.callGetInstalledApps());
					interfaceTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.RESET_INSTALLED_LIST_DISPLAY.ordinal());
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
    		
    	}
    }
	
	
	
	public static class CategoryRowViewHolder{
		int categoryHashid;
		
		TextView category_name;
		TextView category_apps;
	}
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		CategoryRowViewHolder rowViewHolder;
		
		if(convertView == null){
			convertView = layoutInflater.inflate(R.layout.row_category, null);
			
			rowViewHolder = new CategoryRowViewHolder();
			rowViewHolder.category_name = (TextView) convertView.findViewById(R.id.category_name);
			rowViewHolder.category_apps = (TextView) convertView.findViewById(R.id.category_apps);
			
			convertView.setTag(rowViewHolder);
		}else{
			rowViewHolder = (CategoryRowViewHolder) convertView.getTag();
		}

//		rowViewHolder.categoryHashid = category.get(position).getHashid();
//		
//		rowViewHolder.category_name.setText(category.get(position).getName());
//		rowViewHolder.category_apps.setText(category.get(position).getVersionName());
		
		return convertView;
	}
	
	@Override
	public int getCount() {
//		return category.size();
		return 0;
	}

	@Override
	public ViewCategory getItem(int position) {
//		return apps.get(position);
		return null;
	}

	@Override
	public long getItemId(int position) {
//		return apps.get(position).getAppHashid();
		return 0;
	}
	
	
	
	
	/**
	 * StaticCategoriesListAdapter Constructor
	 *
	 * @param context
	 * @param textViewResourceId
	 */
	public StaticCategoriesListAdapter(Context context, ListView listView, AIDLAptoideServiceData serviceDataCaller, Handler aptoideTasksHandler) {
		
		this.serviceDataCaller = serviceDataCaller;
		this.aptoideTasksHandler = aptoideTasksHandler;
		
		appsManager = new CategoriesManager();


		this.listView = listView;
		layoutInflater = LayoutInflater.from(context);
	} 
	
	
	
	public void resetDisplayInstalled(){
		appsManager.reset();
	}
	
	public void refreshDisplayInstalled(){
		notifyDataSetChanged();
	}
	
	
	
    private void initDisplay(){
		listView.setAdapter(this);    	
    }
	
	private synchronized void setFreshInstalledApps(ViewDisplayListApps freshInstalledApps){
//		this.freshApps = freshInstalledApps;
	}
	
	private void resetDisplay(){
//		if(freshApps.isEmpty()){
//			aptoideTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.SWITCH_INSTALLED_TO_NO_APPS.ordinal());
//		}else{
//			aptoideTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.SWITCH_INSTALLED_TO_LIST.ordinal());
//		}
//
//		Log.d("Aptoide-StaticInstalledAppsListAdapter", "new InstalledList: "+freshApps.size());
//    	this.apps = freshApps;
//   		initDisplay();
//    	refreshDisplayInstalled();
//    	
//    	aptoideTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.RESET_UPDATABLE_LIST_DISPLAY.ordinal());
    	
	}
	
	
	
	

	
	public void initDisplayCategories(){
//		categoriesAdapter = new SimpleAdapter(Aptoide.this, category.getDisplayList(), R.layout.row_category 
//         		, new String[] {Constants.KEY_CATEGORY_HASHID, Constants.KEY_CATEGORY_NAME, Constants.DISPLAY_CATEGORY_APPS}
//				, new int[] {R.id.category_hashid, R.id.category_name, R.id.category_apps});
//		
//		categoriesAdapter.setViewBinder(new CategoryListBinder());
//		availableAppsListView.setOnScrollListener(null);
//		availableAppsListView.setAdapter(categoriesAdapter);
	}
	
	public synchronized void setFreshCategories(ViewDisplayCategory freshCategory){
//		AptoideLog.d(Aptoide.this, "setFreshCategories");
		this.freshCategory = freshCategory;
		this.freshCategory.generateDisplayLists();
	}
	
	public void resetDisplayCategories(){
//		if(freshCategory == null || (freshCategory.getCategoryHashid() == Constants.TOP_CATEGORY && !freshCategory.hasChildren())){
//			switchAvailableToEmpty();
//		}else{
//			switchAvailableToList();
//		}
//		
//		if(currentAppsList.equals(EnumAppsLists.Available)){
//			showAvailableList();			
//		}
//		
//    	AptoideLog.d(Aptoide.this, "new CategoriesList: "+freshCategory);
//		boolean newList = this.category == null;
//    	this.category = freshCategory;
//    	initDisplayCategories();
//    	if(!newList){
//    		refreshCategoriesDisplay();
//    	}
	}
	
	public void refreshCategoriesDisplay(){
//		categoriesAdapter.notifyDataSetChanged();
	}
	
}
