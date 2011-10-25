/*
 * Constants.java, part of Aptoide
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

/**
 * Constants
 *
 * @author dsilveira
 *
 */
public class Constants {
	public static final int KBYTES_TO_BYTES = 1024;
	public static final String CACHE_PATH = Environment.getExternalStorageDirectory().getPath() + "/.aptoide/";
	public static final String SELF_UPDATE_FILE = CACHE_PATH + "latestSelfUpdate.apk";	//TODO possibly change apk name to reflect version code
	public static final String LATEST_VERSION_CODE_URI = "http://aptoide.com/latest_version.xml";
	
	// **************************** Database definitions ********************************* //
	
	//TODO deprecate
	public static final String[] CATEGORIES = {"Comics", "Communication", "Entertainment", "Finance", "Health", "Lifestyle", "Multimedia", 
			 "News & Weather", "Productivity", "Reference", "Shopping", "Social", "Sports", "Themes", "Tools", 
			 "Travel, Demo", "Software Libraries", "Arcade & Action", "Brain & Puzzle", "Cards & Casino", "Casual"};
	
	public static final int DB_TRUE = 1;
	public static final int DB_FALSE = 0;
	public static final int DB_ERROR = -1;
	
	/** HashIds are the hashcodes of the real E-A primary keyes separated by pipe symbols. 
	 *			Reasoning behind them is that sqlite is noticeably more efficient
	 *			handling integet indexes than text ones. 
	 *
	 *			Primary Key collision is a possibility due to java's hascode, 
	 *			but, I expect, highly unlikely for our use case. Anyway, if it does happen,
	 *			hashids can still be used to speed up querys, we'll simply have to change db's PKs
	 *			to their actual entity keys to avoid collisions, or maybe add an autoincrement integer id.
	 */ 
	
	public static final String DATABASE = "aptoide_db";
	
	public static final String TABLE_REPOSITORY = "repository";
	public static final String KEY_REPO_HASHID = "repo_hashid";	/** base: uri */
	public static final String KEY_REPO_URI = "uri";
	public static final String KEY_REPO_BASE_PATH = "base_path";
	public static final String KEY_REPO_SIZE = "repo_size";
	public static final String KEY_REPO_UPDATE_TIME = "update_time";
	public static final String KEY_REPO_DELTA = "delta";
	public static final String KEY_REPO_IN_USE = "in_use";
	public static final int NUMBER_OF_COLUMNS_REPO = 7;
	
	public static final String TABLE_LOGIN = "login";
	public static final String KEY_LOGIN_REPO_HASHID = "repo_hashid";
	public static final String KEY_LOGIN_USERNAME = "username";
	public static final String KEY_LOGIN_PASSWORD = "password";
	public static final int NUMBER_OF_COLUMNS_LOGIN = 3;
	
	public static final String TABLE_APPLICATION = "application";
	public static final String KEY_APP_FULL_HASHID = "app_full_hashid";	/** base: package_name|versioncode|repo_hashid */
	public static final String KEY_APP_REPO_HASHID = "repo_hashid";	
	public static final String KEY_APP_HASHID = "app_hashid";				/** base: package_name|versioncode */
	public static final String KEY_APP_PACKAGE_NAME = "package_name";
	public static final String KEY_APP_VERSION_CODE = "version_code";
	public static final String KEY_APP_VERSION_NAME = "version_name";
	public static final String KEY_APP_NAME = "app_name";
	public static final int NUMBER_OF_COLUMNS_APP = 7;
	
	public static final String TABLE_CATEGORY = "category";
	public static final String KEY_CATEGORY_HASHID = "category_hashid";	/** base: category_name */
	public static final String KEY_CATEGORY_NAME = "category_name";
	public static final int NUMBER_OF_COLUMNS_CATEGORY = 2;
	
	public static final String TABLE_SUB_CATEGORY = "sub_category";
	public static final String KEY_SUB_CATEGORY_PARENT = "category_parent";
	public static final String KEY_SUB_CATEGORY_CHILD = "category_child";
	public static final int NUMBER_OF_COLUMNS_SUB_CATEGORY = 2;
	
	public static final String TABLE_APP_CATEGORY = "app_category";
	public static final String KEY_APP_CATEGORY_CATEGORY_HASHID = "category_hashid";
	public static final String KEY_APP_CATEGORY_APP_FULL_HASHID = "app_full_hashid";
	public static final int NUMBER_OF_COLUMNS_APP_CATEGORY = 2;
	
