package cm.aptoide.pt2;



import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbOpenHelper extends SQLiteOpenHelper {

	
	
	
	
	public DbOpenHelper(Context context) {
		super(context, "aptoide.db", null, 1);
		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		
		db.execSQL("create table apk (_id integer primary key, apkid text, name text, vername text, vercode integer, imagepath text, downloads integer, size integer, category2 integer, repo_id integer);");
		db.execSQL("create table category1 (_id integer primary key, name text , size integer, unique (name) on conflict ignore) ;");
		db.execSQL("create table category2 (_id integer primary key, catg1_id integer, name text , size integer, unique (name) on conflict ignore);");
		db.execSQL("create table repo (_id integer primary key, url text, delta text, appcount integer, iconspath text, basepath text, status text, username text, password text);");
		db.execSQL("create table toprepo_extra (_id integer primary key, top_delta text, iconspath text);");
		db.execSQL("create table repo_category1 (repo_id integer, catg1_id integer, primary key(repo_id, catg1_id) on conflict ignore);");
		db.execSQL("create table repo_category2 (repo_id integer, catg2_id integer, primary key(repo_id, catg2_id) on conflict ignore);");
		db.execSQL("create table installed (apkid text, vercode integer, vername text, name text);");
		db.execSQL("create table dynamic_apk (_id integer primary key, apkid text, name text, vername text, vercode integer, imagepath text, downloads integer, size integer, category1 integer, repo_id integer);");
		db.execSQL("CREATE INDEX mytest_id2_idx ON installed(apkid);");
		db.execSQL("CREATE INDEX mytest_id_idx ON apk(apkid,vercode,category2,repo_id);");
		db.execSQL("CREATE INDEX mytest_id3_idx ON dynamic_apk(apkid,vercode,category1,repo_id);");
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		onCreate(db);
	}

}
