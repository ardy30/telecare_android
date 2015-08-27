package com.ebti.mobile.dohtelecare.sqlite;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ebti.mobile.dohtelecare.constant.Constant;
import com.ebti.mobile.dohtelecare.model.BioData;
import com.ebti.mobile.dohtelecare.model.SqlVersion;

public class SqlVersionAdapter extends DbAdapter {
	private final static String TAG="SqlVersionAdapter";
	// ID : 編號
	public final static String COL_SQL_VERSION = "SQL_VERSION";
	
	public final static String TABLE_BIODATA="HM_SQL_VERSION";
	
	SimpleDateFormat dateFormat =   new SimpleDateFormat("yyyy/MM/dd HH:mm");
	
	public SqlVersionAdapter(Context ctx) {
		super(ctx);
	}
	
	//取得版本資訊
	public synchronized ArrayList<SqlVersion> getSqlVersion(){
        ArrayList results = new ArrayList();
		SQLiteDatabase db=openDatabase();
        try {
            Cursor cursor = db.query(TABLE_BIODATA,
                    new String[]{
            		COL_SQL_VERSION,
            		},
                    null, null, null, null, null);
            int num = cursor.getCount();
            cursor.moveToFirst();
            for (int i = 0; i < num; i++) {
	            	SqlVersion s= new SqlVersion();
	            	s.setSql_Version(cursor.getString(0));
	            	Log.d(TAG,"SqlVersion=" + s.toString() + "\n version :" + cursor.getString(0));
	                results.add(s);
	                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
        		Log.e(TAG,"getWieght() Fail :"+ e.getMessage());
        }finally{
        		db.close();
        }
        return results;
	}
}