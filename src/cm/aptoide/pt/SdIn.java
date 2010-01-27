/*
 * Copyright (C) 2009  Roberto Jacinto
 * roberto.jacinto@caixamagica.pt
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package cm.aptoide.pt;

import java.util.Vector;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SdIn extends ListActivity {
	
	private static final int RESCAN_SD_MENU = Menu.FIRST;
	
	
	private Vector<SdApkNode> apk_lst = new Vector<SdApkNode>();
	
	private SearchFiles files = new SearchFiles(this);
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
        apk_lst = files.getAll();
        Vector<String> tlst = new Vector<String>();
        for(SdApkNode node: apk_lst)
        	tlst.add(node.name);
        setListAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, tlst));
        
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE,RESCAN_SD_MENU,1,R.string.menu_update_sdcard)
			.setIcon(android.R.drawable.ic_menu_rotate);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
        case RESCAN_SD_MENU:
        	files.rescan();
        	apk_lst = files.getAll();
            Vector<String> tlst = new Vector<String>();
            for(SdApkNode node: apk_lst)
            	tlst.add(node.name);
            setListAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, tlst));
    		return true;
		}
		return  true;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
			Intent intent = new Intent();
	    	intent.setAction(android.content.Intent.ACTION_VIEW);
	    	intent.setDataAndType(Uri.parse("file://" + apk_lst.elementAt(position).path), "application/vnd.android.package-archive");
	    	startActivity(intent);
	    	return;
	}
}
