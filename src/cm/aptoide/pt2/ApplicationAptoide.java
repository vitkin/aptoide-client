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
package cm.aptoide.pt2;

import cm.aptoide.pt2.preferences.ManagerPreferences;
import cm.aptoide.pt2.services.ServiceDownloadManager;
import android.app.Application;
import android.content.Intent;

/**
 * ApplicationAptoide, centralizes, statically, calls to instantiated objects
 *
 * @author dsilveira
 *
 */
public class ApplicationAptoide extends Application {

	private ManagerPreferences managerPreferences;
	
	@Override
	public void onCreate() {
		managerPreferences = new ManagerPreferences(getApplicationContext());
		startService(new Intent(this, ServiceDownloadManager.class));
		super.onCreate();
	}
	
	public ManagerPreferences getManagerPreferences(){
		return managerPreferences;
	}
}
