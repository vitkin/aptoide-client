package cm.aptoide.pt.webservices.comments;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import cm.aptoide.pt.Configs;
import cm.aptoide.pt.R;
import cm.aptoide.pt.webservices.exceptions.CancelRequestSAXException;
import cm.aptoide.pt.webservices.exceptions.EmptyRequestException;
import cm.aptoide.pt.webservices.exceptions.EndOfRequestReachedSAXException;
import cm.aptoide.pt.webservices.exceptions.FailedRequestSAXException;

/**
 * @author rafael
 * @since 2.5.3
 * 
 * The scroll listener of the comment list.
 */
public class CommentPosterListOnScrollListener implements OnScrollListener {
    
    private int 			currentPage; 						// The current page of data you have loaded
    private int 			previousTotal; 						// The total number of items in the dataset after the last load
    private BigInteger 		lastCommentIdRead;
    
    private Activity 					context;
    private CommentsAdapter<Comment> 	commentList;	
    private CommentGetter 				commentGetter; 			// Comment xml parser
    
    private ExecutingController pendingFetch;					// The fetch comment asynctask being run
    private ProgressBar 		   load;
    
    private AtomicBoolean 	stoped;								//	If mode comments should be fetch
    private TextView 		loadingText;						//	Text field displaying a loading message for messages
    
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
    	
    	this.context 		= context;
    	this.commentList 	= commentList;
    	
    	commentGetter 		= new CommentGetter(repo, apkid, version);
    	
    	this.pendingFetch 	= null;
    	
    	load 				= ((ProgressBar)loadingLayout.findViewById(R.id.progressBar1));
//    	load.startAnimation(R.drawable.loading, 40);
    	loadingText 		= ((TextView)loadingLayout.findViewById(R.id.loadTextComments));
    	
    	reset();
    	
