/*
 * DownloadManager, part of Aptoide
 * Copyright (C) 2012 Duarte Silveira
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
package cm.aptoide.pt;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.LinearLayout;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.TextView;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import cm.aptoide.com.nostra13.universalimageloader.core.ImageLoader;
import cm.aptoide.pt.adapters.DownloadedListAdapter;
import cm.aptoide.pt.adapters.DownloadingListAdapter;
import cm.aptoide.pt.adapters.NotDownloadedListAdapter;
import cm.aptoide.pt.services.AIDLServiceDownloadManager;
import cm.aptoide.pt.services.ServiceDownloadManager;
import cm.aptoide.pt.views.EnumDownloadStatus;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

/**
 * DownloadManager
 *
 * @author dsilveira
 *
 */
public class DownloadManager extends Activity /*SherlockActivity */{
	private boolean isRunning = false;
	
	private LinearLayout downloading;
	private DownloadingListAdapter downloadingAdapter;
	
	private LinearLayout downloaded;
	private DownloadedListAdapter downloadedAdapter;
	
	private LinearLayout notDownloaded;
	private NotDownloadedListAdapter notDownloadedAdapter;

	private Button exitButton;
	
	private TextView noDownloads;

	private AIDLServiceDownloadManager serviceManager = null;

	private boolean serviceManagerIsBound = false;

	private ServiceConnection serviceManagerConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			// This is called when the connection with the service has been
			// established, giving us the object we can use to
			// interact with the service.  We are communicating with the
			// service using AIDL, so here we set the remote service interface.
			serviceManager = AIDLServiceDownloadManager.Stub.asInterface(service);
			serviceManagerIsBound = true;
			
			Log.v("Aptoide-DownloadManager", "Connected to ServiceDownloadManager");
	        
			try {
				serviceManager.callRegisterDownloadManager(serviceManagerCallback);
			} catch (RemoteException e) {
				e.printStackTrace();
			}

