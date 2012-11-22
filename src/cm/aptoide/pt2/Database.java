/*******************************************************************************
 * Copyright (c) 2012 rmateus.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package cm.aptoide.pt2;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;
import cm.aptoide.pt2.Server.State;
import cm.aptoide.pt2.views.ViewApk;
import cm.aptoide.pt2.views.ViewApkFeatured;
import cm.aptoide.pt2.views.ViewLogin;

public class Database {
	public static SQLiteDatabase database = null;
	private static DbOpenHelper dbhandler ;
	private int i=0;
	private static Database db;
	static Context context;
	
	private HashMap<String,Integer> categories1 = new HashMap<String, Integer>();
	private HashMap<String,Integer> categories2 = new HashMap<String, Integer>();
	
	private Database(Context context) {
		dbhandler = new DbOpenHelper(context); 
		database = dbhandler.getWritableDatabase();
		Database.context=context;
	}
	
	public static Database getInstance(Context context){
		if(db==null){
			db = new Database(context);
		}
			return db;
		}
		
	
	
	public void insert(ViewApk apk) {
		
		try{
			

			
			ContentValues values = new ContentValues();
			insertCategories(apk);
			values.put("apkid", apk.getApkid());
			values.put("imagepath", apk.getIconPath());
			values.put("name", apk.getName());
			values.put("size", apk.getSize());
			values.put("downloads", apk.getDownloads());
			values.put("vername", apk.getVername());
			values.put("vercode", apk.getVercode());
			values.put("md5", apk.getMd5());
			values.put("repo_id", apk.getRepo_id());
			values.put("date",apk.getDate());
			values.put("category2", categories2.get(apk.getCategory2()));
			values.put("rating", apk.getRating());
			values.put("path", apk.getPath());
			values.put("minscreen", apk.getMinScreen());
			values.put("minsdk", apk.getMinSdk());
			values.put("mingles", apk.getMinGlEs());
			values.put("mature", apk.getAge());
			database.insert("apk", null, values);
			i++;
			if(i%300==0){
				Intent i = new Intent("update");
				i.putExtra("server", apk.getRepo_id());
				context.sendBroadcast(i);
			}
			database.yieldIfContendedSafely();
			
		} catch (Exception e){
			e.printStackTrace();
		}finally{
			
		}
		
	}
	
	public void close(){
		database.close();
	}
	
	private void insertCategories(ViewApk apk) {
		ContentValues values = new ContentValues();
		values.put("name", apk.getCategory1());
		database.insert("category1", null, values);
		
		if(categories1.get(apk.getCategory1())==null){
			Cursor c = database.query("category1", new String[]{"_id"}, "name = ?", new String[]{apk.getCategory1()}, null,null, null);
			c.moveToFirst();
			categories1.put(apk.getCategory1(),c.getInt(0));
			c.close();
		}
		
		
		if(apk.getCategory1().equals("Other")){
			System.out.println(apk.getApkid());
		}
		
		values.clear();
		values.put("repo_id", apk.getRepo_id());
		values.put("catg1_id", categories1.get(apk.getCategory1()));
		database.insert("repo_category1", null, values);
		values.clear();
		values.put("catg1_id", categories1.get(apk.getCategory1()));
		values.put("name", apk.getCategory2());
		database.insert("category2", null, values);
		
		
		if(categories2.get(apk.getCategory2())==null){
			Cursor c = database.query("category2", new String[]{"_id"}, "name = ?", new String[]{apk.getCategory2()}, null,null, null);
			c.moveToFirst();
			categories2.put(apk.getCategory2(),c.getInt(0));
			c.close();
		}
		
		
		values.clear();
		values.put("repo_id", apk.getRepo_id());
		values.put("catg2_id", categories2.get(apk.getCategory2()));
		database.insert("repo_category2", null, values);
	}
	
	private void insertDynamicCategories(ViewApk apk) {
		ContentValues values = new ContentValues();
		values.put("name", apk.getCategory1());
		database.insert("category1", null, values);
		if(categories1.get(apk.getCategory1())==null){
			Cursor c = database.query("category1", new String[]{"_id"}, "name = ?", new String[]{apk.getCategory1()}, null,null, null);
			c.moveToFirst();
			categories1.put(apk.getCategory1(),c.getInt(0));
			c.close();
		}
		
		values.clear();
		values.put("repo_id", apk.getRepo_id());
		values.put("catg1_id", categories1.get(apk.getCategory1()));
		database.insert("repo_category1", null, values);
	}

	public void startTransation() {
		context.sendBroadcast(new Intent("status"));
		database.beginTransaction();
	}
	
	public void endTransation(Server server){
		Intent intent = new Intent("status");
		intent.putExtra("server", server.url);
		
		if(i!=0 && server.id>0){
			context.sendBroadcast(intent);
			intent.setAction("update");
			context.sendBroadcast(intent);
		}
		intent.setAction("complete");
		context.sendBroadcast(intent);
		
		
		database.setTransactionSuccessful();
		database.endTransaction();
	}

	public Cursor getApps(long category2_id, long store, boolean mergeStores, Order order, boolean allApps) {
		Cursor c = null;
		SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(context);
		try{
			
			String order_string = "";
			switch (order) {
			
			case NAME:
				order_string = "order by a.name collate nocase";
				break;
			case DATE:
				order_string = "order by date desc";
				break;
			case DOWNLOADS:
				order_string = "order by a.downloads desc";
				break;
			case RATING:
				order_string = "order by rating desc";
				break;
			default:
				break;
			}
			
			String filter = "";
			if(sPref.getBoolean("hwspecsChkBox", true)){
				filter = filter +" and minscreen <= " + HWSpecifications.getScreenSize(context) +
						" and minsdk <=  " + HWSpecifications.getSdkVer() +
						" and mingles <= " +HWSpecifications.getEsglVer(context);
			}
			
			if(sPref.getBoolean("matureChkBox", false)){
				filter = filter + " and mature <= 0"; 
			}
			
			if(allApps){
				if(mergeStores){
					
					c = database.rawQuery("select a._id as _id, a.name, a.vername, a.repo_id, a.imagepath as imagepath, a.rating, a.downloads, a.apkid as apkid, a.vercode as vercode, c.iconspath as iconspath from apk as a, repo as c where vercode = (select max(vercode) from apk as b where a.apkid=b.apkid) and a.repo_id = c._id "+filter+" group by apkid "+order_string,null);
					
				}else{
					
					c = database.rawQuery("select a._id as _id, a.name, a.vername, a.repo_id, a.imagepath as imagepath, a.rating, a.downloads, a.apkid as apkid, a.vercode as vercode, c.iconspath as iconspath from apk as a, repo as c where repo_id = ? and vercode in (select vercode from apk as b where a.apkid=b.apkid order by vercode asc) and a.repo_id = c._id "+filter+" group by apkid "+order_string,new String[]{store+""});
				}
				
			}else{
				if(mergeStores){
					
					c = database.rawQuery("select a._id as _id, a.name, a.vername, a.repo_id, a.imagepath as imagepath, a.rating, a.downloads,a.apkid as apkid, a.vercode as vercode, c.iconspath as iconspath from apk as a, repo as c where category2 = ? and vercode = (select max(vercode) from apk as b where a.apkid=b.apkid) and a.repo_id = c._id "+filter+" group by apkid "+order_string,new String[]{category2_id+""});
					
				}else{
					
					c = database.rawQuery("select a._id as _id, a.name, a.vername, a.repo_id, a.imagepath as imagepath, a.rating, a.downloads, a.apkid as apkid, a.vercode as vercode, c.iconspath as iconspath from apk as a, repo as c where repo_id = ? and category2 = ? and vercode in (select vercode from apk as b where a.apkid=b.apkid order by vercode asc) and a.repo_id = c._id "+filter+" group by apkid "+order_string,new String[]{store+"",category2_id+""});
					
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		
		return c;
	}

	public Cursor getCategory1(long store, boolean mergeStores, boolean allApps) {
		Cursor d = null;
			
			
			
		if(mergeStores){
			d = database.rawQuery("select a._id,a.name from category1 as a order by a._id",null);
		}else{
			d = database.rawQuery("select a._id,a.name from category1 as a , repo_category1 as b where a._id=b.catg1_id and b.repo_id = ? order by a._id", new String[]{store+""});
		}
		
		System.out.println("Getting category1 " + store);
		
		MatrixCursor c = new MatrixCursor(new String[]{"_id","name"});
		ArrayList<Holder> a = new ArrayList<Database.Holder>();
		
		for(d.moveToFirst();!d.isAfterLast();d.moveToNext()){
			if(d.getString(1).equals("Top Apps")|| d.getString(1).equals("Latest Apps")){
				Holder holder = new Holder();
				holder.id=d.getLong(0);
				holder.name=d.getString(1);
				a.add(holder);
			}else if(!allApps){
				c.addRow(new Object[]{d.getString(0),d.getString(1)});
			}
		}
		boolean hasCategory = false;
		
		if(!mergeStores&&!allApps && !db.getServer(store, false).state.equals(State.PARSED)&&!db.getServer(store, false).state.equals(State.FAILED)){
			for(d.moveToFirst();!d.isAfterLast();d.moveToNext()){
				if(d.getString(1).equals("Applications")||d.getString(1).equals("Games")){
					hasCategory = true;
					break;
				}
			}
		}else{
			hasCategory=true;
		}
		if(!hasCategory){
			c.newRow().add(-10).add("Applications");
			c.newRow().add(-10).add("Games");
		}
		Collections.sort(a);
		
		if(allApps){
			c.addRow(new Object[]{-4,"All Applications"});
		}
		
		for(Holder holder : a){
			c.addRow(new Object[]{holder.id,holder.name});
		}
		
		if(!mergeStores){
			c.addRow(new Object[]{-2,"Latest Comments"});
			c.addRow(new Object[]{-1,"Latest Likes"});
		}
		
		c.addRow(new Object[]{-3,"Recommended for you"});
		
		d.close();
		
		return c;
	}
	
	static class Holder implements Comparable<Holder>{
		long id;
		String name;
		
		@Override
		public int compareTo(Holder another) {
			return another.name.compareTo(name);
		}
	}
	
	public void insertUserBasedApk(ViewApk apk){
		ContentValues values = new ContentValues();
		
		values.put("apkid", apk.getApkid());
		values.put("vercode", apk.getVercode());
		
		database.insert("userbasedapk", null, values);
	}
	
	public Cursor getUserBasedApk(long repo_id){
		
		Cursor c = null;
		
		try {
			c = database.rawQuery("select a._id, a.name, a.vername, a.repo_id, a.imagepath as imagepath, a.rating, a.downloads, a.apkid, a.vercode, c.iconspath as iconspath from apk a,itembasedapk b, itembasedapkrepo as c where a.repo_id = ? and b.parent_apkid = 'recommended' and a.apkid=b.apkid and a.vercode=b.vercode and b.itembasedapkrepo_id=c._id", new String[]{repo_id+""});
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		return c;
	}
	
	public Cursor getCategory2(long l,long store, boolean mergeStores) {
		Cursor c =  null;
		if(mergeStores){
			c = database.rawQuery("select a._id,a.name from category2 as a where a.catg1_id = ? order by a.name", new String[]{l+""});
		}else{
			c = database.rawQuery("select a._id,a.name from category2 as a , repo_category2 as b where a._id=b.catg2_id and a.catg1_id = ? and b.repo_id = ? order by a.name", new String[]{l+"",store+""});
		}
		System.out.println("Getting category2: "+ l + " store: " + store);
		return c;
	}

	public Server getServer(String uri) {
		Server server = null;
		Cursor c = null;
		
		try {
			c = database.query("repo", new String[]{"_id, url, delta , username, password"}, "url = ?", new String[]{uri}, null, null, null);
			if(c.moveToFirst()){
				server = new Server(c.getString(1),c.getString(2),c.getLong(0));
				ViewLogin login = new ViewLogin(c.getString(3), c.getString(4));
				server.setLogin(login);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			c.close();
		}
		
		return server;
	}
	
	public Server getServer(long id,boolean top) {
		Server server = null;
		Cursor c = null;
		
		try {
			if(top){
				c = database.query("toprepo_extra", new String[]{"_id, url, top_delta, status, username, password"}, "_id = ?", new String[]{id+""}, null, null, null);
			}else{
				c = database.query("repo", new String[]{"_id, url, delta, status, username, password"}, "_id = ?", new String[]{id+""}, null, null, null);
			}
			
			if(c.moveToFirst()){
				server = new Server(c.getString(1),c.getString(2),c.getLong(0));
				server.state=State.valueOf(c.getString(3));
				ViewLogin login = new ViewLogin(c.getString(3), c.getString(4));
				server.setLogin(login);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			c.close();
		}
		
		return server;
	}
	
	public void deleteTopApps(long id, Category category){
		switch (category) {
		case TOP:
			if(categories1.get("Top Apps")!=null){
				database.delete("dynamic_apk", "repo_id = ? and category1 = ?", new String[]{id+"",categories1.get("Top Apps")+""});
				
			}
			break;
		case LATEST:
			if(categories1.get("Latest Apps")!=null){
				database.delete("dynamic_apk", "repo_id = ? and category1 = ?", new String[]{id+"",categories1.get("Latest Apps")+""});
			}
			break;
		default:
			break;
		}
		database.delete("screenshots", "type = ?", new String[]{category.ordinal()+""});
		database.delete("toprepo_extra", "_id = ? and category = ?", new String[]{id+"",category.name().hashCode()+""});
	}
	
	public void deleteServer(long id, boolean fromRepoTable){
		ArrayList<String> catg1_deletes = new ArrayList<String>();
		ArrayList<String> catg2_deletes = new ArrayList<String>();
		if(fromRepoTable){
			database.delete("repo", "_id = ?", new String[]{id+""});
			database.delete("toprepo_extra", "_id = ?", new String[]{id+""});
			database.delete("dynamic_apk", "repo_id = ?", new String[]{id+""});
			database.delete("screenshots", "repo_id = ?", new String[]{id+""});
		}
		database.delete("apk", "repo_id = ?", new String[]{id+""});
		if(fromRepoTable){
			database.delete("repo_category1", "repo_id = ?", new String[]{id+""});
			database.delete("repo_category2", "repo_id = ?", new String[]{id+""});
		
		
		Cursor c = database.query("category1", new String[]{"_id"}, null, null, null, null, null);
		Cursor d = null;
		for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){
			
			d = database.query("repo_category1", new String[]{"repo_id"}, "catg1_id=?", new String[]{c.getString(0)}, null, null, null); 
			
			if(d.getCount()==0){
				catg1_deletes.add(c.getString(0));
			}
			
		}
		c = database.query("category2", new String[]{"_id"}, null, null, null, null, null);
		for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){
			d = database.query("repo_category2", new String[]{"repo_id"}, "catg2_id=?", new String[]{c.getString(0)}, null, null, null); 
			if(d.getCount()==0){
				catg2_deletes.add(c.getString(0));
			}
			
		}
		
		for(String s : catg1_deletes){
			database.delete("category1", "_id = ?", new String[]{s});
		}
		
		for(String s : catg2_deletes){
			database.delete("category2", "_id = ?", new String[]{s});
		}
		c.close();
		if(d!=null){
			d.close();
		}
		}
		
	}

	public void addStore(String string, String username, String password) {
		try{
			ContentValues values = new ContentValues();
			values.put("url", string);
			values.put("username", username);
			values.put("password", password);
			database.insert("repo", null, values);
		}catch (Exception e){
			e.printStackTrace();
		}
		
	}

	public Cursor getStores(boolean mergeStores) {
		Cursor c = null;
		try{
			c = database.query("repo", null, "_id > 0", null, null, null, null);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			
		}
		if(mergeStores&&c.getCount()>0){
			MatrixCursor mc = new MatrixCursor(new String[]{"_id","name","avatar","downloads","status"});
			mc.newRow().add(-1).add("All Stores").add("http://imgs.aptoide.com/includes/themes/default/images/repo_default_icon.png").add("").add("");
			c.close();
			return mc;
		}else{
			return c;
		}
		
	}
	
	public String getStoreName(long repo_id){
		String return_string = null;
		Cursor c = null;
		
		try {
			c = database.query("repo", new String[]{"name"}, "_id = ?", new String[]{repo_id+""}, null, null, null);
			
			if(c.moveToFirst()){
				return_string=c.getString(c.getColumnIndex("name"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		
		return return_string;
	}

	public long getServerId(String server) {
		System.out.println(server);
		Cursor c = database.query("repo", new String[]{"_id"}, "url = ?", new String[]{server}, null, null,null);
		c.moveToFirst();
		long return_long = c.getLong(0);
		c.close();
		
		return return_long;
	}

	public String getIconsPath(long repo_id) {
		
		Cursor c = null;
		String path = null;
		try{
			c = database.query("repo", new String[]{"iconspath"}, "_id = ?", new String[]{repo_id+""}, null, null, null);
			c.moveToFirst();
			if(c.isNull(0)){
				path = getBasePath(repo_id,false);
			}else{
				path = c.getString(0);
			}
			
			
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			c.close();
		}
		return path;
	}

	public String getBasePath(long repo_id,boolean top) {
		Cursor c = null;
		String path = null;
		try{
			if(top){
				c = database.query("toprepo_extra", new String[]{"basepath"}, "_id = ?", new String[]{repo_id+""}, null, null, null);
			}else{
				c = database.query("repo", new String[]{"basepath"}, "_id = ?", new String[]{repo_id+""}, null, null, null);
			}
			
			
			c.moveToFirst();
			path = c.getString(0);
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			c.close();
		}
		return path;
	}

	public void insertServerInfo(Server server) {
		
		ContentValues values = new ContentValues();
		if(server.iconsPath!=null){
			values.put("iconspath", server.iconsPath);
		}
		if(server.basePath!=null){
			values.put("basepath", server.basePath);
		}
		if(server.webservicesPath!=null){
			values.put("webservicespath", server.webservicesPath);
		}
		if(server.delta!=null){
			values.put("delta", server.delta);
		}
		if(server.apkPath!=null){
			values.put("apkpath", server.apkPath);
		}
		if(values.size()>0){
			database.update("repo", values,"_id = ?", new String[]{server.id+""});
		}
		
	}

	public void insertInstalled(ViewApk apk) {
		ContentValues values = new ContentValues();
		
		values.put("apkid", apk.getApkid());
		values.put("vercode", apk.getVercode());
		values.put("vername", apk.getVername());
		values.put("name", apk.getName());
		
		database.insert("installed", null, values);
		
	}

	public List<String> getStartupInstalled() {
		
		Cursor c = null;
		List<String> apkids = new ArrayList<String>();
		try{
			c = database.query("installed", new String[]{"apkid"}, null, null, null, null, null);
			
			for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){
				apkids.add(c.getString(0));
			}
			
		}catch (Exception e){
			e.printStackTrace();
		}finally{
			c.close();
		}
		
		return apkids;
	}

	public Cursor getInstalledApps(Order order) {
		Cursor c = null;
		try{
			
			String query = "select b._id as _id, a.name,a.vername,b.repo_id,b.imagepath,b.rating,b.downloads,b.apkid as apkid,b.vercode as vercode, c.iconspath from installed as a, apk as b, repo as c where a.apkid=b.apkid and b.repo_id=c._id group by a.apkid";
			
			
			switch (order) {
			
			case NAME:
				query = query + " order by b.name collate nocase";
				break;
			case DATE:
				query = query + " order by b.date desc";
				break;
			case DOWNLOADS:
				query = query + " order by b.downloads desc";
				break;
			case RATING:
				query = query + " order by b.rating desc";
				break;
			default:
				break;
			}
			
			c = database.rawQuery(query,null);
		}catch (Exception e){
			e.printStackTrace();
		}
		return c;
	}

	public Cursor getUpdates(Order order) {
		Cursor c = null;
		try{
			
			SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(context);
			String filter = "";
			if(sPref.getBoolean("hwspecsChkBox", true)){
				filter = filter +" and minscreen <= " + HWSpecifications.getScreenSize(context) +
						" and minsdk <=  " + HWSpecifications.getSdkVer() +
						" and mingles <= " +HWSpecifications.getEsglVer(context);
			}
			
			if(sPref.getBoolean("matureChkBox", false)){
				filter = filter + " and mature <= 0"; 
			}
			
			String query = "select b._id as _id, b.name,b.vername,b.repo_id,b.imagepath as imagepath,b.rating,b.downloads,b.apkid as apkid,b.vercode as vercode , c.iconspath as iconspath from installed as a, apk as b, repo as c where a.apkid=b.apkid and b.vercode > a.vercode and b.vercode = (select max(vercode) from apk as b where a.apkid=b.apkid) and b.repo_id = c._id"+filter+" group by a.apkid" ;
			
			switch (order) {
			case NAME:
				query = query + " order by b.name collate nocase";
				break;
			case DATE:
				query = query + " order by b.date desc";
				break;
			case DOWNLOADS:
				query = query + " order by b.downloads desc";
				break;
			case RATING:
				query = query + " order by b.rating desc";
				break;
			default:
				break;
			}
			
			c=database.rawQuery(query, null); 
		}catch (Exception e){
			e.printStackTrace();
		}
		return c;
	}

	public void updateStatus(Server server) {
		ContentValues values = new ContentValues();
		if(server.delta!=null){
			values.put("delta", server.delta);
		}
		values.put("status", server.state.name());
		database.update("repo", values, "url =?", new String[]{server.url});
	}

	public long insertTop(ViewApk apk, Category category) {
		long return_long = 0;
		switch (category) {
		case TOP:
			apk.setCategory1("Top Apps");
			break;
		case LATEST:
			apk.setCategory1("Latest Apps");
			break;
		default:
			break;
		}
		try{
			insertDynamicCategories(apk);
			return_long = insertTopApk(apk);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return return_long;
	}

	private long insertTopApk(ViewApk apk) {
		long return_long = 0;
		try{
			ContentValues values = new ContentValues();
			values.put("name", apk.getName());
			values.put("category1", categories1.get(apk.getCategory1()));
			values.put("vername", apk.getVername());
			values.put("repo_id", apk.getRepo_id());
			values.put("apkid", apk.getApkid());
			values.put("md5", apk.getMd5());
			values.put("vercode", apk.getVercode());
			values.put("imagepath", apk.getIconPath());
			values.put("downloads", apk.getDownloads());
			values.put("size", apk.getSize());
			values.put("rating", apk.getRating());
			values.put("path", apk.getPath());
			values.put("minscreen", apk.getMinScreen());
			values.put("minsdk", apk.getMinSdk());
			values.put("mingles", apk.getMinGlEs());
			values.put("mature", apk.getAge());
			return_long = database.insert("dynamic_apk", null, values);
			i++;
//			database.yieldIfContendedSafely();
//			if(i%300==0){
//				Intent i = new Intent("update");
//				i.putExtra("server", apk.getRepo_id());
//				context.sendBroadcast(i);
//			}
			
		}catch (Exception e){
			e.printStackTrace();
		}
		return return_long;
		
	}

	public Cursor getTopApps(long category_id, long store_id, boolean joinStores_boolean) {
		Cursor c = null;
		try{
			String filter="";
			SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(context);
			
			if(sPref.getBoolean("hwspecsChkBox", true)){
				filter = filter +" and minscreen <= " + HWSpecifications.getScreenSize(context) +
						" and minsdk <=  " + HWSpecifications.getSdkVer() +
						" and mingles <= " +HWSpecifications.getEsglVer(context);
			}
			
			if(sPref.getBoolean("matureChkBox", false)){
				filter = filter + " and mature <= 0"; 
			}
			
			if(joinStores_boolean){
				c = database.rawQuery("select a._id as _id, a.name, a.vername, a.repo_id, a.imagepath as imagepath, a.rating, a.downloads, a.apkid as apkid, a.vercode as vercode, c.iconspath as iconspath from dynamic_apk as a, toprepo_extra as c where category1 = ? and a.repo_id = c._id "+filter + " group by a._id",new String[]{category_id+""});
			}else{
				c = database.rawQuery("select a._id as _id, a.name, a.vername, a.repo_id, a.imagepath as imagepath, a.rating, a.downloads, a.apkid as apkid, a.vercode as vercode, c.iconspath as iconspath from dynamic_apk as a, toprepo_extra as c where repo_id = ? and category1 = ? and a.repo_id = c._id "+filter + " group by a._id",new String[]{store_id+"",category_id+""});
			}
			System.out.println("getapps " + "repo_id ="+store_id +  " category " + category_id);
		}catch(Exception e){
			e.printStackTrace();
		}
		return c;
	}

	public void remove(ViewApk apk, Server server) {
		database.delete("apk", "repo_id = ? and apkid=?", new String[]{server.id+"",apk.getApkid()});
	}

	public String getTopIconsPath(long repo_id) {
		Cursor c = null;
		String path = null;
		try{
			c = database.query("toprepo_extra", new String[]{"iconspath"}, "_id = ?", new String[]{repo_id+""}, null, null, null);
			c.moveToFirst();
			if(c.isNull(0)){
				path = getBasePath(repo_id,true);
			}else{
				path = c.getString(0);
			}
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			c.close();
		}
		return path;
	}

	public void insertTopServerInfo(Server server, Category category, boolean featured) {
		ContentValues values = new ContentValues();
		try{
			values.put("screenspath", server.screenspath);
			values.put("_id", server.id);
			values.put("top_delta", server.top_hash);
			values.put("category", category.name().hashCode());
			
			if(featured){
				values.put("url", "http://apps.store.aptoide.com/");
			}else{
				values.put("url", server.url);
			}
			values.put("iconspath", server.iconsPath);
			values.put("basepath",server.basePath);
			values.put("name",server.name);
			database.insert("toprepo_extra", null, values);
			
			
		}catch (Exception e){
			e.printStackTrace();
		}
		
		
	}
	
	public String getTopAppsHash(long id, Category category){
		Cursor c = null;
		String return_string = "";
		try{
			c= database.query("toprepo_extra", new String[]{"top_delta"}, "_id = ? and category = ?", new String[]{id+"",category.name().hashCode()+""}, null, null, null);
			if(c.moveToFirst()){
				return_string=c.getString(0);
			}
		}catch (Exception e){
			e.printStackTrace();
		}finally{
			c.close();
		}
		return return_string;
	}

	public void prepare() {
		i=0;
		categories1.clear();
		categories2.clear();
	}

	public ViewApk getApk(long id, Category category) {
		Cursor c = null;
		ViewApk apk = new ViewApk();
		Log.d("Aptoide-Database","Get APK id:" + id);
		switch (category) {
		case INFOXML:
			c = database.query("apk as a, repo as c", new String[]{"a.apkid","a.vername","a.repo_id","a.downloads","a.size","a.imagepath","a.name","a.rating","a.path","a.md5","c.iconspath","c.name","c.apkpath","a.vercode"}, "a._id = ? and a.repo_id = c._id", new String[]{id+""}, null, null, null);
			break;
		
		case ITEMBASED:
		case USERBASED:
		case EDITORSCHOICE:
			c = database.query("itembasedapk as a, itembasedapkrepo as c", new String[]{"a.apkid","a.vername","a.itembasedapkrepo_id","a.downloads","a.size","a.icon","a.name","a.rating","a.path","a.md5","c.iconspath","c.name","c.basepath","a.vercode"}, "a._id = ? and a.itembasedapkrepo_id = c._id", new String[]{id+""}, null, null, null);
			break;
		case TOP:
		case LATEST:
		case TOPFEATURED:
			c = database.query("dynamic_apk as a, toprepo_extra as c", new String[]{"a.apkid","a.vername","a.repo_id","a.downloads","a.size","a.imagepath","a.name","a.rating","a.path","a.md5","c.iconspath","c.name","c.basepath","a.vercode"}, "a._id = ? and a.repo_id = c._id", new String[]{id+""}, null, null, null);
		default:
			break;
		}
		
		try {
			
			c.moveToFirst();
			apk.setApkid(c.getString(0));
			apk.setVername(c.getString(1));
			apk.setVercode(c.getInt(13));
			apk.setRepo_id(c.getLong(2));
			apk.setDownloads(c.getString(3));
			apk.setSize(c.getString(4));
			apk.setIconPath(c.getString(10) + c.getString(5));
			apk.setName(c.getString(6));
			apk.setRating(c.getString(7));
			apk.setPath(c.getString(12)+c.getString(8));
			apk.setMd5(c.getString(9));
			apk.setRepoName(c.getString(11));
			apk.setId(id);
			
		} catch (Exception e){
			e.printStackTrace();
		} finally{
			c.close();
		}
		return apk;
	}
	
	public Cursor getAllApkVersions(String apkid, long id, String vername,  boolean b, long repo_id) {
		Cursor c = null;
		MatrixCursor mc = new MatrixCursor(new String[]{"_id","apkid","vername","repo_id"});
		try {
			c = database.query("apk", new String[]{"_id", "apkid","vername","repo_id"}, "apkid = ? and repo_id != ?", new String[]{apkid,repo_id+""}, null, null, "vercode desc");
			
			mc.newRow().add(id).add(apkid).add(vername).add(repo_id);
			
			for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){
				mc.newRow().add(c.getString(0)).add(c.getString(1)).add(c.getString(2)).add(c.getString(3));
			}
			
		} catch (Exception e){
			e.printStackTrace();
		} finally{
			c.close();
		}
		return mc;
	}

	public String getWebServicesPath(long repo_id) {
		Cursor c = null;
		String return_string = null;
		try{
			c = database.query("repo", new String[]{"webservicespath"}, "_id = ?", new String[]{repo_id+""}, null, null, null);
			c.moveToFirst();
			if(c.getCount()>0){
				return_string = c.getString(0);
			}else{
				return_string = "http://webservices.aptoide.com/";
			}
		} catch (Exception e){
			e.printStackTrace();
		} finally{
			c.close();
		}
		return return_string;
	}

	public void insertItemBasedApk(Server server, ViewApkFeatured apk, String hashCode,Category category) {
		//insert itembasedapkrepo
		database.beginTransaction();
		try{
			ContentValues values = new ContentValues();
			values.put("iconspath",server.iconsPath);
			values.put("screenspath",server.screenspath);
			values.put("featuredgraphicpath",server.featuredgraphicPath);
			values.put("name", server.url);
			values.put("basepath", server.basePath);
			//insert itembasedapk
			long i = database.insert("itembasedapkrepo", null, values);
			values.clear();
			values.put("apkid", apk.getApkid());
			values.put("vercode", apk.getVercode());
			values.put("vername", apk.getVername());
			values.put("category2",apk.getCategory2());
			values.put("downloads",apk.getDownloads());
			values.put("rating",apk.getRating());
			values.put("path",apk.getPath());
			values.put("size",apk.getSize());
			values.put("md5",apk.getMd5());
			values.put("name", apk.getName());
			if(apk.isHighlighted()){
				values.put("highlight", "true");
			}
			values.put("featuredgraphic", apk.getFeatureGraphic());
			values.put("itembasedapkrepo_id", i);
			values.put("icon",apk.getIconPath());
			values.put("parent_apkid", hashCode);
			apk.setId(i);
			values.put("minscreen", apk.getMinScreen());
			values.put("minsdk", apk.getMinSdk());
			values.put("mingles", apk.getMinGlEs());
			values.put("mature", apk.getAge());
			insertScreenshots(apk, category);
			database.insert("itembasedapk", null, values);
			
		} catch (Exception e){
			e.printStackTrace();
		}
		database.setTransactionSuccessful();
		database.endTransaction();
	}

	public ArrayList<HashMap<String, String>> getItemBasedApks(String apkid) {
		Cursor c = null;
		Cursor d = null;
		ArrayList<HashMap<String, String>> values = new ArrayList<HashMap<String,String>>();
		SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(context);
		String filter = ""; 
		
		
		if(sPref.getBoolean("hwspecsChkBox", true)){
			filter = filter +" and minscreen <= " + HWSpecifications.getScreenSize(context) +
					" and minsdk <=  " + HWSpecifications.getSdkVer() +
					" and mingles <= " +HWSpecifications.getEsglVer(context);
		}
		
		if(sPref.getBoolean("matureChkBox", false)){
			filter = filter + " and mature <= 0"; 
		}
		
		try {
			c = database.query("itembasedapk", new String[]{"name","icon","itembasedapkrepo_id","_id","rating","apkid","vercode"}, "parent_apkid = ? " +filter, 
					new String[]{apkid}, null, null, null);
			
			for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){
				HashMap<String, String> value = new HashMap<String, String>();
				value.put("name", c.getString(0));
				d = database.query("itembasedapkrepo", new String[]{"iconspath"}, "_id = ?", new String[]{c.getString(2)}, null, null, null);
				d.moveToFirst();
				value.put("icon" ,  d.getString(0)+c.getString(1));
				value.put("_id", c.getString(3));
				value.put("rating", c.getString(4));
				value.put("hashCode", (c.getString(5)+"|"+c.getString(6)).hashCode()+"");
				values.add(value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(d!=null){
				d.close();
			}
			if(c!=null){
				c.close();
			}
		}
		
		return values;
	}
	
	public ArrayList<HashMap<String, String>> getItemBasedApksRecommended(String apkid) {
		Cursor c = null;
		Cursor d = null;
		ArrayList<HashMap<String, String>> values = new ArrayList<HashMap<String,String>>();
		SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(context);
		String filter = ""; 
		
		
		if(sPref.getBoolean("hwspecsChkBox", true)){
			filter = filter +" and a.minscreen <= " + HWSpecifications.getScreenSize(context) +
					" and a.minsdk <=  " + HWSpecifications.getSdkVer() +
					" and a.mingles <= " +HWSpecifications.getEsglVer(context);
		}
		
		if(!sPref.getBoolean("matureChkBox", false)){
			filter = filter + " and a.mature <= 0"; 
		}
		
		try {
			c = database.rawQuery("select a.name as name, a.icon as icon, a.itembasedapkrepo_id as itembasedapkrepo_id, a._id as _id, a.rating as _id, a.apkid as apkid, a.vercode as vercode from itembasedapk as a where a.parent_apkid = ? "+filter , new String[]{apkid});
			
			for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){
				HashMap<String, String> value = new HashMap<String, String>();
				value.put("name", c.getString(0));
				d = database.query("itembasedapkrepo", new String[]{"iconspath"}, "_id = ?", new String[]{c.getString(2)}, null, null, null);
				d.moveToFirst();
				value.put("icon" ,  d.getString(0)+c.getString(1));
				value.put("_id", c.getString(3));
				value.put("rating", c.getString(4));
				value.put("hashCode", (c.getString(5)+"|"+c.getString(6)).hashCode()+"");
				values.add(value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(d!=null){
				d.close();
			}
			c.close();
		}
		
		return values;
	}

	public String getItemBasedApksHash(String string) {
		Cursor c = null;
		String return_string = "";
		try {
			c = database.query("itembasedapk_hash", new String[]{"hash"}, "apkid = ?", new String[]{string}, null, null, null);
			if(c.moveToFirst()){
				return_string = c.getString(0);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			c.close();
		}
		return return_string;
	}

	public void insertItemBasedApkHash(String md5hash, String apkid) {
		ContentValues values = new ContentValues();
		values.put("hash",md5hash);
		values.put("apkid", apkid);
		database.insert("itembasedapk_hash", null, values);
	}

	public void addStoreInfo(String avatar, String name, String downloads,
			long id) {
		ContentValues values = new ContentValues();
		if(avatar==null){
			avatar = "";
		}
		if(name==null){
			name = "Unnamed";
		}
		if(downloads==null){
			downloads = "0";
		}
		values.put("avatar", avatar);
		values.put("name", name);
		values.put("downloads", downloads);
		database.update("repo", values, "_id=?", new String[]{id+""});
		
	}

	public void insertScreenshots(ViewApk apk, Category category) {
		ContentValues values = new ContentValues();
		for(String screenshot : apk.getScreenshots()){
			values.clear();
			values.put("path", screenshot);
			values.put("_id", apk.getId());
			values.put("type", category.ordinal());
			values.put("repo_id", apk.getRepo_id());
			database.insert("screenshots", null, values);
		}
		
	}

	public void getScreenshots(ArrayList<String> originalList, ViewApk viewApk, Category category) {
		Cursor c = null;
		String screenspath = db.getScreenshotsPath(viewApk.getRepo_id(),category);
		try {
			c=database.query("screenshots", new String[]{"path"}, "_id = ? and type = ?", new String[]{viewApk.getId()+"", category.ordinal()+""}, null, null, null);
			for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){
				originalList.add(screenspath+c.getString(0));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		
	}

	private String getScreenshotsPath(long repo_id, Category category) {
		String return_string = "";
		Cursor c = null;
		try {
			switch (category) {
			case LATEST:
			case TOP:
				c= database.query("toprepo_extra", new String[]{"screenspath"}, "_id = ? and category = ?", new String[]{repo_id+"",category.name().hashCode()+""}, null, null, null);
				break;
			case ITEMBASED:
				c = database.query("itembasedapkrepo", new String[]{"screenspath"}, "_id=?", new String[]{repo_id+""}, null, null, null);
				break;
				
			default:
				break;
			}
			if(c.moveToFirst()){
				return_string = c.getString(0);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			c.close();
		}
		
		return return_string;
	}

	public ViewApk getItemBasedApk(long id) {
		Cursor c = null;
		ViewApk apk = new ViewApk();
		try {
			c = database.query("itembasedapk", new String[]{"apkid","vername","itembasedapkrepo_id","downloads","size","icon","name","rating","path","md5"}, "_id = ?", new String[]{id+""}, null, null, null);
		
		c.moveToFirst();
		apk.setApkid(c.getString(0));
		apk.setVername(c.getString(1));
		apk.setRepo_id(c.getLong(2));
		apk.setMd5(c.getString(9));
		apk.setDownloads(c.getString(3));
		apk.setSize(c.getString(4));
		apk.setIconPath(c.getString(5));
		apk.setName(c.getString(6));
		apk.setRating(c.getString(7));
		apk.setPath(c.getString(8));
		apk.setId(id);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			c.close();
		}
		
		return apk;
	}

	public String getItemBasedServer(long repo_id) {
		Cursor c = null;
		String return_string = null;
		try {
			c = database.query("itembasedapkrepo", new String[]{"name"}, "_id=?", new String[]{repo_id+""}, null, null, null);
			
			if(c.moveToFirst()){
				return_string = c.getString(0);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		
		return return_string;
	}

	public String getItemBasedBasePath(long repo_id) {
		Cursor c = null;
		String return_string = null;
		try {
			c = database.query("itembasedapkrepo", new String[]{"basepath"}, "_id=?", new String[]{repo_id+""}, null, null, null);
			
			if(c.moveToFirst()){
				return_string = c.getString(0);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			c.close();
		}
		
		return return_string;
	}
	
	public void deleteItemBasedApks(ViewApk apk) {
		
		Cursor c = null;
		
		try{
			c = database.query("itembasedapk", new String[]{"itembasedapkrepo_id"}, "parent_apkid = ?", new String[]{apk.getApkid()}, null, null, null);
			
			for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){
				database.delete("itembasedapk", "_id = ?", new String[]{c.getString(0)});
			}
			
			
		}catch (Exception e){
			e.printStackTrace();
		}finally{
			c.close();
		}
		
		database.delete("itembasedapk","parent_apkid = ?", new String[]{apk.getApkid()});
	}

	public long getApkId(String apkid, long store_id) {
		Cursor c = null;
		long return_int = -1;
		System.out.println(apkid  + " " + store_id);
		try{
			c = database.query("apk", new String[]{"_id"}, "apkid = ? and repo_id = ?", new String[]{apkid,store_id+""}, null, null, null);
			
			if(c.moveToFirst()){
				return_int = c.getLong(0);
			}
			
		}catch (Exception e){
			e.printStackTrace();
		} finally {
			c.close();
		}
		
		return return_int;
	}
	
	public long getApkId(String apkid) {
		Cursor c = null;
		long return_int = -1;
		System.out.println(apkid);
		try{
			c = database.query("apk", new String[]{"_id"}, "apkid = ?", new String[]{apkid}, null, null, null);
			
			if(c.moveToFirst()){
				return_int = c.getLong(0);
			}
			
		}catch (Exception e){
			e.printStackTrace();
		} finally {
			c.close();
		}
		
		return return_int;
	}

	public ArrayList<HashMap<String, String>> getFeaturedGraphics() {
		ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String,String>>();
		HashMap<String, String> value;
		
		Cursor c = null;
		
		try {
			c = database.rawQuery("select a._id, b.featuredgraphicpath, a.featuredgraphic ,a.apkid, a.vercode from itembasedapk a, itembasedapkrepo b where a.parent_apkid = 'editorschoice' and a.itembasedapkrepo_id = b._id and a.highlight is null ",null);
			
			for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){
				value = new HashMap<String, String>();
				value.put("url", c.getString(1)+c.getString(2));
				value.put("id", c.getString(0));
				value.put("hashCode", (c.getString(3)+"|"+c.getString(4)).hashCode()+"featured");
				arrayList.add(value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			c.close();
		}
		
		
		return arrayList;
	}

	public HashMap<String, String> getHighLightFeature() {
		HashMap<String, String> value = null;
		
		Cursor c = null;
		
		try {
			c = database.rawQuery("select a._id, b.featuredgraphicpath, a.featuredgraphic ,a.apkid, a.vercode from itembasedapk a, itembasedapkrepo b where a.parent_apkid = 'editorschoice' and a.itembasedapkrepo_id = b._id and a.highlight is not null ",null);
			
			for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){
				value = new HashMap<String, String>();
				value.put("url", c.getString(1)+c.getString(2));
				value.put("id", c.getString(0));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			c.close();
		}
		return value;
	}

	public Cursor getScheduledDownloads() {
		
		Cursor c = null;
		
		try {
			c= database.query("scheduled", null, null, null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return c;
	}
	
	public void insertScheduledDownload(String apkid, int vercode, String vername, String remotePath,String name, String md5) {
		
		Cursor c = database.query("scheduled", null, "apkid = ? and vercode = ?", new String[]{apkid,vercode+""}, null, null, null);
		
		if(c.moveToFirst()){
			c.close();
			return;
		}
		c.close();
		
		ContentValues values = new ContentValues();
		values.put("name",name);
		values.put("apkid", apkid);
		values.put("md5", md5);
		values.put("vercode", vercode);
		values.put("vername", vername);
		values.put("remotepath", remotePath);
		
		database.insert("scheduled", null, values);
		
	}

	public void deleteScheduledDownload(String planet) {
		database.delete("scheduled", "_id = ?", new String[]{planet});
	}
	
	public void deleteScheduledDownload(String apkid, String versionName) {
		database.delete("scheduled", "apkid = ? and vername = ?", new String[]{apkid,versionName});
	}

	public void insertInstalled(String apkid, int versionCode, String versionName, String appName) {
		ContentValues values = new ContentValues();
		values.put("apkid", apkid);
		values.put("vercode", versionCode);
		values.put("vername", versionName);
		values.put("name", appName);
		database.insert("installed", null, values);
	}

	public void deleteInstalled(String apkid) {
		database.delete("installed", "apkid = ?", new String[]{apkid});
	}

	public Cursor getSearch(String searchQuery) {
		
		String query = "select b._id as _id, b.name,b.vername,b.repo_id,b.imagepath as imagepath,b.rating,b.downloads,b.apkid as apkid ,b.vercode as vercode, c.iconspath as iconspath from apk as b, repo as c where (b.name LIKE '%"+searchQuery+"%' OR b.apkid LIKE '%"+searchQuery+"%') and b.repo_id = c._id";
		
		Cursor c = null;
		try {
			c= database.rawQuery(query, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return c;
	}

	/**
	 * 
	 */
	public int getInstalledAppVercode(String apkid) {
		
		Cursor c = null;
		int return_int = 0;
		try {
			c= database.query("installed", new String[]{"vercode"}, "apkid = ?", new String[]{apkid}, null, null, null);
			if(c.moveToFirst()){
				return_int=c.getInt(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(c!=null){
				c.close();
			}
		}
		
		return return_int;
		
		
	}
	
	public String getInstalledAppVername(String apkid) {
		
		Cursor c = null;
		String return_string = "";
		try {
			c= database.query("installed", new String[]{"vername"}, "apkid = ?", new String[]{apkid}, null, null, null);
			if(c.moveToFirst()){
				return_string=c.getString(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(c!=null){
				c.close();
			}
		}
		
		return return_string;
		
		
	}
	
}

