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
package cm.aptoide.pt2;

import cm.aptoide.pt2.adapters.DownloadedListAdapter;
import cm.aptoide.pt2.adapters.DownloadingListAdapter;
import cm.aptoide.pt2.adapters.NotDownloadedListAdapter;
import cm.aptoide.pt2.contentloaders.ImageLoader;
import cm.aptoide.pt2.views.EnumDownloadProgressUpdateMessages;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * DownloadManager
 *
 * @author dsilveira
 *
 */
public class DownloadManager extends Activity {
	private ImageLoader imageLoader;
	
	private LinearLayout downloading;
	private DownloadingListAdapter downloadingAdapter;
	
	private LinearLayout downloaded;
	private DownloadedListAdapter downloadedAdapter;
	
	private LinearLayout notDownloaded;
	private NotDownloadedListAdapter notDownloadedAdapter;

	private Button exitButton;
	
	private Handler updatesHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
    		ApplicationServiceManager serviceManager = ((ApplicationServiceManager)getApplication());
        	EnumDownloadProgressUpdateMessages task = EnumDownloadProgressUpdateMessages.reverseOrdinal(msg.what);
        	switch (task) {
	
				case FAILED:
					notDownloadedAdapter.updateList(serviceManager.getDownloadsFailed());
					if(notDownloadedAdapter.isEmpty()){
						notDownloaded.setVisibility(View.GONE);
					}else{
						notDownloaded.setVisibility(View.VISIBLE);
					}
					break;

				case UPDATE:
				case PAUSED:
				case RESUMING:
				case STOPPED:
					downloadingAdapter.updateList(serviceManager.getDownloadsOngoing());
					downloading.setVisibility(View.VISIBLE);
					break;
					
				case COMPLETED:
					downloadingAdapter.updateList(serviceManager.getDownloadsOngoing());
					downloadedAdapter.updateList(serviceManager.getDownloadsCompleted());
					if(downloadingAdapter.isEmpty()){
						downloading.setVisibility(View.GONE);
					}else{
						downloading.setVisibility(View.VISIBLE);
					}
					downloaded.setVisibility(View.VISIBLE);
					break;
					
				default:
					break;
				}
        }
	};
	
	private void prePopulateLists(){
		ApplicationServiceManager serviceManager = ((ApplicationServiceManager)getApplication());
		if(serviceManager.areDownloadsOngoing()){
			downloadingAdapter.updateList(serviceManager.getDownloadsOngoing());
			downloading.setVisibility(View.VISIBLE);
		}
		if(serviceManager.areDownloadsCompleted()){
			downloadedAdapter.updateList(serviceManager.getDownloadsCompleted());
			downloaded.setVisibility(View.VISIBLE);
		}
		if(serviceManager.areDownloadsFailed()){
			notDownloadedAdapter.updateList(serviceManager.getDownloadsFailed());
			notDownloaded.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.download_manager);
		imageLoader = new ImageLoader(this);
		
		downloading = (LinearLayout) findViewById(R.id.downloading_apps);
		ListView uploadingList = (ListView) findViewById(R.id.downloading_list);
		downloadingAdapter = new DownloadingListAdapter(this, imageLoader);
		uploadingList.setAdapter(downloadingAdapter);
		
		
		downloaded = (LinearLayout) findViewById(R.id.downloaded_apps);
		ListView uploadedList = (ListView) findViewById(R.id.downloaded_list);
		downloadedAdapter = new DownloadedListAdapter(this, imageLoader);
		uploadedList.setAdapter(downloadedAdapter);
		downloaded.setVisibility(View.GONE);
		
		notDownloaded = (LinearLayout) findViewById(R.id.failed_apps);
		ListView notUploadedList = (ListView) findViewById(R.id.failed_list);
		notDownloadedAdapter = new NotDownloadedListAdapter(this, imageLoader);
		notUploadedList.setAdapter(notDownloadedAdapter);
//		notUploadedList.setOnItemClickListener(new OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
//				
//			}
//		});
		notDownloaded.setVisibility(View.GONE);

		exitButton = (Button) findViewById(R.id.exit);
		exitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		  });
		
		((ApplicationServiceManager)getApplication()).registerDownloadManager(updatesHandler);
		
		prePopulateLists();
		
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onDestroy() {
		((ApplicationServiceManager)getApplication()).unregisterDownloadManager();
		updatesHandler = null;
		super.onDestroy();
	}
	
}
