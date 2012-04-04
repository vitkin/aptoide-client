/**
 * ManageScheduled,		part of Aptoide
 * 
 * from v3.0 Copyright (C) 2012  Duarte Silveira
 * duarte.silveira@caixamagica.pt
 * 
 * derivative work of RemoteInSearch from Aptoide's earlier versions with 
 * Copyright (C) 2009  Roberto Jacinto
 * roberto.jacinto@caixamagica.pt
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

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import cm.aptoide.pt.data.AIDLAptoideServiceData;
import cm.aptoide.pt.data.AptoideServiceData;
import cm.aptoide.pt.data.display.ViewDisplayListApps;
import cm.aptoide.pt.ifaceutil.StaticScheduledAppsListAdapter;

public class ManageScheduled extends ListActivity {//implements OnItemClickListener{
	
	private ViewDisplayListApps scheduledApps;
	
	private StaticScheduledAppsListAdapter scheduledAdapter;
	
	private ArrayList<Boolean> selected;
	
	private ScheduledAppsManager scheduledAppsManager;
	
	private AIDLAptoideServiceData serviceDataCaller = null;

	private boolean serviceDataIsBound = false;

	private ServiceConnection serviceDataConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			// This is called when the connection with the service has been
			// established, giving us the object we can use to
			// interact with the service.  We are communicating with the
			// service using AIDL, so here we set the remote service interface.
			serviceDataCaller = AIDLAptoideServiceData.Stub.asInterface(service);
			serviceDataIsBound = true;
			
			Log.v("Aptoide-Search", "Connected to ServiceData");
	        			
			getScheduledApps();
			setScheduledList();
			
//            Log.v("Aptoide-Search", "Called for getting apps sorting policy");
//            try {
//				appsSortingPolicy = EnumAppsSorting.reverseOrdinal(serviceDataCaller.callGetAppsSortingPolicy());
//			} catch (RemoteException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}	 
	        			
		}

		public void onServiceDisconnected(ComponentName className) {
			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.
			serviceDataCaller = null;
			serviceDataIsBound = false;
			
			Log.v("Aptoide-Search", "Disconnected from ServiceData");
		}
	};
	
//	private AIDLSearchInterface.Stub serviceDataCallback = new AIDLSearchInterface.Stub() {
//
//		@Override
//		public void updateSearchResults() throws RemoteException {
//			interfaceTasksHandler.sendEmptyMessage(EnumSearchInterfaceTasks.UPDATE_SEARCH_RESULTS.ordinal());	
//		}
//		
//	};
	
//	private Handler interfaceTasksHandler = new Handler() {
//		@Override
//        public void handleMessage(Message msg) {
//        	EnumSearchInterfaceTasks task = EnumSearchInterfaceTasks.reverseOrdinal(msg.what);
//        	switch (task) {
//        		case UPDATE_SEARCH_RESULTS:
////        			updateSearchResults();
//        			break;
//	
//				default:
//					break;
//			}
//        }
//	};
    
    

    private class ScheduledAppsManager{
    	private ExecutorService tasksPool;
    	
    	public ScheduledAppsManager(){
    		tasksPool = Executors.newCachedThreadPool();
    	}
    	
    	public void install(int appHashid){
        	tasksPool.execute(new InstallApp(appHashid));
        }
    	
    	public void unschedule(int appHashid){
    		tasksPool.execute(new UnscheduleApp(appHashid));
    	}
    	
    	private class InstallApp implements Runnable{

    		private int appHashid;
    		
			public InstallApp(int appHashid) {
				this.appHashid = appHashid;
			}

			@Override
			public void run() {
				try {
					serviceDataCaller.callInstallApp(appHashid);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
    		
    	}
    	
    	private class UnscheduleApp implements Runnable{

    		private int appHashid;
    		
			public UnscheduleApp(int appHashid) {
				this.appHashid = appHashid;
			}

			@Override
			public void run() {
				try {
					serviceDataCaller.callUnscheduleInstallApp(appHashid);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
    		
    	}
    }
    
    
	
	public void getScheduledApps(){
		try {
			scheduledApps = serviceDataCaller.callGetScheduledApps();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public void resetScheduledList(){		
//		if(scheduledApps != null){
//			selected = new ArrayList<Boolean>(scheduledApps.size());
//	
//			for (int i=0; i<scheduledApps.size(); i++) {
//				selected.add(true);
//			}
//	
//	//		Log.d("Aptoide-ManageScheduled", "scheduled: "+scheduledApps+" selected: "+selected);
		setListAdapter(scheduledAdapter);
			scheduledAdapter.notifyDataSetChanged();
//		}		
	}
	
	
	public void setScheduledList(){		
		if(scheduledApps != null){
			selected = new ArrayList<Boolean>(scheduledApps.size());
	
			for (int i=0; i<scheduledApps.size(); i++) {
				selected.add(true);
			}
	
	//		Log.d("Aptoide-ManageScheduled", "scheduled: "+scheduledApps+" selected: "+selected);
			scheduledAdapter = new StaticScheduledAppsListAdapter(this, scheduledApps, selected);
			setListAdapter(scheduledAdapter);
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.list_scheduled_apps);

		if(!serviceDataIsBound){
			bindService(new Intent(this, AptoideServiceData.class), serviceDataConnection, Context.BIND_AUTO_CREATE);
		}
		
		scheduledAppsManager = new ScheduledAppsManager();
		
		Button install = (Button) findViewById(R.id.install);
		install.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				for(int i=0; i < selected.size(); i++){
					if(selected.get(i)){
						scheduledAppsManager.install(scheduledApps.get(i).getAppHashid());
					}
				}
				finish();
			}
		});
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();

		menu.add(Menu.NONE, EnumOptionsMenu.INVERT_SELECTION.ordinal(), EnumOptionsMenu.INVERT_SELECTION.ordinal(), R.string.invert_selection)
		.setIcon(R.drawable.ic_menu_revert);
		menu.add(Menu.NONE, EnumOptionsMenu.REMOVE_SELECTED.ordinal(), EnumOptionsMenu.REMOVE_SELECTED.ordinal(), R.string.remove_selected)
		.setIcon(R.drawable.ic_menu_delete);
		
//		menu.add(Menu.NONE, EnumOptionsMenu.DISPLAY_OPTIONS.ordinal(), EnumOptionsMenu.DISPLAY_OPTIONS.ordinal(), R.string.display_options)
//		.setIcon(android.R.drawable.ic_menu_sort_by_size);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		EnumOptionsMenu menuEntry = EnumOptionsMenu.reverseOrdinal(item.getItemId());
		Log.d("Aptoide-OptionsMenu", "menuOption: "+menuEntry+" itemid: "+item.getItemId());
		switch (menuEntry) {
			case INVERT_SELECTION:
				for(int i=0; i < getListAdapter().getCount(); i++){
					LinearLayout row = (LinearLayout)getListView().getChildAt(i);
					CheckBox checkbox = (CheckBox)row.findViewById(R.id.checkbox);
						checkbox.toggle();
					}
				
				break;
				
			case REMOVE_SELECTED:
				AlertDialog confirmRemove = new AlertDialog.Builder(this).create();
				confirmRemove.setMessage(getText(R.string.confirm_remove));
				confirmRemove.setButton(getText(R.string.yes), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						ArrayList<Integer> removalIds = new ArrayList<Integer>();
						for(int i=0; i < scheduledApps.size(); i++){
							if(selected.get(i)){
								removalIds.add(i);
								scheduledAppsManager.unschedule(scheduledApps.get(i).getAppHashid());
							}
						}
						for(int i=removalIds.size()-1; i >= 0; i--){
							selected.remove(removalIds.get(i).intValue());
							scheduledApps.remove(removalIds.get(i).intValue());
						}
						resetScheduledList();
						return;
					} }); 
				confirmRemove.setButton2(getText(R.string.no), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						return;
					}});
				confirmRemove.show();
				
				break;
				
//			case DISPLAY_OPTIONS:
//				//TODO refactor extract dialog management class
//				LayoutInflater displayOptionsInflater = LayoutInflater.from(this);
//				View displayOptions = displayOptionsInflater.inflate(R.layout.dialog_display_options, null);
//				Builder alrt = new AlertDialog.Builder(this).setView(displayOptions);
//				final AlertDialog sortDialog = alrt.create();
//				sortDialog.setIcon(android.R.drawable.ic_menu_sort_by_size);
//				sortDialog.setTitle(getString(R.string.display_options));
//				
//				RadioGroup group_show = (RadioGroup) displayOptions.findViewById(R.id.group_show);
//				group_show.setVisibility(View.GONE);
//				View spacer = displayOptions.findViewById(R.id.spacer);
//				spacer.setVisibility(View.GONE);
//				
//				// ***********************************************************
//				// Sorting
//				final RadioButton byAlphabetic = (RadioButton) displayOptions.findViewById(R.id.by_alphabetic);
//				final RadioButton byFreshness = (RadioButton) displayOptions.findViewById(R.id.by_freshness);
//				final RadioButton byStars = (RadioButton) displayOptions.findViewById(R.id.by_stars);
//				final RadioButton byDownloads = (RadioButton) displayOptions.findViewById(R.id.by_downloads);
//
//				switch (appsSortingPolicy) {
//					case ALPHABETIC:
//						byAlphabetic.setChecked(true);
//						break;
//						
//					case FRESHNESS:
//						byFreshness.setChecked(true);
//						break;
//						
//					case STARS:
//						byStars.setChecked(true);
//						break;
//						
//					case DOWNLOADS:
//						byDownloads.setChecked(true);
//						break;
//	
//					default:
//						break;
//				}
//				
//				
//				// ***********************************************************
//	
//				
//				sortDialog.setButton(getString(R.string.done), new DialogInterface.OnClickListener() {
//					
//					public void onClick(DialogInterface dialog, int which) {
//						EnumAppsSorting newSortingPolicy = null;
//						
//						if(byAlphabetic.isChecked()){
//							newSortingPolicy = EnumAppsSorting.ALPHABETIC;
//						}else if(byFreshness.isChecked()){
//							newSortingPolicy = EnumAppsSorting.FRESHNESS;
//						}else if(byStars.isChecked()){
//							newSortingPolicy = EnumAppsSorting.STARS;
//						}else if(byDownloads.isChecked()){
//							newSortingPolicy = EnumAppsSorting.DOWNLOADS;
//						}
//						if(newSortingPolicy != appsSortingPolicy){
//							appsSortingPolicy = newSortingPolicy;
//							setAppsSortingPolicy(appsSortingPolicy);
//							
//							resetSearchResults();
//							
//						}
//						sortDialog.dismiss();
//					}
//				});
//				
//			sortDialog.show();
//			return true;
		
		
			
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		if(serviceDataIsBound){
			unbindService(serviceDataConnection);	
		}
		scheduledAppsManager.tasksPool.shutdownNow();
		super.onDestroy();
	}
	
}
