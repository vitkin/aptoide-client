/**
 * Constants, part of Aptoide
 * Copyright (C) 2011 Duarte Silveira
 * duarte.silveira@caixamagica.pt
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
package cm.aptoide.pt.data;

import android.os.Environment;
import cm.aptoide.pt.debug.EnumLogLevels;

/**
 * Constants, static java values repository
 *
 * @author dsilveira
 * @since 3.0
 *
 */
public class Constants {
	
	public static final EnumLogLevels LOG_LEVEL_FILTER = EnumLogLevels.DEBUG;
	
	public static final int ARRAY_INDEX_FROM_SIZE_CORRECTION = 1;
	public static final int KBYTES_TO_BYTES = 1024;
	public static final int EMPTY_INT = 0;
	public static final int NO_SCREEN = 0;
	
	public static final String PATH_CACHE = Environment.getExternalStorageDirectory().getPath() + "/.aptoide/";
	public static final String PATH_CACHE_ICONS = PATH_CACHE + "icons/";
	public static final String URI_LATEST_VERSION_XML = "http://aptoide.com/latest_version.xml";
	public static final String FILE_SELF_UPDATE = PATH_CACHE + "latestSelfUpdate.apk";	//TODO possibly change apk name to reflect version code
	public static final String FILE_LATEST_MYAPP = PATH_CACHE + "latest.myapp";
	public static final String FILE_PREFERENCES = "aptoide_preferences";
	
	public static final String SERVICE_DATA_CLASS_NAME = "cm.aptoide.pt.data.ServiceData";
	

	/**  repoHashid + uri + size + inUse + requiresLogin + login */
	public static final int NUMBER_OF_DISPLAY_FIELDS_REPO = 6;

	/**  appHashid + iconCachePath + appName + stars + downloads + upToDateVersionName  */
	public static final int NUMBER_OF_DISPLAY_FIELDS_APP_AVAILABLE = 6;
	/**  appHashid + iconCachePath + appName + installedVersionName + isUpdatable + upToDateVersionName + isDowngradable + downgradeVersionName  */
	public static final int NUMBER_OF_DISPLAY_FIELDS_APP_INSTALLED = 8;
	/**  appHashid + iconCachePath + appName + installedVersionName + upToDateVersionName  */
	public static final int NUMBER_OF_DISPLAY_FIELDS_APP_UPDATE = 5;
	
	
	public static final String DISPLAY_REPO_REQUIRES_LOGIN = "requires_login";
	public static final String DISPLAY_REPO_LOGIN = "login";
	public static final String DISPLAY_APP_ICON_CACHE_PATH = "iconCachePath";
	public static final String DISPLAY_APP_INSTALLED_VERSION_NAME = "installedVersionName";
	public static final String DISPLAY_APP_IS_UPDATABLE = "isUpdatable";
	public static final String DISPLAY_APP_UP_TO_DATE_VERSION_NAME = "upToDateVersionName";
	public static final String DISPLAY_APP_IS_DOWNGRADABLE = "isDowngradable";
	public static final String DISPLAY_APP_DOWNGRADE_VERSION_NAME = "downgradeVersionName";
	
	public static final String DISPLAY_APP_UP_TO_DATE_VERSION_CODE = "upToDateVersionCode";
	public static final String DISPLAY_APP_DOWNGRADE_VERSION_CODE = "downgradeVersionCode";
	//TODO create static display fields for the ones that are using keyes - change in unit tests and display views
	
	// **************************** Database definitions ********************************* //
	
	//TODO deprecate
	public static final String[] CATEGORIES = {"Comics", "Communication", "Entertainment", "Finance", "Health", "Lifestyle", "Multimedia", 
			 "News & Weather", "Productivity", "Reference", "Shopping", "Social", "Sports", "Themes", "Tools", 
			 "Travel, Demo", "Software Libraries", "Arcade & Action", "Brain & Puzzle", "Cards & Casino", "Casual"};
	
	/** stupid sqlite doesn't know booleans */
	public static final int DB_TRUE = 1;
	/** stupid sqlite doesn't know booleans */
	public static final int DB_FALSE = 0;
	public static final int DB_ERROR = -1;
	/** 0 affected rows */
	public static final int DB_NO_CHANGES_MADE = 0;
	
	/** has no parent, so no parent hashid */
	public static final int TOP_CATEGORY = 0;
	
	public static final int COLUMN_FIRST = 0;
	public static final int COLUMN_SECOND = 1;
	public static final int COLUMN_THIRD = 2;
	public static final int COLUMN_FOURTH = 3;
	public static final int COLUMN_FIFTH = 4;
	public static final int COLUMN_SIXTH = 5;
	public static final int COLUMN_SEVENTH = 6;
	public static final int COLUMN_EIGTH = 7;
	public static final int COLUMN_NINTH = 8;			
	
	
	
	/** 
	 * 		HashIds are the hashcodes of the real E-A primary keys separated by pipe symbols. 
	 *		Reasoning behind them is that sqlite is noticeably more efficient
	 *		handling integer indexes than text ones.
	 *		If for a table the primary key column is declared as INTEGER PRIMARY KEY
	 *		this value is used as rowid, thus searching for a value in this column takes
	 *		only one search in table's B-tree, making it twice as fast as any other index search.  
	 *
	 *		Primary Key collision is a possibility due to java's hascode algorithm, 
	 *		but, I expect, highly unlikely for our use case. Anyway, if it does happen,
	 *		hashids can still be used to speed up queries, we'll simply have to change db's PKs
	 *		to their actual entity keys to avoid collisions, or maybe add an auto-increment integer id.
	 *
	 *		For future reference:
	 *			for android <= 2.1 the version of sqlite is 3.5.9, 
	 *			lacking the following features:
	 *				* Foreign key constraints	(only since 3.6.19)
	 *				* recursive triggers		(only since 3.6.18)
	 *				* Stored procedures
	 *				* multiple value inserts
	 *				* booleans
	 *				* ... 			
	 */
	