	public static final String TABLE_APP_INSTALLED = "app_installed";
	public static final String KEY_APP_INSTALLED_HASHID = "app_hashid";	/** base: package_name|versioncode */
	public static final String KEY_APP_INSTALLED_PACKAGE_NAME = "package_name";
	public static final String KEY_APP_INSTALLED_VERSION_CODE = "version_code";
	public static final String KEY_APP_INSTALLED_VERSION_NAME = "version_name";
	public static final String KEY_APP_INSTALLED_NAME = "app_name";
	public static final int NUMBER_OF_COLUMNS_APP_INSTALLED = 5;
	
	public static final String TABLE_ICON = "icon";
	public static final String KEY_ICON_APP_FULL_HASHID = "app_full_hashid";
	public static final String KEY_ICON_REMOTE_PATH_TAIL = "remote_icon_path_tail";
	public static final int NUMBER_OF_COLUMNS_ICON = 2;
	
	public static final String TABLE_DOWNLOAD = "download";
	public static final String KEY_DOWNLOAD_APP_FULL_HASHID = "app_full_hashid";
	public static final String KEY_DOWNLOAD_REMOTE_PATH_TAIL = "remote_path_tail";
	public static final String KEY_DOWNLOAD_MD5HASH = "md5hash";
	public static final String KEY_DOWNLOAD_SIZE = "download_size";
	public static final int NUMBER_OF_COLUMNS_DOWNLOAD = 4;
	
	public static final String TABLE_EXTRA = "extra";
	public static final String KEY_EXTRA_APP_FULL_HASHID = "app_full_hashid";
	public static final String KEY_EXTRA_DESCRIPTION = "description";
	public static final String KEY_EXTRA_DATE = "extra_date";
	public static final String KEY_EXTRA_RATING = "rating";
	public static final String KEY_EXTRA_POPULARITY = "popularity";
	public static final int NUMBER_OF_COLUMNS_EXTRA = 5;
	
	
	/**
	 * Table definitions
	 * 
	 */
	
	public static final String CREATE_TABLE_REPOSITORY = "CREATE TABLE IF NOT EXISTS " + TABLE_REPOSITORY + " ("
			+ KEY_REPO_HASHID + " INTEGER NOT NULL, "
			+ KEY_REPO_URI + " TEXT UNIQUE NOT NULL, "
			+ KEY_REPO_BASE_PATH + " TEXT UNIQUE NOT NULL, "
			+ KEY_REPO_SIZE + " INTEGER NOT NULL DEFAULT (0) CHECK ("+KEY_REPO_SIZE+">=0), "
			+ KEY_REPO_UPDATE_TIME + " TEXT NOT NULL DEFAULT (0), "
			+ KEY_REPO_DELTA + " TEXT NOT NULL DEFAULT (0), "
			+ KEY_REPO_IN_USE + " INTEGER NOT NULL DEFAULT (1), "		/** stupid sqlite doesn't know booleans */
			+ "PRIMARY KEY("+ KEY_REPO_HASHID +") );";
	
	public static final String FOREIGN_KEY_UPDATE_REPO_REPO_HASHID_STRONG = "foreign_key_update_repo_repo_hashid_strong";
	public static final String FOREIGN_KEY_DELETE_REPO = "foreign_key_delete_repo";

	
	
	public static final String CREATE_TABLE_LOGIN = "CREATE TABLE IF NOT EXISTS " + TABLE_LOGIN + " ("
			+ KEY_LOGIN_REPO_HASHID + " INTEGER NOT NULL, "
			+ KEY_LOGIN_USERNAME + " TEXT NOT NULL, "
			+ KEY_LOGIN_PASSWORD + " TEXT NOT NULL, "
			+ "FOREIGN KEY("+ KEY_LOGIN_REPO_HASHID +") REFERENCES "+ TABLE_REPOSITORY +"("+ KEY_REPO_HASHID +")," 
			+ "PRIMARY KEY("+ KEY_LOGIN_REPO_HASHID +") );";
	
	public static final String FOREIGN_KEY_INSERT_LOGIN = "foreign_key_insert_login_repo";
	public static final String FOREIGN_KEY_UPDATE_LOGIN_REPO_HASHID_WEAK = "foreign_key_update_login_repo_hashid_weak";

	
	
