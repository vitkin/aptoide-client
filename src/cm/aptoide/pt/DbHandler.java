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
	            + "name text not null, path text not null, lastver text not null, server text, primary key(apkid, server));";
	
	private static final String CREATE_TABLE_LOCAL = "create table if not exists " + TABLE_NAME_LOCAL + " (apkid text primary key, "
				+ "instver text not null);";
	
	private static final String CREATE_TABLE_URI = "create table if not exists " + TABLE_NAME_URI 
				+ " (uri text primary key, inuse integer not null);";
	
	private static final String CREATE_TABLE_EXTRA = "create table if not exists " + TABLE_NAME_EXTRA
				+ " (apkid text, rat number, dt date, primary key(apkid));";
	

	public DbHandler(Context ctx) {
			db = ctx.openOrCreateDatabase(DATABASE_NAME, 0, null);
			db.execSQL(CREATE_TABLE_APTOIDE);
			db.execSQL(CREATE_TABLE_LOCAL);
			db.execSQL(CREATE_TABLE_URI);
			db.execSQL(CREATE_TABLE_EXTRA);
			//db.close();
	}
	
	public boolean insertApk(String name, String path, String ver, String apkid, String date, Float rat, String serv){
		Cursor c = db.query(TABLE_NAME, new String[] {"lastver"}, "apkid=\""+apkid+"\"", null, null, null, null);
		if(c.moveToFirst()){
			String tmp_ver = c.getString(0);
			String[] db_ver = tmp_ver.split("\\.");
			String[] new_ver = ver.split("\\.");
			
			for(int i = 0; i<Math.min(db_ver.length, new_ver.length); i++){
				if(Integer.parseInt(db_ver[i]) < Integer.parseInt(new_ver[i])){
					db.delete(TABLE_NAME, "apkid=\""+apkid+"\"",null);
					break;
				}else if(Integer.parseInt(db_ver[i]) > Integer.parseInt(new_ver[i])){
					return false;
				}
			}
			if(db_ver.length < new_ver.length){
				db.delete(TABLE_NAME, "apkid=\""+apkid+"\"",null);
			}
		}
		
		ContentValues tmp = new ContentValues();
		tmp.put("apkid", apkid);
		tmp.put("name", name);
		tmp.put("path", path);
		tmp.put("lastver", ver);
		tmp.put("server", serv);
		try{
			db.insert(TABLE_NAME, null, tmp);
			tmp.clear();
			Cursor c2 = db.query(TABLE_NAME_EXTRA, new String[] {"dt", "rat"}, "apkid=\""+apkid+"\"", null, null, null, null);
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
		}
	}
	
	public boolean insertInstalled(String apkid){
		ContentValues tmp = new ContentValues();
		tmp.put("apkid", apkid);
		Cursor c = db.query(TABLE_NAME, new String[] {"lastver"}, "apkid=\""+apkid+"\"", null, null, null, null);
		c.moveToFirst();
		String ver = c.getString(0);
		tmp.put("instver", ver);
		return (db.insert(TABLE_NAME_LOCAL, null, tmp) > 0); 
	}
	
	/*
	 * Same as above, but with explicit version
	 */
	public boolean insertInstalled(String apkid, String ver){
		ContentValues tmp = new ContentValues();
		tmp.put("apkid", apkid);
		tmp.put("instver", ver);
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
		try{
			
			final String basic_query = "select distinct c.apkid, c.name, c.instver, c.lastver, b.dt, b.rat from "
				+ "(select distinct a.apkid as apkid, a.name as name, l.instver as instver, a.lastver as lastver from "
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
			Cursor c = db.rawQuery(search, null);
			c.moveToFirst();
			
			
			for(int i = 0; i< c.getCount(); i++){
				ApkNode node = new ApkNode();
				node.apkid = c.getString(0);
				node.name = c.getString(1);
				if(c.getString(2) == null){
					node.status = 0;
				}else{
					if(c.getString(2).equalsIgnoreCase(c.getString(3))){
						node.status = 1;
						node.ver = c.getString(2);
					}else{
						boolean isupdate = false;
						String[] tmp1 = c.getString(2).split("\\.");
						String[] tmp2 = c.getString(3).split("\\.");
						int sizei = Math.min(tmp1.length, tmp2.length);
						for(int x=0; x<sizei; x++){
							if(Integer.parseInt(tmp1[x]) < Integer.parseInt(tmp2[x])){
								isupdate = true;
							}
						}
						if(!isupdate){
							if(tmp1.length < tmp2.length){
								node.status = 2;
								node.ver = c.getString(2) + "/ new: " + c.getString(3);
							}else{
								node.status = 1;
								node.ver = c.getString(2);
							}
						}else{
							node.status = 2;
							node.ver = c.getString(2) + "/ new: " + c.getString(3);
						}
					}
				}
				node.rat = c.getFloat(5);
				tmp.add(node);
				c.moveToNext();
			}
			
		}catch (Exception e){ 	}
		return tmp;
	}
	
	/*
	 * Same get type function, used in search
	 */
	public Vector<ApkNode> getSearch(String exp, String type){
		Vector<ApkNode> tmp = new Vector<ApkNode>();
		try{
			
			final String basic_query = "select distinct c.apkid, c.name, c.instver, c.lastver, b.dt, b.rat from "
				+ "(select distinct a.apkid as apkid, a.name as name, l.instver as instver, a.lastver as lastver from "
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
			Cursor c = db.rawQuery(search, null);
			c.moveToFirst();
			for(int i = 0; i< c.getCount(); i++){
				ApkNode node = new ApkNode();
				node.apkid = c.getString(0);
				node.name = c.getString(1);
				if(c.getString(2) == null){
					node.status = 0;
				}else{
					if(c.getString(2).equalsIgnoreCase(c.getString(3))){
						node.status = 1;
						node.ver = c.getString(2);
					}else{
						boolean isupdate = false;
						String[] tmp1 = c.getString(2).split("\\.");
						String[] tmp2 = c.getString(3).split("\\.");
						int sizei = Math.min(tmp1.length, tmp2.length);
						for(int x=0; x<sizei; x++){
							if(Integer.parseInt(tmp1[x]) < Integer.parseInt(tmp2[x])){
								isupdate = true;
							}
						}
						if(!isupdate){
							if(tmp1.length < tmp2.length){
								node.status = 2;
								node.ver = c.getString(2) + "/ new: " + c.getString(3);
							}else{
								node.status = 1;
								node.ver = c.getString(2);
							}
						}else{
							node.status = 2;
							node.ver = c.getString(2) + "/ new: " + c.getString(3);
						}
					}
				}
				node.rat = c.getFloat(5);
				tmp.add(node);
				c.moveToNext();
			}

		}catch (Exception e){ }
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
		try{
			Cursor c = db.query(TABLE_NAME, new String[] {"server", "lastver"}, "apkid=\""+id.toString()+"\"", null, null, null, null);
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
		}catch (Exception e){
			System.out.println(e.toString());
		}
		return tmp;
	}
	
	public Vector<String> getPath(String id){
		Vector<String> out = new Vector<String>();
		try{
			Cursor c = db.query(TABLE_NAME, new String[] {"server", "path"}, "apkid=\""+id.toString()+"\"", null, null, null, null);
			c.moveToFirst();
			for(int i =0; i<c.getCount(); i++){
				out.add(c.getString(0)+"/"+c.getString(1));
			}
		}catch(Exception e){
			System.out.println(e.toString());
		}
		return out;
	}
	
	public String getName(String id){
		String out = new String();
		try{
			Cursor c = db.query(TABLE_NAME, new String[] {"name"}, "apkid=\""+id.toString()+"\"", null, null, null, null);
			c.moveToFirst();
			out = c.getString(0);
		}catch (Exception e){ }
		return out;
	}
	
	public Vector<ServerNode> getServers(){
		Vector<ServerNode> out = new Vector<ServerNode>();
		
		Cursor c = db.rawQuery("select uri, inuse from " + TABLE_NAME_URI + " order by uri collate nocase", null);
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
}
