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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cm.aptoide.pt2.R;
import cm.aptoide.pt2.contentloaders.ImageLoader;
import cm.aptoide.pt2.util.quickaction.ActionItem;
import cm.aptoide.pt2.util.quickaction.EnumQuickActions;
import cm.aptoide.pt2.util.quickaction.QuickAction;
import cm.aptoide.pt2.views.EnumDownloadStatus;
import cm.aptoide.pt2.views.ViewDownloadManagement;

public class DownloadingListAdapter extends BaseAdapter{
	private Context context;
	private ImageLoader imageLoader;
	private LayoutInflater layoutInflater;
	
	ActionItem playItem;
	ActionItem pauseItem;
	ActionItem stopItem;
	
	/** ViewDownloadManagemet[] **/
	private Object[] downloading = null;

	/**
	 * DownloadingListAdapter Constructor
	 *
	 * @param context
	 * @param ImageLoader
	 */
	public DownloadingListAdapter(Context context, ImageLoader imageLoader){
		this.context = context;
		this.imageLoader = imageLoader;
		
		playItem = new ActionItem(EnumQuickActions.PLAY.ordinal(), "Resume", context.getResources().getDrawable(R.drawable.ic_media_play));
		pauseItem = new ActionItem(EnumQuickActions.PAUSE.ordinal(), "Pause", context.getResources().getDrawable(R.drawable.ic_media_pause));
		stopItem = new ActionItem(EnumQuickActions.STOP.ordinal(), "Stop", context.getResources().getDrawable(R.drawable.ic_media_stop));

		layoutInflater = LayoutInflater.from(context);
	} 

	
	
	
	
	

	public static class DownloadingRowViewHolder{
		TextView app_name;
		ImageView app_icon;
		ProgressBar app_download_progress;
		TextView app_progress;
		TextView app_speed;
		Button manageDownloadsButton;
				
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		DownloadingRowViewHolder rowViewHolder;

		if(convertView == null){
			convertView = layoutInflater.inflate(R.layout.row_app_downloading, null);

			rowViewHolder = new DownloadingRowViewHolder();
			rowViewHolder.app_name = (TextView) convertView.findViewById(R.id.downloading_name);
			rowViewHolder.app_download_progress = (ProgressBar) convertView.findViewById(R.id.downloading_progress);
			rowViewHolder.app_icon = (ImageView) convertView.findViewById(R.id.downloading_icon);
			rowViewHolder.app_progress = (TextView) convertView.findViewById(R.id.progress);
			rowViewHolder.app_speed = (TextView) convertView.findViewById(R.id.speed);
			rowViewHolder.manageDownloadsButton = (Button) convertView.findViewById(R.id.icon_manage);
			convertView.setTag(rowViewHolder);
		}else{
			rowViewHolder = (DownloadingRowViewHolder) convertView.getTag();
		}

		final ViewDownloadManagement download = (ViewDownloadManagement) downloading[position];

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
		
		
		rowViewHolder.manageDownloadsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				QuickAction actionBar  = new QuickAction(context);
				
				switch (download.getDownloadStatus()) {
					case SETTING_UP:
					case RESTARTING:
					case RESUMING:
						break;
						
					case DOWNLOADING:
						actionBar.addActionItem(pauseItem);
						break;
			
					default:
						actionBar.addActionItem(playItem);
						break;
				}
				actionBar.addActionItem(stopItem);
				actionBar.show(view);
				
				actionBar.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
					@Override
					public void onItemClick(QuickAction quickAction, int pos, int actionId) {
						switch (EnumQuickActions.reverseOrdinal(actionId)) {
							case PLAY:
								getItem(position).resume();
								break;
								
							case PAUSE:
								getItem(position).pause();
								break;
								
							case STOP:
								getItem(position).stop();
								break;
			
							default:
								break;
						}	
					}
				});	
				
			}
		});
		
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
		try {
			return downloading[position].hashCode();
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
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