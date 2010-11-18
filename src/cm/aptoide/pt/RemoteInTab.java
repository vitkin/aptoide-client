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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeoutException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;

public class RemoteInTab extends TabActivity implements  OnItemClickListener, OnTabChangeListener{

	private String LOCAL_PATH = "/sdcard/.aptoide";
	private String ICON_PATH = LOCAL_PATH+"/icons";
	private String XML_PATH = LOCAL_PATH+"/remapklst.xml";
	private String EXTRAS_XML_PATH = LOCAL_PATH+"/extras.xml";
	private String APK_PATH = LOCAL_PATH+"/";
	
	private String REMOTE_FILE = "/info.xml";
	private String REMOTE_EXTRAS_FILE = "/extras.xml";
	
	private static final int UPDATE_REPO = Menu.FIRST;
	private static final int MANAGE_REPO = 2;
	private static final int SEARCH_MENU = 3;
	private static final int SD_INSTALL = 4;
	private static final int SETTINGS = 5;
	private static final int ABOUT = 6;
	
	/* ****************************************************************************** */
	private static final int SETTINGS_FLAG = 0;
	private static final int NEWREPO_FLAG = 0;
	private static final int UPDATE_LIST_FLAG = 0;
	/* Horrible Hack!!! onActivityResult will be find by returned Intent, not flag... */
	
	private DbHandler db = null;
	private Vector<ApkNode> apk_lst_iu = new Vector<ApkNode>();
	private Vector<ApkNode> apk_lst_un = new Vector<ApkNode>();
	private Vector<ApkNode> apk_lst_up = new Vector<ApkNode>();
	
	private PackageManager mPm;
	private PackageInfo pkginfo;
	
	private ProgressDialog pd;
	
	private Context mctx = this; 
	
	private String order_lst = "abc";
		
	private String idTab;
	private static final String TAB_IN = "INST";
	private static final String TAB_UN = "UNIN";
	private static final String TAB_UP = "UPDT";
	private static final String TAB_IN_UP = "INUPDT";
	private ListView ls1;
    private ListView ls2;   
    private ListView ls3;
    private TabHost myTabHost;
    private TabSpec ts;
    private TabSpec ts1;
    private TabSpec ts2;


    
    class LstBinder implements ViewBinder
	{

