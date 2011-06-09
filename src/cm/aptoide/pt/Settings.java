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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

public class Settings extends PreferenceActivity{
	
	private SharedPreferences sPref;
	private SharedPreferences.Editor prefEdit;
	private SharedPreferences sPrefFull;
	private SharedPreferences.Editor prefEditFull;
	
	private Intent rtrn = new Intent();
	
	private ProgressDialog pd = null;
	private Context mctx = null;
	
	//private Button clear_cache = null;
	
	/*private RadioGroup grp2 = null;
	private boolean catg = false;
	private boolean mix = false;
	private int ctg_id;
	private int mix_id;*/
	
	private Preference clear_cache = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.settings);
		
		addPreferencesFromResource(R.xml.settingspref);
		
		mctx = this;
		
		sPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		sPrefFull = getSharedPreferences("aptoide_prefs", MODE_PRIVATE);
		prefEditFull = sPrefFull.edit();

		if(sPref.getString("icdown",null) == null){
			prefEdit = sPref.edit();
			prefEdit.putString("icdown", sPrefFull.getString("icdown", "g3w"));
		}
		
		
		Log.d("Aptoide","The preference is: " + sPref.getString("icdown", "error"));
		Log.d("Aptoide","The preference is: " + sPrefFull.getString("icdown", "error"));
		
		clear_cache = (Preference)findPreference("clearcache");
		clear_cache.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			public boolean onPreferenceClick(Preference preference) {
				
				final AlertDialog alrt = new AlertDialog.Builder(mctx).create();
				alrt.setTitle("Caution");
				alrt.setMessage("Do you wish to empty Aptoide cache?");
				alrt.setButton("yes", new OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						alrt.dismiss();
						pd = ProgressDialog.show(mctx, getText(R.string.top_please_wait), getText(R.string.settings_cache), true);
						pd.setIcon(android.R.drawable.ic_dialog_info);
						
						new Thread() {
							public void run() {
								File aptoide_dir = new File(getText(R.string.base_path).toString());
								for(File fl: aptoide_dir.listFiles()){
									if(fl.isFile() && fl.getName().toLowerCase().endsWith("apk")){
										fl.delete();
									}
								}
								File icons_dir = new File(getText(R.string.icons_path).toString());
								for(File fl: icons_dir.listFiles()){
									fl.delete();
								}
								done_handler.sendEmptyMessage(0);
							}
						}.start(); 
					}
				});
				
				alrt.setButton2("No", new OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						alrt.dismiss();
						
					}
				});
				
				alrt.show();
				
				return true;
			}
		});
		
		/*
		
		grp2 = (RadioGroup) findViewById(R.id.groupshow);
		grp2.setOnCheckedChangeListener(this);
		
		RadioButton btn1 = (RadioButton) findViewById(R.id.shw_ct);
		RadioButton btn2 = (RadioButton) findViewById(R.id.shw_all);
		
		ctg_id = btn1.getId();
		mix_id = btn2.getId();
		
		if(sPref.getBoolean("mode", false))
			btn1.setChecked(true);
		else
			btn2.setChecked(true);
		
		
		Button btn_ok = (Button) findViewById(R.id.btn_save);
		btn_ok.setOnClickListener(this);
		
		clear_cache = (Button) findViewById(R.id.clear_cache);
		clear_cache.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				pd = ProgressDialog.show(mctx, getText(R.string.top_please_wait), getText(R.string.settings_cache), true);
				pd.setIcon(android.R.drawable.ic_dialog_info);
				
				new Thread() {
					public void run() {
						File aptoide_dir = new File(getText(R.string.base_path).toString());
						for(File fl: aptoide_dir.listFiles()){
							if(fl.isFile() && fl.getName().toLowerCase().endsWith("apk")){
								fl.delete();
							}
						}
						File icons_dir = new File(getText(R.string.icons_path).toString());
						for(File fl: icons_dir.listFiles()){
							fl.delete();
						}
						done_handler.sendEmptyMessage(0);
					}
				}.start(); 
			}
		});*/
	}

	/*public void onCheckedChanged(RadioGroup group, int checkedId) {
		
		if(group.equals(grp2)){
			catg = false;
			mix = false;
			
			if(checkedId == ctg_id)
				catg = true;
			else if(checkedId == mix_id)
				mix = true;
		}
	}*/
	

/*	public void onClick(View v) {
		if(catg == true)
			rtrn.putExtra("mode", true);
		else if(mix == true)
			rtrn.putExtra("mode", false);
		finish();
	}*/

	@Override
	public void finish() {
		prefEditFull.putString("icdown", sPref.getString("icdown", "error"));
		prefEditFull.commit();
		if(sPref.getString("icdown", "error").equalsIgnoreCase("nd")){
			Intent serv = new Intent(mctx,FetchIconsService.class);
			mctx.stopService(serv);
		}
		//rtrn.putExtra("settings", 0);
		this.setResult(RESULT_OK, rtrn);
		super.finish();
	}

	private Handler done_handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(pd.isShowing())
				pd.dismiss();
			clear_cache.setEnabled(false);
		}
	};
	
}
