/*
 * ManagerPreferences		auxilliary class to Aptoide's ServiceData
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

package cm.aptoide.pt.data.preferences;

import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;
import cm.aptoide.pt.data.ServiceData;
import cm.aptoide.pt.data.Statistics;
import cm.aptoide.pt.data.system.ScreenDimensions;

/**
 * ManagerPreferences, manages aptoide's preferences I/O
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class ManagerPreferences {
	
	private SharedPreferences getPreferences;
	private SharedPreferences.Editor setPreferences;
	

	public ManagerPreferences(ServiceData serviceData) {
		getPreferences = serviceData.getSharedPreferences("aptoide_preferences", Context.MODE_PRIVATE);
		setPreferences = getPreferences.edit();
		
		String aptoideClientUUID = getPreferences.getString("aptoideClientUUID", null);
		if(aptoideClientUUID == null){
			aptoideClientUUID = UUID.randomUUID().toString();
		}
	}

	
	public SharedPreferences getGetPreferences() {
		return getPreferences;
	}

	public SharedPreferences.Editor getSetPreferences() {
		return setPreferences;
	}

	
	public String getAptoideClientUUID(){
		return getPreferences.getString("aptoideClientUUID", null);
	}
	
	public void setScreenDimensions(ScreenDimensions screenDimensions){
		setPreferences.putInt("screenWidth", screenDimensions.getWidth());
		setPreferences.putInt("screenHeight", screenDimensions.getHeight());
	}
	
	public ScreenDimensions getScreenDimensions(){
		return new ScreenDimensions(getPreferences.getInt("screenWidth", 0), getPreferences.getInt("screenWidth", 0));
	}
	
	public void completeStatistics(Statistics statistics){
		statistics.completeStatistics(getAptoideClientUUID(), getScreenDimensions());
	}
	
}