	public static final String CREATE_TABLE_APPLICATION = "CREATE TABLE IF NOT EXISTS " + TABLE_APPLICATION + " ("
			+ KEY_APP_FULL_HASHID + " INTEGER NOT NULL, "
			+ KEY_APP_REPO_HASHID + " INTEGER NOT NULL, "
			+ KEY_APP_HASHID + " INTEGER NOT NULL, "
			+ KEY_APP_PACKAGE_NAME + " TEXT NOT NULL, "
			+ KEY_APP_VERSION_CODE + " INTEGER NOT NULL CHECK ("+KEY_APP_VERSION_CODE+">=0), "
			+ KEY_APP_VERSION_NAME + " TEXT NOT NULL, "
			+ KEY_APP_NAME + " TEXT NOT NULL, "
			+ "FOREIGN KEY("+ KEY_APP_REPO_HASHID +") REFERENCES "+ TABLE_REPOSITORY +"("+ KEY_REPO_HASHID +")," 
			+ "PRIMARY KEY("+ KEY_APP_FULL_HASHID +") );";	

	public static final String FOREIGN_KEY_INSERT_APPLICATION = "foreign_key_insert_application";
	public static final String FOREIGN_KEY_UPDATE_APPLICATION_REPO_HASHID_WEAK = "foreign_key_update_application_repo_hashid_weak";

	public static final String FOREIGN_KEY_UPDATE_APPLICATION_APP_FULL_HASHID_STRONG = "foreign_key_update_application_app_full_hashid_strong";
	public static final String FOREIGN_KEY_DELETE_APPLICATION = "foreign_key_delete_application";

	
	
	public static final String CREATE_TABLE_CATEGORY = "CREATE TABLE IF NOT EXISTS " + TABLE_CATEGORY + " ("
			+ KEY_CATEGORY_HASHID + " INTEGER NOT NULL, "
			+ KEY_CATEGORY_NAME + " TEXT NOT NULL, "
			+ "PRIMARY KEY("+ KEY_CATEGORY_HASHID +"));";

	public static final String FOREIGN_KEY_UPDATE_CATEGORY_CATEGORY_HASHID_STRONG = "foreign_key_update_category_hashid_strong";
	public static final String FOREIGN_KEY_DELETE_CATEGORY = "foreign_key_delete_category";

	
	
	public static final String CREATE_TABLE_SUB_CATEGORY = "CREATE TABLE IF NOT EXISTS " + TABLE_SUB_CATEGORY + " ("
			+ KEY_SUB_CATEGORY_PARENT + " INTEGER NOT NULL, "
			+ KEY_SUB_CATEGORY_CHILD + " INTEGER NOT NULL, "
			+ "FOREIGN KEY("+ KEY_SUB_CATEGORY_PARENT +") REFERENCES "+ TABLE_CATEGORY +"("+ KEY_CATEGORY_HASHID +"),"
			+ "FOREIGN KEY("+ KEY_SUB_CATEGORY_CHILD +") REFERENCES "+ TABLE_CATEGORY +"("+ KEY_CATEGORY_HASHID +"),"
			+ "PRIMARY KEY("+ KEY_SUB_CATEGORY_CHILD +"));";	

	public static final String FOREIGN_KEY_INSERT_SUB_CATEGORY = "foreign_key_insert_sub_category";
	public static final String FOREIGN_KEY_UPDATE_SUB_CATEGORY_PARENT_WEAK = "foreign_key_update_sub_category_parent_weak";
	public static final String FOREIGN_KEY_UPDATE_SUB_CATEGORY_CHILD_WEAK = "foreign_key_update_sub_category_child_weak";

	
	
	public static final String CREATE_TABLE_APP_CATEGORY = "CREATE TABLE IF NOT EXISTS " + TABLE_APP_CATEGORY + " ("
			+ KEY_APP_CATEGORY_CATEGORY_HASHID + " INTEGER NOT NULL, "
			+ KEY_APP_CATEGORY_APP_FULL_HASHID + " INTEGER NOT NULL, "
			+ "FOREIGN KEY("+ KEY_APP_CATEGORY_CATEGORY_HASHID +") REFERENCES "+ TABLE_CATEGORY +"("+ KEY_CATEGORY_HASHID +"),"
			+ "FOREIGN KEY("+ KEY_APP_CATEGORY_APP_FULL_HASHID +") REFERENCES "+ TABLE_APPLICATION +"("+ KEY_APP_FULL_HASHID +"),"
			+ "PRIMARY KEY("+ KEY_APP_CATEGORY_APP_FULL_HASHID +"));";
	
	public static final String FOREIGN_KEY_INSERT_APP_CATEGORY = "foreign_key_insert_app_category";
	public static final String FOREIGN_KEY_UPDATE_APP_CATEGORY_CATEGORY_HASHID_WEAK = "foreign_key_update_app_category_category_hashid_weak";
	public static final String FOREIGN_KEY_UPDATE_APP_CATEGORY_APP_FULL_HASHID_WEAK = "foreign_key_update_app_category_app_full_hashid_weak";

	
	
