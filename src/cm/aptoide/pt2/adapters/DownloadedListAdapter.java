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
package cm.aptoide.pt2.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt2.Database;
import cm.aptoide.pt2.R;
import cm.aptoide.pt2.contentloaders.ImageLoader;
import cm.aptoide.pt2.sharing.DialogShareOnFacebook;
import cm.aptoide.pt2.views.ViewDownloadManagement;

public class DownloadedListAdapter extends BaseAdapter{

	private Activity activity;
	
	private ImageLoader imageLoader;
	
	private LayoutInflater layoutInflater;

	/** ViewDownloadManagemet[] **/
	private Object[] downloaded = null;


	/**
	 * DownloadedListAdapter Constructor
	 *
	 * @param context
	 * @param ImageLoader
	 */
	public DownloadedListAdapter(Activity activity, ImageLoader imageLoader){
		this.activity = activity;
		
		this.imageLoader = imageLoader;

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
		
		public String getShareAppName() {
			return shareAppName;
		}
		public void setShareAppName(String shareAppName) {
			this.shareAppName = shareAppName;
		}
		public String getShareIcon() {
			return shareIcon;
		}
		public void setShareIcon(String shareIcon) {
			this.shareIcon = shareIcon;
		}
		public String getShareMessage() {
			return shareMessage;
		}
		public void setShareMessage(String shareMessage) {
			this.shareMessage = shareMessage;
		}
		public String getShareStore() {
			return shareStore;
		}
		public void setShareStore(String shareStore) {
			this.shareStore = shareStore;
		}
		public String getShareDescription() {
			return shareDescription;
		}
		public void setShareDescription(String shareDescription) {
			this.shareDescription = shareDescription;
		}
		public String getShareStoreLink() {
			return shareStoreLink;
		}
		public void setShareStoreLink(String shareStoreLink) {
			this.shareStoreLink = shareStoreLink;
		}
		
		
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final DownloadingRowViewHolder rowViewHolder;

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

		ViewDownloadManagement download = (ViewDownloadManagement) downloaded[position];

		rowViewHolder.app_name.setText(download.getAppInfo().getName()+"  "+download.getAppInfo().getVername());
		imageLoader.DisplayImage(download.getCache().getIconPath(), rowViewHolder.app_icon);
		
		
		rowViewHolder.setShareAppName(download.getAppInfo().getName()+"  "+download.getAppInfo().getVername());
//		rowViewHolder.setShareIcon(download.getAppInfo().getIconPath());
		rowViewHolder.setShareIcon("http://cdn1.aptoide.com/imgs/e/f/2/ef2eb8e0a7a5803b868a0c13c99026e9.png");
		rowViewHolder.setShareMessage("I downloaded "+rowViewHolder.getShareAppName()+" for Android to install on my phone!");
		
		rowViewHolder.setShareStore(Database.getInstance(activity).getStoreName(download.getAppInfo().getRepo_id()));
		if(rowViewHolder.getShareStore()==null){
			rowViewHolder.setShareDescription("Visit Aptoide and install the best apps");
			rowViewHolder.setShareStoreLink("http://www.aptoide.com/more/topapps");
		}else{
			rowViewHolder.setShareDescription("Visit "+rowViewHolder.getShareStore()+" Android Store to download and install this app");
			rowViewHolder.setShareStoreLink("http://"+rowViewHolder.getShareStore()+".store.aptoide.com");
		}
		rowViewHolder.app_facebook_share.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				String facebookShareName = rowViewHolder.getShareAppName();
				String facebookShareIcon = rowViewHolder.getShareIcon();
				String facebookShareText = rowViewHolder.getShareMessage();
				String facebookShareDescription = rowViewHolder.getShareDescription();
				String facebookShareStoreLink = rowViewHolder.getShareStoreLink();

				Log.d("Aptoide-sharing", "NameToPost: "+facebookShareName+", IconToPost: "+facebookShareIcon +", DescriptionToPost: "+facebookShareDescription+", MessageToPost: "+facebookShareText+", StoreLinkToPost: "+facebookShareStoreLink);
				
				final DialogShareOnFacebook shareFacebook = new DialogShareOnFacebook(activity, facebookShareName, facebookShareIcon, facebookShareText, facebookShareDescription, facebookShareStoreLink);

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
			return downloaded.length;
		}else{
			return 0;
		}
	}

	@Override
	public ViewDownloadManagement getItem(int position) {
		return (ViewDownloadManagement) downloaded[position];
	}

	@Override
	public long getItemId(int position) {
		return downloaded[position].hashCode();
	}
	
	/**
	 * 
	 * @param updatedList ViewDownloadManagement[] (uncasted)
	 */
	public void updateList(Object[] updatedList){
		downloaded = updatedList;
		notifyDataSetChanged();
	}

	
}