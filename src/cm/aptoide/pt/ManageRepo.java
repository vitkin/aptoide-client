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
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class ManageRepo extends ListActivity{
	
	private DbHandler db = null;
	
	private final int ADD_REPO = 1;
	private final int REM_REPO = 2;
	private final int EDIT_REPO = 3;
	
	private boolean change = false;
	
	private Intent rtrn = new Intent();
	
	private Vector<ServerNode> server_lst = new Vector<ServerNode>();
	
	private String updt_repo;
	private AlertDialog alert2;
	
	private AlertDialog alrt = null;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.repolist);
		
		db = new DbHandler(this);
		
		Intent i = getIntent();
		if(i.hasExtra("empty")){
			final String uri = i.getStringExtra("uri");
			AlertDialog alrt = new AlertDialog.Builder(this).create();
			alrt.setTitle(getString(R.string.title_repo_alrt));
			alrt.setIcon(android.R.drawable.ic_dialog_alert);
			alrt.setMessage(getString(R.string.myrepo_alrt) +
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
			//String uri = i.getStringExtra("uri");
			//Vector<String> new_serv_lst = getRemoteServLst(uri);
			ArrayList<String> new_serv_lst = (ArrayList<String>) i.getSerializableExtra("uri");
			for(final String srv: new_serv_lst){
				AlertDialog alrt = new AlertDialog.Builder(this).create();
				alrt.setTitle(getString(R.string.title_repo_alrt));
				alrt.setIcon(android.R.drawable.ic_dialog_alert);
				alrt.setMessage(getString(R.string.newrepo_alrt) + srv);
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
		}else if(i.hasExtra("newrepo")){
			final String repo = i.getStringExtra("newrepo");
			AlertDialog alrt = new AlertDialog.Builder(this).create();
			alrt.setTitle(getString(R.string.title_repo_alrt));
			alrt.setIcon(android.R.drawable.ic_dialog_alert);
			alrt.setMessage(getString(R.string.newrepo_alrt) + repo);
			alrt.setButton("Yes", new DialogInterface.OnClickListener() {
			      public void onClick(DialogInterface dialog, int which) {
			    	  db.addServer(repo);
			    	  change = true;
			    	  redraw();
			    	  return;
			      } }); 
			alrt.setButton2("No", new DialogInterface.OnClickListener() {
			      public void onClick(DialogInterface dialog, int which) {
			    	  //exit
			      }});
			alrt.show();
		}
	}
	
	@Override
	protected void onResume() {
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
        	server_line.put("napk", "Applications: " + node.napk);
        	result.add(server_line);
        }
        SimpleAdapter show_out = new SimpleAdapter(this, result, R.layout.repolisticons, 
        		new String[] {"uri", "inuse", "napk"}, new int[] {R.id.uri, R.id.img, R.id.numberapks});
        
        setListAdapter(show_out);
	}
	
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		db.changeServerStatus(server_lst.get(position).uri);
		change = true;
		redraw();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE,ADD_REPO,1,R.string.menu_add_repo)
			.setIcon(android.R.drawable.ic_menu_add);
		menu.add(Menu.NONE, EDIT_REPO, 2, R.string.menu_edit_repo)
			.setIcon(android.R.drawable.ic_menu_edit);
		menu.add(Menu.NONE, REM_REPO, 3, R.string.menu_rem_repo)
		.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		
		LayoutInflater li = LayoutInflater.from(this); 
		
		switch(item.getItemId()){
		case ADD_REPO:
			View view = li.inflate(R.layout.addrepo, null);
			
			final TextView sec_msg = (TextView) view.findViewById(R.id.sec_msg);
			final TextView sec_msg2 = (TextView) view.findViewById(R.id.sec_msg2);
			
			final EditText sec_user = (EditText) view.findViewById(R.id.sec_user);
			final EditText sec_pwd = (EditText) view.findViewById(R.id.sec_pwd);
			
			final CheckBox sec = (CheckBox) view.findViewById(R.id.secure_chk);
			sec.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if(isChecked){
						sec_user.setEnabled(true);
						sec_pwd.setEnabled(true);
					}else{
						sec_user.setEnabled(false);
						sec_pwd.setEnabled(false);
					}
				}
			});
			
			
			Builder p = new AlertDialog.Builder(this).setView(view);
			alrt = p.create();
			alrt.setIcon(android.R.drawable.ic_menu_add);
			alrt.setTitle("Add new repository");

			alrt.setButton("Add", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					Message msg = new Message();
					EditText uri = (EditText) alrt.findViewById(R.id.edit_uri);
					String uri_str = uri.getText().toString();
					sec_msg.setVisibility(View.GONE);
					sec_msg2.setVisibility(View.GONE);
					if(sec.isChecked()){
						String user = sec_user.getText().toString();
						String pwd = sec_pwd.getText().toString();
						int result = checkServer(uri_str, user, pwd);
						if(result == 200){
							msg.obj = 0;
							db.addServer(uri_str);
							db.addLogin(user, pwd, uri_str);
							change = true;
							redraw();
						}else if (result == 401){
							sec_msg2.setText("Login is wrong");
							sec_msg2.setVisibility(View.VISIBLE);
							msg.obj = 1;			
						}else{
							sec_msg.setText("Can't connect to server");
							sec_msg.setVisibility(View.VISIBLE);
							msg.obj = 1;
						}
					}else{
						int result = checkServer(uri_str, null, null);
						if(result == 200){
							msg.obj = 0;
							db.addServer(uri_str);
							change = true;
							redraw();
						}else if (result == 401){
							sec_msg2.setText("Login required");
							sec_msg2.setVisibility(View.VISIBLE);
							msg.obj = 1;
						}else{
							sec_msg.setText("Can't connect to server");
							sec_msg.setVisibility(View.VISIBLE);
							msg.obj = 1;
						}
					}
					new_repo.sendMessage(msg);
				} });

			alrt.setButton2("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					alrt.dismiss();
				} });
			alrt.show();
			break;
		
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
			break;
			
		case EDIT_REPO:
			CharSequence[] b2 = new CharSequence[server_lst.size()];
			for(int i=0; i<server_lst.size(); i++){
				b2[i] = server_lst.get(i).uri;
			}
			
			AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
			builder2.setTitle("Chose repository to edit");
			builder2.setIcon(android.R.drawable.ic_menu_edit);
			builder2.setSingleChoiceItems(b2, -1, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
						updt_repo = server_lst.get(which).uri;
				}
			}); 
			builder2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int	whichButton) {
					editRepo(updt_repo);
					return;
				}
			});
			builder2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					return;
				}
			}); 
			 alert2 = builder2.create();
			alert2.show();
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	
	private void editRepo(final String repo){
		LayoutInflater li = LayoutInflater.from(this);
		View view = li.inflate(R.layout.addrepo, null);
		Builder p = new AlertDialog.Builder(this).setView(view);
		final AlertDialog alrt = p.create();
		final EditText uri = (EditText) view.findViewById(R.id.edit_uri);
		uri.setText(repo);
		
		final EditText sec_user = (EditText) view.findViewById(R.id.sec_user);
		final EditText sec_pwd = (EditText) view.findViewById(R.id.sec_pwd);
		final CheckBox sec = (CheckBox) view.findViewById(R.id.secure_chk);
		sec.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					sec_user.setEnabled(true);
					sec_pwd.setEnabled(true);
				}else{
					sec_user.setEnabled(false);
					sec_pwd.setEnabled(false);
				}
			}
		});
		
		String[] logins = null; 
		logins = db.getLogin(repo);
		if(logins != null){
			sec.setChecked(true);
			sec_user.setText(logins[0]);																																																				 
			sec_pwd.setText(logins[1]);
		}else{
			sec.setChecked(false);
		}
	
		alrt.setIcon(android.R.drawable.ic_menu_add);
		alrt.setTitle("Edit repository");
		alrt.setButton("Done", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				String new_repo = uri.getText().toString();
				db.updateServer(repo, new_repo);
				if(sec.isChecked()){
					db.addLogin(sec_user.getText().toString(), sec_pwd.getText().toString(), new_repo);
				}else{
					db.disableLogin(new_repo);
				}
				change = true;
				redraw();
			} });

		alrt.setButton2("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				return;
			} });
		alert2.dismiss();
		alrt.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
	    	e.printStackTrace();
	    } catch (SAXException e) {
	    	e.printStackTrace();
	    } catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	    return out;
	}
	
	private int checkServer(String uri, String user, String pwd){
		
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
		HttpConnectionParams.setSoTimeout(httpParameters, 5000);
		
		DefaultHttpClient mHttpClient = new DefaultHttpClient(httpParameters);
		
		mHttpClient.setRedirectHandler(new RedirectHandler() {
			public boolean isRedirectRequested(HttpResponse response,
					HttpContext context) {
				return false;
			}

			public URI getLocationURI(HttpResponse response, HttpContext context)
			throws ProtocolException {
				return null;
			}
		});
		
        HttpGet mHttpGet = new HttpGet(uri+"/info.xml");
        try {
        	if(user != null && pwd != null){
        		URL mUrl = new URL(uri);
        		mHttpClient.getCredentialsProvider().setCredentials(
        				new AuthScope(mUrl.getHost(), mUrl.getPort()),
        				new UsernamePasswordCredentials(user, pwd));
        	}
        	
			HttpResponse mHttpResponse = mHttpClient.execute(mHttpGet);
			
			Header[] azz = mHttpResponse.getHeaders("Location");
			if(azz.length > 0){
				String newurl = azz[0].getValue();
				Log.d("Aptoide", "Now to: " + newurl);
				Log.d("Aptoide", "Login " + user + " - " + pwd);

				mHttpGet = null;
				mHttpGet = new HttpGet(newurl);
				
				if(user != null && pwd != null){
	        		URL mUrl = new URL(newurl);
	        		mHttpClient.getCredentialsProvider().setCredentials(
	        				new AuthScope(mUrl.getHost(), mUrl.getPort()),
	        				new UsernamePasswordCredentials(user, pwd));
	        	}
				
				mHttpResponse = null;
				mHttpResponse = mHttpClient.execute(mHttpGet);
			}

			return mHttpResponse.getStatusLine().getStatusCode();
		} catch (ClientProtocolException e) {Log.d("Aptoide","Error 1"); return -1;} 
		catch (IOException e) {Log.d("Aptoide","Error 2"); return -1;}
		catch (IllegalArgumentException e) {Log.d("Aptoide","Error 3"); return -1;}
	}
	
	
	private Handler new_repo = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if((Integer)msg.obj == 1)
				alrt.show();
		}
	};

	@Override
	public void finish() {
		if(change)
			rtrn.putExtra("update", true);
		this.setResult(RESULT_OK, rtrn);
		super.finish();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	
}