	/**  force compatibility with all android versions */
	public static final String PRAGMA_FOREIGN_KEYS_OFF = "PRAGMA foreign_keys=OFF;";
	/**  force compatibility with all android versions */
	public static final String PRAGMA_RECURSIVE_TRIGGERS_OFF = "PRAGMA recursive_triggers=OFF;";
	
	
	public static final String DATABASE = "aptoide_db";
	
	
	public static final String TABLE_REPOSITORY = "repository";
	/** base: uri */
	public static final String KEY_REPO_HASHID = "repo_hashid";
	public static final String KEY_REPO_URI = "uri";
	public static final String KEY_REPO_BASE_PATH = "base_path";
	/** relative path from basepath */
	public static final String KEY_REPO_ICONS_PATH = "icons_path";
	/** relative path from basepath */
	public static final String KEY_REPO_SCREENS_PATH = "screens_path";	
	public static final String KEY_REPO_SIZE = "repo_size";
	/** identifies a single version of all non volatile xml files */
	public static final String KEY_REPO_DELTA = "delta";
	public static final String KEY_REPO_IN_USE = "in_use";
	public static final int NUMBER_OF_COLUMNS_REPO = 8;
	
	
	public static final String TABLE_LOGIN = "login";
	public static final String KEY_LOGIN_REPO_HASHID = KEY_REPO_HASHID;
	public static final String KEY_LOGIN_USERNAME = "username";
	public static final String KEY_LOGIN_PASSWORD = "password";
	public static final int NUMBER_OF_COLUMNS_LOGIN = 3;
	
	
	public static final String TABLE_APPLICATION = "application";
	/** base: package_name|versioncode|repo_hashid */
	public static final String KEY_APPLICATION_FULL_HASHID = "app_full_hashid";
	public static final String KEY_APPLICATION_REPO_HASHID = KEY_REPO_HASHID;
	/** base: package_name|versioncode */
	public static final String KEY_APPLICATION_HASHID = "app_hashid";
	public static final String KEY_APPLICATION_PACKAGE_NAME = "package_name";
	public static final String KEY_APPLICATION_VERSION_CODE = "version_code";
	public static final String KEY_APPLICATION_VERSION_NAME = "version_name";
	public static final String KEY_APPLICATION_NAME = "app_name";		//TODO maybe create index, consider changing columns order to increase lookup performance
	public static final String KEY_APPLICATION_RATING = "rating";
	public static final int NUMBER_OF_COLUMNS_APPLICATION = 8;
	
	
	public static final String TABLE_CATEGORY = "category";
	/** base: category_name */
	public static final String KEY_CATEGORY_HASHID = "category_hashid";
	public static final String KEY_CATEGORY_NAME = "category_name";		//TODO maybe create index, consider changing columns order to increase lookup performance
	public static final int NUMBER_OF_COLUMNS_CATEGORY = 2;
	
	
	public static final String TABLE_SUB_CATEGORY = "sub_category";
	public static final String KEY_SUB_CATEGORY_PARENT = "category_parent";
	public static final String KEY_SUB_CATEGORY_CHILD = "category_child";
	public static final int NUMBER_OF_COLUMNS_SUB_CATEGORY = 2;
	
	
	public static final String TABLE_APP_CATEGORY = "app_category";
	public static final String KEY_APP_CATEGORY_CATEGORY_HASHID = KEY_CATEGORY_HASHID;
	public static final String KEY_APP_CATEGORY_APP_FULL_HASHID = KEY_APPLICATION_FULL_HASHID;
	public static final int NUMBER_OF_COLUMNS_APP_CATEGORY = 2;
	
	
	public static final String TABLE_APP_INSTALLED = "app_installed";
	/** base: package_name|versioncode */
	public static final String KEY_APP_INSTALLED_HASHID = KEY_APPLICATION_HASHID;	
	public static final String KEY_APP_INSTALLED_PACKAGE_NAME = "package_name";
	public static final String KEY_APP_INSTALLED_VERSION_CODE = "version_code";
	public static final String KEY_APP_INSTALLED_VERSION_NAME = "version_name"; 
	public static final String KEY_APP_INSTALLED_NAME = "app_name";		//TODO maybe create index, consider changing columns order to increase lookup performance
	public static final int NUMBER_OF_COLUMNS_APP_INSTALLED = 5;
	
	
	public static final String TABLE_ICON_INFO = "icon_info";
	public static final String KEY_ICON_APP_FULL_HASHID = KEY_APPLICATION_FULL_HASHID;
	public static final String KEY_ICON_MD5HASH = "icon_md5hash";
	public static final int NUMBER_OF_COLUMNS_ICON_INFO = 2;
	
	
	public static final String TABLE_DOWNLOAD_INFO = "download_info";
	public static final String KEY_DOWNLOAD_APP_FULL_HASHID = KEY_APPLICATION_FULL_HASHID;
	public static final String KEY_DOWNLOAD_REMOTE_PATH_TAIL = "remote_path_tail";
	public static final String KEY_DOWNLOAD_MD5HASH = "md5hash";
	public static final String KEY_DOWNLOAD_SIZE = "download_size";
	public static final int NUMBER_OF_COLUMNS_DOWNLOAD_INFO = 4;
	
	
	public static final String TABLE_STATS_INFO = "stats_info";
	public static final String KEY_STATS_APP_FULL_HASHID = KEY_APPLICATION_FULL_HASHID;
	public static final String KEY_STATS_DOWNLOADS = "downloads";
	/** receives only one xml tag: likes|dislikes that feeds these next 3 columns after processing */
	public static final String KEY_STATS_STARS = "stars";
	public static final String KEY_STATS_LIKES = "likes";
	public static final String KEY_STATS_DISLIKES = "dislikes";
	public static final int NUMBER_OF_COLUMNS_STATS_INFO = 5;
	
	
	public static final String TABLE_EXTRA_INFO = "extra_info";
	public static final String KEY_EXTRA_APP_FULL_HASHID = KEY_APPLICATION_FULL_HASHID;
	public static final String KEY_EXTRA_DESCRIPTION = "description";
	public static final int NUMBER_OF_COLUMNS_EXTRA_INFO = 2;
	
	
	public static final String TABLE_APP_COMMENTS = "app_comments";
	public static final String KEY_APP_COMMENTS_APP_FULL_HASHID = KEY_APPLICATION_FULL_HASHID;	//TODO create index
	public static final String KEY_APP_COMMENT_ID = "comment_id";
	public static final String KEY_APP_COMMENT = "comment";
	public static final int NUMBER_OF_COLUMNS_APP_COMMENTS = 3;
	
	
	/**
	 * Table definitions
	 * 
	 */
	
	public static final String CREATE_TABLE_REPOSITORY = "CREATE TABLE IF NOT EXISTS " + TABLE_REPOSITORY + " ("
			+ KEY_REPO_HASHID + " INTEGER PRIMARY KEY NOT NULL, "
			+ KEY_REPO_URI + " TEXT NOT NULL, "
			+ KEY_REPO_BASE_PATH + " TEXT NOT NULL, "
			+ KEY_REPO_ICONS_PATH + " TEXT NOT NULL, "
			+ KEY_REPO_SCREENS_PATH + " TEXT NOT NULL, "
			+ KEY_REPO_SIZE + " INTEGER NOT NULL DEFAULT (0) CHECK ("+KEY_REPO_SIZE+">=0), "
			+ KEY_REPO_DELTA + " TEXT NOT NULL DEFAULT (0), "
			+ KEY_REPO_IN_USE + " INTEGER NOT NULL DEFAULT (1) ); ";		/** stupid sqlite doesn't know booleans */
//			+ "PRIMARY KEY("+ KEY_REPO_HASHID +") );";
	
	public static final String FOREIGN_KEY_UPDATE_REPO_REPO_HASHID_STRONG = "foreign_key_update_repo_repo_hashid_strong";
	public static final String FOREIGN_KEY_DELETE_REPO = "foreign_key_delete_repo";

	
	
	public static final String CREATE_TABLE_LOGIN = "CREATE TABLE IF NOT EXISTS " + TABLE_LOGIN + " ("
			+ KEY_LOGIN_REPO_HASHID + " INTEGER PRIMARY KEY NOT NULL, "
			+ KEY_LOGIN_USERNAME + " TEXT NOT NULL, "
			+ KEY_LOGIN_PASSWORD + " TEXT NOT NULL, "
			+ "FOREIGN KEY("+ KEY_LOGIN_REPO_HASHID +") REFERENCES "+ TABLE_REPOSITORY +"("+ KEY_REPO_HASHID +") );"; 
//			+ "PRIMARY KEY("+ KEY_LOGIN_REPO_HASHID +") );";
	
