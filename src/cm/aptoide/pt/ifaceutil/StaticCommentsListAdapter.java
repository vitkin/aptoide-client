/**
 * StaticCommentsListAdapter,		part of Aptoide's data model
 * Copyright (C) 2011  Duarte Silveira
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

package cm.aptoide.pt.ifaceutil;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import cm.aptoide.pt.EnumAppInfoTasks;
import cm.aptoide.pt.R;
import cm.aptoide.pt.data.AIDLAptoideServiceData;
import cm.aptoide.pt.data.display.ViewDisplayComment;
import cm.aptoide.pt.data.display.ViewDisplayListComments;
import cm.aptoide.pt.data.util.Constants;

 /**
 * StaticCommentsListAdapter, models a static loading, Comments list adapter
 * 									extends baseAdapter
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class StaticCommentsListAdapter extends BaseAdapter{

//	private ListView listView;
	private LayoutInflater layoutInflater;

	private ViewDisplayListComments comments = null;
//	private int freshVersion = Constants.EMPTY_INT;
	
//	private CommentsManager commentsManager;
	
//	private HashMap<Integer, ViewDisplayListComments> appVersionsComments;
	
//	private AIDLAptoideServiceData serviceDataCaller = null;
	
//	private Handler aptoideTasksHandler;

	
//	private Handler interfaceTasksHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//        	EnumAppInfoTasks task = EnumAppInfoTasks.reverseOrdinal(msg.what);
//        	switch (task) {
//				
//				case UPDATE_APP_COMMENTS:
//					resetDisplay();
//					break;
//	
//				default:
//					break;
//			}
//        }
//    };
    
    

//    private class CommentsManager{
//    	private ExecutorService commentsColectorsPool;
//    	
//    	public CommentsManager(){
//    		commentsColectorsPool = Executors.newSingleThreadExecutor();
//    	}
//    	
//    	public void reset(int appFullHashid){
//        	commentsColectorsPool.execute(new GetComments(appFullHashid));
//        }
//    	
//    	private class GetComments implements Runnable{
//    		private int appFullHashid;
//
//			public GetComments(int appFullHashid) {
//				this.appFullHashid = appFullHashid;
//			}
//
//			@Override
//			public void run() {
////				aptoideTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.SWITCH_INSTALLED_TO_PROGRESSBAR.ordinal());
//				try {
//					setFreshComments(appFullHashid, serviceDataCaller.callGetVersionComments(appFullHashid));
//					interfaceTasksHandler.sendEmptyMessage(EnumAppInfoTasks.UPDATE_APP_COMMENTS.ordinal());
//				} catch (RemoteException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//
//			}
//    		
//    	}
//    }
	
	
	
	public static class CommentsRowViewHolder{
		TextView author_name;
		TextView date;
		
		TextView subject;
		TextView body;
	}
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		CommentsRowViewHolder rowViewHolder;
		
		if(convertView == null){
			convertView = layoutInflater.inflate(R.layout.row_app_comments, null);
			
			rowViewHolder = new CommentsRowViewHolder();
			rowViewHolder.author_name = (TextView) convertView.findViewById(R.id.author_name);
			rowViewHolder.date = (TextView) convertView.findViewById(R.id.date);
			rowViewHolder.subject = (TextView) convertView.findViewById(R.id.subject);
			rowViewHolder.body = (TextView) convertView.findViewById(R.id.body);
			
			convertView.setTag(rowViewHolder);
		}else{
			rowViewHolder = (CommentsRowViewHolder) convertView.getTag();
		}
		
		rowViewHolder.author_name.setText(comments.get(position).getUserName());
		rowViewHolder.date.setText(comments.get(position).getTimestamp());

		rowViewHolder.subject.setText(comments.get(position).getSubject());
		rowViewHolder.body.setText(comments.get(position).getBody());
		
		
		return convertView;
	}
	
	@Override
	public int getCount() {
		return comments.size();
	}

	@Override
	public ViewDisplayComment getItem(int position) {
		return comments.get(position);
	}

	@Override
	public long getItemId(int position) {
		return comments.get(position).getCommentId();
	}
	
	
	/**
	 * StaticInstalledAppsListAdapter Constructor
	 *
	 * @param context
	 * @param textViewResourceId
	 */
	public StaticCommentsListAdapter(Context context, ViewDisplayListComments comments){//ListView listView, AIDLAptoideServiceData serviceDataCaller) {
		
//		this.serviceDataCaller = serviceDataCaller;
//		this.aptoideTasksHandler = aptoideTasksHandler;

//		comments = new ViewDisplayListComments();

//		commentsManager = new CommentsManager();
		
//		appVersionsComments = new HashMap<Integer, ViewDisplayListComments>();


//		this.listView = listView;
		layoutInflater = LayoutInflater.from(context);
		this.comments = comments;
	} 
	
	
	
//	public void resetDisplayComments(int appFullHashid){
//		comments.clear();
//		refreshDisplayComments();
//		if(appVersionsComments.containsKey(appFullHashid)){
//			this.freshVersion = appFullHashid;
//			resetDisplay();
//		}else{
//			commentsManager.reset(appFullHashid);
//		}
//	}
	
//	public void resetDisplayComments(ViewDisplayListComments comments){
//		this.comments = comments;
//		initDisplay();
//		refreshDisplayComments();
//	}
	
//	public void refreshDisplayComments(){
//		notifyDataSetChanged();
//	}
	
//	public void shutdownNow(){
//		commentsManager.commentsColectorsPool.shutdownNow();
//	}
	
	
	
//    private void initDisplay(){
//		listView.setAdapter(this);    	
//    }
	
//	private synchronized void setFreshComments(int appFullHashid, ViewDisplayListComments freshComments){
//		this.freshVersion = appFullHashid;
//		appVersionsComments.put(appFullHashid, freshComments);
//	}
	
//	private void resetDisplay(){
//		if(freshVersion == Constants.EMPTY_INT){
////			aptoideTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.SWITCH_INSTALLED_TO_NO_APPS.ordinal());
//		}else{
////			aptoideTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.SWITCH_INSTALLED_TO_LIST.ordinal());
//		
//	    	this.comments = appVersionsComments.get(freshVersion);
//			Log.d("Aptoide-StaticCommentsListAdapter", "new CommentsList: "+getCount());
//	   		initDisplay();
//	    	refreshDisplayComments();
//	    	
////	    	aptoideTasksHandler.sendEmptyMessage(EnumAptoideInterfaceTasks.RESET_UPDATABLE_LIST_DISPLAY.ordinal());
//		}
//	}
	
}
