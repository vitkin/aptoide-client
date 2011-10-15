/*
 * Cache		auxilliary class to Aptoide's ServiceData
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

package cm.aptoide.pt.data.cache;


 /**
 * Cache, models a cache file
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class Cache {

	private String localPath;
	private String md5sum;
	
	
	public Cache(String localPath, String md5sum) {
		this.localPath = localPath;
		this.md5sum = md5sum;
	}


	public String getLocalPath() {
		return localPath;
	}

	public String getMd5sum() {
		return md5sum;
	}
	
	
	public void clean(){
		this.localPath = null;
		this.md5sum = null;
	}
	
	public void reuse(String localPath, String md5sum) {
		this.localPath = localPath;
		this.md5sum = md5sum;
	}
	
}
