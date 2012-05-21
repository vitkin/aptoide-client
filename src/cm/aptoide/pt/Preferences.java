package cm.aptoide.pt;

import cm.aptoide.pt.webservices.login.LoginDialog;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.Log;

public class Preferences extends PreferenceActivity {
	Context mctx;
	private SharedPreferences userPref;
	private Editor prefEditFull;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		
		addPreferencesFromResource(R.xml.settingspref);
		mctx=this;
		userPref = getSharedPreferences("aptoide_prefs", MODE_PRIVATE);
		prefEditFull = userPref.edit();
		
		Preference clear_credentials = (Preference)findPreference("clearcredentials");
		Preference set_credentials = (Preference)findPreference("setcredentials");
		
		if(userPref.getString(Configs.LOGIN_USER_NAME, null)==null){
			((Preference)findPreference("clearcredentials")).setEnabled(false);
		}
		
		clear_credentials.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			public boolean onPreferenceClick(Preference preference) {

				prefEditFull.remove( Configs.LOGIN_USER_NAME );
				prefEditFull.remove( Configs.LOGIN_PASSWORD );
				prefEditFull.remove( Configs.LOGIN_USER_ID );
				prefEditFull.commit();
				
				Log.d("Aptoide", "Login action broadcast sent");
				Intent loginAction = new Intent();
				loginAction.setAction("pt.caixamagica.aptoide.LOGIN_ACTION");
				Preferences.this.getApplicationContext().sendBroadcast(loginAction);
				
				final AlertDialog alrtClear = new AlertDialog.Builder(mctx).create();
				alrtClear.setTitle(mctx.getString(R.string.credentialscleared));
				alrtClear.setMessage(mctx.getString(R.string.credentialscleared_des));
				alrtClear.setButton("Ok", new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						alrtClear.dismiss();
					}
				});
				alrtClear.show();
				
				return true;
				
			}
		});
		
		set_credentials.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				LoginDialog loginComments = new LoginDialog(Preferences.this, LoginDialog.InvoqueNature.OVERRIDE_CREDENTIALS);
				loginComments.show();
				return true;	
			}
		});
		
	}
}