	public static final String FOREIGN_KEY_INSERT_LOGIN = "foreign_key_insert_login_repo";
	public static final String FOREIGN_KEY_UPDATE_LOGIN_REPO_HASHID_WEAK = "foreign_key_update_login_repo_hashid_weak";

	
	
	public static final String CREATE_TABLE_APPLICATION = "CREATE TABLE IF NOT EXISTS " + TABLE_APPLICATION + " ("
			+ KEY_APPLICATION_FULL_HASHID + " INTEGER PRIMARY KEY NOT NULL, "
			+ KEY_APPLICATION_REPO_HASHID + " INTEGER NOT NULL, "
			+ KEY_APPLICATION_HASHID + " INTEGER NOT NULL, "
			+ KEY_APPLICATION_PACKAGE_NAME + " TEXT NOT NULL, "
			+ KEY_APPLICATION_VERSION_CODE + " INTEGER NOT NULL CHECK ("+KEY_APPLICATION_VERSION_CODE+">=0), "
			+ KEY_APPLICATION_VERSION_NAME + " TEXT NOT NULL, "
			+ KEY_APPLICATION_NAME + " TEXT NOT NULL, "
			+ KEY_APPLICATION_RATING + " INTEGER NOT NULL CHECK ("+KEY_APPLICATION_RATING+">0), "
			+ "FOREIGN KEY("+ KEY_APPLICATION_REPO_HASHID +") REFERENCES "+ TABLE_REPOSITORY +"("+ KEY_REPO_HASHID +") );"; 
//			+ "PRIMARY KEY("+ KEY_APPLICATION_FULL_HASHID +") );";	

	public static final String FOREIGN_KEY_INSERT_APPLICATION = "foreign_key_insert_application";
	public static final String FOREIGN_KEY_UPDATE_APPLICATION_REPO_HASHID_WEAK = "foreign_key_update_application_repo_hashid_weak";

	public static final String FOREIGN_KEY_UPDATE_APPLICATION_APP_FULL_HASHID_STRONG = "foreign_key_update_application_app_full_hashid_strong";
	public static final String FOREIGN_KEY_DELETE_APPLICATION = "foreign_key_delete_application";

	
	
	public static final String CREATE_TABLE_CATEGORY = "CREATE TABLE IF NOT EXISTS " + TABLE_CATEGORY + " ("
			+ KEY_CATEGORY_HASHID + " INTEGER PRIMARY KEY NOT NULL, "
			+ KEY_CATEGORY_NAME + " TEXT NOT NULL); ";
//			+ "PRIMARY KEY("+ KEY_CATEGORY_HASHID +"));";

	public static final String FOREIGN_KEY_UPDATE_CATEGORY_CATEGORY_HASHID_STRONG = "foreign_key_update_category_hashid_strong";
	public static final String FOREIGN_KEY_DELETE_CATEGORY = "foreign_key_delete_category";

	
	
	public static final String CREATE_TABLE_SUB_CATEGORY = "CREATE TABLE IF NOT EXISTS " + TABLE_SUB_CATEGORY + " ("
			+ KEY_SUB_CATEGORY_CHILD + " INTEGER PRIMARY KEY NOT NULL, "
			+ KEY_SUB_CATEGORY_PARENT + " INTEGER NOT NULL, "
			+ "FOREIGN KEY("+ KEY_SUB_CATEGORY_PARENT +") REFERENCES "+ TABLE_CATEGORY +"("+ KEY_CATEGORY_HASHID +"),"
			+ "FOREIGN KEY("+ KEY_SUB_CATEGORY_CHILD +") REFERENCES "+ TABLE_CATEGORY +"("+ KEY_CATEGORY_HASHID +") );";
//			+ "PRIMARY KEY("+ KEY_SUB_CATEGORY_CHILD +"));";	

	public static final String FOREIGN_KEY_INSERT_SUB_CATEGORY = "foreign_key_insert_sub_category";
	public static final String FOREIGN_KEY_UPDATE_SUB_CATEGORY_PARENT_WEAK = "foreign_key_update_sub_category_parent_weak";
	public static final String FOREIGN_KEY_UPDATE_SUB_CATEGORY_CHILD_WEAK = "foreign_key_update_sub_category_child_weak";

	
	
	public static final String CREATE_TABLE_APP_CATEGORY = "CREATE TABLE IF NOT EXISTS " + TABLE_APP_CATEGORY + " ("
			+ KEY_APP_CATEGORY_APP_FULL_HASHID + " INTEGER PRIMARY KEY NOT NULL, "
			+ KEY_APP_CATEGORY_CATEGORY_HASHID + " INTEGER NOT NULL, "
			+ "FOREIGN KEY("+ KEY_APP_CATEGORY_CATEGORY_HASHID +") REFERENCES "+ TABLE_CATEGORY +"("+ KEY_CATEGORY_HASHID +"),"
			+ "FOREIGN KEY("+ KEY_APP_CATEGORY_APP_FULL_HASHID +") REFERENCES "+ TABLE_APPLICATION +"("+ KEY_APPLICATION_FULL_HASHID +") );";
//			+ "PRIMARY KEY("+ KEY_APP_CATEGORY_APP_FULL_HASHID +"));";
	
	public static final String FOREIGN_KEY_INSERT_APP_CATEGORY = "foreign_key_insert_app_category";
	public static final String FOREIGN_KEY_UPDATE_APP_CATEGORY_CATEGORY_HASHID_WEAK = "foreign_key_update_app_category_category_hashid_weak";
	public static final String FOREIGN_KEY_UPDATE_APP_CATEGORY_APP_FULL_HASHID_WEAK = "foreign_key_update_app_category_app_full_hashid_weak";

	
	
	//TODO table install later pk = fk app_full_hash_id + triggers
	
	
	
	public static final String CREATE_TABLE_APP_INSTALLED = "CREATE TABLE IF NOT EXISTS " + TABLE_APP_INSTALLED + " ("
			+ KEY_APP_INSTALLED_HASHID + " INTEGER PRIMARY KEY NOT NULL, "
			+ KEY_APP_INSTALLED_PACKAGE_NAME + " TEXT NOT NULL, "
			+ KEY_APP_INSTALLED_VERSION_CODE + " INTEGER NOT NULL CHECK ("+KEY_APPLICATION_VERSION_CODE+">=0), "
			+ KEY_APP_INSTALLED_VERSION_NAME + " TEXT NOT NULL, "
			+ KEY_APP_INSTALLED_NAME + " TEXT NOT NULL); ";
//			+ "PRIMARY KEY("+ KEY_APP_INSTALLED_HASHID +") );";	

	public static final String DROP_TABLE_APP_INSTALLED = "DROP TABLE IF EXISTS "+ TABLE_APP_INSTALLED;
	
	//TODO table never update pk = fk hashid from installed + triggers
	
	
	
