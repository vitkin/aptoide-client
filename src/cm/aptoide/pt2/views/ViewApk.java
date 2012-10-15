package cm.aptoide.pt2.views;

import java.util.ArrayList;

import android.util.Log;

public class ViewApk {

	private long id;
	private String apkid = "";
	private String name = apkid;
	private int vercode = 0;
	private String vername = "Unversioned";
	private String size = "0";
	private String downloads = "0";
	private String category1 = "Other";
	private String category2 = "Other";
	private long repo_id = 0;
	private String iconPath;
	private String rating = "0";
	private ArrayList<String> screenshots = new ArrayList<String>();
	private String path;
	private String md5;
	private int appHashId;
	
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
	public ViewApk(long id, String apkid, String name, int vercode, String vername, String size, String downloads, String category1,
			String category2, long repo_id) {
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
		this.appHashId = (apkid+"|"+vercode).hashCode();
		Log.d("Aptoide-ViewApk", "\n\n\napkid: "+apkid+" vercode: "+vercode+" appHashid: "+appHashId);
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
		Log.d("Aptoide-ViewApk", "\n\n\napkid: "+apkid+" vercode: "+vercode+" appHashid: "+appHashId);
		return appHashId;
	}
	
	
	public void setId(long id){
		this.id = id;
	}
	
	public void setApkid(String apkid) {
		this.apkid = apkid;
		this.appHashId = (apkid+"|"+vercode).hashCode();		
		Log.d("Aptoide-ViewApk", "\n\n\napkid: "+apkid+" vercode: "+vercode+" appHashid: "+appHashId);
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
		screenshots.clear();
	}

	/**
	 * hashCode, unsafe cast from long (theoretically the id which is the db's auto-increment id will never overflow integer in a realistic scenario)
	 */
	@Override
	public int hashCode() {
		return this.appHashId;
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

}
