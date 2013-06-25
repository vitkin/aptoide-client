/*******************************************************************************
  * Copyright (c) 2012 rmateus.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package cm.aptoide.pt;



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import cm.aptoide.pt.util.databasecreator.Column;
import cm.aptoide.pt.util.databasecreator.Column.OnConflict;
import cm.aptoide.pt.util.databasecreator.Column.SQLiteType;
import cm.aptoide.pt.util.databasecreator.TableCreator;

import java.util.ArrayList;

public class DbStructure extends SQLiteOpenHelper {

	public final static String TABLE_TOP_LATEST_REPO_INFO= "top_latest_repo_info";
	public final static String TABLE_APK= "apk";
    public final static String TABLE_APK_SCREENSHOTS= "apk_screenshots";
    public final static String TABLE_APK_CACHE= "apk_cache";
	public final static String TABLE_REPO= "repo";
	public final static String TABLE_CATEGORY_1ST = "category_1st";
	public final static String TABLE_CATEGORY_2ND = "category_2nd";
	public final static String TABLE_TOP_REPO = "top_repo";
	public final static String TABLE_TOP_APK = "top_apk";
	public final static String TABLE_TOP_SCREENSHOTS = "top_screenshots";
    public final static String TABLE_TOP_CACHE = "top_cache";
	public final static String TABLE_LATEST_REPO = "latest_repo";
	public final static String TABLE_LATEST_APK = "latest_apk";
	public final static String TABLE_LATEST_SCREENSHOTS = "latest_screenshots";
    public final static String TABLE_LATEST_CACHE = "latest_cache";
	public final static String TABLE_REPO_CATEGORY_1ST = "repo_category_1st";
	public final static String TABLE_REPO_CATEGORY_2ND = "repo_category_2nd";
	public static final String TABLE_INSTALLED = "installed";
	public static final String TABLE_FEATURED_TOP_APK = "featured_top_apk";
	public static final String TABLE_FEATURED_TOP_REPO = "featured_top_repo";
	public static final String TABLE_FEATURED_TOP_SCREENSHOTS = "featured_top_screenshots";
    public static final String TABLE_FEATURED_TOP_CACHE = "featured_top_cache";
	public static final String TABLE_FEATURED_EDITORSCHOICE_APK = "featured_editorschoice_apk";
	public static final String TABLE_FEATURED_EDITORSCHOICE_REPO = "featured_editorschoice_repo";
	public static final String TABLE_FEATURED_EDITORSCHOICE_SCREENSHOTS = "featured_editorschoice_screenshots";
    public static final String TABLE_FEATURED_EDITORSCHOICE_CACHE = "featured_editorschoice_cache";
	public static final String TABLE_ITEMBASED_APK = "itembased_apk";
    public static final String TABLE_ITEMBASED_CACHE = "itembased_cache";
    public static final String TABLE_ITEMBASED_REPO = "itembased_repo";
	public static final String TABLE_HASHES = "hashes";
	public static final String TABLE_ITEMBASED_SCREENSHOTS = "itembased_screenshots";
	public static final String TABLE_SCHEDULED = "scheduled";
    public static final String TABLE_COMMENTS_CACHE = "comments_cache" ;




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
	public final static String COLUMN_STORE_THEME = "theme";
	public static final String COLUMN_STORE_DESCRIPTION = "description";
	public static final String COLUMN_STORE_VIEW = "view";
	public static final String COLUMN_STORE_ITEMS = "items";

	public final static String COLUMN_CATEGORY_1ST_ID = COLUMN_CATEGORY_1ST + "_id";
	public final static String COLUMN_CATEGORY_2ND_ID = COLUMN_CATEGORY_2ND + "_id";
	public static final String COLUMN_ITEMBASEDREPO_ID = "itembased_repo_id";
	public static final String COLUMN_FEATURED_GRAPHICS_PATH = "featuredgraphicspath";
	public static final String COLUMN_FEATURED_GRAPHIC = "featuredgraphic";
	public static final String COLUMN_FEATURED_HIGHLIGHTED = "highlighted";
	public static final String COLUMN_PARENT_APKID = "parent_apkid";
	public static final String TABLE_EXCLUDED_APKID = "excluded_apkid";
	public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_DISLIKES = "dislikes" ;
    public static final String COLUMN_LIKES = "likes" ;
    public static final String COLUMN_MALWARE_STATUS = "malware_status" ;
    public static final String COLUMN_MALWARE_REASON = "malware_reason" ;
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_TEXT = "text";
    public static final String COLUMN_MAINOBB_PATH = "main_obb_path" ;
    public static final String COLUMN_MAINOBB_MD5 = "main_obb_md5" ;
    public static final String COLUMN_MAINOBB_SIZE = "main_obb_size" ;
    public static final String COLUMN_MAINOBB_FILENAME = "main_obb_filename" ;
    public static final String COLUMN_PATCHOBB_PATH = "patch_obb_path" ;
    public static final String COLUMN_PATCHOBB_MD5 = "patch_obb_md5" ;
    public static final String COLUMN_PATCHOBB_SIZE = "patch_obb_size" ;
    public static final String COLUMN_PATCHOBB_FILENAME = "patch_obb_filename" ;


    public DbStructure(Context context) {
		super(context, "aptoide.db", null, 10);

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
								   .addColumn(new Column(SQLiteType.REAL, COLUMN_PRICE ))
								   .createTable();

        creator.newTable(TABLE_APK_SCREENSHOTS).addColumn(new Column(SQLiteType.INTEGER, COLUMN__ID))
                                               .addColumn(new Column(SQLiteType.TEXT, COLUMN_REMOTE_PATH))
                                               .addColumn(new Column(SQLiteType.INTEGER, COLUMN_REPO_ID))
                                               .createTable();

        creator.newTable(TABLE_APK_CACHE).addColumn(new Column(SQLiteType.INTEGER, COLUMN__ID).setUnique(OnConflict.REPLACE))
                                         .addColumn(new Column(SQLiteType.INTEGER, COLUMN_DISLIKES))
                                         .addColumn(new Column(SQLiteType.INTEGER, COLUMN_LIKES))
                                         .addColumn(new Column(SQLiteType.TEXT, COLUMN_MALWARE_STATUS))
                                         .addColumn(new Column(SQLiteType.TEXT, COLUMN_MALWARE_REASON))


                .addColumn(new Column(SQLiteType.TEXT, COLUMN_MAINOBB_PATH))
                .addColumn(new Column(SQLiteType.TEXT, COLUMN_MAINOBB_MD5))
                .addColumn(new Column(SQLiteType.TEXT, COLUMN_MAINOBB_SIZE))
                .addColumn(new Column(SQLiteType.TEXT, COLUMN_MAINOBB_FILENAME))
                .addColumn(new Column(SQLiteType.TEXT, COLUMN_PATCHOBB_PATH))
                .addColumn(new Column(SQLiteType.TEXT, COLUMN_PATCHOBB_MD5))
                .addColumn(new Column(SQLiteType.TEXT, COLUMN_PATCHOBB_SIZE))
                .addColumn(new Column(SQLiteType.TEXT, COLUMN_PATCHOBB_FILENAME))
                                         .createTable();

        creator.newTable(TABLE_COMMENTS_CACHE).addColumn(new Column(SQLiteType.INTEGER, COLUMN__ID))
                                              .addColumn(new Column(SQLiteType.INTEGER, COLUMN_TYPE))
                                              .addColumn(new Column(SQLiteType.TEXT, COLUMN_USERNAME))
                                              .addColumn(new Column(SQLiteType.TEXT, COLUMN_TEXT))
                                              .addColumn(new Column(SQLiteType.TEXT, COLUMN_DATE))
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
									.addColumn(new Column(SQLiteType.TEXT, COLUMN_HASH,"firstHash"))
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
									.addColumn(new Column(SQLiteType.TEXT, COLUMN_STORE_THEME,""))
									.addColumn(new Column(SQLiteType.TEXT, COLUMN_STORE_DESCRIPTION,""))
									.addColumn(new Column(SQLiteType.TEXT, COLUMN_STORE_VIEW,""))
									.addColumn(new Column(SQLiteType.TEXT, COLUMN_STORE_ITEMS,""))
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
											  .addColumn(new Column(SQLiteType.REAL, COLUMN_PRICE ))
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

        creator.newTable(TABLE_TOP_CACHE).addColumn(new Column(SQLiteType.INTEGER, COLUMN__ID).setUnique(OnConflict.REPLACE))
                .addColumn(new Column(SQLiteType.INTEGER, COLUMN_DISLIKES))
                .addColumn(new Column(SQLiteType.INTEGER, COLUMN_LIKES))
                .addColumn(new Column(SQLiteType.TEXT, COLUMN_MALWARE_STATUS))
                .addColumn(new Column(SQLiteType.TEXT, COLUMN_MALWARE_REASON))

                .addColumn(new Column(SQLiteType.TEXT, COLUMN_MAINOBB_PATH))
                .addColumn(new Column(SQLiteType.TEXT, COLUMN_MAINOBB_MD5))
                .addColumn(new Column(SQLiteType.TEXT, COLUMN_MAINOBB_SIZE))
                .addColumn(new Column(SQLiteType.TEXT, COLUMN_MAINOBB_FILENAME))
                .addColumn(new Column(SQLiteType.TEXT, COLUMN_PATCHOBB_PATH))
                .addColumn(new Column(SQLiteType.TEXT, COLUMN_PATCHOBB_MD5))
                .addColumn(new Column(SQLiteType.TEXT, COLUMN_PATCHOBB_SIZE))
                .addColumn(new Column(SQLiteType.TEXT, COLUMN_PATCHOBB_FILENAME))
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
										  .addColumn(new Column(SQLiteType.REAL, COLUMN_PRICE ))
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

        creator.newTable(TABLE_LATEST_CACHE).addColumn(new Column(SQLiteType.INTEGER, COLUMN__ID).setUnique(OnConflict.REPLACE))
                .addColumn(new Column(SQLiteType.INTEGER, COLUMN_DISLIKES))
                .addColumn(new Column(SQLiteType.INTEGER, COLUMN_LIKES))
                .addColumn(new Column(SQLiteType.TEXT, COLUMN_MALWARE_STATUS))
                .addColumn(new Column(SQLiteType.TEXT, COLUMN_MALWARE_REASON))

                .addColumn(new Column(SQLiteType.TEXT, COLUMN_MAINOBB_PATH))
                .addColumn(new Column(SQLiteType.TEXT, COLUMN_MAINOBB_MD5))
                .addColumn(new Column(SQLiteType.TEXT, COLUMN_MAINOBB_SIZE))
                .addColumn(new Column(SQLiteType.TEXT, COLUMN_MAINOBB_FILENAME))
                .addColumn(new Column(SQLiteType.TEXT, COLUMN_PATCHOBB_PATH))
                .addColumn(new Column(SQLiteType.TEXT, COLUMN_PATCHOBB_MD5))
                .addColumn(new Column(SQLiteType.TEXT, COLUMN_PATCHOBB_SIZE))
                .addColumn(new Column(SQLiteType.TEXT, COLUMN_PATCHOBB_FILENAME))
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
									  	        .addColumn(new Column(SQLiteType.REAL, COLUMN_PRICE ))
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

        creator.newTable(TABLE_FEATURED_TOP_CACHE).addColumn(new Column(SQLiteType.INTEGER, COLUMN__ID).setUnique(OnConflict.REPLACE))
                .addColumn(new Column(SQLiteType.INTEGER, COLUMN_DISLIKES))
                .addColumn(new Column(SQLiteType.INTEGER, COLUMN_LIKES))
                .addColumn(new Column(SQLiteType.TEXT, COLUMN_MALWARE_STATUS))
                .addColumn(new Column(SQLiteType.TEXT, COLUMN_MALWARE_REASON))

                .addColumn(new Column(SQLiteType.TEXT, COLUMN_MAINOBB_PATH))
                .addColumn(new Column(SQLiteType.TEXT, COLUMN_MAINOBB_MD5))
                .addColumn(new Column(SQLiteType.TEXT, COLUMN_MAINOBB_SIZE))
                .addColumn(new Column(SQLiteType.TEXT, COLUMN_MAINOBB_FILENAME))
                .addColumn(new Column(SQLiteType.TEXT, COLUMN_PATCHOBB_PATH))
                .addColumn(new Column(SQLiteType.TEXT, COLUMN_PATCHOBB_MD5))
                .addColumn(new Column(SQLiteType.TEXT, COLUMN_PATCHOBB_SIZE))
                .addColumn(new Column(SQLiteType.TEXT, COLUMN_PATCHOBB_FILENAME))
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
														  .addColumn(new Column(SQLiteType.REAL, COLUMN_PRICE ))
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

        creator.newTable(TABLE_FEATURED_EDITORSCHOICE_CACHE).addColumn(new Column(SQLiteType.INTEGER, COLUMN__ID).setUnique(OnConflict.REPLACE))
                .addColumn(new Column(SQLiteType.INTEGER, COLUMN_DISLIKES))
                .addColumn(new Column(SQLiteType.INTEGER, COLUMN_LIKES))
                .addColumn(new Column(SQLiteType.TEXT, COLUMN_MALWARE_STATUS))
                .addColumn(new Column(SQLiteType.TEXT, COLUMN_MALWARE_REASON))

                .addColumn(new Column(SQLiteType.TEXT, COLUMN_MAINOBB_PATH))
                .addColumn(new Column(SQLiteType.TEXT, COLUMN_MAINOBB_MD5))
                .addColumn(new Column(SQLiteType.TEXT, COLUMN_MAINOBB_SIZE))
                .addColumn(new Column(SQLiteType.TEXT, COLUMN_MAINOBB_FILENAME))
                .addColumn(new Column(SQLiteType.TEXT, COLUMN_PATCHOBB_PATH))
                .addColumn(new Column(SQLiteType.TEXT, COLUMN_PATCHOBB_MD5))
                .addColumn(new Column(SQLiteType.TEXT, COLUMN_PATCHOBB_SIZE))
                .addColumn(new Column(SQLiteType.TEXT, COLUMN_PATCHOBB_FILENAME))
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
											 .addColumn(new Column(SQLiteType.REAL, COLUMN_PRICE ))
											 .createTable();

		creator.newTable(TABLE_ITEMBASED_REPO).addColumn(new Column(SQLiteType.INTEGER, COLUMN__ID).setPrimaryKey())
											  .addColumn(new Column(SQLiteType.TEXT,    COLUMN_URL,""))
											  .addColumn(new Column(SQLiteType.TEXT,    COLUMN_ICONS_PATH,""))
											  .addColumn(new Column(SQLiteType.TEXT,    COLUMN_BASE_PATH,""))
											  .addColumn(new Column(SQLiteType.TEXT,    COLUMN_SCREENS_PATH,""))
											  .addColumn(new Column(SQLiteType.TEXT,    COLUMN_NAME,""))
											  .createTable();

        creator.newTable(TABLE_ITEMBASED_CACHE).addColumn(new Column(SQLiteType.INTEGER, COLUMN__ID).setPrimaryKey().setUnique(OnConflict.REPLACE))
                .addColumn(new Column(SQLiteType.INTEGER, COLUMN_DISLIKES))
                .addColumn(new Column(SQLiteType.INTEGER, COLUMN_LIKES))
                .addColumn(new Column(SQLiteType.TEXT, COLUMN_MALWARE_STATUS))
                .addColumn(new Column(SQLiteType.TEXT, COLUMN_MALWARE_REASON))

                .addColumn(new Column(SQLiteType.TEXT, COLUMN_MAINOBB_PATH))
                .addColumn(new Column(SQLiteType.TEXT, COLUMN_MAINOBB_MD5))
                .addColumn(new Column(SQLiteType.TEXT, COLUMN_MAINOBB_SIZE))
                .addColumn(new Column(SQLiteType.TEXT, COLUMN_MAINOBB_FILENAME))
                .addColumn(new Column(SQLiteType.TEXT, COLUMN_PATCHOBB_PATH))
                .addColumn(new Column(SQLiteType.TEXT, COLUMN_PATCHOBB_MD5))
                .addColumn(new Column(SQLiteType.TEXT, COLUMN_PATCHOBB_SIZE))
                .addColumn(new Column(SQLiteType.TEXT, COLUMN_PATCHOBB_FILENAME))
                .createTable();

		creator.newTable(TABLE_HASHES).addColumn(new Column(SQLiteType.TEXT, COLUMN_HASH))
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
										 .addColumn(new Column(SQLiteType.TEXT,    COLUMN_ICON))
                                         .addColumn(new Column(SQLiteType.TEXT,    COLUMN_MAINOBB_PATH))
                                         .addColumn(new Column(SQLiteType.TEXT,    COLUMN_MAINOBB_MD5))
                                         .addColumn(new Column(SQLiteType.TEXT,    COLUMN_MAINOBB_SIZE))
                                         .addColumn(new Column(SQLiteType.TEXT,    COLUMN_MAINOBB_FILENAME))
                                         .addColumn(new Column(SQLiteType.TEXT,    COLUMN_PATCHOBB_PATH))
                                         .addColumn(new Column(SQLiteType.TEXT,    COLUMN_PATCHOBB_MD5))
                                         .addColumn(new Column(SQLiteType.TEXT,    COLUMN_PATCHOBB_SIZE))
                                         .addColumn(new Column(SQLiteType.TEXT,    COLUMN_PATCHOBB_FILENAME))
										 .createTable();

		creator.newTable(TABLE_EXCLUDED_APKID).addColumn(new Column(SQLiteType.TEXT, COLUMN_APKID))
											  .addColumn(new Column(SQLiteType.TEXT, COLUMN_NAME))
											  .addColumn(new Column(SQLiteType.INTEGER, COLUMN_VERCODE))
											  .createTable();

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
		db.execSQL("CREATE INDEX installed_index ON installed(apkid);");
		db.execSQL("CREATE INDEX apk_index ON apk(apkid,vercode,category_2nd,repo_id);");
		db.execSQL("CREATE INDEX top_apk_index ON top_apk(apkid,vercode,repo_id);");
		db.execSQL("CREATE INDEX latest_apk_index ON latest_apk(apkid,vercode,repo_id);");
		db.execSQL("CREATE INDEX itembased_apk_index ON itembased_apk(apkid,vercode,repo_id);");


	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d("Database", "OnUpgrade");
		ArrayList<Server> oldServers = new ArrayList<Server>();
		try{
			Cursor c = db.query("repo", new String[]{"url","name"}, null, null, null, null, null);
			for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){
				Server server = new Server();
				server.name=c.getString(1);
				server.url=c.getString(0);
				oldServers.add(server);
			}
			c.close();
		}catch (Exception e){
			e.printStackTrace();
		}


        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMMENTS_CACHE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCHEDULED);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMBASED_SCREENSHOTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HASHES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMBASED_REPO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMBASED_CACHE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMBASED_APK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FEATURED_EDITORSCHOICE_CACHE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FEATURED_EDITORSCHOICE_SCREENSHOTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FEATURED_EDITORSCHOICE_REPO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FEATURED_EDITORSCHOICE_APK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FEATURED_TOP_CACHE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FEATURED_TOP_SCREENSHOTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FEATURED_TOP_REPO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FEATURED_TOP_APK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INSTALLED);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REPO_CATEGORY_2ND);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REPO_CATEGORY_1ST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LATEST_CACHE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LATEST_SCREENSHOTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LATEST_APK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LATEST_REPO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TOP_CACHE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TOP_SCREENSHOTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TOP_APK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TOP_REPO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY_2ND);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY_1ST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REPO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APK_CACHE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APK_SCREENSHOTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TOP_LATEST_REPO_INFO);
        db.execSQL("DROP INDEX IF EXISTS " + "apk.apk_index");
        db.execSQL("DROP INDEX IF EXISTS " + "installed.installed_index");
        db.execSQL("DROP INDEX IF EXISTS " + "top_apk.top_apk_index");
        db.execSQL("DROP INDEX IF EXISTS " + "latest_apk.latest_apk_index");
        db.execSQL("DROP INDEX IF EXISTS " + "itembased_apk.itembased_apk_index");

        onCreate(db);

		for(Server oldServer : oldServers){
			ContentValues values = new ContentValues();
			values.put(COLUMN_URL, oldServer.url);
			values.put(COLUMN_NAME, oldServer.name);
			values.put(COLUMN_STATUS, "PARSED");
			values.put(COLUMN_HASH, "firstHash");
			db.insert("repo", null, values);
			Log.d("Database", oldServer.url);
		}


        ApplicationAptoide.setRestartLauncher(true);


	}



}
