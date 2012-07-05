package cm.aptoide.pt;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ExtrasDBStructure extends SQLiteOpenHelper {

	
	public static final String TABLE_COMMENTS = "comments";
	public static final String COLUMN_COMMENTS_APKID = "apkid";
	public static final String COLUMN_COMMENTS_COMMENT = "comment";

	private static final String DATABASE_NAME = "extras.db";
	private static final int DATABASE_VERSION = 22;

	// Database creation sql statement
	private static final String CREATE_TABLE_COMMENTS = "create table "
			+ TABLE_COMMENTS + "(" 
			+ COLUMN_COMMENTS_APKID + " text , "
			+ COLUMN_COMMENTS_COMMENT + " text , UNIQUE ("+COLUMN_COMMENTS_APKID+") ON CONFLICT REPLACE); ";

	public ExtrasDBStructure(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(CREATE_TABLE_COMMENTS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(DBStructure.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMMENTS);
		
		onCreate(db);
	}

}
