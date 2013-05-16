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

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.ProgressDialog;
import org.holoeverywhere.widget.Toast;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.text.InputType;
import android.view.View;
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
        preferences = new ManagerPreferences(mctx);
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
        
        
        findPreference("clearcache").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			

			@Override
			public boolean onPreferenceClick(Preference preference) {
				if(unlocked){
					new DeleteDir().execute(new File(icon_path));
				}
				
				return false;
			}
		});
		findPreference("clearapk").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			
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
		hwSpecs.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mctx);
				alertDialogBuilder.setTitle(getString(R.string.setting_hwspecstitle));
				alertDialogBuilder
					.setIcon(android.R.drawable.ic_menu_info_details)
					.setMessage(getString(R.string.setting_sdk_version)+ ": "+HWSpecifications.getSdkVer()+"\n" +
							    getString(R.string.setting_screen_size)+ ": "+HWSpecifications.getScreenSize(mctx)+"\n" +
							    getString(R.string.setting_esgl_version)+ ": "+HWSpecifications.getEsglVer(mctx))
					.setCancelable(false)
					.setNeutralButton(getString(android.R.string.ok),new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) {
							return;
						}
					 });
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
				
				return true;
			}
		});
		
		if(!ApplicationAptoide.MATURECONTENTSWITCH){
			CheckBoxPreference mCheckBoxPref = (CheckBoxPreference) findPreference("matureChkBox");
			PreferenceCategory mCategory = (PreferenceCategory) findPreference("filters");
			mCategory.removePreference(mCheckBoxPref);
		}
		
		Preference showExcluded = (Preference) findPreference("showexcludedupdates");
		showExcluded.setIntent(new Intent(mctx, ExcludedUpdatesActivity.class));
		
		EditTextPreference maxFileCache = (EditTextPreference) findPreference("maxFileCache");
		
		maxFileCache.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
		maxFileCache.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				((EditTextPreference) preference).getEditText().setText(PreferenceManager.getDefaultSharedPreferences(mctx).getString("maxFileCache","200"));
				return false;
			}
		});
		

		Preference about = (Preference) findPreference("aboutDialog");
		about.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				View view = LayoutInflater.from(mctx).inflate(R.layout.dialog_about, null);
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mctx).setView(view);

				final AlertDialog aboutDialog = alertDialogBuilder.create();


				aboutDialog.setTitle(getString(R.string.about));
				aboutDialog.setIcon(android.R.drawable.ic_menu_info_details);
				aboutDialog.setCancelable(false);
				aboutDialog.setButton(Dialog.BUTTON_NEUTRAL, "Ok", new Dialog.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
				aboutDialog.show();

				return true;
			}
		});
			
		if(ApplicationAptoide.PARTNERID!=null){
			PreferenceScreen preferenceScreen = getPreferenceScreen();
			Preference etp = (Preference) preferenceScreen.findPreference("aboutDialog");

			PreferenceGroup preferenceGroup = (PreferenceGroup) findPreference("about");
			preferenceGroup.removePreference(etp);
			preferenceScreen.removePreference(preferenceGroup);
			
		}
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
			try{
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
			}catch (Exception e){
				e.printStackTrace();
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
