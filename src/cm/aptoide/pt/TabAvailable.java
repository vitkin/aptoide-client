package cm.aptoide.pt;

import java.io.File;
import java.util.Vector;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
		lv.setOnItemClickListener(this);
		lv.setFastScrollEnabled(true);

		
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
		menu.add(Menu.NONE, 3, 3, R.string.menu_order)
		.setIcon(android.R.drawable.ic_menu_sort_by_size);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 3:
			Log.d("Aptoide","Estamos aqui...");
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
						Log.d("Aptoide","We enter pop stuf...");
						if(sPref.getBoolean("mode", false)){
							Log.d("Aptoide","We enter pop mod on!");
							if(!(shown_now == null) || main_shown_now == 2){
								handler_adpt = getGivenCatg(shown_now, main_shown_now);
							}else{
								handler_adpt = getRootCtg();
							}
							displayRefresh.sendEmptyMessage(0);
						}else{
							shown_now = null;
							handler_adpt = null;
							displayRefresh.sendEmptyMessage(0);
						}
						prefEdit.remove("pop_changes");
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

			p.setButton(getString(R.string.install), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					p.dismiss();					
					new Thread() {
						public void run() {
							String apk_path = downloadFile(pkg_id);
							Message msg = new Message();
							msg.arg1 = 1;
							download_handler.sendMessage(msg);
							if(apk_path == null){
								download_error_handler.sendEmptyMessage(0);
							}else{
								installApk(apk_path);
							}
						}
					}.start(); 	
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
		}
	}
	
	
	protected Handler displayRefresh = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			redraw();
			if(handler_adpt == null)
				handler_adpt = availAdpt;
			lv.setAdapter(handler_adpt);
			setContentView(lv);
			lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			lv.setSelection(pos-1);
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
