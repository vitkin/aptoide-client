package cm.aptoide.pt2;

public class Apk {

	
	String apkid= "";
	String name = apkid;
	String vercode= "0";
	String vername = "Unversioned";
	String size =  "0";
	String downloads = "0";
	String category1 = "Other";
	String category2 = "Other";
	public long repo_id = 0;
	public String iconPath;
	
	public void clear(){
		apkid= "";
		name = apkid;
		vercode= "0";
		vername = "Unversioned";
		size =  "No size";
		downloads = "No downloads";
		category1 = "Other";
		category2 = "Other";
	}
	
}