	//TODO table install later pk = fk app_full_hash_id + triggers
	
	
	
	public static final String CREATE_TABLE_APP_INSTALLED = "CREATE TABLE IF NOT EXISTS " + TABLE_APP_INSTALLED + " ("
			+ KEY_APP_INSTALLED_HASHID + " INTEGER NOT NULL, "
			+ KEY_APP_INSTALLED_PACKAGE_NAME + " TEXT NOT NULL, "
			+ KEY_APP_INSTALLED_VERSION_CODE + " INTEGER NOT NULL CHECK ("+KEY_APP_VERSION_CODE+">=0), "
			+ KEY_APP_INSTALLED_VERSION_NAME + " TEXT NOT NULL, "
			+ KEY_APP_INSTALLED_NAME + " TEXT NOT NULL, "
			+ "PRIMARY KEY("+ KEY_APP_INSTALLED_HASHID +") );";	

	
	
	//TODO table never update pk = fk hashid from installed + triggers
	
	
	
	public static final String CREATE_TABLE_ICON = "CREATE TABLE IF NOT EXISTS " + TABLE_ICON + " ("
			+ KEY_ICON_APP_FULL_HASHID + " INTEGER NOT NULL, "
			+ KEY_ICON_REMOTE_PATH_TAIL + " TEXT NOT NULL, "
			+ "FOREIGN KEY("+ KEY_ICON_APP_FULL_HASHID +") REFERENCES "+ TABLE_APPLICATION +"("+ KEY_APP_FULL_HASHID +"),"
			+ "PRIMARY KEY("+ KEY_ICON_APP_FULL_HASHID +"));";

	public static final String FOREIGN_KEY_INSERT_ICON = "foreign_key_insert_icon";
	public static final String FOREIGN_KEY_UPDATE_ICON_APP_FULL_HASHID_WEAK = "foreign_key_update_icon_app_full_hashid_weak";
	
	
	public static final String CREATE_TABLE_DOWNLOAD = "CREATE TABLE IF NOT EXISTS " + TABLE_DOWNLOAD + " ("
			+ KEY_DOWNLOAD_APP_FULL_HASHID + " INTEGER NOT NULL, "
			+ KEY_DOWNLOAD_REMOTE_PATH_TAIL + " TEXT NOT NULL, "
			+ KEY_DOWNLOAD_MD5HASH + " TEXT NOT NULL, "
			+ KEY_DOWNLOAD_SIZE + " INTEGER NOT NULL CHECK ("+KEY_DOWNLOAD_SIZE+">0), "
			+ "FOREIGN KEY("+ KEY_DOWNLOAD_APP_FULL_HASHID +") REFERENCES "+ TABLE_APPLICATION +"("+ KEY_APP_FULL_HASHID +"),"
			+ "PRIMARY KEY("+ KEY_DOWNLOAD_APP_FULL_HASHID +") );";
	
	public static final String FOREIGN_KEY_INSERT_DOWNLOAD = "foreign_key_insert_download";
	public static final String FOREIGN_KEY_UPDATE_DOWNLOAD_APP_FULL_HASHID_WEAK = "foreign_key_update_download_app_full_hashid_weak";

	
	
	public static final String CREATE_TABLE_EXTRA = "CREATE TABLE IF NOT EXISTS " + TABLE_EXTRA + " ("
			+ KEY_EXTRA_APP_FULL_HASHID + " INTEGER NOT NULL, "
			+ KEY_EXTRA_DESCRIPTION + " TEXT NOT NULL, "
			+ KEY_EXTRA_DATE + " DATE NOT NULL, "
			+ KEY_EXTRA_RATING + " INTEGER NOT NULL, "
			+ KEY_EXTRA_POPULARITY + " INTEGER NOT NULL CHECK ("+KEY_EXTRA_POPULARITY+">=0), "
			+ "FOREIGN KEY("+ KEY_EXTRA_APP_FULL_HASHID +") REFERENCES "+ TABLE_APPLICATION +"("+ KEY_APP_FULL_HASHID +"),"
			+ "PRIMARY KEY("+ KEY_EXTRA_APP_FULL_HASHID +") );";	//TODO integrity restrictions on date and rating

	public static final String FOREIGN_KEY_INSERT_EXTRA = "foreign_key_insert_extra";
	public static final String FOREIGN_KEY_UPDATE_EXTRA_APP_FULL_HASHID_WEAK = "foreign_key_update_extra_app_full_hashid_weak";

	
	
