package cm.aptoide.pt;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.TimeoutException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.DropBoxManager.Entry;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RatingBar;
import android.widget.SectionIndexer;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;

public class BaseManagement extends Activity {

	static private PackageManager mPm;
	static private PackageInfo pkginfo;

	static private DbHandler db = null;
	static private Context mctx = null;

	static protected SharedPreferences sPref;
	static protected SharedPreferences.Editor prefEdit;

	static private ProgressDialog pd;

	static protected Vector<ApkNode> apk_lst = null;

	static private boolean pop_change = false;

	static private String order_lst = "abc";

	static protected SimpleAdapter availAdpt = null;
	static protected SimpleAdapter instAdpt = null;
	static protected SimpleAdapter updateAdpt = null;

	protected static final int INSTALL = 127;
	protected static final int REMOVE = 128;
	protected static final int UPDATE = 129;
	protected static final String LOCAL_APK_PATH = Environment.getExternalStorageDirectory().getPath()+"/.aptoide/";


	private static SimpleAdapter main_catg_adpt = null;
	private static SimpleAdapter app_catg_adpt = null;
	private static SimpleAdapter game_catg_adpt = null;




	private static final String[] main_ctg = {"Games", "Applications", "Others"};
	private static final String[] app_ctg = {"Comics", "Communication", "Entertainment", "Finance", "Health", "Lifestyle", "Multimedia", 
		"News & Weather", "Productivity", "Reference", "Shopping", "Social", "Sports", "Themes", "Tools", 
		"Travel", "Demo", "Software Libraries", "Other"};
	private static final String[] game_ctg = {"Arcade & Action", "Brain & Puzzle", "Cards & Casino", "Casual", "Other"};

	protected int filteredApps =0;

	private Vector<ServerEntry> entries;

	DownloadQueueService downloadQueueService;

