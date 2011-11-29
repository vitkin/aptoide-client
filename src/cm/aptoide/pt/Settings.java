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

import cm.aptoide.pt.webservices.login.LoginDialog;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.Log;

public class Settings extends PreferenceActivity implements OnSharedPreferenceChangeListener{
	
	private SharedPreferences sPref;
	// private SharedPreferences.Editor prefEdit;
	private SharedPreferences sPrefFull;
	private SharedPreferences.Editor prefEditFull;
	
	private Intent rtrn = new Intent();
	
	private ProgressDialog pd = null;
	private Context mctx = null;
	
	ListPreference lst_pref_icns = null;
	
	private Preference clear_cache = null;
	private CheckBoxPreference hwbox;
	private CheckBoxPreference schDwnBox;	
	private boolean changed = false;
	
	private DownloadQueueService downloadQueueService;
	
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
				if(sPref.getString(Configs.LOGIN_USER_NAME, null)==null){
					clear_credentials.setEnabled(false);
				}else{
					clear_credentials.setEnabled(true);
				}
			}
		}
	}
	
	
	private ServiceConnection serviceConnection = new ServiceConnection() {
	    public void onServiceConnected(ComponentName className, IBinder serviceBinder) {
	        // This is called when the connection with the service has been
	        // established, giving us the service object we can use to
	        // interact with the service.  Because we have bound to a explicit
	        // service that we know is running in our own process, we can
	        // cast its IBinder to a concrete class and directly access it.
	        downloadQueueService = ((DownloadQueueService.DownloadQueueBinder)serviceBinder).getService();

	        Log.d("Aptoide-Settings", "DownloadQueueService bound to Settings");
	        
	        downloadQueueService.dismissAllNotifications();
	    }
	    
	    public void onServiceDisconnected(ComponentName className) {
	        // This is called when the connection with the service has been
	        // unexpectedly disconnected -- that is, its process crashed.
	        // Because it is running in our same process, we should never
	        // see this happen.
	        downloadQueueService = null;
	        
	        Log.d("Aptoide-Settings","DownloadQueueService unbound from Settings");
	    }

	};
	BroadcastReceiver receiver;
	private boolean changed2 =false;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.settingspref);
		
		mctx = this;
		
		lst_pref_icns = (ListPreference) findPreference("icdown");
		
		sPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		SharedPreferences userPref = this.getApplicationContext().getSharedPreferences("aptoide_prefs", Context.MODE_PRIVATE);
		sPrefFull = getSharedPreferences("aptoide_prefs", MODE_PRIVATE);
		prefEditFull = sPrefFull.edit();

		/*String pref_str = sPref.getString("icdown", "error");
		Log.d("Aptoide","I got what?: " + pref_str);
		if(pref_str.equalsIgnoreCase("error")){
			prefEdit = sPref.edit();
			prefEdit.putString("icdown", sPrefFull.getString("icdown", "g3w"));
			prefEdit.commit();
		}*/
		
		
		Log.d("Aptoide","The preference is: " + sPref.getString("icdown", "error"));
		Log.d("Aptoide","The preference is: " + sPref.getBoolean("hwspecsChkBox", false));
		Log.d("Aptoide","The preference is: " + sPref.getBoolean("schDwnBox", false));
		Log.d("Aptoide","The preference is: " + sPrefFull.getString("icdown", "error"));
		Log.d("Aptoide","The preference is: " + sPrefFull.getBoolean("hwspecsChkBox", false));
		Log.d("Aptoide","The preference is: " + sPrefFull.getBoolean("schDwnBox", false));
		
		lst_pref_icns = (ListPreference) findPreference("icdown");
		Preference clear_credentials = (Preference)findPreference("clearcredentials");
		Preference set_credentials = (Preference)findPreference("setcredentials");
		Preference hwspecs = (Preference)findPreference("hwspecs");
		hwspecs.setIntent(new Intent(getBaseContext(),HWSpecActivity.class));
		hwbox = (CheckBoxPreference) findPreference("hwspecsChkBox");
		schDwnBox = (CheckBoxPreference) findPreference("schDwnBox");
		
		
		
		if((Configs.COMMENTS_ADD_ON && Configs.COMMENTS_ON) || (Configs.TASTE_ADD_ON && Configs.TASTE_ON)){
			
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
					Settings.this.getApplicationContext().sendBroadcast(loginAction);
					
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
					LoginDialog loginComments = new LoginDialog(Settings.this, LoginDialog.InvoqueNature.OVERRIDE_CREDENTIALS);
					loginComments.show();
					return true;	
				}
			});
			
			Log.d("Aptoide", "Broadcast registered");
			receiver = new LoginListener(clear_credentials, userPref);
			IntentFilter filter = new IntentFilter("pt.caixamagica.aptoide.LOGIN_ACTION");
			registerReceiver(receiver, filter);
			
		}else{
			clear_credentials.setEnabled(false);
			set_credentials.setEnabled(false);
		}
		
		
		clear_cache = (Preference)findPreference("clearcache");
		clear_cache.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			public boolean onPreferenceClick(Preference preference) {
				
				final AlertDialog alrt = new AlertDialog.Builder(mctx).create();
				alrt.setTitle("Caution");
				alrt.setMessage("Do you wish to empty Aptoide cache?");
				alrt.setButton("yes", new OnClickListener() {
	
					public void onClick(DialogInterface dialog, int which) {
						getApplicationContext().bindService(new Intent(getApplicationContext(), DownloadQueueService.class), serviceConnection, Context.BIND_AUTO_CREATE);
						alrt.dismiss();

						boolean isSrvAlive = false;
						Log.d("Aptoide","... WHAT!!!!!");
						ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
						for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
							Log.d("Aptoide","... " + service.service.getClassName());
							if ("cm.aptoide.pt.FetchIconsService".equals(service.service.getClassName())) {
								isSrvAlive = true;
								Log.d("Aptoide","ALALALALALALAL");
								break;
							}
						}
						
						if(isSrvAlive){
							final AlertDialog alrt2 = new AlertDialog.Builder(mctx).create();
							alrt2.setTitle("Error");
							alrt2.setMessage("Aptoide icon service is still running.\nPlease try again latter.");
							alrt2.setButton("Ok", new OnClickListener() {
								
								public void onClick(DialogInterface dialog, int which) {
									alrt2.dismiss();
									
								}
							});
							alrt2.show();
						}else{
							pd = ProgressDialog.show(mctx, getText(R.string.top_please_wait), getText(R.string.settings_cache), true);
							pd.setIcon(android.R.drawable.ic_dialog_info);

							new Thread() {
								public void run() {
									try{
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
									}catch (Exception e){ }
									finally{
										done_handler.sendEmptyMessage(0);
									}
								}
							}.start(); 
						}
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
		
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
		
		
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		updateSum();
		
	}
	
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,	String key) {
		if(key.equalsIgnoreCase("icdown")){
			updateSum();
		}else if(key.equalsIgnoreCase("hwspecsChkBox")){
			Log.d("a","a");
			if(hwbox.isChecked()){
				prefEditFull.putBoolean("hwspecsChkBox", true);
				changed2 =true;
			}else{
				prefEditFull.putBoolean("hwspecsChkBox", false);
				changed2 =true;
			}
			
			
		}else if(key.equalsIgnoreCase("schDwnBox")){
			if(schDwnBox.isChecked()){
				prefEditFull.putBoolean("schDwnBox", true);
				changed = true;
			}else{
				prefEditFull.putBoolean("schDwnBox", false);
				
			}
		}
	}
	
	private void updateSum(){
		String pref_str = sPref.getString("icdown", "error");
		String[] talk = getResources().getStringArray(R.array.dwnif);
		if(pref_str.equalsIgnoreCase("error")){
			lst_pref_icns.setSummary("- - -");
		}else if(pref_str.equalsIgnoreCase("g3w")){
			lst_pref_icns.setSummary(talk[0]);
		}else if(pref_str.equalsIgnoreCase("wo")){
			lst_pref_icns.setSummary(talk[1]);
		}else if(pref_str.equalsIgnoreCase("nd")){
			lst_pref_icns.setSummary(talk[2]);
		}
	}


	@Override
	public void finish() {
		prefEditFull.putString("icdown", sPref.getString("icdown", "error"));
		
		prefEditFull.commit();
		
		if(sPref.getString("icdown", "error").equalsIgnoreCase("nd")){
			Intent serv = new Intent(mctx,FetchIconsService.class);
			mctx.stopService(serv);
		}
		if (sPref.getBoolean("schDwnBox", true)&&changed) {
			Intent i = new Intent(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
			ConnectivityManager netstate = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			if (netstate.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() 
					== NetworkInfo.State.CONNECTED)
				i.putExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED, true);
			sendBroadcast(i);
		}
		//rtrn.putExtra("settings", 0);
		if(changed2){
			Intent i = new Intent("pt.caixamagica.aptoide.FILTER_CHANGED");
			sendBroadcast(i);
		}
		unregisterReceiver(receiver);
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
