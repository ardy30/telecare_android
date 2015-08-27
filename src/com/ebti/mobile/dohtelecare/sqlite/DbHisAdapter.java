package com.ebti.mobile.dohtelecare.sqlite;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import com.ebti.mobile.dohtelecare.R;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DbHisAdapter {
	private static final Object LOCK = new Object();
	private static final String TAG="DbAdapter";
    private Context mCtx = null;
    //private SQLite sqlLite;
    //private SQLiteDatabase db;
	// 得到SD卡路径
	private final String DATABASE_PATH = android.os.Environment.getExternalStorageDirectory().getAbsolutePath()+ "/DohTeleCare";
	
	private final String NO_SDCARD_DATABASE_PATH = "/data/data/com.ebti.mobile.dohtelecare/databases/";
	//private final Activity activity;
	private final String DATABASE_FILENAME;
	
    public  DbHisAdapter(Context ctx) {
        this.mCtx = ctx; 
		DATABASE_FILENAME = "hisData.db";
        //sqlLite = new SQLite(ctx);
    }

//    public DbAdapter open() throws SQLException {
//        try {
//        	//Log.i(TAG,"Open Database Connection");
//        	//sqlLite = new SQLite(mCtx);            
//        } catch (Exception e) {
//        	Log.e(TAG,"Exception:" + e.getMessage());
//        }
//        return this;
//    }
    
    
	// 得到操作数据库的对象
	public SQLiteDatabase openDatabase()
	{
		synchronized (LOCK) {
			if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
				//Log.i(TAG, "Has SDCard");
				try
				{
					boolean b = false;
					// 得到数据库的完整路径名
					String databaseFilename = DATABASE_PATH + "/" + DATABASE_FILENAME;
					//Log.i(TAG, "databaseFilename : " + databaseFilename);
					// 将数据库文件从资源文件放到合适地方（资源文件也就是数据库文件放在项目的res下的raw目录中）
					// 将数据库文件复制到SD卡中 
					File dir = new File(DATABASE_PATH);
					if (!dir.exists()){
						//Log.i(TAG,"MakeDir=" + dir.getAbsolutePath());
						b = dir.mkdir();
					}
					// 判断是否存在该文件
					if (!(new File(databaseFilename)).exists())
					{
						//Log.i(TAG,"Database file=" + databaseFilename);
						// 不存在得到数据库输入流对象
						InputStream is = mCtx.getResources().openRawResource(R.raw.personal);
						// 创建输出流
						FileOutputStream fos = new FileOutputStream(databaseFilename);
						// 将数据输出
						byte[] buffer = new byte[8192];
						int count = 0;
						while ((count = is.read(buffer)) > 0)
						{
							fos.write(buffer, 0, count);
						}
						// 关闭资源
						fos.close();
						is.close();
					}
					// 得到SQLDatabase对象
					SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(databaseFilename, null);
					return database;
				}catch (Exception e){
					Log.e(TAG, "openDatabase() Exception : " + e);
					System.out.println(e.getMessage());
				}
			}else{
				//Log.i(TAG, "No SDCard");
				try
				{
					boolean b = false;
					// 得到数据库的完整路径名
					String databaseFilename = NO_SDCARD_DATABASE_PATH + DATABASE_FILENAME;
					//Log.i(TAG, "databaseFilename : " + databaseFilename);
					// 将数据库文件从资源文件放到合适地方（资源文件也就是数据库文件放在项目的res下的raw目录中）
					// 将数据库文件复制到手機裡 
					File dir = new File(NO_SDCARD_DATABASE_PATH);
					if (!dir.exists()){
						//Log.i(TAG,"MakeDir=" + dir.getAbsolutePath());
						b = dir.mkdir();
					}
					// 判断是否存在该文件
					if (!(new File(databaseFilename)).exists())
					{
						//Log.i(TAG,"Database file=" + databaseFilename);
						// 不存在得到数据库输入流对象
						InputStream is = mCtx.getResources().openRawResource(R.raw.personal);
						// 创建输出流
						FileOutputStream fos = new FileOutputStream(databaseFilename);
						// 将数据输出
						byte[] buffer = new byte[8192];
						int count = 0;
						while ((count = is.read(buffer)) > 0)
						{
							fos.write(buffer, 0, count);
						}
						// 关闭资源
						fos.close();
						is.close();
					}
					// 得到SQLDatabase对象
					SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(databaseFilename, null);
					return database;
				}catch (Exception e){
					Log.e(TAG, "openDatabase() Exception : " + e);
					System.out.println(e.getMessage());
				}
			}
		}
		
		return null;
	}
}
