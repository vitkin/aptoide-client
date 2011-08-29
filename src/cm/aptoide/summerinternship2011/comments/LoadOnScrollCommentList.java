/**
 * 
 */
package cm.aptoide.summerinternship2011.comments;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;

import cm.aptoide.pt.R;
import cm.aptoide.summerinternship2011.exceptions.EmptyRequestException;
import cm.aptoide.summerinternship2011.exceptions.FailedRequestException;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

/**
 * 
 * @author rafael
 * @since summerinternship2011
 *
 */
public class LoadOnScrollCommentList implements OnScrollListener {
	
    private final static int visibleThreshold = 3; // The minimum amount of items to have below your current scroll position, before loading more
    private final static int commentsToLoad = 3; //The number of comments to retrieve per fetch
    
    private int currentPage; // The current page of data you have loaded
    private int previousTotal; // The total number of items in the dataset after the last load
    private Boolean loading; // True if we are still waiting for the last set of data to load
    private BigInteger lastCommentIdRead;
    private boolean continueFetching;
    
    private Activity context;
    private ArrayAdapter<Comment> commentList;
    private CommentGetter commentGetter; //Comment xml parser
    private boolean pausedNetwork; // If no internconnection is found
    private boolean stopOnFirstPage; // 
    
    /**
     * 
     * @param context
     * @param commentList
     * @param repo
     * @param apkid
     * @param version
     */
    public LoadOnScrollCommentList(Activity context, ArrayAdapter<Comment> commentList, String repo, String apkid, String version) {
    	
    	this.context = context;
    	this.commentList = commentList;
    	
    	commentGetter = new CommentGetter(repo, apkid, version);
    	
    	reset();
    }
 
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    	new Fetch(firstVisibleItem,visibleItemCount,totalItemCount).execute();
    }
    
    public synchronized void reset(){
    	currentPage = 0;
    	previousTotal = 0;
    	loading = true;
    	lastCommentIdRead = null;
    	continueFetching = true;
    	
    	pausedNetwork = false;
    	stopOnFirstPage = false;
    }
    
    /**
     * @author rafael
     * @since summerinternship2011
     * 
     */
    private class Fetch extends AsyncTask<Void, Void, ArrayList<Comment>> {
		
		private int firstVisibleItem; 
		private int visibleItemCount; 
		private int totalItemCount;
		
		/**
		 * 
		 * @param firstVisibleItem
		 * @param visibleItemCount
		 * @param totalItemCount
		 */
		public Fetch(int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			this.firstVisibleItem = firstVisibleItem; 
    		this.visibleItemCount = visibleItemCount; 
    		this.totalItemCount = totalItemCount;
		}
		
		@Override
		protected ArrayList<Comment> doInBackground(Void... params) {
			synchronized(LoadOnScrollCommentList.this){
				
				if(continueFetching){
			    		
				            if (loading && totalItemCount > previousTotal) {
				                
				            	loading = false;
				                previousTotal = totalItemCount;
				                currentPage++;
				                
				            }
				            
				            if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
					        	loading = true;
					        	
					        	try{
					        		
				    				try{
										commentGetter.parse(context, commentsToLoad, lastCommentIdRead, false);
									} catch(cm.aptoide.summerinternship2011.exceptions.EndOfRequestReached e){}
									
									if(commentGetter.getComments().size()!=0){
										
										lastCommentIdRead = commentGetter.getComments().get(commentGetter.getComments().size()-1).getId();
										return commentGetter.getComments();
										
									} else { 
										if(!commentGetter.getStatus().equals(cm.aptoide.summerinternship2011.Status.OK))
											throw new FailedRequestException("Request could not be executed");
										else
											throw new EmptyRequestException("Request empty.");
									}
									
				    			}catch(IOException e){
				    				pausedNetwork = true;
				    				if(currentPage==1) 
				    					stopOnFirstPage = true;
				    				continueFetching = false;
				    			}catch (Exception e) {
				    				if(currentPage==1) 
				    					stopOnFirstPage = true;
				    				continueFetching = false;
									//FailedRequestException && EmptyRequestException  && SAXException && 
				    				//&& ParserConfigurationException && FactoryConfigurationError
								}
								
					        }
				           
		    	}
			} //Sync end
			return null;
			
		}

		@Override
		protected void onPostExecute(ArrayList<Comment> result) {
			if(result != null){
				if(pausedNetwork) commentsCanBeLoaded();
				for(Comment comment: commentGetter.getComments()){
					synchronized(commentList){ commentList.add(comment); }
				}
			}else{
				commentsCouldNotBeLoaded();
			}
		}	
    	
		private void commentsCouldNotBeLoaded(){
			
			if(stopOnFirstPage)
				((TextView)((ListView)context.findViewById(R.id.listComments)).findViewById(R.id.commentsLabel)).setText(context.getString(R.string.comments_unavailable));	
		}
		
		private void commentsCanBeLoaded(){
			((TextView)((ListView)context.findViewById(R.id.listComments)).findViewById(R.id.commentsLabel)).setText(context.getString(R.string.commentlabel));
			pausedNetwork = false;
		}
		
    }
    
    public synchronized void fetchNewComments(){
    	
    	if(commentList.getCount()!=0){
	    	try{
		    	try{
		    		Comment comment = commentList.getItem(0);
					commentGetter.parse(context, commentList.getItem(0).getId());
				} catch(cm.aptoide.summerinternship2011.exceptions.EndOfRequestReached e){}
				synchronized(commentList){
					((CommentsAdapter<Comment>)commentList).addAtBegin(commentGetter.getComments());
				}
	    	}catch(Exception e){}
    	}
	}
    
    public void onScrollStateChanged(AbsListView view, int scrollState) {}
 
}