	private ServiceConnection serviceConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder serviceBinder) {
			// This is called when the connection with the service has been
			// established, giving us the service object we can use to
			// interact with the service.  Because we have bound to a explicit
			// service that we know is running in our own process, we can
			// cast its IBinder to a concrete class and directly access it.
			downloadQueueService = ((DownloadQueueService.DownloadQueueBinder)serviceBinder).getService();

			Log.d("Aptoide-BaseManagement", "DownloadQueueService bound to a Tab");
		}

		public void onServiceDisconnected(ComponentName className) {
			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.
			// Because it is running in our same process, we should never
			// see this happen.
			downloadQueueService = null;

			Log.d("Aptoide-BaseManagement","DownloadQueueService unbound from a Tab");
		}

	};	



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getApplicationContext().bindService(new Intent(getApplicationContext(), DownloadQueueService.class), serviceConnection, Context.BIND_AUTO_CREATE);
		mPm = getPackageManager();
		db = new DbHandler(getApplicationContext());
		mctx = this;
		sPref = getSharedPreferences("aptoide_prefs", MODE_PRIVATE);
		prefEdit = sPref.edit();
		order_lst = sPref.getString("order_lst", "abc");
		redrawCatgList();

	}

	protected void redrawCatgList(){

		int[] main_ctg_count = db.getCountMainCtg();
		Map<String, Object> count_lst_app = db.getCountSecCatg(1);
		Map<String, Object> count_lst_games = db.getCountSecCatg(0);

		List<Map<String, Object>> main_catg = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> app_catg = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> game_catg = new ArrayList<Map<String, Object>>();
		Map<String, Object> apk_line = null;

		int p = 0;
		for (String node : main_ctg) {
			apk_line = new HashMap<String, Object>();
			apk_line.put("name", node);
			if(main_ctg_count == null)
				apk_line.put("cat_count","0 "+getString(R.string.ctg_available));
			else
				apk_line.put("cat_count",main_ctg_count[p] + " "+getString(R.string.ctg_available));

			main_catg.add(apk_line);
			p++;
		}
		main_catg_adpt = new SimpleAdapter(mctx, main_catg, R.layout.catglist, 
				new String[] {"name", "name", "cat_count"}, new int[] {R.id.cntrl, R.id.name, R.id.cat_count});


		for (String node : app_ctg) {
			Integer count = new Integer(0);
			if(count_lst_app != null){
				count = (Integer)count_lst_app.get(node);
				if(count == null)
					count = 0;
			}
			apk_line = new HashMap<String, Object>();
			apk_line.put("cntrl", "apps");
			apk_line.put("name", node);
			apk_line.put("cat_count",count + " "+getString(R.string.ctg_available));
			app_catg.add(apk_line);
		}
		app_catg_adpt = new SimpleAdapter(mctx, app_catg, R.layout.catglist, 
				new String[] {"cntrl", "name", "cat_count"}, new int[] {R.id.cntrl, R.id.name, R.id.cat_count});


		for (String node : game_ctg) {
			Integer count = new Integer(0);
			if(count_lst_games != null){
				count = (Integer)count_lst_games.get(node);
				if(count == null)
					count = 0;
			}

			apk_line = new HashMap<String, Object>();
			apk_line.put("cntrl", "games");
			apk_line.put("name", node);
			apk_line.put("cat_count",count + " "+getString(R.string.ctg_files));
			game_catg.add(apk_line);
		}

		game_catg_adpt = new SimpleAdapter(mctx, game_catg, R.layout.catglist, 
				new String[] {"cntrl", "name", "cat_count"}, new int[] {R.id.cntrl, R.id.name, R.id.cat_count});
	}


	protected AlertDialog resumeMe(){
		//String istype;

		LayoutInflater li = LayoutInflater.from(this);
		View view = li.inflate(R.layout.orderpopup, null);
		Builder alrt = new AlertDialog.Builder(this).setView(view);
		final AlertDialog p = alrt.create();
		p.setIcon(android.R.drawable.ic_menu_sort_by_size);
		p.setTitle(getString(R.string.order_popup_title));

		p.setButton(getString(R.string.btn_ok), new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				if(pop_change){
					prefEdit.putBoolean("pop_changes", true);
					prefEdit.commit();
					pop_change = false;
				}
				p.dismiss();
			}
		});

		// ***********************************************************
		// Categories
		final RadioButton btn1 = (RadioButton) view.findViewById(R.id.shw_ct);
		final RadioButton btn2 = (RadioButton) view.findViewById(R.id.shw_all);
		if(sPref.getBoolean("mode", false)){
			btn1.setChecked(true);
		}else{
			btn2.setChecked(true);
		}
		final RadioGroup grp2 = (RadioGroup) view.findViewById(R.id.groupshow);
		grp2.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if(checkedId == btn1.getId()){
					pop_change = true;
					prefEdit.putBoolean("mode", true);
				}else{
					pop_change = true;
					prefEdit.putBoolean("mode", false);
				}

			}
		});

		// ***********************************************************

		// ***********************************************************
		// Order
		final RadioButton ord_rct = (RadioButton) view.findViewById(R.id.org_rct);
		final RadioButton ord_abc = (RadioButton) view.findViewById(R.id.org_abc);
		final RadioButton ord_rat = (RadioButton) view.findViewById(R.id.org_rat);
		final RadioButton ord_dwn = (RadioButton) view.findViewById(R.id.org_dwn);

		if(order_lst.equals("abc"))
			ord_abc.setChecked(true);
		else if(order_lst.equals("rct"))
			ord_rct.setChecked(true);
		else if(order_lst.equals("rat"))
			ord_rat.setChecked(true);
		else if(order_lst.equals("dwn"))
			ord_dwn.setChecked(true);

		final RadioGroup grp1 = (RadioGroup) view.findViewById(R.id.groupbtn);

		grp1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if(checkedId == ord_rct.getId()){
					pop_change = true;
					order_lst = "rct";
				}else if(checkedId == ord_abc.getId()){
					pop_change = true;
					order_lst = "abc";
				}else if(checkedId == ord_rat.getId()){
					pop_change = true;
					order_lst = "rat";
				}else if(checkedId == ord_dwn.getId()){
					pop_change = true;
					order_lst = "dwn";
				}
			}
		});

		// ***********************************************************

		return p;
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d("","onResume");
		if(sPref.getBoolean("search_updt", false)){
			prefEdit.remove("search_updt");
			prefEdit.commit();
			redraw();
			redrawCatgList();
		}else if(sPref.getBoolean("update", false)){
			prefEdit.remove("update");
			prefEdit.commit();
			/*if(sPref.contains("order_lst")){
				Log.d("Aptoide","************************** This 1 ********************************");
				order_lst = sPref.getString("order_lst", "abc");
				prefEdit.remove("order_lst");
				prefEdit.commit();
			}*/
			redraw();
			redrawCatgList();

		}/*else if(sPref.contains("order_lst")){
			Log.d("Aptoide","*********************** This 2 ******************************");
			order_lst = sPref.getString("order_lst", "abc");
			prefEdit.remove("order_lst");
			prefEdit.commit();
			redraw();
		}*/
	}

	protected void removeApk(String apk_pkg){
		try {
			pkginfo = mPm.getPackageInfo(apk_pkg, 0);
		} catch (NameNotFoundException e) {	}
		Uri uri = Uri.fromParts("package", pkginfo.packageName, null);
		Intent intent = new Intent(Intent.ACTION_DELETE, uri);
		prefEdit.putString("pkg", pkginfo.packageName);
		prefEdit.commit();
		startActivityForResult(intent,REMOVE); 
	}

	protected void installApk(String apk_path, String ver){

		try {
			pkginfo = mPm.getPackageArchiveInfo(apk_path, 0);
			if(pkginfo == null){
				// Ficheiro está corrupto, temos de verificar!
			}else{
				Intent intent = new Intent();
				intent.setAction(android.content.Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.parse("file://" + apk_path), "application/vnd.android.package-archive");
				prefEdit.putString("ver", ver);
				prefEdit.putString("pkg", pkginfo.packageName);
				prefEdit.commit();
				startActivityForResult(intent,INSTALL);
				Log.d("Aptoide-BaseManagement", "Installing Apk: "+apk_path);
			}
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.failed_install), Toast.LENGTH_LONG);
		}
	}

	protected void updateApk(String apk_path, String apk_id, String ver){	

		try {		
			pkginfo = mPm.getPackageArchiveInfo(apk_path, 0);
			Intent intent = new Intent();
			intent.setAction(android.content.Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.parse("file://" + apk_path), "application/vnd.android.package-archive");
			//    	prefEdit.putString("pkg", pkginfo.packageName);
			prefEdit.putString("pkg", apk_id);
			prefEdit.putString("ver", ver);
			prefEdit.commit();
			startActivityForResult(intent,UPDATE);
			Log.d("Aptoide-BaseManagement", "Updating Apk: "+apk_path);
		} catch (Exception e) {
			Toast.makeText(mctx, mctx.getString(R.string.failed_update), Toast.LENGTH_LONG);
		}
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d("Installer Result",requestCode + " " +resultCode);
		if(requestCode == INSTALL){
			String apkid = sPref.getString("pkg", null);
			try {
				pkginfo = mPm.getPackageInfo(apkid, 0);

				db.insertInstalled(apkid, sPref.getString("ver", null));
				prefEdit.remove("pkg"); prefEdit.remove("ver");
				prefEdit.commit();
				redrawCatgList();
				redraw();
			} catch (NameNotFoundException e) {	}
		}else if(requestCode == REMOVE){
			String apkid = sPref.getString("pkg", null);
			try {
				pkginfo = mPm.getPackageInfo(apkid, 0);
			} catch (NameNotFoundException e) {	
				db.removeInstalled(apkid);
				prefEdit.remove("pkg"); 
				prefEdit.remove("ver"); 
				prefEdit.commit();
				redrawCatgList();
				redraw();
			}
		}else if(requestCode == UPDATE){
			String apkid = sPref.getString("pkg", null);
			try {
				pkginfo = mPm.getPackageInfo(apkid, 0);
			} catch (NameNotFoundException e) {	}
			int vercode = pkginfo.versionCode;
			String version = sPref.getString("ver", null);
			if(db.wasUpdateOrDowngrade(apkid, vercode)){
				db.removeInstalled(apkid);
				db.insertInstalled(apkid, version);
				db.deleteScheduledDownload(apkid,version);
				downloadQueueService.dismissNotification(apkid.hashCode());
				prefEdit.remove("pkg"); prefEdit.remove("ver");
				prefEdit.commit();
				redrawCatgList();
				redraw();
			}
		}
	}


	String age_filter;
	protected void redraw(){
		age_filter=sPref.getString("app_rating", "All");
		if(pd!=null&&pd.isShowing()){
			pd.dismiss();
		}
		Log.d("Aptoide","======================= I REDRAW");
		prefEdit.putBoolean("redrawis", true);
		prefEdit.commit();

		pd = ProgressDialog.show(mctx, getText(R.string.top_please_wait), getText(R.string.updating_msg), true);

		pd.setIcon(android.R.drawable.ic_dialog_info);


		new Thread() {



			public void run(){

				try{

					List<Map<String, Object>> availMap = new ArrayList<Map<String, Object>>();
					List<Map<String, Object>> instMap = new ArrayList<Map<String, Object>>();
					List<Map<String, Object>> updtMap = new ArrayList<Map<String, Object>>();
					Map<String, Object> apk_line;

					if(apk_lst != null)
						apk_lst.clear();
					apk_lst = db.getAll(order_lst);

					/*
					 * status
					 * 0 - not installed
					 * 1 - installed
					 * 2 - installed need update
					 * 3 - installed don't need update but downgrade possible
					 * 
					 */



					for(ApkNode node: apk_lst){
						if(ageFilter(node.age,age_filter)){
							filteredApps++;
							continue;
						}
						apk_line = new HashMap<String, Object>();
						apk_line.put("pkg", node.apkid);
						String iconpath = new String(getString(R.string.icons_path)+node.apkid);
						apk_line.put("icon", iconpath);
						apk_line.put("rat", node.rat);

						if(node.down >= 0)
							apk_line.put("down", formatDownloads(node.down) + " Down.");

						if(node.status == 1){
							db.deleteScheduledDownload(node.apkid,node.ver);

							apk_line.put("status",node.ver);
							apk_line.put("name", node.name);
							apk_line.put("status2", "invisible");


							instMap.add(apk_line);

						}else if(node.status == 2){
							db.deleteScheduledDownload(node.apkid,node.ver);

							apk_line.put("status",node.verUpdate);
							apk_line.put("name", node.name);
							updtMap.add(apk_line);

							apk_line = new HashMap<String, Object>();
							apk_line.put("pkg", node.apkid);
							apk_line.put("down", formatDownloads(node.down) + " Down.");
							iconpath = new String(getString(R.string.icons_path)+node.apkid);
							apk_line.put("icon", iconpath);
							apk_line.put("rat", node.rat);


							apk_line.put("status", node.ver);
							apk_line.put("status2", "upgrade");

							apk_line.put("name", node.name);
							instMap.add(apk_line);




						}else if(node.status == 3){
							apk_line.put("status", node.ver);

							apk_line.put("status2", "downgrade");
							apk_line.put("name", node.name);
							instMap.add(apk_line);


						}else if(!sPref.getBoolean("mode", false)){
							apk_line.put("status", node.ver);
							apk_line.put("name", node.name);
							availMap.add(apk_line);
							

						}
						

					}

					Comparator<Map<String, Object>> alphabeticComp = new Comparator<Map<String,Object>>(){
						//Order alphabetic ins
						public int compare(Map<String, Object> map1, Map<String, Object> map2) {	


							String name1 = ((String)map1.get("name"))!=null?((String)map1.get("name")):((String)map1.get("name"));
							String name2 = ((String)map2.get("name"))!=null?((String)map2.get("name")):((String)map2.get("name"));




							//							if(name1!=null && name1.length()>0){
							//								
							//								if(name2!=null && name2.length()>0){
							//									return ((int)(name1.charAt(0)))-((int)(name2.charAt(0)));
							//								}
							//								
							//								return 1;
							//								
							//							}
							//							
							return name1.toLowerCase().compareToIgnoreCase(name2.toLowerCase());

						}
					};

					//if sort alphabetically, no results to other sorts
					Collections.sort(instMap, alphabeticComp);
					//					Collections.sort(availMap, alphabeticComp); 
					Collections.sort(updtMap, alphabeticComp);

					for(Map<String, Object> map:instMap){ map.remove("statusSort"); }

					availAdpt = new SimpleAdapter(mctx, availMap, R.layout.app_row, 
							new String[] {"pkg", "name", "status", "icon", "rat", "down"}, 
							new int[] {R.id.app_hashid, R.id.app_name,R.id.installed_versionname,R.id.app_icon, R.id.stars, R.id.downloads});

					availAdpt.setViewBinder(new LstBinder());

					instAdpt = new SimpleAdapter(mctx, instMap, R.layout.app_row, 
							new String[] {"pkg", "name", "status", "status2", "icon", "rat", "down"}, 
							new int[] {R.id.app_hashid, R.id.app_name,R.id.installed_versionname, R.id.app_downgrade,R.id.app_icon, R.id.stars, R.id.downloads});

					instAdpt.setViewBinder(new LstBinder());

					updateAdpt = new SimpleAdapter(mctx, updtMap, R.layout.app_row, 
							new String[] {"pkg", "name", "status", "icon", "rat", "down"}, 
							new int[] {R.id.app_hashid, R.id.app_name,R.id.installed_versionname, R.id.app_icon, R.id.stars, R.id.downloads});

					updateAdpt.setViewBinder(new LstBinder());

					//Log.d("Aptoide", e.getMessage()+"");
				}finally{
					Log.d("Aptoide","======================= I REDRAW SAY KILL");
					stop_pd.sendEmptyMessage(0);

				}
			}


		}.start();

	}



	protected boolean ageFilter(String age, String age_filter) {
		boolean result;
		try{
			result = EnumAges.get(age.trim()).ordinal()>EnumAges.get(age_filter).ordinal();
		}catch (Exception e) {
			e.printStackTrace();
			return true;
		}
		return result;
		
	}



	private String formatDownloads(int down) {

		String formatedNumber = Integer.toString(down);

		if (down > 1000000) {
			return formatedNumber.substring(0, formatedNumber.length() - 6)
					+ "M";
		} else if (down > 1000) {
			return formatedNumber.substring(0, formatedNumber.length() - 3)
					+ "K";
		}

		return Integer.toString(down);
	}

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
				Log.d(textRepresentation+"","");
				ImageView tmpr = (ImageView)view;	
				File icn = new File(textRepresentation);
				if(icn.exists() && icn.length() > 0){
					new Uri.Builder().build();
					tmpr.setImageURI(Uri.parse(textRepresentation));
				}else if(textRepresentation.equals("upgrade")){
					tmpr.setVisibility(View.VISIBLE);
					tmpr.setImageResource(R.drawable.upgrade);
				}else if(textRepresentation.equals("downgrade")){
					tmpr.setVisibility(View.VISIBLE);
					tmpr.setImageResource(R.drawable.downgrade);
				}else if(textRepresentation.equals("invisible")){
					tmpr.setVisibility(View.GONE);
				}else{
					tmpr.setImageResource(android.R.drawable.sym_def_app_icon);
				}
			}else if(view.getClass().toString().equalsIgnoreCase("class android.widget.LinearLayout")){
				LinearLayout tmpr = (LinearLayout)view;
				tmpr.setTag(textRepresentation);
			}else{

				return false;
			}
			return true;
		}
	}

	class SimpeLstBinder implements ViewBinder
	{
		public boolean setViewValue(View view, Object data, String textRepresentation)
		{
			if(view.getClass().toString().equalsIgnoreCase("class android.widget.TextView")){
				TextView tmpr = (TextView)view;
				tmpr.setText(textRepresentation);
			}else if(view.getClass().toString().equalsIgnoreCase("class android.widget.LinearLayout")){
				LinearLayout tmpr = (LinearLayout)view;
				tmpr.setTag(textRepresentation);
			}else{
				return false;
			}
			return true;
		}

	}

	protected void queueDownload(String packageName, String ver, boolean isUpdate){


		Vector<DownloadNode> tmp_serv = new Vector<DownloadNode>();	

		try{

			tmp_serv = db.getPathHash(packageName, ver);

			String localPath = new String(LOCAL_APK_PATH+packageName+".apk");
			String appName = packageName;
			for(ApkNode node: apk_lst){
				if(node.apkid.equals(packageName)){
					appName = node.name;
					break;
				}
			}

			//if(tmp_serv.size() > 0){
			DownloadNode downloadNode = tmp_serv.firstElement();
			downloadNode.setPackageName(packageName);
			downloadNode.setAppName(appName);
			downloadNode.setLocalPath(localPath);
			downloadNode.setUpdate(isUpdate);
			String remotePath = downloadNode.getRemotePath();
			//}

			if(remotePath.length() == 0)
				throw new TimeoutException();

			String[] logins = null; 
			logins = db.getLogin(downloadNode.getRepo());
			//			downloadNode.getRemotePath()
			downloadNode.setLogins(logins);
			Log.d("Aptoide-BaseManagement","queueing download: "+packageName +" "+downloadNode.getSize());	

			downloadQueueService.startDownload(downloadNode);

		} catch(Exception e){	}
	}

	protected SimpleAdapter getRootCtg(){
		main_catg_adpt.setViewBinder(new SimpeLstBinder());
		return main_catg_adpt;
	}

	protected SimpleAdapter getAppCtg(){
		app_catg_adpt.setViewBinder(new SimpeLstBinder());
		return app_catg_adpt;
	}

	protected SimpleAdapter getGamesCtg(){
		game_catg_adpt.setViewBinder(new SimpeLstBinder());
		return game_catg_adpt;
	}

	protected SimpleAdapter getAvailable(String show_now, int main_show_now){
		if(sPref.getBoolean("mode", false)){
			if(!(show_now == null) || main_show_now == 2){
				return getGivenCatg(show_now, main_show_now);
			}
			main_catg_adpt.setViewBinder(new SimpeLstBinder());
			return main_catg_adpt;
		}
		return availAdpt;
	}

	protected SimpleAdapter getGivenCatg(String ctg, int ord){
		age_filter=sPref.getString("app_rating", "All");
		filteredApps =0;
		List<Map<String, Object>> availMap = new ArrayList<Map<String, Object>>();
		Map<String, Object> apk_line;
		Vector<ApkNode> tmp_lst = null;
		SimpleAdapter rtnadp = null;

		/* if(tmp_lst != null)
			 tmp_lst.clear();*/
		tmp_lst = db.getAll(order_lst, ctg, ord);


		for(ApkNode node: tmp_lst){
			if(ageFilter(node.age, age_filter)){
				filteredApps++;
				continue;
			}
			apk_line = new HashMap<String, Object>();
			apk_line.put("pkg", node.apkid);
			String iconpath = new String(getString(R.string.icons_path)+node.apkid);
			apk_line.put("icon", iconpath);
			apk_line.put("rat", node.rat);
			apk_line.put("status", "" + node.ver);
			apk_line.put("name", node.name);
			if(node.down >= 0)
				apk_line.put("down", formatDownloads(node.down) + " Down.");
			//			 if(filterPass(node))
			//			 {
			availMap.add(apk_line);
			//				}else{
			//					filteredApps++;
			//				}
		}

		
		rtnadp = new SimpleAdapter(mctx, availMap, R.layout.app_row, 
				new String[] {"pkg", "name", "status", "icon", "rat", "down"}, 
				new int[] {R.id.app_hashid, R.id.app_name,R.id.installed_versionname,R.id.app_icon, R.id.stars, R.id.downloads});

		rtnadp.setViewBinder(new LstBinder());
		//		rtnadp.notifyDataSetChanged();
		return rtnadp;
	}



	protected Handler stop_pd = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Intent i = new Intent("pt.caixamagica.aptoide.REDRAW");
			while(pd.isShowing()){
				Log.d("Aptoide","======================= I KILL");
				pd.dismiss();








			}
			prefEdit.putBoolean("changeavail", true);
			prefEdit.putBoolean("changeinst", true);
			prefEdit.putBoolean("changeupdt", true);
			prefEdit.putBoolean("redrawis", false);
			prefEdit.commit();
			sendBroadcast(i);
			reposCheck();
		}



	};



	private class ServerEntry {
		public String repo;
		public boolean updates;
		public int appscount;

		public ServerEntry(String repo, boolean updates, int appscount) {
			this.repo = repo;
			this.updates = updates;
			this.appscount = appscount;
		}

	}


	private void reposCheck() {
		if(sPref.getBoolean("checkRepos", false)){
			new Thread(){
				@Override
				public void run() {
					try{
						Vector<ServerNode> allServers;
						Vector<String> hashids = new Vector<String>();
						Vector<String> servers= new Vector<String>();
						entries = new Vector<ServerEntry>();
						
						allServers = db.getServers();
						if(allServers!=null&&!allServers.isEmpty()){
							for(ServerNode server : allServers){
								if (server.inuse&&!server.hash.equals("0")&&server.extended.equals("1")) {
									servers.add(server.uri);
									hashids.add(db.getServerDelta(server.uri));
								}
							}
							String url_servers="";
							String url_hashids="";
							if(!hashids.isEmpty()){
								for (int i=0; i!=servers.size();i++){
									if(i==0){
										url_servers=servers.get(i).split("http://")[1].split(".bazaarandroid.com/")[0];
										url_hashids=hashids.get(i);
									}else{
										url_servers+=","+servers.get(i).split("http://")[1].split(".bazaarandroid.com/")[0];
										url_hashids+=","+hashids.get(i);
									}
								}
								String url = "https://www.bazaarandroid.com/webservices/listRepositoryChange/"+url_servers+"/"+url_hashids+"/"+"xml";
								Log.d("",url);
								BufferedInputStream bstream = new BufferedInputStream(NetworkApis.getInputStream(mctx, url));
								SAXParserFactory spf = SAXParserFactory.newInstance(); //Throws SAXException, ParserConfigurationException, SAXException, FactoryConfigurationError
								SAXParser sp = spf.newSAXParser();
								sp.parse(new InputSource(bstream), handler);

								int totalapps =0;
								boolean updates =false; 
								for(ServerEntry entry : entries){
									if(entry.updates){
										totalapps+=entry.appscount;
										updates = true;
									}
								}
								if(updates){
									Intent i = new Intent("pt.caixamagica.aptoide.HAS_UPDATES");
									i.putExtra("appscount", totalapps);
									sendBroadcast(i);
								}
							}
						}
					} catch (ParserConfigurationException e) {
						e.printStackTrace();
					} catch (SAXException e) {
						e.printStackTrace();
					}
					catch (IOException e) {
						e.printStackTrace();
					} 

				}

			}.start();
			prefEdit.putBoolean("checkRepos", false);
			prefEdit.commit();
		}
	}



	private DefaultHandler handler= new DefaultHandler(){

		private boolean t_entry = false;
		private boolean t_repo = false;
		private boolean t_hasupdates = false;

		private String repo;
		private boolean hasUpdates;
		private int appscount;
		private boolean t_added;

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			// TODO Auto-generated method stub
			super.startElement(uri, localName, qName, attributes);

			if(qName.equalsIgnoreCase("entry")){
				t_entry=true;

			}else if(qName.equalsIgnoreCase("repo")){
				t_repo=true;
			}else if(qName.equalsIgnoreCase("hasupdates")){
				t_hasupdates=true;
			}
			else if(qName.equalsIgnoreCase("added")){
				t_added=true;
			}




		}

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			// TODO Auto-generated method stub
			super.characters(ch, start, length);


			//				if(t_entry){

			//				}else 
			if(t_repo){
				repo=new String(ch,start,length);



			}else if (t_hasupdates){
				hasUpdates=Boolean.parseBoolean((new String(ch,start,length)));
				if(!hasUpdates){
					appscount=0;
				}
			}else if (t_added){
				appscount=Integer.parseInt((new String(ch,start,length)));
			}


		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			// TODO Auto-generated method stub
			super.endElement(uri, localName, qName);
			if(qName.equalsIgnoreCase("entry")){
				t_entry=false;
				ServerEntry entry = new ServerEntry(repo,hasUpdates,appscount);
				entries.add(entry);
			}else if(qName.equalsIgnoreCase("repo")){
				t_repo=false;
			}else if(qName.equalsIgnoreCase("hasupdates")){
				t_hasupdates=false;
			}
			else if(qName.equalsIgnoreCase("added")){
				t_added=false;
			}

		}



	};

	@Override
	protected void onPause() {
		super.onPause();
		prefEdit.putString("order_lst", order_lst);
		prefEdit.commit();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}	


}
