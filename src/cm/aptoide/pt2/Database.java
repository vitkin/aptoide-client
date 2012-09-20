package cm.aptoide.pt2;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Database {
	private static SQLiteDatabase database = null;
	private static SQLiteDatabase extrasDatabase = null;
	private static DbOpenHelper dbhandler ;
	private static ExtrasDbOpenHelper extrasDbHandler ;
	private int i=0;
	private static Database db;
	static Context context;
	
	private HashMap<String,Integer> categories1 = new HashMap<String, Integer>();
	private HashMap<String,Integer> categories2 = new HashMap<String, Integer>();
	
	private Database(Context context) {
		dbhandler = new DbOpenHelper(context); 
		database = dbhandler.getWritableDatabase();
		this.context=context;
	}
	
	public static Database getInstance(Context context){
		if(db==null){
			db = new Database(context);
		}
			return db;
		}
		
	
	
	public void insert(Apk apk) {
		
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
			values.put("repo_id", apk.getRepo_id());
			values.put("category2", categories2.get(apk.getCategory2()));
			database.insert("apk", null, values);
			i++;
			if(i%300==0){
				Intent i = new Intent("update");
				i.putExtra("server", apk.getRepo_id());
				context.sendBroadcast(i);
			}
			if(database.yieldIfContendedSafely()){
				System.out.println("yielded");
			}
			
		} catch (Exception e){
			e.printStackTrace();
		}finally{
			
		}
		
	}
	
	public void close(){
		database.close();
	}
	
	private void insertCategories(Apk apk) {
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
	
	private void insertDynamicCategories(Apk apk) {
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
	
	public void endTransation(long l, Server server){
		Intent i = new Intent("status");
		i.putExtra("server", server);
		i.putExtra("time", l+"");
		context.sendBroadcast(i);
		i.setAction("complete");
		context.sendBroadcast(i);
		i.setAction("update");
		i.putExtra("server", server.id);
		context.sendBroadcast(i);
		database.setTransactionSuccessful();
		database.endTransaction();
	}

	public Cursor getApps(long category2_id, long store, boolean mergeStores) {
		Cursor c = null;
		try{
			if(mergeStores){
//				c = database.query("apk", new String[]{"_id","name","vername"},"category2 = ?", new String[]{l+""}, "name", null, "name collate nocase");
				c = database.rawQuery("select _id, name, vername, repo_id, imagepath from apk as a where category2 = ? and vercode = (select max(vercode) from apk as b where a.apkid=b.apkid) group by apkid order by name collate nocase",new String[]{category2_id+""});
			}else{
				c = database.rawQuery("select _id, name, vername, repo_id, imagepath from apk as a where repo_id = ? and category2 = ? and vercode in (select vercode from apk as b where a.apkid=b.apkid order by vercode asc) group by apkid order by name collate nocase",new String[]{store+"",category2_id+""});
//				c = database.query("apk", new String[]{"_id","name","vername"},"category2 = ? and repo_id = ?", new String[]{l+"",store+""}, "name", null, "name collate nocase");
			}
			System.out.println("getapps " + "repo_id ="+store +  " category " + category2_id);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		
		return c;
	}

	public Cursor getCategory1(long store, boolean mergeStores) {
		Cursor c = null;
		if(mergeStores){
			c = database.rawQuery("select a._id,a.name from category1 as a order by a._id",null);
		}else{
			c = database.rawQuery("select a._id,a.name from category1 as a , repo_category1 as b where a._id=b.catg1_id and b.repo_id = ? order by a._id", new String[]{store+""});
		}
		System.out.println("Getting category1 " + store);
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
			c.moveToFirst();
			server = new Server(c.getString(1),c.getString(2),c.getLong(0));
			server.username = c.getString(3);
			server.password = c.getString(4);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			c.close();
		}
		
		return server;
	}
	
	public Server getServer(long id) {
		Server server = null;
		Cursor c = null;
		
		try {
			c = database.query("repo", new String[]{"_id, url, delta"}, "_id = ?", new String[]{id+""}, null, null, null);
			c.moveToFirst();
			server = new Server(c.getString(1),c.getString(2),c.getLong(0));
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			c.close();
		}
		
		return server;
	}
	
	public void deleteTopApps(long id){
		database.delete("dynamic_apk", "repo_id = ?", new String[]{id+""});
		database.delete("toprepo_extra", "_id = ?", new String[]{id+""});
	}
	
	public void deleteServer(long id, boolean fromRepoTable){
		ArrayList<String> catg1_deletes = new ArrayList<String>();
		ArrayList<String> catg2_deletes = new ArrayList<String>();
		if(fromRepoTable){
			database.delete("repo", "_id = ?", new String[]{id+""});
			database.delete("toprepo_extra", "_id = ?", new String[]{id+""});
			database.delete("dynamic_apk", "repo_id = ?", new String[]{id+""});
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

	public Cursor getStores() {
		return database.query("repo", null, null, null, null, null, null);
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
				path = getBasePath(repo_id);
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

	private String getBasePath(long repo_id) {
		Cursor c = null;
		String path = null;
		try{
			c = database.query("repo", new String[]{"basepath"}, "_id = ?", new String[]{repo_id+""}, null, null, null);
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
		if(values.size()>0){
			database.update("repo", values,"_id = ?", new String[]{server.id+""});
		}
		
		
	}

	public void insertInstalled(Apk apk) {
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

	public Cursor getInstalledApps() {
		Cursor c = null;
		try{
			c = database.rawQuery("select b._id as _id, a.name,a.vername,b.repo_id,b.imagepath from installed as a, apk as b where a.apkid=b.apkid group by a.name order by a.name",null);
		}catch (Exception e){
			e.printStackTrace();
		}
		return c;
	}

	public Cursor getUpdates() {
		Cursor c = null;
		try{
			c=database.rawQuery("select b._id, b.name,b.vername,b.repo_id,b.imagepath from installed as a, apk as b where a.apkid=b.apkid and b.vercode > a.vercode and b.vercode = (select max(vercode) from apk as b where a.apkid=b.apkid) order by b.name", null); 
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

	public void insertDynamic(Apk apk, Category category) {
		switch (category) {
		case TOP:
			try{
				apk.setCategory1("Top Apps");
				insertDynamicCategories(apk);
				insertDynamicApk(apk);
			}catch (Exception e) {
				e.printStackTrace();
			}
			
			break;

		default:
			break;
		}
	}

	private void insertDynamicApk(Apk apk) {
		try{
			ContentValues values = new ContentValues();
			values.put("name", apk.getName());
			values.put("category1", categories1.get(apk.getCategory1()));
			values.put("vername", apk.getVername());
			values.put("repo_id", apk.getRepo_id());
			values.put("apkid", apk.getApkid());
			values.put("vercode", apk.getVercode());
			values.put("imagepath", apk.getIconPath());
			database.insert("dynamic_apk", null, values);
			i++;
			if(i%300==0){
				Intent i = new Intent("update");
				i.putExtra("server", apk.getRepo_id());
				context.sendBroadcast(i);
			}
			if(database.yieldIfContendedSafely()){
				System.out.println("yielded");
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		
	}

	public Cursor getTopApps(long category_id, long store_id, boolean joinStores_boolean) {
		Cursor c = null;
		try{
			if(joinStores_boolean){
				c = database.rawQuery("select _id, name, vername, repo_id, imagepath from dynamic_apk as a where category1 = ? and vercode = (select max(vercode) from apk as b where a.apkid=b.apkid) group by apkid order by name collate nocase",new String[]{category_id+""});
			}else{
				c = database.rawQuery("select _id, name, vername, repo_id, imagepath from dynamic_apk as a where repo_id = ? and category1 = ? and vercode in (select vercode from dynamic_apk as b where a.apkid=b.apkid order by vercode asc) group by apkid order by name collate nocase",new String[]{store_id+"",category_id+""});
			}
			System.out.println("getapps " + "repo_id ="+store_id +  " category " + category_id);
		}catch(Exception e){
			e.printStackTrace();
		}
		return c;
	}

	public void remove(Apk apk) {
		database.delete("apk", "apkid=?", new String[]{apk.getApkid()});
	}

	public String getTopIconsPath(long repo_id) {
		Cursor c = null;
		String path = null;
		try{
			c = database.query("toprepo_extra", new String[]{"iconspath"}, "_id = ?", new String[]{repo_id+""}, null, null, null);
			c.moveToFirst();
			if(c.isNull(0)){
				path = getBasePath(repo_id);
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

	public void insertTopServerInfo(Server server) {
		ContentValues values = new ContentValues();
		values.put("iconspath", server.iconsPath);
		values.put("_id", server.id);
		values.put("top_delta", server.delta);
		database.insert("toprepo_extra", null, values);
	}
	
	public String getTopAppsHash(long id){
		Cursor c = null;
		String return_string = "";
		try{
			c= database.query("toprepo_extra", new String[]{"top_delta"}, "_id = ?", new String[]{id+""}, null, null, null);
			c.moveToFirst();
			return_string=c.getString(0);
		}catch (Exception e){
			e.printStackTrace();
		}finally{
			c.close();
		}
		return return_string;
		
	}

	public void prepare() {
		categories1.clear();
		categories2.clear();
	}



	
}