	/**
	 * Triggers,	Stupid sqlite only constraints foreign keys after 3.6.19 which means android 2.2
	 * 				only hope of implementing those constraints is by using triggers, as explained in this sqlite wiki webpage:
	 * 				http://www.sqlite.org/cvstrac/wiki?p=ForeignKeyTriggers	 * 
	 */
	
	public static final String CREATE_TRIGGER_REPO_UPDATE_REPO_HASHID_STRONG = "CREATE TRIGGER IF NOT EXISTS "+ FOREIGN_KEY_UPDATE_REPO_REPO_HASHID_STRONG
			+ " AFTER UPDATE OF "+ KEY_REPO_HASHID  +" ON " + TABLE_REPOSITORY
			+ " FOR EACH ROW BEGIN"
			+ "     UPDATE "+ TABLE_LOGIN +" SET "+ KEY_LOGIN_REPO_HASHID +" = NEW."+ KEY_REPO_HASHID +" WHERE "+ KEY_LOGIN_REPO_HASHID +" = OLD."+ KEY_REPO_HASHID +");"
			+ "     UPDATE "+ TABLE_APPLICATION +" SET "+ KEY_APP_REPO_HASHID +" = NEW."+ KEY_REPO_HASHID +" WHERE "+ KEY_APP_REPO_HASHID +" = OLD."+ KEY_REPO_HASHID +");"
			+ " END;";
	
	public static final String CREATE_TRIGGER_REPO_DELETE_REPO = "CREATE TRIGGER IF NOT EXISTS "+ FOREIGN_KEY_DELETE_REPO
			+ " BEFORE DELETE ON" + TABLE_REPOSITORY
			+ " FOR EACH ROW BEGIN"
			+ "     DELETE FROM "+ TABLE_LOGIN +" WHERE "+ KEY_LOGIN_REPO_HASHID +" = OLD."+ KEY_REPO_HASHID +");"
			+ "     DELETE FROM "+ TABLE_APPLICATION +" WHERE "+ KEY_APP_REPO_HASHID +" = OLD."+ KEY_REPO_HASHID +");"
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
			+ "     WHERE NEW."+ KEY_APP_REPO_HASHID +" IS NOT NULL"
			+ "	        AND (SELECT "+ KEY_REPO_HASHID +" FROM "+ TABLE_REPOSITORY +" WHERE "+ KEY_REPO_HASHID +" = NEW."+ KEY_APP_REPO_HASHID +") IS NULL;"
			+ " END;";
	
	public static final String CREATE_TRIGGER_APPLICATION_UPDATE_REPO_HASHID_WEAK = "CREATE TRIGGER IF NOT EXISTS "+ FOREIGN_KEY_UPDATE_APPLICATION_REPO_HASHID_WEAK
			+ " BEFORE UPDATE OF "+ KEY_APP_REPO_HASHID  +" ON " + TABLE_APPLICATION
			+ " FOR EACH ROW BEGIN"
			+ "    SELECT RAISE(ROLLBACK, 'update on table "+ TABLE_APPLICATION +" violates foreign key constraint "+ FOREIGN_KEY_UPDATE_APPLICATION_REPO_HASHID_WEAK +"')"
			+ "    WHERE (SELECT "+ KEY_REPO_HASHID +" FROM "+ TABLE_REPOSITORY +" WHERE "+ KEY_REPO_HASHID +" = NEW."+ KEY_APP_REPO_HASHID +") IS NULL;"
			+ " END;";

	
	public static final String CREATE_TRIGGER_APPLICATION_UPDATE_APP_FULL_HASHID_STRONG = "CREATE TRIGGER IF NOT EXISTS "+ FOREIGN_KEY_UPDATE_APPLICATION_APP_FULL_HASHID_STRONG
			+ " AFTER UPDATE OF "+ KEY_APP_FULL_HASHID  +" ON " + TABLE_APPLICATION
			+ " FOR EACH ROW BEGIN"
			+ "     UPDATE "+ TABLE_APP_CATEGORY +" SET "+ KEY_APP_CATEGORY_APP_FULL_HASHID +" = NEW."+ KEY_APP_FULL_HASHID +" WHERE "+ KEY_APP_CATEGORY_APP_FULL_HASHID +" = OLD."+ KEY_APP_FULL_HASHID +");"
			+ "     UPDATE "+ TABLE_ICON +" SET "+ KEY_ICON_APP_FULL_HASHID +" = NEW."+ KEY_APP_FULL_HASHID +" WHERE "+ KEY_ICON_APP_FULL_HASHID +" = OLD."+ KEY_APP_FULL_HASHID +");"
			+ "     UPDATE "+ TABLE_DOWNLOAD +" SET "+ KEY_DOWNLOAD_APP_FULL_HASHID +" = NEW."+ KEY_APP_FULL_HASHID +" WHERE "+ KEY_DOWNLOAD_APP_FULL_HASHID +" = OLD."+ KEY_APP_FULL_HASHID +");"
			+ "     UPDATE "+ TABLE_EXTRA +" SET "+ KEY_EXTRA_APP_FULL_HASHID +" = NEW."+ KEY_APP_FULL_HASHID +" WHERE "+ KEY_EXTRA_APP_FULL_HASHID +" = OLD."+ KEY_APP_FULL_HASHID +");"
			+ " END;";
	
