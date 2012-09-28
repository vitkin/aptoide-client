/*
 * ServiceManager, part of Aptoide
 * Copyright (C) 2012 Duarte Silveira
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
package cm.aptoide.pt2;

import java.util.ArrayList;
import java.util.HashMap;

import cm.aptoide.pt2.services.ServiceDownload;
import cm.aptoide.pt2.views.ViewCache;
import cm.aptoide.pt2.views.ViewDownload;
import cm.aptoide.pt2.views.ViewDownloadManagement;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.util.Log;
import android.net.NetworkInfo;
import android.os.IBinder;

/**
 * ServiceManager
 *
 * @author dsilveira
 *
 */
public class ApplicationServiceManager extends Application {

	private ServiceDownload serviceDownload;
	private ServiceConnection serviceConnection = new ServiceConnection() {
	    public void onServiceConnected(ComponentName className, IBinder serviceBinder) {
	        // This is called when the connection with the service has been
	        // established, giving us the service object we can use to
	        // interact with the service.  Because we have bound to a explicit
	        // service that we know is running in our own process, we can
	        // cast its IBinder to a concrete class and directly access it.
	        serviceDownload = ((ServiceDownload.ServiceDownloadBinder)serviceBinder).getService();

	        Log.d("Aptoide", "bound to ServiceDownload");
	    }
	    
	    public void onServiceDisconnected(ComponentName className) {
	        // This is called when the connection with the service has been
	        // unexpectedly disconnected -- that is, its process crashed.
	        // Because it is running in our same process, we should never
	        // see this happen.
	        serviceDownload = null;

	        Log.d("Aptoide", "unbound from ServiceDownload");
	    }

	};
	private ConnectivityManager connectivityState;

	ArrayList<ViewDownloadManagement> downloadManagementPool;
	ArrayList<ViewCache> cachePool;
	
	HashMap<Integer, ViewDownloadManagement> ongoingDownloads;
	
	@Override
	public void onCreate() {
		connectivityState = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		
		downloadManagementPool = new ArrayList<ViewDownloadManagement>();
		ongoingDownloads = new HashMap<Integer, ViewDownloadManagement>();
		
		bindService(new Intent(this, ServiceDownload.class), serviceConnection, Context.BIND_AUTO_CREATE);
		
		super.onCreate();
	}
	
	public boolean isConnectionAvailable(){
		boolean connectionAvailable = false;
		try {
			connectionAvailable = connectivityState.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED;
			Log.d("ManagerDownloads", "isConnectionAvailable mobile: "+connectionAvailable);	
		} catch (Exception e) { }
		try {
			connectionAvailable = connectionAvailable || connectivityState.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED;
			Log.d("ManagerDownloads", "isConnectionAvailable wifi: "+connectionAvailable);
		} catch (Exception e) { }
		try {
			connectionAvailable = connectionAvailable || connectivityState.getNetworkInfo(6).getState() == NetworkInfo.State.CONNECTED;
			Log.d("ManagerDownloads", "isConnectionAvailable wimax: "+connectionAvailable);
		} catch (Exception e) { }
		try {
			connectionAvailable = connectionAvailable || connectivityState.getNetworkInfo(9).getState() == NetworkInfo.State.CONNECTED;
			Log.d("ManagerDownloads", "isConnectionAvailable ethernet: "+connectionAvailable);
		} catch (Exception e) { }
		
		return connectionAvailable;
	}
	
//	public boolean isPermittedConnectionAvailable(ViewIconDownloadPermissions permissions){
//		boolean connectionAvailable = false;
//		if(permissions.isWiFi()){
//			try {
//				connectionAvailable = connectionAvailable || connectivityState.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED;
//				Log.d("ManagerDownloads", "isPermittedConnectionAvailable wifi: "+connectionAvailable);
//			} catch (Exception e) { }
//		} 
//		if(permissions.isWiMax()){
//			try {
//				connectionAvailable = connectionAvailable || connectivityState.getNetworkInfo(6).getState() == NetworkInfo.State.CONNECTED;
//				Log.d("ManagerDownloads", "isPermittedConnectionAvailable wimax: "+connectionAvailable);
//			} catch (Exception e) { }
//		} 
//		if(permissions.isMobile()){
//			try {
//				connectionAvailable = connectionAvailable || connectivityState.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED;
//				Log.d("ManagerDownloads", "isPermittedConnectionAvailable mobile: "+connectionAvailable);
//			} catch (Exception e) { }
//		}
//		if(permissions.isEthernet()){
//			try {
//				connectionAvailable = connectionAvailable || connectivityState.getNetworkInfo(9).getState() == NetworkInfo.State.CONNECTED;
//				Log.d("ManagerDownloads", "isPermittedConnectionAvailable ethernet: "+connectionAvailable);
//			} catch (Exception e) { }
//		}
//
//		Log.d("ManagerDownloads", "isPermittedConnectionAvailable: "+connectionAvailable+"  permissions: "+permissions);
//		return connectionAvailable;
//	}
	
	public void startDownload(ViewDownloadManagement viewDownload){
		ongoingDownloads.put(viewDownload.hashCode(), viewDownload);
		if(viewDownload.isLoginRequired()){
			serviceDownload.downloadApk(viewDownload.getDownload(), viewDownload.getCache(), viewDownload.getLogin());
		}else{
			serviceDownload.downloadApk(viewDownload.getDownload(), viewDownload.getCache());
		}
	}
	
}
