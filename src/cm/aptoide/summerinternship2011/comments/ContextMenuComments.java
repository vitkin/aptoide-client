/**
 * 
 */
package cm.aptoide.summerinternship2011.comments;

import cm.aptoide.pt.R;
import android.content.Context;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;

/**
 * @author rafael
 *
 */
public class ContextMenuComments implements View.OnCreateContextMenuListener{
	
	private Context context;
	
	public ContextMenuComments(Context context) {
		this.context = context;
		Event.REPLY.setString(context.getString(R.string.reply));
		Event.COPY_TO_CLIPBOARD.setString(context.getString(R.string.copyclip));
		Event.GENERATE_QR_CODE.setString(context.getString(R.string.qrcodeview));
	}
	
	public enum Event{
		REPLY(0), COPY_TO_CLIPBOARD(1), GENERATE_QR_CODE(2);
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
	
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		menu.setHeaderTitle(context.getString(R.string.whattodo));
		for(Event item:Event.values())
			menu.add(0, item.getId(), 0, item.getString());
	}
	
}
