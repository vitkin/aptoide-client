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
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Vector;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.Toast;

public class RemoteInTab extends TabActivity {

	private final String SDCARD = Environment.getExternalStorageDirectory().getPath();
	private String LOCAL_PATH = SDCARD+"/.aptoide";
	private String ICON_PATH = LOCAL_PATH+"/icons";
	private String XML_PATH = LOCAL_PATH+"/remapklst.xml";
	private String EXTRAS_XML_PATH = LOCAL_PATH+"/extras.xml";
	
	
	private String REMOTE_FILE = "/info.xml";
	private String REMOTE_EXTRAS_FILE = "/extras.xml";
	
	private static final int UPDATE_REPO = Menu.FIRST;
	private static final int MANAGE_REPO = 2;
	private static final int SEARCH_MENU = 4;
	private static final int SETTINGS = 5;
	private static final int ABOUT = 6;
	
	private static final int SETTINGS_FLAG = 31;
	private static final int NEWREPO_FLAG = 33;
	private static final int FETCH_APK = 35;
	
	private DbHandler db = null;

	private ProgressDialog pd;
	
	private Context mctx;
	
	//private String order_lst = "abc";
	//private String lst_mode = "mix";
	
    private TabHost myTabHost;
    
    private SharedPreferences sPref;
	private SharedPreferences.Editor prefEdit;
	
	private ConnectivityManager netstate = null; 
	
	private Vector<String> failed_repo = new Vector<String>();
	
	private Intent intp;
	private Intent intserver;

	
	private Handler fetchHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if(pd.isShowing())
				pd.dismiss();
			
