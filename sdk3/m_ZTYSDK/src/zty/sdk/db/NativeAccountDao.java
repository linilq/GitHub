package zty.sdk.db;

import java.util.ArrayList;

import zty.sdk.model.NativeAccountInfor;
import zty.sdk.utils.Util_G;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class NativeAccountDao {

	private ZtyDBHelper dbHelper;

	public NativeAccountDao(Context context) {
		dbHelper = ZtyDBHelper.getInstance(context);
	}

	/**
	 * 该方法只适合在数据库创建后，新账号(未知indexnum)加入
	 * <p>旧账号加入数据库(第一次创建数据库)时使用{@link #insert(NativeAccountInfor)}}</p>
	 * @param account
	 */
	public void inserAndUpdateAll(NativeAccountInfor account) {
		insertNew(account);
		onAddUpdateIndexnum(account.getUsn());
	}

	public void delete(String usn) {
		onDeleteUpdateIndexnum(usn);
		
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		long ret = -1;
		do {
			ret = db.delete(ZtyDBHelper.DB_TABLE_NAME, " usn=? ",
					new String[] { usn });
		} while (ret < 0);
	}

	/**
	 * 获取账号绑定状态  false：没有绑定
	 * @param username
	 * @return
	 */
	public String getBStatus(String username){
		String ret = "false";
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery("select * from " + ZtyDBHelper.DB_TABLE_NAME
				+ " where usn=? ", new String[] { username });
		if (cursor.moveToNext()) {
			ret = cursor.getString(cursor.getColumnIndex("bstatus"));
		}
		return ret;
	}
	/**
	 * 获取账号绑定通知状态 false：没有通知过
	 * @param username
	 * @return
	 */
	public String getNStatus(String username){
		String ret = "false";
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery("select * from " + ZtyDBHelper.DB_TABLE_NAME
				+ " where usn=? ", new String[] { username });
		if (cursor.moveToNext()) {
			ret = cursor.getString(cursor.getColumnIndex("nstatus"));
		}
		return ret;
	}
	/**
	 * 返回所有账号列表
	 * @return
	 */
	public ArrayList<NativeAccountInfor> getAll(){
		ArrayList<NativeAccountInfor> list = new ArrayList<NativeAccountInfor>();
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cursor = db
				.rawQuery("select * from " + ZtyDBHelper.DB_TABLE_NAME
						+ " where 1=1 order by indexnum", new String[] {});
		while (cursor.moveToNext()) {
			NativeAccountInfor account = new NativeAccountInfor();
			account.setIndexnum(cursor.getInt(cursor.getColumnIndex("indexnum")));
			account.setUsn(cursor.getString(cursor.getColumnIndex("usn")));
			account.setPsd(cursor.getString(cursor.getColumnIndex("psd")));
			account.setBstatus(cursor.getString(cursor.getColumnIndex("bstatus")));
			account.setNstatus(cursor.getString(cursor.getColumnIndex("nstatus")));
			account.setPnum(cursor.getString(cursor.getColumnIndex("pnum")));
			account.setPreferpayway(cursor.getString(cursor.getColumnIndex("preferpayway")));
			list.add(account);
			Util_G.debug_i("LINILQTEST", account.toString());
		}
		
		return list;
	}
	
	/**
	 * 修改账户绑定状态以及手机号
	 * 
	 * @param username
	 * @param bStatus
	 */
	public void updateBStatus(String username, String bStatus,String pnum) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("bstatus", bStatus);
		values.put("pnum", pnum);
		long ret = -1;
		do {
			ret = db.update(ZtyDBHelper.DB_TABLE_NAME, values, "usn=?",
					new String[] { username });
		} while (ret < 0);
	}
	
	/**
	 * 修改账户偏爱支付方式
	 * 
	 * @param username
	 * @param bStatus
	 */
	public void updatePayway(String username, String preferPayway) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("preferpayway", preferPayway);
		long ret = -1;
		do {
			ret = db.update(ZtyDBHelper.DB_TABLE_NAME, values, "usn=?",
					new String[] { username });
		} while (ret < 0);
	}
	
	/**
	 * 修改账户绑定通知状态
	 * 
	 * @param username
	 * @param nStatus
	 */
	public void updateNStatus(String username, String nStatus) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("nstatus", nStatus);
		long ret = -1;
		do {
			ret = db.update(ZtyDBHelper.DB_TABLE_NAME, values, "usn=?",
					new String[] { username });
		} while (ret < 0);
	}
	/**
	 * 修改账户密码
	 * @param username
	 * @param psd
	 */
	public void updatePsd(String username, String psd) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("psd", psd);
		long ret = -1;
		do {
			ret = db.update(ZtyDBHelper.DB_TABLE_NAME, values, "usn=?",
					new String[] { username });
		} while (ret < 0);
	}
	
	/**
	 * 删除usn前，更新所有账号indexnum
	 * @param usn
	 */
	public void onDeleteUpdateIndexnum(String usn){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int oldIndex = update(db, usn,-1);
		if (oldIndex != -1) {
			Cursor cursor = db
					.rawQuery("select * from " + ZtyDBHelper.DB_TABLE_NAME
							+ " where 1=1 ", new String[] {});
			while (cursor.moveToNext()) {
				String tempUsn = cursor.getString(cursor.getColumnIndex("usn"));
				int tempIndexnum = cursor.getInt(cursor
						.getColumnIndex("indexnum"));
				ContentValues values = new ContentValues();
				// 处理indexnum > oldIndex的账号，自减一
				if (!tempUsn.equals(usn) && tempIndexnum > oldIndex) {
					values.put("indexnum", tempIndexnum - 1);
					db.update(ZtyDBHelper.DB_TABLE_NAME, values,
								"usn=?", new String[] { tempUsn });
				}
				
			}
			cursor.close();
			db.close();
		}
	}
	
	
	
	/**
	 * 更新所有账号indexnum，自增1,
	 * 
	 * @param db
	 */
	public void onAddUpdateIndexnum(String usnNow) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int oldIndex = update(db, usnNow,1);
		if (oldIndex != -1) {
			Cursor cursor = db
					.rawQuery("select * from " + ZtyDBHelper.DB_TABLE_NAME
							+ " where 1=1 ", new String[] {});
			while (cursor.moveToNext()) {
				String tempUsn = cursor.getString(cursor.getColumnIndex("usn"));
				int tempIndexnum = cursor.getInt(cursor
						.getColumnIndex("indexnum"));
				ContentValues values = new ContentValues();
				if (oldIndex != 0) {// 不是新数据插入时，处理index比它小的
					if (!tempUsn.equals(usnNow) && tempIndexnum < oldIndex) {
						values.put("indexnum", tempIndexnum + 1);
						db.update(ZtyDBHelper.DB_TABLE_NAME, values,
								"usn=?", new String[] { tempUsn });
					}
				} else {// 是新数据插入时，处理其他所有的
					if (!tempUsn.equals(usnNow)) {
						values.put("indexnum", tempIndexnum + 1);
						db.update(ZtyDBHelper.DB_TABLE_NAME, values,
								"usn=?", new String[] { tempUsn });
					}
				}
			}
			cursor.close();
			db.close();
		}

	}

	/**
	 * 更新选中账号的indexnum，并返回其原值,失败返回-1
	 * 
	 * @param db
	 * @param username
	 * @param indexnum
	 * @return oldIndex;
	 */
	private int update(SQLiteDatabase db, String username,int indexnum) {
		int oldIndex = -1;
		Cursor cursor = db.rawQuery("select * from " + ZtyDBHelper.DB_TABLE_NAME
				+ " where usn=? ", new String[] { username });
		if (cursor.moveToNext()) {
			oldIndex = cursor.getInt(cursor.getColumnIndex("indexnum"));
			ContentValues values = new ContentValues();
			values.put("indexnum", indexnum);
			long ret = -1;
			do {
				ret = db.update(ZtyDBHelper.DB_TABLE_NAME, values, " usn=? ",
						new String[] { username });
			} while (ret == -1);
		}
		cursor.close();
		return oldIndex;
	}

	/**
	 * 插入新用户记录，此时未知indexnum
	 * 
	 * @param account
	 */
	private void insertNew(NativeAccountInfor account) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("indexnum", 0);
		values.put("usn", account.getUsn());
		values.put("psd", account.getPsd());
		values.put("bstatus", account.getBstatus());
		values.put("nstatus", account.getNstatus());
		values.put("pnum", account.getPnum());
		long ret = -1;
		do {
			ret = db.insert(ZtyDBHelper.DB_TABLE_NAME, null, values);
		} while (ret == -1);
		db.close();
	}
	
	/**
	 * 插入已知indexnum的新用户记录时使用
	 * 
	 * @param account
	 */
	public void insert(NativeAccountInfor account) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("indexnum", account.getIndexnum());
		values.put("usn", account.getUsn());
		values.put("psd", account.getPsd());
		values.put("bstatus", account.getBstatus());
		values.put("nstatus", account.getNstatus());
		values.put("pnum", account.getPnum());
		long ret = -1;
		do {
			ret = db.insert(ZtyDBHelper.DB_TABLE_NAME, null, values);
		} while (ret == -1);
		db.close();
	}

}