		public boolean setViewValue(View view, Object data, String textRepresentation)
		{
			if(view.getClass().toString().equalsIgnoreCase("class android.widget.RatingBar")){
				RatingBar tmpr = (RatingBar)view;
				tmpr.setRating(new Float(textRepresentation));
			}else if(view.getClass().toString().equalsIgnoreCase("class android.widget.TextView")){
				TextView tmpr = (TextView)view;
				tmpr.setText(textRepresentation);
			}else if(view.getClass().toString().equalsIgnoreCase("class android.widget.ImageView")){
				ImageView tmpr = (ImageView)view;	
				File icn = new File(textRepresentation);
				if(icn.exists() && icn.length() > 0){
					new Uri.Builder().build();
    				tmpr.setImageURI(Uri.parse(textRepresentation));
             	}else{
             		tmpr.setImageResource(android.R.drawable.sym_def_app_icon);
             	}
			}else{
				return false;
			}

			return true;
		}
	}
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.tablist);
				
		File local_path = new File(LOCAL_PATH);
		if(!local_path.exists())
			local_path.mkdir();
		
		File icon_path = new File(ICON_PATH);
		if(!icon_path.exists())
			icon_path.mkdir();
		
		db = new DbHandler(this);
		
		mPm = getPackageManager();
		
		myTabHost = getTabHost();
        myTabHost.setOnTabChangedListener(this);
        ls1 = new ListView(this);             
        ls2 = new ListView(this);
        ls1.setOnItemClickListener(this);
        ls2.setOnItemClickListener(this);
        ls3 = new ListView(this);
        ls3.setOnItemClickListener(this);
        
        Vector<ServerNode> srv_lst = db.getServers();
        if (srv_lst.isEmpty()){
        	Intent call = new Intent(this, ManageRepo.class);
        	call.putExtra("empty", true);
			call.putExtra("uri", "http://aptoide.com/repo");
			startActivityForResult(call,NEWREPO_FLAG);
        }
        
		Intent i = getIntent();
		if(i.hasExtra("uri")){
			Intent call = new Intent(this, ManageRepo.class);
			call.putExtra("uri", i.getStringExtra("uri"));
			startActivityForResult(call,NEWREPO_FLAG);
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		idTab = TAB_UN;
		redraw(true, false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE,UPDATE_REPO,1,R.string.menu_update_repo)
			.setIcon(android.R.drawable.ic_menu_rotate);
		menu.add(Menu.NONE, MANAGE_REPO, 2, R.string.menu_manage)
			.setIcon(android.R.drawable.ic_menu_agenda);
		menu.add(Menu.NONE, SEARCH_MENU,3,R.string.menu_search)
			.setIcon(android.R.drawable.ic_menu_search);
		menu.add(Menu.NONE, SD_INSTALL,4,R.string.menu_sdcard_read)
			.setIcon(android.R.drawable.ic_menu_save);
		menu.add(Menu.NONE, SETTINGS, 5, R.string.menu_settings)
			.setIcon(android.R.drawable.ic_menu_preferences);
		menu.add(Menu.NONE, ABOUT,6,R.string.menu_about)
			.setIcon(android.R.drawable.ic_menu_help);
		return true;
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case UPDATE_REPO:
			updateRepos();
			return true;
		case MANAGE_REPO:
			Intent i = new Intent(this, ManageRepo.class);
			startActivityForResult(i,NEWREPO_FLAG);
			return true;
		case SD_INSTALL:
			Intent y = new Intent(this, SdIn.class);
            startActivity(y);
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
			alrt.setTitle("APTOIDE");
			alrt.setButton("ChangeLog", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int	whichButton) {
					Uri uri = Uri.parse("http://aptoide.com/changelog.html");
					startActivity(new Intent( Intent.ACTION_VIEW, uri));
				}
			});
			alrt.show();
			return true;
		case SETTINGS:
			Intent s = new Intent(RemoteInTab.this, Settings.class);
			s.putExtra("order", order_lst);
			startActivityForResult(s,SETTINGS_FLAG);
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void removeApk(String apk_pkg, int position){
		try {
			pkginfo = mPm.getPackageInfo(apk_pkg, 0);
		} catch (NameNotFoundException e) {	}
		Uri uri = Uri.fromParts("package", pkginfo.packageName, null);
	    Intent intent = new Intent(Intent.ACTION_DELETE, uri);
	    startActivityForResult(intent,position); 
	}
	
	private void installApk(String apk_pkg, int position){
		pkginfo = mPm.getPackageArchiveInfo(apk_pkg, 0);
		Intent intent = new Intent();
    	intent.setAction(android.content.Intent.ACTION_VIEW);
    	intent.setDataAndType(Uri.parse("file://" + apk_pkg), "application/vnd.android.package-archive");
    	
    	Message msg = new Message();
		msg.arg1 = 1;
		download_handler.sendMessage(msg);
    	
    	startActivityForResult(intent,position);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		mPm = getPackageManager();
		if(data != null && data.hasExtra("settings")){
			if(data.hasExtra("align"))
				order_lst = data.getStringExtra("align");
			redraw(false, false);
		}else if(data != null && data.hasExtra("updates")){
			redraw(false, false);
		}else if(data != null && data.hasExtra("newrepo")){
			if(data.hasExtra("update")){
				AlertDialog.Builder ask_alrt = new AlertDialog.Builder(this);
				ask_alrt.setTitle("Update repositories");
				ask_alrt.setIcon(android.R.drawable.ic_menu_rotate);
				ask_alrt.setMessage(getString(R.string.repo_alrt));
				ask_alrt.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int	whichButton) {
						updateRepos();
					}
				});
				ask_alrt.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						return;
					}
				});
				AlertDialog alert = ask_alrt.create();
				alert.show();
			}
		}else{
			if(idTab.equalsIgnoreCase(TAB_IN)){
				db.removeInstalled(apk_lst_iu.get(requestCode).apkid);
			}else if (idTab.equalsIgnoreCase(TAB_UP)){
				try{
					pkginfo = mPm.getPackageInfo(apk_lst_up.get(requestCode).apkid, 0);
				}catch(Exception e){ }
				String vers = pkginfo.versionName;
				int verscode = pkginfo.versionCode;
				db.removeInstalled(apk_lst_up.get(requestCode).apkid);
				db.insertInstalled(apk_lst_up.get(requestCode).apkid, vers, verscode);
			}else if (idTab.equalsIgnoreCase(TAB_IN_UP)){
				try{
					pkginfo = mPm.getPackageInfo(apk_lst_iu.get(requestCode).apkid, 0);
				}catch(Exception e){ }
				String vers = pkginfo.versionName;
				int verscode = pkginfo.versionCode;
				db.removeInstalled(apk_lst_iu.get(requestCode).apkid);
				db.insertInstalled(apk_lst_iu.get(requestCode).apkid, vers, verscode);
			}else{
				db.insertInstalled(apk_lst_un.get(requestCode).apkid);
			}
			redraw(false, false);

		}
	}
	
	/*
	 * Retira a lista da base de dados e apresenta-a
	 */
	private void redraw(boolean update, boolean verbose){
		
		boolean updates = false;
		
		Vector<ApkNode> tmp = db.getAll(order_lst);
		if(update){
			//Applications have been installed or removed outside Aptoide?
			//Typically only executed once, on startup
			getUpdates(tmp);
			tmp = db.getAll(order_lst);
		}
			
		apk_lst_un.clear();
		apk_lst_iu.clear();
		for(ApkNode node: tmp){
			if(node.status == 0)
				apk_lst_un.add(node);
			else{
				apk_lst_iu.add(node);
				//if(verbose && node.status == 2)
				if(node.status == 2)
					updates = true;
			}
		}
		
		myTabHost.setCurrentTabByTag(TAB_UN);
		idTab = TAB_UN;
		myTabHost.setup();
		myTabHost.clearAllTabs();
		myTabHost.setup();
		ts = myTabHost.newTabSpec(TAB_IN);
        
        ts.setIndicator("Installed", getResources().getDrawable(R.drawable.installed));               
                  
        ts.setContent(new TabHost.TabContentFactory(){
             public View createTabContent(String tag)
             {                                            
            	 List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
                 Map<String, Object> apk_line;
                 
                 for(ApkNode node: apk_lst_iu){
                 	apk_line = new HashMap<String, Object>();
                 	if(node.status == 1){
                 		apk_line.put("status", getString(R.string.installed) + " " + node.ver);
                 		apk_line.put("name", node.name);
                 	}else{
                 		apk_line.put("status2", getString(R.string.installed_update) + " " + node.ver);
                 		apk_line.put("name2", node.name);
                 	}
                 	String iconpath = new String(RemoteInTab.this.getString(R.string.icons_path)+node.apkid);
                 	apk_line.put("icon", iconpath);
                 	apk_line.put("rat", node.rat);
                 	result.add(apk_line);
                 }
                 SimpleAdapter show_out = new SimpleAdapter(RemoteInTab.this, result, R.layout.listicons, 
                 		new String[] {"name", "name2", "status", "status2", "icon", "rat"}, new int[] {R.id.name, R.id.nameup, R.id.isinst, R.id.isupdt, R.id.appicon, R.id.rating});
                 
                 show_out.setViewBinder(new RemoteInTab.LstBinder());
                 
                  ls1.setAdapter(show_out);
                  return ls1;
             }         
        }); 
        

        ts1 = myTabHost.newTabSpec(TAB_UN);
        
        ts1.setIndicator("Uninstalled", getResources().getDrawable(android.R.drawable.ic_input_add));
                  
        ts1.setContent(new TabHost.TabContentFactory(){
             public View createTabContent(String tag)
             {                                            
            	 List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
                 Map<String, Object> apk_line;
                 
                 for(ApkNode node: apk_lst_un){
                 	apk_line = new HashMap<String, Object>();
                 	apk_line.put("name", node.name);
            		//apk_line.put("status", getString(R.string.not_inst));
                 	apk_line.put("status", getString(R.string.srv_version) + " " + node.ver);
                 	String iconpath = new String(RemoteInTab.this.getString(R.string.icons_path)+node.apkid);
                 	apk_line.put("icon", iconpath);
                 	apk_line.put("rat", node.rat);
                 	result.add(apk_line);
                 }
                 SimpleAdapter show_out = new SimpleAdapter(RemoteInTab.this, result, R.layout.listicons, 
                 		new String[] {"name", "status", "icon", "rat"}, new int[] {R.id.name, R.id.isinst, R.id.appicon, R.id.rating});
                 
                 show_out.setViewBinder(new RemoteInTab.LstBinder());
                 
                  ls2.setAdapter(show_out);
                  return ls2;
             }         
        }); 
        
        myTabHost.addTab(ts1);
        myTabHost.addTab(ts);
        
        /*if(updates){
        	//If there are updates to any program, ask user
        	AlertDialog.Builder update_alrt = new AlertDialog.Builder(this);
			update_alrt.setTitle("Updates available");
			update_alrt.setIcon(android.R.drawable.ic_dialog_info);
			update_alrt.setMessage(getString(R.string.update_alrt));
			update_alrt.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int	whichButton) {
					//call update list activity
					Intent call = new Intent(RemoteInTab.this, UpdateList.class);
					startActivityForResult(call,UPDATE_LIST_FLAG);
				}
			});
			update_alrt.setNegativeButton("No", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					return;
				}
			});
			AlertDialog alert = update_alrt.create();
			alert.show();
        }*/
        if(updates){
        	apk_lst_up = db.getUpdates(order_lst);
        	ts2 = myTabHost.newTabSpec(TAB_UP);
            ts2.setIndicator("Updates", getResources().getDrawable(R.drawable.updates));
            ts2.setContent(new TabHost.TabContentFactory(){
            	public View createTabContent(String tag)
            	{                                            
            		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
            		Map<String, Object> apk_line;

            		for(ApkNode node: apk_lst_up){
            			apk_line = new HashMap<String, Object>();
            			apk_line.put("name", node.name);
            			//apk_line.put("status", getString(R.string.not_inst));
            			apk_line.put("status", getString(R.string.installed_update) + " " + node.ver);
            			String iconpath = new String(RemoteInTab.this.getString(R.string.icons_path)+node.apkid);
            			apk_line.put("icon", iconpath);
            			apk_line.put("rat", node.rat);
            			result.add(apk_line);
            		}
            		SimpleAdapter show_out = new SimpleAdapter(RemoteInTab.this, result, R.layout.listicons, 
            				new String[] {"name", "status", "icon", "rat"}, new int[] {R.id.nameup, R.id.isupdt, R.id.appicon, R.id.rating});

            		show_out.setViewBinder(new RemoteInTab.LstBinder());

            		ls3.setAdapter(show_out);
            		return ls3;
            	}         
            }); 
            myTabHost.addTab(ts2);
        }

	}

	private void getUpdates(Vector<ApkNode> apks){
		for(ApkNode node: apks){
			if(node.status == 0){
				try{
					pkginfo = mPm.getPackageInfo(node.apkid, 0);
					String vers = pkginfo.versionName;
					int verscode = pkginfo.versionCode;
					db.insertInstalled(node.apkid, vers, verscode);
				}catch(Exception e) {
					//Not installed anywhere... does nothing
				}
			}else{
				try{
					pkginfo = mPm.getPackageInfo(node.apkid, 0);
				}catch (Exception e){
					db.removeInstalled(node.apkid);
				}
			}
		}
	}
	
	public boolean updateRepos(){
		pd = ProgressDialog.show(this, "Please Wait", "Updating applications list...", true);
		pd.setIcon(android.R.drawable.ic_dialog_info);
		
		//Check for connection first!
		ConnectivityManager netstate = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE); 
		if(netstate.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED ||  netstate.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED){		
			db.removeAll();
			new Thread() {
				public void run() {
					try{
						Vector<ServerNode> serv = db.getServers();
						for(ServerNode node: serv){
							if(node.inuse){
								downloadList(node.uri);
								xmlPass(node.uri,true);
							}
						}
					} catch (Exception e) { }
					update_handler.sendEmptyMessage(0);
				}
			}.start(); 
			return true;
		}else{
			pd.dismiss();
            Toast.makeText(RemoteInTab.this, "Could not connect to the network.", Toast.LENGTH_LONG).show(); 
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
	    try {
	    	File xml_file = null;
	    	SAXParser sp = spf.newSAXParser();
	    	XMLReader xr = sp.getXMLReader();
	    	if(type){
	    		RssHandler handler = new RssHandler(this,srv);
	    		xr.setContentHandler(handler);
	    		xml_file = new File(XML_PATH);
	    	}else{
	    		ExtrasRssHandler handler = new ExtrasRssHandler(this, srv);
	    		xr.setContentHandler(handler);
	    		xml_file = new File(EXTRAS_XML_PATH);
	    	}
	    	
	    	InputStreamReader isr = new FileReader(xml_file);
	    	InputSource is = new InputSource(isr);
	    	xr.parse(is);
	    	xml_file.delete();
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
	}
	
	/*
	 * Get extras.xml file from server and save it in the SD card 
	 */
	private void downloadExtras(String srv){
		try{
			BufferedInputStream getit = new BufferedInputStream(new URL(srv+REMOTE_EXTRAS_FILE).openStream());

			File file_teste = new File(LOCAL_PATH+REMOTE_EXTRAS_FILE);
			if(file_teste.exists())
				file_teste.delete();
			
			FileOutputStream saveit = new FileOutputStream(LOCAL_PATH+REMOTE_EXTRAS_FILE);
			BufferedOutputStream bout = new BufferedOutputStream(saveit,1024);
			byte data[] = new byte[1024];
			
			int readed = getit.read(data,0,1024);
			while(readed != -1) {
				bout.write(data,0,readed);
				readed = getit.read(data,0,1024);
			}
			bout.close();
			getit.close();
			saveit.close();
		}catch (UnknownHostException e){
			Message msg = new Message();
			msg.obj = new String(srv);
			error_handler.sendMessage(msg);
		}catch(Exception e){ }
	}
	
	/*
	 * Get info.xml file from server and save it in the SD card
	 */
	private void downloadList(String srv){
		try{
			BufferedInputStream getit = new BufferedInputStream(new URL(srv+REMOTE_FILE).openStream());

			File file_teste = new File(XML_PATH);
			if(file_teste.exists())
				file_teste.delete();
			
			FileOutputStream saveit = new FileOutputStream(XML_PATH);
			BufferedOutputStream bout = new BufferedOutputStream(saveit,1024);
			byte data[] = new byte[1024];
			
			int readed = getit.read(data,0,1024);
			while(readed != -1) {
				bout.write(data,0,readed);
				readed = getit.read(data,0,1024);
			}
			bout.close();
			getit.close();
			saveit.close();
		}catch (UnknownHostException e){
			Message msg = new Message();
			msg.obj = new String(srv);
			error_handler.sendMessage(msg);
		}catch(Exception e){ }
	}
	
	private String downloadFile(String apkid){
		Vector<String> tmp_serv = new Vector<String>();
		String getserv = new String();
		String md5hash = null;
		String[] srv_hash;
		try{
			
			tmp_serv = db.getPathHash(apkid);

			
			
			for(String serv: tmp_serv){
				srv_hash = serv.split("\\*");
				if(srv_hash[0] != null){
					getserv = srv_hash[0];
					md5hash = srv_hash[1];
					break;
				}
			}
			
			if(getserv.length() == 0)
				throw new TimeoutException();
						
			Message msg = new Message();
			msg.arg1 = 0;
			msg.obj = new String(getserv);
			download_handler.sendMessage(msg);
			
			BufferedInputStream getit = new BufferedInputStream(new URL(getserv).openStream());

			String path = new String(APK_PATH+db.getName(apkid)+".apk");
				
			FileOutputStream saveit = new FileOutputStream(path);
			BufferedOutputStream bout = new BufferedOutputStream(saveit,1024);
			byte data[] = new byte[1024];
			
			int readed = getit.read(data,0,1024);
			while(readed != -1) {
				bout.write(data,0,readed);
				readed = getit.read(data,0,1024);
			}
			bout.close();
			getit.close();
			saveit.close();
			File f = new File(path);
			Md5Handler hash = new Md5Handler();
			
			if(md5hash.equalsIgnoreCase("null") || md5hash.equalsIgnoreCase(hash.md5Calc(f))){
				return path;
			}else{
				return null;
			}
		} catch(Exception e){
			return null;
		}
	}

	
	/*
	 * Handlers for thread functions that need to access GUI
	 */
	private Handler update_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	redraw(true, true);
        	if(pd.isShowing())
        		pd.dismiss();
        	new Thread() {
				public void run() {
					try{
						Vector<ServerNode> serv = db.getServers();
						for(ServerNode node: serv){
							if(node.inuse){
								downloadExtras(node.uri);
								xmlPass(node.uri, false);
							}
						}
					} catch (Exception e) { }
				}
			}.start(); 
        }
	};


	private Handler error_handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(pd.isShowing())
				pd.dismiss();
			AlertDialog p = new AlertDialog.Builder(mctx).create();
			p.setTitle("Time out");
			p.setIcon(android.R.drawable.ic_dialog_alert);
			p.setMessage("Could not connect to server: < " + msg.obj.toString() + " >");
			p.setButton("Ok", new DialogInterface.OnClickListener() {
			      public void onClick(DialogInterface dialog, int which) {
			          return;
			        } });
			p.show();
		}
	};
	
	private Handler download_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	if(msg.arg1 == 0){
        		pd = ProgressDialog.show(mctx, "Download", getString(R.string.download_alrt) + msg.obj.toString(), true);
        	}else{
        		pd.dismiss();
        	}
        }
	};
	
	private Handler download_error_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	Toast.makeText(mctx, getString(R.string.error_download_alrt), Toast.LENGTH_LONG).show();
        }
	};

	
	/*
	 * Tab related functions
	 */
	public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
		final ApkNode pkgi;
		if(idTab.equalsIgnoreCase(TAB_IN)){
			pkgi = apk_lst_iu.get(arg2);
		}else if(idTab.equalsIgnoreCase(TAB_UN)){
			pkgi = apk_lst_un.get(arg2);
		}else{
			pkgi = apk_lst_up.get(arg2);
		}
		Vector<String> tmp_get = db.getApk(pkgi.apkid);
		//final AlertDialog p = new AlertDialog.Builder(this).create();
		String tmp_path = this.getString(R.string.icons_path)+pkgi.apkid;
		File test_icon = new File(tmp_path);
		/*if(test_icon.exists() && test_icon.length() > 0){
			p.setIcon(new BitmapDrawable(tmp_path));
		}else{
			p.setIcon(android.R.drawable.sym_def_app_icon);
		}*/
		/*p.setTitle(pkgi.name);
		p.setMessage(getString(R.string.up_server) + tmp_get.firstElement() + 
						"\n\n"+ getString(R.string.lstver) + " " + tmp_get.get(1) +
						"\n\n"+ getString(R.string.isinst) + " " + tmp_get.get(2) + 
						"\n\n"+ getString(R.string.instver)+ " " + tmp_get.get(3) + 
						"\n\n"+ getString(R.string.about_alrt) + db.getDescript(pkgi.apkid));*/
		
		LayoutInflater li = LayoutInflater.from(this);
		View view = li.inflate(R.layout.alertscroll, null);
		Builder alrt = new AlertDialog.Builder(this).setView(view);
		final AlertDialog p = alrt.create();
		if(test_icon.exists() && test_icon.length() > 0){
			p.setIcon(new BitmapDrawable(tmp_path));
		}else{
			p.setIcon(android.R.drawable.sym_def_app_icon);
		}
		p.setTitle(pkgi.name);
		TextView t1 = (TextView) view.findViewById(R.id.n11);
		t1.setText(tmp_get.firstElement());
		TextView t2 = (TextView) view.findViewById(R.id.n22);
		t2.setText(tmp_get.get(1));
		TextView t3 = (TextView) view.findViewById(R.id.n33);
		t3.setText(tmp_get.get(2));
		TextView t4 = (TextView) view.findViewById(R.id.n44);
		t4.setText(tmp_get.get(3));
		TextView t5 = (TextView) view.findViewById(R.id.n55);
		String tmpi = db.getDescript(pkgi.apkid);
		if(!(tmpi == null)){
			t5.setText(tmpi);
		}else{
			t5.setText("No info availale on server. Search market by pressing the button below for more info.");
		}
		
		
		p.setButton("Ok", new DialogInterface.OnClickListener() {
		      public void onClick(DialogInterface dialog, int which) {
		          return;
		        } });
		if(tmp_get.get(2).equalsIgnoreCase("\tno\n")){
			p.setButton2(getString(R.string.install), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					p.dismiss();					
					new Thread() {
						public void run() {
							String apk_pkg = downloadFile(pkgi.apkid);
							if(apk_pkg == null){
								Message msg = new Message();
								msg.arg1 = 1;
								download_handler.sendMessage(msg);
								download_error_handler.sendEmptyMessage(0);
							}else{
								installApk(apk_pkg, arg2);
							}
						}
					}.start(); 	
				} });
			p.setButton3("Search Market", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					p.dismiss();					
					Intent intent = new Intent();
			    	intent.setAction(android.content.Intent.ACTION_VIEW);
			    	intent.setData(Uri.parse("market://details?id="+pkgi.apkid));
			    	startActivity(intent);
				} });
		}else{
			p.setButton2(getString(R.string.rem), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					String apk_pkg = apk_lst_iu.get(arg2).apkid;
					removeApk(apk_pkg, arg2);
				} });
			if(pkgi.status == 2){
				p.setButton3(getString(R.string.update), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						p.dismiss();
						new Thread() {
							public void run() {
								String apk_pkg = downloadFile(pkgi.apkid);
								if(apk_pkg == null){
									Message msg = new Message();
									msg.arg1 = 1;
									download_handler.sendMessage(msg);
									download_error_handler.sendEmptyMessage(0);
								}else{
									idTab = TAB_IN_UP;
									installApk(apk_pkg, arg2);
								}
							}
						}.start();
					} });
			}else{
				p.setButton3("Search Market", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						p.dismiss();					
						Intent intent = new Intent();
				    	intent.setAction(android.content.Intent.ACTION_VIEW);
				    	intent.setData(Uri.parse("market://details?id="+pkgi.apkid));
				    	startActivity(intent);
					} });
			}
		}
		p.show();
	}

	public void onTabChanged(String tabId) {
		idTab = tabId;		
	}
}
