package cm.aptoide.pt;

import java.util.ArrayList;

import android.app.ListActivity;
import android.database.Cursor;
import android.media.audiofx.NoiseSuppressor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ArrayAdapter;

public class ExcludedUpdatesActivity extends ListActivity {
	ArrayList<String> objects = new ArrayList<String>();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		redraw();
		
		
		registerForContextMenu(getListView());
	}

	/**
	 * 
	 */
	private void redraw() {
		Cursor c = Database.getInstance().getExcludedApks();
		for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){
			objects.add(c.getString(0));
		}
		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, objects));
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add("Remove from list");
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		Database.getInstance().deleteFromExcludeUpdate(objects.get(item.getItemId()));
		objects.clear();
		redraw();
		return super.onContextItemSelected(item);
	}
	
}
