/**
 * 
 */
package cm.aptoide.summerinternship2011;

import android.graphics.Color;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;

/**
 * @author rafael
 * @since summerinternship2011
 * 
 * Used as a focus listener to control the input of some fields that have a label written.
 */
public class SetBlank implements OnFocusChangeListener{
	
	private boolean alreadySetted;
	
	/**
	 * 
	 */
	public SetBlank(){ 
		alreadySetted = false;
	}
	
	public void onFocusChange(View viewEdit, boolean hasFocus) {
		if(!alreadySetted && hasFocus){
			((EditText)viewEdit).setText("");
			((EditText)viewEdit).setTextColor(Color.BLACK);
			alreadySetted = true;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean getAlreadySetted(){
		return alreadySetted;
	}
	
}