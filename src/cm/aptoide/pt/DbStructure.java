/*******************************************************************************
  * Copyright (c) 2012 rmateus.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package cm.aptoide.pt;



import cm.aptoide.pt.RepoParser.TopParser;
import cm.aptoide.pt.util.databasecreator.Column;
import cm.aptoide.pt.util.databasecreator.Column.OnConflict;
import cm.aptoide.pt.util.databasecreator.Column.SQLiteType;
import cm.aptoide.pt.util.databasecreator.TableCreator;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbStructure extends SQLiteOpenHelper {
	
	public final static String TABLE_TOP_LATEST_REPO_INFO= "top_latest_repo_info";
	public final static String TABLE_APK= "apk";
	public final static String TABLE_REPO= "repo";
	public final static String TABLE_CATEGORY_1ST = "category_1st";
	public final static String TABLE_CATEGORY_2ND = "category_2nd";
	public final static String TABLE_TOP_REPO = "top_repo";
	public final static String TABLE_TOP_APK = "top_apk";
	public final static String TABLE_TOP_SCREENSHOTS = "top_screenshots";
	public final static String TABLE_LATEST_REPO = "latest_repo";
	public final static String TABLE_LATEST_APK = "latest_apk";
	public final static String TABLE_LATEST_SCREENSHOTS = "latest_screenshots";
	public final static String TABLE_REPO_CATEGORY_1ST = "repo_category_1st";
	public final static String TABLE_REPO_CATEGORY_2ND = "repo_category_2nd";
	public static final String TABLE_INSTALLED = "installed";
	public static final String TABLE_FEATURED_TOP_APK = "featured_top_apk";
	public static final String TABLE_FEATURED_TOP_REPO = "featured_top_repo";
	public static final String TABLE_FEATURED_TOP_SCREENSHOTS = "featured_top_screenshots";
	public static final String TABLE_FEATURED_EDITORSCHOICE_APK = "featured_editorschoice_apk";
	public static final String TABLE_FEATURED_EDITORSCHOICE_REPO = "featured_editorschoice_repo";
	public static final String TABLE_FEATURED_EDITORSCHOICE_SCREENSHOTS = "featured_editorschoice_screenshots";
	public static final String TABLE_ITEMBASED_APK = "itembased_apk";
	public static final String TABLE_ITEMBASED_REPO = "itembased_repo";
	public static final String TABLE_ITEMBASED_HASHES = "itembased_hashes";
	public static final String TABLE_ITEMBASED_SCREENSHOTS = "itembased_screenshots";
	public static final String TABLE_SCHEDULED = "scheduled";
	
	public final static String COLUMN__ID = "_id";
	public final static String COLUMN_APKID = "apkid";
	public final static String COLUMN_NAME = "name";
	public final static String COLUMN_VERNAME = "vername";
	public final static String COLUMN_VERCODE = "vercode";
	public final static String COLUMN_ICON = "icon";
	public final static String COLUMN_DOWNLOADS = "downloads";
	public final static String COLUMN_SIZE = "size";
	public final static String COLUMN_RATING = "rating";
	public final static String COLUMN_REMOTE_PATH = "remote_path";
	public final static String COLUMN_CATEGORY_2ND = "category_2nd";
	public final static String COLUMN_CATEGORY_1ST = "category_1st";
	public final static String COLUMN_MD5 = "md5";
	public final static String COLUMN_REPO_ID = "repo_id";
	public final static String COLUMN_DATE = "date";
	public final static String COLUMN_MIN_SDK = "minsdk";
	public final static String COLUMN_MIN_SCREEN = "minscreen";
	public final static String COLUMN_MIN_GLES = "mingles";
	public final static String COLUMN_MATURE = "mature";
	public final static String COLUMN_HASH = "hash";
	
	public final static String COLUMN_URL = "url";
	public final static String COLUMN_ICONS_PATH = "iconspath";
	public final static String COLUMN_BASE_PATH = "basepath";
	public static final String COLUMN_SCREENS_PATH = "screenspath";
	public final static String COLUMN_STATUS = "status";
	public final static String COLUMN_WEBSERVICESPATH = "webservicespath";
	public final static String COLUMN_USERNAME = "username";
	public final static String COLUMN_PASSWORD = "password";
	public final static String COLUMN_AVATAR_URL = "avatar_url";
	public final static String COLUMN_APKPATH = "apkpath";
	
	public final static String COLUMN_CATEGORY_1ST_ID = COLUMN_CATEGORY_1ST + "_id";
	public final static String COLUMN_CATEGORY_2ND_ID = COLUMN_CATEGORY_2ND + "_id";
	public static final String COLUMN_ITEMBASEDREPO_ID = "itembased_repo_id";
	public static final String COLUMN_FEATURED_GRAPHICS_PATH = "featuredgraphicspath";
	public static final String COLUMN_FEATURED_GRAPHIC = "featuredgraphic";
	public static final String COLUMN_FEATURED_HIGHLIGHTED = "highlighted";
	public static final String COLUMN_PARENT_APKID = "parent_apkid";
	public static final String TABLE_EXCLUDED_APKID = "excluded_apkid";
	
	
	
	
	public DbStructure(Context context) {
		super(context, "aptoide.db", null, 6);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		TableCreator creator = new TableCreator(db);
		
		
		creator.newTable(TABLE_APK).addColumn(new Column(SQLiteType.INTEGER, COLUMN__ID).setPrimaryKey())
								   .addColumn(new Column(SQLiteType.TEXT, COLUMN_APKID))
								   .addColumn(new Column(SQLiteType.TEXT, COLUMN_NAME))
								   .addColumn(new Column(SQLiteType.TEXT,COLUMN_VERNAME))
								   .addColumn(new Column(SQLiteType.INTEGER, COLUMN_VERCODE))
								   .addColumn(new Column(SQLiteType.TEXT, COLUMN_ICON))
								   .addColumn(new Column(SQLiteType.INTEGER, COLUMN_DOWNLOADS))
								   .addColumn(new Column(SQLiteType.INTEGER, COLUMN_SIZE))
								   .addColumn(new Column(SQLiteType.TEXT, COLUMN_RATING))
								   .addColumn(new Column(SQLiteType.TEXT, COLUMN_REMOTE_PATH))
								   .addColumn(new Column(SQLiteType.INTEGER, COLUMN_CATEGORY_2ND))
								   .addColumn(new Column(SQLiteType.TEXT, COLUMN_MD5))
								   .addColumn(new Column(SQLiteType.INTEGER, COLUMN_REPO_ID))
								   .addColumn(new Column(SQLiteType.DATE, COLUMN_DATE))
								   .addColumn(new Column(SQLiteType.INTEGER, COLUMN_MIN_SCREEN))
								   .addColumn(new Column(SQLiteType.INTEGER, COLUMN_MIN_SDK))
								   .addColumn(new Column(SQLiteType.REAL, COLUMN_MIN_GLES))
								   .addColumn(new Column(SQLiteType.INTEGER, COLUMN_MATURE))
								   .createTable();
		
		
		creator.newTable(TABLE_CATEGORY_1ST).addColumn(new Column(SQLiteType.INTEGER, COLUMN__ID).setPrimaryKey())
											.addColumn(new Column(SQLiteType.TEXT,COLUMN_NAME).setUnique(OnConflict.IGNORE))
											.createTable();
		
		
		creator.newTable(TABLE_CATEGORY_2ND).addColumn(new Column(SQLiteType.INTEGER, COLUMN__ID).setPrimaryKey())
											.addColumn(new Column(SQLiteType.TEXT,COLUMN_NAME).setUnique(OnConflict.IGNORE))
											.addColumn(new Column(SQLiteType.INTEGER, COLUMN_CATEGORY_1ST_ID))
											.createTable();
		
		
		creator.newTable(TABLE_REPO).addColumn(new Column(SQLiteType.INTEGER, COLUMN__ID).setPrimaryKey())
									.addColumn(new Column(SQLiteType.TEXT, COLUMN_URL,""))
									.addColumn(new Column(SQLiteType.TEXT, COLUMN_HASH,""))
									.addColumn(new Column(SQLiteType.TEXT, COLUMN_ICONS_PATH,""))
									.addColumn(new Column(SQLiteType.TEXT, COLUMN_BASE_PATH,""))
									.addColumn(new Column(SQLiteType.TEXT, COLUMN_STATUS,""))
									.addColumn(new Column(SQLiteType.TEXT, COLUMN_WEBSERVICESPATH,""))
									.addColumn(new Column(SQLiteType.TEXT, COLUMN_USERNAME))
									.addColumn(new Column(SQLiteType.TEXT, COLUMN_PASSWORD))
									.addColumn(new Column(SQLiteType.TEXT, COLUMN_AVATAR_URL,""))
									.addColumn(new Column(SQLiteType.TEXT, COLUMN_NAME,""))
									.addColumn(new Column(SQLiteType.TEXT, COLUMN_SCREENS_PATH,""))
									.addColumn(new Column(SQLiteType.INTEGER, COLUMN_DOWNLOADS,"0"))
									.addColumn(new Column(SQLiteType.TEXT, COLUMN_APKPATH,""))
									.createTable(); 
		
		
		creator.newTable(TABLE_TOP_APK).addColumn(new Column(SQLiteType.INTEGER, COLUMN__ID).setPrimaryKey())
											  .addColumn(new Column(SQLiteType.TEXT, COLUMN_APKID))
											  .addColumn(new Column(SQLiteType.TEXT, COLUMN_NAME))
											  .addColumn(new Column(SQLiteType.TEXT,COLUMN_VERNAME))
											  .addColumn(new Column(SQLiteType.INTEGER, COLUMN_VERCODE))
											  .addColumn(new Column(SQLiteType.TEXT, COLUMN_ICON))
											  .addColumn(new Column(SQLiteType.INTEGER, COLUMN_DOWNLOADS))
											  .addColumn(new Column(SQLiteType.INTEGER, COLUMN_SIZE))
											  .addColumn(new Column(SQLiteType.TEXT, COLUMN_RATING))
											  .addColumn(new Column(SQLiteType.TEXT, COLUMN_REMOTE_PATH))
											  .addColumn(new Column(SQLiteType.INTEGER, COLUMN_CATEGORY_2ND))
											  .addColumn(new Column(SQLiteType.TEXT, COLUMN_MD5))
											  .addColumn(new Column(SQLiteType.INTEGER, COLUMN_REPO_ID))
											  .addColumn(new Column(SQLiteType.DATE, COLUMN_DATE))
											  .addColumn(new Column(SQLiteType.INTEGER, COLUMN_MIN_SCREEN))
											  .addColumn(new Column(SQLiteType.INTEGER, COLUMN_MIN_SDK))
											  .addColumn(new Column(SQLiteType.REAL, COLUMN_MIN_GLES))
											  .addColumn(new Column(SQLiteType.INTEGER, COLUMN_MATURE))
											  .createTable();
		

		creator.newTable(TABLE_TOP_SCREENSHOTS).addColumn(new Column(SQLiteType.INTEGER, COLUMN__ID))
											   .addColumn(new Column(SQLiteType.TEXT, COLUMN_REMOTE_PATH))
											   .addColumn(new Column(SQLiteType.INTEGER, COLUMN_REPO_ID))
											   .createTable();
		
		
		creator.newTable(TABLE_TOP_REPO).addColumn(new Column(SQLiteType.INTEGER, COLUMN__ID).setPrimaryKey())
										.addColumn(new Column(SQLiteType.TEXT, COLUMN_URL,""))
										.addColumn(new Column(SQLiteType.TEXT, COLUMN_HASH,""))
										.addColumn(new Column(SQLiteType.TEXT, COLUMN_ICONS_PATH,""))
										.addColumn(new Column(SQLiteType.TEXT, COLUMN_BASE_PATH,""))
										.addColumn(new Column(SQLiteType.TEXT, COLUMN_SCREENS_PATH,""))
										.addColumn(new Column(SQLiteType.TEXT, COLUMN_NAME,""))
										.createTable();
		
		
		creator.newTable(TABLE_LATEST_APK).addColumn(new Column(SQLiteType.INTEGER, COLUMN__ID).setPrimaryKey())
										  .addColumn(new Column(SQLiteType.TEXT, COLUMN_APKID))
										  .addColumn(new Column(SQLiteType.TEXT, COLUMN_NAME))
										  .addColumn(new Column(SQLiteType.TEXT,COLUMN_VERNAME))
										  .addColumn(new Column(SQLiteType.INTEGER, COLUMN_VERCODE))
										  .addColumn(new Column(SQLiteType.TEXT, COLUMN_ICON))
										  .addColumn(new Column(SQLiteType.INTEGER, COLUMN_DOWNLOADS))
										  .addColumn(new Column(SQLiteType.INTEGER, COLUMN_SIZE))
										  .addColumn(new Column(SQLiteType.TEXT, COLUMN_RATING))
										  .addColumn(new Column(SQLiteType.TEXT, COLUMN_REMOTE_PATH))
										  .addColumn(new Column(SQLiteType.INTEGER, COLUMN_CATEGORY_2ND))
										  .addColumn(new Column(SQLiteType.TEXT, COLUMN_MD5))
										  .addColumn(new Column(SQLiteType.INTEGER, COLUMN_REPO_ID))
										  .addColumn(new Column(SQLiteType.DATE, COLUMN_DATE))
										  .addColumn(new Column(SQLiteType.INTEGER, COLUMN_MIN_SCREEN))
										  .addColumn(new Column(SQLiteType.INTEGER, COLUMN_MIN_SDK))
										  .addColumn(new Column(SQLiteType.REAL, COLUMN_MIN_GLES))
										  .addColumn(new Column(SQLiteType.INTEGER, COLUMN_MATURE))
										  .createTable();

		
		creator.newTable(TABLE_LATEST_SCREENSHOTS).addColumn(new Column(SQLiteType.INTEGER, COLUMN__ID))
												  .addColumn(new Column(SQLiteType.TEXT, COLUMN_REMOTE_PATH))
												  .addColumn(new Column(SQLiteType.INTEGER, COLUMN_REPO_ID))
												  .createTable();


		creator.newTable(TABLE_LATEST_REPO).addColumn(new Column(SQLiteType.INTEGER, COLUMN__ID).setPrimaryKey())
										   .addColumn(new Column(SQLiteType.TEXT, COLUMN_URL,""))
										   .addColumn(new Column(SQLiteType.TEXT, COLUMN_HASH,""))
										   .addColumn(new Column(SQLiteType.TEXT, COLUMN_ICONS_PATH,""))
										   .addColumn(new Column(SQLiteType.TEXT, COLUMN_BASE_PATH,""))
										   .addColumn(new Column(SQLiteType.TEXT, COLUMN_SCREENS_PATH,""))
										   .addColumn(new Column(SQLiteType.TEXT, COLUMN_NAME,""))
										   .createTable();
		
		db.execSQL("create table " + TABLE_REPO_CATEGORY_1ST + " (repo_id integer, " + COLUMN_CATEGORY_1ST_ID+ " integer, primary key(repo_id, " + COLUMN_CATEGORY_1ST_ID+ ") on conflict ignore);");
		db.execSQL("create table " + TABLE_REPO_CATEGORY_2ND + " (repo_id integer, " + COLUMN_CATEGORY_2ND_ID+ " integer, primary key(repo_id, " + COLUMN_CATEGORY_2ND_ID+ ") on conflict ignore);");
		
//		creator.newTable(TABLE_REPO_CATEGORY_1ST).addColumn(new Column(SQLiteType.INTEGER, COLUMN_REPO_ID).setPrimaryKey().setUnique(OnConflict.IGNORE))
//												 .addColumn(new Column(SQLiteType.INTEGER, COLUMN_CATEGORY_1ST_ID).setPrimaryKey().setUnique(OnConflict.IGNORE))
//												 .createTable();
//		
//		
//		creator.newTable(TABLE_REPO_CATEGORY_2ND).addColumn(new Column(SQLiteType.INTEGER, COLUMN_REPO_ID).setPrimaryKey().setUnique(OnConflict.IGNORE))
//												 .addColumn(new Column(SQLiteType.INTEGER, COLUMN_CATEGORY_2ND_ID).setPrimaryKey().setUnique(OnConflict.IGNORE))
//												 .createTable();
		
		
		creator.newTable(TABLE_INSTALLED).addColumn(new Column(SQLiteType.INTEGER, COLUMN__ID))
										 .addColumn(new Column(SQLiteType.TEXT, COLUMN_APKID))
										 .addColumn(new Column(SQLiteType.INTEGER, COLUMN_VERCODE))
										 .addColumn(new Column(SQLiteType.TEXT, COLUMN_VERNAME))
										 .addColumn(new Column(SQLiteType.TEXT, COLUMN_NAME))
										 .createTable();
		
		
		creator.newTable(TABLE_FEATURED_TOP_APK).addColumn(new Column(SQLiteType.INTEGER, COLUMN__ID).setPrimaryKey())
									  	        .addColumn(new Column(SQLiteType.TEXT,    COLUMN_APKID))
									  	        .addColumn(new Column(SQLiteType.TEXT,    COLUMN_NAME))
									  	        .addColumn(new Column(SQLiteType.TEXT,    COLUMN_VERNAME))
									  	        .addColumn(new Column(SQLiteType.INTEGER, COLUMN_VERCODE))
									  	        .addColumn(new Column(SQLiteType.TEXT,    COLUMN_ICON))
									  	        .addColumn(new Column(SQLiteType.INTEGER, COLUMN_DOWNLOADS))
									  	        .addColumn(new Column(SQLiteType.INTEGER, COLUMN_SIZE))
									  	        .addColumn(new Column(SQLiteType.TEXT,    COLUMN_RATING))
									  	        .addColumn(new Column(SQLiteType.TEXT,    COLUMN_REMOTE_PATH))
									  	        .addColumn(new Column(SQLiteType.INTEGER, COLUMN_CATEGORY_2ND))
									  	        .addColumn(new Column(SQLiteType.TEXT,    COLUMN_MD5))
									  	        .addColumn(new Column(SQLiteType.INTEGER, COLUMN_REPO_ID))
									  	        .addColumn(new Column(SQLiteType.DATE,    COLUMN_DATE))
									  	        .addColumn(new Column(SQLiteType.INTEGER, COLUMN_MIN_SCREEN))
									  	        .addColumn(new Column(SQLiteType.INTEGER, COLUMN_MIN_SDK))
									  	        .addColumn(new Column(SQLiteType.REAL,    COLUMN_MIN_GLES))
									  	        .addColumn(new Column(SQLiteType.INTEGER, COLUMN_MATURE))
									  	        .createTable();
		
		
		creator.newTable(TABLE_FEATURED_TOP_REPO).addColumn(new Column(SQLiteType.INTEGER, COLUMN__ID).setPrimaryKey())
												 .addColumn(new Column(SQLiteType.TEXT,    COLUMN_URL,""))
												 .addColumn(new Column(SQLiteType.TEXT,    COLUMN_HASH,""))
												 .addColumn(new Column(SQLiteType.TEXT,    COLUMN_ICONS_PATH,""))
												 .addColumn(new Column(SQLiteType.TEXT,    COLUMN_BASE_PATH,""))
												 .addColumn(new Column(SQLiteType.TEXT,    COLUMN_SCREENS_PATH,""))
												 .addColumn(new Column(SQLiteType.TEXT,    COLUMN_NAME,""))
												 .createTable();
		
		
		creator.newTable(TABLE_FEATURED_TOP_SCREENSHOTS).addColumn(new Column(SQLiteType.INTEGER, COLUMN__ID))
												     	.addColumn(new Column(SQLiteType.TEXT,    COLUMN_REMOTE_PATH))
												    	.createTable();
		
		
		creator.newTable(TABLE_FEATURED_EDITORSCHOICE_APK).addColumn(new Column(SQLiteType.INTEGER, COLUMN__ID).setPrimaryKey())
														  .addColumn(new Column(SQLiteType.TEXT,    COLUMN_APKID))
														  .addColumn(new Column(SQLiteType.TEXT,    COLUMN_NAME))
														  .addColumn(new Column(SQLiteType.TEXT,    COLUMN_VERNAME))
														  .addColumn(new Column(SQLiteType.INTEGER, COLUMN_VERCODE))
														  .addColumn(new Column(SQLiteType.TEXT,    COLUMN_ICON))
														  .addColumn(new Column(SQLiteType.INTEGER, COLUMN_DOWNLOADS))
														  .addColumn(new Column(SQLiteType.INTEGER, COLUMN_SIZE))
														  .addColumn(new Column(SQLiteType.TEXT,    COLUMN_RATING))
														  .addColumn(new Column(SQLiteType.TEXT,    COLUMN_REMOTE_PATH))
														  .addColumn(new Column(SQLiteType.INTEGER, COLUMN_CATEGORY_2ND))
														  .addColumn(new Column(SQLiteType.TEXT,    COLUMN_MD5))
														  .addColumn(new Column(SQLiteType.INTEGER, COLUMN_REPO_ID))
														  .addColumn(new Column(SQLiteType.DATE,    COLUMN_DATE))
														  .addColumn(new Column(SQLiteType.INTEGER, COLUMN_MIN_SCREEN))
														  .addColumn(new Column(SQLiteType.INTEGER, COLUMN_MIN_SDK))
														  .addColumn(new Column(SQLiteType.REAL,    COLUMN_MIN_GLES))
														  .addColumn(new Column(SQLiteType.INTEGER, COLUMN_MATURE))
														  .addColumn(new Column(SQLiteType.INTEGER, COLUMN_FEATURED_GRAPHIC))
														  .addColumn(new Column(SQLiteType.INTEGER, COLUMN_FEATURED_HIGHLIGHTED))
														  .createTable();
		
		
		creator.newTable(TABLE_FEATURED_EDITORSCHOICE_REPO).addColumn(new Column(SQLiteType.INTEGER, COLUMN__ID).setPrimaryKey())
														   .addColumn(new Column(SQLiteType.TEXT, COLUMN_URL,""))
														   .addColumn(new Column(SQLiteType.TEXT, COLUMN_ICONS_PATH,""))
														   .addColumn(new Column(SQLiteType.TEXT, COLUMN_BASE_PATH,""))
														   .addColumn(new Column(SQLiteType.TEXT, COLUMN_SCREENS_PATH,""))
														   .addColumn(new Column(SQLiteType.TEXT, COLUMN_NAME,""))
														   .addColumn(new Column(SQLiteType.TEXT, COLUMN_FEATURED_GRAPHICS_PATH,""))
														   .createTable();
		
		
		creator.newTable(TABLE_FEATURED_EDITORSCHOICE_SCREENSHOTS).addColumn(new Column(SQLiteType.INTEGER, COLUMN__ID))
																  .addColumn(new Column(SQLiteType.TEXT,    COLUMN_REMOTE_PATH))
																  .createTable();
		
		
		
		creator.newTable(TABLE_ITEMBASED_APK).addColumn(new Column(SQLiteType.INTEGER, COLUMN__ID).setPrimaryKey())
											 .addColumn(new Column(SQLiteType.TEXT,    COLUMN_APKID))
											 .addColumn(new Column(SQLiteType.TEXT,    COLUMN_NAME))
											 .addColumn(new Column(SQLiteType.TEXT,    COLUMN_VERNAME))
											 .addColumn(new Column(SQLiteType.INTEGER, COLUMN_VERCODE))
											 .addColumn(new Column(SQLiteType.TEXT,    COLUMN_ICON))
											 .addColumn(new Column(SQLiteType.INTEGER, COLUMN_DOWNLOADS))
											 .addColumn(new Column(SQLiteType.INTEGER, COLUMN_SIZE))
											 .addColumn(new Column(SQLiteType.TEXT,    COLUMN_RATING))
											 .addColumn(new Column(SQLiteType.TEXT,    COLUMN_REMOTE_PATH))
											 .addColumn(new Column(SQLiteType.INTEGER, COLUMN_CATEGORY_2ND))
											 .addColumn(new Column(SQLiteType.TEXT,    COLUMN_MD5))
											 .addColumn(new Column(SQLiteType.INTEGER, COLUMN_REPO_ID))
											 .addColumn(new Column(SQLiteType.DATE,    COLUMN_DATE))
											 .addColumn(new Column(SQLiteType.INTEGER, COLUMN_MIN_SCREEN))
											 .addColumn(new Column(SQLiteType.INTEGER, COLUMN_MIN_SDK))
											 .addColumn(new Column(SQLiteType.REAL,    COLUMN_MIN_GLES))
											 .addColumn(new Column(SQLiteType.INTEGER, COLUMN_MATURE))
											 .addColumn(new Column(SQLiteType.TEXT, COLUMN_PARENT_APKID))
											 .createTable();
		
		creator.newTable(TABLE_ITEMBASED_REPO).addColumn(new Column(SQLiteType.INTEGER, COLUMN__ID).setPrimaryKey())
											  .addColumn(new Column(SQLiteType.TEXT,    COLUMN_URL,""))
											  .addColumn(new Column(SQLiteType.TEXT,    COLUMN_ICONS_PATH,""))
											  .addColumn(new Column(SQLiteType.TEXT,    COLUMN_BASE_PATH,""))
											  .addColumn(new Column(SQLiteType.TEXT,    COLUMN_SCREENS_PATH,""))
											  .addColumn(new Column(SQLiteType.TEXT,    COLUMN_NAME,""))
											  .createTable();
		
		creator.newTable(TABLE_ITEMBASED_HASHES).addColumn(new Column(SQLiteType.TEXT, COLUMN_HASH))
												.addColumn(new Column(SQLiteType.TEXT, COLUMN_APKID).setPrimaryKey().setUnique(OnConflict.REPLACE))
												.createTable();
		
		
		creator.newTable(TABLE_ITEMBASED_SCREENSHOTS).addColumn(new Column(SQLiteType.INTEGER, COLUMN__ID))
		   											 .addColumn(new Column(SQLiteType.TEXT,    COLUMN_REMOTE_PATH))
		   											 .addColumn(new Column(SQLiteType.INTEGER, COLUMN_REPO_ID))
		   											 .createTable();
		
		
		creator.newTable(TABLE_SCHEDULED).addColumn(new Column(SQLiteType.INTEGER, COLUMN__ID).setPrimaryKey())
										 .addColumn(new Column(SQLiteType.TEXT,    COLUMN_NAME))
										 .addColumn(new Column(SQLiteType.TEXT,    COLUMN_APKID))
										 .addColumn(new Column(SQLiteType.INTEGER, COLUMN_VERCODE))
										 .addColumn(new Column(SQLiteType.TEXT,    COLUMN_VERNAME))
										 .addColumn(new Column(SQLiteType.TEXT,    COLUMN_REMOTE_PATH))
										 .addColumn(new Column(SQLiteType.TEXT,    COLUMN_MD5))
										 .createTable();
		
		creator.newTable(TABLE_EXCLUDED_APKID).addColumn(new Column(SQLiteType.TEXT, COLUMN_APKID)).createTable();
		
		ContentValues values = new ContentValues();
		values.put(COLUMN__ID, Database.TOP_ID);
		values.put(COLUMN_NAME, "Top Apps");
		db.insert(TABLE_CATEGORY_1ST, null, values);
		
		values = new ContentValues();
		values.put(COLUMN__ID, Database.LATEST_ID);
		values.put(COLUMN_NAME, "Latest Apps");
		db.insert(TABLE_CATEGORY_1ST, null, values);
//		db.execSQL("create table scheduled (_id integer primary key, name text, apkid text, vercode integer, vername text, remotepath text, md5 text );");

		
		
//		db.execSQL("create table apk (_id integer primary key, apkid text, name text, vername text, vercode integer, imagepath text, downloads integer, size integer,rating text,path text, category2 integer, md5 text,  repo_id integer, date date, minscreen int, minsdk int, mingles real, mature int, exclude_update boolean default false);");
//		db.execSQL("create table category1 (_id integer primary key, name text , size integer, unique (name) on conflict ignore) ;");
//		db.execSQL("create table category2 (_id integer primary key, catg1_id integer, name text , size integer, unique (name) on conflict ignore);");
//		db.execSQL("create table repo (_id integer primary key, url text default '', delta text, appcount integer, iconspath text default '', basepath text default '', status text default '', webservicespath text default '', username text, password text, avatar text default '', name text default '', downloads integer default 0, apkpath text default '');");
//		db.execSQL("create table toprepo_extra (_id integer, top_delta text, screenspath text, category text,iconspath text, basepath text, url text, name text);");
		
//		db.execSQL("create table installed (apkid text, vercode integer, vername text, name text);");
//		db.execSQL("create table dynamic_apk (_id integer primary key, apkid text, name text, vername text, vercode integer, imagepath text, downloads integer, size integer, rating text,path text, category1 integer, md5 text, repo_id integer, minscreen int, minsdk int, mingles real, mature int);");
//		db.execSQL("create table screenshots (_id integer, type integer, path text, repo_id integer);");
//		db.execSQL("create table itembasedapkrepo (_id integer primary key, name text, basepath text, iconspath text, screenspath text, featuredgraphicpath text);");
//		db.execSQL("create table itembasedapk (_id integer primary key, itembasedapkrepo_id integer, apkid text, name text, vercode integer, vername text, category2 text, downloads integer, rating text, icon text, md5 text, path text, size integer, parent_apkid text, featuredgraphic text, highlight text, minscreen int, minsdk int, mingles real, mature int);");
//		db.execSQL("create table itembasedapk_hash (hash text, apkid text primary key, unique (apkid) on conflict replace);");
//		db.execSQL("create table userbasedapk (_id integer primary key, apkid text, vercode integer);");
//		db.execSQL("create table scheduled (_id integer primary key, name text, apkid text, vercode integer, vername text, remotepath text, md5 text );");
//		db.execSQL("CREATE INDEX mytest_id2_idx ON installed(apkid);");
		db.execSQL("CREATE INDEX mytest_id_idx ON apk(apkid,vercode,category_2nd,repo_id);");
//		db.execSQL("CREATE INDEX mytest_id3_idx ON dynamic_apk(apkid,vercode,category1,repo_id);");
		
		
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		db.execSQL("DROP TABLE IF EXISTS apk");
		db.execSQL("DROP TABLE IF EXISTS category1");
		db.execSQL("DROP TABLE IF EXISTS category2");
		db.execSQL("DROP TABLE IF EXISTS repo");
		db.execSQL("DROP TABLE IF EXISTS toprepo_extra");
		db.execSQL("DROP TABLE IF EXISTS repo_category1");
		db.execSQL("DROP TABLE IF EXISTS repo_category2");
		db.execSQL("DROP TABLE IF EXISTS installed");
		db.execSQL("DROP TABLE IF EXISTS dynamic_apk");
		db.execSQL("DROP TABLE IF EXISTS itembasedapkrepo");
		db.execSQL("DROP TABLE IF EXISTS itembasedapk");
		db.execSQL("DROP TABLE IF EXISTS itembasedapk_hash");
		db.execSQL("DROP TABLE IF EXISTS userbasedapk");
		db.execSQL("DROP TABLE IF EXISTS scheduled");
		db.execSQL("DROP TABLE IF EXISTS screenshots");
		
		onCreate(db);
	}
	
	

}