	public static final String CREATE_TABLE_ICON_INFO = "CREATE TABLE IF NOT EXISTS " + TABLE_ICON_INFO + " ("
			+ KEY_ICON_APP_FULL_HASHID + " INTEGER PRIMARY KEY NOT NULL, "
			+ KEY_ICON_MD5HASH + " TEXT NOT NULL, "
			+ "FOREIGN KEY("+ KEY_ICON_APP_FULL_HASHID +") REFERENCES "+ TABLE_APPLICATION +"("+ KEY_APPLICATION_FULL_HASHID +") );";
//			+ "PRIMARY KEY("+ KEY_ICON_APP_FULL_HASHID +"));";

	public static final String FOREIGN_KEY_INSERT_ICON_INFO = "foreign_key_insert_icon_info";
	public static final String FOREIGN_KEY_UPDATE_ICON_INFO_APP_FULL_HASHID_WEAK = "foreign_key_update_icon_info_app_full_hashid_weak";
	
	
	public static final String CREATE_TABLE_DOWNLOAD_INFO = "CREATE TABLE IF NOT EXISTS " + TABLE_DOWNLOAD_INFO + " ("
			+ KEY_DOWNLOAD_APP_FULL_HASHID + " INTEGER PRIMARY KEY NOT NULL, "
			+ KEY_DOWNLOAD_REMOTE_PATH_TAIL + " TEXT NOT NULL, "
			+ KEY_DOWNLOAD_MD5HASH + " TEXT NOT NULL, "
			+ KEY_DOWNLOAD_SIZE + " INTEGER NOT NULL CHECK ("+KEY_DOWNLOAD_SIZE+">0), "
			+ "FOREIGN KEY("+ KEY_DOWNLOAD_APP_FULL_HASHID +") REFERENCES "+ TABLE_APPLICATION +"("+ KEY_APPLICATION_FULL_HASHID +") );";
//			+ "PRIMARY KEY("+ KEY_DOWNLOAD_APP_FULL_HASHID +") );";
	
	public static final String FOREIGN_KEY_INSERT_DOWNLOAD_INFO = "foreign_key_insert_download_info";
	public static final String FOREIGN_KEY_UPDATE_DOWNLOAD_INFO_APP_FULL_HASHID_WEAK = "foreign_key_update_download_info_app_full_hashid_weak";

	
	
	public static final String CREATE_TABLE_STATS_INFO = "CREATE TABLE IF NOT EXISTS " + TABLE_STATS_INFO + " ("
			+ KEY_STATS_APP_FULL_HASHID + " INTEGER PRIMARY KEY NOT NULL, "
			+ KEY_STATS_DOWNLOADS + " INTEGER NOT NULL CHECK ("+KEY_STATS_DOWNLOADS+">=0), "
			+ KEY_STATS_STARS + " REAL NOT NULL CHECK ("+KEY_STATS_STARS+">=0), "
			+ KEY_STATS_LIKES + " INTEGER NOT NULL CHECK ("+KEY_STATS_LIKES+">=0), "
			+ KEY_STATS_DISLIKES + " INTEGER NOT NULL CHECK ("+KEY_STATS_DISLIKES+">=0), "
			+ "FOREIGN KEY("+ KEY_STATS_APP_FULL_HASHID +") REFERENCES "+ TABLE_APPLICATION +"("+ KEY_APPLICATION_FULL_HASHID +") );";
//			+ "PRIMARY KEY("+ KEY_STATS_APP_FULL_HASHID +") );";

	public static final String FOREIGN_KEY_INSERT_STATS_INFO = "foreign_key_insert_stats_info";
	public static final String FOREIGN_KEY_UPDATE_STATS_INFO_APP_FULL_HASHID_WEAK = "foreign_key_update_stats_info_app_full_hashid_weak";

	
	
	public static final String CREATE_TABLE_EXTRA_INFO = "CREATE TABLE IF NOT EXISTS " + TABLE_EXTRA_INFO + " ("
			+ KEY_EXTRA_APP_FULL_HASHID + " INTEGER PRIMARY KEY NOT NULL, "
			+ KEY_EXTRA_DESCRIPTION + " TEXT NOT NULL, "
			+ "FOREIGN KEY("+ KEY_EXTRA_APP_FULL_HASHID +") REFERENCES "+ TABLE_APPLICATION +"("+ KEY_APPLICATION_FULL_HASHID +") );";
//			+ "PRIMARY KEY("+ KEY_EXTRA_APP_FULL_HASHID +") );";

	public static final String FOREIGN_KEY_INSERT_EXTRA_INFO = "foreign_key_insert_extra_info";
	public static final String FOREIGN_KEY_UPDATE_EXTRA_INFO_APP_FULL_HASHID_WEAK = "foreign_key_update_extra_info_app_full_hashid_weak";

	
	
	public static final String CREATE_TABLE_APP_COMMENTS = "CREATE TABLE IF NOT EXISTS " + TABLE_APP_COMMENTS + " ("
			+ KEY_APP_COMMENT_ID + " INTEGER PRIMARY KEY NOT NULL CHECK ("+KEY_APP_COMMENT_ID+">0), "
			+ KEY_APP_COMMENTS_APP_FULL_HASHID + " INTEGER NOT NULL, "
			+ KEY_APP_COMMENT + " TEXT NOT NULL, "
			+ "FOREIGN KEY("+ KEY_APP_COMMENTS_APP_FULL_HASHID +") REFERENCES "+ TABLE_APPLICATION +"("+ KEY_APPLICATION_FULL_HASHID +") );";
//			+ "PRIMARY KEY("+ KEY_APP_COMMENT_ID +") );";

	public static final String FOREIGN_KEY_INSERT_APP_COMMENT = "foreign_key_insert_app_comment";
	public static final String FOREIGN_KEY_UPDATE_APP_COMMENT_APP_FULL_HASHID_WEAK = "foreign_key_update_app_comment_app_full_hashid_weak";

	
	
	/**
	 * Triggers,	Stupid sqlite only constraints foreign keys after 3.6.19 which means android 2.2
	 * 				only hope of implementing those constraints is by using triggers, as explained in this sqlite wiki webpage:
	 * 				http://www.sqlite.org/cvstrac/wiki?p=ForeignKeyTriggers	 * 
	 */
	
	public static final String CREATE_TRIGGER_REPO_UPDATE_REPO_HASHID_STRONG = "CREATE TRIGGER IF NOT EXISTS "+ FOREIGN_KEY_UPDATE_REPO_REPO_HASHID_STRONG
			+ " AFTER UPDATE OF "+ KEY_REPO_HASHID  +" ON " + TABLE_REPOSITORY
			+ " FOR EACH ROW BEGIN"
			+ "     UPDATE "+ TABLE_LOGIN +" SET "+ KEY_LOGIN_REPO_HASHID +" = NEW."+ KEY_REPO_HASHID +" WHERE "+ KEY_LOGIN_REPO_HASHID +" = OLD."+ KEY_REPO_HASHID +";"
			+ "     UPDATE "+ TABLE_APPLICATION +" SET "+ KEY_APPLICATION_REPO_HASHID +" = NEW."+ KEY_REPO_HASHID +" WHERE "+ KEY_APPLICATION_REPO_HASHID +" = OLD."+ KEY_REPO_HASHID +";"
			+ " END;";
	
