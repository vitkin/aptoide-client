/**
 * ViewMyapp,		auxiliary class to Aptoide's ServiceData
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

package cm.aptoide.pt.data.listeners;

import android.os.Parcel;
import android.os.Parcelable;
import cm.aptoide.pt.data.util.Constants;


 /**
 * ViewMyapp, models a myapp's App Info
 * 
 * @author dsilveira
 * @since 3.0
 *
 */
public class ViewMyapp implements Parcelable{

	private String name;
	private String pname;
	private String md5sum;
	private int size;
	private String remotePath;
	

	/**
	 * ViewMyapp Constructor
	 *
	 * @param String name
	 * @param String pname
	 * @param String md5sum
	 * @param int size
	 * @param String remotePath
	 */
	public ViewMyapp(String name, String pname, String md5sum, int size, String remotePath) {
		this.name = name;
		this.pname = pname;
		this.md5sum = md5sum;
		this.size = size;
		this.remotePath = remotePath;
	}
	
	/**
	 * ViewMyapp Constructor
	 *
	 * @param String name
	 */
	public ViewMyapp(String name){
		this.name = name;
	}
	

	public void setPackageName(String pname) {
		this.pname = pname;
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

	public String getName() {
		return this.name;
	}
	
	public String getPackageName() {
		return pname;
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
		this.name = null;
		this.pname = null;
		this.md5sum = null;
		this.size = Constants.EMPTY_INT;
	}

	/**
	 * ViewMyapp object reuse reConstructor
	 * 
	 * @param String name
	 * @param String pname
	 * @param String md5sum
	 * @param int size
	 * @param String remotePath
	 */
	public void reuse(String name, String pname, String md5sum, int size, String remotePath) {
		this.name = name;
		this.pname = pname;
		this.md5sum = md5sum;
		this.size = size;
		this.remotePath = remotePath;
	}
	
	/**
	 * ViewMyapp object reuse reConstructor
	 *
	 * @param String name
	 */
	public void reuse(String name){
		this.name = name;
	}
	

	@Override
	public String toString() {
		return "Name: "+name+" packageName: "+pname+" md5sum: "+md5sum+" size: "+size+" remotePath: "+remotePath;
	}
	
	
	
	// Parcelable stuff //
	
	
	public static final Parcelable.Creator<ViewMyapp> CREATOR = new
			Parcelable.Creator<ViewMyapp>() {

		@Override
		public ViewMyapp createFromParcel(Parcel in) {
			return new ViewMyapp(in);
		}

		public ViewMyapp[] newArray(int size) {
			return new ViewMyapp[size];
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

	private ViewMyapp(Parcel in){
		readFromParcel(in);
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(this.name);
		out.writeString(this.pname);
		out.writeString(this.md5sum);
		out.writeInt(this.size);
		out.writeString(this.remotePath);
	}

	public void readFromParcel(Parcel in) {
		this.name = in.readString();
		this.pname = in.readString();
		this.md5sum = in.readString();
		this.size = in.readInt();
		this.remotePath = in.readString();
	}
	
}
