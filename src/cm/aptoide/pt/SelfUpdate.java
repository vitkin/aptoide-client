/**
 * SelfUpdate, part of Aptoide
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
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import cm.aptoide.pt.data.AIDLAptoideServiceData;
import cm.aptoide.pt.data.AptoideServiceData;


/**
 * SelfUpdate, handles Aptoide's self-Update interface
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class SelfUpdate extends Activity {
	
	AlertDialog selfUpdate;
	
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
			
			Log.v("Aptoide-SelfUpdate", "Connected to ServiceData");
	        
			try {
				serviceDataCaller.callRegisterSelfUpdateObserver(serviceDataCallback);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			handleSelfUpdate();
			
		}

		public void onServiceDisconnected(ComponentName className) {
			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.
			serviceDataIsBound = false;
			serviceDataCaller = null;
			
			Log.v("Aptoide-SelfUpdate", "Disconnected from ServiceData");
		}
	};
	
	private AIDLSelfUpdate.Stub serviceDataCallback = new AIDLSelfUpdate.Stub() {
		
		@Override
		public void cancelUpdateActivity() throws RemoteException {
			finish();
		}
	};
	
	
	private void handleSelfUpdate(){
		final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress);
    	AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
    	alertBuilder.setCancelable(false)
    				.setPositiveButton(R.string.yes , new DialogInterface.OnClickListener() {
    					public void onClick(DialogInterface dialog, int id) {
    						dialog.cancel();
    						progressBar.setVisibility(View.VISIBLE);
    						try {
								serviceDataCaller.callAcceptSelfUpdate();
							} catch (RemoteException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
    					}
    				})    	
    				.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
    					public void onClick(DialogInterface dialog, int id) {
    						dialog.cancel();
    						try {
								serviceDataCaller.callRejectSelfUpdate();
							} catch (RemoteException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
    						finish();
    					}
    				})
    				.setMessage(R.string.update_confirm)
    				;
    	
    	selfUpdate = alertBuilder.create();
    	
    	selfUpdate.setTitle(R.string.update_available);
    	selfUpdate.setIcon(R.drawable.icon);
    	
    	selfUpdate.show();
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.auto_updating);
    	
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
	public void finish() {	//TODO fix  java.lang.IllegalArgumentException: Service not registered: cm.aptoide.pt.SelfUpdate$1@405636c8 when unbinding
//		if(serviceDataIsBound){
//			unbindService(serviceDataConnection);
//		}
		super.finish();
	}
	
}
