package cm.aptoide.pt2.views;

public class ViewApk {

	private String apkid = "";
	private String name = apkid;
	private String vercode = "0";
	private String vername = "Unversioned";
	private String size = "0";
	private String downloads = "0";
	private String category1 = "Other";
	private String category2 = "Other";
	private long repo_id = 0;
	private String iconPath;

	public String getApkid() {
		return apkid;
	}

	public String getName() {
		return name;
	}

	public String getVercode() {
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
	
	public void setApkid(String apkid) {
		this.apkid = apkid;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setVercode(String vercode) {
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
		
		this.apkid = "";
		this.name = apkid;
		this.vercode = "0";
		this.vername = "Unversioned";
		this.size = "No size";
		this.downloads = "No downloads";
		this.category1 = "Other";
		this.category2 = "Other";
		
	}

}
