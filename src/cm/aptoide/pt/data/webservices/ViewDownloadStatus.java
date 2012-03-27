/**
 * ViewDownloadStatus,		part of Aptoide's data model
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

package cm.aptoide.pt.data.webservices;

import cm.aptoide.pt.data.model.ViewRepository;
import cm.aptoide.pt.data.util.Constants;

 /**
 * ViewDownloadStatus, models a download's status
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class ViewDownloadStatus {

	private ViewRepository repo;
	private int offset;
	private EnumDownloadType downloadType;

	
	/**
	 * ViewIcon Constructor
	 *
	 * @param remotePath
	 * @param localPath
	 * @param appHashid
	 * @param downloadType
	 */
	public ViewDownloadStatus(ViewRepository repo, int offset, EnumDownloadType downloadType) {
		this.repo = repo;
		this.offset = offset;
		this.downloadType = downloadType;
	}
	

	public int getOffset() {
		return offset;
	}

	public void incrementOffset(int increment) {
		this.offset += increment;
	}

	public ViewRepository getRepository() {
		return repo;
	}

	public EnumDownloadType getDownloadType() {
		return downloadType;
	}



	/**
	 * ViewIcon object reuse, clean references
	 */
	public void clean(){
		this.repo = null;
		this.offset = Constants.EMPTY_INT;
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
	public void reuse(ViewRepository repo, int offset, EnumDownloadType downloadType) {
		this.repo = repo;
		this.offset = offset;
		this.downloadType = downloadType;
	}
	
	
}
