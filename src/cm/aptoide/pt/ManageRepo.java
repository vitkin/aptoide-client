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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ManageRepo extends ListActivity{
	
	private DbHandler db = null;
	
	private final int ADD_REPO = 1;
	private final int REM_REPO = 2;
	
	private boolean change = false;
	
	private Intent rtrn = new Intent();
	
	private Vector<ServerNode> server_lst = new Vector<ServerNode>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.repolist);
		
		db = new DbHandler(this);
		
		Intent i = getIntent();
		if(i.hasExtra("empty")){
			final String uri = i.getStringExtra("uri");
			AlertDialog alrt = new AlertDialog.Builder(this).create();
			alrt.setTitle("Attention");
			alrt.setIcon(android.R.drawable.ic_dialog_alert);
			alrt.setMessage("It looks like you don't have any added repository.\nWe suggest you use ours.\nDo you want to add our repository?\n\n" +
					uri);
			alrt.setButton("Yes", new DialogInterface.OnClickListener() {
			      public void onClick(DialogInterface dialog, int which) {
			    	  db.addServer(uri);
			    	  change = true;
			    	  redraw();
			    	  return;
			      } }); 
			alrt.setButton2("No", new DialogInterface.OnClickListener() {
			      public void onClick(DialogInterface dialog, int which) {
			    	  return;
			      }});
			alrt.show();
		}else if(i.hasExtra("uri")){
			String uri = i.getStringExtra("uri");
			Vector<String> new_serv_lst = getRemoteServLst(uri);
			for(final String srv: new_serv_lst){
				AlertDialog alrt = new AlertDialog.Builder(this).create();
				alrt.setTitle("Attention");
				alrt.setIcon(android.R.drawable.ic_dialog_alert);
				alrt.setMessage("Do you want to add this repository to your list?\n\n" + srv);
				alrt.setButton("Yes", new DialogInterface.OnClickListener() {
				      public void onClick(DialogInterface dialog, int which) {
				    	  db.addServer(srv);
				    	  change = true;
				    	  redraw();
				    	  return;
				      } }); 
				alrt.setButton2("No", new DialogInterface.OnClickListener() {
				      public void onClick(DialogInterface dialog, int which) {
				    	  return;
				      }});
				alrt.show();
			}
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		redraw();
	}

	private void redraw(){
		server_lst = db.getServers();
		 
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        Map<String, Object> server_line;
        
        for(ServerNode node: server_lst){
        	server_line = new HashMap<String, Object>();
        	server_line.put("uri", node.uri);
        	if(node.inuse){
        		server_line.put("inuse", R.drawable.btn_check_on);
        	}else{
        		server_line.put("inuse", R.drawable.btn_check_off);
        	}
        	result.add(server_line);
        }
        SimpleAdapter show_out = new SimpleAdapter(this, result, R.layout.repolisticons, 
        		new String[] {"uri", "inuse"}, new int[] {R.id.uri, R.id.img});
        
        setListAdapter(show_out);
	}
	
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		db.changeServerStatus(server_lst.get(position).uri);
		change = true;
		redraw();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE,ADD_REPO,1,R.string.menu_add_repo)
			.setIcon(android.R.drawable.ic_menu_add);
		menu.add(Menu.NONE, REM_REPO, 2, R.string.menu_rem_repo)
			.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		super.onMenuItemSelected(featureId, item);
		LayoutInflater li = LayoutInflater.from(this); 
		
		switch(item.getItemId()){
		case ADD_REPO:
			View view = li.inflate(R.layout.addrepo, null);
			Builder p = new AlertDialog.Builder(this).setView(view);
			final AlertDialog alrt = p.create();
			
			alrt.setIcon(android.R.drawable.ic_menu_add);
			alrt.setTitle("Add new repository");
			alrt.setButton("Add", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					EditText uri = (EditText) alrt.findViewById(R.id.edit_uri);
					String uri_str = uri.getText().toString();
					db.addServer(uri_str);
					change = true;
					redraw();
				} });

			alrt.setButton2("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					return;
				} });
			alrt.show();
			return true;
		
		case REM_REPO:
			final Vector<String> rem_lst = new Vector<String>();	
			CharSequence[] b = new CharSequence[server_lst.size()];
			for(int i=0; i<server_lst.size(); i++){
				b[i] = server_lst.get(i).uri;
			}
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Chose repository to remove");
			builder.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
			builder.setMultiChoiceItems(b, null, new DialogInterface.OnMultiChoiceClickListener() {
                		public void onClick(DialogInterface dialog,int whichButton, boolean isChecked) {
                		        if(isChecked){
                		        	rem_lst.addElement(server_lst.get(whichButton).uri);
                		        }else{
                		        	rem_lst.removeElement(server_lst.get(whichButton).uri);
                		        }
                		 }
             }); 
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                		public void onClick(DialogInterface dialog, int	whichButton) {
                			db.removeServer(rem_lst);
                			change = true;
                			redraw();
                		}
            });
			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                		public void onClick(DialogInterface dialog, int whichButton) {
                			return;
                		}
            }); 
			AlertDialog alert = builder.create();
			alert.show();
			return true;
		}
		return true;	
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK){
			Bundle b = data.getExtras();
			String a = b.getString("URI");
			db.addServer(a);
			change = true;
			redraw();
		}
	}
	
	private Vector<String> getRemoteServLst(String file){
		SAXParserFactory spf = SAXParserFactory.newInstance();
		Vector<String> out = new Vector<String>();
	    try {
	    	SAXParser sp = spf.newSAXParser();
	    	XMLReader xr = sp.getXMLReader();
	    	NewServerRssHandler handler = new NewServerRssHandler(this);
	    	xr.setContentHandler(handler);
	    	
	    	InputStreamReader isr = new FileReader(new File(file));
	    	InputSource is = new InputSource(isr);
	    	xr.parse(is);
	    	File xml_file = new File(file);
	    	xml_file.delete();
	    	out = handler.getNewSrvs();
	    } catch (IOException e) {
	    	// TODO Auto-generated catch block
	    	e.printStackTrace();
	    } catch (SAXException e) {
	    	// TODO Auto-generated catch block
	    	e.printStackTrace();
	    } catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return out;
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		rtrn.putExtra("newrepo", 0);
		if(change)
			rtrn.putExtra("update", true);
		this.setResult(RESULT_OK, rtrn);
		super.finish();
	}
	
	
}
