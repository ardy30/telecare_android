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

public class BioDataAdapter extends DbAdapter {
	private final static String TAG="BioDataAdapter";
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
	//身高紀錄
	public final static String COL_BODY_HEIGHT = "BODY_HEIGHT";
	//體重計路
	public final static String COL_BODY_WEIGHT = "BODY_WEIGHT";	
	//身體質量指數
	public final static String COL_BMI = "BMI";
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
	// UPLOADED  是否上傳(1.已上傳,0未上傳)
	public final static String COL_UPLOADED = "UPLOADED";
	// INPUT_TYPE  資料來源(Device.儀器資料,Manual.手動輸入資料)
	public final static String COL_INPUT_TYPE = "INPUT_TYPE";
	
	public final static String TABLE_BIODATA="HM_BIO_DATA";
	
	SimpleDateFormat dateFormat =   new SimpleDateFormat("yyyy/MM/dd HH:mm");
	
	public BioDataAdapter(Context ctx) {
		super(ctx);
	}

	public synchronized int deleteBioData(long _id) {
		SQLiteDatabase db=openDatabase();
		int status=db.delete(TABLE_BIODATA, // 資料表名稱
				"_ID=" + _id, // WHERE
				null // WHERE的參數
				);
		return status;
	}
	//Create Data
	public synchronized void createBioData(BioData bio){
		//Log.i(TAG,"Create BioData()");
		SQLiteDatabase db=openDatabase();
		ContentValues args = new ContentValues();
		args.put(COL_ID, bio.get_id());
		args.put(COL_USER_ID, bio.getUserId());
		args.put(COL_DEVICE_SN,  bio.getDeviceSn());
		args.put(COL_DEVICE_TIME, bio.getDeviceTime());
		args.put(COL_DEVICE_ID, bio.getDeviceId());
		args.put(COL_DEVICE_TYPE,  bio.getDeviceType());
		args.put(COL_READ_TIME, dateFormat.format(System.currentTimeMillis()));
		args.put(COL_BODY_HEIGHT, bio.getBodyHeight());
		args.put(COL_BODY_WEIGHT, bio.getBodyWeight());
		args.put(COL_AC, bio.getAc());
		args.put(COL_PC, bio.getPc());
		args.put(COL_NM, bio.getNm());
		args.put(COL_BHP, bio.getBhp());
		args.put(COL_BLP, bio.getBlp());
		args.put(COL_PULSE, bio.getPulse());
		args.put(COL_SEX, bio.getSex());
		args.put(COL_UPLOADED, 0);
		args.put(COL_AGE, bio.getAge());
		args.put(COL_BMI, bio.getBmi());
		args.put(COL_INPUT_TYPE, bio.getInputType());
		long i = db.insert(TABLE_BIODATA, null, args);
		db.close();
		//Log.i(TAG, "long i:"+i);
		return;
	}
	//Create  blood pressure record
	public synchronized long createBloodPressure(BioData bio){
		//Log.i(TAG,"Create Bloodpressure()" + "");
		SQLiteDatabase db=openDatabase();
		ContentValues args = new ContentValues();
		args.put(COL_ID, bio.get_id());
		args.put(COL_USER_ID, bio.getUserId());
		args.put(COL_DEVICE_ID,  bio.getDeviceId());
		args.put(COL_DEVICE_SN, bio.getDeviceSn());
		args.put(COL_DEVICE_TIME, bio.getDeviceTime());
		args.put(COL_DEVICE_TYPE,  Constant.BIODATA_DEVICE_TYPE_BLOOD_PRESSURE);
		args.put(COL_READ_TIME, dateFormat.format(System.currentTimeMillis()));
		args.put(COL_BHP, bio.getBhp());
		args.put(COL_BLP, bio.getBlp());
		args.put(COL_PULSE, bio.getPulse());
		args.put(COL_INPUT_TYPE, bio.getInputType());
		args.put(COL_UPLOADED, Constant.DATA_IS_NOT_UPLOAD);
		long i = db.insert(TABLE_BIODATA, null, args);		
		db.close();
		return i;
	}
	
