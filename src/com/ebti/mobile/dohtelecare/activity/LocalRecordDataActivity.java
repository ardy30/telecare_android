package com.ebti.mobile.dohtelecare.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.ebti.mobile.dohtelecare.R;
import com.ebti.mobile.dohtelecare.constant.Constant;
import com.ebti.mobile.dohtelecare.model.BioData;
import com.ebti.mobile.dohtelecare.sqlite.BioDataAdapter;
import com.ebti.mobile.dohtelecare.sqlite.UserAdapter;
import com.ebti.mobile.dohtelecare.util.LocalRecorderAdapter;
import com.ebti.mobile.dohtelecare.util.ScreenManager;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LocalRecordDataActivity extends Activity {
	public static final String TAG = "LocalRecordDataActivity";
	
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
	SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.S");
	
	String beginDateStr = "";
	String endDateStr= "";
	
	private static ConnectivityManager connMgr;

	//確認網路狀態
	public static boolean isMobileNetworkAvailable(Context con){
		if(null == connMgr){
			connMgr = (ConnectivityManager)con.getSystemService(Context.CONNECTIVITY_SERVICE);
		}
		NetworkInfo wifiInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo mobileInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if(wifiInfo!=null && wifiInfo.isAvailable()){
			return true;
		}else if(mobileInfo!=null && mobileInfo.isAvailable()){
			return true;
		}else{
			return false;
		}
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.serverrecorddatalist);
        
		//放入Activity Stack
		ScreenManager.getScreenManager().pushActivity(this);
  		
  		//==========================common=========================
			
		//登出
		((Button) findViewById(R.id.logoutbutton)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.i(TAG, "logoutbutton onClick()");
				//清除資料
				UserAdapter userAdapter = new UserAdapter(getApplicationContext());
				userAdapter.delAllUser();
				
				LoginActivity.instance.finish();

			    ScreenManager.getScreenManager().popAllActivityExceptOne(LocalRecordDataActivity.class);
				ScreenManager.getScreenManager().popActivity();
				
				Intent intent = new Intent(LocalRecordDataActivity.this, LoginActivity.class);
				startActivity(intent);
			}
		});
		
		//返回
		((Button) findViewById(R.id.backbutton)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.i(TAG, "backbutton onClick()");
//				((Button) findViewById(R.id.backbutton)).setBackgroundColor(getResources().getColor(R.color.click_color));
				ScreenManager.getScreenManager().popAllActivityExceptOne(LocalRecordDataActivity.class);
				ScreenManager.getScreenManager().popActivity();
			}
		});
		
		//====================common End===========================
		//隱藏
		((Button)findViewById(R.id.anotherData)).setVisibility(4);
		
		//Head Text
		TextView item3 = (TextView) findViewById(R.id.item3);
		item3.setText(R.string.upload_status);
		
		//趨勢圖Button隱藏
		Button googlechartbutton = (Button) findViewById(R.id.googlechartbutton);
		googlechartbutton.setVisibility(4);//不可見
		
	    Bundle bundle = this.getIntent().getExtras();
	    final String dataType = bundle.getString("dataType");
	    beginDateStr = bundle.getString("beginDateStr");
	    endDateStr = bundle.getString("endDateStr");
	    Date beginDate = null;
	    Date endDate= null ;
	    try {
			beginDate = dateFormat.parse(beginDateStr);
			endDate = dateFormat.parse(endDateStr);
			
			Date queryBeginDate = setBeginDate(beginDate);
			Date queryEndDate = setEndDate(endDate);
			
			beginDateStr = dateTimeFormat.format(queryBeginDate);
			endDateStr = dateTimeFormat.format(queryEndDate);
			Log.i(TAG, "beginDateStr = "+beginDateStr);
		    Log.i(TAG, "endDateStr = "+endDateStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    
	    //背景設定
	    //RelativeLayout relativeLayout2 = (RelativeLayout) findViewById(R.id.relativeLayout2);
	    Button anotherData = (Button) findViewById(R.id.anotherData);
	    TextView title = (TextView) findViewById(R.id.title);
	    if(dataType.equals("BP")){
	    //	relativeLayout2.setBackgroundDrawable(getResources().getDrawable(R.drawable.blood_pressure_record));
	    	title.setText(getString(R.string.bloodpress_title));
	    	anotherData.setBackgroundDrawable(getResources().getDrawable(R.drawable.serverrecordatalist_blood_sugar_dynamic));
	    }else{
	    //	relativeLayout2.setBackgroundDrawable(getResources().getDrawable(R.drawable.blood_glucose_record));
	    	title.setText(getString(R.string.bloodsugar_title));
	    	anotherData.setBackgroundDrawable(getResources().getDrawable(R.drawable.serverrecordatalist_blood_pressure_dynamic));
	    }
	    
	    //另一個資料
	    ((Button) findViewById(R.id.anotherData)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.i(TAG, "anotherData onClick()");
				Intent intent = new Intent(LocalRecordDataActivity.this, LocalRecordDataActivity.class);
				Bundle bundle = new Bundle();
				if(dataType.equals("BP")){
					bundle.putString("dataType", "BG");
				}else{
					bundle.putString("dataType", "BP");
				}
				intent.putExtras(bundle);
				startActivity(intent);
				ScreenManager.getScreenManager().popActivity();
			}
		});
	    
	    UserAdapter userAdapter = new UserAdapter(getApplicationContext());
		com.ebti.mobile.dohtelecare.model.User user = userAdapter.getUIDUnitType();
		//ListView
		ListView lv= (ListView)findViewById(R.id.recordListView);

        // create the grid item mapping
        String[] from = new String[] {"col_1", "col_2"};
        int[] to = new int[] { R.id.item2, R.id.item3};

        // prepare the list of all records
        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
        //自BioData取得資料
        BioDataAdapter bioDataAdapter = new BioDataAdapter(getApplicationContext());
	    if(dataType.equals("BG")){
	    	//血糖
	    	
	    	ArrayList<BioData> listBioData;
	    	
	    	if(beginDate!=null && endDate!=null){
	    		listBioData = bioDataAdapter.getUserBloodGlucose(user.getUid(),beginDateStr,endDateStr);
	    	}else{
	    		listBioData = bioDataAdapter.getUserBloodGlucose(user.getUid());
	    	}
	        
	        Log.i(TAG, "listBioData size : " + listBioData.size());
	        if(listBioData.size() > 0){
		        for(BioData bioData : listBioData){
		        	HashMap<String, String> map = new HashMap<String, String>();
		        	String recordData = bioData.getDeviceTime();
		        	
		        	if(bioData.getAc()!=null){
		        		recordData += "\n飯前血糖:" + bioData.getAc();
		        	}else if(bioData.getPc()!=null){
		        		recordData += "\n飯後血糖:" + bioData.getPc();
		        	}else{
		        		recordData += "\n隨機血糖:" + bioData.getNm();
		        	}
		        	map.put("col_1", recordData);
		        	if(bioData.getUploaded()==1){
		        		map.put("col_2", "已上傳");
		        	}else{
		        		map.put("col_2", "未上傳");
		        	}
		        	fillMaps.add(map);
		        }
	        }else{
	        	((LinearLayout)findViewById(R.id.linearLayout3)).setVisibility(8);
	        	((TextView)findViewById(R.id.nodatashow)).setText(R.string.no_data);
	        }
	    }else{
	    	//血壓
	    	ArrayList<BioData> listBioData ;
	    	if(beginDate!=null && endDate!=null){
	    		listBioData = bioDataAdapter.getUserBloodPressure(user.getUid(),beginDateStr,endDateStr);
	    	}else{
	    		listBioData = bioDataAdapter.getUserBloodPressure(user.getUid());
	    	}
	    	
	    	Log.i(TAG, "listBioData size : " + listBioData.size());
	        if(listBioData.size() > 0){
		        for(BioData bioData : listBioData){
		        	HashMap<String, String> map = new HashMap<String, String>();
		        	String recordData = bioData.getDeviceTime() + "\n" + "收縮壓:" + bioData.getBhp() + ",舒張壓:" + bioData.getBlp() + "\n脈搏:" + bioData.getPulse();
		        	map.put("col_1", recordData);
		        	if(bioData.getUploaded()==1){
		        		map.put("col_2", "已上傳");
		        	}else{
		        		map.put("col_2", "未上傳");
		        	}
		        	fillMaps.add(map);
		        }
	        }else{
	        	((LinearLayout)findViewById(R.id.linearLayout3)).setVisibility(8);
	        	((TextView)findViewById(R.id.nodatashow)).setText(R.string.no_data);
	        }
	    }
	    // fill in the grid_item layout
        LocalRecorderAdapter adapter = new LocalRecorderAdapter(this, fillMaps, R.layout.bluetoothrecord_datalist, from, to);
        lv.setAdapter(adapter);
    }

    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.i(TAG, "onActivityResult(), requestCode : " + requestCode + ", resultCode : " + resultCode);
		if ((requestCode == Constant.REQUEST_CODE_SUCCESS) && (resultCode == RESULT_OK)) {
			Bundle bundle = data.getExtras();
			getMessageDialog("通知", bundle.getString(Constant.DELIVER_LOGIN_MESSAGE)).show();
		}
	}
    
    private AlertDialog getMessageDialog(String title, String message){
        //產生一個Builder物件
        Builder builder = new AlertDialog.Builder(this);
        //設定Dialog的標題
        builder.setIcon(R.drawable.alert_icon);
        builder.setTitle(title);
        //設定Dialog的內容
        builder.setMessage(message);
        //設定Positive按鈕資料
        builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //按下按鈕時顯示快顯
            }
        });
        //利用Builder物件建立AlertDialog
        return builder.create();
    }
    
    private Date setBeginDate(Date beginDate){
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(beginDate);
    	calendar.set(Calendar.HOUR_OF_DAY, 0);
    	calendar.set(Calendar.MINUTE, 0);
    	calendar.set(Calendar.SECOND,0);
    	calendar.set(Calendar.MILLISECOND, 0);  
    	return calendar.getTime();
    }
    
    private Date setEndDate(Date endDate){
    	Log.i(TAG, "endDate = "+endDate);
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(endDate);
    	Log.i(TAG, "calendar.getTime() = "+calendar.getTime());
    	calendar.set(Calendar.HOUR_OF_DAY, 23);
    	calendar.set(Calendar.MINUTE, 59);
    	calendar.set(Calendar.SECOND,59);
    	calendar.set(Calendar.MILLISECOND, 999);    
    	Log.i(TAG, "calendar.getTime() = "+calendar.getTime());
    	return calendar.getTime();
    }
}
