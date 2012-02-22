/**
 * ViewLatestVersionInfo,		auxiliary class to Aptoide's ServiceData
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

package cm.aptoide.pt.data.xml;

import android.os.Parcel;
import android.os.Parcelable;
import cm.aptoide.pt.data.util.Constants;


 /**
 * ViewLatestVersionInfo, models a LatestVersion's Info
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class ViewLatestVersionInfo implements Parcelable{

	private int versionCode;
	private String md5sum;
	private int size;
	private String remotePath;
	

	/**
	 * ViewMyapp Constructor
	 *
	 * @param int versionCode
	 * @param String md5sum
	 * @param int size
	 * @param String remotePath
	 */
	public ViewLatestVersionInfo(int versionCode, String md5sum, int size, String remotePath) {
		this.versionCode = versionCode;
		this.md5sum = md5sum;
		this.size = size;
		this.remotePath = remotePath;
	}
	
	/**
	 * ViewMyapp Constructor
	 *
	 * @param String name
	 */
	public ViewLatestVersionInfo(int versionCode){
		this.versionCode = versionCode;
		this.size = Constants.EMPTY_INT;
	}
	

	public void setMd5sum(String md5sum) {
		this.md5sum = md5sum;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public void setRemotePath(String remotePath) {
		this.remotePath = remotePath;
	}

	public int getVersionCode() {
		return this.versionCode;
	}

	public String getMd5sum() {
		return this.md5sum;
	}
	
	public int getSize(){
		return this.size;
	}
	
	public String getRemotePath(){
		return this.remotePath;
	}
	

	/**
	 * ViewMyapp object reuse clean references
	 */
	public void clean(){
		this.versionCode = Constants.EMPTY_INT;
		this.md5sum = null;
		this.size = Constants.EMPTY_INT;
	}

	/**
	 * ViewMyapp object reuse reConstructor
	 * 
	 * @param int versionCode
	 * @param String md5sum
	 * @param int size
	 * @param String remotePath
	 */
	public void reuse(int versionCode, String md5sum, int size, String remotePath) {
		this.versionCode = versionCode;
		this.md5sum = md5sum;
		this.size = size;
		this.remotePath = remotePath;
	}
	
	/**
	 * ViewMyapp object reuse reConstructor
	 *
	 * @param String name
	 */
	public void reuse(int versionCode){
		this.versionCode = versionCode;
		this.size = Constants.EMPTY_INT;
	}
	

	@Override
	public String toString() {
		return "VersionCode: "+versionCode+" md5sum: "+md5sum+" size: "+size+" remotePath: "+remotePath;
	}
	
	
	
	// Parcelable stuff //
	
	
	public static final Parcelable.Creator<ViewLatestVersionInfo> CREATOR = new
			Parcelable.Creator<ViewLatestVersionInfo>() {

		@Override
		public ViewLatestVersionInfo createFromParcel(Parcel in) {
			return new ViewLatestVersionInfo(in);
		}

		public ViewLatestVersionInfo[] newArray(int size) {
			return new ViewLatestVersionInfo[size];
		}
	};

	/** 
	 * we're annoyingly forced to create this even if we clearly don't need it,
	 *  so we just use the default return 0
	 *  
	 *  @return 0
	 */
	@Override
	public int describeContents() {
		return 0;
	}

	private ViewLatestVersionInfo(Parcel in){
		readFromParcel(in);
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(this.versionCode);
		out.writeString(this.md5sum);
		out.writeInt(this.size);
		out.writeString(this.remotePath);
	}

	public void readFromParcel(Parcel in) {
		this.versionCode = in.readInt();
		this.md5sum = in.readString();
		this.size = in.readInt();
		this.remotePath = in.readString();
	}
	
}