	//Create glucose record
	public synchronized long createGlucose(BioData bioData){
		//Log.i(TAG,"Create glucose()");
		SQLiteDatabase db=openDatabase();
		ContentValues args = new ContentValues();
		args.put(COL_ID, bioData.get_id());
		args.put(COL_USER_ID, bioData.getUserId());
		args.put(COL_DEVICE_ID,  bioData.getDeviceId());
		args.put(COL_DEVICE_SN,  bioData.getDeviceSn());
		args.put(COL_DEVICE_TIME, bioData.getDeviceTime());
		args.put(COL_DEVICE_TYPE, Constant.BIODATA_DEVICE_TYPE_BLOOD_GLUCOSE);
		args.put(COL_READ_TIME, dateFormat.format(System.currentTimeMillis()));
		args.put(COL_AC,  bioData.getAc());
		args.put(COL_PC,  bioData.getPc());
		args.put(COL_NM,  bioData.getNm());
		args.put(COL_INPUT_TYPE, bioData.getInputType());
		args.put(COL_UPLOADED, 0);
		long i= db.insert(TABLE_BIODATA, null, args);
		db.close();
		return i;
	}
	
	//Create height & weight
	public synchronized long createWeight(BioData bioData){
		//Log.i(TAG,"Create glucose()" + "");
		SQLiteDatabase db=openDatabase();
		ContentValues args = new ContentValues();
		args.put(COL_ID, bioData.get_id());
		args.put(COL_USER_ID, bioData.getUserId());
		args.put(COL_DEVICE_SN,  bioData.getDeviceSn());
		args.put(COL_DEVICE_TIME, bioData.getDeviceTime());
		args.put(COL_DEVICE_TYPE,  Constant.BIODATA_DEVICE_TYPE_WEIGHT);
		args.put(COL_READ_TIME, dateFormat.format(System.currentTimeMillis()));
		args.put(COL_BODY_HEIGHT,  bioData.getBodyHeight());
		args.put(COL_BODY_WEIGHT,  bioData.getBodyWeight());
		args.put(COL_INPUT_TYPE, bioData.getInputType());
		args.put(COL_UPLOADED, 0);
		long i= db.insert(TABLE_BIODATA, null, args);
		db.close();
		return i;
	}

	//刪除所有資料
	public synchronized void delAll(){
		SQLiteDatabase db=openDatabase();
		long status=db.delete(TABLE_BIODATA,null,null);
		db.close();
		//Log.i(TAG,"Delete All TABLE_BIODATA=" + status);		
	}
	
