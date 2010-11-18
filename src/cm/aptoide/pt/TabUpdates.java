package cm.aptoide.pt;

import java.io.File;
import java.util.Vector;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


public class TabUpdates extends BaseManagement implements OnItemClickListener{

	private ListView lv = null;

	private DbHandler db = null;
	private Context mctx = null;
	
	private int pos = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		lv = new ListView(this);
		lv.setFastScrollEnabled(true);
		lv.setOnItemClickListener(this);
		db = new DbHandler(this);
		mctx = this;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if(sPref.getBoolean("changeupdt", false)){
			lv.setAdapter(updateAdpt);
			setContentView(lv);
			lv.setSelection(pos-1);
			prefEdit.remove("changeupdt");
			prefEdit.commit();
		}
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		final String pkg_id = ((LinearLayout)arg1).getTag().toString();

		pos = arg2;
		
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
			t5.setText("No info availale on server. Search market by pressing the button below for more info.");
		}


		p.setButton2("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				return;
			} });

		p.setButton(getString(R.string.update), new DialogInterface.OnClickListener() {
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
							updateApk(apk_path, pkg_id);
						}
					}
				}.start(); 	
			} });
		p.setButton3("Search Market", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				p.dismiss();					
				Intent intent = new Intent();
				intent.setAction(android.content.Intent.ACTION_VIEW);
				intent.setData(Uri.parse("market://details?id="+pkg_id));
				try{
					startActivity(intent);
				}catch (ActivityNotFoundException e){
					Toast.makeText(mctx, "You don't have Google Market installed!", Toast.LENGTH_LONG).show();
				}
			} });

		p.show();
		
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}	

}
