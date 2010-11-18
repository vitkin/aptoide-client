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
import org.apache.http.util.EntityUtils;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SimpleAdapter.ViewBinder;

public class RemoteInSearch extends ListActivity{
	
	private String LOCAL_PATH = "/sdcard/.aptoide";
	private String APK_PATH = LOCAL_PATH+"/";
	
	private static final int MANAGE_REPO = Menu.FIRST;
	private static final int SEARCH_MENU = 2;
	private static final int SETTINGS = 3;
	private static final int ABOUT = 4;
	
	private DbHandler db = null;
	private Vector<ApkNode> apk_lst = null;
	
	private PackageManager mPm;
	private PackageInfo pkginfo;

	private static final int SETTINGS_FLAG = 0;
	
	private ProgressDialog pd;
	
	private Context mctx = this; 

	private String query;
	
	private String order_lst = "abc";
	
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
		
		getListView().setFastScrollEnabled(true);

		Intent i = getIntent();
		query = i.getStringExtra(SearchManager.QUERY);
		apk_lst = db.getSearch(query,order_lst);
	}
		
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		query = intent.getStringExtra(SearchManager.QUERY);
		apk_lst = db.getSearch(query,order_lst);
	}

	@Override
	protected void onResume() {
		super.onResume();
		redraw();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, MANAGE_REPO, 1, R.string.menu_manage)
			.setIcon(android.R.drawable.ic_menu_agenda);
		menu.add(Menu.NONE, SEARCH_MENU,2,R.string.menu_search)
			.setIcon(android.R.drawable.ic_menu_search);
		menu.add(Menu.NONE, SETTINGS, 3, R.string.menu_settings)
			.setIcon(android.R.drawable.ic_menu_preferences);
		menu.add(Menu.NONE, ABOUT,4,R.string.menu_about)
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
			Intent s = new Intent(RemoteInSearch.this, Settings.class);
			s.putExtra("order", order_lst);
			startActivityForResult(s,SETTINGS_FLAG);
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	@Override
	protected void onListItemClick(ListView l, View v, final int position, long id) {
		
		super.onListItemClick(l, v, position, id);
		
		Vector<String> tmp_get = db.getApk(apk_lst.get(position).apkid);

		String tmp_path = this.getString(R.string.icons_path)+apk_lst.get(position).apkid;
		File test_icon = new File(tmp_path);
		
		LayoutInflater li = LayoutInflater.from(this);
		View view = li.inflate(R.layout.alertscroll, null);
		Builder alrt = new AlertDialog.Builder(this).setView(view);
		final AlertDialog p = alrt.create();
		if(test_icon.exists() && test_icon.length() > 0){
			p.setIcon(new BitmapDrawable(tmp_path));
		}else{
			p.setIcon(android.R.drawable.sym_def_app_icon);
		}
		p.setTitle(apk_lst.get(position).name);
		TextView t1 = (TextView) view.findViewById(R.id.n11);
		t1.setText(tmp_get.firstElement());
		TextView t2 = (TextView) view.findViewById(R.id.n22);
		t2.setText(tmp_get.get(1));
		TextView t3 = (TextView) view.findViewById(R.id.n33);
		t3.setText(tmp_get.get(2));
		TextView t4 = (TextView) view.findViewById(R.id.n44);
		t4.setText(tmp_get.get(3));
		TextView t5 = (TextView) view.findViewById(R.id.n55);
		String tmpi = db.getDescript(apk_lst.get(position).apkid);
		if(!(tmpi == null)){
			t5.setText(tmpi);
		}else{
			t5.setText("No info availale on server. Search market by pressing the button below for more info.");
		}
		
		p.setButton2("Ok", new DialogInterface.OnClickListener() {
		      public void onClick(DialogInterface dialog, int which) {
		          return;
		        } });
		if(tmp_get.get(2).equalsIgnoreCase("\tno\n")){
			p.setButton(getString(R.string.install), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					p.dismiss();
					new Thread() {
						public void run() {
							String apk_pkg = downloadFile(position);
							if(apk_pkg == null){
								Message msg = new Message();
								msg.arg1 = 1;
								download_handler.sendMessage(msg);
								download_error_handler.sendEmptyMessage(0);
							}else{
								installApk(apk_pkg, position);
							}
						}
					}.start();
				} });
			p.setButton3("Search Market", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					p.dismiss();					
					Intent intent = new Intent();
			    	intent.setAction(android.content.Intent.ACTION_VIEW);
			    	intent.setData(Uri.parse("market://details?id="+apk_lst.get(position).apkid));
			    	startActivity(intent);
				} });
		}else{ 
			p.setButton(getString(R.string.rem), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					String apk_pkg = apk_lst.get(position).apkid;
					removeApk(apk_pkg, position);
				} });
			if(apk_lst.get(position).status == 2){
				p.setButton3(getString(R.string.update), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						p.dismiss();
						new Thread() {
							public void run() {
								String apk_pkg = downloadFile(position);
								if(apk_pkg == null){
									//Toast.makeText(RemoteInSearch.this, "Could not connect to server!", Toast.LENGTH_LONG).show();
									Message msg = new Message();
									msg.arg1 = 1;
									download_handler.sendMessage(msg);
									download_error_handler.sendEmptyMessage(0);
								}else{
									installApk(apk_pkg, position);
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
				    	intent.setData(Uri.parse("market://details?id="+apk_lst.get(position).apkid));
				    	startActivity(intent);
					} });
			}
		}
		p.show();
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
    	
    	Message msg = new Message();
		msg.arg1 = 1;
		download_handler.sendMessage(msg);
    	
    	startActivityForResult(intent,position);
	}
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mPm = getPackageManager();
		if(data != null && data.hasExtra("settings")){
			if(data.hasExtra("align"))
				order_lst = data.getStringExtra("align");
			redraw();
		}else{
			List<PackageInfo> getapks = mPm.getInstalledPackages(0);
			for(PackageInfo node: getapks){
				if(node.packageName.equalsIgnoreCase(pkginfo.packageName)){
					db.insertInstalled(apk_lst.get(requestCode).apkid);
					redraw();
					return;
				}
			}
			db.removeInstalled(apk_lst.get(requestCode).apkid);
			redraw();
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
        setListAdapter(show_out);
	}

	private String downloadFile(int position){
		Vector<DownloadNode> tmp_serv = new Vector<DownloadNode>();
		String getserv = new String();
		String md5hash = null;
		String repo = null;

		try{
			tmp_serv = db.getPathHash(apk_lst.get(position).apkid);

			if(tmp_serv.size() > 0){
				DownloadNode node = tmp_serv.get(0);
				getserv = node.repo + "/" + node.path;
				md5hash = node.md5h;
				repo = node.repo;
			}

			if(getserv.length() == 0)
				throw new TimeoutException();

			Message msg = new Message();
			msg.arg1 = 0;
			msg.obj = new String(getserv);
			download_handler.sendMessage(msg);

			/*BufferedInputStream getit = new BufferedInputStream(new URL(getserv).openStream());

			String path = new String(APK_PATH+apk_lst.get(position).name+".apk");

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
			saveit.close();*/

			String path = new String(APK_PATH+apk_lst.get(position).name+".apk");
			FileOutputStream saveit = new FileOutputStream(path);
			DefaultHttpClient mHttpClient = new DefaultHttpClient();
			HttpGet mHttpGet = new HttpGet(getserv);

			String[] logins = null; 
			logins = db.getLogin(repo);
			if(logins != null){
				URL mUrl = new URL(getserv);
				mHttpClient.getCredentialsProvider().setCredentials(
						new AuthScope(mUrl.getHost(), mUrl.getPort()),
						new UsernamePasswordCredentials(logins[0], logins[1]));
			}

			HttpResponse mHttpResponse = mHttpClient.execute(mHttpGet);
			if(mHttpResponse.getStatusLine().getStatusCode() == 401){
				return null;
			}else{
				byte[] buffer = EntityUtils.toByteArray(mHttpResponse.getEntity());
				saveit.write(buffer);
			}


			File f = new File(path);
			Md5Handler hash = new Md5Handler();
			if(md5hash == null || md5hash.equalsIgnoreCase(hash.md5Calc(f))){
				return path;
			}else{
				return null;
			}
		} catch(Exception e){
			return null;
		}
	}
	
	/*
	 * UI Handlers
	 * 
	 */
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
}
