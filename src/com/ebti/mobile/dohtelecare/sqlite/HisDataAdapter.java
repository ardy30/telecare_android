package com.ebti.mobile.dohtelecare.sqlite;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ebti.mobile.dohtelecare.constant.Constant;
import com.ebti.mobile.dohtelecare.model.HisData;

public class HisDataAdapter extends DbHisAdapter {
	private final static String TAG="HisDataAdapter";
	// ID : 編號
	public final static String COL_ID = "_ID";
	// USER_ID:使用者代號
	public final static String COL_USER_ID = "USER_ID";
	//設備序號
	public final static String COL_DEVICE_SN= "DEVICE_SN";
	//設備量測時間
	public final static String COL_DEVICE_TIME = "DEVICE_TIME";	
	//設備量測紀錄編號
	public final static String COL_DEVICE_ID = "DEVICE_ID";
	//	// DEVICE_TYPE  量測類型		// (1:體重、2:血糖、3:血壓)	
	public final static String COL_DEVICE_TYPE = "DEVICE_TYPE";
	//讀取時間
	public final static String COL_READ_TIME = "READ_TIME";
	//性別
	public final static String COL_SEX = "SEX";
	//年齡
	public final static String COL_AGE = "AGE";
	// 飯前血糖
	public final static String COL_AC = "AC";
	//飯後血糖	
	public final static String COL_PC = "PC";
	//隨機血糖(不知道是飯前或飯後，無法分類)	
	public final static String COL_NM = "NM";
	//收縮壓	
	public final static String COL_BHP = "BHP";		
	//舒張壓	
	public final static String COL_BLP = "BLP";
	//脈搏
	public final static String COL_PULSE = "PULSE";
	// UPLOADED  使否上傳(1.已上傳,0未上傳)
	public final static String COL_UPLOADED = "UPLOADED";
	// INPUT_TYPE  資料來源(Device.儀器資料,Manual.手動輸入資料)
	public final static String COL_INPUT_TYPE = "INPUT_TYPE";
	
	public final static String TABLE_HISDATA = "HM_HIS_DATA";
	
	SimpleDateFormat dateFormat =   new SimpleDateFormat("yyyy/MM/dd HH:mm");
	
	public HisDataAdapter(Context ctx) {
		super(ctx);
	}

	public synchronized int deleteHisData(long _id) {
		SQLiteDatabase db=openDatabase();
		int status=db.delete(TABLE_HISDATA, // 資料表名稱
				"_ID=" + _id, // WHERE
				null // WHERE的參數
				);
		return status;
	}
	//Create Data
	public synchronized void createHisData(HisData his){
		//Log.i(TAG,"Create HisData()");
		SQLiteDatabase db=openDatabase();
		ContentValues args = new ContentValues();
		args.put(COL_ID, his.get_id());
		args.put(COL_USER_ID, his.getUserId());
		args.put(COL_DEVICE_SN,  his.getDeviceSn());
		args.put(COL_DEVICE_TIME, his.getDeviceTime());
		args.put(COL_DEVICE_ID, his.getDeviceId());
		args.put(COL_DEVICE_TYPE,  his.getDeviceType());
		args.put(COL_READ_TIME, dateFormat.format(System.currentTimeMillis()));
		args.put(COL_AC, his.getAc());
		args.put(COL_PC, his.getPc());
		args.put(COL_BHP, his.getBhp());
		args.put(COL_BLP, his.getBlp());
		args.put(COL_PULSE, his.getPulse());
		args.put(COL_SEX, his.getSex());
		args.put(COL_UPLOADED, 0);
		args.put(COL_AGE, his.getAge());
		args.put(COL_INPUT_TYPE, his.getInputType());
		long i = db.insert(TABLE_HISDATA, null, args);
		db.close();
		//Log.i(TAG, "long i:"+i);
		return;
	}
	//Create  blood pressure record
	public synchronized long createBloodPressure(HisData his){
		//Log.i(TAG,"Create Bloodpressure()" + "");
		SQLiteDatabase db=openDatabase();
		ContentValues args = new ContentValues();
		args.put(COL_ID, his.get_id());
		args.put(COL_USER_ID, his.getUserId());
		args.put(COL_DEVICE_SN, his.getDeviceSn());
		args.put(COL_DEVICE_TIME, his.getDeviceTime());
		args.put(COL_DEVICE_TYPE,  Constant.BIODATA_DEVICE_TYPE_BLOOD_PRESSURE);
		args.put(COL_READ_TIME, dateFormat.format(System.currentTimeMillis()));
		args.put(COL_BHP, his.getBhp());
		args.put(COL_BLP, his.getBlp());
		args.put(COL_PULSE, his.getPulse());
		args.put(COL_UPLOADED, Constant.DATA_IS_NOT_UPLOAD);
		args.put(COL_INPUT_TYPE, his.getInputType());
		long i = db.insert(TABLE_HISDATA, null, args);
		db.close();
		return i;
	}
	