	public static final String CREATE_TRIGGER_APPLICATION_DELETE = "CREATE TRIGGER IF NOT EXISTS "+ FOREIGN_KEY_DELETE_APPLICATION
			+ " BEFORE DELETE ON" + TABLE_APPLICATION
			+ " FOR EACH ROW BEGIN"
			+ "     DELETE FROM "+ TABLE_APP_CATEGORY +" WHERE "+ KEY_APP_CATEGORY_APP_FULL_HASHID +" = OLD."+ KEY_APP_FULL_HASHID +");"
			+ "     DELETE FROM "+ TABLE_ICON +" WHERE "+ KEY_ICON_APP_FULL_HASHID +" = OLD."+ KEY_APP_FULL_HASHID +");"
			+ "     DELETE FROM "+ TABLE_DOWNLOAD +" WHERE "+ KEY_DOWNLOAD_APP_FULL_HASHID +" = OLD."+ KEY_APP_FULL_HASHID +");"
			+ "     DELETE FROM "+ TABLE_EXTRA +" WHERE "+ KEY_EXTRA_APP_FULL_HASHID +" = OLD."+ KEY_APP_FULL_HASHID +");"
			+ " END;";
	
	

	
	public static final String CREATE_TRIGGER_CATEGORY_UPDATE_CATEGORY_HASHID_STRONG = "CREATE TRIGGER IF NOT EXISTS "+ FOREIGN_KEY_UPDATE_CATEGORY_CATEGORY_HASHID_STRONG
			+ " AFTER UPDATE OF "+ KEY_CATEGORY_HASHID  +" ON " + TABLE_CATEGORY
			+ " FOR EACH ROW BEGIN"
			+ "     UPDATE "+ TABLE_SUB_CATEGORY +" SET "+ KEY_SUB_CATEGORY_PARENT +" = NEW."+ KEY_CATEGORY_HASHID +" WHERE "+ KEY_SUB_CATEGORY_PARENT +" = OLD."+ KEY_CATEGORY_HASHID +");"
			+ "     UPDATE "+ TABLE_SUB_CATEGORY +" SET "+ KEY_SUB_CATEGORY_PARENT +" = NEW."+ KEY_CATEGORY_HASHID +" WHERE "+ KEY_SUB_CATEGORY_CHILD +" = OLD."+ KEY_CATEGORY_HASHID +");"
			+ "     UPDATE "+ TABLE_APP_CATEGORY +" SET "+ KEY_APP_CATEGORY_CATEGORY_HASHID +" = NEW."+ KEY_CATEGORY_HASHID+" WHERE "+ KEY_APP_CATEGORY_CATEGORY_HASHID +" = OLD."+ KEY_CATEGORY_HASHID +");"
			+ " END;";
	
	public static final String CREATE_TRIGGER_CATEGORY_DELETE = "CREATE TRIGGER IF NOT EXISTS "+ FOREIGN_KEY_DELETE_CATEGORY
			+ " BEFORE DELETE ON" + TABLE_CATEGORY
			+ " FOR EACH ROW BEGIN"
			+ "     DELETE FROM "+ TABLE_SUB_CATEGORY +" WHERE "+ KEY_SUB_CATEGORY_PARENT +" = OLD."+ KEY_CATEGORY_HASHID +");"
			+ "     DELETE FROM "+ TABLE_SUB_CATEGORY +" WHERE "+ KEY_SUB_CATEGORY_CHILD +" = OLD."+ KEY_CATEGORY_HASHID +");"
			+ "     DELETE FROM "+ TABLE_APP_CATEGORY +" WHERE "+ KEY_APP_CATEGORY_CATEGORY_HASHID +" = OLD."+ KEY_CATEGORY_HASHID +");"
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
			+ "	        	AND (SELECT "+ KEY_APP_FULL_HASHID +" FROM "+ TABLE_APPLICATION +" WHERE "+ KEY_APP_FULL_HASHID +" = NEW."+ KEY_APP_CATEGORY_APP_FULL_HASHID +") IS NULL)"
			+ "			OR (NEW."+ KEY_APP_CATEGORY_CATEGORY_HASHID +" IS NOT NULL"
			+ "	        	AND (SELECT "+ KEY_CATEGORY_HASHID +" FROM "+ TABLE_CATEGORY +" WHERE "+ KEY_CATEGORY_HASHID +" = NEW."+ KEY_APP_CATEGORY_CATEGORY_HASHID +") IS NULL);"
			+ " END;";
	
