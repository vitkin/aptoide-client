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
import cm.aptoide.pt.data.display.ViewDisplayCategory;
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

	private CategoriesManager categoriesManager;
	
	private AIDLAptoideServiceData serviceDataCaller = null;
	
	private Handler aptoideTasksHandler;

	
	private Handler interfaceTasksHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	EnumAptoideInterfaceTasks task = EnumAptoideInterfaceTasks.reverseOrdinal(msg.what);
        	switch (task) {
				
				case RESET_CATEGORIES:
					resetDisplay();
					break;
	
				default:
					break;
			}
        }
    };
    
    

    private class CategoriesManager{
    	private ExecutorService categoriesColectorsPool;
    	
    	public CategoriesManager(){
    		categoriesColectorsPool = Executors.newSingleThreadExecutor();
    	}
    	
    	public void reset(){
        	categoriesColectorsPool.execute(new GetCategories());
        }
    	
    	private class GetCategories implements Runnable{

			@Override
			public void run() {

				aptoideTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.SWITCH_AVAILABLE_TO_PROGRESSBAR.ordinal());
				try {
					if( category == null || category.getCategoryHashid() == Constants.TOP_CATEGORY || category.hasChildren() ){
						Log.d("Aptoide","resetting categories list.");
						setFreshCategories(serviceDataCaller.callGetCategories());
						interfaceTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.RESET_CATEGORIES.ordinal());
					}
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
    		
    	}
    }
	
	
	
	public static class CategoryRowViewHolder{		
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

		rowViewHolder.category_name.setText(category.getSubCategories().get(position).getCategoryName());
		rowViewHolder.category_apps.setText(Integer.toString(category.getSubCategories().get(position).getAvailableApps()));
		
		return convertView;
	}
	
	@Override
	public int getCount() {
		return category.getSubCategories().size();
	}

	@Override
	public ViewDisplayCategory getItem(int position) {
		return category.getSubCategories().get(position);
	}

	@Override
	public long getItemId(int position) {
		return category.getSubCategories().get(position).getCategoryHashid();
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
		
		categoriesManager = new CategoriesManager();


		this.listView = listView;
		layoutInflater = LayoutInflater.from(context);
	} 
	
	public ViewDisplayCategory getCategory(){
		return category;
	}
	
	public void resetDisplayCategories(){
		categoriesManager.reset();
	}
	
	public void gotoSubCategory(int categoryHashid){
		category = category.getSubCategory(categoryHashid);
		if(category.hasChildren()){
			refreshDisplayCategories();			
		}else{
			listView.setAdapter(null);
		}
	}
	
	public void gotoParentCategory(){
		category = category.getParentCategory();
		initDisplay();
		refreshDisplayCategories();
	}
	
    private void initDisplay(){
		listView.setAdapter(this);    	
    }
	
	public void refreshDisplayCategories(){
		notifyDataSetChanged();
	}
	
	private synchronized void setFreshCategories(ViewDisplayCategory freshCategory){
		Log.d("Aptoide-StaticCategoriesListAdapter", "setFreshCategories");
		this.freshCategory = freshCategory;
//		refreshDisplayCategories();
	}
	
	private void resetDisplay(){
		if(freshCategory == null || (freshCategory.getCategoryHashid() == Constants.TOP_CATEGORY && !freshCategory.hasChildren())){
			aptoideTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.SWITCH_AVAILABLE_TO_NO_APPS.ordinal());
		}else{
			aptoideTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.SWITCH_AVAILABLE_TO_CATEGORIES.ordinal());
		}

		Log.d("Aptoide-StaticCategoriesListAdapter", "new category: "+freshCategory);
		this.category = freshCategory;
   		initDisplay();
		refreshDisplayCategories();
		listView.setSelection(1);
    	
    	aptoideTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.RESET_UPDATABLE_LIST_DISPLAY.ordinal());
    	
	}
	
	public void shutdownNow(){
		categoriesManager.categoriesColectorsPool.shutdownNow();
	}
	
}
