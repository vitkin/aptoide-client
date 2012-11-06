package cm.aptoide.pt2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

public class ExtrasDbOpenHelper extends SQLiteOpenHelper {
	
	
	
	public static final String TABLE_COMMENTS = "comments";
	public static final String COLUMN_COMMENTS_APKID = "apkid";
	public static final String COLUMN_COMMENTS_COMMENT = "comment";
	
	private static final String CREATE_TABLE_COMMENTS = "create table "
			+ TABLE_COMMENTS + "(" 
			+ COLUMN_COMMENTS_APKID + " text , "
			+ COLUMN_COMMENTS_COMMENT + " text , UNIQUE ("+COLUMN_COMMENTS_APKID+") ON CONFLICT REPLACE); ";

	public ExtrasDbOpenHelper(Context context) {
		super(context, "extras.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_COMMENTS);
		db.execSQL("create index idx on comments(apkid)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

}
