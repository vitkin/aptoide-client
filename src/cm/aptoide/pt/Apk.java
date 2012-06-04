package cm.aptoide.pt;

import java.sql.Struct;

import android.content.ContentValues;

public class Apk {
	public Apk(int vercode, long repo_id) {
		this.vercode=vercode;
		this.repo_id=repo_id;
	}
	public Apk() {}
	
	public Apk(ContentValues values) {
		
	}
	long id;
	public String apkid;
	public String name;
	public String vername;
	public int vercode;
	public String downloads;
	public String size;
	public String category1;
	public String category2;
	public String stars;
	public int age;
	public String md5;
	public String path;
	public String icon;
	public String date;
	public String minSdk;
	public long repo_id;
	public int minScreenSize;
	public String featuregraphic;

}