	    	startActivityForResult(intp, FETCH_APK);
			super.handleMessage(msg);
		}
		
	};
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mctx = this;
		
		db = new DbHandler(this);

		sPref = getSharedPreferences("aptoide_prefs", MODE_PRIVATE);
		prefEdit = sPref.edit();
		prefEdit.putBoolean("update", true);
		prefEdit.commit();

		myTabHost = getTabHost();
		myTabHost.addTab(myTabHost.newTabSpec("avail").setIndicator(getText(R.string.tab_avail),getResources().getDrawable(android.R.drawable.ic_menu_add)).setContent(new Intent(this, TabAvailable.class)));  
		myTabHost.addTab(myTabHost.newTabSpec("inst").setIndicator(getText(R.string.tab_inst),getResources().getDrawable(android.R.drawable.ic_menu_agenda)).setContent(new Intent(this, TabInstalled.class)));
		myTabHost.addTab(myTabHost.newTabSpec("updt").setIndicator(getText(R.string.tab_updt),getResources().getDrawable(android.R.drawable.ic_menu_info_details)).setContent(new Intent(this, TabUpdates.class)));

		myTabHost.setPersistentDrawingCache(ViewGroup.PERSISTENT_SCROLLING_CACHE);

		netstate = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		
		File sdcard_file = new File(SDCARD);
		if(!sdcard_file.exists()){
			final AlertDialog upd_alrt = new AlertDialog.Builder(mctx).create();
			upd_alrt.setIcon(android.R.drawable.ic_dialog_alert);
			upd_alrt.setTitle(getText(R.string.remote_in_noSD_title));
			upd_alrt.setMessage(getText(R.string.remote_in_noSD));
			upd_alrt.setButton(getText(R.string.btn_ok), new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});
			upd_alrt.show();
		}else{
			StatFs stat = new StatFs(sdcard_file.getPath());
			long blockSize = stat.getBlockSize();
			long totalBlocks = stat.getBlockCount();
			long availableBlocks = stat.getAvailableBlocks();

			long total = (blockSize * totalBlocks)/1024/1024;
			long avail = (blockSize * availableBlocks)/1024/1024;
			Log.d("Aptoide","* * * * * * * * * *");
			Log.d("Aptoide", "Total: " + total + " Mb");
			Log.d("Aptoide", "Available: " + avail + " Mb");

			if(avail < 10 ){
				Log.d("Aptoide","No space left on SDCARD...");
				Log.d("Aptoide","* * * * * * * * * *");

				final AlertDialog upd_alrt = new AlertDialog.Builder(mctx).create();
				upd_alrt.setIcon(android.R.drawable.ic_dialog_alert);
				upd_alrt.setTitle(getText(R.string.remote_in_noSD_title));
				upd_alrt.setMessage(getText(R.string.remote_in_noSDspace));
				upd_alrt.setButton(getText(R.string.btn_ok), new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
				upd_alrt.show();
			}else{
				Log.d("Aptoide","Ok!");
				Log.d("Aptoide","* * * * * * * * * *");

				File local_path = new File(LOCAL_PATH);
				if(!local_path.exists())
					local_path.mkdir();

				File icon_path = new File(ICON_PATH);
				if(!icon_path.exists())
					icon_path.mkdir();


				Vector<ServerNode> srv_lst = db.getServers();
				if (srv_lst.isEmpty()){
					Intent call = new Intent(this, ManageRepo.class);
					call.putExtra("empty", true);
					call.putExtra("uri", "http://apps.aptoide.org");
					startActivityForResult(call,NEWREPO_FLAG);
				}

				Intent i = getIntent();
				if(i.hasExtra("uri")){
					if(i.hasExtra("apks")){
						ArrayList<String> servers_lst = (ArrayList<String>) i.getSerializableExtra("uri");
						if(servers_lst != null && servers_lst.size() > 0){
							intserver = new Intent(this, ManageRepo.class);
							intserver.putExtra("uri", i.getSerializableExtra("uri"));
						}else{
							intserver = null;
						}
						
						final String[] nodi = ((ArrayList<String[]>) i.getSerializableExtra("apks")).get(0);
						final AlertDialog alrt = new AlertDialog.Builder(this).create();
						alrt.setTitle("Install");
						alrt.setMessage("Do you wish to install: " + nodi[1] + " ?");
						alrt.setButton(getText(R.string.btn_yes), new OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								alrt.dismiss();
								pd = ProgressDialog.show(mctx, getText(R.string.top_download), getText(R.string.fetch_apk) + ": " + nodi[1], true);
								pd.setIcon(android.R.drawable.ic_dialog_info);
								
								new Thread(new Runnable() {
									public void run() {
										installFromLink(nodi[0]);
									}
								}).start();

							}
						});
						alrt.setButton2(getText(R.string.btn_no), new OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								alrt.dismiss();
							}
						});
						alrt.show();
					}else{
						Intent call = new Intent(this, ManageRepo.class);
						ArrayList<String> servers_lst = (ArrayList<String>) i.getSerializableExtra("uri");
						if(servers_lst != null && servers_lst.size() > 0){
							call.putExtra("uri", i.getSerializableExtra("uri"));
							startActivityForResult(call,NEWREPO_FLAG);
						}
					}
				}else if(i.hasExtra("newrepo")){
					Intent call = new Intent(this, ManageRepo.class);
					call.putExtra("newrepo", i.getStringExtra("newrepo"));
					startActivityForResult(call,NEWREPO_FLAG);
				}else if(i.hasExtra("linkxml")){
					
				}
			}
		}
	}

	private void installFromLink(String path){
		try{
			String file_out = new String(SDCARD+"/.aptoide/fetched.apk");
			FileOutputStream saveit = new FileOutputStream(file_out);
			DefaultHttpClient mHttpClient = new DefaultHttpClient();
			HttpGet mHttpGet = new HttpGet(path);

			HttpResponse mHttpResponse = mHttpClient.execute(mHttpGet);
			
			if(mHttpResponse.getStatusLine().getStatusCode() == 401){
				 return;
			 }

			InputStream getit = mHttpResponse.getEntity().getContent();
			byte data[] = new byte[8096];
			int readed;
			while((readed = getit.read(data, 0, 8096)) != -1) {
				saveit.write(data,0,readed);
			}
			
			intp = new Intent();
	    	intp.setAction(android.content.Intent.ACTION_VIEW);
	    	intp.setDataAndType(Uri.parse("file://" + file_out), "application/vnd.android.package-archive");
	    	fetchHandler.sendEmptyMessage(0);
		}catch(IOException e) { }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE,UPDATE_REPO,1,R.string.menu_update_repo)
			.setIcon(android.R.drawable.ic_menu_rotate);
		menu.add(Menu.NONE, MANAGE_REPO, 2, R.string.menu_manage)
			.setIcon(android.R.drawable.ic_menu_agenda);
		menu.add(Menu.NONE, SEARCH_MENU,4,R.string.menu_search)
			.setIcon(android.R.drawable.ic_menu_search);
		menu.add(Menu.NONE, SETTINGS, 5, R.string.menu_settings)
			.setIcon(android.R.drawable.ic_menu_preferences);
		menu.add(Menu.NONE, ABOUT,6,R.string.menu_about)
			.setIcon(android.R.drawable.ic_menu_help);
		return true;
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case UPDATE_REPO:
			final AlertDialog upd_alrt = new AlertDialog.Builder(this).create();
			if(!db.areServers()){
				upd_alrt.setIcon(android.R.drawable.ic_dialog_alert);
				upd_alrt.setTitle(getText(R.string.update_repos));
				upd_alrt.setMessage(getText(R.string.updating_norepos));
				upd_alrt.setButton(getText(R.string.btn_ok), new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						
					}
				});
			}else{
				if(netstate.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED){
					upd_alrt.setIcon(android.R.drawable.ic_dialog_alert);
					upd_alrt.setTitle(getText(R.string.update_repos));
					upd_alrt.setMessage(getText(R.string.updating_cfrm));
					upd_alrt.setButton(getText(R.string.btn_yes), new OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							updateRepos();
						}
					});
					upd_alrt.setButton2(getText(R.string.btn_no), new OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							upd_alrt.dismiss();
						}
					});
				}else{
					upd_alrt.setIcon(android.R.drawable.ic_dialog_alert);
					upd_alrt.setTitle(getText(R.string.update_repos));
					upd_alrt.setMessage(getText(R.string.updating_3g));
					upd_alrt.setButton(getText(R.string.btn_yes), new OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							updateRepos();
						}
					});
					upd_alrt.setButton2(getText(R.string.btn_no), new OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							upd_alrt.dismiss();
						}
					});
				}
			}
			upd_alrt.show();
			return true;
		case MANAGE_REPO:
			Intent i = new Intent(this, ManageRepo.class);
			startActivityForResult(i,NEWREPO_FLAG);
			return true;
		case SEARCH_MENU:
			onSearchRequested();
			return true;
		case ABOUT:
			LayoutInflater li = LayoutInflater.from(this);
			View view = li.inflate(R.layout.about, null);			
			Builder p = new AlertDialog.Builder(this).setView(view);
			final AlertDialog alrt = p.create();
			alrt.setIcon(R.drawable.icon);
			alrt.setTitle(R.string.app_name);
			alrt.setButton(getText(R.string.btn_chlog), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int	whichButton) {
					Uri uri = Uri.parse(getString(R.string.change_log_url));
					startActivity(new Intent( Intent.ACTION_VIEW, uri));
				}
			});
			alrt.show();
			return true;
		case SETTINGS:
			Intent s = new Intent(RemoteInTab.this, Settings.class);
			startActivityForResult(s,SETTINGS_FLAG);
			return true;	
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == NEWREPO_FLAG){
			if(data != null && data.hasExtra("update")){
				final AlertDialog alrt = new AlertDialog.Builder(this).create();
				alrt.setTitle(getText(R.string.update_repos));
				alrt.setMessage(getText(R.string.update_main));
				alrt.setButton(getText(R.string.btn_yes), new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						updateRepos();
					}
				});
				alrt.setButton2(getText(R.string.btn_no), new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						alrt.dismiss();
					}
				});
				alrt.show();
			}
		}else if(requestCode == SETTINGS_FLAG){
			
			if(data != null && data.hasExtra("mode")){
				prefEdit.putBoolean("mode", data.getExtras().getBoolean("mode"));
	        	prefEdit.commit();
	        	prefEdit.putBoolean("update", true);
	        	prefEdit.commit();
	        	onResume();
			}

		}else if(requestCode == FETCH_APK){
			if(intserver != null)
				startActivityForResult(intserver, NEWREPO_FLAG);
		}
	}
	
	public boolean updateRepos(){
		pd = ProgressDialog.show(this, getText(R.string.top_please_wait), getText(R.string.updating_msg), true);
		pd.setIcon(android.R.drawable.ic_dialog_info);
		
		//Check for connection first!
		
		if(netstate.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED ||  netstate.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED){
			db.removeAll();
			myTabHost.setCurrentTabByTag("inst");
			new Thread() {
				public void run() {
					try{
						Vector<ServerNode> serv = db.getServers();
						boolean parse = false;
						failed_repo.clear();
						for(ServerNode node: serv){
							if(node.inuse){
								Log.d("Aptoide", "Updating repo: " + node.uri);
								parse = downloadList(node.uri);
								if(parse){
									xmlPass(node.uri,true);
								}else{
									failed_repo.add(node.uri);
								}
							}
						}
					} catch (Exception e) { e.printStackTrace();}
					finally{
						update_handler.sendEmptyMessage(0);
					}
				}
			}.start(); 
			return true;
		}else{
			pd.dismiss();
            Toast.makeText(RemoteInTab.this, getText(R.string.aptoide_error), Toast.LENGTH_LONG).show(); 
			return false;
		}
	}
	
	/*
	 * Pass XML info to BD
	 * @type: true - info.xml
	 * 		  false - extras.xml
	 */
	private void xmlPass(String srv, boolean type){
	    SAXParserFactory spf = SAXParserFactory.newInstance();
	    File xml_file = null;
	    SAXParser sp = null;
    	XMLReader xr = null;
	    try {
	    	sp = spf.newSAXParser();
	    	xr = sp.getXMLReader();
	    	if(type){
	    		RssHandler handler = new RssHandler(this,srv);
	    		xr.setContentHandler(handler);
	    		xr.setErrorHandler(handler);
	    		xml_file = new File(XML_PATH);
	    	}else{
	    		ExtrasRssHandler handler = new ExtrasRssHandler(this, srv);
	    		xr.setContentHandler(handler);
	    		xml_file = new File(EXTRAS_XML_PATH);
	    	}
	    	
	    	InputStreamReader isr = new FileReader(xml_file);
	    	InputSource is = new InputSource(isr);
	    	xr.parse(is);
	    	
	    } catch (Exception e){
	    	xr = null;
		}finally{
			xml_file.delete();
		}
	}
	
	
	/*
	 * Get extras.xml file from server and save it in the SD card 
	 */
	private boolean downloadExtras(String srv){
		String url = srv+REMOTE_EXTRAS_FILE;
		
        try {
        	FileOutputStream saveit = new FileOutputStream(LOCAL_PATH+REMOTE_EXTRAS_FILE);
        	
        	
        	/*HttpParams httpParameters = new BasicHttpParams();
    		HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
    		HttpConnectionParams.setSoTimeout(httpParameters, 5000);
    		DefaultHttpClient mHttpClient = new DefaultHttpClient(httpParameters);
            HttpGet mHttpGet = new HttpGet(url);
            
            String[] logins = null; 
    		logins = db.getLogin(srv);
    		if(logins != null){
    			URL mUrl = new URL(url);
    			mHttpClient.getCredentialsProvider().setCredentials(
                        new AuthScope(mUrl.getHost(), mUrl.getPort()),
                        new UsernamePasswordCredentials(logins[0], logins[1]));
    		}
            
			HttpResponse mHttpResponse = mHttpClient.execute(mHttpGet);*/
        	
        	HttpResponse mHttpResponse = NetworkApis.getHttpResponse(url, srv, mctx);
        	
			/*if(mHttpResponse.getStatusLine().getStatusCode() == 401){
				
			}else if(mHttpResponse.getStatusLine().getStatusCode() == 404){
				
			}else{
				byte[] buffer = EntityUtils.toByteArray(mHttpResponse.getEntity());
                saveit.write(buffer);
			}*/
			if(mHttpResponse.getStatusLine().getStatusCode() == 200){
				byte[] buffer = EntityUtils.toByteArray(mHttpResponse.getEntity());
                saveit.write(buffer);
			}else{
				return false;
				//Does nothing...
			}
			return true;
		} catch (UnknownHostException e){ return false;} 
		  catch (ClientProtocolException e) { return false;} 
		  catch (IOException e) { return false;}
	}
	
	/*
	 * Get info.xml file from server and save it in the SD card
	 */
	private boolean downloadList(String srv){
		String url = srv+REMOTE_FILE;
        try {
        	FileOutputStream saveit = new FileOutputStream(XML_PATH);
        	        	
        	HttpResponse mHttpResponse = NetworkApis.getHttpResponse(url, srv, mctx);
        	
			if(mHttpResponse.getStatusLine().getStatusCode() == 200){
				byte[] buffer = EntityUtils.toByteArray(mHttpResponse.getEntity());
				saveit.write(buffer);
			}else{
				return false;
				//Does nothing
			}
			return true;
		} catch (UnknownHostException e){
			/*Message msg = new Message();
			msg.obj = new String(srv);
			error_handler.sendMessage(msg);*/
			return false;
		} catch (ClientProtocolException e) { return false;} 
		  catch (IOException e) {  return false;}
		  catch (IllegalArgumentException e) { return false;}
	}
	
	
	/*
	 * Handlers for thread functions that need to access GUI
	 */
	private Handler update_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
    		prefEdit.putBoolean("update", true);
        	prefEdit.commit();
        	if(pd.isShowing()){
        		pd.dismiss();
        	}
        	myTabHost.setCurrentTabByTag("avail");
    		
    		if(failed_repo.size() > 0){
    			final AlertDialog p = new AlertDialog.Builder(mctx).create();
    			p.setTitle(getText(R.string.top_error));
    			p.setIcon(android.R.drawable.ic_dialog_alert);
    			String report = getText(R.string.error_on_update).toString();
    			for(String node: failed_repo){
    				report = report.concat(node+"\n");
    			}
    			p.setMessage(report);
    			p.setButton(getText(R.string.btn_ok), new DialogInterface.OnClickListener() {
    			      public void onClick(DialogInterface dialog, int which) {
    			          p.dismiss();
    			        } });
    			p.show();
    		}
        	new Thread() {
				public void run() {
					try{
						Vector<ServerNode> serv = db.getServers();
						boolean parse = false;
						for(ServerNode node: serv){
							if(node.inuse){
								parse = downloadExtras(node.uri);
								if(parse){
									xmlPass(node.uri, false);
								}
							}
						}
					} catch (Exception e) { }
				}
			}.start(); 
        }
	};

	
	/*private Handler secure_error_handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(pd.isShowing())
				pd.dismiss();
			AlertDialog p = new AlertDialog.Builder(mctx).create();
			p.setTitle("Login required");
			p.setIcon(android.R.drawable.ic_dialog_alert);
			p.setMessage("Server: \"" + msg.obj.toString() + "\" requests login.\nCheck your username/password.");
			p.setButton(getText(R.string.btn_ok), new DialogInterface.OnClickListener() {
			      public void onClick(DialogInterface dialog, int which) {
			          return;
			        } });
			p.show();
		}
	};

	private Handler error_handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(pd.isShowing())
				pd.dismiss();
			final AlertDialog p = new AlertDialog.Builder(mctx).create();
			p.setTitle("Time out");
			p.setIcon(android.R.drawable.ic_dialog_alert);
			p.setMessage("Could not connect to server: " + msg.obj.toString());
			p.setButton(getText(R.string.btn_ok), new DialogInterface.OnClickListener() {
			      public void onClick(DialogInterface dialog, int which) {
			          p.dismiss();
			        } });
			p.show();
		}
	};*/


	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	
	

}
