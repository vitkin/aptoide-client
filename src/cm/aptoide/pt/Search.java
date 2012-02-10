/**
 * ManageRepos,		part of Aptoide
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

import java.io.File;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;
import cm.aptoide.pt.data.AIDLAptoideServiceData;
import cm.aptoide.pt.data.AptoideServiceData;
import cm.aptoide.pt.data.display.ViewDisplayListApps;
import cm.aptoide.pt.data.util.Constants;

public class Search extends Activity implements OnItemClickListener{
	
	private SimpleAdapter resultsAdapter;
	private ListView resultsListView = null;
	private ViewDisplayListApps searchResults;
	private View bazaarSearchButton = null;
	
	private String searchString;
	private String listOrdering;
	
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
	        			
			getSearchResults();
	        			
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
	
	class ResultsListBinder implements ViewBinder
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
	
	public void getSearchResults(){
		try {
			searchResults = serviceDataCaller.callGetAppSearchResults(searchString);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		resetResultsList();
		
	}
	
	private void resetBazaarSearchButton(){
		if(bazaarSearchButton != null)
        	resultsListView.removeFooterView(bazaarSearchButton);
        
        bazaarSearchButton = View.inflate(this, R.layout.btn_search_bazaar, null);
        
        Button search_baz = (Button) bazaarSearchButton.findViewById(R.id.bazaar_search);
        search_baz.setText("Search '" + searchString + "' on Bazaar");
        search_baz.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				String url = Constants.URI_SEARCH_BAZAAR+searchString;
				Intent searchBazzar = new Intent(Intent.ACTION_VIEW);
				searchBazzar.setData(Uri.parse(url));
				startActivity(searchBazzar);
			}
		});
        
        resultsListView.addFooterView(bazaarSearchButton);
	}
	
	public void resetResultsList(){		
		resultsAdapter = new SimpleAdapter(Search.this, searchResults.getList(), R.layout.row_app, 
				new String[] {Constants.KEY_APPLICATION_HASHID, Constants.KEY_APPLICATION_NAME, Constants.DISPLAY_APP_UP_TO_DATE_VERSION_NAME, Constants.KEY_STATS_DOWNLOADS,Constants.KEY_STATS_STARS,  Constants.DISPLAY_APP_ICON_CACHE_PATH},
				new int[] {R.id.app_hashid, R.id.app_name, R.id.uptodate_versionname, R.id.downloads, R.id.stars, R.id.app_icon});
		
		resultsAdapter.setViewBinder(new ResultsListBinder());
		resultsListView.setAdapter(resultsAdapter);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		resultsListView = new ListView(this);
		resultsListView.setPersistentDrawingCache(ViewGroup.PERSISTENT_ALL_CACHES);
		resultsListView.setBackgroundColor(Color.WHITE);
		resultsListView.setOnItemClickListener(this);
		
		setContentView(resultsListView);
				
//		resultsListView = getListView();
//		resultsListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		Intent searchIntent = getIntent();
		if(searchIntent.hasExtra(SearchManager.QUERY)){
			searchString = searchIntent.getStringExtra(SearchManager.QUERY);

			//@dsilveira #529 search doens't handle hiphens well		
			searchString = searchString.replaceAll("[\\%27]|[\\']|[\\-]{2}|[\\%23]|[#]|\\s{2,}", " ").trim();
			
			resetBazaarSearchButton();
//		}else if(searchIntent.hasExtra("market")){
//			String apk_id= searchIntent.getStringExtra("market");
//			searchString=apk_id;
//			apk_lst = db.getSearchById(apk_id);
//			if(!apk_lst.isEmpty())
//			Log.d("",apk_lst.get(0).apkid);
//			
//			if(searchIntent.hasExtra("install")){
//			onListItemClick(getListView(), getListView(), 0, 0);
//			}
		}
		

		if(!serviceDataIsBound){
			bindService(new Intent(this, AptoideServiceData.class), serviceDataConnection, Context.BIND_AUTO_CREATE);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> listView, View view, int position, long id) {

		final int appHashid = Integer.parseInt(((LinearLayout)view).getTag().toString());
		Log.d("Aptoide-Search", "Onclick position: "+position+" appHashid: "+appHashid);
		Intent appInfo = new Intent(this,AppInfo.class);
		appInfo.putExtra("appHashid", appHashid);
		startActivity(appInfo);
		//TODO click change color effect
	}
		
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if(intent.hasExtra(SearchManager.QUERY)){
			searchString = intent.getStringExtra(SearchManager.QUERY);

			//@dsilveira #529 search doens't handle hiphens well	
			searchString = searchString.replaceAll("[\\%27]|[\\']|[\\-]{2}|[\\%23]|[#]|\\s{2,}", " ").trim();
			
			resetBazaarSearchButton();
		}
		
	}

	@Override
	protected void onDestroy() {
		if(serviceDataIsBound){
			unbindService(serviceDataConnection);	
		}
		super.onDestroy();
	}
	
}
