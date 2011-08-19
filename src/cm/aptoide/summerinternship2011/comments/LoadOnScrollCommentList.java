/**
 * 
 */
package cm.aptoide.summerinternship2011.comments;

import java.math.BigInteger;

import cm.aptoide.summerinternship2011.Status;
import cm.aptoide.summerinternship2011.comments.CommentGetter.EndOfRequestReached;

import android.content.Context;
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
	
	
    private final static int visibleThreshold = 5; // The minimum amount of items to have below your current scroll position, before loading more
    private int currentPage = 0; // The current page of data you have loaded
    private int previousTotal = 0; // The total number of items in the dataset after the last load
    private boolean loading = true; // True if we are still waiting for the last set of data to load
    
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
    	lastCommentIdRead = null;
    	commentGetter = new CommentGetter("market", "cm.aptoide.pt", "2.0.2");
    	this.context = context;
    	continueFetching = true;
    }
 
    /**
     * 
     */
    public void onScroll(AbsListView view, int firstVisibleItem,
            int visibleItemCount, int totalItemCount) {
        
    	if(continueFetching){
	    	if (loading) {
	            if (totalItemCount > previousTotal) {
	                loading = false;
	                previousTotal = totalItemCount;
	                currentPage++;
	            }
	        }
	        
	        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
	        	try{
	        		
		        	try{
						commentGetter.parse(context, 1, lastCommentIdRead);
					} catch(EndOfRequestReached e){}
					
					if(commentGetter.getStatus().equals(Status.OK) && commentGetter.getComments().size()!=0){
						lastCommentIdRead = commentGetter.getComments().get(0).getId();
						commentList.add(commentGetter.getComments().get(0));	
					} else { continueFetching = false; }
					
		        	loading = true;
		        	
	        	}catch(Exception e){
	        		continueFetching = false;
	        	}
	        }
    	}
    }
	
    /**
     * 
     */
    public void onScrollStateChanged(AbsListView view, int scrollState) {}
 
}