	public static final String CREATE_TRIGGER_REPO_DELETE_REPO = "CREATE TRIGGER IF NOT EXISTS "+ FOREIGN_KEY_DELETE_REPO
			+ " BEFORE DELETE ON " + TABLE_REPOSITORY
			+ " FOR EACH ROW BEGIN"
			+ "     DELETE FROM "+ TABLE_LOGIN +" WHERE "+ KEY_LOGIN_REPO_HASHID +" = OLD."+ KEY_REPO_HASHID +";"
			+ "     DELETE FROM "+ TABLE_APPLICATION +" WHERE "+ KEY_APPLICATION_REPO_HASHID +" = OLD."+ KEY_REPO_HASHID +";"	//TODO since there are no chained triggers check if this correctly deletes all app's childs
			+ " END;";
	
	
	
	
	public static final String CREATE_TRIGGER_LOGIN_INSERT = "CREATE TRIGGER IF NOT EXISTS "+ FOREIGN_KEY_INSERT_LOGIN
			+ " BEFORE INSERT ON " + TABLE_LOGIN
			+ " FOR EACH ROW BEGIN"
			+ "     SELECT RAISE(ROLLBACK, 'insert on table "+ TABLE_LOGIN +" violates foreign key constraint "+ FOREIGN_KEY_INSERT_LOGIN +"')"
			+ "     WHERE NEW."+ KEY_LOGIN_REPO_HASHID +" IS NOT NULL"
			+ "	        AND (SELECT "+ KEY_REPO_HASHID +" FROM "+ TABLE_REPOSITORY +" WHERE "+ KEY_REPO_HASHID +" = NEW."+ KEY_LOGIN_REPO_HASHID +") IS NULL;"
			+ " END;";
	
	public static final String CREATE_TRIGGER_LOGIN_UPDATE_REPO_HASHID_WEAK = "CREATE TRIGGER IF NOT EXISTS "+ FOREIGN_KEY_UPDATE_LOGIN_REPO_HASHID_WEAK
			+ " BEFORE UPDATE OF "+ KEY_LOGIN_REPO_HASHID  +" ON " + TABLE_LOGIN
			+ " FOR EACH ROW BEGIN"
			+ "    SELECT RAISE(ROLLBACK, 'update on table "+ TABLE_LOGIN +" violates foreign key constraint "+ FOREIGN_KEY_UPDATE_LOGIN_REPO_HASHID_WEAK +"')"
			+ "    WHERE (SELECT "+ KEY_REPO_HASHID +" FROM "+ TABLE_REPOSITORY +" WHERE "+ KEY_REPO_HASHID +" = NEW."+ KEY_LOGIN_REPO_HASHID +") IS NULL;"
			+ " END;";
	
	
	
	
	public static final String CREATE_TRIGGER_APPLICATION_INSERT = "CREATE TRIGGER IF NOT EXISTS "+ FOREIGN_KEY_INSERT_APPLICATION
			+ " BEFORE INSERT ON " + TABLE_APPLICATION
			+ " FOR EACH ROW BEGIN"
			+ "     SELECT RAISE(ROLLBACK, 'insert on table "+ TABLE_APPLICATION +" violates foreign key constraint "+ FOREIGN_KEY_INSERT_APPLICATION +"')"
			+ "     WHERE NEW."+ KEY_APPLICATION_REPO_HASHID +" IS NOT NULL"
			+ "	        AND (SELECT "+ KEY_REPO_HASHID +" FROM "+ TABLE_REPOSITORY +" WHERE "+ KEY_REPO_HASHID +" = NEW."+ KEY_APPLICATION_REPO_HASHID +") IS NULL;"
			+ " END;";
	
	public static final String CREATE_TRIGGER_APPLICATION_UPDATE_REPO_HASHID_WEAK = "CREATE TRIGGER IF NOT EXISTS "+ FOREIGN_KEY_UPDATE_APPLICATION_REPO_HASHID_WEAK
			+ " BEFORE UPDATE OF "+ KEY_APPLICATION_REPO_HASHID  +" ON " + TABLE_APPLICATION
			+ " FOR EACH ROW BEGIN"
			+ "    SELECT RAISE(ROLLBACK, 'update on table "+ TABLE_APPLICATION +" violates foreign key constraint "+ FOREIGN_KEY_UPDATE_APPLICATION_REPO_HASHID_WEAK +"')"
			+ "    WHERE (SELECT "+ KEY_REPO_HASHID +" FROM "+ TABLE_REPOSITORY +" WHERE "+ KEY_REPO_HASHID +" = NEW."+ KEY_APPLICATION_REPO_HASHID +") IS NULL;"
			+ " END;";

	
	public static final String CREATE_TRIGGER_APPLICATION_UPDATE_APP_FULL_HASHID_STRONG = "CREATE TRIGGER IF NOT EXISTS "+ FOREIGN_KEY_UPDATE_APPLICATION_APP_FULL_HASHID_STRONG
			+ " AFTER UPDATE OF "+ KEY_APPLICATION_FULL_HASHID  +" ON " + TABLE_APPLICATION
			+ " FOR EACH ROW BEGIN"
			+ "     UPDATE "+ TABLE_APP_CATEGORY +" SET "+ KEY_APP_CATEGORY_APP_FULL_HASHID +" = NEW."+ KEY_APPLICATION_FULL_HASHID +" WHERE "+ KEY_APP_CATEGORY_APP_FULL_HASHID +" = OLD."+ KEY_APPLICATION_FULL_HASHID +";"
			+ "     UPDATE "+ TABLE_ICON_INFO +" SET "+ KEY_ICON_APP_FULL_HASHID +" = NEW."+ KEY_APPLICATION_FULL_HASHID +" WHERE "+ KEY_ICON_APP_FULL_HASHID +" = OLD."+ KEY_APPLICATION_FULL_HASHID +";"
			+ "     UPDATE "+ TABLE_DOWNLOAD_INFO +" SET "+ KEY_DOWNLOAD_APP_FULL_HASHID +" = NEW."+ KEY_APPLICATION_FULL_HASHID +" WHERE "+ KEY_DOWNLOAD_APP_FULL_HASHID +" = OLD."+ KEY_APPLICATION_FULL_HASHID +";"
			+ "     UPDATE "+ TABLE_STATS_INFO +" SET "+ KEY_STATS_APP_FULL_HASHID +" = NEW."+ KEY_APPLICATION_FULL_HASHID +" WHERE "+ KEY_STATS_APP_FULL_HASHID +" = OLD."+ KEY_APPLICATION_FULL_HASHID +";"
			+ "     UPDATE "+ TABLE_EXTRA_INFO +" SET "+ KEY_EXTRA_APP_FULL_HASHID +" = NEW."+ KEY_APPLICATION_FULL_HASHID +" WHERE "+ KEY_EXTRA_APP_FULL_HASHID +" = OLD."+ KEY_APPLICATION_FULL_HASHID +";"
			+ " END;";
	
