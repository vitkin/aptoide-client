/*
 * DownloadedListAdapter, part of appsBackup
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
package cm.aptoide.pt.adapters;

import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.sharing.DialogShareOnFacebook;
import cm.aptoide.pt.views.ViewDownloadManagement;
import cm.aptoide.pt.views.ViewListDownloads;
import cm.aptoide.pt.R;

public class DownloadedListAdapter extends BaseAdapter{

	private Activity activity;
	
	private ImageLoader imageLoader;
	
	private LayoutInflater layoutInflater;

	private ViewListDownloads downloaded = null;
	private ViewListDownloads updated = null;
	

	private Handler updateListHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	downloaded = updated;
    		notifyDataSetChanged();        	
        }
	};

	/**
	 * DownloadedListAdapter Constructor
	 *
	 * @param context
	 * @param ImageLoader
	 */
	public DownloadedListAdapter(Activity activity, ImageLoader imageLoader2){
		this.activity = activity;
		
		this.imageLoader = imageLoader2;

		layoutInflater = LayoutInflater.from(activity);

	} 

	public static class DownloadingRowViewHolder{
		TextView app_name;
		ImageView app_icon;
		ImageView app_facebook_share;
		
		String shareAppName;
		String shareIcon;
		String shareMessage;
		String shareStore; 
		String shareDescription;
		String shareStoreLink;
				
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		DownloadingRowViewHolder rowViewHolder;

		if(convertView == null){
			convertView = layoutInflater.inflate(R.layout.row_app_downloaded, null);
			
			rowViewHolder = new DownloadingRowViewHolder();
			rowViewHolder.app_name = (TextView) convertView.findViewById(R.id.downloaded_name);
			rowViewHolder.app_icon = (ImageView) convertView.findViewById(R.id.downloaded_icon);
			rowViewHolder.app_facebook_share = (ImageView) convertView.findViewById(R.id.downloaded_facebook_share);
			convertView.setTag(rowViewHolder);
		}else{
			rowViewHolder = (DownloadingRowViewHolder) convertView.getTag();
		}

		final ViewDownloadManagement download = downloaded.get(position);
		
		rowViewHolder.app_name.setText(download.getAppInfo().getName()+"  "+download.getAppInfo().getVername());
		imageLoader.displayImage(download.getCache().getIconPath(), rowViewHolder.app_icon, (download.getAppInfo().getApkid()+"|"+download.getAppInfo().getVercode()).hashCode()+"");
		
		rowViewHolder.app_facebook_share.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				String facebookShareName = download.getAppInfo().getName()+"  "+download.getAppInfo().getVername();
				String facebookShareIcon = download.getAppInfo().getIcon();
				String facebookShareMessage = "I downloaded "+facebookShareName+" for Android to install on my phone!";
				String facebookShareDescription;
				String facebookShareStoreLink;
				if(download.getAppInfo().getRepoName().equals("Aptoide")){
					facebookShareDescription = "Visit Aptoide and install the best apps";
					facebookShareStoreLink = "http://www.aptoide.com/more/topapps";
				}else{
					facebookShareDescription = "Visit "+download.getAppInfo().getRepoName()+" Android Store to download and install this app";
					facebookShareStoreLink = "http://"+download.getAppInfo().getRepoName()+".store.aptoide.com";
				}
				
				Log.d("Aptoide-sharing", "NameToPost: "+facebookShareName+", IconToPost: "+facebookShareIcon +", DescriptionToPost: "+facebookShareDescription+", MessageToPost: "+facebookShareMessage+", StoreLinkToPost: "+facebookShareStoreLink);
				
				final DialogShareOnFacebook shareFacebook = new DialogShareOnFacebook(activity, facebookShareName, facebookShareIcon, facebookShareMessage, facebookShareDescription, facebookShareStoreLink);

				shareFacebook.setOnDismissListener(new OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialog) {
						shareFacebook.dismiss();
					}
				});
				
				shareFacebook.show();
		    }
		});
		
		return convertView;
	}


	@Override
	public int getCount() {
		if(downloaded != null){
			return downloaded.size();
		}else{
			return 0;
		}
	}

	@Override
	public ViewDownloadManagement getItem(int position) {
		return downloaded.get(position);
	}

	@Override
	public long getItemId(int position) {
		return downloaded.get(position).hashCode();
	}
	
	/**
	 * 
	 * @param updatedList ViewListDownloads
	 */
	public void updateList(ViewListDownloads updatedList){
		updated = updatedList;
		updateListHandler.sendEmptyMessage(0);
	}

	
}