/**
 * 
 */
package cm.aptoide.summerinternship2011.comments;

import cm.aptoide.pt.R;
import android.content.Context;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.Toast;

/**
 * @author rafael
 *
 */
public class ContextMenuComments implements View.OnCreateContextMenuListener{
	
	private Context context;
	
	public ContextMenuComments(Context context) {
		this.context = context;
		Event.REPLY.setString(context.getString(R.string.reply));
	}
	
	private enum Event{
		REPLY(0);
		private int id;
		private String string;
		
		private Event(int id){
			this.id = Menu.FIRST+id;
			this.string=null;
		}
		public int getId() {
			return id;
		}
		public String getString() {
			return string;
		}
		public void setString(String string) {
			this.string = string;
		}
		public static Event getEventFromId(int id){
			for(Event event:values()){
				if(event.getId()==id)
					return event;
			}
			return null;
		}
	}
	
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		for(Event item:Event.values())
			menu.add(0, item.getId(), 0, item.getString());
	}
	
	/**
	 * 
	 * @param item
	 * @return If we handled the event or not. True in the first case.
	 */
	public boolean onContextItemSelected(MenuItem item) {
		Event event = Event.getEventFromId(item.getItemId());
        
		if(event!=null){
			switch (event) {
	        	case REPLY:
	        		Toast.makeText(context, "Reply", Toast.LENGTH_LONG);
	        	return true; 
	        }
		}
		
		return false;
        
	}
	
}