	public static final String CREATE_TRIGGER_APPLICATION_DELETE = "CREATE TRIGGER IF NOT EXISTS "+ FOREIGN_KEY_DELETE_APPLICATION
			+ " BEFORE DELETE ON " + TABLE_APPLICATION
			+ " FOR EACH ROW BEGIN"
			+ "     DELETE FROM "+ TABLE_APP_CATEGORY +" WHERE "+ KEY_APP_CATEGORY_APP_FULL_HASHID +" = OLD."+ KEY_APPLICATION_FULL_HASHID +";"
			+ "     DELETE FROM "+ TABLE_ICON_INFO +" WHERE "+ KEY_ICON_APP_FULL_HASHID +" = OLD."+ KEY_APPLICATION_FULL_HASHID +";"
			+ "     DELETE FROM "+ TABLE_DOWNLOAD_INFO +" WHERE "+ KEY_DOWNLOAD_APP_FULL_HASHID +" = OLD."+ KEY_APPLICATION_FULL_HASHID +";"
			+ "     DELETE FROM "+ TABLE_STATS_INFO +" WHERE "+ KEY_STATS_APP_FULL_HASHID +" = OLD."+ KEY_APPLICATION_FULL_HASHID +";"
			+ "     DELETE FROM "+ TABLE_EXTRA_INFO +" WHERE "+ KEY_EXTRA_APP_FULL_HASHID +" = OLD."+ KEY_APPLICATION_FULL_HASHID +";"
			+ " END;";
	
	

	
	public static final String CREATE_TRIGGER_CATEGORY_UPDATE_CATEGORY_HASHID_STRONG = "CREATE TRIGGER IF NOT EXISTS "+ FOREIGN_KEY_UPDATE_CATEGORY_CATEGORY_HASHID_STRONG
			+ " AFTER UPDATE OF "+ KEY_CATEGORY_HASHID  +" ON " + TABLE_CATEGORY
			+ " FOR EACH ROW BEGIN"
			+ "     UPDATE "+ TABLE_SUB_CATEGORY +" SET "+ KEY_SUB_CATEGORY_PARENT +" = NEW."+ KEY_CATEGORY_HASHID +" WHERE "+ KEY_SUB_CATEGORY_PARENT +" = OLD."+ KEY_CATEGORY_HASHID +";"
			+ "     UPDATE "+ TABLE_SUB_CATEGORY +" SET "+ KEY_SUB_CATEGORY_PARENT +" = NEW."+ KEY_CATEGORY_HASHID +" WHERE "+ KEY_SUB_CATEGORY_CHILD +" = OLD."+ KEY_CATEGORY_HASHID +";"
			+ "     UPDATE "+ TABLE_APP_CATEGORY +" SET "+ KEY_APP_CATEGORY_CATEGORY_HASHID +" = NEW."+ KEY_CATEGORY_HASHID+" WHERE "+ KEY_APP_CATEGORY_CATEGORY_HASHID +" = OLD."+ KEY_CATEGORY_HASHID +";"
			+ " END;";
	
	public static final String CREATE_TRIGGER_CATEGORY_DELETE = "CREATE TRIGGER IF NOT EXISTS "+ FOREIGN_KEY_DELETE_CATEGORY
			+ " BEFORE DELETE ON " + TABLE_CATEGORY
			+ " FOR EACH ROW BEGIN"
			+ "     DELETE FROM "+ TABLE_SUB_CATEGORY +" WHERE "+ KEY_SUB_CATEGORY_PARENT +" = OLD."+ KEY_CATEGORY_HASHID +";"
			+ "     DELETE FROM "+ TABLE_SUB_CATEGORY +" WHERE "+ KEY_SUB_CATEGORY_CHILD +" = OLD."+ KEY_CATEGORY_HASHID +";"
			+ "     DELETE FROM "+ TABLE_APP_CATEGORY +" WHERE "+ KEY_APP_CATEGORY_CATEGORY_HASHID +" = OLD."+ KEY_CATEGORY_HASHID +";"
			+ " END;";
	
	
	
	
	public static final String CREATE_TRIGGER_SUB_CATEGORY_INSERT = "CREATE TRIGGER IF NOT EXISTS "+ FOREIGN_KEY_INSERT_SUB_CATEGORY
			+ " BEFORE INSERT ON " + TABLE_SUB_CATEGORY
			+ " FOR EACH ROW BEGIN"
			+ "     SELECT RAISE(ROLLBACK, 'insert on table "+ TABLE_SUB_CATEGORY +" violates foreign key constraint "+ FOREIGN_KEY_INSERT_SUB_CATEGORY +"')"
			+ "     WHERE (NEW."+ KEY_SUB_CATEGORY_PARENT +" IS NOT NULL"
			+ "	        	AND (SELECT "+ KEY_CATEGORY_HASHID +" FROM "+ TABLE_CATEGORY +" WHERE "+ KEY_CATEGORY_HASHID +" = NEW."+ KEY_SUB_CATEGORY_PARENT +") IS NULL)"
			+ "			OR (NEW."+ KEY_SUB_CATEGORY_CHILD +" IS NOT NULL"
			+ "	        	AND (SELECT "+ KEY_CATEGORY_HASHID +" FROM "+ TABLE_CATEGORY +" WHERE "+ KEY_CATEGORY_HASHID +" = NEW."+ KEY_SUB_CATEGORY_CHILD +") IS NULL);"
			+ " END;";
	
	public static final String CREATE_TRIGGER_SUB_CATEGORY_UPDATE_PARENT_WEAK = "CREATE TRIGGER IF NOT EXISTS "+ FOREIGN_KEY_UPDATE_SUB_CATEGORY_PARENT_WEAK
			+ " BEFORE UPDATE OF "+ KEY_SUB_CATEGORY_PARENT  +" ON " + TABLE_SUB_CATEGORY
			+ " FOR EACH ROW BEGIN"
			+ "    SELECT RAISE(ROLLBACK, 'update on table "+ TABLE_SUB_CATEGORY +" violates foreign key constraint "+ FOREIGN_KEY_UPDATE_SUB_CATEGORY_PARENT_WEAK +"')"
			+ "    WHERE (SELECT "+ KEY_CATEGORY_HASHID +" FROM "+ TABLE_CATEGORY +" WHERE "+ KEY_CATEGORY_HASHID +" = NEW."+ KEY_SUB_CATEGORY_PARENT +") IS NULL;"
			+ " END;";
	