	public static final String CREATE_TRIGGER_APP_CATEGORY_UPDATE_APP_FULL_HASHID_WEAK = "CREATE TRIGGER IF NOT EXISTS "+ FOREIGN_KEY_UPDATE_APP_CATEGORY_APP_FULL_HASHID_WEAK
			+ " BEFORE UPDATE OF "+ KEY_APP_CATEGORY_APP_FULL_HASHID +" ON " + TABLE_APP_CATEGORY
			+ " FOR EACH ROW BEGIN"
			+ "    SELECT RAISE(ROLLBACK, 'update on table "+ TABLE_APP_CATEGORY +" violates foreign key constraint "+ FOREIGN_KEY_UPDATE_APP_CATEGORY_APP_FULL_HASHID_WEAK +"')"
			+ "    WHERE (SELECT "+ KEY_APP_FULL_HASHID +" FROM "+ TABLE_APPLICATION +" WHERE "+ KEY_APP_FULL_HASHID +" = NEW."+ KEY_APP_CATEGORY_APP_FULL_HASHID +") IS NULL;"
			+ " END;";
	
	public static final String CREATE_TRIGGER_APP_CATEGORY_UPDATE_CATEGORY_HASHID_WEAK = "CREATE TRIGGER IF NOT EXISTS "+ FOREIGN_KEY_UPDATE_APP_CATEGORY_CATEGORY_HASHID_WEAK
			+ " BEFORE UPDATE OF "+ KEY_APP_CATEGORY_CATEGORY_HASHID  +" ON " + TABLE_APP_CATEGORY
			+ " FOR EACH ROW BEGIN"
			+ "    SELECT RAISE(ROLLBACK, 'update on table "+ TABLE_APP_CATEGORY +" violates foreign key constraint "+ FOREIGN_KEY_UPDATE_APP_CATEGORY_CATEGORY_HASHID_WEAK +"')"
			+ "    WHERE (SELECT "+ KEY_CATEGORY_HASHID +" FROM "+ TABLE_CATEGORY +" WHERE "+ KEY_CATEGORY_HASHID +" = NEW."+ KEY_APP_CATEGORY_CATEGORY_HASHID +") IS NULL;"
			+ " END;";
	
	
	
	
	public static final String CREATE_TRIGGER_ICON_INSERT = "CREATE TRIGGER IF NOT EXISTS "+ FOREIGN_KEY_INSERT_ICON
			+ " BEFORE INSERT ON " + TABLE_ICON
			+ " FOR EACH ROW BEGIN"
			+ "     SELECT RAISE(ROLLBACK, 'insert on table "+ TABLE_ICON +" violates foreign key constraint "+ FOREIGN_KEY_INSERT_ICON +"')"
			+ "     WHERE NEW."+ KEY_ICON_APP_FULL_HASHID +" IS NOT NULL"
			+ "	        AND (SELECT "+ KEY_APP_FULL_HASHID +" FROM "+ TABLE_APPLICATION +" WHERE "+ KEY_APP_FULL_HASHID +" = NEW."+ KEY_ICON_APP_FULL_HASHID +") IS NULL;"
			+ " END;";
	
