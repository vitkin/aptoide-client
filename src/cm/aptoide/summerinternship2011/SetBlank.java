package cm.aptoide.summerinternship2011;

import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;

public class SetBlank implements OnTouchListener{
	private boolean alreadySetted;
	public SetBlank(){
		alreadySetted = false;
	}

	public boolean onTouch(View viewEdit, MotionEvent event) {
		if(!alreadySetted){
			((EditText)viewEdit).setText("");
			((EditText)viewEdit).setTextColor(Color.BLACK);
			alreadySetted = true;
		}
		//If return true it indicates that this action consumed the event and the result is that the edit text won't get selected
		return false;
	}
}