/**
 * 
 */
package cm.aptoide.summerinternship2011.comments;

import java.math.BigInteger;
import java.util.Date;



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
    
//  private ArrayAdapter<CommentView> commentList;
    private Context context;
    
    /**
     * 
     * @param context
     * @param commentList
     */
    public LoadOnScrollCommentList(Context context, ArrayAdapter<Comment> commentList) {
//   	this.commentList = commentList;
    	this.context = context;
    }
 
    /**
     * 
     */
    public void onScroll(AbsListView view, int firstVisibleItem,
            int visibleItemCount, int totalItemCount) {
        
    	if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
                currentPage++;
            }
        }
        
        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
//        	commentList.add(new CommentView(context, new Comment(new BigInteger ("1"), "Zé tosco", "Gosto muito desta aplicação", new Date())));
        	loading = true;
        }
    }
	
    /**
     * 
     */
    public void onScrollStateChanged(AbsListView view, int scrollState) {}
 
}