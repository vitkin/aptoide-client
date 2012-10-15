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

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt2.R;
import cm.aptoide.pt2.contentloaders.ImageLoader;
import cm.aptoide.pt2.views.ViewDownloadManagement;

public class DownloadedListAdapter extends BaseAdapter{

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
	public DownloadedListAdapter(Context context, ImageLoader imageLoader){
		this.imageLoader = imageLoader;

		layoutInflater = LayoutInflater.from(context);

	} 

	public static class DownloadingRowViewHolder{
		TextView app_name;
		ImageView app_icon;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		DownloadingRowViewHolder rowViewHolder;

		if(convertView == null){
			convertView = layoutInflater.inflate(R.layout.row_app_downloaded, null);

			rowViewHolder = new DownloadingRowViewHolder();
			rowViewHolder.app_name = (TextView) convertView.findViewById(R.id.downloaded_name);
			rowViewHolder.app_icon = (ImageView) convertView.findViewById(R.id.downloaded_icon);
			convertView.setTag(rowViewHolder);
		}else{
			rowViewHolder = (DownloadingRowViewHolder) convertView.getTag();
		}

		ViewDownloadManagement download = (ViewDownloadManagement) downloaded[position];

		rowViewHolder.app_name.setText(download.getAppInfo().getName()+"  "+download.getAppInfo().getVername());
		imageLoader.DisplayImage(download.getCache().getIconPath(), rowViewHolder.app_icon);

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