	//Create glucose record
	public synchronized long createGlucose(HisData hisData){
		//Log.i(TAG,"Create glucose()");
		SQLiteDatabase db=openDatabase();
		ContentValues args = new ContentValues();
		args.put(COL_ID, hisData.get_id());
		args.put(COL_USER_ID, hisData.getUserId());
		args.put(COL_DEVICE_SN,  hisData.getDeviceSn());
		args.put(COL_DEVICE_TIME, hisData.getDeviceTime());
		args.put(COL_DEVICE_TYPE, Constant.BIODATA_DEVICE_TYPE_BLOOD_GLUCOSE);
		args.put(COL_READ_TIME, dateFormat.format(System.currentTimeMillis()));
		args.put(COL_AC,  hisData.getAc());
		args.put(COL_PC,  hisData.getPc());
		args.put(COL_NM,  hisData.getNm());
		args.put(COL_UPLOADED, Constant.DATA_IS_NOT_UPLOAD);
		args.put(COL_INPUT_TYPE, hisData.getInputType());
		long i= db.insert(TABLE_HISDATA, null, args);
		db.close();
		return i;
	}

	//刪除所有資料
	public synchronized void delAll(){
		SQLiteDatabase db=openDatabase();
		long status=db.delete(TABLE_HISDATA,null,null);
		db.close();
		//Log.i(TAG,"Delete All TABLE_HISDATA=" + status);		
	}
	
