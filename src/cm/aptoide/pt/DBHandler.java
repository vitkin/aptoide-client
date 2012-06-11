package cm.aptoide.pt;


import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import cm.aptoide.pt.webservices.login.Algorithms;



import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.test.IsolatedContext;
import android.util.Log;
import android.widget.TextView;

public class DBHandler {

	private static SQLiteDatabase database;
	private static DBStructure dbHelper;
	private static ExtrasDBStructure extrasDbHelper;
	private HashMap<String,Apk> localApk ;
	private SharedPreferences sPref;
	private Context context; 
	
	public DBHandler(Context context) {
		dbHelper = new DBStructure(context);
		this.context=context;
		extrasDbHelper = new ExtrasDBStructure(context);
		sPref = context.getSharedPreferences("aptoide_prefs", Context.MODE_PRIVATE);
	}
	
	public synchronized void open() {
		System.out.println("Opening");
		if(database==null||!database.isOpen()){
			database = dbHelper.getWritableDatabase();
		}
			
		
		
		
		
	}
	
	public synchronized void openExtras() {
		System.out.println("Opening Extras");
		if(database==null||!database.isOpen()){
			database = extrasDbHelper.getWritableDatabase();
		}
			
		
		
		
		
	}

	public synchronized void close() {
		if(database.isOpen()){
			dbHelper.close();
		}
		
		System.out.println("Closed");
	}
	
	public void beginTransation(){
			database.beginTransaction();
		
	}
	
