/**
 * 
 */
package cm.aptoide.summerinternship2011.comments;

import java.math.BigInteger;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import cm.aptoide.pt.R;
import cm.aptoide.summerinternship2011.exceptions.EmptyRequestException;
import cm.aptoide.summerinternship2011.exceptions.EndOfRequestReached;
import cm.aptoide.summerinternship2011.exceptions.FailedRequestException;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

/**
 * @author rafael
 * @since summerinternship2011
 * 
 * The scroll listener of the comment list.
 * 
 */
public class LoadOnScrollCommentList implements OnScrollListener {
	
    private final static int visibleThreshold = 6; // The minimum amount of items to have below your current scroll position, before loading more
    private final static int commentsToLoad = 2; //The number of comments to retrieve per fetch
    
    private int currentPage; // The current page of data you have loaded
    private int previousTotal; // The total number of items in the dataset after the last load
    private Boolean loading; // True if we are still waiting for the last set of data to load
    private BigInteger lastCommentIdRead;
    private boolean continueFetching;
    
    private Activity context;
    private CommentsAdapter<Comment> commentList;
    private CommentGetter commentGetter; //Comment xml parser
    private LinearLayout loadingLayout;
    
    private ArrayList<Fetch> pendingFetch;
    
    /**
     * 
     * @param context
     * @param commentList
     * @param repo
     * @param apkid
     * @param version
     * @param loadingLayout
     * @throws SAXException 
     * @throws ParserConfigurationException 
     */
    public LoadOnScrollCommentList(Activity context, CommentsAdapter<Comment> commentList, String repo, 
    								String apkid, String version, LinearLayout loadingLayout) 
    										throws ParserConfigurationException, SAXException {
    	
    	this.context = context;
    	this.commentList = commentList;
    	
    	commentGetter = new CommentGetter(repo, apkid, version);
    	this.loadingLayout = loadingLayout;
    	
    	this.pendingFetch = new ArrayList<Fetch>();
    	
    	reset();
    	
    }
    
    /**
     * 
     */
    public void reset(){
    	
    	currentPage = 0;
    	previousTotal = 0;
    	loading = true;
    	lastCommentIdRead = null;
    	continueFetching = true;
    	
    	((TextView)loadingLayout.findViewById(R.id.loadTextComments)).setText(R.string.loading);
		((ImageView)loadingLayout.findViewById(R.id.loadImageComments)).setImageResource(R.drawable.loading);
		
    }
    
    /**
     * 
     * @param repo
     * @param apkid
     * @param apkversion
     */
    public void resetGetter(String repo, String apkid, String apkversion){
    	commentGetter.reset(repo, apkid, apkversion);
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
			
			//Add this thread to the list of pending threads
			synchronized(pendingFetch){ pendingFetch.add(this); }
			
			// Make sure no one uses commentGetter
			synchronized(commentGetter) {
				
				if(continueFetching && !isCancelled()){
					
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
							} catch(EndOfRequestReached e){}
							
							if(commentGetter.getComments().size()!=0){
								
								lastCommentIdRead = commentGetter.getComments().get(commentGetter.getComments().size()-1).getId();
								return commentGetter.getComments();
								
							} else { 
								
								if(!commentGetter.getStatus().equals(cm.aptoide.summerinternship2011.Status.OK)){
									throw new FailedRequestException("Request could not be executed");
								}else{
									throw new EmptyRequestException("Request empty.");
								}
								
							}
							
		    			}catch (Exception e) { continueFetching = false; }
						
			        }
				           
		    	}
				
			} //Sync end
			
			return null;
			
		}

		@Override
		protected void onPostExecute(ArrayList<Comment> result) {
			
			// Make sure no new comments are added while reseting comment list
			synchronized (pendingFetch){ 
				
				//Check if the thread is canceled
				if(!isCancelled()){ 
					
					if(result != null){
						
						((TextView)loadingLayout.findViewById(R.id.loadTextComments)).setText(R.string.endcomreached);
						((ImageView)loadingLayout.findViewById(R.id.loadImageComments)).setImageBitmap(null);
						
						ArrayList<Comment> comments = commentGetter.getComments();
						for(Comment comment: comments){
							 commentList.add(comment);
						}
						
					}else{
						
						((TextView)loadingLayout.findViewById(R.id.loadTextComments)).setText(R.string.endcomreached);
						((ImageView)loadingLayout.findViewById(R.id.loadImageComments)).setImageBitmap(null);
						
					}
					
				}
			
			 // Remove thread from pending fetch 
			 pendingFetch.remove(this); 
			 
			}
			
		}
		
		@Override
		protected void onCancelled() {
			// Remove thread from pending fetch 
			synchronized (pendingFetch){ pendingFetch.remove(this); }
		}
		
    } //End of Fetch class
    
    /**
     * Fetches new comments and adds it to the beginning of the list.
     * Synchronized due to other AsyncThreads that may interfere in the normal program work flow.
     * This is invoked when trying to fetch the newly created comment.
     * 
     */
    public void fetchNewComments(){
    	
    		synchronized (pendingFetch){
    			
		    	try{
		    		// Make sure no one uses commentGetter
		    		synchronized(commentGetter){
		    			
				    	try{
				    		if(commentList.getCount()!=0){
								commentGetter.parse(context, commentList.getItem(0).getId(), 
										context.getApplicationContext().getSharedPreferences("aptoide_prefs", Context.MODE_PRIVATE).getString("useridLogin", null));
				    		} else {
				    			commentGetter.parse(context, commentsToLoad, lastCommentIdRead, false);
				    		}
						} catch(EndOfRequestReached e){}
						((CommentsAdapter<Comment>)commentList).addAtBegin(commentGetter.getComments());
						
		    		}
		    	
		    	} catch(Exception e)		{}
		    	
		    	cancelAllPendingRequests();
		    	
    		}
    	
	}
    
    public void fetchNewApp(String apk_repo_str_raw,String apk_id,String apk_ver_str_raw){
    	// Make sure no new comments are added while reseting comment list
    	synchronized(pendingFetch){ 
	    	this.reset();
			this.resetGetter(apk_repo_str_raw, apk_id, apk_ver_str_raw);
			commentList.removeAll();
			this.cancelAllPendingRequests();
    	}
    	
    }
    
    /**
     * 
     */
    public void cancelAllPendingRequests(){
		for (Fetch fetch:pendingFetch){
			fetch.cancel(false);
		}
    }
    
    /**
     * 
     */
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    	new Fetch(firstVisibleItem,visibleItemCount,totalItemCount).execute();
    }
    
    /**
     * 
     */
    public void onScrollStateChanged(AbsListView view, int scrollState) {}
 
}