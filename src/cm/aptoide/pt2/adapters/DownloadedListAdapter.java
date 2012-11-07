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

		ViewDownloadManagement download = (ViewDownloadManagement) downloaded[position];

		rowViewHolder.app_name.setText(download.getAppInfo().getName()+"  "+download.getAppInfo().getVername());
		imageLoader.DisplayImage(download.getCache().getIconPath(), rowViewHolder.app_icon);
		
		final String shareAppName = download.getAppInfo().getName()+"  "+download.getAppInfo().getVername();
		final String sharePicture = download.getAppInfo().getIconPath();
		
		rowViewHolder.app_facebook_share.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				String facebookShareName = shareAppName;
				String facebookSharePicture = sharePicture;
//				String facebookShareText = shareText;
//				String facebookShareLink = shareLink;  
//				String facebookShareStore = shareStore;

				Log.d("Aptoide-sharing", "NameToPost: "+facebookShareName+", IconToPost: "+facebookSharePicture);
				
				final DialogShareOnFacebook shareFacebook = new DialogShareOnFacebook(activity, facebookShareName, facebookSharePicture);

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