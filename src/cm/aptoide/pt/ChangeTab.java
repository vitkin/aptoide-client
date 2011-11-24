package cm.aptoide.pt;

import android.gesture.GestureOverlayView;
import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.ViewFlipper;

/**
 * @author rafael
 *
 */
public class ChangeTab extends SimpleOnGestureListener {
	
	private ViewFlipper viewFlipper;
	private TabWidget tabWidget;
	private TabHost tabHost;
	
	public ChangeTab(TabHost tabHost) {
		this.viewFlipper 	= (ViewFlipper)((GestureOverlayView)((RelativeLayout)tabHost.getChildAt(0)).getChildAt(1)).getChildAt(0);
		this.tabWidget 		= (TabWidget)((RelativeLayout)tabHost.getChildAt(0)).getChildAt(0);
		this.tabHost 		= tabHost;
	}
	
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		if(e1.getX() - e2.getX() > Configs.SWIPE_SLIDE_TO_TAB_MIN_DISTANCE && Math.abs(velocityX) > Configs.SWIPE_SLIDE_TO_TAB_THRESHOLD_VELOCITY) {
			
			int tab = (tabHost.getCurrentTab()+1 ) % tabWidget.getChildCount();
			tabHost.setCurrentTab( (tabHost.getCurrentTab()+1 ) % tabWidget.getChildCount() );
			viewFlipper.setInAnimation(inFromRightAnimation());
			viewFlipper.setOutAnimation(outToLeftAnimation());
			viewFlipper.setDisplayedChild(tab);
			
			return true;
		} else if (e2.getX() - e1.getX() > Configs.SWIPE_SLIDE_TO_TAB_MIN_DISTANCE && Math.abs(velocityX) > Configs.SWIPE_SLIDE_TO_TAB_THRESHOLD_VELOCITY) {
			int tab = (tabHost.getCurrentTab()+(tabWidget.getChildCount()-1) ) % tabWidget.getChildCount();
			tabHost.setCurrentTab( tab );
			viewFlipper.setInAnimation(inFromLeftAnimation());
			viewFlipper.setOutAnimation(outToRightAnimation());
			viewFlipper.setDisplayedChild(tab);
			
			return true;
		}
		return false;
	}
	
	private Animation inFromRightAnimation() {
    	Animation inFromRight = new TranslateAnimation(
    	Animation.RELATIVE_TO_PARENT,  +2.0f, Animation.RELATIVE_TO_PARENT,  0.0f,
    	Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
    	);
    	inFromRight.setDuration(500);
    	inFromRight.setInterpolator(new AccelerateInterpolator());
    	return inFromRight;
	 }

    private Animation outToLeftAnimation() {
    	Animation outtoLeft = new TranslateAnimation(
    	 Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,  -2.0f,
    	 Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
    	);
    	outtoLeft.setDuration(500);
    	outtoLeft.setInterpolator(new AccelerateInterpolator());
    	return outtoLeft;
    }
	    
    private Animation inFromLeftAnimation() {
    	Animation inFromLeft = new TranslateAnimation(
    	Animation.RELATIVE_TO_PARENT,  -2.0f, Animation.RELATIVE_TO_PARENT,  0.0f,
    	Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
    	);
    	inFromLeft.setDuration(500);
    	inFromLeft.setInterpolator(new AccelerateInterpolator());
    	return inFromLeft;
    }
	    
    private Animation outToRightAnimation() {
    	Animation outtoRight = new TranslateAnimation(
    	 Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,  +2.0f,
    	 Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
    	);
    	outtoRight.setDuration(500);
    	outtoRight.setInterpolator(new AccelerateInterpolator());
    	return outtoRight;
    }
	
}