package cm.aptoide.pt;

import java.util.ArrayList;
import java.util.Vector;

import cm.aptoide.summerinternship2011.multiversion.VersionApk;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;


public class TabInstalled extends BaseManagement implements OnItemClickListener{
	
	private ListView lv = null;
	
    private InstallApkListener installApkListener = null;
    private Boolean installApkListenerIsRegistered = false;

	
	private DbHandler db = null;
	//private Context mctx = null;
	
	private int pos = -1;
	

	protected class InstallApkListener extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("Aptoide-TabInstalled", "broadcast received");
			if (intent.getAction().equals("pt.caixamagica.aptoide.INSTALL_APK_ACTION")) {
				installApk(intent.getStringExtra("localPath"), intent.getStringExtra("version"));
			}
			if (intent.getAction().equals("pt.caixamagica.aptoide.UPDATE_APK_ACTION")) {
				updateApk(intent.getStringExtra("localPath"), intent.getStringExtra("apkid"), intent.getStringExtra("version"));
			}
		}
	}
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		lv = new ListView(this);
		lv.setBackgroundDrawable(this.getApplicationContext().getResources().getDrawable(R.drawable.backgroundlistinst));
		lv.setCacheColorHint(0);
		lv.setFastScrollEnabled(true);
		lv.setOnItemClickListener(this);
		db = new DbHandler(this);
		
		installApkListener = new InstallApkListener();
		//mctx = this;
	}
	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		menu.add(Menu.NONE, EnumOptionsMenu.DISPLAY_OPTIONS.ordinal(), EnumOptionsMenu.DISPLAY_OPTIONS.ordinal(), R.string.menu_order)
//			.setIcon(android.R.drawable.ic_menu_sort_by_size);
//		return super.onCreateOptionsMenu(menu);
//	}
//	
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		EnumOptionsMenu menuEntry = EnumOptionsMenu.reverseOrdinal(item.getItemId());
//		Log.d("Aptoide-OptionsMenu", "menuOption: "+menuEntry+" itemid: "+item.getItemId());
//		switch (menuEntry) {
//		case DISPLAY_OPTIONS:
//			/*if(resumeMe()){
//				lv.setAdapter(instAdpt);
//				setContentView(lv);
//				lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
//				lv.setSelection(pos-1);
//			}*/
//			
//			final AlertDialog p = resumeMe();
//			p.show();
//			
//			new Thread(){
//				@Override
//				public void run() {
//					super.run();
//					while(p.isShowing()){
//						try {
//							Thread.sleep(1000);
//						} catch (InterruptedException e) {	}
//					}
//					displayRefresh.sendEmptyMessage(0);
//				}
//			}.start();
//				
//		}
//		return super.onOptionsItemSelected(item);
//	}
	
	@Override
	protected void onPause() {
		if (!installApkListenerIsRegistered) {
            unregisterReceiver(installApkListener);
            installApkListenerIsRegistered = false;
        }
		Log.d("Aptoide-TabInstalled", "installApkListenerIsUnregistered");
		
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d("Aptoide-TabInstalled", "onResume");
		
		if (!installApkListenerIsRegistered) {
            registerReceiver(installApkListener, new IntentFilter("pt.caixamagica.aptoide.INSTALL_APK_ACTION"));
            registerReceiver(installApkListener, new IntentFilter("pt.caixamagica.aptoide.UPDATE_APK_ACTION"));
            installApkListenerIsRegistered = true;
        }
		Log.d("Aptoide-TabInstalled", "installApkListenerIsRegistered");

		
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

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		final String pkg_id = ((LinearLayout)arg1).getTag().toString();

		pos = arg2;

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
		apkinfo.putExtra("type", 1);
		apkinfo.putExtra("vercode", Integer.parseInt(tmp_get.get(7)));
		ArrayList<VersionApk> oldVersionsApk = db.getOldApks(pkg_id);
		
		try {
			PackageManager mPm = getApplicationContext().getPackageManager();
			PackageInfo pkginfo = mPm.getPackageInfo(pkg_id, 0);
			apkinfo.putExtra("instversion", new VersionApk(pkginfo.versionName,pkginfo.versionCode,pkg_id,-1));
			VersionApk versionApkPassed = new VersionApk(tmp_get.get(1),Integer.parseInt(tmp_get.get(7)),pkg_id,-1);
			VersionApk versionApkInstalled = new VersionApk(pkginfo.versionName,pkginfo.versionCode,pkg_id,-1);
			oldVersionsApk.add(versionApkPassed);
			
			if(!oldVersionsApk.contains(versionApkInstalled)){
				apkinfo.putExtra("applicationExistsInRepo", false);
			}
			
			oldVersionsApk.remove(versionApkPassed);
		} catch (NameNotFoundException e) {
			//Not installed... do nothing
		}
		
		apkinfo.putParcelableArrayListExtra("oldVersions", oldVersionsApk);
		startActivityForResult(apkinfo,30);
		
		/*
		Vector<String> tmp_get = db.getApk(pkg_id);
		String tmp_path = this.getString(R.string.icons_path)+pkg_id;
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
		p.setTitle(db.getName(pkg_id));
		TextView t1 = (TextView) view.findViewById(R.id.n11);
		t1.setText(tmp_get.firstElement());
		TextView t2 = (TextView) view.findViewById(R.id.n22);
		t2.setText(tmp_get.get(1));
		TextView t3 = (TextView) view.findViewById(R.id.n33);
		t3.setText(tmp_get.get(2));
		TextView t4 = (TextView) view.findViewById(R.id.n44);
		t4.setText(tmp_get.get(3));
		TextView t5 = (TextView) view.findViewById(R.id.n55);
		String tmpi = db.getDescript(pkg_id);
		if(!(tmpi == null)){
			t5.setText(tmpi);
		}else{
			t5.setText(getText(R.string.app_pop_up_no_info));
		}

		TextView t6 = (TextView) view.findViewById(R.id.down_n);
		t6.setText(tmp_get.get(4));

		p.setButton2(getText(R.string.btn_ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				return;
			} });

		p.setButton(getString(R.string.rem), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				removeApk(pkg_id);
			} });
		p.setButton3(getText(R.string.btn_search_market), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				p.dismiss();					
				Intent intent = new Intent();
				intent.setAction(android.content.Intent.ACTION_VIEW);
				intent.setData(Uri.parse("market://details?id="+pkg_id));
				try{
					startActivity(intent);
				}catch (ActivityNotFoundException e){
					Toast.makeText(mctx, getText(R.string.error_no_market), Toast.LENGTH_LONG).show();
				}
			} });

		p.show();
		*/
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 30 && data != null && data.hasExtra("apkid")){
			new Thread() {
				public void run() {
					String apk_id = data.getStringExtra("apkid");
					Log.d("Aptoide", ".... removing: " + apk_id);
					removeApk(apk_id);
				}
			}.start();
		}
	}



	protected Handler displayRefresh = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			redraw();
			lv.setAdapter(instAdpt);
			setContentView(lv);
			lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			lv.setSelection(pos-1);
		}
		 
	 };
	
	
	protected Handler displayRefresh2 = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(sPref.getBoolean("changeinst", false)){
				lv.setAdapter(instAdpt);
				setContentView(lv);
				lv.setSelection(pos-1);
				prefEdit.remove("changeinst");
				prefEdit.commit();
			}
		}
		
	};
	 
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}	
}