	public static final String CREATE_TRIGGER_SUB_CATEGORY_UPDATE_CHILD_WEAK = "CREATE TRIGGER IF NOT EXISTS "+ FOREIGN_KEY_UPDATE_SUB_CATEGORY_CHILD_WEAK
			+ " BEFORE UPDATE OF "+ KEY_SUB_CATEGORY_CHILD  +" ON " + TABLE_SUB_CATEGORY
			+ " FOR EACH ROW BEGIN"
			+ "    SELECT RAISE(ROLLBACK, 'update on table "+ TABLE_SUB_CATEGORY +" violates foreign key constraint "+ FOREIGN_KEY_UPDATE_SUB_CATEGORY_CHILD_WEAK +"')"
			+ "    WHERE (SELECT "+ KEY_CATEGORY_HASHID +" FROM "+ TABLE_CATEGORY +" WHERE "+ KEY_CATEGORY_HASHID +" = NEW."+ KEY_SUB_CATEGORY_CHILD +") IS NULL;"
			+ " END;";
	
	
	
	
	public static final String CREATE_TRIGGER_APP_CATEGORY_INSERT = "CREATE TRIGGER IF NOT EXISTS "+ FOREIGN_KEY_INSERT_APP_CATEGORY
			+ " BEFORE INSERT ON " + TABLE_APP_CATEGORY
			+ " FOR EACH ROW BEGIN"
			+ "     SELECT RAISE(ROLLBACK, 'insert on table "+ TABLE_APP_CATEGORY +" violates foreign key constraint "+ FOREIGN_KEY_INSERT_APP_CATEGORY +"')"
			+ "     WHERE (NEW."+ KEY_APP_CATEGORY_APP_FULL_HASHID +" IS NOT NULL"
			+ "	        	AND (SELECT "+ KEY_APPLICATION_FULL_HASHID +" FROM "+ TABLE_APPLICATION +" WHERE "+ KEY_APPLICATION_FULL_HASHID +" = NEW."+ KEY_APP_CATEGORY_APP_FULL_HASHID +") IS NULL)"
			+ "			OR (NEW."+ KEY_APP_CATEGORY_CATEGORY_HASHID +" IS NOT NULL"
			+ "	        	AND (SELECT "+ KEY_CATEGORY_HASHID +" FROM "+ TABLE_CATEGORY +" WHERE "+ KEY_CATEGORY_HASHID +" = NEW."+ KEY_APP_CATEGORY_CATEGORY_HASHID +") IS NULL);"
			+ " END;";
	
	public static final String CREATE_TRIGGER_APP_CATEGORY_UPDATE_APP_FULL_HASHID_WEAK = "CREATE TRIGGER IF NOT EXISTS "+ FOREIGN_KEY_UPDATE_APP_CATEGORY_APP_FULL_HASHID_WEAK
			+ " BEFORE UPDATE OF "+ KEY_APP_CATEGORY_APP_FULL_HASHID +" ON " + TABLE_APP_CATEGORY
			+ " FOR EACH ROW BEGIN"
			+ "    SELECT RAISE(ROLLBACK, 'update on table "+ TABLE_APP_CATEGORY +" violates foreign key constraint "+ FOREIGN_KEY_UPDATE_APP_CATEGORY_APP_FULL_HASHID_WEAK +"')"
			+ "    WHERE (SELECT "+ KEY_APPLICATION_FULL_HASHID +" FROM "+ TABLE_APPLICATION +" WHERE "+ KEY_APPLICATION_FULL_HASHID +" = NEW."+ KEY_APP_CATEGORY_APP_FULL_HASHID +") IS NULL;"
			+ " END;";
	
	public static final String CREATE_TRIGGER_APP_CATEGORY_UPDATE_CATEGORY_HASHID_WEAK = "CREATE TRIGGER IF NOT EXISTS "+ FOREIGN_KEY_UPDATE_APP_CATEGORY_CATEGORY_HASHID_WEAK
			+ " BEFORE UPDATE OF "+ KEY_APP_CATEGORY_CATEGORY_HASHID  +" ON " + TABLE_APP_CATEGORY
			+ " FOR EACH ROW BEGIN"
			+ "    SELECT RAISE(ROLLBACK, 'update on table "+ TABLE_APP_CATEGORY +" violates foreign key constraint "+ FOREIGN_KEY_UPDATE_APP_CATEGORY_CATEGORY_HASHID_WEAK +"')"
			+ "    WHERE (SELECT "+ KEY_CATEGORY_HASHID +" FROM "+ TABLE_CATEGORY +" WHERE "+ KEY_CATEGORY_HASHID +" = NEW."+ KEY_APP_CATEGORY_CATEGORY_HASHID +") IS NULL;"
			+ " END;";
	
	
	
	
	public static final String CREATE_TRIGGER_ICON_INFO_INSERT = "CREATE TRIGGER IF NOT EXISTS "+ FOREIGN_KEY_INSERT_ICON_INFO
			+ " BEFORE INSERT ON " + TABLE_ICON_INFO
			+ " FOR EACH ROW BEGIN"
			+ "     SELECT RAISE(ROLLBACK, 'insert on table "+ TABLE_ICON_INFO +" violates foreign key constraint "+ FOREIGN_KEY_INSERT_ICON_INFO +"')"
			+ "     WHERE NEW."+ KEY_ICON_APP_FULL_HASHID +" IS NOT NULL"
			+ "	        AND (SELECT "+ KEY_APPLICATION_FULL_HASHID +" FROM "+ TABLE_APPLICATION +" WHERE "+ KEY_APPLICATION_FULL_HASHID +" = NEW."+ KEY_ICON_APP_FULL_HASHID +") IS NULL;"
			+ " END;";
	
	public static final String CREATE_TRIGGER_ICON_INFO_UPDATE_APP_FULL_HASHID_WEAK = "CREATE TRIGGER IF NOT EXISTS "+ FOREIGN_KEY_UPDATE_ICON_INFO_APP_FULL_HASHID_WEAK
			+ " BEFORE UPDATE OF "+ KEY_ICON_APP_FULL_HASHID  +" ON " + TABLE_ICON_INFO
			+ " FOR EACH ROW BEGIN"
			+ "    SELECT RAISE(ROLLBACK, 'update on table "+ TABLE_ICON_INFO +" violates foreign key constraint "+ FOREIGN_KEY_UPDATE_ICON_INFO_APP_FULL_HASHID_WEAK +"')"
			+ "    WHERE (SELECT "+ KEY_APPLICATION_FULL_HASHID +" FROM "+ TABLE_APPLICATION +" WHERE "+ KEY_APPLICATION_FULL_HASHID +" = NEW."+ KEY_ICON_APP_FULL_HASHID +") IS NULL;"
			+ " END;";
	
	
	
	
	public static final String CREATE_TRIGGER_DOWNLOAD_INFO_INSERT = "CREATE TRIGGER IF NOT EXISTS "+ FOREIGN_KEY_INSERT_DOWNLOAD_INFO
			+ " BEFORE INSERT ON " + TABLE_DOWNLOAD_INFO
			+ " FOR EACH ROW BEGIN"
			+ "     SELECT RAISE(ROLLBACK, 'insert on table "+ TABLE_DOWNLOAD_INFO +" violates foreign key constraint "+ FOREIGN_KEY_INSERT_DOWNLOAD_INFO +"')"
			+ "     WHERE NEW."+ KEY_DOWNLOAD_APP_FULL_HASHID +" IS NOT NULL"
			+ "	        AND (SELECT "+ KEY_APPLICATION_FULL_HASHID +" FROM "+ TABLE_APPLICATION +" WHERE "+ KEY_APPLICATION_FULL_HASHID +" = NEW."+ KEY_DOWNLOAD_APP_FULL_HASHID +") IS NULL;"
			+ " END;";
	