	public void prepareDb(){
		Apk apk = null;
		localApk = new HashMap<String,Apk>();
		
		Cursor c = database.query(DBStructure.TABLE_APK, new String[]{DBStructure.COLUMN_APK_VERCODE,DBStructure.COLUMN_APK_REPO_ID,DBStructure.COLUMN_APK_APKID}, null,null, null, null, null);
		for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){
			apk=new Apk();
			apk.apkid=c.getString(2);
			apk.vercode=c.getInt(0);
			apk.repo_id=c.getInt(1);
			localApk.put(apk.apkid,apk);
		}
		
		
		c.close();
	}
	
	public void endTransation(){
		try{
			database.setTransactionSuccessful();
			database.endTransaction();
		}catch (Exception e) {
			e.printStackTrace();
		}
			
		
	}
	
	public void insertAPK(Apk apk) {
		try{
			
		
		ContentValues values = new ContentValues();
		long id;
		
		if(!localApk.containsKey(apk.apkid)){
			
			values.put(DBStructure.COLUMN_APK_APKID, apk.apkid);
			values.put(DBStructure.COLUMN_APK_NAME, apk.name);
			values.put(DBStructure.COLUMN_APK_VERNAME, apk.vername);
			values.put(DBStructure.COLUMN_APK_VERCODE, apk.vercode);
			values.put(DBStructure.COLUMN_APK_DOWNLOADS, apk.downloads);
			values.put(DBStructure.COLUMN_APK_RATING, apk.stars);
			values.put(DBStructure.COLUMN_APK_AGE, apk.age);
			values.put(DBStructure.COLUMN_APK_SIZE, apk.size);
			values.put(DBStructure.COLUMN_APK_MD5, apk.md5);
			values.put(DBStructure.COLUMN_APK_PATH, apk.path);
			values.put(DBStructure.COLUMN_APK_ICON, apk.icon);
			values.put(DBStructure.COLUMN_APK_DATE, apk.date);
			values.put(DBStructure.COLUMN_APK_SDK, apk.minSdk);
			values.put(DBStructure.COLUMN_APK_SCREEN, apk.minScreenSize);
			values.put(DBStructure.COLUMN_APK_REPO_ID, apk.repo_id);
			
			id = database.insert(DBStructure.TABLE_APK, null,values);
			values.clear();
			values.put(DBStructure.COLUMN_CATEGORY_APKID, id);
			values.put(DBStructure.COLUMN_CATEGORY_CATEGORY1_NAME,apk.category1);
			values.put(DBStructure.COLUMN_CATEGORY_CATEGORY2_NAME,apk.category2);
			values.put(DBStructure.COLUMN_CATEGORY_REPO_ID,apk.repo_id);
			database.insert(DBStructure.TABLE_CATEGORY, null, values);
			localApk.put(apk.apkid, new Apk(apk.vercode,apk.repo_id));
		} else {
			
				
				values.put(DBStructure.COLUMN_APK_APKID, apk.apkid);
				values.put(DBStructure.COLUMN_APK_NAME, apk.name);
				values.put(DBStructure.COLUMN_APK_VERNAME, apk.vername);
				values.put(DBStructure.COLUMN_APK_VERCODE, apk.vercode);
				values.put(DBStructure.COLUMN_APK_DOWNLOADS, apk.downloads);
				values.put(DBStructure.COLUMN_APK_RATING, apk.stars);
				values.put(DBStructure.COLUMN_APK_AGE, apk.age);
				values.put(DBStructure.COLUMN_APK_SIZE, apk.size);
				values.put(DBStructure.COLUMN_APK_MD5, apk.md5);
				values.put(DBStructure.COLUMN_APK_PATH, apk.path);
				values.put(DBStructure.COLUMN_APK_ICON, apk.icon);
				values.put(DBStructure.COLUMN_APK_DATE, apk.date);
				values.put(DBStructure.COLUMN_APK_SDK, apk.minSdk);
				values.put(DBStructure.COLUMN_APK_SCREEN, apk.minScreenSize);
				values.put(DBStructure.COLUMN_APK_REPO_ID, apk.repo_id);
				if(localApk.get(apk.apkid).vercode>apk.vercode){
					database.insert(DBStructure.TABLE_OLD, null,values);
				}else if(localApk.get(apk.apkid).vercode<apk.vercode){
					Cursor c = database.query(DBStructure.TABLE_APK, null, DBStructure.COLUMN_APK_APKID+"=?" , new String[]{apk.apkid}, null, null, null);
					c.moveToFirst();
					ContentValues old_values = new ContentValues();
					old_values.clear();
					old_values.put(DBStructure.COLUMN_APK_APKID, c.getString(c.getColumnIndex(DBStructure.COLUMN_APK_APKID)));
					old_values.put(DBStructure.COLUMN_APK_NAME, c.getString(c.getColumnIndex(DBStructure.COLUMN_APK_NAME)));
					old_values.put(DBStructure.COLUMN_APK_VERNAME, c.getString(c.getColumnIndex(DBStructure.COLUMN_APK_VERNAME)));
					old_values.put(DBStructure.COLUMN_APK_VERCODE, c.getString(c.getColumnIndex(DBStructure.COLUMN_APK_VERCODE)));
					old_values.put(DBStructure.COLUMN_APK_DOWNLOADS, c.getString(c.getColumnIndex(DBStructure.COLUMN_APK_DOWNLOADS)));
					old_values.put(DBStructure.COLUMN_APK_RATING, c.getString(c.getColumnIndex(DBStructure.COLUMN_APK_RATING)));
					old_values.put(DBStructure.COLUMN_APK_AGE, c.getString(c.getColumnIndex(DBStructure.COLUMN_APK_AGE)));
					old_values.put(DBStructure.COLUMN_APK_SIZE, c.getString(c.getColumnIndex(DBStructure.COLUMN_APK_SIZE)));
					old_values.put(DBStructure.COLUMN_APK_MD5, c.getString(c.getColumnIndex(DBStructure.COLUMN_APK_MD5)));
					old_values.put(DBStructure.COLUMN_APK_PATH, c.getString(c.getColumnIndex(DBStructure.COLUMN_APK_PATH)));
					old_values.put(DBStructure.COLUMN_APK_ICON, c.getString(c.getColumnIndex(DBStructure.COLUMN_APK_ICON)));
					old_values.put(DBStructure.COLUMN_APK_DATE, c.getString(c.getColumnIndex(DBStructure.COLUMN_APK_DATE)));
					old_values.put(DBStructure.COLUMN_APK_SDK, c.getString(c.getColumnIndex(DBStructure.COLUMN_APK_SDK)));
					old_values.put(DBStructure.COLUMN_APK_SCREEN, c.getString(c.getColumnIndex(DBStructure.COLUMN_APK_SCREEN)));
					old_values.put(DBStructure.COLUMN_APK_REPO_ID, c.getString(c.getColumnIndex(DBStructure.COLUMN_APK_REPO_ID)));
					c.close();
					database.insert(DBStructure.TABLE_OLD, null,old_values);
					database.update(DBStructure.TABLE_APK, values, DBStructure.COLUMN_APK_APKID+"=?", new String[]{apk.apkid});
					localApk.remove(apk.apkid);
					localApk.put(apk.apkid, new Apk(apk.vercode,apk.repo_id));
//				}else if(localApk.get(apk.apkid).vercode==apk.vercode){
//					values.put(DBStructure.COLUMN_OTHER_SERVER_CATEGORY2, apk.category2);
//					database.insert(DBStructure.TABLE_OTHER_SERVER, null,
//							values);
				}
		}
		
		
		
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Cursor getApk(String orderBy) {
		Cursor c = null;
		String query = "select b.name, b._id,b.icon,b.vername,b.repo_id,b.rating,b.downloads from apk b";
		try {
			if(sPref.getString("app_rating","All").equals("Mature")){
				query += " where b.age <=1";
			}else{
				query += " where b.age <1";
			}
			if(sPref.getBoolean("hwspecsChkBox",false)){
				HWSpecifications specs = new HWSpecifications(context);
				query += " and b.screen <= "+specs.screenSize+" and b.sdk <= "+specs.sdkVer;
			}
			System.out.println(sPref.getString("app_rating","none"));
			
			
			query+=" order by b."+orderBy;
			System.out.println(query);
			c= database.rawQuery(query ,null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return c;
				
	}
	
	public Cursor getInstalled(String orderBy) {
		Cursor c = null;
		try {
			c = database.rawQuery("select a.name, b._id, b.icon, a.vername,b.repo_id, b.rating,b.downloads from installed a INNER JOIN apk b ON a.apkid=b.apkid order by b."+orderBy,null);
			c.moveToFirst();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return  c;
				
	}
	
	public Cursor getUpdates(String orderBy) {
		
		Cursor c = null;
		 try {
			c= database.rawQuery("select b.name, b._id, b.icon, b.vername,b.repo_id, b.rating,b.downloads,b.apkid from installed as a INNER JOIN apk b ON a.apkid=b.apkid and a.vercode < b.vercode order by b."+orderBy,null);
			c.moveToFirst();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return  c;
				
	}

	public void delete(String string) {
		database.delete(DBStructure.TABLE_APK, "apkid='cm.aptoide.pt'", null);
		System.out.println("Deleted2");
	}

//	public String getCategory2(long arg2) {
//		Cursor c = database.query(DBStructure.TABLE_CATEGORY, new String[]{DBStructure.COLUMN_CATEGORY_NAME}, DBStructure.COLUMN_CATEGORY_APKID+"=?", new String[]{arg2+""}, null, null, null);
//		c.moveToFirst();
//		String returnString = c.getString(0);
//		c.close();
//		return returnString;
//	}
//	
	public Cursor getCategories1() {
		Cursor c = null;
		try {
			c = database.query(DBStructure.TABLE_CATEGORY, new String[]{DBStructure.COLUMN_CATEGORY_CATEGORY1_NAME,DBStructure.COLUMN_CATEGORY_APKID}, null, null, DBStructure.COLUMN_CATEGORY_CATEGORY1_NAME, null, null);
			c.moveToFirst();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return c;
	}
	
	public Cursor getCategories2(String category1) {
		Cursor c = null;
		try {
			c = database.query(DBStructure.TABLE_CATEGORY, new String[]{DBStructure.COLUMN_CATEGORY_CATEGORY2_NAME,DBStructure.COLUMN_CATEGORY_APKID}, DBStructure.COLUMN_CATEGORY_CATEGORY1_NAME+"=?", new String[]{category1},  DBStructure.COLUMN_CATEGORY_CATEGORY2_NAME, null,null);
			c.moveToFirst();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return c;
	}

	public void removeRepo(long repo_id, boolean storeManagerRemove) {
		int entries = 0;
		System.out.println("Removing repo: "+repo_id);
		try {
		
		entries+=database.delete(DBStructure.TABLE_APK, DBStructure.COLUMN_APK_REPO_ID+"=?", new String[]{repo_id+""});
		entries+=database.delete(DBStructure.TABLE_OLD, DBStructure.COLUMN_APK_OLD_REPO_ID+"=?", new String[]{repo_id+""});
		entries+=database.delete(DBStructure.TABLE_CATEGORY, DBStructure.COLUMN_CATEGORY_REPO_ID+"=?", new String[]{repo_id+""});
		entries+=database.delete(DBStructure.TABLE_SCHEDULED, DBStructure.COLUMN_SCHEDULED_REPO_ID+"=?", new String[]{repo_id+""});
		
		System.out.println("Deleted: "+entries+ " entries.");
		
		
		if(storeManagerRemove){
			database.delete(DBStructure.TABLE_REPOS, DBStructure.COLUMN_REPOS_ID+"=?", new String[]{repo_id+""});
		}else{
			prepareDb();
		}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
//		database.setTransactionSuccessful();
		
	}
	
	public void resetRepo(long repo_id){
		database.delete(DBStructure.TABLE_APK, DBStructure.COLUMN_APK_REPO_ID+"=?", new String[]{repo_id+""});
		database.delete(DBStructure.TABLE_OLD, DBStructure.COLUMN_APK_OLD_REPO_ID+"=?", new String[]{repo_id+""});
		database.delete(DBStructure.TABLE_CATEGORY, DBStructure.COLUMN_CATEGORY_REPO_ID+"=?", new String[]{repo_id+""});
		ContentValues values = new ContentValues();
		values.put(DBStructure.COLUMN_REPOS_DELTA, 0);
		values.put(DBStructure.COLUMN_REPOS_UPDATETIME, 0);
		database.update(DBStructure.TABLE_REPOS, values, DBStructure.COLUMN_REPOS_ID+"=?", new String[]{repo_id+""});
	}
	
	public String getCountAll() {
		Cursor c = database.query(DBStructure.TABLE_APK, new String[]{DBStructure.COLUMN_APK_APKID}, null, null, null, null, null);
		String returnString = c.getCount()+"";
		c.close();
		return returnString;
	}


	public List<String> getStartupInstalled() {
		List<String> database_installed_list = new ArrayList<String>();
		Cursor c = database.query(DBStructure.TABLE_INSTALLED, new String[]{DBStructure.COLUMN_INSTALLED_APKID}, null, null, null, null, null);
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			database_installed_list.add(c.getString(c.getColumnIndex(DBStructure.COLUMN_INSTALLED_APKID)));
        }
		c.close();
		return database_installed_list;
	}

	public void insertInstalled(Apk apk) {
		
		ContentValues values = new ContentValues();
		values.put(DBStructure.COLUMN_INSTALLED_NAME, apk.name);
		values.put(DBStructure.COLUMN_INSTALLED_APKID, apk.apkid);
		values.put(DBStructure.COLUMN_INSTALLED_VERNAME, apk.vername);
		values.put(DBStructure.COLUMN_INSTALLED_VERCODE, apk.vercode);
		database.insert(DBStructure.TABLE_INSTALLED, null, values);
		
	}

	public Cursor getRepositories() {
		Cursor c = null;
		try {
			c= database.query(DBStructure.TABLE_REPOS, null, null, null, null, null, null);
			c.moveToFirst();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return c;
	}
	
	public void insertRepository(String uri, String user, String password, boolean extended) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		ContentValues values = new ContentValues();
		
		
		values.put(DBStructure.COLUMN_REPOS_URI,uri);
		values.put(DBStructure.COLUMN_REPOS_INUSE,1);
		values.put(DBStructure.COLUMN_REPOS_USER,user);
		values.put(DBStructure.COLUMN_REPOS_PASSWORD,password);
		values.put(DBStructure.COLUMN_REPOS_EXTENDED,extended);
		
		try {
			database.insert(DBStructure.TABLE_REPOS, null,values);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<ServerNode> getInUseServers() {
		Cursor c = null;
		ServerNode server;
		ArrayList<ServerNode> serverList = new ArrayList<ServerNode>();
		try{
			c=database.query(DBStructure.TABLE_REPOS, null, DBStructure.COLUMN_REPOS_INUSE+"=?", new String[]{"1"}, null, null, null);
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){
			server = new ServerNode();
			server.uri=c.getString(c.getColumnIndex(DBStructure.COLUMN_REPOS_URI));
			server.hash=c.getString(c.getColumnIndex(DBStructure.COLUMN_REPOS_DELTA));
			server.id=c.getLong(c.getColumnIndex(DBStructure.COLUMN_REPOS_ID));
			serverList.add(server);
		}
		
		c.close();
		return serverList;
	}

	public void insertBasepath(String basepath, long repo_id) {
		try{
			ContentValues values = new ContentValues();
			values.put(DBStructure.COLUMN_REPOS_BASEPATH, basepath);
			database.update(DBStructure.TABLE_REPOS, values, DBStructure.COLUMN_REPOS_ID+"=?", new String[]{repo_id+""});
			
		}catch (Exception e) {
			
		}
	}
	
	public void insertAPKpath(String apkpath, long repo_id) {
		try{
			ContentValues values = new ContentValues();
			values.put(DBStructure.COLUMN_REPOS_APKPATH, apkpath);
			database.update(DBStructure.TABLE_REPOS, values, DBStructure.COLUMN_REPOS_ID+"=?", new String[]{repo_id+""});
			
		}catch (Exception e) {
			
		}
	}

	public void insertIconspath(String iconspath, long repo_id) {
		try{
		ContentValues values = new ContentValues();
		values.put(DBStructure.COLUMN_REPOS_ICONSPATH, iconspath);
		database.update(DBStructure.TABLE_REPOS, values, DBStructure.COLUMN_REPOS_ID+"=?", new String[]{repo_id+""});
		}catch (Exception e) {
			
		}
	}

	public void insertScreenspath(String screenspath, long repo_id) {
		try{
	
		ContentValues values = new ContentValues();
		values.put(DBStructure.COLUMN_REPOS_SCREENSPATH, screenspath);
		database.update(DBStructure.TABLE_REPOS, values, DBStructure.COLUMN_REPOS_ID+"=?", new String[]{repo_id+""});
		}catch (Exception e) {
			
		}
	}

	public void insertWebservicespath(String wspath, long repo_id) {
		try{
	
		ContentValues values = new ContentValues();
		values.put(DBStructure.COLUMN_REPOS_WSPATH, wspath);
		database.update(DBStructure.TABLE_REPOS, values, DBStructure.COLUMN_REPOS_ID+"=?", new String[]{repo_id+""});
		}catch (Exception e) {
			
		}
	}
	
	public String getBasepath(long repo_id){
		String basepath = null;
		Cursor c=null;
		try{
		c = database.query(DBStructure.TABLE_REPOS, new String[]{DBStructure.COLUMN_REPOS_BASEPATH}, DBStructure.COLUMN_REPOS_ID+"=?", new String[]{repo_id+""}, null, null, null);
		
		c.moveToFirst();
		basepath=c.getString(0);
		}catch (Exception e) {
			
		}
		c.close();
		return basepath;
		
	}

	public String getIconspath(long repo_id){
		String basepath = null;
		Cursor c=null;
		try{
		c = database.query(DBStructure.TABLE_REPOS, new String[]{DBStructure.COLUMN_REPOS_ICONSPATH}, DBStructure.COLUMN_REPOS_ID+"=?", new String[]{repo_id+""}, null, null, null);
		
		c.moveToFirst();
		basepath=c.getString(0);
		}catch (Exception e) {
			
		}
		c.close();
		return basepath;
		
	}

	public String getWebservicespath(long repo_id){
		String basepath = null;
		Cursor c=null;
		try{
		c = database.query(DBStructure.TABLE_REPOS, new String[]{DBStructure.COLUMN_REPOS_WSPATH}, DBStructure.COLUMN_REPOS_ID+"=?", new String[]{repo_id+""}, null, null, null);
		
		c.moveToFirst();
		basepath=c.getString(0);
		}catch (Exception e) {
			
		}
		c.close();
		return basepath;
		
	}
	
	public String getApkpath(long repo_id){
		String basepath = null;
		Cursor c=null;
		try{
		c = database.query(DBStructure.TABLE_REPOS, new String[]{DBStructure.COLUMN_REPOS_APKPATH}, DBStructure.COLUMN_REPOS_ID+"=?", new String[]{repo_id+""}, null, null, null);
		
		c.moveToFirst();
		basepath=c.getString(0);
		}catch (Exception e) {
			
		}
		c.close();
		return basepath;
	}
	
	public void insertNApk(int apks, long repo_id){
		ContentValues values = new ContentValues();
		values.put(DBStructure.COLUMN_REPOS_NAPK,apks);
		try {
			database.update(DBStructure.TABLE_REPOS,values,DBStructure.COLUMN_REPOS_ID+"=?",new String[]{repo_id+""});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Cursor getApkByCategory(String category, String orderBy) {
		Cursor c = null;
		String query = "select b.name, b._id,b.icon,b.vername,b.repo_id,b.rating,b.downloads from category a, apk b where a._id=b._id and a.category2='"+category+"'";
		try {
			if(sPref.getBoolean("hwspecsChkBox",false)){
				HWSpecifications specs = new HWSpecifications(context);
				query += " and b.screen <= "+specs.screenSize+" and b.sdk <= "+specs.sdkVer;
				
				
				
			}
			System.out.println(sPref.getString("app_rating","none"));
				
			
			if(sPref.getString("app_rating","All").equals("Mature")){
				query += " and b.age <=1";
			}else{
				query += " and b.age <1";
			}
			
			query+=" order by b."+orderBy;
			System.out.println(query);
			c= database.rawQuery(query ,null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		return c;
	}

	public String getUpdateTime(String srv) {
		String time = null;
		Cursor c = database.query(DBStructure.TABLE_REPOS, new String[]{DBStructure.COLUMN_REPOS_UPDATETIME}, DBStructure.COLUMN_REPOS_URI+"=?", new String[]{srv}, null, null, null);
		c.moveToFirst();
		time=c.getString(0);
		c.close();
		return time;
	}

	public void setUpdateTime(String hash, String srv) {
		ContentValues values = new ContentValues();
		values.put(DBStructure.COLUMN_REPOS_UPDATETIME, hash);
		database.update(DBStructure.TABLE_REPOS, values, DBStructure.COLUMN_REPOS_URI+"=?", new String[]{srv});
	}

	public void setServerDelta(long repo_id, String deltahash) {
		ContentValues values = new ContentValues();
		values.put(DBStructure.COLUMN_REPOS_DELTA, deltahash);
		database.update(DBStructure.TABLE_REPOS, values, DBStructure.COLUMN_REPOS_ID+"=?", new String[]{repo_id+""});
	}

	public void removeApk(String apkid) {
		database.delete(DBStructure.TABLE_APK, DBStructure.COLUMN_APK_APKID+"=?", new String[]{apkid});
		database.delete(DBStructure.TABLE_OLD, DBStructure.COLUMN_APK_APKID+"=?", new String[]{apkid});
	}

	public int getCategoryCount(String name, boolean secondaryCatg) {
		Cursor c = null;
		int i = 0;
		try{
			if(secondaryCatg){
				c = database.rawQuery("select count(*) from apk as a,category as b where a._id=b._id and b.category2='"+name+"'", null);
			}else{
				c = database.rawQuery("select count(*) from apk as a,category as b where a._id=b._id and b.category1='"+name+"'", null);
			}
			
			c.moveToFirst();
			i = c.getInt(0);
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		c.close();
		return i;
	}

	public Cursor getApk(long id) {
		Cursor c = null;
		try{
			c= database.query(DBStructure.TABLE_APK, null, DBStructure.COLUMN_APK_ID+"=?", new String[]{id+""}, null, null, null);
			c.moveToFirst();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return c;
	}

	public String getRepoName(long repo_id) {
		String repo_name = null;
		Cursor c = database.query(DBStructure.TABLE_REPOS, new String[]{DBStructure.COLUMN_REPOS_URI}, DBStructure.COLUMN_REPOS_ID+"=?", new String[]{repo_id+""}, null, null, null);
		c.moveToFirst();
		repo_name = c.getString(0);
		c.close();
		return repo_name;
	}

	public ArrayList<VersionApk> getOldApks(String apkid) {
		ArrayList<VersionApk> tmp = new ArrayList<VersionApk>();
		Cursor c = null;
		try{
			
			c = database.query(DBStructure.TABLE_OLD, new String[] {DBStructure.COLUMN_APK_OLD_VERNAME, DBStructure.COLUMN_APK_OLD_SIZE, DBStructure.COLUMN_APK_OLD_VERCODE, DBStructure.COLUMN_APK_OLD_DOWNLOADS}, DBStructure.COLUMN_APK_OLD_APKID+"=?", new String[]{apkid}, null, null, null);
			c.moveToFirst();
			for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){
				tmp.add( new VersionApk(c.getString(0), c.getInt(2), apkid, c.getInt(1),c.getInt(3)) );
			}
			
		}catch (Exception e){
			e.printStackTrace();
		}finally{
			c.close();
		}
		return tmp;
	}

	public Vector<DownloadNode> getPathHash(String id_apk, String ver){
		Vector<DownloadNode> out = new Vector<DownloadNode>();
		Cursor c = null;
		Cursor e = null;
		try{
			c = database.query(DBStructure.TABLE_APK, new String[] {DBStructure.COLUMN_APK_REPO_ID, DBStructure.COLUMN_APK_PATH, DBStructure.COLUMN_APK_MD5, DBStructure.COLUMN_APK_SIZE, DBStructure.COLUMN_APK_VERNAME}, "apkid='"+id_apk+"' and vername ='"+ver+"'", null, null, null, null);
			c.moveToFirst();
			for(int i =0; i<c.getCount(); i++){
				String repo = c.getString(0);
				
				e = database.query(DBStructure.TABLE_REPOS, new String[] {"apkpath"}, "_id='"+repo+"'", null, null, null, null);
				e.moveToFirst();
				if(e.getCount()==0){
					e = database.query(DBStructure.TABLE_REPOS, new String[] {"basepath"}, "_id='"+repo+"'", null, null, null, null);
					e.moveToFirst();
				}
				
				
				String remotePath = e.getString(0)+c.getString(1);
				String md5sum = null;
				if(!c.isNull(2)){
					md5sum = c.getString(2);
				}
				int size = c.getInt(3);
				DownloadNode node = new DownloadNode(repo, remotePath, md5sum, size);
				node.version = c.getString(4);
				out.add(node);
			}
			//c.close();
		}catch(Exception exception){
			Log.e("Aptoide", exception.getMessage());
		}finally{
			c.close();
			if(e!=null){
				e.close();
			}
		}
		
		if(out.size()==0){
			out = getPathHashOld(id_apk, ver);
		}
		return out;
	}
	
	public Vector<DownloadNode> getFeaturedPathHash(String id_apk, String ver){
		Vector<DownloadNode> out = new Vector<DownloadNode>();
		Cursor c = null;
		Cursor e = null;
		try{
			c = database.query(DBStructure.TABLE_EDITORSCHOICE, new String[] {DBStructure.COLUMN_APK_REPO_ID, DBStructure.COLUMN_APK_PATH, DBStructure.COLUMN_APK_MD5, DBStructure.COLUMN_APK_SIZE, DBStructure.COLUMN_APK_VERNAME}, "apkid='"+id_apk+"' and vername ='"+ver+"'", null, null, null, null);
			c.moveToFirst();
			for(int i =0; i<c.getCount(); i++){
				String repo = c.getString(0);
				e = database.query(DBStructure.TABLE_EDITORSCHOICEREPOS, new String[] {"basepath"}, "_id='"+repo+"'", null, null, null, null);
				e.moveToFirst();
				String remotePath = e.getString(0)+c.getString(1);
				String md5sum = null;
				if(!c.isNull(2)){
					md5sum = c.getString(2);
				}
				int size = c.getInt(3);
				DownloadNode node = new DownloadNode(repo, remotePath, md5sum, size);
				node.version = c.getString(4);
				out.add(node);
			}
			//c.close();
		}catch(Exception exception){
			Log.e("Aptoide", exception.getMessage());
		}finally{
			c.close();
			if(e!=null){
				e.close();
			}
		}
		return out;
	}
	
	/**
	 * @author rafael
	 * 
	 * @param id_apk
	 * @param ver
	 * @return
	 */
	private Vector<DownloadNode> getPathHashOld(String id_apk, String ver){
		
		Vector<DownloadNode> out = new Vector<DownloadNode>();
		Cursor c = null;
		Cursor e = null;
		try{
			c = database.query(DBStructure.TABLE_OLD, new String[] {DBStructure.COLUMN_APK_REPO_ID, DBStructure.COLUMN_APK_PATH, DBStructure.COLUMN_APK_MD5, DBStructure.COLUMN_APK_SIZE, DBStructure.COLUMN_APK_VERNAME}, "apkid='"+id_apk+"' and vername ='"+ver+"'", null, null, null, null);
			c.moveToFirst();
			for(int i =0; i<c.getCount(); i++){
				String repo = c.getString(0);
				
				e = database.query(DBStructure.TABLE_REPOS, new String[] {"apkpath"}, "_id='"+repo+"'", null, null, null, null);
				e.moveToFirst();
				if(e.getCount()==0){
					e = database.query(DBStructure.TABLE_REPOS, new String[] {"basepath"}, "_id='"+repo+"'", null, null, null, null);
					e.moveToFirst();
				}
				
				
				String remotePath = e.getString(0)+c.getString(1);
				String md5sum = null;
				if(!c.isNull(2)){
					md5sum = c.getString(2);
				}
				int size = c.getInt(3);
				DownloadNode node = new DownloadNode(repo, remotePath, md5sum, size);
				node.version = c.getString(4);
				out.add(node);
			}
			//c.close();
		}catch(Exception exception){
		}finally{
			c.close();
			if(e!=null){
				e.close();
			}
				
		}
		return out;
		
	}

	public void insertInstalled(String apkid,
			int versionCode, String versionName, String name) {
		ContentValues values = new ContentValues();
		values.put(DBStructure.COLUMN_INSTALLED_APKID, apkid);
		values.put(DBStructure.COLUMN_INSTALLED_VERCODE, versionCode);
		values.put(DBStructure.COLUMN_INSTALLED_VERNAME, versionName);
		values.put(DBStructure.COLUMN_INSTALLED_NAME, name);
		try{
			database.insert(DBStructure.TABLE_INSTALLED, null, values);	
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public void deleteInstalled(String apkid) {
		try{
			database.delete(DBStructure.TABLE_INSTALLED, DBStructure.COLUMN_INSTALLED_APKID+"=?", new String[]{apkid});
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public Cursor getSearch(String query) {
		Cursor c = null;
		try{
			c= database.query(DBStructure.TABLE_APK, new String[]{DBStructure.COLUMN_APK_NAME,DBStructure.COLUMN_APK_ID,DBStructure.COLUMN_APK_ICON,DBStructure.COLUMN_APK_VERNAME,DBStructure.COLUMN_APK_REPO_ID,DBStructure.COLUMN_APK_RATING,DBStructure.COLUMN_APK_DOWNLOADS,}, DBStructure.COLUMN_APK_NAME+" LIKE '%"+query+"%' OR "+DBStructure.COLUMN_APK_APKID+" LIKE '%"+query+"%'", null, null, null, DBStructure.COLUMN_APK_NAME);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return c;
	}

	public int getApkId(String apkid) {
		Cursor c = null;
		try{
			c = database.query(DBStructure.TABLE_APK, new String[]{DBStructure.COLUMN_APK_ID}, DBStructure.COLUMN_APK_APKID+"=?", new String[]{apkid}, null, null, null);
			c.moveToFirst();
			if(c.getCount()>0){
				return c.getInt(0);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public int getOldApkId(String apkid, String vername) {
		Cursor c = null;
		try{
			c = database.query(DBStructure.TABLE_OLD, new String[]{DBStructure.COLUMN_APK_ID}, DBStructure.COLUMN_APK_OLD_APKID+"=? and "+DBStructure.COLUMN_APK_OLD_VERNAME+"=?", new String[]{apkid,vername}, null, null, null);
			c.moveToFirst();
			if(c.getCount()>0){
				return c.getInt(0);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public void setRepoInUse(long repo_id, int i) {
		ContentValues values = new ContentValues();
		values.put(DBStructure.COLUMN_REPOS_INUSE,i);
		try{
			database.update(DBStructure.TABLE_REPOS, values, DBStructure.COLUMN_REPOS_ID+"=?", new String[]{repo_id+""});
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public String[] getLogin(String srv) {
		String[] login = new String[2];
		try{
			Cursor c = database.query(DBStructure.TABLE_REPOS, new String[]{DBStructure.COLUMN_REPOS_USER,DBStructure.COLUMN_REPOS_PASSWORD}, DBStructure.COLUMN_REPOS_URI+"=?", new String[]{srv}, null, null, null);
			c.moveToFirst();
			if(c.getCount()!=0){
				login[0]=c.getString(0);
				login[1]=c.getString(1);
			}
			c.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		if(login[0]==null){
			login[0]="";
			login[1]="";
		}
		
		return login;
	}

	public Integer getRepoId(String repoName) {
		Cursor c = null;
		int ret_int = 0;
		try{
			c= database.query(DBStructure.TABLE_REPOS, new String[]{DBStructure.COLUMN_REPOS_ID}, DBStructure.COLUMN_REPOS_URI+"=?", new String[]{repoName}, null, null, null);
			c.moveToFirst();
			ret_int = c.getInt(0);
		}catch(Exception e){
			e.printStackTrace();
		}
		c.close();
		return ret_int;
	}

	public void addLogin(String username, String password, String new_repo) {
		ContentValues values = new ContentValues();
		values.put(DBStructure.COLUMN_REPOS_USER, username);
		values.put(DBStructure.COLUMN_REPOS_PASSWORD, password);
		
		database.update(DBStructure.TABLE_REPOS, values, DBStructure.COLUMN_REPOS_URI+"=?", new String[]{new_repo});
	}

	public void disableLogin(String new_repo) {
		ContentValues values = new ContentValues();
		values.put(DBStructure.COLUMN_REPOS_USER, "");
		values.put(DBStructure.COLUMN_REPOS_PASSWORD, "");
		database.update(DBStructure.TABLE_REPOS, values, DBStructure.COLUMN_REPOS_URI+"=?", new String[]{new_repo});
	}
	
	public Cursor getScheduledDownloads(){
		Cursor c = null;
		try{
			c = database.query(DBStructure.TABLE_SCHEDULED, null, null, null, null, null, null);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return c;
	}

	public void insertScheduledDownload(long id, String apkid, String name,String iconpath,
			String vername, long repo_id) {
		ContentValues values = new ContentValues();
		values.put(DBStructure.COLUMN_SCHEDULED_ID, id);
		values.put(DBStructure.COLUMN_SCHEDULED_APKID, apkid);
		values.put(DBStructure.COLUMN_SCHEDULED_NAME, name);
		values.put(DBStructure.COLUMN_SCHEDULED_VERNAME, vername);
		values.put(DBStructure.COLUMN_SCHEDULED_REPO_ID, repo_id);
		values.put(DBStructure.COLUMN_SCHEDULED_ICONSPATH, iconpath);
		try{
			database.insert(DBStructure.TABLE_SCHEDULED, null, values);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void insertScheduledDownload(String apkid, String name,String iconpath,
			String vername, long repo_id) {
		ContentValues values = new ContentValues();
		values.put(DBStructure.COLUMN_SCHEDULED_APKID, apkid);
		values.put(DBStructure.COLUMN_SCHEDULED_NAME, name);
		values.put(DBStructure.COLUMN_SCHEDULED_VERNAME, vername);
		values.put(DBStructure.COLUMN_SCHEDULED_REPO_ID, repo_id);
		values.put(DBStructure.COLUMN_SCHEDULED_ICONSPATH, iconpath);
		try{
			database.insert(DBStructure.TABLE_SCHEDULED, null, values);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getInstalledVercode(String apkid) {
		Cursor c = null;
		String vercode = "-1";
		try{
			c = database.query(DBStructure.TABLE_INSTALLED, new String[]{DBStructure.COLUMN_INSTALLED_VERCODE}, DBStructure.COLUMN_INSTALLED_APKID+"=?", new String[]{apkid}, null, null, null);
			c.moveToFirst();
			if(c.getCount()!=0){
				vercode = c.getString(0);
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		c.close();
		return vercode;
	}

	public Cursor getOldApk(long id) {
		Cursor c = null;
		try{
			c= database.query(DBStructure.TABLE_OLD, null, DBStructure.COLUMN_APK_OLD_ID+"=?", new String[]{id+""}, null, null, null);
			c.moveToFirst();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return c;
	}

	public void deleteScheduledDownload(String apkid, String vername) {
		System.out.println(apkid + " "+vername);
		try{
			int i = database.delete(DBStructure.TABLE_SCHEDULED, DBStructure.COLUMN_SCHEDULED_APKID+"=? and "+DBStructure.COLUMN_SCHEDULED_VERNAME+"=?", new String[]{apkid,vername});
			if(i==0){
				i = database.delete(DBStructure.TABLE_SCHEDULED, DBStructure.COLUMN_SCHEDULED_APKID+"=?", new String[]{apkid});
			}
			System.out.println("Removed: "+i+" entries");
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getExtendedServer(long repo_id) {
		int return_int = 0;
		Cursor c = null;
		try{
			c= database.query(DBStructure.TABLE_REPOS, new String[]{DBStructure.COLUMN_REPOS_EXTENDED}, DBStructure.COLUMN_REPOS_ID+"=?", new String[]{repo_id+""}, null, null, null);
			c.moveToFirst();
			return_int=c.getInt(0);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return return_int;
	}

	public void setExtended(String repo) {
		ContentValues values = new ContentValues();
		values.put(DBStructure.COLUMN_REPOS_EXTENDED, true);
		database.update(DBStructure.TABLE_REPOS, values, DBStructure.COLUMN_REPOS_URI+"=?", new String[]{repo});
	}

	public void disableExtended(String repo) {
		ContentValues values = new ContentValues();
		values.put(DBStructure.COLUMN_REPOS_EXTENDED, false);
		database.update(DBStructure.TABLE_REPOS, values, DBStructure.COLUMN_REPOS_URI+"=?", new String[]{repo});
	}

	public String getApkName(String packageName) {
		String return_string = null;
		Cursor c = null;
		try{
			c = database.query(DBStructure.TABLE_APK, new String []{DBStructure.COLUMN_APK_NAME}, DBStructure.COLUMN_APK_APKID+"=?", new String[]{packageName}, null, null, null);
			c.moveToFirst();
			return_string = c.getString(0);
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			c.close();
		}
		if(return_string==null){
			return_string=packageName;
		}
		return return_string;
		
	}
	
	public String getFeaturedApkName(String packageName) {
		String return_string = null;
		Cursor c = null;
		try{
			c = database.query(DBStructure.TABLE_EDITORSCHOICE, new String []{DBStructure.COLUMN_APK_NAME}, DBStructure.COLUMN_APK_APKID+"=?", new String[]{packageName}, null, null, null);
			c.moveToFirst();
			return_string = c.getString(0);
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			c.close();
		}
		if(return_string==null){
			return_string=packageName;
		}
		return return_string;
		
	}

	public Cursor getFeaturedApk(long id) {
		Cursor c = null;
		System.out.println(id);
		try{
			c= database.query(DBStructure.TABLE_EDITORSCHOICE, null, DBStructure.COLUMN_APK_ID+"=?", new String[]{id+""}, null, null, null);
			c.moveToFirst();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return c;
	}

	public String getEditorsChoiceRepoName(long repo_id) {
		Cursor c = null;
		String return_string = null;
		try {
			c = database.query(DBStructure.TABLE_EDITORSCHOICEREPOS, new String[]{DBStructure.COLUMN_EDITORSCHOICEREPO_NAME}, DBStructure.COLUMN_EDITORSCHOICEREPO_ID+"=?", new String[]{repo_id+""}, null, null, null);
			c.moveToFirst();
			return_string = c.getString(0);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			c.close();
		}
		return return_string;
	}

	public long insertEditorChoiceRepo(Repository repository) {
		ContentValues values = new ContentValues();
		if(repository.id!=null){
			values.put(DBStructure.COLUMN_EDITORSCHOICEREPO_ID, repository.id);
		}
		
		if(repository.hash!=null){
			values.put(DBStructure.COLUMN_EDITORSCHOICEREPO_HASH, repository.hash);
		}
		values.put(DBStructure.COLUMN_EDITORSCHOICEREPO_BASEPATH, repository.basepath);
		values.put(DBStructure.COLUMN_EDITORSCHOICEREPO_FEATUREDGRAPHICPATH, repository.featuregraphicpath);
		values.put(DBStructure.COLUMN_EDITORSCHOICEREPO_ICONSPATH, repository.iconspath);
		values.put(DBStructure.COLUMN_EDITORSCHOICEREPO_SCREENSPATH, repository.screenspath);
		values.put(DBStructure.COLUMN_EDITORSCHOICEREPO_NAME, repository.name);
		
		return database.insert(DBStructure.TABLE_EDITORSCHOICEREPOS, null, values);
	}

	public long insertEditorsChoice(Apk apk) {
		ContentValues values = new ContentValues();
		values.put(DBStructure.COLUMN_APK_APKID, apk.apkid);
		values.put(DBStructure.COLUMN_APK_NAME, apk.name);
		values.put(DBStructure.COLUMN_APK_VERNAME, apk.vername);
		values.put(DBStructure.COLUMN_APK_VERCODE, apk.vercode);
		values.put(DBStructure.COLUMN_APK_DOWNLOADS, apk.downloads);
		values.put(DBStructure.COLUMN_APK_RATING, apk.stars);
		values.put(DBStructure.COLUMN_APK_AGE, apk.age);
		values.put(DBStructure.COLUMN_APK_SIZE, apk.size);
		values.put(DBStructure.COLUMN_APK_MD5, apk.md5);
		values.put(DBStructure.COLUMN_APK_PATH, apk.path);
		values.put(DBStructure.COLUMN_APK_ICON, apk.icon);
		values.put(DBStructure.COLUMN_APK_DATE, apk.date);
		values.put(DBStructure.COLUMN_APK_SDK, apk.minSdk);
		values.put(DBStructure.COLUMN_APK_SCREEN, apk.minScreenSize);
		values.put(DBStructure.COLUMN_APK_REPO_ID, apk.repo_id);
		values.put(DBStructure.COLUMN_EDITORSCHOICE_FEATUREDGRAPHIC, apk.featuregraphic);
		values.put(DBStructure.COLUMN_EDITORSCHOICE_HIGHLIGHT, apk.highlighted);
		return database.insert(DBStructure.TABLE_EDITORSCHOICE, null, values);
		
		
		
		
	}

	public void deleteEditorsChoice() {
		database.delete(DBStructure.TABLE_EDITORSCHOICE, DBStructure.COLUMN_APK_REPO_ID+">0", null);
		database.delete(DBStructure.TABLE_SCREENSHOTS, DBStructure.COLUMN_SCREENSHOTS_REPO_ID+">0", null);
		database.delete(DBStructure.TABLE_EDITORSCHOICEREPOS, DBStructure.COLUMN_EDITORSCHOICEREPO_ID+">0", null);
	}

	public ArrayList<HashMap<String, String>> getFeaturedGraphics() {
		Cursor c = null;
		ArrayList<HashMap<String, String>> items = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> item = null;
		try{
			c = database.rawQuery("select a._id, featuredgraphicpath,featuredgraphic from editorschoice as a ,editorschoicerepo as b where a.repo_id=b._id and b._id>0 and a.highlighted is null",null);
			for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){
				item = new HashMap<String, String>();
				item.put("id", c.getString(0));
				item.put("url", c.getString(1)+c.getString(2));
				items.add(item);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			c.close();
		}
		return items;
	}

	public ArrayList<HashMap<String, String>> getTopApps() {
		ArrayList<HashMap<String, String>> values = new ArrayList<HashMap<String, String>>();
		Cursor c = null;
		try{
			c=database.query(DBStructure.TABLE_EDITORSCHOICE, new String[]{DBStructure.COLUMN_APK_NAME,DBStructure.COLUMN_APK_ICON,DBStructure.COLUMN_APK_RATING,DBStructure.COLUMN_APK_ID,DBStructure.COLUMN_APK_AGE}, DBStructure.COLUMN_APK_REPO_ID+"=?", new String[]{"-1"}, null, null, null);
			HashMap<String, String> item = null;
			boolean mature = sPref.getString("app_rating", "All").equals("Mature");
			for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){
				item = new HashMap<String, String>();
				item.put("name", c.getString(0));
				item.put("icon", c.getString(1));
				item.put("rating", c.getString(2));
				item.put("id", c.getString(3));
				if(mature){
					values.add(item);
				}else{
					if(c.getString(4).equals("0")){
						values.add(item);
					}
				}
				
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			c.close();
		}
		
		return values;
	}

	public boolean verifyTopAppsHash(String string) {
		Cursor c = null;
		try{
			c = database.query(DBStructure.TABLE_EDITORSCHOICEREPOS, new String[]{DBStructure.COLUMN_EDITORSCHOICEREPO_HASH}, DBStructure.COLUMN_EDITORSCHOICEREPO_ID+"=?", new String[]{"-1"}, null, null, null);
			c.moveToFirst();
			if(c.getCount()!=0&&string.equals(c.getString(0))){
				return true;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			c.close();
		}
		return false;
	}

	public void deleteTopApps() {
		database.delete(DBStructure.TABLE_EDITORSCHOICE, DBStructure.COLUMN_APK_OLD_REPO_ID+"=-1", null);
		database.delete(DBStructure.TABLE_EDITORSCHOICEREPOS, DBStructure.COLUMN_EDITORSCHOICEREPO_ID+"=-1", null);
		database.delete(DBStructure.TABLE_SCREENSHOTS, DBStructure.COLUMN_SCREENSHOTS_REPO_ID+"=-1", null);
	}

	public String getTopAppsIconPath() {
		Cursor c = null;
		String return_string = null;
		try{
			c = database.query(DBStructure.TABLE_EDITORSCHOICEREPOS, new String[]{DBStructure.COLUMN_EDITORSCHOICEREPO_ICONSPATH}, DBStructure.COLUMN_EDITORSCHOICEREPO_ID+"=?", new String[]{"-1"}, null, null, null);
			c.moveToFirst();
			return_string = c.getString(0);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return return_string;
	}

	public HashMap<String,String> getHighLightFeature() {
		Cursor c = null;
		HashMap<String,String> return_string = null;
		try{
			c = database.rawQuery("select a._id,featuredgraphicpath,featuredgraphic from editorschoice as a ,editorschoicerepo as b where a.repo_id=b._id and b._id>0 and a.highlighted is not null",null);
			c.moveToFirst();
			if(c.getCount()!=0){
				return_string = new HashMap<String, String>();
				return_string.put("url",c.getString(1)+c.getString(2));
				return_string.put("id",c.getString(0));
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return return_string;
	}

	public void inserFeaturedScreenshots(Apk apk) {
		ContentValues values = new ContentValues();
		for(int i =0; i!=apk.screenshots.size();i++){
			values.put(DBStructure.COLUMN_SCREENSHOTS_APKID, apk.id);
			values.put(DBStructure.COLUMN_SCREENSHOTS_SCREENSHOT, apk.screenshots.get(i));
			values.put(DBStructure.COLUMN_SCREENSHOTS_REPO_ID, apk.repo_id);
			database.insert(DBStructure.TABLE_SCREENSHOTS, null, values);
			values.clear();
		}
	}

	public String getFeaturedIconspath(long repo_id) {
		String basepath = null;
		Cursor c=null;
		try{
		c = database.query(DBStructure.TABLE_EDITORSCHOICEREPOS, new String[]{DBStructure.COLUMN_REPOS_ICONSPATH}, DBStructure.COLUMN_REPOS_ID+"=?", new String[]{repo_id+""}, null, null, null);
		
		c.moveToFirst();
		basepath=c.getString(0);
		}catch (Exception e) {
			
		}
		c.close();
		return basepath;
	}

	public ArrayList<String> getFeaturedScreenhots(long id,long repo_id) {
		ArrayList<String> urls = new ArrayList<String>();
		Cursor c = null;
		
		try{
			c = database.query(DBStructure.TABLE_SCREENSHOTS, new String[]{DBStructure.COLUMN_SCREENSHOTS_SCREENSHOT}, DBStructure.COLUMN_SCREENSHOTS_APKID+"=?", new String[]{id+""}, null, null, null);
			for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){
				urls.add(getFeaturedScreenhotsPath(repo_id)+c.getString(0));
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			c.close();
		}
		
		
		return urls;
	}

	private String getFeaturedScreenhotsPath(long repo_id) {
		String return_string = null;
		Cursor c = null;
		try{
			c=database.query(DBStructure.TABLE_EDITORSCHOICEREPOS, new String[]{DBStructure.COLUMN_EDITORSCHOICEREPO_SCREENSPATH}, DBStructure.COLUMN_EDITORSCHOICEREPO_ID+"=?", new String[]{repo_id+""}, null, null, null);
			c.moveToFirst();
			return_string=c.getString(0);
			
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return return_string;
	}

	public void insertFailedDownload(HashMap<String, String> hashMap) {
		ContentValues values = new ContentValues();
		values.put(DBStructure.COLUMN_FAILED_DOWNLOADS_REMOTEPATH,hashMap.get("remotePath"));
		values.put(DBStructure.COLUMN_FAILED_DOWNLOADS_MD5,hashMap.get("md5sum"));
		values.put(DBStructure.COLUMN_FAILED_DOWNLOADS_PACKAGENAME,hashMap.get("packageName"));
		values.put(DBStructure.COLUMN_FAILED_DOWNLOADS_APPNAME,hashMap.get("appName"));
		values.put(DBStructure.COLUMN_FAILED_DOWNLOADS_SIZE,hashMap.get("intSize"));
		values.put(DBStructure.COLUMN_FAILED_DOWNLOADS_VERSION,hashMap.get("version"));
		values.put(DBStructure.COLUMN_FAILED_DOWNLOADS_LOCALPATH,hashMap.get("localPath"));
		hashMap.get("isUpdate");
		if(hashMap.containsKey("username")){
			values.put(DBStructure.COLUMN_FAILED_DOWNLOADS_USERNAME,hashMap.get("username"));
			values.put(DBStructure.COLUMN_FAILED_DOWNLOADS_PASSWORD,hashMap.get("password"));
		}
		
		database.insert(DBStructure.TABLE_FAILED_DOWNLOADS, null, values);
		
	}
	
	public ArrayList<HashMap<String, String>> getFailedDownloads(){
		ArrayList<HashMap<String, String>> values = new ArrayList<HashMap<String, String>>();
		Cursor c = null;
		try {
			c = database.query(DBStructure.TABLE_FAILED_DOWNLOADS, null, null, null, null, null, null);
			for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){
				HashMap<String, String> value = new HashMap<String, String>();
				value.put("remotePath", c.getString(c.getColumnIndex(DBStructure.COLUMN_FAILED_DOWNLOADS_REMOTEPATH)));
				value.put("md5sum", c.getString(c.getColumnIndex(DBStructure.COLUMN_FAILED_DOWNLOADS_MD5)));
				value.put("packageName", c.getString(c.getColumnIndex(DBStructure.COLUMN_FAILED_DOWNLOADS_PACKAGENAME)));
				value.put("appName", c.getString(c.getColumnIndex(DBStructure.COLUMN_FAILED_DOWNLOADS_APPNAME)));
				value.put("intSize", c.getString(c.getColumnIndex(DBStructure.COLUMN_FAILED_DOWNLOADS_SIZE)));
				value.put("version", c.getString(c.getColumnIndex(DBStructure.COLUMN_FAILED_DOWNLOADS_VERSION)));
				value.put("localPath", c.getString(c.getColumnIndex(DBStructure.COLUMN_FAILED_DOWNLOADS_LOCALPATH)));
				value.put("username", c.getString(c.getColumnIndex(DBStructure.COLUMN_FAILED_DOWNLOADS_USERNAME)));
				value.put("password", c.getString(c.getColumnIndex(DBStructure.COLUMN_FAILED_DOWNLOADS_PASSWORD)));
				if(value.get("username")!=null){
					value.put("loginRequired","true");
				}
				values.add(value);
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return values;
		
	}

	public void deleteFailedDownloads() {
		database.delete(DBStructure.TABLE_FAILED_DOWNLOADS, null, null);
	}

	public boolean verifyEditorsChoiceHash(String string) {
		Cursor c = null;
		try{
			c = database.rawQuery("select _id from editorschoicerepo where hash ='"+string.trim()+"'", null);
			c.moveToFirst();
			System.out.println(c.getCount() + " getcount " +string);
			return c.getCount()!=0;
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			c.close();
		}
		return false;
	}
	
	
	
	
	
	
}

