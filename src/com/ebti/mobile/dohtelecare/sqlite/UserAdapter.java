package com.ebti.mobile.dohtelecare.sqlite;

import com.ebti.mobile.dohtelecare.model.User;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class UserAdapter extends DbAdapter {
	private final static String TAG="UserAdapter";
	// UID : 登入帳號
	public final static String COL_UID = "UID";
	// NAME : 用戶姓名
	public final static String COL_NAME = "NAME";
	// PASSWORD: 密碼
	public final static String COL_PASSWORD = "PASSWORD";
	// NICK_NAME : 暱稱
	public final static String COL_NICK_NAME = "NICK_NAME";
	// GENDER : 性別
	public final static String COL_GENDER = "GENDER";
	// BIRTHDAY: 生日
	public final static String COL_BIRTHDAY = "BIRTHDAY";
	// MOBILE :行動電話
	public final static String COL_MOBILE = "MOBILE";
	// AC_HIGH: 飯前血糖
	public final static String COL_ACHIGH = "AC_HIGH";
	// AC_LOW: 飯後血糖
	public final static String COL_ACLOW = "AC_LOW";
	// BHP: 收縮壓
	public final static String COL_BHP = "BHP";
	// BLP: 舒張壓
	public final static String COL_BLP = "BLP";
	// HEIGHT:身高
	public final static String COL_HEIGHT = "HEIGHT";
	// WEIGHT:體重
	public final static String COL_WEIGHT = "WEIGHT";
	// REMEMBER_USER:記憶帳號密碼
	public final static String COL_REMEMBER_USER = "REMEMBER_USER";
	// TYPE:帳號型態
	public final static String COL_TYPE = "TYPE";
	// UNIT:單位名稱
	public final static String COL_UNIT = "UNIT";
	
	public final static String TABLE_USER = "HM_USER";

	public UserAdapter(Context ctx) {
		super(ctx);
	}

	public synchronized int deleteUser(String uid) {
		SQLiteDatabase db= openDatabase();
		int status=db.delete(TABLE_USER, // 資料表名稱
				"UID=" + uid, // WHERE
				null // WHERE的參數
				);
		db.close();
		return status;
	}

	public synchronized long createtUser(User u) {
		Log.d(TAG,"Create User=" + u.getUid());
		SQLiteDatabase db= openDatabase();
		ContentValues args = new ContentValues();
		args.put(COL_UID, u.getUid());
		args.put(COL_NAME, u.getName());
		args.put(COL_PASSWORD, u.getPassword());
		args.put(COL_NICK_NAME, u.getNickname());
		args.put(COL_GENDER, u.getGender());
		args.put(COL_BIRTHDAY, u.getBirthday());
		args.put(COL_MOBILE, u.getMobile());
		args.put(COL_ACHIGH, u.getAcHigh());
		args.put(COL_ACLOW, u.getAcLow());
		args.put(COL_BHP, u.getBhp());
		args.put(COL_BLP, u.getBlp());
		args.put(COL_HEIGHT, u.getHeight());
		args.put(COL_WEIGHT, u.getWeight());
		args.put(COL_REMEMBER_USER, u.getRememberUser());
		args.put(COL_TYPE, u.getType());
		args.put(COL_UNIT, u.getUnit());
		long i= db.insert(TABLE_USER, null, args);
		db.close();
		Log.i("info", "insert db result i: " + i );
		return i;
	}
	
	//update
	public synchronized long updateUserWeightAndHieght(User u) {
		Log.d(TAG,"update User=" + u.getUid());
		SQLiteDatabase db= openDatabase();
		ContentValues args = new ContentValues();
		args.put(COL_UID, u.getUid());
		//args.put(COL_NAME, u.getName());
		//args.put(COL_PASSWORD, u.getPassword());
		//args.put(COL_NICK_NAME, u.getNickname());
		//args.put(COL_GENDER, u.getGender());
		//args.put(COL_BIRTHDAY, u.getBirthday());
		//args.put(COL_MOBILE, u.getMobile());
		//args.put(COL_ACHIGH, u.getAcHigh());
		//args.put(COL_ACLOW, u.getAcLow());
		//args.put(COL_BHP, u.getBhp());
		//args.put(COL_BLP, u.getBlp());
		args.put(COL_HEIGHT, u.getHeight());
		args.put(COL_WEIGHT, u.getWeight());
		long i= db.update(TABLE_USER, args, COL_UID + "=\'" + u.getUid() + "\'", null);
		//Log.i(TAG, "long i:"+i+", COL_UID="+u.getUid());
		db.close();
		return i;
	}
	
	//update
	public synchronized long updateUserPassword(User u) {
		Log.d(TAG,"updateUserPassword User=" + u.getUid());
		SQLiteDatabase db= openDatabase();
		ContentValues args = new ContentValues();
		args.put(COL_UID, u.getUid());
		//args.put(COL_NAME, u.getName());
		args.put(COL_PASSWORD, u.getPassword());
		//args.put(COL_NICK_NAME, u.getNickname());
		//args.put(COL_GENDER, u.getGender());
		//args.put(COL_BIRTHDAY, u.getBirthday());
		//args.put(COL_MOBILE, u.getMobile());
		//args.put(COL_ACHIGH, u.getAcHigh());
		//args.put(COL_ACLOW, u.getAcLow());
		//args.put(COL_BHP, u.getBhp());
		//args.put(COL_BLP, u.getBlp());
		//args.put(COL_HEIGHT, u.getHeight());
		//args.put(COL_WEIGHT, u.getWeight());
		long i= db.update(TABLE_USER, args, COL_UID + "=\'" + u.getUid() + "\'", null);
		//Log.i(TAG, "long i:"+i+", COL_UID="+u.getUid());
		db.close();
		return i;
	}
	
	// get user by userId
	public synchronized User getUserByUid(String uid) {
		User u = null;
		SQLiteDatabase db=null;// openDatabase();
		try {
			//Log.i(TAG,"getUserByUid()=" + uid);
			db= openDatabase();
				Cursor uCursor = db.query(true,
					TABLE_USER,
					new String[] {COL_UID,COL_NAME }, 
					COL_UID + "=\'" + uid + "\'",
					null, 
					null,
					null,
					null,null);
			//Log.i(TAG,"Cursor="+uCursor );
			if (uCursor != null && uCursor.getCount() > 0) {
				uCursor.moveToFirst();
				u = new User();
				u.setUid(uCursor.getString(0));
				u.setName(uCursor.getString(1));					
			}
			uCursor.close();
		} catch (Exception e) {
			Log.e(TAG,"getUserByUid Fail() :"+ e.getMessage());
		}finally{
			if (null !=db){
				db.close();
			}
		}
		return u;
	}
	// get user uid
	public synchronized String getUID() {
		//Log.i(TAG,"getUID()");
		String u = null;
		SQLiteDatabase db=null;// openDatabase();
		try {
			db= openDatabase();
			Cursor uCursor = db.query(true,
				TABLE_USER,
				new String[] {COL_UID }, 
				null,
				null, 
				null,
				null,
				null,null);
			//Log.i(TAG,"Cursor="+uCursor );
			if (uCursor != null && uCursor.getCount() > 0) {
				uCursor.moveToFirst();
				u =uCursor.getString(0);
			}
			uCursor.close();
		} catch (Exception e) {
			Log.e(TAG,"getUID Fail() :"+ e.getMessage());
		}finally{
			if (null !=db){
				db.close();
			}
		}
		return u;
	}
	
	// get user Unit And Type
	public synchronized User getUIDUnitType() {
		//Log.i(TAG,"getUIDUnitType()");
		User u = null;
		SQLiteDatabase db = null;// openDatabase();
		try {
			db = openDatabase();
			Cursor uCursor = db.query(true,
				TABLE_USER,
				new String[] {COL_UID, COL_TYPE, COL_UNIT }, 
				null,
				null, 
				null,
				null,
				null,null);
			//Log.i(TAG,"Cursor="+uCursor );
			if (uCursor != null && uCursor.getCount() > 0) {
				uCursor.moveToFirst();
				u = new User();
				u.setUid(uCursor.getString(0));
				u.setType(uCursor.getString(1));
				u.setUnit(uCursor.getString(2));
			}
			uCursor.close();
		} catch (Exception e) {
			Log.e(TAG,"getUID Fail() :"+ e.getMessage());
		}finally{
			if (null !=db){
				db.close();
			}
		}
		return u;
	}
	
	// get user uid and password
	public synchronized User getUserUIdAndPassword() {
		//Log.i(TAG,"getUID()");
		User u = null;
		SQLiteDatabase db=null;// openDatabase();
		try {
			db= openDatabase();
			Cursor uCursor = db.query(true,
				TABLE_USER,
				new String[] {COL_UID, COL_PASSWORD }, 
				null,
				null, 
				null,
				null,
				null,null);
			//Log.i(TAG,"Cursor="+uCursor );
			if (uCursor != null && uCursor.getCount() > 0) {
				uCursor.moveToFirst();
				u = new User();
				u.setUid(uCursor.getString(0));
				u.setPassword(uCursor.getString(1));;
			}
			uCursor.close();
		} catch (Exception e) {
			Log.e(TAG,"getUserUIdAndPassword Fail() :"+ e.getMessage());
		}finally{
			if (null !=db){
				db.close();
			}
		}
		return u;
	}
	
	// get remember user
	public synchronized String getRememberUser() {
		//Log.i(TAG,"getRememberUser()");
		String u = null;
		SQLiteDatabase db=null;// openDatabase();
		try {
			db= openDatabase();
			// true為過濾重複值
			Cursor uCursor = db.query(true,
				TABLE_USER,
				new String[] {COL_REMEMBER_USER }, 
				null,
				null, 
				null,
				null,
				null,null);
			//Log.i(TAG,"Cursor="+uCursor );
			if (uCursor != null && uCursor.getCount() > 0) {
				uCursor.moveToFirst();
				u =uCursor.getString(0);
			}
			uCursor.close();
		} catch (Exception e) {
			Log.e(TAG,"getUID Fail() :"+ e.getMessage());
		}finally{
			if (null !=db){
				db.close();
			}
		}
		return u;
	}
	
	// get user weight
	public synchronized String getUserWeight() {
		//Log.i(TAG,"getUserWeight()");
		String u = null;
		SQLiteDatabase db=null;// openDatabase();
		try {
			db= openDatabase();
				Cursor uCursor = db.query(true,
					TABLE_USER,
					new String[] {COL_WEIGHT }, 
					null,
					null, 
					null,
					null,
					null,null);
			//Log.i(TAG,"Cursor="+uCursor );
			if (uCursor != null && uCursor.getCount() > 0) {
				uCursor.moveToFirst();
				u =uCursor.getString(0);
			}
			uCursor.close();
		} catch (Exception e) {
			Log.e(TAG,"getUID Fail() :"+ e.getMessage());
		}finally{
			if (null !=db){
				db.close();
			}
		}
		return u;
	}
	
	public synchronized void delAllUser(){
		SQLiteDatabase db=openDatabase();
		long status=db.delete(TABLE_USER,null,null);
		db.close();
		//Log.i(TAG,"Delete All User=" + status);		
	}
	//刪除紀錄，回傳成功刪除筆數
	public synchronized long delUser(String userId) {
		//	return db.delete("table_name",	//資料表名稱
		//	"_ID=" + rowId,			//WHERE
		//	null				//WHERE的參數
		//	);
		SQLiteDatabase db=openDatabase();
		long status =db.delete(TABLE_USER, ""+ userId, null);
		Log.d(TAG,"Delete User=" + userId + ",status=" + status);
		db.close();
		return status;
	}
}