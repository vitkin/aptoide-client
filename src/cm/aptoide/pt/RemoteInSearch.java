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
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeoutException;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SimpleAdapter.ViewBinder;

public class RemoteInSearch extends ListActivity{
	
	private ListView lv = null;
	private int pos = -1;
	
	private String LOCAL_APK_PATH = Environment.getExternalStorageDirectory().getPath()+"/.aptoide/";
	
	private static final int MANAGE_REPO = Menu.FIRST;
	private static final int CHANGE_FILTER = 2;
	private static final int SEARCH_MENU = 3;
	private static final int SETTINGS = 4;
	private static final int ABOUT = 5;
	
	static protected SharedPreferences sPref;
	static protected SharedPreferences.Editor prefEdit;
	
	private DbHandler db = null;
	private Vector<ApkNode> apk_lst = null;
	
	private PackageManager mPm;
	private PackageInfo pkginfo;

	private static final int SETTINGS_FLAG = 0;
	
	private ProgressDialog pd;
	
	private Context mctx = this; 

	private String query;
	
	private String order_lst = "abc";
	
	private View baz_search = null;
	
	private DownloadQueueService downloadQueueService;
	private ServiceConnection serviceConnection = new ServiceConnection() {
	    public void onServiceConnected(ComponentName className, IBinder serviceBinder) {
	        // This is called when the connection with the service has been
	        // established, giving us the service object we can use to
	        // interact with the service.  Because we have bound to a explicit
	        // service that we know is running in our own process, we can
	        // cast its IBinder to a concrete class and directly access it.
	        downloadQueueService = ((DownloadQueueService.DownloadQueueBinder)serviceBinder).getService();

	        Log.d("Aptoide-RemoteInSearch", "DownloadQueueService bound to RemoteInSearch");
	    }
	    
	    public void onServiceDisconnected(ComponentName className) {
	        // This is called when the connection with the service has been
	        // unexpectedly disconnected -- that is, its process crashed.
	        // Because it is running in our same process, we should never
	        // see this happen.
	        downloadQueueService = null;
	        
	        Log.d("Aptoide-RemoteInSearch","DownloadQueueService unbound from RemoteInSearch");
	    }

	};
	
	
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
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.list);
				
		db = new DbHandler(this);
		
		mPm = getPackageManager();
		
		sPref = getSharedPreferences("aptoide_prefs", MODE_PRIVATE);
		prefEdit = sPref.edit();
		
		lv = getListView();
		lv.setFastScrollEnabled(true);
		lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		Intent i = getIntent();
		query = i.getStringExtra(SearchManager.QUERY);
		
		query = query.replaceAll("[\\%27]|[\\']|[\\-\\-]|[\\%23]|[#]", " ");
		
		apk_lst = db.getSearch(query,order_lst);
		
		bindService(new Intent(getApplicationContext(), DownloadQueueService.class), serviceConnection, Context.BIND_AUTO_CREATE);
	}
		
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		query = intent.getStringExtra(SearchManager.QUERY);
		
		query = query.replaceAll("[\\%27]|[\\']|[\\-\\-]|[\\%23]|[#]", " ");
		
		apk_lst = db.getSearch(query,order_lst);
	}

	@Override
	protected void onResume() {
		super.onResume();
		redraw();
		lv.setSelection(pos-1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, MANAGE_REPO, 1, R.string.menu_manage)
			.setIcon(android.R.drawable.ic_menu_agenda);
		menu.add(Menu.NONE, CHANGE_FILTER, 2, R.string.menu_order)
		.setIcon(android.R.drawable.ic_menu_sort_by_size);
		menu.add(Menu.NONE, SEARCH_MENU,3,R.string.menu_search)
			.setIcon(android.R.drawable.ic_menu_search);
		menu.add(Menu.NONE, SETTINGS, 4, R.string.menu_settings)
			.setIcon(android.R.drawable.ic_menu_preferences);
		menu.add(Menu.NONE, ABOUT,5,R.string.menu_about)
			.setIcon(android.R.drawable.ic_menu_help);
		return true;
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case MANAGE_REPO:
			Intent i = new Intent(this, ManageRepo.class);
			startActivity(i);
			return true;
		case SEARCH_MENU:
			onSearchRequested();
			return true;
		case ABOUT:
			LayoutInflater li = LayoutInflater.from(this);
			View view = li.inflate(R.layout.about, null);
			TextView info = (TextView)view.findViewById(R.id.about11);
			info.setText(mctx.getString(R.string.about_txt11, mctx.getString(R.string.ver_str)));
			Builder p = new AlertDialog.Builder(this).setView(view);
			final AlertDialog alrt = p.create();
			alrt.setIcon(R.drawable.icon);
			alrt.setTitle(getText(R.string.app_name));
			alrt.setButton(getText(R.string.btn_chlog), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int	whichButton) {
					Uri uri = Uri.parse(getString(R.string.change_log_url));
					startActivity(new Intent( Intent.ACTION_VIEW, uri));
				}
			});
			alrt.show();
			return true;
		case SETTINGS:
			Intent s = new Intent(RemoteInSearch.this, Settings.class);
			s.putExtra("order", order_lst);
			startActivityForResult(s,SETTINGS_FLAG);
			return true;
		case CHANGE_FILTER:
			if(order_lst.equalsIgnoreCase("abc"))
				order_lst = "iu";
			else if(order_lst.equalsIgnoreCase("iu"))
				order_lst = "recent";
			else
				order_lst = "abc";
			redraw();
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	@Override
	protected void onListItemClick(ListView l, View v, final int position, long id) {
		
		super.onListItemClick(l, v, position, id);
		
		pos = position;
		
		String apkid = apk_lst.get(position).apkid;
		String name = apk_lst.get(position).name;
		Vector<String> tmp_get = db.getApk(apkid);

		String tmp_path = this.getString(R.string.icons_path)+apkid;
		
		Intent apkinfo = new Intent(mctx, ApkInfo.class);
		apkinfo.putExtra("position", position);
		apkinfo.putExtra("icon", tmp_path);
		apkinfo.putExtra("apk_id", apkid);
		apkinfo.putExtra("name", name);
		
		String tmpi = db.getDescript(apkid);
		if(!(tmpi == null)){
			apkinfo.putExtra("about",tmpi);
		}else{
			apkinfo.putExtra("about",getText(R.string.app_pop_up_no_info));
		}
		
		apkinfo.putExtra("server", tmp_get.firstElement());
		apkinfo.putExtra("version", tmp_get.get(1));
		apkinfo.putExtra("dwn", tmp_get.get(4));
		apkinfo.putExtra("rat", tmp_get.get(5));
		apkinfo.putExtra("size", tmp_get.get(6));
		
		if(apk_lst.get(position).status == 0){
			apkinfo.putExtra("type", 0);
		}else{
			apkinfo.putExtra("type", 1);
		}
		
		startActivityForResult(apkinfo,30);
		
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
		pkginfo = mPm.getPackageArchiveInfo(apk_pkg, 0); //variavel global usada no retorno da instalacao
		Intent intent = new Intent();
    	intent.setAction(android.content.Intent.ACTION_VIEW);
    	intent.setDataAndType(Uri.parse("file://" + apk_pkg), "application/vnd.android.package-archive");
    	
    	startActivityForResult(intent,position);	//TODO 	passar este método e o correspondente result para a classe Aptoide
    												//		unificando os códigos desta classe com o da remoteintab
	}
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d("Aptoide", "Position is: " + (pos-1));
		if(requestCode == 30){
			if(data != null){
				String apkid = data.getStringExtra("apkid");
				final int pos = data.getIntExtra("position", -1);
				if(pos > -1){
					if(data.getBooleanExtra("in", false)){
						Log.d("Aptoide","This: " + apkid + " - " + pos + " - Install");
						//installApk(apkid, pos);
						/*new Thread() {
							public void run() {*/
						//String apk_pkg = downloadFile(pos);
						queueDownload(pos);
						/*Message msg_alt = new Message();
						if(apk_pkg == null){
							Message msg = new Message();
							msg.arg1 = 1;
							download_handler.sendMessage(msg);
							msg_alt.arg1 = 1;
							download_error_handler.sendMessage(msg_alt);
						}else if(apk_pkg.equals("*md5*")){
							Message msg = new Message();
							msg.arg1 = 1;
							download_handler.sendMessage(msg);
							msg_alt.arg1 = 0;
							download_error_handler.sendMessage(msg_alt);
						}else{
							installApk(apk_pkg, pos);
						}*/
						/*}
						}.start();*/
					}else if(data.getBooleanExtra("rm", false)){
						Log.d("Aptoide","This: " + apkid + " - " + pos + " - Remove");
						removeApk(apkid, pos);
					}
				}
			}
		}else{

			mPm = getPackageManager();
			if(data != null && data.hasExtra("settings")){
				if(data.hasExtra("align"))
					order_lst = data.getStringExtra("align");
				redraw();
				lv.setSelection(pos-1);
			}else{
				List<PackageInfo> getapks = mPm.getInstalledPackages(0);
				for(PackageInfo node: getapks){
					if(node.packageName.equalsIgnoreCase(pkginfo.packageName)){
						db.insertInstalled(apk_lst.get(requestCode).apkid);
						prefEdit.putBoolean("search_updt", true);
						prefEdit.commit();
						redraw();
						lv.setSelection(pos-1);
						return;
					}
				}
				db.removeInstalled(apk_lst.get(requestCode).apkid);
				prefEdit.putBoolean("search_updt", true);
				prefEdit.commit();
				redraw();
				lv.setSelection(pos-1);
			}
		}
	}


	/*
	 * Retira a lista da base de dados e apresenta-a
	 */
	private void redraw(){

		apk_lst = db.getSearch(query,order_lst);
		
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        Map<String, Object> apk_line;
        
        for(ApkNode node: apk_lst){
        	apk_line = new HashMap<String, Object>();
        	if(node.status == 0){
        		apk_line.put("status", getString(R.string.not_inst));
        		apk_line.put("name", node.name);
        	}else if(node.status == 1){
        		apk_line.put("status", getString(R.string.installed) + " " + node.ver);
        		apk_line.put("name", node.name);
        	}else{
        		apk_line.put("status2", getString(R.string.installed_update) + " " + node.ver);
        		apk_line.put("name2", node.name);
        	}
        	String iconpath = new String(this.getString(R.string.icons_path)+node.apkid);
        	apk_line.put("icon", iconpath);
         	apk_line.put("rat", node.rat);
        	result.add(apk_line);
        }
        SimpleAdapter show_out = new SimpleAdapter(this, result, R.layout.listicons, 
        		new String[] {"name", "name2", "status", "status2", "icon", "rat"}, new int[] {R.id.name, R.id.nameup,  R.id.isinst, R.id.isupdt, R.id.appicon, R.id.rating});
        show_out.setViewBinder(new RemoteInSearch.LstBinder());
        
        if(baz_search != null)
        	getListView().removeFooterView(baz_search);
        
        baz_search = View.inflate(this, R.layout.bzzsrch, null);
        
        Button search_baz = (Button) baz_search.findViewById(R.id.baz_src);
        search_baz.setText("Search '" + query + "' on Bazaar");
        search_baz.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				String url = "http://m.bazaarandroid.com/searchview.php?search="+query;
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}
		});
        
        getListView().addFooterView(baz_search);
        
        setListAdapter(show_out);

	}

	private void queueDownload(final int position){
		
		Vector<DownloadNode> tmp_serv = new Vector<DownloadNode>();	
		
		try{
		
			final String apkid = apk_lst.get(position).apkid;
			tmp_serv = db.getPathHash(apkid);
			
			String localPath = new String(LOCAL_APK_PATH+apk_lst.get(position).apkid+".apk");
			
			//if(tmp_serv.size() > 0){
			DownloadNode downloadNode = new DownloadNode();
			downloadNode = tmp_serv.firstElement();
			final String remotePath = downloadNode.repo + "/" + downloadNode.path;
			
			//}
	
			if(remotePath.length() == 0)
				throw new TimeoutException();
			
			String[] logins = null; 
			logins = db.getLogin(downloadNode.repo);
					
			//TODO refactor DownloadNode to include all needed fields @dsilveira
			downloadQueueService.startDownload(position, localPath, downloadNode, apkid, logins, mctx);
	
		} catch(Exception e){	}
	}
	
}