	public static final String CREATE_TRIGGER_ICON_UPDATE_APP_FULL_HASHID_WEAK = "CREATE TRIGGER IF NOT EXISTS "+ FOREIGN_KEY_UPDATE_ICON_APP_FULL_HASHID_WEAK
			+ " BEFORE UPDATE OF "+ KEY_ICON_APP_FULL_HASHID  +" ON " + TABLE_ICON
			+ " FOR EACH ROW BEGIN"
			+ "    SELECT RAISE(ROLLBACK, 'update on table "+ TABLE_ICON +" violates foreign key constraint "+ FOREIGN_KEY_UPDATE_ICON_APP_FULL_HASHID_WEAK +"')"
			+ "    WHERE (SELECT "+ KEY_APP_FULL_HASHID +" FROM "+ TABLE_APPLICATION +" WHERE "+ KEY_APP_FULL_HASHID +" = NEW."+ KEY_ICON_APP_FULL_HASHID +") IS NULL;"
			+ " END;";
	
	
	
	
	public static final String CREATE_TRIGGER_DOWNLOAD_INSERT = "CREATE TRIGGER IF NOT EXISTS "+ FOREIGN_KEY_INSERT_DOWNLOAD
			+ " BEFORE INSERT ON " + TABLE_DOWNLOAD
			+ " FOR EACH ROW BEGIN"
			+ "     SELECT RAISE(ROLLBACK, 'insert on table "+ TABLE_DOWNLOAD +" violates foreign key constraint "+ FOREIGN_KEY_INSERT_DOWNLOAD +"')"
			+ "     WHERE NEW."+ KEY_DOWNLOAD_APP_FULL_HASHID +" IS NOT NULL"
			+ "	        AND (SELECT "+ KEY_APP_FULL_HASHID +" FROM "+ TABLE_APPLICATION +" WHERE "+ KEY_APP_FULL_HASHID +" = NEW."+ KEY_DOWNLOAD_APP_FULL_HASHID +") IS NULL;"
			+ " END;";
	
	public static final String CREATE_TRIGGER_DOWNLOAD_UPDATE_APP_FULL_HASHID_WEAK = "CREATE TRIGGER IF NOT EXISTS "+ FOREIGN_KEY_UPDATE_DOWNLOAD_APP_FULL_HASHID_WEAK
			+ " BEFORE UPDATE OF "+ KEY_DOWNLOAD_APP_FULL_HASHID  +" ON " + TABLE_DOWNLOAD
			+ " FOR EACH ROW BEGIN"
			+ "    SELECT RAISE(ROLLBACK, 'update on table "+ TABLE_DOWNLOAD +" violates foreign key constraint "+ FOREIGN_KEY_UPDATE_DOWNLOAD_APP_FULL_HASHID_WEAK +"')"
			+ "    WHERE (SELECT "+ KEY_APP_FULL_HASHID +" FROM "+ TABLE_APPLICATION +" WHERE "+ KEY_APP_FULL_HASHID +" = NEW."+ KEY_DOWNLOAD_APP_FULL_HASHID +") IS NULL;"
			+ " END;";
	
	
	
	
	public static final String CREATE_TRIGGER_EXTRA_INSERT = "CREATE TRIGGER IF NOT EXISTS "+ FOREIGN_KEY_INSERT_EXTRA
			+ " BEFORE INSERT ON " + TABLE_EXTRA
			+ " FOR EACH ROW BEGIN"
			+ "     SELECT RAISE(ROLLBACK, 'insert on table "+ TABLE_EXTRA +" violates foreign key constraint "+ FOREIGN_KEY_INSERT_EXTRA +"')"
			+ "     WHERE NEW."+ KEY_EXTRA_APP_FULL_HASHID +" IS NOT NULL"
			+ "	        AND (SELECT "+ KEY_APP_FULL_HASHID +" FROM "+ TABLE_APPLICATION +" WHERE "+ KEY_APP_FULL_HASHID +" = NEW."+ KEY_EXTRA_APP_FULL_HASHID +") IS NULL;"
			+ " END;";
	
	public static final String CREATE_TRIGGER_EXTRA_UPDATE_APP_FULL_HASHID_WEAK = "CREATE TRIGGER IF NOT EXISTS "+ FOREIGN_KEY_UPDATE_EXTRA_APP_FULL_HASHID_WEAK
			+ " BEFORE UPDATE OF "+ KEY_EXTRA_APP_FULL_HASHID  +" ON " + TABLE_EXTRA
			+ " FOR EACH ROW BEGIN"
			+ "    SELECT RAISE(ROLLBACK, 'update on table "+ TABLE_EXTRA +" violates foreign key constraint "+ FOREIGN_KEY_UPDATE_EXTRA_APP_FULL_HASHID_WEAK +"')"
			+ "    WHERE (SELECT "+ KEY_APP_FULL_HASHID +" FROM "+ TABLE_APPLICATION +" WHERE "+ KEY_APP_FULL_HASHID +" = NEW."+ KEY_EXTRA_APP_FULL_HASHID+") IS NULL;"
			+ " END;";
	
	
	/**
	 *  TODO Views and Indexes
	 * 
	 */
	
}