    	stoped 				= new AtomicBoolean(true);
    	pendingFetch = new ExecutingController();
    }
    
    /**
     * 
     */
    public void reset(){
    	currentPage 		= 0;
    	previousTotal 		= 0;
    	lastCommentIdRead 	= null;
    	stoped 				= new AtomicBoolean(false);
    	initing.sendEmptyMessage(0);
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
			
			if(!isCancelled()){
				
	            if (totalItemCount > previousTotal) {
	                previousTotal = totalItemCount;
	                currentPage++;
	            }
	            
	            if ((totalItemCount - visibleItemCount) <= (firstVisibleItem + Configs.VISIBLE_THRESHOLD_COMMENTS)) {
		        	
		        	try{
		        		
		        		synchronized(commentGetter){
		        			//	Importante para garantir que a busca do comentário que foi acabado de ser submtido não colide com o fetch actual
		        			try{
								commentGetter.parse(context, Configs.VISIBLE_THRESHOLD_COMMENTS, lastCommentIdRead, false, this);
							} catch(EndOfRequestReachedSAXException e){}
							
							if(commentGetter.getComments().size()!=0){
								
								lastCommentIdRead = commentGetter.getComments().get(commentGetter.getComments().size()-1).getId();
								return commentGetter.getComments();
								
							} else { 
								
								if(!commentGetter.getStatus().equals(cm.aptoide.pt.webservices.EnumResponseStatus.OK)){
									throw new FailedRequestSAXException("Request could not be executed.");
								}else{
									throw new EmptyRequestException("Request empty.");
								}
								
							}
							
		        		}
		        		
	    			}catch(CancelRequestSAXException e){
	    				
	    			}catch (Exception e) { 
	    				stoped.set(true);
	    				Log.d("Aptoide", "Comment, fetcher as cancelled future fetch for this app version");
	    			}
					
		        }
			           
	    	}
			
			return null;
			
		}

		@Override
		protected void onPostExecute(ArrayList<Comment> result) {
			
			if(result != null){
				ArrayList<Comment> comments = commentGetter.getComments();
				for(Comment comment: comments){
					 commentList.add(comment);
				}
			} else {
				loadingText.setVisibility(View.GONE);
				load.setVisibility(View.GONE);
			}
			Log.d("Aptoide", "Comment, fetcher as finished");
			finish();
		}
		
		@Override
		protected void onCancelled() {
			Log.d("Aptoide", "Comment, fetcher was cancelled");
			finish();
		}
		
		private void finish(){
			pendingFetch.clearExecuting();
		}
		
    } //End of Fetch class
    
    /**
     * Fetches new comments and adds it to the beginning of the list.
     * Synchronized due to other AsyncThreads that may interfere in the normal program work flow.
     * This is invoked when trying to fetch the newly created comment.
     * 
     */
    public void fetchNewComments(){
    	
    	synchronized(pendingFetch){
        	
    		while(pendingFetch.isExecuting()){
    			try {
					pendingFetch.wait();
				} catch (InterruptedException e) {}
    		}
    		
    		//There is no fetcher running
    		
	    	try{
	    		
		    	try{
		    		if(commentList.getCount()!=0){
		    			String userId = context.getApplicationContext().getSharedPreferences("aptoide_prefs", Context.MODE_PRIVATE).getString(Configs.LOGIN_USER_ID, null);
						commentGetter.parse(context, commentList.getItem(0).getId(), userId);
		    		} else {
		    			commentGetter.parse(context, Configs.COMMNETS_TO_LOAD, lastCommentIdRead, false, (AsyncTask<?,?,?>)null);
		    		}
				} catch(EndOfRequestReachedSAXException e){}
				
				addComments.sendEmptyMessage(0);
				
	    	} catch(Exception e)		{
	    		Log.d("Aptoide", "An unusual exception was thrown while trying to retrive the new comment of the use. "+e.getMessage());
	    	}
    	
    	}
    	
	}
    
    /**
     * 
     * @param apk_repo_str_raw
     * @param apk_id
     * @param apk_ver_str_raw
     */
    public void fetchNewApp(String apk_repo_str_raw,String apk_id,String apk_ver_str_raw){
		
    	cancelCommentFetch();
    	
    	Log.d("Aptoide", "Comment, fetching new app");
    	
    	synchronized(pendingFetch){
    	
    		while(pendingFetch.isExecuting()){
    			Log.d("Aptoide", "Comment, waiting for fetch finish");
    			try {
					pendingFetch.wait();
				} catch (InterruptedException e) {}
				Log.d("Aptoide", "Comment, fetch as finished ready to proced");
    		}
    		
    		//There is no fetcher running
    		
	    	this.resetGetter(apk_repo_str_raw, apk_id, apk_ver_str_raw);
	    	this.reset();
			removeAllComments.sendEmptyMessage(0);
			
    	}
    	
    	Log.d("Aptoide", "Comment, fetch new app was complete");
    	
    }
    
    /**
     * 
     */
    public void cancelCommentFetch(){
		
    	synchronized(pendingFetch){
    		if(pendingFetch.getExecuting()!=null){
    			pendingFetch.getExecuting().cancel(false);
    		}
    	}
    	
    }
    
    /**
     * 
     */
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    	
    	if(!stoped.get()){
    		if(!pendingFetch.getAndSetIsExecuting(firstVisibleItem, visibleItemCount, totalItemCount)){
    			Log.d("Aptoide", "Comment, launch new fetch");
    			pendingFetch.getExecuting().execute();
    		}
		}
		
    }
    
    /**
     * 
     */
    public void onScrollStateChanged(AbsListView view, int scrollState) {}
    
    
    
    
    
    private RemoveAllComments removeAllComments = new RemoveAllComments();
    private class RemoveAllComments extends Handler{
		public RemoveAllComments() {}
		@Override
		public void handleMessage(Message msg) {
			commentList.removeAll();
		}
	}
    
    private InitingInterface initing = new InitingInterface();
    private class InitingInterface extends Handler{
		public InitingInterface() {}
		@Override
		public void handleMessage(Message msg) {
			loadingText.setVisibility(View.VISIBLE);
	    	load.setVisibility(View.VISIBLE);
		}
	}
    
    private AddComments addComments= new AddComments();
    private class AddComments extends Handler{
		public AddComments() {}
		@Override
		public void handleMessage(Message msg) {
			commentList.addAtBegin(commentGetter.getComments());
		}
	}
    
    
    /**
     * @author rafael
     * @since 2.5.3
     * 
     */
    private class ExecutingController{
    	private Fetch executing;
    	public ExecutingController() {
    		executing = null;
		}
    	public synchronized boolean getAndSetIsExecuting(int firstVisibleItem,int visibleItemCount,int totalItemCount){
    		boolean isExecuting = isExecuting();
    		if(!isExecuting)
    			executing 			= new Fetch(firstVisibleItem,visibleItemCount,totalItemCount);
    		return isExecuting;
    	}
    	public synchronized Fetch getExecuting() {
			return executing;
		}
    	public synchronized boolean isExecuting() {
			return executing!=null;
		}
    	public synchronized void clearExecuting() {
			executing=null;
			this.notifyAll();
		}
    } 
    
}