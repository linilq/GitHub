package zty.sdk.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ZtyDBHelper extends SQLiteOpenHelper {

	public static final int DB_VERSION = 1;
	public static final String DB_NAME = "zty_account.db";
	public static final String DB_TABLE_NAME = "account_infor";
	public static final String DB_CREATE_SQL = "create table "+DB_TABLE_NAME+" ("
			+ "id integer primary key autoincrement,"
			+ "indexnum Integer,usn text,psd text,"
			+ "bstatus text,nstatus text,pnum text,preferpayway text)";
	public static final String DB_DROP_SQL = "drop table if exists account_infor";
	private static ZtyDBHelper instance;
	
	public static ZtyDBHelper getInstance(Context context){
		if (instance==null) {
			instance = new ZtyDBHelper(context);
		}
		return instance;
	}
	
	
	private ZtyDBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DB_CREATE_SQL);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(DB_DROP_SQL);
		db.execSQL(DB_CREATE_SQL);

	}

}
