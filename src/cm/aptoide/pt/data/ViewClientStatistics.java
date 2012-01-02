/**
 * Clientstatistics,		auxiliary class to Aptoide's ServiceData
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
package cm.aptoide.pt.data;

import cm.aptoide.pt.data.system.ViewScreenDimensions;

/**
 * Clientstatistics, models stored and observed statistics
 *
 * @author dsilveira
 * @since 3.0
 *
 */
public class ViewClientStatistics {

	private String aptoideVersionNameInUse;
	private String aptoideClientUUID;
	
	private ViewScreenDimensions screenDimensions;

	public ViewClientStatistics(String aptoideVersionNameInUse) {
		this.aptoideVersionNameInUse = aptoideVersionNameInUse;
	}
	
	public ViewClientStatistics(String aptoideVersionNameInUse, String aptoideClientUUID, int screenWidth, int screenHeight) {
		this.aptoideVersionNameInUse = aptoideVersionNameInUse;
		this.aptoideClientUUID = aptoideClientUUID;
		this.screenDimensions = new ViewScreenDimensions(screenWidth, screenHeight);
	}	
	
	public ViewClientStatistics(String aptoideVersionNameInUse, String aptoideClientUUID, ViewScreenDimensions screenDimensions) {
		this.aptoideVersionNameInUse = aptoideVersionNameInUse;
		this.aptoideClientUUID = aptoideClientUUID;
		this.screenDimensions = screenDimensions;
	}

	public void completeStatistics(String aptoideClientUUID, ViewScreenDimensions screenDimensions){
		this.aptoideClientUUID = aptoideClientUUID;
		this.screenDimensions = screenDimensions;
	}
	
	public String getAptoideVersionNameInUse() {
		return aptoideVersionNameInUse;
	}

	public String getAptoideClientUUID() {
		return aptoideClientUUID;
	}

	public ViewScreenDimensions getScreenDimensions() {
		return screenDimensions;
	}
	
}