	public static final String CREATE_TRIGGER_DOWNLOAD_INFO_UPDATE_APP_FULL_HASHID_WEAK = "CREATE TRIGGER IF NOT EXISTS "+ FOREIGN_KEY_UPDATE_DOWNLOAD_INFO_APP_FULL_HASHID_WEAK
			+ " BEFORE UPDATE OF "+ KEY_DOWNLOAD_APP_FULL_HASHID  +" ON " + TABLE_DOWNLOAD_INFO
			+ " FOR EACH ROW BEGIN"
			+ "    SELECT RAISE(ROLLBACK, 'update on table "+ TABLE_DOWNLOAD_INFO +" violates foreign key constraint "+ FOREIGN_KEY_UPDATE_DOWNLOAD_INFO_APP_FULL_HASHID_WEAK +"')"
			+ "    WHERE (SELECT "+ KEY_APPLICATION_FULL_HASHID +" FROM "+ TABLE_APPLICATION +" WHERE "+ KEY_APPLICATION_FULL_HASHID +" = NEW."+ KEY_DOWNLOAD_APP_FULL_HASHID +") IS NULL;"
			+ " END;";
	
	
	
	
	public static final String CREATE_TRIGGER_STATS_INFO_INSERT = "CREATE TRIGGER IF NOT EXISTS "+ FOREIGN_KEY_INSERT_STATS_INFO
			+ " BEFORE INSERT ON " + TABLE_STATS_INFO
			+ " FOR EACH ROW BEGIN"
			+ "     SELECT RAISE(ROLLBACK, 'insert on table "+ TABLE_STATS_INFO +" violates foreign key constraint "+ FOREIGN_KEY_INSERT_STATS_INFO +"')"
			+ "     WHERE NEW."+ KEY_STATS_APP_FULL_HASHID +" IS NOT NULL"
			+ "	        AND (SELECT "+ KEY_APPLICATION_FULL_HASHID +" FROM "+ TABLE_APPLICATION +" WHERE "+ KEY_APPLICATION_FULL_HASHID +" = NEW."+ KEY_STATS_APP_FULL_HASHID +") IS NULL;"
			+ " END;";
	
	public static final String CREATE_TRIGGER_STATS_INFO_UPDATE_APP_FULL_HASHID_WEAK = "CREATE TRIGGER IF NOT EXISTS "+ FOREIGN_KEY_UPDATE_STATS_INFO_APP_FULL_HASHID_WEAK
			+ " BEFORE UPDATE OF "+ KEY_STATS_APP_FULL_HASHID  +" ON " + TABLE_STATS_INFO
			+ " FOR EACH ROW BEGIN"
			+ "    SELECT RAISE(ROLLBACK, 'update on table "+ TABLE_STATS_INFO +" violates foreign key constraint "+ FOREIGN_KEY_UPDATE_STATS_INFO_APP_FULL_HASHID_WEAK +"')"
			+ "    WHERE (SELECT "+ KEY_APPLICATION_FULL_HASHID +" FROM "+ TABLE_APPLICATION +" WHERE "+ KEY_APPLICATION_FULL_HASHID +" = NEW."+ KEY_STATS_APP_FULL_HASHID+") IS NULL;"
			+ " END;";
	
	
	
	
	public static final String CREATE_TRIGGER_EXTRA_INFO_INSERT = "CREATE TRIGGER IF NOT EXISTS "+ FOREIGN_KEY_INSERT_EXTRA_INFO
			+ " BEFORE INSERT ON " + TABLE_EXTRA_INFO
			+ " FOR EACH ROW BEGIN"
			+ "     SELECT RAISE(ROLLBACK, 'insert on table "+ TABLE_EXTRA_INFO +" violates foreign key constraint "+ FOREIGN_KEY_INSERT_EXTRA_INFO +"')"
			+ "     WHERE NEW."+ KEY_EXTRA_APP_FULL_HASHID +" IS NOT NULL"
			+ "	        AND (SELECT "+ KEY_APPLICATION_FULL_HASHID +" FROM "+ TABLE_APPLICATION +" WHERE "+ KEY_APPLICATION_FULL_HASHID +" = NEW."+ KEY_EXTRA_APP_FULL_HASHID +") IS NULL;"
			+ " END;";
	
	public static final String CREATE_TRIGGER_EXTRA_INFO_UPDATE_APP_FULL_HASHID_WEAK = "CREATE TRIGGER IF NOT EXISTS "+ FOREIGN_KEY_UPDATE_EXTRA_INFO_APP_FULL_HASHID_WEAK
			+ " BEFORE UPDATE OF "+ KEY_EXTRA_APP_FULL_HASHID  +" ON " + TABLE_EXTRA_INFO
			+ " FOR EACH ROW BEGIN"
			+ "    SELECT RAISE(ROLLBACK, 'update on table "+ TABLE_EXTRA_INFO +" violates foreign key constraint "+ FOREIGN_KEY_UPDATE_EXTRA_INFO_APP_FULL_HASHID_WEAK +"')"
			+ "    WHERE (SELECT "+ KEY_APPLICATION_FULL_HASHID +" FROM "+ TABLE_APPLICATION +" WHERE "+ KEY_APPLICATION_FULL_HASHID +" = NEW."+ KEY_EXTRA_APP_FULL_HASHID+") IS NULL;"
			+ " END;";
	
	
	
	
	public static final String CREATE_TRIGGER_APP_COMMENT_INSERT = "CREATE TRIGGER IF NOT EXISTS "+ FOREIGN_KEY_INSERT_APP_COMMENT
			+ " BEFORE INSERT ON " + TABLE_APP_COMMENTS
			+ " FOR EACH ROW BEGIN"
			+ "     SELECT RAISE(ROLLBACK, 'insert on table "+ TABLE_APP_COMMENTS +" violates foreign key constraint "+ FOREIGN_KEY_INSERT_APP_COMMENT +"')"
			+ "     WHERE NEW."+ KEY_APP_COMMENTS_APP_FULL_HASHID +" IS NOT NULL"
			+ "	        AND (SELECT "+ KEY_APPLICATION_FULL_HASHID +" FROM "+ TABLE_APPLICATION +" WHERE "+ KEY_APPLICATION_FULL_HASHID +" = NEW."+ KEY_APP_COMMENTS_APP_FULL_HASHID +") IS NULL;"
			+ " END;";
	
	public static final String CREATE_TRIGGER_APP_COMMENT_UPDATE_APP_FULL_HASHID_WEAK = "CREATE TRIGGER IF NOT EXISTS "+ FOREIGN_KEY_UPDATE_APP_COMMENT_APP_FULL_HASHID_WEAK
			+ " BEFORE UPDATE OF "+ KEY_APP_COMMENTS_APP_FULL_HASHID  +" ON " + TABLE_APP_COMMENTS
			+ " FOR EACH ROW BEGIN"
			+ "    SELECT RAISE(ROLLBACK, 'update on table "+ TABLE_APP_COMMENTS +" violates foreign key constraint "+ FOREIGN_KEY_UPDATE_APP_COMMENT_APP_FULL_HASHID_WEAK +"')"
			+ "    WHERE (SELECT "+ KEY_APPLICATION_FULL_HASHID +" FROM "+ TABLE_APPLICATION +" WHERE "+ KEY_APPLICATION_FULL_HASHID +" = NEW."+ KEY_APP_COMMENTS_APP_FULL_HASHID+") IS NULL;"
			+ " END;";
	
	
	/**
	 *  TODO Views and Indexes
	 * 
	 */
	
}