	//刪除超過90天的資料
	public synchronized void delBefore90DaysData(){
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_YEAR, -90);
		Log.i("info", "Calendar.DAY_OF_YEAR:"+ Calendar.DAY_OF_YEAR);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String deviceTime = sdf.format(c.getTime());
		Log.i("info", "deviceTime:" + deviceTime);
		SQLiteDatabase db=openDatabase();
		long status=db.delete(TABLE_BIODATA,COL_DEVICE_TIME + " < '" + deviceTime + "'",null);
		db.close();
		//Log.i(TAG,"Delete delBefore90DaysData TABLE_BIODATA=" + status);		
	}
	
	//取得體重資訊
	public synchronized ArrayList<BioData> getWieght(){
        ArrayList results = new ArrayList();
		SQLiteDatabase db=openDatabase();
        try {
            Cursor cursor = db.query(TABLE_BIODATA,
                    new String[]{
            		COL_ID,
            		COL_USER_ID,
            		COL_DEVICE_SN,
            		COL_DEVICE_TIME ,
            		COL_DEVICE_ID,
            		COL_DEVICE_TYPE,
            		COL_READ_TIME,
            		COL_AGE,
            		COL_BODY_HEIGHT,
            		COL_BODY_WEIGHT,
            		COL_AC,
            		COL_PC,
            		COL_NM,
            		COL_BHP,
            		COL_BLP,
            		COL_PULSE,
            		COL_UPLOADED,
            		COL_BMI,
            		COL_SEX,
            		COL_INPUT_TYPE
            		},
                    COL_DEVICE_TYPE+" = '" + Constant.BIODATA_DEVICE_TYPE_WEIGHT + "'", null, null, null, COL_DEVICE_TIME +" DESC");
            int num = cursor.getCount();
            cursor.moveToFirst();
            for (int i = 0; i < num; i++) {
            	BioData b= new BioData();
            	b.set_id(cursor.getString(0));
            	b.setUserId(cursor.getString(1));
            	b.setDeviceSn(cursor.getString(2));
            	b.setDeviceTime(cursor.getString(3));
            	b.setDeviceId(cursor.getString(4));
            	b.setDeviceType(cursor.getString(5));
            	b.setReadTime(cursor.getString(6));
            	b.setAge(cursor.getString(7));
            	b.setBodyHeight(cursor.getString(8));
            	b.setBodyWeight(cursor.getString(9));
            	b.setAc(cursor.getString(10));
            	b.setPc(cursor.getString(11));
            	b.setNm(cursor.getString(12));
            	b.setBhp(cursor.getString(13));
            	b.setBlp(cursor.getString(14));
            	b.setPulse(cursor.getString(15));
            	b.setUploaded(cursor.getInt(16));
            	b.setBmi(cursor.getString(17));
            	b.setSex(cursor.getString(18));
            	b.setInputType(cursor.getString(19));
            	Log.d(TAG,"BioData=" + b.toString());
                results.add(b);
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
	
	//取得血糖資訊
	public synchronized ArrayList<BioData> getBloodGlucose(){
        ArrayList results = new ArrayList();
		SQLiteDatabase db=openDatabase();
        try {
            Cursor cursor = db.query(TABLE_BIODATA,
                    new String[]{
            		COL_ID,
            		COL_USER_ID,
            		COL_DEVICE_SN,
            		COL_DEVICE_TIME ,
            		COL_DEVICE_ID,
            		COL_DEVICE_TYPE,
            		COL_READ_TIME,
            		COL_AGE,
            		COL_BODY_HEIGHT,
            		COL_BODY_WEIGHT,
            		COL_AC,
            		COL_PC,
            		COL_NM,
            		COL_BHP,
            		COL_BLP,
            		COL_PULSE,
            		COL_UPLOADED,
            		COL_BMI,
            		COL_SEX,
            		COL_INPUT_TYPE
            		},
                    COL_DEVICE_TYPE+" = '" + Constant.BIODATA_DEVICE_TYPE_BLOOD_GLUCOSE + "'", null, null, null, COL_DEVICE_TIME +" DESC");
            int num = cursor.getCount();
            cursor.moveToFirst();
            for (int i = 0; i < num; i++) {
            	BioData b= new BioData();
            	b.set_id(cursor.getString(0));
            	b.setUserId(cursor.getString(1));
            	b.setDeviceSn(cursor.getString(2));
            	b.setDeviceTime(cursor.getString(3));
            	b.setDeviceId(cursor.getString(4));
            	b.setDeviceType(cursor.getString(5));
            	b.setReadTime(cursor.getString(6));
            	b.setAge(cursor.getString(7));
            	b.setBodyHeight(cursor.getString(8));
            	b.setBodyWeight(cursor.getString(9));
            	b.setAc(cursor.getString(10));
            	b.setPc(cursor.getString(11));
            	b.setNm(cursor.getString(12));
            	b.setBhp(cursor.getString(13));
            	b.setBlp(cursor.getString(14));
            	b.setPulse(cursor.getString(15));
            	b.setUploaded(cursor.getInt(16));
            	b.setBmi(cursor.getString(17));
            	b.setSex(cursor.getString(18));
            	b.setInputType(cursor.getString(19));
            	Log.d(TAG,"BioData=" + b.toString());
                results.add(b);
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
	
	//取得血糖資訊
	public synchronized ArrayList<BioData> getUserBloodGlucose(String userId){
        ArrayList results = new ArrayList();
		SQLiteDatabase db=openDatabase();
        try {
            Cursor cursor = db.query(TABLE_BIODATA,
                    new String[]{
            		COL_ID,
            		COL_USER_ID,
            		COL_DEVICE_SN,
            		COL_DEVICE_TIME ,
            		COL_DEVICE_ID,
            		COL_DEVICE_TYPE,
            		COL_READ_TIME,
            		COL_AGE,
            		COL_BODY_HEIGHT,
            		COL_BODY_WEIGHT,
            		COL_AC,
            		COL_PC,
            		COL_NM,
            		COL_BHP,
            		COL_BLP,
            		COL_PULSE,
            		COL_UPLOADED,
            		COL_BMI,
            		COL_SEX,
            		COL_INPUT_TYPE
            		},
                    COL_DEVICE_TYPE+" = '" + Constant.BIODATA_DEVICE_TYPE_BLOOD_GLUCOSE 
                    + "' and " + COL_USER_ID + " = '" + userId + "' ", null, null, null, COL_DEVICE_TIME +" DESC");
            int num = cursor.getCount();
            cursor.moveToFirst();
            for (int i = 0; i < num; i++) {
            	BioData b= new BioData();
            	b.set_id(cursor.getString(0));
            	b.setUserId(cursor.getString(1));
            	b.setDeviceSn(cursor.getString(2));
            	b.setDeviceTime(cursor.getString(3));
            	b.setDeviceId(cursor.getString(4));
            	b.setDeviceType(cursor.getString(5));
            	b.setReadTime(cursor.getString(6));
            	b.setAge(cursor.getString(7));
            	b.setBodyHeight(cursor.getString(8));
            	b.setBodyWeight(cursor.getString(9));
            	b.setAc(cursor.getString(10));
            	b.setPc(cursor.getString(11));
            	b.setNm(cursor.getString(12));
            	b.setBhp(cursor.getString(13));
            	b.setBlp(cursor.getString(14));
            	b.setPulse(cursor.getString(15));
            	b.setUploaded(cursor.getInt(16));
            	b.setBmi(cursor.getString(17));
            	b.setSex(cursor.getString(18));
            	b.setInputType(cursor.getString(19));
            	Log.d(TAG,"BioData=" + b.toString());
                results.add(b);
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
	
	//取得時間區間內血糖資訊
	public synchronized ArrayList<BioData> getUserBloodGlucose(String userId,String beginDate ,String endDate){
        ArrayList results = new ArrayList();
		SQLiteDatabase db=openDatabase();
        try {
            Cursor cursor = db.query(TABLE_BIODATA,
                    new String[]{
            		COL_ID,
            		COL_USER_ID,
            		COL_DEVICE_SN,
            		COL_DEVICE_TIME ,
            		COL_DEVICE_ID,
            		COL_DEVICE_TYPE,
            		COL_READ_TIME,
            		COL_AGE,
            		COL_BODY_HEIGHT,
            		COL_BODY_WEIGHT,
            		COL_AC,
            		COL_PC,
            		COL_NM,
            		COL_BHP,
            		COL_BLP,
            		COL_PULSE,
            		COL_UPLOADED,
            		COL_BMI,
            		COL_SEX,
            		COL_INPUT_TYPE
            		},
                    COL_DEVICE_TYPE+" = '" + Constant.BIODATA_DEVICE_TYPE_BLOOD_GLUCOSE 
                    + "' and " + COL_USER_ID + " = '" + userId + "' "+" and "+COL_DEVICE_TIME+" >= '"+beginDate+"' and "+COL_DEVICE_TIME+" <= '"+endDate+"' ", null, null, null, COL_DEVICE_TIME +" DESC");
            int num = cursor.getCount();
            cursor.moveToFirst();
            for (int i = 0; i < num; i++) {
            	BioData b= new BioData();
            	b.set_id(cursor.getString(0));
            	b.setUserId(cursor.getString(1));
            	b.setDeviceSn(cursor.getString(2));
            	b.setDeviceTime(cursor.getString(3));
            	b.setDeviceId(cursor.getString(4));
            	b.setDeviceType(cursor.getString(5));
            	b.setReadTime(cursor.getString(6));
            	b.setAge(cursor.getString(7));
            	b.setBodyHeight(cursor.getString(8));
            	b.setBodyWeight(cursor.getString(9));
            	b.setAc(cursor.getString(10));
            	b.setPc(cursor.getString(11));
            	b.setNm(cursor.getString(12));
            	b.setBhp(cursor.getString(13));
            	b.setBlp(cursor.getString(14));
            	b.setPulse(cursor.getString(15));
            	b.setUploaded(cursor.getInt(16));
            	b.setBmi(cursor.getString(17));
            	b.setSex(cursor.getString(18));
            	b.setInputType(cursor.getString(19));
            	Log.d(TAG,"BioData=" + b.toString());
                results.add(b);
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
	public synchronized ArrayList<BioData> getBloodPressure(){
        ArrayList results = new ArrayList();
		SQLiteDatabase db=openDatabase();
        try {
            Cursor cursor = db.query(TABLE_BIODATA,
                    new String[]{
            		COL_ID,
            		COL_USER_ID,
            		COL_DEVICE_SN,
            		COL_DEVICE_TIME ,
            		COL_DEVICE_ID,
            		COL_DEVICE_TYPE,
            		COL_READ_TIME,
            		COL_AGE,
            		COL_BODY_HEIGHT,
            		COL_BODY_WEIGHT,
            		COL_AC,
            		COL_PC,
            		COL_NM,
            		COL_BHP,
            		COL_BLP,
            		COL_PULSE,
            		COL_UPLOADED,
            		COL_BMI,
            		COL_SEX,
            		COL_INPUT_TYPE
            		},
                    COL_DEVICE_TYPE+" = '" + Constant.BIODATA_DEVICE_TYPE_BLOOD_PRESSURE + "'", null, null, null,  COL_DEVICE_TIME +" DESC");
            int num = cursor.getCount();
            cursor.moveToFirst();
            for (int i = 0; i < num; i++) {
            	BioData b= new BioData();
            	b.set_id(cursor.getString(0));
            	b.setUserId(cursor.getString(1));
            	b.setDeviceSn(cursor.getString(2));
            	b.setDeviceTime(cursor.getString(3));
            	b.setDeviceId(cursor.getString(4));
            	b.setDeviceType(cursor.getString(5));
            	b.setReadTime(cursor.getString(6));
            	b.setAge(cursor.getString(7));
            	b.setBodyHeight(cursor.getString(8));
            	b.setBodyWeight(cursor.getString(9));
            	b.setAc(cursor.getString(10));
            	b.setPc(cursor.getString(11));
            	b.setNm(cursor.getString(12));
            	b.setBhp(cursor.getString(13));
            	b.setBlp(cursor.getString(14));
            	b.setPulse(cursor.getString(15));
            	b.setUploaded(cursor.getInt(16));
            	b.setBmi(cursor.getString(17));
            	b.setSex(cursor.getString(18));
            	b.setInputType(cursor.getString(19));
            	Log.d(TAG,"BioData=" + b.toString());
                results.add(b);
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
	
	//取得血壓資訊
	public synchronized ArrayList<BioData> getUserBloodPressure(String userId){
        ArrayList results = new ArrayList();
		SQLiteDatabase db=openDatabase();
        try {
            Cursor cursor = db.query(TABLE_BIODATA,
                    new String[]{
            		COL_ID,
            		COL_USER_ID,
            		COL_DEVICE_SN,
            		COL_DEVICE_TIME ,
            		COL_DEVICE_ID,
            		COL_DEVICE_TYPE,
            		COL_READ_TIME,
            		COL_AGE,
            		COL_BODY_HEIGHT,
            		COL_BODY_WEIGHT,
            		COL_AC,
            		COL_PC,
            		COL_NM,
            		COL_BHP,
            		COL_BLP,
            		COL_PULSE,
            		COL_UPLOADED,
            		COL_BMI,
            		COL_SEX,
            		COL_INPUT_TYPE
            		},
                    COL_DEVICE_TYPE+" = '" + Constant.BIODATA_DEVICE_TYPE_BLOOD_PRESSURE 
                    + "' and " + COL_USER_ID + " = '" + userId + "'", null, null, null,  COL_DEVICE_TIME +" DESC");
            int num = cursor.getCount();
            cursor.moveToFirst();
            for (int i = 0; i < num; i++) {
            	BioData b= new BioData();
            	b.set_id(cursor.getString(0));
            	b.setUserId(cursor.getString(1));
            	b.setDeviceSn(cursor.getString(2));
            	b.setDeviceTime(cursor.getString(3));
            	b.setDeviceId(cursor.getString(4));
            	b.setDeviceType(cursor.getString(5));
            	b.setReadTime(cursor.getString(6));
            	b.setAge(cursor.getString(7));
            	b.setBodyHeight(cursor.getString(8));
            	b.setBodyWeight(cursor.getString(9));
            	b.setAc(cursor.getString(10));
            	b.setPc(cursor.getString(11));
            	b.setNm(cursor.getString(12));
            	b.setBhp(cursor.getString(13));
            	b.setBlp(cursor.getString(14));
            	b.setPulse(cursor.getString(15));
            	b.setUploaded(cursor.getInt(16));
            	b.setBmi(cursor.getString(17));
            	b.setSex(cursor.getString(18));
            	b.setInputType(cursor.getString(19));
            	Log.d(TAG,"BioData=" + b.toString());
                results.add(b);
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
	
	//取得時間區間內血壓資訊
	public synchronized ArrayList<BioData> getUserBloodPressure(String userId,String beginDate,String endDate){
        ArrayList results = new ArrayList();
		SQLiteDatabase db=openDatabase();
        try {
            Cursor cursor = db.query(TABLE_BIODATA,
                    new String[]{
            		COL_ID,
            		COL_USER_ID,
            		COL_DEVICE_SN,
            		COL_DEVICE_TIME ,
            		COL_DEVICE_ID,
            		COL_DEVICE_TYPE,
            		COL_READ_TIME,
            		COL_AGE,
            		COL_BODY_HEIGHT,
            		COL_BODY_WEIGHT,
            		COL_AC,
            		COL_PC,
            		COL_NM,
            		COL_BHP,
            		COL_BLP,
            		COL_PULSE,
            		COL_UPLOADED,
            		COL_BMI,
            		COL_SEX,
            		COL_INPUT_TYPE
            		},
                    COL_DEVICE_TYPE+" = '" + Constant.BIODATA_DEVICE_TYPE_BLOOD_PRESSURE 
                    + "' and " + COL_USER_ID + " = '" + userId + "'"+" and "+COL_DEVICE_TIME+" >= '"+beginDate+"' and +"+COL_DEVICE_TIME+" <= '"+endDate+"' ", null, null, null,  COL_DEVICE_TIME +" DESC");
            int num = cursor.getCount();
            cursor.moveToFirst();
            for (int i = 0; i < num; i++) {
            	BioData b= new BioData();
            	b.set_id(cursor.getString(0));
            	b.setUserId(cursor.getString(1));
            	b.setDeviceSn(cursor.getString(2));
            	b.setDeviceTime(cursor.getString(3));
            	b.setDeviceId(cursor.getString(4));
            	b.setDeviceType(cursor.getString(5));
            	b.setReadTime(cursor.getString(6));
            	b.setAge(cursor.getString(7));
            	b.setBodyHeight(cursor.getString(8));
            	b.setBodyWeight(cursor.getString(9));
            	b.setAc(cursor.getString(10));
            	b.setPc(cursor.getString(11));
            	b.setNm(cursor.getString(12));
            	b.setBhp(cursor.getString(13));
            	b.setBlp(cursor.getString(14));
            	b.setPulse(cursor.getString(15));
            	b.setUploaded(cursor.getInt(16));
            	b.setBmi(cursor.getString(17));
            	b.setSex(cursor.getString(18));
            	b.setInputType(cursor.getString(19));
            	Log.d(TAG,"BioData=" + b.toString());
                results.add(b);
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
	public synchronized ArrayList<BioData> getAll(){
        ArrayList results = new ArrayList();
		SQLiteDatabase db=openDatabase();
        try {
            Cursor cursor = db.query(TABLE_BIODATA,
                    new String[]{
            		COL_ID,
            		COL_USER_ID,
            		COL_DEVICE_SN,
            		COL_DEVICE_TIME ,
            		COL_DEVICE_ID,
            		COL_DEVICE_TYPE,
            		COL_READ_TIME,
            		COL_AGE,
            		COL_BODY_HEIGHT,
            		COL_BODY_WEIGHT,
            		COL_AC,
            		COL_PC,
            		COL_NM,
            		COL_BHP,
            		COL_BLP,
            		COL_PULSE,
            		COL_UPLOADED,
            		COL_BMI,
            		COL_SEX,
            		COL_INPUT_TYPE
            		},
                    null, null, null, null, COL_DEVICE_TIME +" DESC");
            int num = cursor.getCount();
            cursor.moveToFirst();
            for (int i = 0; i < num; i++) {
            	BioData b= new BioData();
            	b.set_id(cursor.getString(0));
            	b.setUserId(cursor.getString(1));
            	b.setDeviceSn(cursor.getString(2));
            	b.setDeviceTime(cursor.getString(3));
            	b.setDeviceId(cursor.getString(4));
            	b.setDeviceType(cursor.getString(5));
            	b.setReadTime(cursor.getString(6));
            	b.setAge(cursor.getString(7));
            	b.setBodyHeight(cursor.getString(8));
            	b.setBodyWeight(cursor.getString(9));
            	b.setAc(cursor.getString(10));
            	b.setPc(cursor.getString(11));
            	b.setNm(cursor.getString(12));
            	b.setBhp(cursor.getString(13));
            	b.setBlp(cursor.getString(14));
            	b.setPulse(cursor.getString(15));
            	b.setUploaded(cursor.getInt(16));
            	b.setBmi(cursor.getString(17));
            	b.setSex(cursor.getString(18));
            	b.setInputType(cursor.getString(19));
            	Log.d(TAG,"BioData=" + b.toString());
                results.add(b);
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
	
	//2012.10.19 by Neo
	/**查詢Uploaded = 0 的生理資訊*/
	public synchronized ArrayList<BioData> getUploaded(){
        ArrayList results = new ArrayList();
		SQLiteDatabase db=openDatabase();
	    try {
	    	Cursor cursor = db.query(TABLE_BIODATA,
    			new String[]{
    			COL_ID,
            	COL_USER_ID,
            	COL_DEVICE_SN,
            	COL_DEVICE_TIME ,
            	COL_DEVICE_ID,
            	COL_DEVICE_TYPE,
            	COL_READ_TIME,
            	COL_AGE,
            	COL_BODY_HEIGHT,
            	COL_BODY_WEIGHT,
            	COL_AC,
            	COL_PC,
            	COL_NM,
            	COL_BHP,
            	COL_BLP,
            	COL_PULSE,
            	COL_UPLOADED,
            	COL_BMI,
            	COL_SEX,
            	COL_INPUT_TYPE
            	},
            	COL_UPLOADED+"='" + Constant.DATA_IS_NOT_UPLOAD + "'", null, null, null, COL_DEVICE_TIME +" DESC");
            int num = cursor.getCount();
            cursor.moveToFirst();
            for (int i = 0; i < num; i++) {
            	BioData b= new BioData();
            	b.set_id(cursor.getString(0));
            	b.setUserId(cursor.getString(1));
            	b.setDeviceSn(cursor.getString(2));
            	b.setDeviceTime(cursor.getString(3));
            	b.setDeviceId(cursor.getString(4));
            	b.setDeviceType(cursor.getString(5));
            	b.setReadTime(cursor.getString(6));
            	b.setAge(cursor.getString(7));
            	b.setBodyHeight(cursor.getString(8));
            	b.setBodyWeight(cursor.getString(9));
            	b.setAc(cursor.getString(10));
            	b.setPc(cursor.getString(11));
            	b.setNm(cursor.getString(12));
            	b.setBhp(cursor.getString(13));
            	b.setBlp(cursor.getString(14));
            	b.setPulse(cursor.getString(15));
            	b.setUploaded(cursor.getInt(16));
            	b.setBmi(cursor.getString(17));
            	b.setSex(cursor.getString(18));
            	b.setInputType(cursor.getString(19));
            	Log.d(TAG,"BioData=" + b.toString());
                results.add(b);
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
	
	//2012.10.22 by Neo
	/**更新Uploaded為 1 */
	public synchronized void updataUploaded(ArrayList<BioData> listBioData ){			
		SQLiteDatabase db=openDatabase();
		ContentValues args = new ContentValues();
		
		for(BioData bioData : listBioData){
			
			args.put(COL_UPLOADED,Constant.DATA_ALREADY_UPLOAD);
			db.update(TABLE_BIODATA, args, COL_ID+"=\'" + bioData.get_id() +"\'", null);
		}
	}
}