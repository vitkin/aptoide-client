/*
 * DownloadingListAdapter, part of appsBackup
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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cm.aptoide.pt2.R;
import cm.aptoide.pt2.contentloaders.ImageLoader;
import cm.aptoide.pt2.views.ViewDownloadManagement;

public class DownloadingListAdapter extends BaseAdapter{

	private ImageLoader imageLoader;
	
	private LayoutInflater layoutInflater;

	/** ViewDownloadManagemet[] **/
	private Object[] downloading = null;

	/**
	 * DownloadingListAdapter Constructor
	 *
	 * @param context
	 * @param ImageLoader
	 */
	public DownloadingListAdapter(Context context, ImageLoader imageLoader){
		this.imageLoader = imageLoader;

		layoutInflater = LayoutInflater.from(context);

	} 

	public static class DownloadingRowViewHolder{
		TextView app_name;
		ImageView app_icon;
		ProgressBar app_download_progress;
		TextView app_progress;
		TextView app_speed;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		DownloadingRowViewHolder rowViewHolder;

		if(convertView == null){
			convertView = layoutInflater.inflate(R.layout.row_app_downloading, null);

			rowViewHolder = new DownloadingRowViewHolder();
			rowViewHolder.app_name = (TextView) convertView.findViewById(R.id.downloading_name);
			rowViewHolder.app_download_progress = (ProgressBar) convertView.findViewById(R.id.downloading_progress);
			rowViewHolder.app_icon = (ImageView) convertView.findViewById(R.id.downloading_icon);
			rowViewHolder.app_progress = (TextView) convertView.findViewById(R.id.progress);
			rowViewHolder.app_speed = (TextView) convertView.findViewById(R.id.speed);
			convertView.setTag(rowViewHolder);
		}else{
			rowViewHolder = (DownloadingRowViewHolder) convertView.getTag();
		}

		ViewDownloadManagement download = (ViewDownloadManagement) downloading[position];

		rowViewHolder.app_name.setText(download.getAppInfo().getName()+"  "+download.getAppInfo().getVername());
		rowViewHolder.app_progress.setText(download.getProgressString());
		rowViewHolder.app_speed.setText(download.getSpeedInKBpsString());
		if(download.getProgress() != 0 && download.getProgress() < 99){
			rowViewHolder.app_download_progress.setIndeterminate(false);
			rowViewHolder.app_download_progress.setMax(100);
		}else{
			rowViewHolder.app_download_progress.setIndeterminate(true);
		}
		rowViewHolder.app_download_progress.setProgress(download.getProgress());
		imageLoader.DisplayImage(download.getCache().getIconPath(), rowViewHolder.app_icon);

		return convertView;
	}


	@Override
	public int getCount() {
		if(downloading != null){
			return downloading.length;
		}else{
			return 0;
		}
	}

	@Override
	public ViewDownloadManagement getItem(int position) {
		return (ViewDownloadManagement) downloading[position];
	}

	@Override
	public long getItemId(int position) {
		return downloading[position].hashCode();
	}

	/**
	 * 
	 * @param updatedList ViewDownloadManagement[] (uncasted)
	 */
	public void updateList(Object[] updatedList){
		downloading = updatedList;
		notifyDataSetChanged();
	}
	
}