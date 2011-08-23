/**
 * 
 */
package cm.aptoide.summerinternship2011.comments;

import java.math.BigInteger;
import java.util.ArrayList;
import cm.aptoide.summerinternship2011.FailedRequestException;
import cm.aptoide.summerinternship2011.comments.CommentGetter.EndOfRequestReached;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.AbsListView.OnScrollListener;

/**
 * 
 * @author rafael
 * @since summerinternship2011
 *
 */
public class LoadOnScrollCommentList implements OnScrollListener {
	
	
    private final static int visibleThreshold = 2; // The minimum amount of items to have below your current scroll position, before loading more
    private final static int commentsToLoad = 1; //The number of comments to retrieve per fetch
    private int currentPage; // The current page of data you have loaded
    private int previousTotal; // The total number of items in the dataset after the last load
    private Boolean loading; // True if we are still waiting for the last set of data to load
    
    private ArrayAdapter<Comment> commentList;
    private BigInteger lastCommentIdRead;
    private CommentGetter commentGetter;
    private Context context;
    
    private boolean continueFetching;
    
    /**
     * 
     * @param context
     * @param commentList
     */
    public LoadOnScrollCommentList(Context context,ArrayAdapter<Comment> commentList) {
    	this.commentList = commentList;
    	this.context = context;
    	
    	lastCommentIdRead = null;
    	commentGetter = new CommentGetter("market", "cm.aptoide.pt", "2.0.2");
    	continueFetching = true;
    	
    	loading = true;
    	previousTotal = 0;
    	currentPage = 0;
    	
    }
 
    /**
     * 
     */
    public void onScroll(AbsListView view, int firstVisibleItem,
            int visibleItemCount, int totalItemCount) {
        
    		new Fetch(firstVisibleItem,visibleItemCount,totalItemCount).execute();
    		
    	}
    	
    	
    private class Fetch extends AsyncTask<Void, Void, ArrayList<Comment>> {
		
		private int firstVisibleItem; 
		private int visibleItemCount; 
		private int totalItemCount;
		
		public Fetch(int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			this.firstVisibleItem = firstVisibleItem; 
    		this.visibleItemCount = visibleItemCount; 
    		this.totalItemCount = totalItemCount;
		}
		
		@Override
		protected ArrayList<Comment> doInBackground(Void... params) {
			
			if(continueFetching){
		    		
					synchronized(loading){
						
				    	if (loading) {
				            if (totalItemCount > previousTotal) {
				                loading = false;
				                previousTotal = totalItemCount;
				                currentPage++;
				            }
				        }
				        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
				        	loading = true;
				        	try{
			    				try{
									commentGetter.parse(context, commentsToLoad, lastCommentIdRead);
								} catch(EndOfRequestReached e){}
								if(commentGetter.getStatus().equals(cm.aptoide.summerinternship2011.Status.OK) && commentGetter.getComments().size()!=0){
									lastCommentIdRead = commentGetter.getComments().get(commentGetter.getComments().size()-1).getId();
									Log.d("Aptoide","\tCarregados num -->"+" "+lastCommentIdRead);
									return commentGetter.getComments();
								} else { 
									throw new FailedRequestException("Request empty or could not be executed as expected.");  
								}
			    			}catch(Exception e){
				        		continueFetching = false;
				        	}
			    			
				        }
			        
				} //Sync end
					
	    	}
			
			return null;
			
		}

		@Override
		protected void onPostExecute(ArrayList<Comment> result) {
			if(result !=null)
				for(Comment comment: commentGetter.getComments())
					commentList.add(comment);
		}	
    	
    }
    
    /**
     * 
     */
    public void onScrollStateChanged(AbsListView view, int scrollState) {}
 
}