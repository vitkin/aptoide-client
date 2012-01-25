/**
 * ViewDownloadInfo,		part of Aptoide's data model
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

package cm.aptoide.pt.data.downloads;

import cm.aptoide.pt.data.Constants;

 /**
 * ViewDownloadInfo, models a download's info
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class ViewDownloadInfo {

	private String remotePath;
	private String appName;
	private int appHashid;
	private EnumDownloadType downloadType;

	
	/**
	 * ViewIcon Constructor
	 *
	 * @param remotePath
	 * @param localPath
	 * @param appHashid
	 * @param downloadType
	 */
	public ViewDownloadInfo(String remotePath, String appName, int appHashid, EnumDownloadType downloadType) {
		this.remotePath = remotePath;
		this.appName = appName;
		this.appHashid = appHashid;
		this.downloadType = downloadType;
	}
	

	public String getRemotePath() {
		return remotePath;
	}

	public String getAppName() {
		return appName;
	}

	public int getAppHashid() {
		return appHashid;
	}

	public EnumDownloadType getDownloadType() {
		return downloadType;
	}


	@Override
	public String toString() {
		return "ViewDownloadInfo: "
				+" remotePath: "+remotePath
				+" appName: "+appName
				+" appHashid: "+appHashid
				+" downloadType: "+downloadType;
	}


	/**
	 * ViewIcon object reuse, clean references
	 */
	public void clean(){
		this.remotePath = null;
		this.appName = null;
		this.appHashid = Constants.EMPTY_INT;
		this.downloadType = null;
	}

	/**
	 * ViewIcon object reuse, reConstructor
	 *  
	 * @param remotePath
	 * @param localPath
	 * @param applicationHashid
	 * @param downloadType
	 */
	public void reuse(String remotePath, String appName, int applicationHashid, EnumDownloadType downloadType) {
		this.remotePath = remotePath;
		this.appName = appName;
		this.appHashid = applicationHashid;
		this.downloadType = downloadType;
	}
	
	
}
