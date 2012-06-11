package cm.aptoide.pt;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBStructure extends SQLiteOpenHelper {

	public static final String TABLE_APK = "apk";
	public static final String COLUMN_APK_ID = "_id";
	public static final String COLUMN_APK_APKID = "apkid";
	public static final String COLUMN_APK_NAME = "name";
	public static final String COLUMN_APK_VERNAME = "vername";
	public static final String COLUMN_APK_VERCODE = "vercode";
	public static final String COLUMN_APK_DOWNLOADS = "downloads";
	public static final String COLUMN_APK_RATING = "rating";
	public static final String COLUMN_APK_AGE = "age";
	public static final String COLUMN_APK_SIZE = "size";
	public static final String COLUMN_APK_MD5 = "md5";
	public static final String COLUMN_APK_PATH = "path";
	public static final String COLUMN_APK_ICON = "icon";
	public static final String COLUMN_APK_DATE = "date";
	public static final String COLUMN_APK_SDK = "sdk";
	public static final String COLUMN_APK_SCREEN = "screen";
	public static final String COLUMN_APK_OPENGLES = "opengles";
	public static final String COLUMN_APK_REPO_ID = "repo_id";
	
	public static final String TABLE_CATEGORY = "category";
	public static final String COLUMN_CATEGORY_CATEGORY1_NAME = "category1";
	public static final String COLUMN_CATEGORY_CATEGORY2_NAME = "category2";
	public static final String COLUMN_CATEGORY_APKID = "_id";
	public static final String COLUMN_CATEGORY_REPO_ID = "repo_id";
	
	public static final String TABLE_OLD = "apk_old_versions";
	public static final String COLUMN_APK_OLD_ID = "_id";
	public static final String COLUMN_APK_OLD_APKID = "apkid";
	public static final String COLUMN_APK_OLD_NAME = "name";
	public static final String COLUMN_APK_OLD_VERNAME = "vername";
	public static final String COLUMN_APK_OLD_VERCODE = "vercode";
	public static final String COLUMN_APK_OLD_CATEGORY = "category";
	public static final String COLUMN_APK_OLD_DOWNLOADS = "downloads";
	public static final String COLUMN_APK_OLD_RATING = "rating";
	public static final String COLUMN_APK_OLD_AGE = "age";
	public static final String COLUMN_APK_OLD_SIZE = "size";
	public static final String COLUMN_APK_OLD_MD5 = "md5";
	public static final String COLUMN_APK_OLD_PATH = "path";
	public static final String COLUMN_APK_OLD_ICON = "icon";
	public static final String COLUMN_APK_OLD_DATE = "date";
	public static final String COLUMN_APK_OLD_SDK = "sdk";
	public static final String COLUMN_APK_OLD_SCREEN = "screen";
	public static final String COLUMN_APK_OLD_OPENGLES = "opengles";
	public static final String COLUMN_APK_OLD_REPO_ID = "repo_id";
	
	public static final String TABLE_REPOS = "repositories";
	public static final String COLUMN_REPOS_ID = "_id";
	public static final String COLUMN_REPOS_URI = "uri";
	public static final String COLUMN_REPOS_BASEPATH = "basepath";
	public static final String COLUMN_REPOS_ICONSPATH = "iconspath";
	public static final String COLUMN_REPOS_APKPATH = "apkpath";
	public static final String COLUMN_REPOS_SCREENSPATH = "screenspath";
	public static final String COLUMN_REPOS_WSPATH = "wspath";
	public static final String COLUMN_REPOS_DELTA = "delta";
	public static final String COLUMN_REPOS_UPDATETIME = "updatetime";
	public static final String COLUMN_REPOS_NAPK = "napk";
	public static final String COLUMN_REPOS_INUSE = "inuse";
	public static final String COLUMN_REPOS_USER = "user";
	public static final String COLUMN_REPOS_PASSWORD = "password";
	public static final String COLUMN_REPOS_EXTENDED = "extended";
	
	public static final String TABLE_INSTALLED = "installed";
	public static final String COLUMN_INSTALLED_APKID = "apkid";
	public static final String COLUMN_INSTALLED_VERCODE = "vercode";
	public static final String COLUMN_INSTALLED_VERNAME = "vername";
	public static final String COLUMN_INSTALLED_NAME = "name";
	
	public static final String TABLE_SCHEDULED = "scheduled";
	public static final String COLUMN_SCHEDULED_ID = "_id";
	public static final String COLUMN_SCHEDULED_APKID = "apkid";
	public static final String COLUMN_SCHEDULED_VERNAME = "vername";
	public static final String COLUMN_SCHEDULED_NAME = "name";
	public static final String COLUMN_SCHEDULED_REPO_ID = "repo_uri";
	public static final String COLUMN_SCHEDULED_ICONSPATH = "iconspath";
	
	public static final String TABLE_EDITORSCHOICE = "editorschoice";
	public static final String COLUMN_EDITORSCHOICE_FEATUREDGRAPHIC = "featuredgraphic";
	public static final String COLUMN_EDITORSCHOICE_HIGHLIGHT = "highlighted";
			//same columns as Table APK
	
	public static final String TABLE_EDITORSCHOICEREPOS = "editorschoicerepo";
	public static final String COLUMN_EDITORSCHOICEREPO_ID = "_id";
	public static final String COLUMN_EDITORSCHOICEREPO_BASEPATH = "basepath";
	public static final String COLUMN_EDITORSCHOICEREPO_SCREENSPATH = "screenspath";
	public static final String COLUMN_EDITORSCHOICEREPO_NAME = "name";
	public static final String COLUMN_EDITORSCHOICEREPO_FEATUREDGRAPHICPATH= "featuredgraphicpath";
	public static final String COLUMN_EDITORSCHOICEREPO_ICONSPATH = "iconspath";
	public static final String COLUMN_EDITORSCHOICEREPO_HASH = "hash";
	
	public static final String TABLE_SCREENSHOTS = "screenshots";
	public static final String COLUMN_SCREENSHOTS_APKID = "_id";
	public static final String COLUMN_SCREENSHOTS_REPO_ID = "repo_id";
	public static final String COLUMN_SCREENSHOTS_SCREENSHOT = "screenshots";
	
	public static final String TABLE_FAILED_DOWNLOADS = "failed_downloads";
	public static final String COLUMN_FAILED_DOWNLOADS_REMOTEPATH = "remotePath";
	public static final String COLUMN_FAILED_DOWNLOADS_MD5 = "md5";
	public static final String COLUMN_FAILED_DOWNLOADS_PACKAGENAME = "packageName";
	public static final String COLUMN_FAILED_DOWNLOADS_APPNAME = "appName";
	public static final String COLUMN_FAILED_DOWNLOADS_SIZE = "size";
	public static final String COLUMN_FAILED_DOWNLOADS_VERSION = "version";
	public static final String COLUMN_FAILED_DOWNLOADS_LOCALPATH = "localPath";
	public static final String COLUMN_FAILED_DOWNLOADS_USERNAME = "username";
	public static final String COLUMN_FAILED_DOWNLOADS_PASSWORD = "password";

	private static final String DATABASE_NAME = "aptoide.db";
	private static final int DATABASE_VERSION = 3;

	// Database creation sql statement
	private static final String CREATE_TABLE_APK = "CREATE TABLE "
			+ TABLE_APK + " ( " + COLUMN_APK_ID	+ " integer , "
			+ COLUMN_APK_APKID+ " text , "
			+ COLUMN_APK_NAME+ " text , " 
			+ COLUMN_APK_VERNAME + " text , " 
			+ COLUMN_APK_VERCODE + " integer , " 
			+ COLUMN_APK_DOWNLOADS + " integer , " 
			+ COLUMN_APK_RATING + " float , "
			+ COLUMN_APK_AGE + " integer , " 
			+ COLUMN_APK_SIZE + " text , " 
			+ COLUMN_APK_MD5 + " text , " 
			+ COLUMN_APK_PATH + " text , " 
			+ COLUMN_APK_ICON + " text , " 
			+ COLUMN_APK_DATE + " date , " 
			+ COLUMN_APK_SDK + " integer , " 
			+ COLUMN_APK_SCREEN + " integer , " 
			+ COLUMN_APK_OPENGLES + " text ," 
			+ COLUMN_APK_REPO_ID + " text , PRIMARY KEY ("+COLUMN_APK_ID+" autoincrement)); "; 
	
	private static final String CREATE_TABLE_EDITORSCHOICE = "CREATE TABLE "
			+ TABLE_EDITORSCHOICE + " ( " + COLUMN_APK_ID	+ " integer , "
			+ COLUMN_APK_APKID+ " text , "
			+ COLUMN_APK_NAME+ " text , " 
			+ COLUMN_APK_VERNAME + " text , " 
			+ COLUMN_APK_VERCODE + " integer , " 
			+ COLUMN_APK_DOWNLOADS + " integer , " 
			+ COLUMN_APK_RATING + " float , "
			+ COLUMN_APK_AGE + " integer , " 
			+ COLUMN_APK_SIZE + " text , " 
			+ COLUMN_APK_MD5 + " text , " 
			+ COLUMN_APK_PATH + " text , " 
			+ COLUMN_APK_ICON + " text , " 
			+ COLUMN_APK_DATE + " date , " 
			+ COLUMN_APK_SDK + " integer , " 
			+ COLUMN_APK_SCREEN + " integer , " 
			+ COLUMN_APK_OPENGLES + " text ,"
			+ COLUMN_EDITORSCHOICE_HIGHLIGHT + " text ,"
			+ COLUMN_EDITORSCHOICE_FEATUREDGRAPHIC + " text ," 
			+ COLUMN_APK_REPO_ID + " integer , PRIMARY KEY ("+COLUMN_APK_ID+" autoincrement)); "; 
	
	private static final String CREATE_TABLE_OLD = "create table "
			+ TABLE_OLD + "( " + COLUMN_APK_OLD_ID
			+ " integer primary key autoincrement, "
			+ COLUMN_APK_OLD_APKID+ " text , "
			+ COLUMN_APK_OLD_NAME+ " text , " 
			+ COLUMN_APK_OLD_VERNAME + " text , " 
			+ COLUMN_APK_OLD_VERCODE + " integer , " 
			+ COLUMN_APK_OLD_CATEGORY + " text , " 
			+ COLUMN_APK_OLD_DOWNLOADS + " text , " 
			+ COLUMN_APK_OLD_RATING + " text , "
			+ COLUMN_APK_OLD_AGE + " text , " 
			+ COLUMN_APK_OLD_SIZE + " text , " 
			+ COLUMN_APK_OLD_MD5 + " text , " 
			+ COLUMN_APK_OLD_PATH + " text , " 
			+ COLUMN_APK_OLD_ICON + " text , " 
			+ COLUMN_APK_OLD_DATE + " text , " 
			+ COLUMN_APK_OLD_SDK + " text , " 
			+ COLUMN_APK_OLD_SCREEN + " text , " 
			+ COLUMN_APK_OLD_OPENGLES + " text ," 
			+ COLUMN_APK_OLD_REPO_ID + " text ); "; 
	
	
	private static final String CREATE_TABLE_CATEGORY = "create table "
			+ TABLE_CATEGORY + "(" 
			+ COLUMN_CATEGORY_CATEGORY1_NAME+ " text , "
			+ COLUMN_CATEGORY_CATEGORY2_NAME+ " text , "
			+ COLUMN_CATEGORY_APKID + " integer , " 
			+ COLUMN_CATEGORY_REPO_ID + " text , PRIMARY KEY("+COLUMN_CATEGORY_APKID+")); ";
	
	private static final String CREATE_TABLE_SCREENSHOTS = "create table "
			+ TABLE_SCREENSHOTS + "(" 
			+ COLUMN_SCREENSHOTS_APKID+ " integer , "
			+ COLUMN_SCREENSHOTS_REPO_ID+ " integer , "
			+ COLUMN_SCREENSHOTS_SCREENSHOT + " text); ";
	
	
	
	private static final String CREATE_TABLE_REPOS = "create table "
			+ TABLE_REPOS + "( " + COLUMN_REPOS_ID
			+ " integer , "
			+ COLUMN_REPOS_URI+ " text , "
			+ COLUMN_REPOS_BASEPATH+ " text , "
			+ COLUMN_REPOS_ICONSPATH+ " text , "
			+ COLUMN_REPOS_APKPATH+ " text , " 
			+ COLUMN_REPOS_SCREENSPATH + " text , " 
			+ COLUMN_REPOS_WSPATH + " text , " 
			+ COLUMN_REPOS_DELTA + " text , " 
			+ COLUMN_REPOS_UPDATETIME + " text , " 
			+ COLUMN_REPOS_NAPK + " text , "
			+ COLUMN_REPOS_INUSE + " text , " 
			+ COLUMN_REPOS_USER + " text , "
			+ COLUMN_REPOS_EXTENDED + " boolean , " 
			+ COLUMN_REPOS_PASSWORD + " text ,PRIMARY KEY ("+COLUMN_REPOS_ID+" autoincrement)); "; 
	
	private static final String CREATE_TABLE_INSTALLED = "create table "
			+ TABLE_INSTALLED + "(" 
			+ COLUMN_INSTALLED_APKID + " text , "
			+ COLUMN_INSTALLED_NAME + " text , "
			+ COLUMN_INSTALLED_VERNAME + " text , " 
			+ COLUMN_INSTALLED_VERCODE + " integer, primary key ("+COLUMN_INSTALLED_APKID+", "+COLUMN_INSTALLED_VERCODE+") ); ";
	
	private static final String CREATE_TABLE_SCHEDULED = "create table "
			+ TABLE_SCHEDULED + "(" 
			+ COLUMN_SCHEDULED_ID + " integer primary key autoincrement, "
			+ COLUMN_SCHEDULED_APKID + " text , "
			+ COLUMN_SCHEDULED_NAME + " text , "
			+ COLUMN_SCHEDULED_VERNAME + " text , "
			+ COLUMN_SCHEDULED_REPO_ID + " text, "
			+ COLUMN_SCHEDULED_ICONSPATH + " text );";
	
	private static final String CREATE_TABLE_EDITORSCHOICEREPO = "create table "
			+ TABLE_EDITORSCHOICEREPOS+ "(" 
			+ COLUMN_EDITORSCHOICEREPO_ID+ " integer primary key autoincrement, "
			+ COLUMN_EDITORSCHOICEREPO_BASEPATH + " text , "
			+ COLUMN_EDITORSCHOICEREPO_FEATUREDGRAPHICPATH + " text , "
			+ COLUMN_EDITORSCHOICEREPO_ICONSPATH + " text , "
			+ COLUMN_EDITORSCHOICEREPO_NAME + " text, "
			+ COLUMN_EDITORSCHOICEREPO_HASH + " text, "
			+ COLUMN_EDITORSCHOICEREPO_SCREENSPATH + " text );";
	
	private static final String CREATE_TABLE_FAILEDDOWNLOADS = "create table "
			+ TABLE_FAILED_DOWNLOADS+ "(" 
			+ COLUMN_FAILED_DOWNLOADS_APPNAME+ " text, "
			+ COLUMN_FAILED_DOWNLOADS_LOCALPATH + " text , "
			+ COLUMN_FAILED_DOWNLOADS_MD5 + " text , "
			+ COLUMN_FAILED_DOWNLOADS_PACKAGENAME + " text , "
			+ COLUMN_FAILED_DOWNLOADS_PASSWORD + " text, "
			+ COLUMN_FAILED_DOWNLOADS_REMOTEPATH + " text, "
			+ COLUMN_FAILED_DOWNLOADS_USERNAME + " text, "
			+ COLUMN_FAILED_DOWNLOADS_VERSION + " text, "
			+ COLUMN_FAILED_DOWNLOADS_SIZE + " text );";
	
	

	public DBStructure(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(CREATE_TABLE_APK);
		database.execSQL(CREATE_TABLE_OLD);
		database.execSQL(CREATE_TABLE_CATEGORY);
		database.execSQL(CREATE_TABLE_REPOS);
		database.execSQL(CREATE_TABLE_INSTALLED);
		database.execSQL(CREATE_TABLE_SCHEDULED);
		database.execSQL(CREATE_TABLE_EDITORSCHOICE);
		database.execSQL(CREATE_TABLE_EDITORSCHOICEREPO);
		database.execSQL(CREATE_TABLE_SCREENSHOTS);
		database.execSQL(CREATE_TABLE_FAILEDDOWNLOADS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(DBStructure.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_APK);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_OLD);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_REPOS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_INSTALLED);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCHEDULED);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_EDITORSCHOICE);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_EDITORSCHOICEREPOS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCREENSHOTS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAILED_DOWNLOADS);
		
		onCreate(db);
	}

}
