package cm.aptoide.pt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.TimeoutException;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcelable;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.StatFs;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.CursorAdapter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import cm.aptoide.pt.utils.Algorithms;
import cm.aptoide.pt.utils.EnumOptionsMenu;

import com.viewpagerindicator.TitlePageIndicator;
import com.viewpagerindicator.TitlePageIndicator.IndicatorStyle;

public class Aptoide extends FragmentActivity {
	private String XML_PATH;
	Context context;
	ArrayList<View> pages;
	DBHandler db;
	ViewPager vp;
	ProgressDialog pd;
	CursorAdapter availAdapter;
	CursorAdapter installedAdapter;
	CursorAdapter updatesAdapter;
	ListView installed_listView;
	ListView updates_listView;
	ListView available_listView;
	View featured;
	ArrayList<ServerNode> servers;
	private int parsingProgress = 0;
	ImageView search;
	private DownloadQueueService downloadQueueService;
	public ConnectivityManager netstate = null; 
	private ServiceConnection conn = new ServiceConnection() {

		

		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub

		}

		public void onServiceConnected(ComponentName name, IBinder service) {
			downloadQueueService=((DownloadQueueService.DownloadQueueBinder) service).getService();
		}
	};
	
	protected static final String LOCAL_APK_PATH = Environment.getExternalStorageDirectory().getPath()+"/.aptoide/";

	private int NEWREPO_FLAG = 0;
	
	private final String SDCARD = Environment.getExternalStorageDirectory().getPath();
	private String LOCAL_PATH = SDCARD+"/.aptoide";
	SharedPreferences sPref;
	Editor editor;
	boolean pop_change = false;
	private String order_lst;
	private boolean receiverIsRegister = false;
	private boolean xmlfile = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aptoide);
		context = this;

		File sdcard_file = new File(SDCARD);
		if(!sdcard_file.exists() || !sdcard_file.canWrite()){

			final AlertDialog upd_alrt = new AlertDialog.Builder(context).create();
			upd_alrt.setIcon(android.R.drawable.ic_dialog_alert);
			upd_alrt.setTitle(getText(R.string.remote_in_noSD_title));
			upd_alrt.setMessage(getText(R.string.remote_in_noSD));
			upd_alrt.setButton(Dialog.BUTTON_NEUTRAL,getText(R.string.btn_ok), new Dialog.OnClickListener() {
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

				final AlertDialog upd_alrt = new AlertDialog.Builder(context).create();
				upd_alrt.setIcon(android.R.drawable.ic_dialog_alert);
				upd_alrt.setTitle(getText(R.string.remote_in_noSD_title));
				upd_alrt.setMessage(getText(R.string.remote_in_noSDspace));
				upd_alrt.setButton(Dialog.BUTTON_NEUTRAL,getText(R.string.btn_ok), new Dialog.OnClickListener() {
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
				bindService(new Intent(this,DownloadQueueService.class), conn , Service.BIND_AUTO_CREATE);
				netstate = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
				sPref = getSharedPreferences("aptoide_prefs", MODE_PRIVATE);
				editor = sPref.edit();
				if(!sPref.contains("orderByCategory")){

					editor.putBoolean("orderByCategory", true);
				}
				order_lst=sPref.getString("order_list", DBStructure.COLUMN_APK_NAME+" collate nocase");
				if(!sPref.contains("order_list")){
					editor.putString("order_list", order_lst);
				}

				//		if(sPref.getInt("version", 0) < pkginfo.versionCode){
				//	   		db.updateTables();
				//	   		editor.putBoolean("mode", true);
				//	   		editor.putInt("version", pkginfo.versionCode);
				//		}

				if(sPref.getString("myId", null) == null){
					String rand_id = UUID.randomUUID().toString();
					editor.putString("myId", rand_id);
				}

				if(sPref.getInt("scW", 0) == 0 || sPref.getInt("scH", 0) == 0){
					DisplayMetrics dm = new DisplayMetrics();
					getWindowManager().getDefaultDisplay().getMetrics(dm);
					editor.putInt("scW", dm.widthPixels);
					editor.putInt("scH", dm.heightPixels);
				}

				if(sPref.getString("icdown", null) == null){
					editor.putString("icdown", "g3w");
				}
				if(!sPref.contains("app_rating")){

					editor.putString("app_rating", "All");
				}else if(sPref.getString("app_rating", "All").equals("Teen")){
					editor.putString("app_rating", "All");
				}

				if(!sPref.contains("schDwnBox")){
					editor.putBoolean("schDwnBox", false);
				}
				if(!sPref.contains("hwspecsChkBox")){
					editor.putBoolean("hwspecsChkBox", true);
				}

				editor.commit();

				XML_PATH = getCacheDir()+"/temp_info.xml";
				db = new DBHandler(context);
				db.open();

				TitlePageIndicator pi = (TitlePageIndicator) findViewById(R.id.indicator);

				vp = (ViewPager) findViewById(R.id.viewpager);
				search = (ImageView) findViewById(R.id.btsearch);
				pages = new ArrayList<View>();

				available_listView = new ListView(context);
				installed_listView = new ListView(context);
				updates_listView = new ListView(context);

				//		availAdapter = new AvailableCursorAdapter(context, null, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);


				installedAdapter = new AvailableCursorAdapter(context, null, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
				updatesAdapter = new AvailableCursorAdapter(context, null, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);


				featured = LayoutInflater.from(context).inflate(R.layout.featured, null);
				pages.add(featured);
				pages.add(available_listView);
				pages.add(installed_listView);
				pages.add(updates_listView);




				vp.setAdapter(new ViewPagerAdapter(context, pages));
				pi.setViewPager(vp);

				pi.setTextColor(Color.WHITE);
				//		pi.setSelectedBold(false);
				pi.setFooterIndicatorStyle(IndicatorStyle.Triangle);
				pi.setSelectedColor(Color.argb(200, 119, 170, 10));
				//		db.beginTransation();
				redrawAll();
				loadFeatured();
				//		db.endTransation();
				available_listView.setAdapter(availAdapter);
				installed_listView.setAdapter(installedAdapter);
				updates_listView.setAdapter(updatesAdapter);

				available_listView.setOnItemClickListener(availItemClick);
				updates_listView.setOnItemClickListener(updatesItemClick);
				installed_listView.setOnItemClickListener(installedItemClick);
				search.setOnClickListener(searchClick);

				IntentFilter filter = new IntentFilter();
				filter.addAction("pt.caixamagica.aptoide.REDRAW");
				registerReceiver(receiver, filter);
				receiverIsRegister=true;

				if(getIntent().hasExtra("newrepo")){
					Intent i = new Intent(this,StoreManager.class);
					i.putExtra("newrepo", getIntent().getSerializableExtra("newrepo"));
					startActivityForResult(i, 0);
				}





				if(sPref.getBoolean("firstrun",true)){

					Intent shortcutIntent = new Intent(Intent.ACTION_MAIN);
					shortcutIntent.setClassName("cm.aptoide.pt", "cm.aptoide.pt.Start");
					final Intent intent = new Intent();
					intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
					
					intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name));
					Parcelable iconResource = Intent.ShortcutIconResource.fromContext(this, R.drawable.icon);

					intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconResource);
					intent.putExtra("duplicate", false);
					intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
					sendBroadcast(intent);

					if(new File(LOCAL_PATH+"/servers.xml").exists()){
						try{

							SAXParserFactory spf = SAXParserFactory.newInstance();
							SAXParser sp = spf.newSAXParser();

							MyappHandler handler = new MyappHandler();

							sp.parse(new File(Environment.getExternalStorageDirectory().getPath()+".aptoide/servers.xml"),handler);
							ArrayList<String> server = handler.getServers();
							if(!server.isEmpty()){
								Intent i = new Intent(this,StoreManager.class);
								i.putExtra("newrepo", server);
								//TODO Alertdialog
								startActivityForResult(i, 0);
								xmlfile=true;
							}

						}catch (Exception e) {
							e.printStackTrace();
						}

					}


				}
				
				if(db.getRepositories().getCount()==0
						&&!xmlfile){
					Intent i = new Intent(this,StoreManager.class);
					i.putExtra("norepos", true);
					startActivityForResult(i, 0);
				}

				editor.putBoolean("firstrun", false);
				editor.commit();
				
			}

			

		}







	}
	
	
	
	private OnClickListener featuredListener = new OnClickListener() {
		
		public void onClick(View v) {
			Intent i = new Intent(Aptoide.this,ApkInfo.class);
			i.putExtra("id", Long.parseLong((String) v.getTag()));
			i.putExtra("type", "featured");
			startActivity(i);
		}
	};

	private void loadFeatured() {
		new Thread(new Runnable() {
			int a=0;
			ImageLoader2 imageLoader = new ImageLoader2(context);
			int[] res_ids = {R.id.central,R.id.topleft,R.id.topright,R.id.bottomleft,R.id.bottomright};
			ArrayList<HashMap<String, String>> image_urls = new ArrayList<HashMap<String, String>>();
			public void run() {
				try {
					SAXParserFactory spf = SAXParserFactory.newInstance();
					SAXParser sp = spf.newSAXParser();

					sp.parse(NetworkApis.getInputStream(context,
							"http://www.bazaarandroid.com/apks/editors.xml"),
							new EditorsChoiceRepoParser(context));
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					HashMap<String,String> image_url_highlight = db.getHighLightFeature();
					if(image_url_highlight!=null){
						a=1;
						ImageView v = (ImageView) featured.findViewById(res_ids[0]);
						imageLoader.DisplayImage(-1, image_url_highlight.get("url"), v, context);
						v.setTag(image_url_highlight.get("id"));
						v.setOnClickListener(featuredListener);
					}
					image_urls = db.getFeaturedGraphics();
					Collections.shuffle(image_urls);
					runOnUiThread(new Runnable() {
						
						public void run() {
							try{
								for(int i = a; i != res_ids.length;i++){
									ImageView v = (ImageView) featured.findViewById(res_ids[i]);
									imageLoader.DisplayImage(-1, image_urls.get(i).get("url"), v, context);
									v.setTag(image_urls.get(i).get("id"));
									v.setOnClickListener(featuredListener);
								}
								
								
							}catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
				}
			}

			
		}).start();
		
		new Thread(new Runnable() {
			
			public void run() {
				try {
					SAXParserFactory spf = SAXParserFactory.newInstance();
					SAXParser sp = spf.newSAXParser();
					sp.parse(NetworkApis.getInputStream(context,
							"http://apps.bazaarandroid.com/top.xml"),
							new TopAppsRepoParser(context));
				} catch (Exception e) {
					e.printStackTrace();
					
				}finally{
					loadUItopapps();
				}
			}

			
		}).start();
		
	}
	ArrayList<HashMap<String, String>> values;
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if(intent.hasExtra("newrepo")){
			Intent i = new Intent(this,StoreManager.class);
			i.putExtra("newrepo", intent.getSerializableExtra("newrepo"));
			startActivityForResult(i, 0);
		}
	}
	
	private void loadUItopapps() {
		((ToggleButton) featured.findViewById(R.id.toggleButton1)).setOnCheckedChangeListener(null);
		values = db.getTopApps();
		runOnUiThread(new Runnable() {
			ImageLoader imageLoader = new ImageLoader(context);
			
			public void run() {
				LinearLayout ll = (LinearLayout) featured.findViewById(R.id.container); 
				ll.removeAllViews();
		        LinearLayout llAlso = new LinearLayout(Aptoide.this);
		        llAlso.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
		                LayoutParams.WRAP_CONTENT));
		        llAlso.setOrientation(LinearLayout.HORIZONTAL);
		        for (int i = 0; i!=values.size(); i++) {
		            RelativeLayout txtSamItem = (RelativeLayout) getLayoutInflater().inflate(R.layout.griditem, null);
		           	((TextView) txtSamItem.findViewById(R.id.name)).setText(values.get(i).get("name"));
		           	imageLoader.DisplayImage(-1, db.getTopAppsIconPath()+values.get(i).get("icon"), (ImageView)txtSamItem.findViewById(R.id.icon), context);
		           	float stars = 0f;
		           	try{
		           		stars = Float.parseFloat(values.get(i).get("rating"));
		           	}catch (Exception e) {
		           		stars = 0f;
					}
		           	((RatingBar) txtSamItem.findViewById(R.id.rating)).setRating(stars);
		            txtSamItem.setPadding(10, 0, 0, 0);
		            txtSamItem.setTag(values.get(i).get("id"));
		            txtSamItem.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 100, 1));
		            txtSamItem.setOnClickListener(featuredListener);

		            txtSamItem.measure(0, 0);
		            
		            if (i%2==0) {
		                ll.addView(llAlso);

		                llAlso = new LinearLayout(Aptoide.this);
		                llAlso.setLayoutParams(new LayoutParams(
		                        LayoutParams.FILL_PARENT,
		                        100));
		                llAlso.setOrientation(LinearLayout.HORIZONTAL);
		                llAlso.addView(txtSamItem);
		            } else {
		                llAlso.addView(txtSamItem);
		            }
		        }

		        ll.addView(llAlso);
		        System.out.println(sPref.getString("app_rating", "All").equals(
						"Mature"));
		        ((ToggleButton) featured.findViewById(R.id.toggleButton1))
				.setChecked(sPref.getString("app_rating", "All").equals(
						"Mature"));
		        ((ToggleButton) featured.findViewById(R.id.toggleButton1))
				.setOnCheckedChangeListener(adultCheckedListener);
			}
		});
	}
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		if (vp.getCurrentItem() <= 1) {
			menu.add(0, EnumOptionsMenu.UPDATE_REPO.ordinal(), 0, getString(R.string.menu_update_repo)).setIcon(android.R.drawable.ic_menu_rotate);
		}
		
		if (vp.getCurrentItem() == 3) {
			menu.add(0, EnumOptionsMenu.UPDATE_ALL.ordinal(), 0, getString(R.string.menu_update_all)).setIcon(android.R.drawable.ic_menu_rotate);
		}
		menu.add(0, EnumOptionsMenu.MANAGE_REPO.ordinal(), 0, getString(R.string.menu_manage)).setIcon(android.R.drawable.ic_menu_agenda);
		menu.add(0, EnumOptionsMenu.DISPLAY_OPTIONS.ordinal(), 0, getString(R.string.menu_display_options)).setIcon(android.R.drawable.ic_menu_sort_by_size);
		menu.add(0, EnumOptionsMenu.SETTINGS.ordinal(), 0, getString(R.string.menu_settings)).setIcon(android.R.drawable.ic_menu_preferences);
		menu.add(0, EnumOptionsMenu.SCHEDULED_DOWNLOADS.ordinal(), 0, getString(R.string.schDwnBtn)).setIcon(android.R.drawable.ic_menu_recent_history);
		menu.add(0, EnumOptionsMenu.ABOUT.ordinal(), 0, getString(R.string.menu_about)).setIcon(android.R.drawable.ic_menu_help);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		EnumOptionsMenu menuEntry = EnumOptionsMenu.reverseOrdinal(item.getItemId());
		switch (menuEntry) {
		case UPDATE_FEATURED:
			loadFeatured();
			break;
		case MANAGE_REPO:
			startActivityForResult(new Intent(this,StoreManager.class), 0);
			break;
		case UPDATE_REPO:
			forceUpdateRepos();
			break;
		case DISPLAY_OPTIONS:
			showDisplayOptionsDialog();
			break;
		case SETTINGS:
			Intent i = new Intent(this,Preferences.class);
			startActivityForResult(i, 15);
			break;
		case ABOUT:
			LayoutInflater li = LayoutInflater.from(this);
			View view = li.inflate(R.layout.about, null);
			TextView info = (TextView)view.findViewById(R.id.about11);
			info.setText(getString(R.string.about_txt11, getString(R.string.ver_str)));
			Builder pd = new AlertDialog.Builder(this).setView(view);
			final AlertDialog alrt = pd.create();
			alrt.setIcon(R.drawable.icon);
			alrt.setTitle(R.string.app_name);
			alrt.setButton(getText(R.string.btn_chlog), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int	whichButton) {
					Uri uri = Uri.parse(getString(R.string.change_log_url));
					startActivity(new Intent( Intent.ACTION_VIEW, uri));
				}
			});
			alrt.show();
			break;
		case UPDATE_ALL:
			new Thread(new Runnable() {

				public void run() {
					for (int i = 0; i != updatesAdapter.getCount(); i++) {
						System.out.println(updatesAdapter.getCount());
						queueDownload(((Cursor) updatesAdapter.getItem(i))
								.getString(7), ((Cursor) updatesAdapter
								.getItem(i)).getString(3), true);
					}
				}
			}).start();
			break;
		case SCHEDULED_DOWNLOADS:
			startActivityForResult(new Intent(this,ScheduledDownload.class), 2);
			break;
		default:
			break;
		}
			
			
			
		return super.onOptionsItemSelected(item);
	}
	ToggleButton adult ;
	private void showDisplayOptionsDialog() {
		View view = LayoutInflater.from(context).inflate(R.layout.orderpopup, null);
		Builder builder = new AlertDialog.Builder(context).setView(view);
		AlertDialog dialog = builder.create();
		dialog.setButton(Dialog.BUTTON_NEUTRAL, "Ok", new Dialog.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				if(pop_change){
					editor.commit();
					redrawAll();
				}
			}
		});
		
		final RadioButton btn1 = (RadioButton) view.findViewById(R.id.shw_ct);
		final RadioButton btn2 = (RadioButton) view.findViewById(R.id.shw_all);
		adult  = (ToggleButton) view.findViewById(R.id.adultcontent_toggle);
		if(sPref.getBoolean("orderByCategory", false)){
			btn1.setChecked(true);
		}else{
			btn2.setChecked(true);
		}
		adult.setChecked(sPref.getString("app_rating", "All").equals("Mature"));
		adult.setOnCheckedChangeListener(adultCheckedListener);
		final RadioGroup grp2 = (RadioGroup) view.findViewById(R.id.groupshow);
		grp2.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			

			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if(checkedId == btn1.getId()){
					pop_change = true;
					editor.putBoolean("orderByCategory", true);
				}else{
					pop_change = true;
					editor.putBoolean("orderByCategory", false);
				}

			}
		});
		
				final RadioButton ord_rct = (RadioButton) view.findViewById(R.id.org_rct);
				final RadioButton ord_abc = (RadioButton) view.findViewById(R.id.org_abc);
				final RadioButton ord_rat = (RadioButton) view.findViewById(R.id.org_rat);
				final RadioButton ord_dwn = (RadioButton) view.findViewById(R.id.org_dwn);

				if(order_lst.equals(DBStructure.COLUMN_APK_NAME+" collate nocase"))
					ord_abc.setChecked(true);
				else if(order_lst.equals(DBStructure.COLUMN_APK_DATE +" desc"))
					ord_rct.setChecked(true);
				else if(order_lst.equals(DBStructure.COLUMN_APK_RATING+" desc"))
					ord_rat.setChecked(true);
				else if(order_lst.equals(DBStructure.COLUMN_APK_DOWNLOADS+" desc"))
					ord_dwn.setChecked(true);

				final RadioGroup grp1 = (RadioGroup) view.findViewById(R.id.groupbtn);

				grp1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						if(checkedId == ord_rct.getId()){
							pop_change = true;
							order_lst = DBStructure.COLUMN_APK_DATE +" desc";
						}else if(checkedId == ord_abc.getId()){
							pop_change = true;
							order_lst = DBStructure.COLUMN_APK_NAME+" collate nocase";
						}else if(checkedId == ord_rat.getId()){
							pop_change = true;
							order_lst = DBStructure.COLUMN_APK_RATING+" desc";
						}else if(checkedId == ord_dwn.getId()){
							pop_change = true;
							order_lst = DBStructure.COLUMN_APK_DOWNLOADS+" desc";
						}
						editor.putString("order_list", order_lst);
					}
					
				});
		
		dialog.show();
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(receiverIsRegister){
			unregisterReceiver(receiver);
			unbindService(conn);
		}
		
		
	}
	
	Vector<String> errorRepos;
	Vector<String> updatedRepos;
	boolean errors = false;
	
	private void updateRepos() {
		
		 

		errorRepos = new Vector<String>();
		updatedRepos = new Vector<String>();
		pd = new ProgressDialog(context);
		pd.setMessage(getString(R.string.please_wait));
		pd.show();
		
		parsingProgress=0;
		final AlertDialog alert = new AlertDialog.Builder(context).create();
		
		boolean connectionAvailable = false;
		try {
			connectionAvailable = netstate.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED;
		} catch (Exception e) {
			
			e.printStackTrace()	;
			
			}
		try {
			connectionAvailable = connectionAvailable
					|| netstate.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED;
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			connectionAvailable = connectionAvailable
					|| netstate.getNetworkInfo(6).getState() == NetworkInfo.State.CONNECTED;

		} catch (Exception e) {
			e.printStackTrace();

		}
		try {
			connectionAvailable = connectionAvailable
					|| netstate.getNetworkInfo(9).getState() == NetworkInfo.State.CONNECTED;

		} catch (Exception e) {
			e.printStackTrace();

		}
		
		if (connectionAvailable) {
			new Thread() {

				public void run() {
					try {
						SAXParserFactory spf = SAXParserFactory.newInstance();
						SAXParser sp = spf.newSAXParser();
						servers = new ArrayList<ServerNode>();
						servers = db.getInUseServers();
						if (servers.isEmpty()) {
							alert.setMessage(getString(R.string.updating_norepos));
							errors = true;
							alert.show();
						} else {
							for (ServerNode server : servers) {
								parsingProgress++;
								int parse = downloadList(server.uri,
										server.hash);

								if (parse == 0) {

									sp.parse(new File(XML_PATH),
											new RepoParser(context, handler,
													server.id, XML_PATH));

								} else if (parse == 1) {
									updatedRepos.add(server.uri);

								} else {
									errorRepos.add(server.uri);
									errors = true;
								}

							}
							Intent i = new Intent(Aptoide.this,
									ExtrasService.class);
							i.putExtra("repos", servers);
							startService(i);
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						runOnUiThread(new Runnable() {
							private android.content.DialogInterface.OnClickListener neutralListener = new Dialog.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub

								}
							};

							public void run() {
								pd.dismiss();
								redrawAll();
								if (!errors) {
									alert.setMessage(getString(R.string.update_done_msg1));
									alert.setButton(Dialog.BUTTON_NEUTRAL,
											getString(android.R.string.ok),
											neutralListener);
									alert.show();
								}
								if (!errorRepos.isEmpty()) {
									String repos = "";
									for (String repo : errorRepos) {
										repos=repos+"\n" + repo;
									}
									alert.setMessage(getString(R.string.error_on_update)
											+ repos);
									alert.setButton(Dialog.BUTTON_NEUTRAL,
											getString(android.R.string.ok),
											neutralListener);
									alert.show();
								}
								if (!updatedRepos.isEmpty()) {
									String repos = "";
									for (String repo : updatedRepos) {
										
										repos=repos+"\n" + repo;
									}
									alert.setMessage("Servers are already up to date: "
											+ repos);
									alert.setButton(Dialog.BUTTON_NEUTRAL,
											getString(android.R.string.ok),
											neutralListener);
									alert.show();
								}

							}
						});
						new File(XML_PATH).delete();
						
					}
				};
			}.start();
		}else{
			Log.d("Aptoide","======================= I UPDATEREPOS DISMISS");
			pd.dismiss();
			Toast.makeText(context, getText(R.string.aptoide_error), Toast.LENGTH_LONG).show(); 
		}
	}

	Handler handler = new Handler() {
		
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what > 0) {
				pd.dismiss();
				pd=new ProgressDialog(context);
				pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				pd.setMessage("Parsing: "+parsingProgress+" of "+servers.size());
				if(msg.what<100){
					pd.setMax(msg.what);
				}else{
					pd.setMax(100);
				}
				
				pd.setCancelable(false);
				pd.show();
			} else {
				pd.incrementProgressBy(1);
			}
		}
	};
	
	@Override
	protected void onActivityResult(int requestCode, int arg1, Intent data) {
		super.onActivityResult(requestCode, arg1, data);
		if(requestCode == NEWREPO_FLAG && data != null && data.hasExtra("update")){
			final AlertDialog alrt = new AlertDialog.Builder(this).create();
			alrt.setTitle(getText(R.string.update_repos));
			alrt.setMessage(getText(R.string.update_main));
			alrt.setButton(Dialog.BUTTON_POSITIVE,getText(R.string.dialog_yes), new Dialog.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					forceUpdateRepos();
				}
			});
			alrt.setButton(Dialog.BUTTON_NEGATIVE,getText(R.string.dialog_no), new Dialog.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					alrt.dismiss();
				}
			});
			alrt.show();
		}else if(requestCode == NEWREPO_FLAG && data != null && data.hasExtra("redraw")){
			currentCategory1="none";
			currentCategory2="none";
			
			runOnUiThread(new Runnable() {
				
				public void run() {
					redrawAll();
				}
			});
		}else if(requestCode == 15 && data != null && data.hasExtra("redraw")){
			currentCategory1="none";
			currentCategory2="none";
			
			runOnUiThread(new Runnable() {
				
				public void run() {
					redrawAll();
					loadUItopapps();
				}
			});
		}
	}
	
	private void redrawAll() {
		if(sPref.getBoolean("orderByCategory", true)){
			availAdapter = new CategoryCursorAdapter(context, null, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER,false);
		}else{
			currentCategory1="none";
			currentCategory2="none";
			availAdapter = new AvailableCursorAdapter(context, null, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		}
		
		getSupportLoaderManager().destroyLoader(0x01);
		getSupportLoaderManager().destroyLoader(0x02);
		getSupportLoaderManager().destroyLoader(0x03);
		getSupportLoaderManager().restartLoader(0x01,null,new LoaderCallbacks<Cursor>() {

			public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
				SimpleCursorLoader a = new SimpleCursorLoader(context) {
					
					@Override
					public Cursor loadInBackground() {
						if(sPref.getBoolean("orderByCategory", true)){
							if(currentCategory2.equals("none")){
								if(currentCategory1.equals("none")){
									return db.getCategories1();
								}else{
									
									return db.getCategories2(currentCategory1);
								}
								
							}else{
								runOnUiThread(new Runnable() {
									
									public void run() {
										availAdapter = new AvailableCursorAdapter(context, null, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
										
									}
								});
								return db.getApkByCategory(currentCategory2, order_lst);
							}
							
						}else{
							return db.getApk(order_lst);
						}
						
					}
				};
				
				return a;
			}

			public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
				availAdapter.changeCursor(arg1);
				available_listView.setAdapter(availAdapter);
				redrawInstalled();
			}

			public void onLoaderReset(Loader<Cursor> arg0) {
				availAdapter.changeCursor(null);
				
			}
		});
		
	}
	
	private void redrawAvailable() {
		availAdapter = new CategoryCursorAdapter(context, null, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER,false);
		getSupportLoaderManager().destroyLoader(0x01);
		getSupportLoaderManager().restartLoader(0x01,null,new LoaderCallbacks<Cursor>() {

			public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
				SimpleCursorLoader a = new SimpleCursorLoader(context) {
					
					@Override
					public Cursor loadInBackground() {
						return db.getCategories1();
					}
				};
				
				return a;
			}

			public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
				availAdapter.changeCursor(arg1);
				available_listView.setAdapter(availAdapter);
			}

			public void onLoaderReset(Loader<Cursor> arg0) {
				availAdapter.changeCursor(null);
				
			}
		});
		
	}
	
	public void redrawCategory(final String category){
		availAdapter = new CategoryCursorAdapter(context, null, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER,true);
		getSupportLoaderManager().destroyLoader(0x01);
		getSupportLoaderManager().restartLoader(0x01,null,new LoaderCallbacks<Cursor>() {

			public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
				SimpleCursorLoader a = new SimpleCursorLoader(context) {
					
					@Override
					public Cursor loadInBackground() {
						return db.getCategories2(category);
					}
				};
				
				return a;
			}

			public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
				availAdapter.changeCursor(arg1);
				available_listView.setAdapter(availAdapter);
				System.out.println("Current Category 1:" + currentCategory1);
				System.out.println("Current Category 2:" + currentCategory2);
			}

			public void onLoaderReset(Loader<Cursor> arg0) {
				availAdapter.changeCursor(null);
				
			}
		});
		
		
	}
	
	public void redrawInstalled(){
		getSupportLoaderManager().restartLoader(0x02,null,new LoaderCallbacks<Cursor>() {

			public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
				SimpleCursorLoader a = new SimpleCursorLoader(context) {

					@Override
					public Cursor loadInBackground() {
						return db.getInstalled(order_lst);
					}
				};
				return a;
			}

			public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
				installedAdapter.changeCursor(arg1);
				redrawUpdates();
			}

			public void onLoaderReset(Loader<Cursor> arg0) {
				installedAdapter.changeCursor(null);
				
			}
		});
	}
	
	public void redrawApkByCategory(final String category){
		availAdapter = new AvailableCursorAdapter(context, null, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		getSupportLoaderManager().destroyLoader(0x01);
		getSupportLoaderManager().restartLoader(0x01,null,new LoaderCallbacks<Cursor>() {

			public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
				SimpleCursorLoader a = new SimpleCursorLoader(context) {
					
					@Override
					public Cursor loadInBackground() {
						return db.getApkByCategory(category,order_lst);
					}
				};
				
				return a;
			}

			public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
				availAdapter.changeCursor(arg1);
				available_listView.setAdapter(availAdapter);
				System.out.println("Current Category 1:" + currentCategory1);
				System.out.println("Current Category 2:" + currentCategory2);
			}

			public void onLoaderReset(Loader<Cursor> arg0) {
				availAdapter.changeCursor(null);
				
			}
		});
	}
	
	public void redrawUpdates() {
		getSupportLoaderManager().restartLoader(0x03,null,new LoaderCallbacks<Cursor>() {

			public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
				SimpleCursorLoader a = new SimpleCursorLoader(context) {

					@Override
					public Cursor loadInBackground() {
						return db.getUpdates(order_lst);
					}
				};
				return a;
			}

			public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
				updatesAdapter.changeCursor(arg1);
			}

			public void onLoaderReset(Loader<Cursor> arg0) {
				updatesAdapter.changeCursor(null);
			}
		});
	}
	
	private OnItemClickListener availItemClick = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if(parent.getAdapter().getClass().getName().contains("CategoryCursorAdapter")){
				
				
				if(currentCategory1.equals("none")){
					String category = ((Cursor) parent.getAdapter().getItem(position)).getString(0);
					redrawCategory(category);
					currentCategory1=category;
				}else if(!currentCategory1.equals("none")){
					String category = ((Cursor) parent.getAdapter().getItem(position)).getString(0);
					redrawApkByCategory(category);
					currentCategory2=category;
				}
				
			}else if(parent.getAdapter().getClass().getName().contains("AvailableCursorAdapter")){
				Intent i = new Intent(Aptoide.this,ApkInfo.class);
				i.putExtra("id", parent.getAdapter().getItemId(position));
				i.putExtra("type", "available");
				startActivity(i);
			}
			
			
		}
	};
	
	private OnItemClickListener updatesItemClick = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> parent, View v, int position,
				long arg3) {
			Intent i = new Intent(Aptoide.this,ApkInfo.class);
			i.putExtra("id", parent.getItemIdAtPosition(position));
			i.putExtra("type", "updates");
			startActivity(i);			
		}
	};
	
	private OnItemClickListener installedItemClick = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> parent, View v, int position,
				long arg3) {
			Intent i = new Intent(Aptoide.this,ApkInfo.class);
			i.putExtra("id", parent.getItemIdAtPosition(position));
			i.putExtra("type", "installed");
			startActivity(i);			
		}
	};
	
	CompoundButton.OnCheckedChangeListener adultCheckedListener = new CompoundButton.OnCheckedChangeListener() {

		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			if (isChecked) {
				
				AlertDialog ad = new AlertDialog.Builder(context).create();
				ad.setMessage("Are you at least 21 years old?");
				ad.setButton(Dialog.BUTTON_POSITIVE,getString(R.string.btn_yes), new Dialog.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						editor.putString("app_rating", "Mature");
						editor.commit();
						pd = new ProgressDialog(context);
						pd.setMessage(getString(R.string.please_wait));
						pd.show();
						new Thread(new Runnable() {
							
							public void run() {
								try{
									loadUItopapps();
								}catch (Exception e) {
									e.printStackTrace();
								}finally{
									runOnUiThread(new Runnable() {
										
										public void run() {
											pd.dismiss();
											
										}
									});	
								}
								
								
							}
						}).start();
						redrawAll();
					}
				});
				ad.setButton(Dialog.BUTTON_NEGATIVE,getString(R.string.btn_no), new Dialog.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						((ToggleButton) featured.findViewById(R.id.toggleButton1)).setChecked(false);
						if(adult!=null){
							adult.setChecked(false);
						}
						
					}
				});
				ad.show();
			} else {
				editor.putString("app_rating", "All");
				editor.commit();
				pd = new ProgressDialog(context);
				pd.setMessage(getString(R.string.please_wait));
				pd.show();
				new Thread(new Runnable() {
					
					public void run() {
						loadUItopapps();
						
						runOnUiThread(new Runnable() {
							
							public void run() {
								pd.dismiss();
								
							}
						});
						
					}
				}).start();
				
				redrawAll();
			}
			
			
		}

	};
	
	private OnClickListener searchClick = new OnClickListener() {
		
		public void onClick(View v) {
			onSearchRequested();
		}
	};
	
	String currentCategory1="none";
	String currentCategory2="none";
	private String REMOTE_FILE = "info.xml";
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
			if(vp.getCurrentItem()==1){
				System.out.println("Current Category 1:" + currentCategory1);
				System.out.println("Current Category 2:" + currentCategory2);
				if(!currentCategory1.equals("none")){

					if(currentCategory2.equals("none")){
						redrawAvailable();
						currentCategory1="none";
						return false;
					}else if(!currentCategory1.equals("none")){
						redrawCategory(currentCategory1);
						currentCategory2="none";
						return false;
					}

				}else{

					return super.onKeyDown(keyCode, event);
				}
			}else{
				return super.onKeyDown(keyCode, event);
			}
		}
		
		return super.onKeyDown(keyCode, event);
		
	}
	private int downloadList(final String srv, String delta_hash){
		runOnUiThread(new Runnable() {
			
			public void run() {
				pd.setMessage("Connecting...");
				pd.setCancelable(false);
				
			}
		});
		String url = srv+REMOTE_FILE ;
		try {

			//String delta_hash = db.getServerDelta(srv);
			if(delta_hash!=null && delta_hash.length()>2)
				url = url.concat("?hash="+delta_hash);

			Log.d("Aptoide","A fazer fetch info de: " + url);

			FileOutputStream saveit = new FileOutputStream(XML_PATH);

			HttpResponse mHttpResponse = null; //NetworkApis.getHttpResponse(url, srv, mctx);

			if(mHttpResponse == null){
				for(int xx=0; xx<2; xx++){
					try{
						mHttpResponse = NetworkApis.getHttpResponse(url, srv, context);
						if(mHttpResponse != null)
							break;
						else
							Log.d("Aptoide","--------------------->Connection is null");
					}catch (Exception e) {
						e.printStackTrace();
						}
				}
				if(mHttpResponse == null)
					return -1;
			}

			Log.d("Aptoide","Got status: "+ mHttpResponse.getStatusLine().getStatusCode());
			
			if(mHttpResponse.getStatusLine().getStatusCode() == 200){
				Log.d("Aptoide","Got status 200");


				// see last-modified...
				MessageDigest md5hash = MessageDigest.getInstance("MD5");
				Header lst_modif = mHttpResponse.getLastHeader("Last-Modified");
				

				if(lst_modif != null){
					Log.d("Aptoide","lst_modif not null!");
					String lst_modif_str = lst_modif.getValue();
					String hash_lst_modif = new BigInteger(1,md5hash.digest(lst_modif_str.getBytes())).toString(16);
					Log.d("Aptoide","date is: " + lst_modif_str);
					Log.d("Aptoide","hash date: " + hash_lst_modif);

					String db_lst_modify = db.getUpdateTime(srv);
					if(db_lst_modify == null){
						db.setUpdateTime(hash_lst_modif, srv);
					}else{
						if(!db_lst_modify.equalsIgnoreCase(hash_lst_modif)){
							db.setUpdateTime(hash_lst_modif, srv);
						}else{
							Log.d("Aptoide","No update needed!");
							return 1;
						}
					}
				}
				//@dsilveira #531 +20lines fix OutOfMemory crash			
				InputStream instream = null;

				if((mHttpResponse.getEntity().getContentEncoding() != null) && (mHttpResponse.getEntity().getContentEncoding().getValue().equalsIgnoreCase("gzip"))){

					Log.d("Aptoide","with gzip");
					instream = new GZIPInputStream(mHttpResponse.getEntity().getContent());

				}else{

					Log.d("Aptoide","No gzip");
					instream = mHttpResponse.getEntity().getContent();

				}

				int nRead;
				byte[] data = new byte[1024];

				while ((nRead = instream.read(data, 0, data.length)) != -1) {
					saveit.write(data, 0, nRead);
				}
				

			}else{
				return -1;
				//Does nothing
			}
			return 0;
		} catch (UnknownHostException e){
			e.printStackTrace();
			return -1;
		} catch (ClientProtocolException e) { 
			e.printStackTrace();
			return -1;} 
		catch (IOException e) { 
			e.printStackTrace();
			return -1;}
		catch (IllegalArgumentException e) {
			e.printStackTrace();
			return -1;} 
		catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return -1;}
		catch (Exception e) {
			e.printStackTrace();
			return -1;}
	}
	
	BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals("pt.caixamagica.aptoide.REDRAW")){
				redrawInstalled();
			}
		}
	};
	
	protected void queueDownload(String packageName, String ver, boolean isUpdate){


		Vector<DownloadNode> tmp_serv = new Vector<DownloadNode>();	

		try{

			tmp_serv = db.getPathHash(packageName, ver);

			String localPath = new String(LOCAL_APK_PATH+packageName+"."+ver+".apk");
			String appName = packageName;
			appName = db.getApkName(packageName);
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
//			logins = db.getLogin(downloadNode.getRepo());
			//			downloadNode.getRemotePath()
//			downloadNode.setLogins(logins);
			Log.d("Aptoide-BaseManagement","queueing download: "+packageName +" "+downloadNode.getSize());	
			downloadQueueService.setCurrentContext(context);
			downloadQueueService.startDownload(downloadNode);

		} catch(Exception e){	
			e.printStackTrace();
		}
	}
	
	public void forceUpdateRepos(){
		
		if(!(db.getInUseServers().size()!=0)){
			final AlertDialog upd_alrt = new AlertDialog.Builder(this).create();
			upd_alrt.setIcon(android.R.drawable.ic_dialog_alert);
			upd_alrt.setTitle(getText(R.string.update_repos));
			upd_alrt.setMessage(getText(R.string.updating_norepos));
			upd_alrt.setButton(Dialog.BUTTON_NEUTRAL,getText(R.string.btn_ok), new Dialog.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {

				}
			});
			upd_alrt.show();
		}else{
			boolean freeConnectionAvailable = false;
			try {
				freeConnectionAvailable = netstate.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED;
				freeConnectionAvailable = freeConnectionAvailable || netstate.getNetworkInfo(9).getState() == NetworkInfo.State.CONNECTED;

			} catch (Exception e) { }

			if(freeConnectionAvailable){
				updateRepos();
			}else{
				
				if (sPref.getBoolean("3g", true)) {
					final View v = LayoutInflater.from(context).inflate(R.layout.remember, null);
					final Builder upd_alrt_builder = new AlertDialog.Builder(this).setView(v);
					final AlertDialog upd_alrt = upd_alrt_builder.create();
					upd_alrt.setIcon(android.R.drawable.ic_dialog_alert);
					upd_alrt.setTitle(getText(R.string.update_repos));
					upd_alrt.setMessage(getText(R.string.updating_3g));
					
					upd_alrt.setButton(Dialog.BUTTON_POSITIVE,getText(R.string.btn_yes), new Dialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							updateRepos();
							if(((CheckBox) v.findViewById(R.id.remember)).isChecked()){
								editor.putBoolean("3g", false);
								editor.commit();
							}
							
						}
					});
					upd_alrt.setButton(Dialog.BUTTON_NEGATIVE,getText(R.string.btn_no), new Dialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							upd_alrt.dismiss();
						}
					});
					upd_alrt.show();
				}else{
					updateRepos();
				}
				
				
			}
			
		}

	}
	
}