			continueLoading();
			
		}

		public void onServiceDisconnected(ComponentName className) {
			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.
			serviceManagerIsBound = false;
			serviceManager = null;
			
			Log.v("Aptoide-DownloadManager", "Disconnected from ServiceDownloadManager");
		}
	};
	
	private AIDLDownloadManager.Stub serviceManagerCallback = new AIDLDownloadManager.Stub() {

		@Override
		public void updateDownloadStatus(int status) throws RemoteException {
        	if (serviceManagerIsBound) {
    			EnumDownloadStatus task = EnumDownloadStatus.reverseOrdinal(status);
				switch (task) {
					case RESTARTING:
					case FAILED:
						try {
							downloadingAdapter.updateList(serviceManager.callGetDownloadsOngoing());
							notDownloadedAdapter.updateList(serviceManager.callGetDownloadsFailed());
						} catch (Exception e) {
							e.printStackTrace();
						}
						break;
	
					case DOWNLOADING:
					case PAUSED:
						try {
							downloadingAdapter.updateList(serviceManager.callGetDownloadsOngoing());
						} catch (Exception e) {
							e.printStackTrace();
						}
						break;
					case RESUMING:
						try {
							downloadingAdapter.updateList(serviceManager.callGetDownloadsOngoing());
						} catch (Exception e) {
							e.printStackTrace();
						}
						break;
					case STOPPED:
						try {
							downloadingAdapter.updateList(serviceManager.callGetDownloadsOngoing());
						} catch (Exception e) {
							e.printStackTrace();
						}
						break;
	
					case COMPLETED:
						try {
							downloadingAdapter.updateList(serviceManager.callGetDownloadsOngoing());
							downloadedAdapter.updateList(serviceManager.callGetDownloadsCompleted());
						} catch (Exception e) {
							e.printStackTrace();
						}
						break;
	
					default:
						break;
				}
			}
        	interfaceTasksHandler.sendEmptyMessage(status);
		}
		
	};
	

	private Handler interfaceTasksHandler = new Handler(){
		
		public void handleMessage(Message msg) {
			switch (EnumDownloadStatus.reverseOrdinal(msg.what)) {
				case RESTARTING:
				case FAILED:
					if (downloadingAdapter.isEmpty()) {
						downloading.setVisibility(View.GONE);
					} else {
						downloading.setVisibility(View.VISIBLE);
					}
					if(downloadedAdapter.isEmpty()){
						downloaded.setVisibility(View.GONE);
					}
					notDownloaded.setVisibility(View.VISIBLE);
					break;
	
				case DOWNLOADING:
				case PAUSED:
				case RESUMING:
				case STOPPED:
					downloading.setVisibility(View.VISIBLE);
					if(notDownloadedAdapter.isEmpty()){
						notDownloaded.setVisibility(View.GONE);
					}
					if(downloadedAdapter.isEmpty()){
						downloaded.setVisibility(View.GONE);
					}
					break;
	
				case COMPLETED:
					if (downloadingAdapter.isEmpty()) {
						downloading.setVisibility(View.GONE);
					} else {
						downloading.setVisibility(View.VISIBLE);
					}
					if(notDownloadedAdapter.isEmpty()){
						notDownloaded.setVisibility(View.GONE);
					}
					downloaded.setVisibility(View.VISIBLE);
					break;
					
				default:
					break;
				}
			
		}
	};
	
	
	private void prePopulateLists(){
		try {
			
			if(serviceManager.callAreDownloadsOngoing()){
				noDownloads.setVisibility(View.GONE);
				downloadingAdapter.updateList(serviceManager.callGetDownloadsOngoing());
//				if(!downloadingAdapter.isEmpty()){
					downloading.setVisibility(View.VISIBLE);
//				}else{
//					downloading.setVisibility(View.GONE);
//				}
			}else{
				downloading.setVisibility(View.GONE);
			}
			if(serviceManager.callAreDownloadsCompleted()){
				noDownloads.setVisibility(View.GONE);
				downloadedAdapter.updateList(serviceManager.callGetDownloadsCompleted());
//				if (!downloadedAdapter.isEmpty()) {
					downloaded.setVisibility(View.VISIBLE);
//				}else{
//					downloaded.setVisibility(View.GONE);
//				}
			}else{
				downloaded.setVisibility(View.GONE);
			}
			if(serviceManager.callAreDownloadsFailed()){
				noDownloads.setVisibility(View.GONE);
				notDownloadedAdapter.updateList(serviceManager.callGetDownloadsFailed());
//				if(!notDownloadedAdapter.isEmpty()){
					notDownloaded.setVisibility(View.VISIBLE);
//				}else{
//					notDownloaded.setVisibility(View.GONE);
//				}
			}else{
				notDownloaded.setVisibility(View.GONE);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		AptoideThemePicker.setAptoideTheme(this);
		super.onCreate(savedInstanceState);
		if(!isRunning){
			isRunning = true;

			if(!serviceManagerIsBound){
	    		bindService(new Intent(this, ServiceDownloadManager.class), serviceManagerConnection, Context.BIND_AUTO_CREATE);
	    	}
			
			setContentView(R.layout.download_manager);
//			getSupportActionBar().setIcon(R.drawable.brand_padding);
//			getSupportActionBar().setTitle(getString(R.string.download_manager));
//			getSupportActionBar().setHomeButtonEnabled(true);
//			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			downloading = (LinearLayout) findViewById(R.id.downloading_apps);
			downloading.setVisibility(View.GONE);
			
			downloaded = (LinearLayout) findViewById(R.id.downloaded_apps);
			downloaded.setVisibility(View.GONE);

			notDownloaded = (LinearLayout) findViewById(R.id.failed_apps);
			notDownloaded.setVisibility(View.GONE);

			exitButton = (Button) findViewById(R.id.exit);
			exitButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});

			noDownloads = (TextView) findViewById(R.id.no_downloads);
			noDownloads.setVisibility(View.VISIBLE);
		}

	}
	
	private void continueLoading(){
		ListView uploadingList = (ListView) findViewById(R.id.downloading_list);
		downloadingAdapter = new DownloadingListAdapter(this, serviceManager, ImageLoader.getInstance());
		uploadingList.setAdapter(downloadingAdapter);
		
		ListView uploadedList = (ListView) findViewById(R.id.downloaded_list);
		downloadedAdapter = new DownloadedListAdapter(this, ImageLoader.getInstance());
		uploadedList.setAdapter(downloadedAdapter);
		
		uploadedList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				try {
					serviceManager.callInstallApp(downloadedAdapter.getItem(position).getCache());
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}

		});
		
		ListView notUploadedList = (ListView) findViewById(R.id.failed_list);
		notDownloadedAdapter = new NotDownloadedListAdapter(this, ImageLoader.getInstance());
		notUploadedList.setAdapter(notDownloadedAdapter);
		notUploadedList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long positionLong) {
				try {
					serviceManager.callRestartDownload(notDownloadedAdapter.getItem(position).hashCode());
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});
		
		prePopulateLists();
		registerForContextMenu(uploadedList);
	}
	
	@Override
	protected void onDestroy() {
		try {
			serviceManager.callUnregisterDownloadManager();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		unbindService(serviceManagerConnection);
		super.onDestroy();
	}
	
	
//	@Override
//	public void onCreateContextMenu(ContextMenu menu, View v,
//			ContextMenuInfo menuInfo) {
//
//		menu.add(0, 0, 0, getString(R.string.export_apk));
//
//		super.onCreateContextMenu(menu, v, menuInfo);
//	}
//
//	@Override
//	public boolean onContextItemSelected(MenuItem item) {
//
//		switch (item.getItemId()) {
//		case 0:
//
//			break;
//
//		default:
//			break;
//		}
//
//		return super.onContextItemSelected(item);
//	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		menu.add(Menu.NONE, 0, 0, R.string.clear_all).setIcon(android.R.drawable.ic_notification_clear_all);
		
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
//		if (item.getItemId() == android.R.id.home) {
//			finish();
//			return true;
//		}
		switch (item.getItemId()) {
		case 0:
			Log.d("Aptoide-DownloadManager", "clear all");
			
			if(downloadedAdapter.getCount()>0 || notDownloadedAdapter.getCount()>0){
				try {
					serviceManager.callClearDownloads();
					downloadedAdapter.updateList(serviceManager.callGetDownloadsCompleted());
					notDownloadedAdapter.updateList(serviceManager.callGetDownloadsFailed());
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			break;
		default:
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
}
