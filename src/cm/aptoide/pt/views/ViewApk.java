/*******************************************************************************
 * Copyright (c) 2012 rmateus.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package cm.aptoide.pt.views;

import java.util.ArrayList;

import cm.aptoide.pt.annotations.DatabaseField;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class ViewApk implements Parcelable {

	private long id;
	@DatabaseField
	private String apkid = "";
	@DatabaseField
	private String name = apkid;
	@DatabaseField
	private int vercode = 0;
	@DatabaseField
	private String vername = "Unversioned";
	@DatabaseField
	private String size = "0";
	@DatabaseField
	private String downloads = "0";
	private String category1 = "Other";
	@DatabaseField
	private String category2 = "Other";
	@DatabaseField
	private long repo_id = 0;
	@DatabaseField
	private String iconPath;
	@DatabaseField
	private String rating = "0";
	private ArrayList<String> screenshots = new ArrayList<String>();
	@DatabaseField
	private String path;
	@DatabaseField
	private String md5;
	
	private int appHashId = 0;
	
	private String date;
	private int age = 0;
	private int minScreen= 0;
	private String minSdk = "0";
	private String minGlEs = "0";
	private String repoName = "Aptoide";
	
	
	/**
	 * 
	 * ViewApk Skeleton Constructor
	 *
	 */
	public ViewApk(){
	}
	
	/**
	 * 
	 * ViewApk Constructor
	 *
	 * @param id
	 * @param apkid
	 * @param name
	 * @param vercode
	 * @param vername
	 * @param size
	 * @param downloads
	 * @param category1
	 * @param category2
	 * @param repo_id
	 */
	public ViewApk(long id, String apkid, String name, int vercode, String vername, String size, String downloads, String category1, String category2, long repo_id) {
		this.id = id;
		this.apkid = apkid;
		this.name = name;
		this.vercode = vercode;
		this.vername = vername;
		this.size = size;
		this.downloads = downloads;
		this.category1 = category1;
		this.category2 = category2;
		this.repo_id = repo_id;
		generateAppHashid();
//		Log.d("Aptoide-ViewApk", "\n\n\napkid: "+apkid+" vercode: "+vercode+" appHashid: "+appHashId);
	}
	
	public void generateAppHashid(){
		this.appHashId = (apkid+"|"+vercode).hashCode();
	}
	

	public long getId(){
		return id;
	}
	
	public String getRating(){
		return rating;
	}
	
	public String getApkid() {
		return apkid;
	}

	public String getName() {
		return name;
	}

	public int getVercode() {
		return vercode;
	}

	public String getVername() {
		return vername;
	}

	public String getSize() {
		return size;
	}

	public String getDownloads() {
		return downloads;
	}

	public String getCategory1() {
		return category1;
	}

	public String getCategory2() {
		return category2;
	}

	public long getRepo_id() {
		return repo_id;
	}

	public String getIconPath() {
		return iconPath;
	}
	
	public int getAppHashId(){
		if(appHashId == 0){
			generateAppHashid();
		}
		return appHashId;
	}
	
	
	public void setId(long id){
		this.id = id;
	}
	
	public void setApkid(String apkid) {
		this.apkid = apkid;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setVercode(int vercode) {
		this.vercode = vercode;
	}

	public void setVername(String vername) {
		this.vername = vername;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public void setDownloads(String downloads) {
		this.downloads = downloads;
	}

	public void setCategory1(String category1) {
		this.category1 = category1;
	}

	public void setCategory2(String category2) {
		this.category2 = category2;
	}

	public void setRepo_id(long repo_id) {
		this.repo_id = repo_id;
	}

	public void setIconPath(String iconPath) {
		this.iconPath = iconPath;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	public String getPath(){
		return this.path;
	}

	public void setRating(String rating) {
		this.rating=rating;
	}

	public void addScreenshot(String string) {
		screenshots.add(string);
	}

	public ArrayList<String> getScreenshots() {
		return screenshots;
	}

	public String getMd5() {
		return this.md5;
	}
	
	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void setAge(int ordinal) {
		this.age=ordinal;
	}
	
	public int getAge(){
		return age;
	}

	public int getMinScreen() {
		return minScreen;
	}

	public void setMinScreen(int minScreen) {
		this.minScreen = minScreen;
	}

	public String getMinSdk() {
		return minSdk;
	}

	public void setMinSdk(String minSdk) {
		this.minSdk = minSdk;
	}

	public String getMinGlEs() {
		return minGlEs;
	}

	public void setMinGlEs(String minGlEs) {
		this.minGlEs = minGlEs;
	}
	
	

	public void clear() {
		
		this.id = 0;
		this.apkid = "";
		this.name = apkid;
		this.vercode = 0;
		this.vername = "Unversioned";
		this.size = "No size";
		this.downloads = "No downloads";
		this.category1 = "Other";
		this.category2 = "Other";
		this.iconPath="";
		this.path="";
		this.date="";
		this.age=0;
		this.minGlEs="0";
		this.minScreen=0;
		this.minSdk="0";
		screenshots.clear();
	}

	
	/**
	 * hashCode, unsafe cast from long (theoretically the id which is the db's auto-increment id will never overflow integer in a realistic scenario)
	 */
	@Override
	public int hashCode() {
		return getAppHashId();
	}


	@Override
	public boolean equals(Object object) {
		if(object instanceof ViewApk){
			ViewApk app = (ViewApk) object;
			if(app.hashCode() == this.hashCode()){
				return true;
			}
		}
		return false;
	}


	@Override
	public String toString() {
		return " appHashId: "+appHashId+" PackageName: "+apkid+" Name: "+name+"  VersionName: "+vername;
	}

	


	// Parcelable stuff //
	
	
	public static final Parcelable.Creator<ViewApk> CREATOR = new Parcelable.Creator<ViewApk>() {
		public ViewApk createFromParcel(Parcel in) {
			return new ViewApk(in);
		}

		public ViewApk[] newArray(int size) {
			return new ViewApk[size];
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

	protected ViewApk(Parcel in){
		readFromParcel(in);
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(apkid);
		out.writeString(name);
		out.writeInt(vercode);
		out.writeString(vername);
		out.writeString(iconPath);
		out.writeString(path);
		out.writeInt(appHashId);
	}
	
	public void readFromParcel(Parcel in) {
		this.apkid = in.readString();
		this.name = in.readString();
		this.vercode = in.readInt();
		this.vername = in.readString();
		this.iconPath = in.readString();
		this.path = in.readString();
		this.appHashId = in.readInt();
	}

	public String getRepoName() {
		return repoName;
	}

	public void setRepoName(String repoName) {
		this.repoName = repoName;
	}
	
}