	//取得血糖資訊
	public synchronized ArrayList<HisData> getBloodGlucose(){
        ArrayList results = new ArrayList();
		SQLiteDatabase db=openDatabase();
        try {
            Cursor cursor = db.query(TABLE_HISDATA,
                    new String[]{
            		COL_ID,
            		COL_USER_ID,
            		COL_DEVICE_SN,
            		COL_DEVICE_TIME ,
            		COL_DEVICE_ID,
            		COL_DEVICE_TYPE,
            		COL_READ_TIME,
            		COL_AGE,
            		COL_AC,
            		COL_PC,
            		COL_NM,
            		COL_BHP,
            		COL_BLP,
            		COL_PULSE,
            		COL_UPLOADED,
            		COL_SEX,
            		COL_INPUT_TYPE
            		},
                    COL_DEVICE_TYPE+" = '" + Constant.BIODATA_DEVICE_TYPE_BLOOD_GLUCOSE + "'", null, null, null, COL_DEVICE_TIME +" DESC");
            int num = cursor.getCount();
            cursor.moveToFirst();
            for (int i = 0; i < num; i++) {
            	HisData h= new HisData();
            	h.set_id(cursor.getString(0));
            	h.setUserId(cursor.getString(1));
            	h.setDeviceSn(cursor.getString(2));
            	h.setDeviceTime(cursor.getString(3));
            	h.setDeviceId(cursor.getString(4));
            	h.setDeviceType(cursor.getString(5));
            	h.setReadTime(cursor.getString(6));
            	h.setAge(cursor.getString(7));
            	h.setAc(cursor.getString(8));
            	h.setPc(cursor.getString(9));
            	h.setNm(cursor.getString(10));
            	h.setBhp(cursor.getString(11));
            	h.setBlp(cursor.getString(12));
            	h.setPulse(cursor.getString(13));
            	h.setUploaded(cursor.getInt(14));
            	h.setSex(cursor.getString(15));
            	h.setInputType(cursor.getString(16));
            	Log.d(TAG,"HisData=" + h.toString());
                results.add(h);
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
        	Log.e(TAG,"getBloodGlucose() Fail :"+ e.getMessage());
        }finally{
        	db.close();
        }
        return results;
	}
	
	//取得飯前血糖
	public synchronized ArrayList<HisData> getBloodGlucoseAC(){
        ArrayList results = new ArrayList();
		SQLiteDatabase db=openDatabase();
        try {
            Cursor cursor = db.query(TABLE_HISDATA,
                    new String[]{
            		COL_ID,
            		COL_USER_ID,
            		COL_DEVICE_SN,
            		COL_DEVICE_TIME ,
            		COL_DEVICE_ID,
            		COL_DEVICE_TYPE,
            		COL_READ_TIME,
            		COL_AGE,
            		COL_AC,
            		COL_PC,
            		COL_NM,
            		COL_BHP,
            		COL_BLP,
            		COL_PULSE,
            		COL_UPLOADED,
            		COL_SEX,
            		COL_INPUT_TYPE
            		},
                    COL_DEVICE_TYPE+" = '" + Constant.BIODATA_DEVICE_TYPE_BLOOD_GLUCOSE + "' and " + COL_AC + " != 'null'", null, null, null, COL_DEVICE_TIME +" DESC");
            int num = cursor.getCount();
            cursor.moveToFirst();
            for (int i = 0; i < num; i++) {
            	HisData h= new HisData();
            	h.set_id(cursor.getString(0));
            	h.setUserId(cursor.getString(1));
            	h.setDeviceSn(cursor.getString(2));
            	h.setDeviceTime(cursor.getString(3));
            	h.setDeviceId(cursor.getString(4));
            	h.setDeviceType(cursor.getString(5));
            	h.setReadTime(cursor.getString(6));
            	h.setAge(cursor.getString(7));
            	h.setAc(cursor.getString(8));
            	h.setPc(cursor.getString(9));
            	h.setNm(cursor.getString(10));
            	h.setBhp(cursor.getString(11));
            	h.setBlp(cursor.getString(12));
            	h.setPulse(cursor.getString(13));
            	h.setUploaded(cursor.getInt(14));
            	h.setSex(cursor.getString(15));
            	h.setInputType(cursor.getString(16));
            	Log.d(TAG,"HisData=" + h.toString());
                results.add(h);
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
        	Log.e(TAG,"getBloodGlucose() Fail :"+ e.getMessage());
        }finally{
        	db.close();
        }
        return results;
	}
	
	//取得飯前血糖
	public synchronized ArrayList<HisData> getBloodGlucosePC(){
        ArrayList results = new ArrayList();
		SQLiteDatabase db=openDatabase();
        try {
            Cursor cursor = db.query(TABLE_HISDATA,
                    new String[]{
            		COL_ID,
            		COL_USER_ID,
            		COL_DEVICE_SN,
            		COL_DEVICE_TIME ,
            		COL_DEVICE_ID,
            		COL_DEVICE_TYPE,
            		COL_READ_TIME,
            		COL_AGE,
            		COL_AC,
            		COL_PC,
            		COL_NM,
            		COL_BHP,
            		COL_BLP,
            		COL_PULSE,
            		COL_UPLOADED,
            		COL_SEX,
            		COL_INPUT_TYPE
            		},
                    COL_DEVICE_TYPE+" = '" + Constant.BIODATA_DEVICE_TYPE_BLOOD_GLUCOSE + "' and " + COL_PC + " != 'null'", null, null, null, COL_DEVICE_TIME +" DESC");
            int num = cursor.getCount();
            cursor.moveToFirst();
            for (int i = 0; i < num; i++) {
            	HisData h= new HisData();
            	h.set_id(cursor.getString(0));
            	h.setUserId(cursor.getString(1));
            	h.setDeviceSn(cursor.getString(2));
            	h.setDeviceTime(cursor.getString(3));
            	h.setDeviceId(cursor.getString(4));
            	h.setDeviceType(cursor.getString(5));
            	h.setReadTime(cursor.getString(6));
            	h.setAge(cursor.getString(7));
            	h.setAc(cursor.getString(8));
            	h.setPc(cursor.getString(9));
            	h.setNm(cursor.getString(10));
            	h.setBhp(cursor.getString(11));
            	h.setBlp(cursor.getString(12));
            	h.setPulse(cursor.getString(13));
            	h.setUploaded(cursor.getInt(14));
            	h.setSex(cursor.getString(15));
            	h.setInputType(cursor.getString(16));
            	Log.d(TAG,"HisData=" + h.toString());
                results.add(h);
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
        	Log.e(TAG,"getBloodGlucose() Fail :"+ e.getMessage());
        }finally{
        	db.close();
        }
        return results;
	}
	
	//取得飯前血糖
	public synchronized ArrayList<HisData> getBloodGlucoseNM(){
        ArrayList results = new ArrayList();
		SQLiteDatabase db=openDatabase();
        try {
            Cursor cursor = db.query(TABLE_HISDATA,
                    new String[]{
            		COL_ID,
            		COL_USER_ID,
            		COL_DEVICE_SN,
            		COL_DEVICE_TIME ,
            		COL_DEVICE_ID,
            		COL_DEVICE_TYPE,
            		COL_READ_TIME,
            		COL_AGE,
            		COL_AC,
            		COL_PC,
            		COL_NM,
            		COL_BHP,
            		COL_BLP,
            		COL_PULSE,
            		COL_UPLOADED,
            		COL_SEX,
            		COL_INPUT_TYPE
            		},
                    COL_DEVICE_TYPE+" = '" + Constant.BIODATA_DEVICE_TYPE_BLOOD_GLUCOSE + "' and " + COL_NM + " != 'null'", null, null, null, COL_DEVICE_TIME +" DESC");
            int num = cursor.getCount();
            cursor.moveToFirst();
            for (int i = 0; i < num; i++) {
            	HisData h= new HisData();
            	h.set_id(cursor.getString(0));
            	h.setUserId(cursor.getString(1));
            	h.setDeviceSn(cursor.getString(2));
            	h.setDeviceTime(cursor.getString(3));
            	h.setDeviceId(cursor.getString(4));
            	h.setDeviceType(cursor.getString(5));
            	h.setReadTime(cursor.getString(6));
            	h.setAge(cursor.getString(7));
            	h.setAc(cursor.getString(8));
            	h.setPc(cursor.getString(9));
            	h.setNm(cursor.getString(10));
            	h.setBhp(cursor.getString(11));
            	h.setBlp(cursor.getString(12));
            	h.setPulse(cursor.getString(13));
            	h.setUploaded(cursor.getInt(14));
            	h.setSex(cursor.getString(15));
            	h.setInputType(cursor.getString(16));
            	Log.d(TAG,"HisData=" + h.toString());
                results.add(h);
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
        	Log.e(TAG,"getBloodGlucose() Fail :"+ e.getMessage());
        }finally{
        	db.close();
        }
        return results;
	}
	
	//取得血壓資訊
	public synchronized ArrayList<HisData> getBloodPressure(){
        ArrayList results = new ArrayList();
		SQLiteDatabase db=openDatabase();
        try {
            Cursor cursor = db.query(TABLE_HISDATA,
                    new String[]{
            		COL_ID,
            		COL_USER_ID,
            		COL_DEVICE_SN,
            		COL_DEVICE_TIME ,
            		COL_DEVICE_ID,
            		COL_DEVICE_TYPE,
            		COL_READ_TIME,
            		COL_AGE,
            		COL_AC,
            		COL_PC,
            		COL_NM,
            		COL_BHP,
            		COL_BLP,
            		COL_PULSE,
            		COL_UPLOADED,
            		COL_SEX,
            		COL_INPUT_TYPE
            		},
                    COL_DEVICE_TYPE+" = '" + Constant.BIODATA_DEVICE_TYPE_BLOOD_PRESSURE + "'", null, null, null,  COL_DEVICE_TIME + " DESC");
            int num = cursor.getCount();
            cursor.moveToFirst();
            for (int i = 0; i < num; i++) {
            	HisData h= new HisData();
            	h.set_id(cursor.getString(0));
            	h.setUserId(cursor.getString(1));
            	h.setDeviceSn(cursor.getString(2));
            	h.setDeviceTime(cursor.getString(3));
            	h.setDeviceId(cursor.getString(4));
            	h.setDeviceType(cursor.getString(5));
            	h.setReadTime(cursor.getString(6));
            	h.setAge(cursor.getString(7));
            	h.setAc(cursor.getString(8));
            	h.setPc(cursor.getString(9));
            	h.setNm(cursor.getString(10));
            	h.setBhp(cursor.getString(11));
            	h.setBlp(cursor.getString(12));
            	h.setPulse(cursor.getString(13));
            	h.setUploaded(cursor.getInt(14));
            	h.setSex(cursor.getString(15));
            	h.setInputType(cursor.getString(16));
            	Log.d(TAG,"HisData=" + h.toString());
                results.add(h);
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
        	Log.e(TAG,"getBloodPressure() Fail :"+ e.getMessage());
        }finally{
        	db.close();
        }
        return results;
	}
	
	//取得所有生理資訊
	public synchronized ArrayList<HisData> getAll(){
        ArrayList results = new ArrayList();
		SQLiteDatabase db=openDatabase();
        try {
            Cursor cursor = db.query(TABLE_HISDATA,
                    new String[]{
            		COL_ID,
            		COL_USER_ID,
            		COL_DEVICE_SN,
            		COL_DEVICE_TIME ,
            		COL_DEVICE_ID,
            		COL_DEVICE_TYPE,
            		COL_READ_TIME,
            		COL_AGE,
            		COL_AC,
            		COL_PC,
            		COL_NM,
            		COL_BHP,
            		COL_BLP,
            		COL_PULSE,
            		COL_UPLOADED,
            		COL_SEX,
            		COL_INPUT_TYPE
            		},
                    null, null, null, null, COL_DEVICE_TIME +" DESC");
            int num = cursor.getCount();
            cursor.moveToFirst();
            for (int i = 0; i < num; i++) {
            	HisData h= new HisData();
            	h.set_id(cursor.getString(0));
            	h.setUserId(cursor.getString(1));
            	h.setDeviceSn(cursor.getString(2));
            	h.setDeviceTime(cursor.getString(3));
            	h.setDeviceId(cursor.getString(4));
            	h.setDeviceType(cursor.getString(5));
            	h.setReadTime(cursor.getString(6));
            	h.setAge(cursor.getString(7));
            	h.setAc(cursor.getString(8));
            	h.setPc(cursor.getString(9));
            	h.setNm(cursor.getString(10));
            	h.setBhp(cursor.getString(11));
            	h.setBlp(cursor.getString(12));
            	h.setPulse(cursor.getString(13));
            	h.setUploaded(cursor.getInt(14));
            	h.setSex(cursor.getString(15));
            	h.setInputType(cursor.getString(16));
            	Log.d(TAG,"HisData=" + h.toString());
                results.add(h);
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
        	Log.e(TAG,"getAll() Fail :"+ e.getMessage());
        }finally{
        	db.close();
        }
        return results;
	}
}