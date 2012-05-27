package cm.aptoide.pt;

import cm.aptoide.pt.webservices.login.LoginDialog;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
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
		hwbox = (CheckBoxPreference) findPreference("hwspecsChkBox");
		schDwnBox = (CheckBoxPreference) findPreference("schDwnBox");
		matureChkBox = (CheckBoxPreference) findPreference("matureChkBox");

		if (userPref.getString(Configs.LOGIN_USER_NAME, null) == null) {
			((Preference) findPreference("clearcredentials")).setEnabled(false);
		}

		clear_credentials
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					public boolean onPreferenceClick(Preference preference) {

						prefEditFull.remove(Configs.LOGIN_USER_NAME);
						prefEditFull.remove("userName");
						prefEditFull.remove(Configs.LOGIN_PASSWORD);
						prefEditFull.remove(Configs.LOGIN_USER_ID);
						prefEditFull.commit();

						Log.d("Aptoide", "Login action broadcast sent");
						Intent loginAction = new Intent();
						loginAction
								.setAction("pt.caixamagica.aptoide.LOGIN_ACTION");
						Preferences.this.getApplicationContext().sendBroadcast(
								loginAction);

						final AlertDialog alrtClear = new AlertDialog.Builder(
								mctx).create();
						alrtClear.setTitle(mctx
								.getString(R.string.credentialscleared));
						alrtClear.setMessage(mctx
								.getString(R.string.credentialscleared_des));
						alrtClear.setButton("Ok", new OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								alrtClear.dismiss();
							}
						});
						alrtClear.show();

						return true;

					}
				});

		set_credentials
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {
					public boolean onPreferenceClick(Preference preference) {
						LoginDialog loginComments = new LoginDialog(
								Preferences.this,
								LoginDialog.InvoqueNature.OVERRIDE_CREDENTIALS);
						loginComments.show();
						return true;
					}
				});
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);

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
	public void finish() {
		prefEditFull.putString("icdown", sPref.getString("icdown", "error"));
		prefEditFull.commit();
		super.finish();
	}
}
