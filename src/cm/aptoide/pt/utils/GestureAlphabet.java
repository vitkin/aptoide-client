package cm.aptoide.pt.utils;

import java.util.ArrayList;
import java.util.Map;

import android.content.Context;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.widget.ListView;
import android.widget.Toast;
import cm.aptoide.pt.Configs;
import cm.aptoide.pt.R;

/**
 * @author rafael
 *
 */
public class GestureAlphabet implements GestureOverlayView.OnGesturePerformedListener{
	private GestureLibrary gestureLibrary;
	private ListView list;
	private Context context;
	
	public GestureAlphabet(Context context,ListView list){
		gestureLibrary = GestureLibraries.fromRawResource(context, R.raw.gestures);
	    gestureLibrary.load();
	    this.list = list;
	    this.context = context;
	}
	
	public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
	    ArrayList<Prediction> predictions = gestureLibrary.recognize(gesture);
	    
	    
	    
	    
	    	if (predictions.size() > 0) {
	    		if(react(predictions.get(0).name)){
			    	if(predictions.get(0).score >= Configs.MIN_SEARCH_GESTURE_CONFIANCE){
				    	String action = predictions.get(0).name;
				        	
				        int index = searchIndexOfApk(action.charAt(0));
				        if(index>=0){
				        	Toast.makeText(context, context.getString(R.string.moving_to)+" "+action, Toast.LENGTH_SHORT).show();
				        	list.setSelection(index);
				    	}else{
				    		Toast.makeText(context, context.getString(R.string.recognized)+" "+action+". "+context.getString(R.string.no_found), Toast.LENGTH_SHORT).show();
				    	}
				        
			    	}else{
			    		Toast.makeText(context, context.getString(R.string.not_recognized), Toast.LENGTH_SHORT).show();
			    	}
	    		}
		    } else {
		    	Toast.makeText(context, context.getString(R.string.not_recognized), Toast.LENGTH_SHORT).show();
		    }
	    
	    
	}
	
	private boolean react(String action){
		for(String not_react : Configs.GESTURE_SEARCH_NOT_REACT_TO){
        	if(action.equals(not_react)){
        		return false;
        	}
        }
		return true;
	}
	
	
	@SuppressWarnings("unchecked")
	private int searchIndexOfApk(char given){
		for(int i = 0;i<list.getCount();i++){
			String name = ((String)((Map<String, Object>)list.getItemAtPosition(i)).get("name"))!=null?((String)((Map<String, Object>)list.getItemAtPosition(i)).get("name")):((String)((Map<String, Object>)list.getItemAtPosition(i)).get("name2"));
			if(name!=null){
				if((name.charAt(0)+"").equalsIgnoreCase(""+given))
					return i;
			}
		}
		return -1;
	}
	
}