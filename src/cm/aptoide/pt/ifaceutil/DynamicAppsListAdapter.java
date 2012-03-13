/**
 * DynamicAppsListAdapter,		part of Aptoide's data model
 * Copyright (C) 2011  Duarte Silveira
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

package cm.aptoide.pt.ifaceutil;

import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import cm.aptoide.pt.Aptoide;
import cm.aptoide.pt.EnumAppsSorting;
import cm.aptoide.pt.data.AIDLAptoideServiceData;
import cm.aptoide.pt.data.display.ViewDisplayApplication;
import cm.aptoide.pt.data.system.ViewScreenDimensions;
import cm.aptoide.pt.debug.AptoideLog;

 /**
 * DynamicAppsListAdapter, models a dynamic loading apps list adapter
 * 							extends arrayAdapter
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class DynamicAppsListAdapter extends ArrayAdapter<ViewDisplayApplication>{

	private EnumAppsSorting appsSortingPolicy = null;
	
	private AIDLAptoideServiceData serviceDataCaller = null;

	private boolean serviceDataSeenRunning = false;
	private boolean serviceDataIsBound = false;

	
	private ServiceConnection serviceDataConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			// This is called when the connection with the service has been
			// established, giving us the object we can use to
			// interact with the service.  We are communicating with the
			// service using AIDL, so here we set the remote service interface.
			serviceDataCaller = AIDLAptoideServiceData.Stub.asInterface(service);
			serviceDataIsBound = true;
			
			Log.v("Aptoide-DynamicAppsListAdapter", "Connected to ServiceData");

			
	        
	        try {
	        	Log.v("Aptoide-DynamicAppsListAdapter", "Called for getting apps sorting policy");
	            appsSortingPolicy = EnumAppsSorting.reverseOrdinal(serviceDataCaller.callGetAppsSortingPolicy());	        	
	        	
	        } catch (RemoteException e) {
				// TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        
		}

		public void onServiceDisconnected(ComponentName className) {
			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.
			serviceDataCaller = null;
			serviceDataIsBound = false;
			
			Log.v("Aptoide-DynamicAppsListAdapter", "Disconnected from ServiceData");
		}
	};
	
	
	/**
	 * DynamicAppsListAdapter Constructor
	 *
	 * @param context
	 * @param textViewResourceId
	 * @param objects
	 */
	public DynamicAppsListAdapter(Context context, int textViewResourceId, List<ViewDisplayApplication> objects) {
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
	} 

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return super.getView(position, convertView, parent);
	}
	
}
