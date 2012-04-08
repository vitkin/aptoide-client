/**
 * Search,		part of Aptoide
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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.app.SearchManager;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import cm.aptoide.pt.data.AIDLAptoideServiceData;
import cm.aptoide.pt.data.AptoideServiceData;
import cm.aptoide.pt.data.display.ViewDisplayListApps;
import cm.aptoide.pt.data.util.Constants;
import cm.aptoide.pt.ifaceutil.StaticSearchAppResultsListAdapter;

public class Search extends ListActivity {

	View loading;
	View empty;
	ImageView searchView;
	
	private ViewDisplayListApps freshSearchResults;
	private ViewDisplayListApps searchResults;
	
	private StaticSearchAppResultsListAdapter resultsAdapter;
	
	private View bazaarSearchView = null;
	
	private String searchString;
	private EnumAppsSorting appsSortingPolicy = null;
	private Handler waitHandler = null;

	private SearchAppsManager searchAppsManager;
	private final int RESET_RESULTS_LIST = Constants.EMPTY_INT; 
	
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

			searchAppsManager.getSearchResults();
			
            Log.v("Aptoide-Search", "Called for getting apps sorting policy");
            try {
				appsSortingPolicy = EnumAppsSorting.reverseOrdinal(serviceDataCaller.callGetAppsSortingPolicy());
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
	
	private Handler interfaceTasksHandler = new Handler() {
		@Override
        public void handleMessage(Message msg) {
        	switch (msg.what) {
        		case RESET_RESULTS_LIST:
        			resetResultsList();
        			break;
	
				default:
					break;
			}
        }
	};
	
	
	private class SearchAppsManager{
    	private ExecutorService tasksPool;
    	
    	public SearchAppsManager(){
    		tasksPool = Executors.newSingleThreadExecutor();
    	}
    	
    	public void getSearchResults(){
        	try {
				tasksPool.execute(new GetSearchResults());
			} catch (Exception e) { }
        }
    	
    	private class GetSearchResults implements Runnable{
			@Override
			public void run() {
				try {
					freshSearchResults = serviceDataCaller.callGetAppSearchResults(searchString);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				interfaceTasksHandler.sendEmptyMessage(RESET_RESULTS_LIST);
			}
    	}

	}
	
	public void resetSearchResults(){
		waitHandler.postDelayed(new Runnable() {
            public void run() {
            	setLoading();
				searchAppsManager.getSearchResults();
            }
        }, 200);
	}
	
	private void resetBazaarSearchButton(){
		if(bazaarSearchView != null)
        	getListView().removeFooterView(bazaarSearchView);
        
        bazaarSearchView = View.inflate(this, R.layout.btn_search_bazaar, null);
        
        Button searchBazaarButton = (Button) bazaarSearchView.findViewById(R.id.bazaar_search);
        searchBazaarButton.setText(getString(R.string.search_bazaar, searchString));
        searchBazaarButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				String url = Constants.URI_SEARCH_BAZAAR+searchString;
				Intent searchBazzar = new Intent(Intent.ACTION_VIEW);
				searchBazzar.setData(Uri.parse(url));
				startActivity(searchBazzar);
			}
		});
        
        getListView().addFooterView(bazaarSearchView);
	}
	
	public void resetResultsList(){	
		loading.setVisibility(View.GONE);
		searchResults = freshSearchResults;
		if(searchResults == null){
			empty.setVisibility(View.VISIBLE);
		}

		resultsAdapter = new StaticSearchAppResultsListAdapter(this, searchResults);
		setListAdapter(resultsAdapter);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.list_search_app_results);

		searchView = (ImageView) findViewById(R.id.search_button);
		searchView.setOnTouchListener(new SearchClickListener());

		loading = findViewById(R.id.loading);
		empty = findViewById(android.R.id.empty);
		empty.setVisibility(View.INVISIBLE);
		
		getListView().setPersistentDrawingCache(ViewGroup.PERSISTENT_ALL_CACHES);
		
		waitHandler = new Handler();
		
		searchAppsManager = new SearchAppsManager();

		Intent searchIntent = getIntent();
		if(searchIntent.hasExtra(SearchManager.QUERY)){
			searchString = searchIntent.getStringExtra(SearchManager.QUERY);

			//@dsilveira #529 search doens't handle hiphens well		
			searchString = searchString.replaceAll("[\\%27]|[\\']|[\\-]{2}|[\\%23]|[#]|\\s{2,}", " ").trim();
			
			resetBazaarSearchButton();
		}
		

		if(!serviceDataIsBound){
			bindService(new Intent(this, AptoideServiceData.class), serviceDataConnection, Context.BIND_AUTO_CREATE);
		}
	}

	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if(intent.hasExtra(SearchManager.QUERY)){
			searchString = intent.getStringExtra(SearchManager.QUERY);

			//@dsilveira #529 search doens't handle hiphens well	
			searchString = searchString.replaceAll("[\\%27]|[\\']|[\\-]{2}|[\\%23]|[#]|\\s{2,}", " ").trim();
			
			resetBazaarSearchButton();
			
			setLoading();
			
			searchAppsManager.getSearchResults();
			
		}
		
	}

	public void setLoading(){
		searchResults = new ViewDisplayListApps();
		resultsAdapter = new StaticSearchAppResultsListAdapter(this, searchResults);
		setListAdapter(resultsAdapter);
		empty.setVisibility(View.INVISIBLE);
		loading.setVisibility(View.VISIBLE);
	}
	
	
	public void setAppsSortingPolicy(EnumAppsSorting sortingPolicy){
		Log.d("Aptoide-Search", "setAppsSortingPolicy to: "+sortingPolicy);
		try {
			serviceDataCaller.callSetAppsSortingPolicy(sortingPolicy.ordinal());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	@Override
	protected void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);
		final int appHashid = searchResults.get(position).getAppHashid();
		Log.d("Aptoide-Search", "Onclick position: "+position+" appHashid: "+appHashid);
		Intent appInfo = new Intent(this,AppInfo.class);
		appInfo.putExtra("appHashid", appHashid);
		startActivity(appInfo);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		
		menu.add(Menu.NONE, EnumOptionsMenu.DISPLAY_OPTIONS.ordinal(), EnumOptionsMenu.DISPLAY_OPTIONS.ordinal(), R.string.display_options)
		.setIcon(R.drawable.ic_menu_filter);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		EnumOptionsMenu menuEntry = EnumOptionsMenu.reverseOrdinal(item.getItemId());
		Log.d("Aptoide-OptionsMenu", "menuOption: "+menuEntry+" itemid: "+item.getItemId());
		switch (menuEntry) {
			case DISPLAY_OPTIONS:
				//TODO refactor extract dialog management class
				LayoutInflater displayOptionsInflater = LayoutInflater.from(this);
				View displayOptions = displayOptionsInflater.inflate(R.layout.dialog_display_options, null);
				Builder alrt = new AlertDialog.Builder(this).setView(displayOptions);
				final AlertDialog sortDialog = alrt.create();
				sortDialog.setIcon(R.drawable.ic_menu_filter);
				sortDialog.setTitle(getString(R.string.display_options));
				
				RadioGroup group_show = (RadioGroup) displayOptions.findViewById(R.id.group_show);
				group_show.setVisibility(View.GONE);
				View spacer = displayOptions.findViewById(R.id.spacer);
				spacer.setVisibility(View.GONE);
				
				// ***********************************************************
				// Sorting
				final RadioButton byAlphabetic = (RadioButton) displayOptions.findViewById(R.id.by_alphabetic);
				final RadioButton byFreshness = (RadioButton) displayOptions.findViewById(R.id.by_freshness);
				final RadioButton byStars = (RadioButton) displayOptions.findViewById(R.id.by_stars);
				final RadioButton byDownloads = (RadioButton) displayOptions.findViewById(R.id.by_downloads);

				switch (appsSortingPolicy) {
					case ALPHABETIC:
						byAlphabetic.setChecked(true);
						break;
						
					case FRESHNESS:
						byFreshness.setChecked(true);
						break;
						
					case STARS:
						byStars.setChecked(true);
						break;
						
					case DOWNLOADS:
						byDownloads.setChecked(true);
						break;
	
					default:
						break;
				}
				
				
				// ***********************************************************
	
				
				sortDialog.setButton(getString(R.string.done), new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						EnumAppsSorting newSortingPolicy = null;
						
						if(byAlphabetic.isChecked()){
							newSortingPolicy = EnumAppsSorting.ALPHABETIC;
						}else if(byFreshness.isChecked()){
							newSortingPolicy = EnumAppsSorting.FRESHNESS;
						}else if(byStars.isChecked()){
							newSortingPolicy = EnumAppsSorting.STARS;
						}else if(byDownloads.isChecked()){
							newSortingPolicy = EnumAppsSorting.DOWNLOADS;
						}
						if(newSortingPolicy != appsSortingPolicy){
							appsSortingPolicy = newSortingPolicy;
							setAppsSortingPolicy(appsSortingPolicy);
							
							resetSearchResults();
							
						}
						sortDialog.dismiss();
					}
				});
				
			sortDialog.show();
			return true;
			
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	class SearchClickListener implements OnTouchListener {

		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				// Button was pressed, change button background
				searchView.setImageResource(R.drawable.searchover);
				return true;
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				// Button was released, reset button background
				searchView.setImageResource(R.drawable.search);
				onSearchRequested();
				return true;
			}

			return true;
		}

	};
	
	@Override
	public boolean onSearchRequested() {	
		startSearch(searchString, false, null, false);
		return true;
	}

	@Override
	protected void onDestroy() {
		searchAppsManager.tasksPool.shutdownNow();
		if(serviceDataIsBound){
			unbindService(serviceDataConnection);	
		}
		super.onDestroy();
	}
	
}
