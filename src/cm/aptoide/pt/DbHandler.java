/*
 * Copyright (C) 2009  Roberto Jacinto
 * roberto.jacinto@caixamagica.pt
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

package cm.aptoide.pt;

import java.util.Vector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DbHandler {
	
	private static final String DATABASE_NAME = "aptoide_db";
	private static final String TABLE_NAME_LOCAL = "local";
	private static final String TABLE_NAME = "aptoide";
	private static final String TABLE_NAME_URI = "servers";
	
	private static final String TABLE_NAME_EXTRA = "extra";
	
	private SQLiteDatabase db;
	
	private static final String CREATE_TABLE_APTOIDE = "create table if not exists " + TABLE_NAME + " (apkid text, "
	            + "name text not null, path text not null, lastver text not null, lastvercode number not null ,server text, md5hash text, primary key(apkid, server));";
	
	private static final String CREATE_TABLE_LOCAL = "create table if not exists " + TABLE_NAME_LOCAL + " (apkid text primary key, "
				+ "instver text not null, instvercode number not null);";
	
	private static final String CREATE_TABLE_URI = "create table if not exists " + TABLE_NAME_URI 
				+ " (uri text primary key, inuse integer not null);";
	
	private static final String CREATE_TABLE_EXTRA = "create table if not exists " + TABLE_NAME_EXTRA
				+ " (apkid text, rat number, dt date, desc text, primary key(apkid));";
	

	public DbHandler(Context ctx) {
		db = ctx.openOrCreateDatabase(DATABASE_NAME, 0, null);
		db.execSQL(CREATE_TABLE_URI);
		db.execSQL(CREATE_TABLE_EXTRA);
		db.execSQL(CREATE_TABLE_APTOIDE);
		db.execSQL(CREATE_TABLE_LOCAL);
		//db.close();
	}
	
	/*
	 * Code for DB update on new version of Aptoide
	 */
	public void UpdateTables(){
		try{
			// What you want to check
			db.query(TABLE_NAME, new String[] {"lastvercode", "md5hash"}, null, null, null, null, null);
		}catch(Exception e){
			// What you want to update
			db.execSQL("drop table " + TABLE_NAME + ";");
			db.execSQL("drop table " + TABLE_NAME_LOCAL + ";");
			db.execSQL(CREATE_TABLE_APTOIDE);
			db.execSQL(CREATE_TABLE_LOCAL);
		}
	}
	
	public void UpdateTables2(){
		try{
			db.query(TABLE_NAME_EXTRA, new String[] {"desc"}, null, null, null, null, null);
		}catch(Exception e){
			db.execSQL("drop table " + TABLE_NAME_EXTRA + ";");
			db.execSQL(CREATE_TABLE_EXTRA);
		}
	}

	public boolean insertApk(String name, String path, String ver, int vercode ,String apkid, String date, Float rat, String serv, String md5hash){
		Cursor c2 = null;
		Cursor c = db.query(TABLE_NAME, new String[] {"lastvercode"}, "apkid=\""+apkid+"\"", null, null, null, null);
		if(c.moveToFirst()){
			int db_ver = c.getInt(0);
			if (db_ver < vercode){
				db.delete(TABLE_NAME, "apkid=\""+apkid+"\"",null);
			}else if(db_ver > vercode){
				return false;
			}
		}
		c.close();
		ContentValues tmp = new ContentValues();
		tmp.put("apkid", apkid);
		tmp.put("name", name);
		tmp.put("path", path);
		tmp.put("lastver", ver);
		tmp.put("lastvercode", vercode);
		tmp.put("server", serv);
		tmp.put("md5hash", md5hash);
		try{
			db.insert(TABLE_NAME, null, tmp);
			tmp.clear();
			c2 = db.query(TABLE_NAME_EXTRA, new String[] {"dt", "rat"}, "apkid=\""+apkid+"\"", null, null, null, null);
			c2.moveToFirst();
			if(c2.getCount()>0){
				float tmp_rat = c2.getFloat(1);
				if(tmp_rat > rat)
					tmp_rat--;
				else if(tmp_rat < rat)
					tmp_rat++;
				tmp.put("rat", tmp_rat);
				String tmp_dt = c.getString(0);
				String[] date_old = tmp_dt.split("-");
				String[] date_new = date.split("-");
				for(int i=0; i<3; i++){
					if(Integer.parseInt(date_old[i]) < Integer.parseInt(date_new[i])){
						tmp_dt = date;
						break;
					}
				}
				tmp.put("dt", tmp_dt);
				db.delete(TABLE_NAME_EXTRA, "apkid=\""+apkid+"\"",null);
			}else{
				tmp.put("dt", date);
				tmp.put("rat", rat);
			}
			tmp.put("apkid", apkid);
			db.insert(TABLE_NAME_EXTRA, null, tmp);
			return true;
		}catch (Exception e){
			return false;
		}finally{
			c2.close();
		}
	}
	
	public boolean insertInstalled(String apkid){
		ContentValues tmp = new ContentValues();
		tmp.put("apkid", apkid);
		Cursor c = db.query(TABLE_NAME, new String[] {"lastver", "lastvercode"}, "apkid=\""+apkid+"\"", null, null, null, null);
		c.moveToFirst();
		String ver = c.getString(0);
		int vercode = c.getInt(1);
		tmp.put("instver", ver);
		tmp.put("instvercode", vercode);
		c.close();
		return (db.insert(TABLE_NAME_LOCAL, null, tmp) > 0); 
	}
	
	/*
	 * Same as above, but with explicit version
	 */
	public boolean insertInstalled(String apkid, String ver, int vercode){
		ContentValues tmp = new ContentValues();
		tmp.put("apkid", apkid);
		tmp.put("instver", ver);
		tmp.put("instvercode", vercode);
		return (db.insert(TABLE_NAME_LOCAL, null, tmp) > 0); 
	}
	
	public boolean removeInstalled(String apkid){
		return (db.delete(TABLE_NAME_LOCAL, "apkid=\""+apkid+"\"", null) > 0);
	}
	
	public boolean removeAll(){
		return (db.delete(TABLE_NAME, null, null) > 0);
	}
	
	public Vector<ApkNode> getAll(String type){
		Vector<ApkNode> tmp = new Vector<ApkNode>();
		Cursor c = null;
		try{
			
			final String basic_query = "select distinct c.apkid, c.name, c.instver, c.lastver, c.instvercode, c.lastvercode ,b.dt, b.rat from "
				+ "(select distinct a.apkid as apkid, a.name as name, l.instver as instver, l.instvercode as instvercode, a.lastver as lastver, a.lastvercode as lastvercode from "
				+ TABLE_NAME + " as a left join " + TABLE_NAME_LOCAL + " as l on a.apkid = l.apkid) as c left join "
				+ TABLE_NAME_EXTRA + " as b on c.apkid = b.apkid";
			
			final String iu = " order by instver desc";
			final String rat = " order by rat desc";
			final String mr = " order by dt desc";
			final String alfb = " order by name collate nocase";
						
			String search;
			if(type.equalsIgnoreCase("abc")){
				search = basic_query+alfb;
			}else if(type.equalsIgnoreCase("iu")){
				search = basic_query+iu;
			}else if(type.equalsIgnoreCase("recent")){
				search = basic_query+mr;
			}else if(type.equalsIgnoreCase("rating")){
				search = basic_query+rat;
			}else{
				search = basic_query;
			}
			c = db.rawQuery(search, null);
			c.moveToFirst();
			
			
			for(int i = 0; i< c.getCount(); i++){
				ApkNode node = new ApkNode();
				node.apkid = c.getString(0);
				node.name = c.getString(1);
				if(c.getString(2) == null){
					node.status = 0;
					node.ver = c.getString(3);
				}else{
					//if(c.getString(2).equalsIgnoreCase(c.getString(3))){
					if(c.getInt(4) == c.getInt(5)){
						node.status = 1;
						node.ver = c.getString(2);
					}else{
						int instvercode = c.getInt(4);
						int lastvercode = c.getInt(5);
						if(instvercode < lastvercode){
							node.status = 2;
							node.ver = c.getString(2) + "/ new: " + c.getString(3);
						}else{
							node.status = 1;
							node.ver = c.getString(2);
						}
					}
					
				}
				node.rat = c.getFloat(7);
				tmp.add(node);
				c.moveToNext();
			}
		}catch (Exception e){ }
		finally{
			c.close();
		}
		return tmp;
	}
	
	/*
	 * Same get type function, used in search
	 */
	public Vector<ApkNode> getSearch(String exp, String type){
		Vector<ApkNode> tmp = new Vector<ApkNode>();
		Cursor c = null;
		try{
			
			final String basic_query = "select distinct c.apkid, c.name, c.instver, c.lastver, c.instvercode, c.lastvercode, b.dt, b.rat from "
				+ "(select distinct a.apkid as apkid, a.name as name, l.instver as instver, a.lastver as lastver, l.instvercode as instvercode, a.lastvercode as lastvercode from "
				+ TABLE_NAME + " as a left join " + TABLE_NAME_LOCAL + " as l on a.apkid = l.apkid) as c left join "
				+ TABLE_NAME_EXTRA + " as b on c.apkid = b.apkid where name like '%" + exp + "%'";
			
			final String iu = " order by instver desc";
			final String rat = " order by rat desc";
			final String mr = " order by dt desc";
			final String alfb = " order by name collate nocase";
			
			String search;
			if(type.equalsIgnoreCase("abc")){
				search = basic_query+alfb;
			}else if(type.equalsIgnoreCase("iu")){
				search = basic_query+iu;
			}else if(type.equalsIgnoreCase("recent")){
				search = basic_query+mr;
			}else if(type.equalsIgnoreCase("rating")){
				search = basic_query+rat;
			}else{
				search = basic_query;
			}
			c = db.rawQuery(search, null);
			c.moveToFirst();
			for(int i = 0; i< c.getCount(); i++){
				ApkNode node = new ApkNode();
				node.apkid = c.getString(0);
				node.name = c.getString(1);
				if(c.getString(2) == null){
					node.status = 0;
				}else{
					//if(c.getString(2).equalsIgnoreCase(c.getString(3))){
					if(c.getInt(4) == c.getInt(5)){
						node.status = 1;
						node.ver = c.getString(2);
					}else{
						int instvercode = c.getInt(4);
						int lastvercode = c.getInt(5);
						if(instvercode < lastvercode){
							node.status = 2;
							node.ver = c.getString(2) + "/ new: " + c.getString(3);
						}else{
							node.status = 1;
							node.ver = c.getString(2);
						}
					}
				}
				node.rat = c.getFloat(7);
				tmp.add(node);
				c.moveToNext();
			}
			//c.close();
		}catch (Exception e){ }
		finally{
			c.close();
		}
		return tmp;
	}
	
	/*
	 * Same function as above, used in list of updates
	 */
	
	public Vector<ApkNode> getUpdates(String type){
		Vector<ApkNode> tmp = new Vector<ApkNode>();
		Cursor c = null;
		try{
			
			final String basic_query = "select distinct c.apkid, c.name, c.instver, c.lastver, c.instvercode, c.lastvercode ,b.dt, b.rat from "
				+ "(select distinct a.apkid as apkid, a.name as name, l.instver as instver, l.instvercode as instvercode, a.lastver as lastver, a.lastvercode as lastvercode from "
				+ TABLE_NAME + " as a left join " + TABLE_NAME_LOCAL + " as l on a.apkid = l.apkid) as c left join "
				+ TABLE_NAME_EXTRA + " as b on c.apkid = b.apkid where c.instvercode < c.lastvercode";
			
			final String iu = " order by instver desc";
			final String rat = " order by rat desc";
			final String mr = " order by dt desc";
			final String alfb = " order by name collate nocase";
						
			String search;
			if(type.equalsIgnoreCase("abc")){
				search = basic_query+alfb;
			}else if(type.equalsIgnoreCase("iu")){
				search = basic_query+iu;
			}else if(type.equalsIgnoreCase("recent")){
				search = basic_query+mr;
			}else if(type.equalsIgnoreCase("rating")){
				search = basic_query+rat;
			}else{
				search = basic_query;
			}
			c = db.rawQuery(search, null);
			c.moveToFirst();
			
			
			for(int i = 0; i< c.getCount(); i++){
				ApkNode node = new ApkNode();
				node.apkid = c.getString(0);
				node.name = c.getString(1);
				if(c.getString(2) == null){
					node.status = 0;
				}else{
					//if(c.getString(2).equalsIgnoreCase(c.getString(3))){
					if(c.getInt(4) == c.getInt(5)){
						node.status = 1;
						node.ver = c.getString(2);
					}else{
						int instvercode = c.getInt(4);
						int lastvercode = c.getInt(5);
						if(instvercode < lastvercode){
							node.status = 2;
							node.ver = c.getString(2) + "/ new: " + c.getString(3);
						}else{
							node.status = 1;
							node.ver = c.getString(2);
						}
					}
					
				}
				node.rat = c.getFloat(7);
				tmp.add(node);
				c.moveToNext();
			}
		}catch (Exception e){ 	}
		finally{
			c.close();
		}
		return tmp;
	}
	
	/*
	 * Returned values about an application
	 *  - vec(0): servers
	 *  - vec(1): server version
	 *  - vec(2): is it installed?
	 *  - vec(3): installed version
	 */
	public Vector<String> getApk(String id){
		Vector<String> tmp = new Vector<String>();
		Cursor c = null;
		try{
			c = db.query(TABLE_NAME, new String[] {"server", "lastver"}, "apkid=\""+id.toString()+"\"", null, null, null, null);
			c.moveToFirst();
			String tmp_serv = new String();
			for(int i=0; i<c.getCount(); i++){
				tmp_serv = tmp_serv.concat("\n"+c.getString(0));
				c.moveToNext();
			}
			tmp.add(tmp_serv);
			c.moveToPrevious();
			tmp.add(c.getString(1));
			c = db.query(TABLE_NAME_LOCAL, new String[] {"instver"}, "apkid=\""+id.toString()+"\"", null, null, null, null);
			if(c.getCount() == 0){
				tmp.add("no");
				tmp.add(" --- ");
			}else{
				tmp.add("yes");
				c.moveToFirst();
				tmp.add(c.getString(0));
			}
			c.close();
		}catch (Exception e){
			//System.out.println(e.toString());
		}finally{
			c.close();
		}
		return tmp;
	}
	
	
	public Vector<String> getPathHash(String id){
		Vector<String> out = new Vector<String>();
		Cursor c = null;
		try{
			c = db.query(TABLE_NAME, new String[] {"server", "path", "md5hash"}, "apkid=\""+id.toString()+"\"", null, null, null, null);
			c.moveToFirst();
			for(int i =0; i<c.getCount(); i++){
				if(c.isNull(2)){
					out.add(c.getString(0)+"/"+c.getString(1)+"*"+null);
				}else{
					out.add(c.getString(0)+"/"+c.getString(1)+"*"+c.getString(2));
				}
			}
			c.close();
		}catch(Exception e){
		}finally{
			c.close();
		}
		return out;
	}
	
	public String getName(String id){
		String out = new String();
		Cursor c = null;
		try{
			c = db.query(TABLE_NAME, new String[] {"name"}, "apkid=\""+id.toString()+"\"", null, null, null, null);
			c.moveToFirst();
			out = c.getString(0);
			c.close();
		}catch (Exception e){ }
		finally{
			c.close();
		}
		return out;
	}
	
	public Vector<ServerNode> getServers(){
		Vector<ServerNode> out = new Vector<ServerNode>();
		Cursor c = null;
		try {
			c = db.rawQuery("select uri, inuse from " + TABLE_NAME_URI + " order by uri collate nocase", null);
			c.moveToFirst();
			for(int i=0; i<c.getCount(); i++){
				ServerNode node = new ServerNode();
				node.uri = c.getString(0);
				if(c.getInt(1) == 1){
					node.inuse = true;
				}else{
					node.inuse = false;
				}
				out.add(node);
				c.moveToNext();
			}
		}catch (Exception e){ }
		finally{
			c.close();
		}
		return out;
	}
	
	public void changeServerStatus(String uri){
		Cursor c = db.query(TABLE_NAME_URI, new String[] {"inuse"}, "uri=\""+uri+"\"", null, null, null, null);
		c.moveToFirst();
		int state = c.getInt(0);
		db.delete(TABLE_NAME_URI, "uri=\""+uri+"\"", null);
		ContentValues tmp = new ContentValues();
		tmp.put("uri", uri);
		if(state == 1)
			tmp.put("inuse", 0);
		else
			tmp.put("inuse", 1);
		db.insert(TABLE_NAME_URI, null, tmp);
		c.close();
	}
	
	/*
	 * @inuse
	 *  1 - yes
	 *  0 - no
	 */
	public void addServer(String srv){
		ContentValues tmp = new ContentValues();
		tmp.put("uri", srv);
		tmp.put("inuse", 1);
		db.insert(TABLE_NAME_URI, null, tmp);
	}
	
	public void removeServer(Vector<String> serv){
		for(String node: serv){
			db.delete(TABLE_NAME_URI, "uri=\""+node+"\"", null);
		}
	}
	
	public void addExtraXML(String apkid, String cmt, String srv){
		Cursor c = null;
		try{
			c = db.query(TABLE_NAME, new String[] {"lastvercode"}, "server=\""+srv+"\" and apkid=\""+apkid+"\"", null, null, null, null);
			if(c.getCount() > 0){
				ContentValues extra = new ContentValues();
				extra.put("desc", cmt);
				db.update(TABLE_NAME_EXTRA, extra, "apkid=\""+apkid+"\"", null);
			}
		}catch(Exception e) { }
		finally{
			c.close();
		}
	}
	
	public String getDescript(String apkid){
		Cursor c = null;
		String ret = null;
		try{
			c = db.query(TABLE_NAME_EXTRA, new String[] {"desc"}, "apkid=\""+apkid+"\"", null, null, null, null);
			if(c.getCount() > 0){
				c.moveToFirst();
				ret = c.getString(0);
			}
		}
		catch(Exception e) { }
		finally{
			c.close();
		}
		return ret;
	}
	
}
