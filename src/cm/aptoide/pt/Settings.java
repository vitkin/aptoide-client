/**
 * Settings, part of Aptoide
 * Copyright (C) 2011 Duarte Silveira
 * duarte.silveira@caixamagica.pt
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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import cm.aptoide.pt.data.AIDLAptoideServiceData;
import cm.aptoide.pt.data.AptoideServiceData;
import cm.aptoide.pt.data.downloads.ViewIconDownloadPermissions;
import cm.aptoide.pt.data.preferences.ViewSettings;
import cm.aptoide.pt.data.system.ViewHwFilters;


/**
 * Settings, handles Aptoide's settings interface
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class Settings extends Activity {
	
	ViewSettings storedSettings;
	ViewHwFilters hwFilters;
	ViewIconDownloadPermissions iconDownloadPermissions;
	
	CheckBox hwFilter;
	
	private AIDLAptoideServiceData serviceDataCaller = null;

	private boolean serviceDataIsBound = false;

	private ServiceConnection serviceDataConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			// This is called when the connection with the service has been
			// established, giving us the object we can use to
			// interact with the service.  We are communicating with the
			// service using AIDL, so here we set the remote service interface.
			serviceDataCaller = AIDLAptoideServiceData.Stub.asInterface(service);
			serviceDataIsBound = true;
			
			Log.v("Aptoide-Settings", "Connected to ServiceData");
	        
			try {
				storedSettings = serviceDataCaller.callGetSettings();
				hwFilters = serviceDataCaller.callGetHwFilters();
				iconDownloadPermissions = serviceDataCaller.callGetIconDownloadPermissions();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			showSettings();
			
		}

		public void onServiceDisconnected(ComponentName className) {
			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.
			serviceDataIsBound = false;
			serviceDataCaller = null;
			
			Log.v("Aptoide-Settings", "Disconnected from ServiceData");
		}
	};
	
	
	private void showSettings(){
		setContentView(R.layout.settings);
		
		TextView iconDownloadPermissions = (TextView) findViewById(R.id.icon_download_permissions);
		iconDownloadPermissions.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final ViewIconDownloadPermissions storedPermissions = Settings.this.iconDownloadPermissions;
				Log.d("Aptoide-Settings", "clicked icon download permissions");
				View iconDownloadView = LinearLayout.inflate(Settings.this, R.layout.dialog_icon_download_permissions, null);
				Builder dialogBuilder = new AlertDialog.Builder(Settings.this).setView(iconDownloadView);
				final AlertDialog iconDownloadDialog = dialogBuilder.create();
				iconDownloadDialog.setIcon(android.R.drawable.ic_menu_manage);
				iconDownloadDialog.setTitle(getString(R.string.download_icons));
				
				final RadioButton wifi = (RadioButton) iconDownloadView.findViewById(R.id.wifi);
				if(storedPermissions.isWiFi()){
					wifi.setChecked(true);
				}else{
					wifi.setChecked(false);
				}
				final RadioButton ethernet = (RadioButton) iconDownloadView.findViewById(R.id.ethernet);
				if(storedPermissions.isEthernet()){
					ethernet.setChecked(true);
				}else{
					ethernet.setChecked(false);
				}
				final RadioButton _4g_ = (RadioButton) iconDownloadView.findViewById(R.id._4g_);
				if(storedPermissions.is4G()){
					_4g_.setChecked(true);
				}else{
					_4g_.setChecked(false);
				}
				final RadioButton _3g_ = (RadioButton) iconDownloadView.findViewById(R.id._3g_);
				if(storedPermissions.is3G()){
					_3g_.setChecked(true);
				}else{
					_3g_.setChecked(false);
				}
				final RadioButton _2g_ = (RadioButton) iconDownloadView.findViewById(R.id._2g_);
				if(storedPermissions.is2G()){
					_2g_.setChecked(true);
				}else{
					_2g_.setChecked(false);
				}
				final RadioButton never = (RadioButton) iconDownloadView.findViewById(R.id.never);
				if(storedPermissions.isNever()){
					never.setChecked(true);
				}else{
					never.setChecked(false);
				}
				never.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if(isChecked){
							wifi.setChecked(false);
							ethernet.setChecked(false);
							_4g_.setChecked(false);
							_3g_.setChecked(false);
							_2g_.setChecked(false);
						}else{
							wifi.setChecked(true);
							ethernet.setChecked(true);
							_4g_.setChecked(true);
							_3g_.setChecked(true);
							_2g_.setChecked(true);
						}
					}
				});
				final Button done = (Button) iconDownloadView.findViewById(R.id.done);
				done.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						ViewIconDownloadPermissions newPermissions 
							= new ViewIconDownloadPermissions( wifi.isChecked(), ethernet.isChecked(), _4g_.isChecked(), _3g_.isChecked(), _2g_.isChecked() );
						if(!newPermissions.equals(storedPermissions)){
							try {
								serviceDataCaller.callSetIconDownloadPermissions(newPermissions);
							} catch (RemoteException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						iconDownloadDialog.dismiss();
					}
				});
				
				final Button cancel = (Button) iconDownloadView.findViewById(R.id.cancel);
				cancel.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						iconDownloadDialog.dismiss();
					}
				});
				
				iconDownloadDialog.show();
				
			}
		});
		
		TextView hwSpecs = (TextView) findViewById(R.id.hw_specs);
		hwSpecs.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d("Aptoide-Settings", "clicked hw specs: "+hwFilters);
				View hwSpecsView = LinearLayout.inflate(Settings.this, R.layout.dialog_hw_specs, null);
				Builder dialogBuilder = new AlertDialog.Builder(Settings.this).setView(hwSpecsView);
				final AlertDialog hwSpecsDialog = dialogBuilder.create();
				hwSpecsDialog.setIcon(android.R.drawable.ic_menu_info_details);
				hwSpecsDialog.setTitle(getString(R.string.hw_specs));
				
				final TextView sdk = (TextView) hwSpecsView.findViewById(R.id.sdk);
				sdk.setText(Integer.toString(hwFilters.getSdkVersion()));
				final TextView screen = (TextView) hwSpecsView.findViewById(R.id.screen);
				screen.setText(Integer.toString(hwFilters.getScreenSize()));
				final TextView gles = (TextView) hwSpecsView.findViewById(R.id.gles);
				gles.setText(Float.toString(hwFilters.getGlEsVersion()));
				
				hwSpecsDialog.setButton(getString(R.string.back), new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						hwSpecsDialog.dismiss();
					}
				});
				
				hwSpecsDialog.show();
			}
		});
		
		hwFilter = (CheckBox) findViewById(R.id.hw_filter);
		hwFilter.setChecked(storedSettings.isHwFilterOn());
		hwFilter.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Log.d("Aptoide-Settings", "checkbox: "+isChecked+" storedValue: "+storedSettings.isHwFilterOn());
				if(isChecked != storedSettings.isHwFilterOn()){
					try {
						serviceDataCaller.callSetHwFilter(isChecked);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
    	
		if(!serviceDataIsBound){
    		bindService(new Intent(this, AptoideServiceData.class), serviceDataConnection, Context.BIND_AUTO_CREATE);
    	}
		
		super.onCreate(savedInstanceState);
	}

	
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK ) {
//			Log.d("Aptoide-SelfUpdate", "");
//			//TODO cancel download
//			return true;
//		}
//		return super.onKeyDown(keyCode, event);
//	}
	
	@Override
	public void finish() {
		if(hwFilter.isChecked() != storedSettings.isHwFilterOn()){
			try {
				serviceDataCaller.callResetAvailableApps();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(serviceDataIsBound){
			unbindService(serviceDataConnection);
		}
		super.finish();
	}
	
}
