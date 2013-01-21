/*
 * ApplicationAptoide, part of Aptoide
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
package cm.aptoide.pt;

import cm.aptoide.pt.preferences.ManagerPreferences;
import cm.aptoide.pt.services.ServiceDownloadManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

/**
 * ApplicationAptoide, centralizes, statically, calls to instantiated objects
 *
 * @author dsilveira
 *
 */
public class ApplicationAptoide extends Application {

	private ManagerPreferences managerPreferences;
	private static Context context;
	
	@Override
	public void onCreate() {
		managerPreferences = new ManagerPreferences(getApplicationContext());
		setContext(getApplicationContext());
		super.onCreate();
	}
	
	public ManagerPreferences getManagerPreferences(){
		return managerPreferences;
	}

	/**
	 * @return the context
	 */
	public static Context getContext() {
		return context;
	}

	/**
	 * @param context the context to set
	 */
	public static void setContext(Context context) {
		ApplicationAptoide.context = context;
	}
}
