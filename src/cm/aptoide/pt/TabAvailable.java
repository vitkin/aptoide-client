package cm.aptoide.pt;

import java.util.Vector;


import cm.aptoide.pt.multiversion.VersionApk;
import cm.aptoide.pt.utils.EnumOptionsMenu;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class TabAvailable extends BaseManagement implements OnItemClickListener{

	static protected SharedPreferences sPref;
	
	private ListView lv = null;
	
	private DbHandler db = null;
	private Context mctx = null;
	
	private int pos = -1;
	
	private int deep = 0;
		
	private String shown_now = null;
	private int main_shown_now = -1;
	
	private SimpleAdapter handler_adpt = null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		db = new DbHandler(this);
		mctx = this;
		sPref = getSharedPreferences("aptoide_prefs", MODE_PRIVATE);
		lv = new ListView(this);
		
		if(Configs.BACKGROUND_ON_TABS){
			if(Configs.INTERFACE_TABS_ON_BOTTOM){
				lv.setBackgroundDrawable(this.getApplicationContext().getResources().getDrawable(R.drawable.backgroundlistava_tab_bottom));
			}else{
				lv.setBackgroundDrawable(this.getApplicationContext().getResources().getDrawable(R.drawable.backgroundlistava_tab_top));
			}
		}
		
		lv.setCacheColorHint(0);
		lv.setOnItemClickListener(this);
		lv.setFastScrollEnabled(true);
		
		final GestureDetector changeTabGes = new GestureDetector(new ChangeTab(((TabActivity)this.getParent()).getTabHost()));
        lv.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return changeTabGes.onTouchEvent(event); 
            }
        });
		
		new Thread(){

			@Override
			public void run() {
				super.run();
				if(getAvailable(null,-1) == null){
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) { }
				}else{
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) { }
					onResumeHandler.sendEmptyMessage(0);
				}
			}			
		}.start();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE,EnumOptionsMenu.UPDATE_REPO.ordinal(),EnumOptionsMenu.UPDATE_REPO.ordinal(),R.string.menu_update_repo)
			.setIcon(android.R.drawable.ic_menu_rotate);
		menu.add(Menu.NONE, EnumOptionsMenu.DISPLAY_OPTIONS.ordinal(), EnumOptionsMenu.DISPLAY_OPTIONS.ordinal(), R.string.menu_display_options)
			.setIcon(android.R.drawable.ic_menu_sort_by_size);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		EnumOptionsMenu menuEntry = EnumOptionsMenu.reverseOrdinal(item.getItemId());
		Log.d("Aptoide-OptionsMenu", "menuOption: "+menuEntry+" itemid: "+item.getItemId());
		switch (menuEntry) {
		case UPDATE_REPO:
			Intent remoteUpdate = new Intent("pt.caixamagica.aptoide.UPDATE_REPOS");
			remoteUpdate.setClassName("cm.aptoide.pt", "cm.aptoide.pt.RemoteInTab");
			startActivity(remoteUpdate);
			return true;
		case DISPLAY_OPTIONS:
			final AlertDialog p = resumeMe();
			p.show();
			
			new Thread(){
				@Override
				public void run() {
					super.run();
					while(p.isShowing()){
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {	}
					}
					if(sPref.getBoolean("pop_changes", false)){
						prefEdit.remove("pop_changes");
						prefEdit.commit();
						if(sPref.getBoolean("mode", false)){
							if(!(shown_now == null) || main_shown_now == 2){
								handler_adpt = getGivenCatg(shown_now, main_shown_now);
							}else{
								handler_adpt = getRootCtg();
							}
							displayRefresh.sendEmptyMessage(0);
						}else{
							shown_now = null;
							handler_adpt = null;
							redrawHandler.sendEmptyMessage(0);
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e1) { }
							while(sPref.getBoolean("redrawis", false)){
								try {
									Thread.sleep(500);
								} catch (InterruptedException e) { }
							}
							displayRefresh.sendEmptyMessage(0);
						}
					}
				}
			}.start();
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		new Thread(){
			@Override
			public void run() {
				super.run();
				while(sPref.getBoolean("redrawis", false)){
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) { }
				}
				displayRefresh2.sendEmptyMessage(0);
			}
			
		}.start();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && sPref.getBoolean("mode", false) && deep > 0) {
	        switch (deep) {
			case 1:
				lv.setAdapter(getRootCtg());
				setContentView(lv);
				lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
				deep = 0;
				break;
			case 2:
				lv.setAdapter(getAppCtg());
				setContentView(lv);
				lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
				deep = 1;
				break;
			case 3:
				lv.setAdapter(getGamesCtg());
				setContentView(lv);
				lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
				deep = 1;
				break;
			}
			
	        return true;
	    }
		return super.onKeyDown(keyCode, event);
	}


	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

		pos = arg2;
		
		final String pkg_id = ((LinearLayout)arg1).getTag().toString();

		if(pkg_id.equals("Applications")){
			shown_now = null;
			Toast.makeText(mctx, "Applications", Toast.LENGTH_SHORT).show();
			lv.setAdapter(getAppCtg());
			setContentView(lv);
			lv.setSelection(pos-1);
			deep = 1;
		}else if(pkg_id.equals("Games")){
			shown_now = null;
			Toast.makeText(mctx, "Games", Toast.LENGTH_SHORT).show();
			lv.setAdapter(getGamesCtg());
			setContentView(lv);
			lv.setSelection(pos-1);
			deep = 1;
		}else if(pkg_id.equals("Others")){
			shown_now = null;
			main_shown_now = 2;
			Toast.makeText(mctx, "Others", Toast.LENGTH_SHORT).show();
			lv.setAdapter(getGivenCatg(null, 2));
			setContentView(lv);
			lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			lv.setSelection(pos-1);
			deep = 1;
		}else if(pkg_id.equals("apps")){
			shown_now = ((TextView)((LinearLayout)arg1).findViewById(R.id.name)).getText().toString();
			main_shown_now = 1;
			Toast.makeText(mctx, "Applications - " + ((TextView)((LinearLayout)arg1).findViewById(R.id.name)).getText().toString(), Toast.LENGTH_SHORT).show();
			lv.setAdapter(getGivenCatg(((TextView)((LinearLayout)arg1).findViewById(R.id.name)).getText().toString(),1));
			setContentView(lv);
			lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			lv.setSelection(pos-1);
			deep = 2;
		}else if(pkg_id.equals("games")){
			shown_now = ((TextView)((LinearLayout)arg1).findViewById(R.id.name)).getText().toString();
			main_shown_now = 0;
			Toast.makeText(mctx, "Games - " + ((TextView)((LinearLayout)arg1).findViewById(R.id.name)).getText().toString(), Toast.LENGTH_SHORT).show();
			lv.setAdapter(getGivenCatg(((TextView)((LinearLayout)arg1).findViewById(R.id.name)).getText().toString(),0));
			setContentView(lv);
			lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			lv.setSelection(pos-1);
			deep = 3;
		}else{
			
			Intent apkinfo = new Intent(this,ApkInfo.class);
			apkinfo.putExtra("name", db.getName(pkg_id));
			apkinfo.putExtra("icon", this.getString(R.string.icons_path)+pkg_id);
			apkinfo.putExtra("apk_id", pkg_id);
			
			String tmpi = db.getDescript(pkg_id);
			if(!(tmpi == null)){
				apkinfo.putExtra("about",tmpi);
			}else{
				apkinfo.putExtra("about",getText(R.string.app_pop_up_no_info));
			}
			
			Vector<String> tmp_get = db.getApk(pkg_id);
			apkinfo.putExtra("server", tmp_get.firstElement());
			apkinfo.putExtra("version", tmp_get.get(1));
			apkinfo.putExtra("dwn", tmp_get.get(4));
			apkinfo.putExtra("rat", tmp_get.get(5));
			apkinfo.putExtra("size", tmp_get.get(6));
			apkinfo.putExtra("type", 0);
			apkinfo.putExtra("vercode", Integer.parseInt(tmp_get.get(7)));
			
			try {
				PackageManager mPm = getApplicationContext().getPackageManager();
				PackageInfo pkginfo = mPm.getPackageInfo(pkg_id, 0);
				apkinfo.putExtra("instversion", new VersionApk(pkginfo.versionName, pkginfo.versionCode,pkg_id,-1));
			} catch (NameNotFoundException e) {
				//Not installed... do nothing
			}
			
			//apkinfo.putExtra("vercode", Integer.parseInt(tmp_get.get(7)));new DbHandler(this).getOldAndNewApks(pkg_id).contains(new VersionApk(tmp_get.get(1).substring(1,tmp_get.get(1).length()-1), Integer.parseInt(tmp_get.get(7)), pkg_id, 0));
			
			apkinfo.putParcelableArrayListExtra("oldVersions", db.getOldApks(pkg_id));
			
			startActivityForResult(apkinfo,30);
		
		}
	}
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 30 && data != null && data.hasExtra("apkid") && data.hasExtra("version")){
			String apk_id = data.getStringExtra("apkid");
			Log.d("Aptoide", "....... getting: " + apk_id);
			queueDownload(apk_id, data.getStringExtra("version"), false);
		}
	}



	protected Handler displayRefresh = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(handler_adpt == null){
				handler_adpt = availAdpt;
			}
			lv.setAdapter(handler_adpt);
			setContentView(lv);
			lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			lv.setSelection(pos-1);
		}
		 
	 };
	 
	 protected Handler redrawHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			redraw();
		}
		 
	 };
	 
	 protected Handler onResumeHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			lv.setAdapter(getAvailable(null,-1));
			setContentView(lv);
		}
		 
	 };
	
	 
	 protected Handler displayRefresh2 = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if(sPref.getBoolean("changeavail", false)){
					lv.setAdapter(getAvailable(shown_now,main_shown_now));
					setContentView(lv);
					lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
					lv.setSelection(pos-1);
					prefEdit.remove("changeavail");
					prefEdit.commit();
				}
			}
			
		};
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	
}
