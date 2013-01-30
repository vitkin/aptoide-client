/*******************************************************************************
 * Copyright (c) 2012 rmateus.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package cm.aptoide.pt;

import java.io.File;
import java.text.DecimalFormat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cm.aptoide.pt.preferences.ManagerPreferences;

public class Settings extends PreferenceActivity{
	String aptoide_path = Environment.getExternalStorageDirectory()+"/.aptoide/";
	String icon_path = Environment.getExternalStorageDirectory()+"/.aptoide/icons/";
	ManagerPreferences preferences;
	Context mctx;
	private boolean unlocked = false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        mctx = this;
        new GetDirSize().execute(new File(aptoide_path),new File(icon_path));
        preferences = new ManagerPreferences(this);
//        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener() {
//			
//			@Override
//			public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
//					String key) {
//				preferences.setIconDownloadPermissions(new ViewIconDownloadPermissions(((CheckBoxPreference)findPreference("wifi")).isChecked(),
//						((CheckBoxPreference)findPreference("ethernet")).isChecked(),
//						((CheckBoxPreference)findPreference("4g")).isChecked(),
//						((CheckBoxPreference)findPreference("3g")).isChecked()));
//			}
//		});
        findPreference("clearcache").setOnPreferenceClickListener(new OnPreferenceClickListener() {
			

			@Override
			public boolean onPreferenceClick(Preference preference) {
				if(unlocked){
					new DeleteDir().execute(new File(icon_path));
				}
				
				return false;
			}
		});
		findPreference("clearapk").setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				if(unlocked){
					new DeleteDir().execute(new File(aptoide_path));
				}
				
				return false;
			}
		});
		
//		Preference hwspecs = (Preference) findPreference("hwspecs");
//		hwspecs.setIntent(new Intent(getBaseContext(), HWSpecActivity.class));
		Preference hwSpecs = (Preference) findPreference("hwspecs");
		hwSpecs.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				
				View hwSpecsView = LinearLayout.inflate(Settings.this, R.layout.dialog_hw_specs, null);
				Builder dialogBuilder = new AlertDialog.Builder(Settings.this).setView(hwSpecsView);
				final AlertDialog hwSpecsDialog = dialogBuilder.create();
				hwSpecsDialog.setIcon(android.R.drawable.ic_menu_info_details);
				hwSpecsDialog.setTitle(getString(R.string.setting_hwspecstitle));
				
				TextView sdkVer= (TextView) hwSpecsView.findViewById(R.id.sdkver);
				TextView screenSize = (TextView) hwSpecsView.findViewById(R.id.screenSize);
				TextView esglVer = (TextView) hwSpecsView.findViewById(R.id.esglVer);
				
				sdkVer.setText(HWSpecifications.getSdkVer()+"");
				screenSize.setText(HWSpecifications.getScreenSize(getBaseContext())+"");
				esglVer.setText(HWSpecifications.getEsglVer(getBaseContext()));
				
				hwSpecsDialog.setButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						hwSpecsDialog.dismiss();
					}
				});
				
				hwSpecsDialog.show();
				return true;
			}
		});
		
		Preference showExcluded = (Preference) findPreference("showexcludedupdates");
		showExcluded.setIntent(new Intent(getBaseContext(), ExcludedUpdatesActivity.class));
		
		EditTextPreference maxFileCache = (EditTextPreference) findPreference("maxFileCache");
		
		
		
		maxFileCache.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				
				System.out.println("asdfasdf" + preference);
				System.out.println("asdfasdf" + newValue);
				
				return false;
			}
		});
		
		maxFileCache.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
		
	}

	public class DeleteDir extends AsyncTask<File, Void, Void>{
		ProgressDialog pd;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = new ProgressDialog(mctx);
			pd.setMessage(getString(R.string.please_wait));
			pd.show();
		}
		@Override
		protected Void doInBackground(File... params) {
			deleteDirectory(params[0]);
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			pd.dismiss();
			Toast toast= Toast.makeText(mctx, mctx.getString(R.string.clear_cache_sucess), Toast.LENGTH_SHORT);  
			toast.show(); 
			new GetDirSize().execute(new File(aptoide_path),new File(icon_path));
		}
		
	}
	
	
	
	
	
	public class GetDirSize extends AsyncTask<File, Void, Double[]>{
		double getDirSize(File dir) {
			double size = 0;
			if (dir.isFile()) {
				size = dir.length();
			} else {
				File[] subFiles = dir.listFiles();
				for (File file : subFiles) {
					if (file.isFile()) {
						size += file.length();
					} else {
						size += this.getDirSize(file);
					}

				}
			}

			return size;
		}
		@Override
		protected Double[] doInBackground(File... dir) {
			Double [] sizes = new Double[2];
			
			for (int i = 0; i!=sizes.length;i++){
				sizes[i]=this.getDirSize(dir[i]) / 1024 / 1024;
			}
			return sizes;
		}
		
		@Override
		protected void onPostExecute(Double[] result) {
			super.onPostExecute(result);
			redrawSizes(result);
			unlocked=true;
		}
		
	}
	
	private void redrawSizes(Double[] size) {
		findPreference("clearapk").setSummary(getString(R.string.clearcontent_sum)+" (Using " +new DecimalFormat("#.##").format(size[0])+"MB)");
		findPreference("clearcache").setSummary(getString(R.string.clearcache_sum)+" (Using " +new DecimalFormat("#.##").format(size[1])+"MB)");
	}
	
	
	static public boolean deleteDirectory(File path) {
	    if( path.exists() ) {
	      File[] files = path.listFiles();
	      if (files == null) {
	          return true;
	      }
	      for(int i=0; i<files.length; i++) {
	         if(files[i].isDirectory()) {
	           deleteDirectory(files[i]);
	         }
	         else {
	           files[i].delete();
	         }
	      }
	    }
	    return true ;
	  }
	
	

}
