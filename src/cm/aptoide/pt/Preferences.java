package cm.aptoide.pt;

import java.io.File;

import cm.aptoide.pt.webservices.login.Login;
import android.app.AlertDialog;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.Log;

public class Preferences extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {
	Context mctx;
	private SharedPreferences userPref;
	private Editor prefEditFull;
	private ListPreference lst_pref_icns;
	private SharedPreferences sPref;
	private CheckBoxPreference hwbox;
	private CheckBoxPreference schDwnBox;
	private CheckBoxPreference matureChkBox;
	private BroadcastReceiver receiver;
	
protected class LoginListener extends BroadcastReceiver {
		
		private Preference clear_credentials;
		private SharedPreferences sPref;
		public LoginListener(Preference clear_credentials, SharedPreferences sPref){
			
			this.clear_credentials = clear_credentials;
			this.sPref = sPref;
		}
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("Aptoide","Settings received "+intent.getAction());
			if (intent.getAction().equals("pt.caixamagica.aptoide.LOGIN_ACTION")) {
				if(sPref.getString(Configs.LOGIN_USER_LOGIN, null)==null){
					clear_credentials.setEnabled(false);
				}else{
					clear_credentials.setEnabled(true);
				}
			}
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.settingspref);
		lst_pref_icns = (ListPreference) findPreference("icdown");
		mctx = this;
		userPref = getSharedPreferences("aptoide_prefs", MODE_PRIVATE);
		prefEditFull = userPref.edit();

		sPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

		Preference clear_credentials = (Preference) findPreference("clearcredentials");
		Preference set_credentials = (Preference) findPreference("setcredentials");
		Preference hwspecs = (Preference) findPreference("hwspecs");
		hwspecs.setIntent(new Intent(getBaseContext(), HWSpecActivity.class));
		Preference login = findPreference("setcredentials");
		login.setIntent(new Intent(this,Login.class));
		hwbox = (CheckBoxPreference) findPreference("hwspecsChkBox");
		schDwnBox = (CheckBoxPreference) findPreference("schDwnBox");
		matureChkBox = (CheckBoxPreference) findPreference("matureChkBox");
		matureChkBox.setChecked(userPref.getString("app_rating", "All").equals("All"));
		Log.d("Aptoide", "Broadcast registered");
		findPreference("clearcache").setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				deleteDirectory(getCacheDir());
				return false;
			}
		});
		
		findPreference("clearapk").setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				deleteDirectory(new File(Environment.getExternalStorageDirectory()+"/.aptoide/"));
				return false;
			}
		});

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

	private void updateSum() {
		String pref_str = sPref.getString("icdown", "error");
		String[] talk = getResources().getStringArray(R.array.dwnif);
		if (pref_str.equalsIgnoreCase("error")) {
			lst_pref_icns.setSummary("- - -");
		} else if (pref_str.equalsIgnoreCase("g3w")) {
			lst_pref_icns.setSummary(talk[0]);
		} else if (pref_str.equalsIgnoreCase("wo")) {
			lst_pref_icns.setSummary(talk[1]);
		} else if (pref_str.equalsIgnoreCase("nd")) {
			lst_pref_icns.setSummary(talk[2]);
		}
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equalsIgnoreCase("icdown")) {
			updateSum();
		} else if (key.equalsIgnoreCase("hwspecsChkBox")) {
			if (hwbox.isChecked()) {
				prefEditFull.putBoolean("hwspecsChkBox", true);
			} else {
				prefEditFull.putBoolean("hwspecsChkBox", false);
			}

		} else if (key.equalsIgnoreCase("schDwnBox")) {
			if (schDwnBox.isChecked()) {
				prefEditFull.putBoolean("schDwnBox", true);
			} else {
				prefEditFull.putBoolean("schDwnBox", false);
			}
		} else if (key.equalsIgnoreCase("matureChkBox")) {
			if (matureChkBox.isChecked()) {
				prefEditFull.putString("app_rating", "All");
			} else {
				prefEditFull.putString("app_rating", "Mature");
			}
		}
		Intent i = new Intent();
		i.putExtra("redraw",true);
		setResult(RESULT_OK, i);
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateSum();
	}
	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent intent) {
		
		super.onActivityResult(arg0, arg1, intent);
		if(intent.hasExtra("username")){
			
		}
		
	}
	
	@Override
	public void finish() {
		prefEditFull.putString("icdown", sPref.getString("icdown", "error"));
		prefEditFull.commit();
		super.finish();
	}
}
