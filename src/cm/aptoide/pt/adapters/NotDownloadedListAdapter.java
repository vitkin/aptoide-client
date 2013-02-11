/*
 * NotDownloadedListAdapter, part of appsBackup
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

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.views.ViewDownloadManagement;
import cm.aptoide.pt.views.ViewListDownloads;
import cm.aptoide.pt.R;

public class NotDownloadedListAdapter extends BaseAdapter{

	private Context context;
	private ImageLoader imageLoader;
	
	private LayoutInflater layoutInflater;

	private ViewListDownloads notDownloaded = null;
	private ViewListDownloads updated = null;
	
	
	private Handler updateListHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	notDownloaded = updated;
    		notifyDataSetChanged();        	
        }
	};

	/**
	 * NotDownloadedListAdapter Constructor
	 *
	 * @param context
	 * @param ImageLoader
	 */
	public NotDownloadedListAdapter(Context context, ImageLoader imageLoader2){
		this.context = context;
		this.imageLoader = imageLoader2;

		layoutInflater = LayoutInflater.from(context);

	} 

	public static class NotUploadedRowViewHolder{
		TextView app_name;
		ImageView app_icon;
		TextView failed_status;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		NotUploadedRowViewHolder rowViewHolder;

		if(convertView == null){
			convertView = layoutInflater.inflate(R.layout.row_app_not_downloaded, null);

			rowViewHolder = new NotUploadedRowViewHolder();
			rowViewHolder.app_name = (TextView) convertView.findViewById(R.id.app_name);
			rowViewHolder.app_icon = (ImageView) convertView.findViewById(R.id.app_icon);
			rowViewHolder.failed_status = (TextView) convertView.findViewById(R.id.failed_status);
			convertView.setTag(rowViewHolder);
		}else{
			rowViewHolder = (NotUploadedRowViewHolder) convertView.getTag();
		}
		
		ViewDownloadManagement download = notDownloaded.get(position);

		rowViewHolder.app_name.setText(download.getAppInfo().getName()+"  "+download.getAppInfo().getVername());
		String iconUrl = download.getAppInfo().getIcon();
		imageLoader.displayImage(iconUrl, rowViewHolder.app_icon, (download.getAppInfo().getApkid()+"|"+download.getAppInfo().getVercode()));
		rowViewHolder.failed_status.setText(download.getDownload().getFailReason().toString(context));

		return convertView;
	}


	@Override
	public int getCount() {
		if(notDownloaded != null){
			return notDownloaded.size();
		}else{
			return 0;
		}
	}

	@Override
	public ViewDownloadManagement getItem(int position) {
		return notDownloaded.get(position);
	}

	@Override
	public long getItemId(int position) {
		return notDownloaded.get(position).hashCode();
	}

	/**
	 * 
	 * @param updatedList ViewListDownloads
	 */
	public void updateList(ViewListDownloads updatedList){
		updated = updatedList;
		updateListHandler.sendEmptyMessage(0);
	}
	
	public void clearAll(){
		notDownloaded.clear();
	}
	
}