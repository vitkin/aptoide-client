/*******************************************************************************
 * Copyright (c) 2012 rmateus.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package cm.aptoide.pt.adapters;

import cm.aptoide.com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.database.Cursor;
import android.os.RemoteException;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.views.ViewApk;
import cm.aptoide.pt.views.ViewCache;
import cm.aptoide.pt.views.ViewDownloadManagement;
import cm.aptoide.pt.Category;
import cm.aptoide.pt.Database;
import cm.aptoide.pt.DbStructure;
import cm.aptoide.pt.R;
import cm.aptoide.pt.services.AIDLServiceDownloadManager;

public class UpdatesAdapter extends CursorAdapter {

	private AIDLServiceDownloadManager serviceDownloadManager = null;

	

	public UpdatesAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
	}
	
	/**
	 * @param serviceDownloadManager the serviceDownloadManager to set
	 */
	public void setServiceDownloadManager(
			AIDLServiceDownloadManager serviceDownloadManager) {
		this.serviceDownloadManager = serviceDownloadManager;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		
		View v = LayoutInflater.from(context).inflate(R.layout.app_update_row, null);
		
		return v;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            holder.name = (TextView) view.findViewById(R.id.app_name);
            holder.icon= (ImageView) view.findViewById(R.id.app_icon);
            holder.vername= (TextView) view.findViewById(R.id.uptodate_versionname);
            holder.update = (ImageView) view.findViewById(R.id.app_update);
//            holder.downloads= (TextView) view.findViewById(R.id.downloads);
//            holder.rating= (RatingBar) view.findViewById(R.id.stars);
            view.setTag(holder);
        }
        final long id = cursor.getLong(0);
        final String name = cursor.getString(1);
		final String apkId = cursor.getString(7);
		final String vername = cursor.getString(2);
		final int vercode = cursor.getInt(8);
		final String md5 = cursor.getString(10);
		final String apkpath = cursor.getString(11) + cursor.getString(12);  
		String iconspath = cursor.getString(9)+cursor.getString(4);
		final String hash = (cursor.getString(cursor.getColumnIndex("apkid"))+"|"+cursor.getString(cursor.getColumnIndex("vercode")));
		holder.name.setText(name);
		ImageLoader.getInstance().displayImage(iconspath, holder.icon,hash);
//		 try{
//	        	holder.rating.setRating(Float.parseFloat(cursor.getString(5)));	
//	        }catch (Exception e) {
//	        	holder.rating.setRating(0);
//			}
//		 holder.downloads.setText(cursor.getString(6));
		holder.vername.setText(context.getString(R.string.update_to)+": "+ vername);
		holder.update.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				ViewApk apk = Database.getInstance().getApk(id, Category.INFOXML);
				try {
					serviceDownloadManager.callStartDownload(
							new ViewDownloadManagement(
							apkpath,
							apk,
							new ViewCache(apk.hashCode(), 
									apk.getMd5(),apkId,vername)));
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public static class ViewHolder {
		TextView name;
		TextView vername;
		ImageView icon;
		ImageView update;
		public boolean updateExcluded;
//		RatingBar rating;
//		TextView downloads;
	}

}
