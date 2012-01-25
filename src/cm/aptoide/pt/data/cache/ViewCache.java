/**
 * ViewCache,		auxilliary class to Aptoide's ServiceData
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

import java.io.File;


 /**
 * ViewCache, models a cache file
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class ViewCache {

	private String localPath;
	private boolean hasMd5Sum;
	private String md5sum;
	

	public ViewCache(String localPath) {
		this.localPath = localPath;
		this.hasMd5Sum = false;
	}
	
	public ViewCache(String localPath, String md5sum) {
		this(localPath);
		this.hasMd5Sum = true;
		this.md5sum = md5sum;
	}


	public String getLocalPath() {
		return localPath;
	}
	
	public File getFile(){
		return new File(localPath);
	}
	
	public boolean hasMd5Sum(){
		return hasMd5Sum;
	}
	
	public void setMd5Sum(String md5sum){
		this.md5sum = md5sum;
	}

	public String getMd5sum() {
		return md5sum;
	}
	
	@Override
	public String toString() {
		return "ViewCache:  localPath: "+localPath+" md5sum: "+md5sum;
	}

	public void clean(){
		this.localPath = null;
		this.hasMd5Sum = false;
		this.md5sum = null;
	}
	
	public void reuse(String localPath) {
		this.localPath = localPath;
		this.hasMd5Sum = false;
	}
	
	public void reuse(String localPath, String md5sum) {
		reuse(localPath);
		this.hasMd5Sum = true;
		this.md5sum = md5sum;
	}
	
}
