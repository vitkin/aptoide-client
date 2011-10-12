package cm.aptoide.pt.webservices.comments;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import cm.aptoide.pt.Configs;
import cm.aptoide.pt.R;
import cm.aptoide.pt.webservices.exceptions.EmptyRequestException;
import cm.aptoide.pt.webservices.exceptions.EndOfRequestReachedSAXException;
import cm.aptoide.pt.webservices.exceptions.FailedRequestSAXException;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

/**
 * @author rafael
 * @since 2.5.3
 * 
 * The scroll listener of the comment list.
 */
public class CommentPosterListOnScrollListener implements OnScrollListener {
    
    private int currentPage; // The current page of data you have loaded
    private int previousTotal; // The total number of items in the dataset after the last load
    private Boolean loading; // True if we are still waiting for the last set of data to load
    private BigInteger lastCommentIdRead;
    
    private Activity context;
    private CommentsAdapter<Comment> commentList;
    private CommentGetter commentGetter; //Comment xml parser
    
    private ArrayList<Fetch> pendingFetch;
    private GifView load;
    
    private AtomicBoolean stoped;
    private TextView loadingText;
    
    private AtomicBoolean isRequestRunning;
    
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
    public CommentPosterListOnScrollListener(Activity context, CommentsAdapter<Comment> commentList, String repo, 
    								String apkid, String version, LinearLayout loadingLayout) 
    										throws ParserConfigurationException, SAXException {
    	
    	this.context = context;
    	this.commentList = commentList;
    	
    	commentGetter = new CommentGetter(repo, apkid, version);
    	
    	this.pendingFetch = new ArrayList<Fetch>();
    	
    	load = ((GifView)loadingLayout.findViewById(R.id.loadImageComments));
    	load.startAnimation(R.drawable.loading, 40);
    	loadingText = ((TextView)loadingLayout.findViewById(R.id.loadTextComments));
    	
    	reset();
    	
    }
    
    /**
     * 
     */
    public void reset(){
    	
    	currentPage 		= 0;
    	previousTotal 		= 0;
    	loading 			= true;
    	lastCommentIdRead 	= null;
    	stoped 				= new AtomicBoolean(false);
    	isRequestRunning	= new AtomicBoolean(false);
    	updateInterface.sendEmptyMessage(0);
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
			
			Thread.currentThread().setName("FetchCommentsTask");
			
			//Add this thread to the list of pending threads
			synchronized(pendingFetch){ pendingFetch.add(this); }
			
			// Make sure no one uses commentGetter
			synchronized(commentGetter) {
				
				if(!isCancelled()){
					
		            if (loading && totalItemCount > previousTotal) {
		                
		            	loading = false;
		                previousTotal = totalItemCount;
		                currentPage++;
		                
		            }
		            
		            if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + Configs.VISIBLE_THRESHOLD_COMMENTS)) {
			        	loading = true;
			        	
			        	try{
			        		
		    				try{
								commentGetter.parse(context, Configs.VISIBLE_THRESHOLD_COMMENTS, lastCommentIdRead, false);
							} catch(EndOfRequestReachedSAXException e){}
							
							if(commentGetter.getComments().size()!=0){
								
								lastCommentIdRead = commentGetter.getComments().get(commentGetter.getComments().size()-1).getId();
								return commentGetter.getComments();
								
							} else { 
								
								if(!commentGetter.getStatus().equals(cm.aptoide.pt.webservices.EnumResponseStatus.OK)){
									throw new FailedRequestSAXException("Request could not be executed");
								}else{
									throw new EmptyRequestException("Request empty.");
								}
								
							}
							
		    			}catch (Exception e) { 
		    				stoped.set(true);
		    			}
						
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
						
						ArrayList<Comment> comments = commentGetter.getComments();
						for(Comment comment: comments){
							 commentList.add(comment);
						}
						
					} else {
							
						loadingText.getLayoutParams().height = 0;
						loadingText.setText("");
						load.stopAnimation();
						
					}
					
				}
			
			 // Remove thread from pending fetch 
			 pendingFetch.remove(this); 
			 
			}
			isRequestRunning.set(false);
		}
		
		@Override
		protected void onCancelled() {
			// Remove thread from pending fetch 
			synchronized (pendingFetch){ pendingFetch.remove(this); }
			isRequestRunning.set(false);
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
										context.getApplicationContext().getSharedPreferences("aptoide_prefs", Context.MODE_PRIVATE)
										.getString(Configs.LOGIN_USER_ID, null));
				    		} else {
				    			commentGetter.parse(context, Configs.COMMNETS_TO_LOAD, lastCommentIdRead, false);
				    		}
						} catch(EndOfRequestReachedSAXException e){}
						
						((CommentsAdapter<Comment>)commentList).addAtBegin(commentGetter.getComments());
						
		    		}
		    		
		    		cancelAllPendingRequests();
		    		
		    	} catch(Exception e)		{}
		    	
    		}
    	
	}
    
    /**
     * 
     * @param apk_repo_str_raw
     * @param apk_id
     * @param apk_ver_str_raw
     */
    public void fetchNewApp(String apk_repo_str_raw,String apk_id,String apk_ver_str_raw){
    	// Make sure no new comments are added while reseting comment list
    	synchronized(commentGetter){
	    	synchronized(pendingFetch){
				this.resetGetter(apk_repo_str_raw, apk_id, apk_ver_str_raw);
				commentList.removeAll();
				this.reset();
				this.cancelAllPendingRequests();
	    	}
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
    	if(!stoped.get()){
    		if(isRequestRunning.getAndSet(true)){
    			new Fetch(firstVisibleItem,visibleItemCount,totalItemCount).execute();
    		}
    	}
    }
    
    /**
     * 
     */
    public void onScrollStateChanged(AbsListView view, int scrollState) {}
    
    private UpdateInterface updateInterface = new UpdateInterface();
    
    private class UpdateInterface extends Handler{
		public UpdateInterface() {}
		@Override
		public void handleMessage(Message msg) {
			loadingText.setText(R.string.loading);
	    	loadingText.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
	    	load.startAnimation();
		}
	}
    
}