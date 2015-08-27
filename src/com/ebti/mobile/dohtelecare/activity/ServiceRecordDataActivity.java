package com.ebti.mobile.dohtelecare.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.ebti.mobile.dohtelecare.R;
import com.ebti.mobile.dohtelecare.constant.Constant;
import com.ebti.mobile.dohtelecare.model.HisData;
import com.ebti.mobile.dohtelecare.sqlite.HisDataAdapter;
import com.ebti.mobile.dohtelecare.sqlite.UserAdapter;
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
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ServiceRecordDataActivity extends Activity {
	public static final String TAG = "ServiceRecordDataActivity";
	
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
		}else if(mobileInfo!= null && mobileInfo.isAvailable()){
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

			    ScreenManager.getScreenManager().popAllActivityExceptOne(ServiceRecordDataActivity.class);
				ScreenManager.getScreenManager().popActivity();
				
				Intent intent = new Intent(ServiceRecordDataActivity.this, LoginActivity.class);
				startActivity(intent);
			}
		});
		
		//返回
		((Button) findViewById(R.id.backbutton)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.i(TAG, "backbutton onClick()");
//				((Button) findViewById(R.id.backbutton)).setBackgroundColor(getResources().getColor(R.color.click_color));
				ScreenManager.getScreenManager().popAllActivityExceptOne(ServiceRecordDataActivity.class);
				ScreenManager.getScreenManager().popActivity();
			}
		});
		
		//====================common End===========================
	    
		//Head Text
		TextView item3 = (TextView) findViewById(R.id.item3);
		item3.setText(R.string.server_record_devicename);
		
	    Bundle bundle = this.getIntent().getExtras();
	    final String dataType = bundle.getString("dataType");
	    final String beginDate = bundle.get("beginDate").toString();
	    final String endDate = bundle.get("endDate").toString();
	    Log.i(TAG, "beginDate : " + beginDate + ", endDate : " + endDate);
	    
	    //背景設定
	   // RelativeLayout relativeLayout2 = (RelativeLayout) findViewById(R.id.relativeLayout2);
	    //按鈕設定
	    Button anotherData = (Button) findViewById(R.id.anotherData);
	    TextView title = (TextView) findViewById(R.id.title);
	    if(dataType.equals("BP")){
	   // 	relativeLayout2.setBackgroundDrawable(getResources().getDrawable(R.drawable.blood_pressure_record));
	    	title.setText(getString(R.string.bloodpress_title));
	    	anotherData.setBackgroundDrawable(getResources().getDrawable(R.drawable.serverrecordatalist_blood_sugar_dynamic));
	    }else{
	    	//relativeLayout2.setBackgroundDrawable(getResources().getDrawable(R.drawable.blood_glucose_record));
	     	
	    	title.setText(getString(R.string.bloodsugar_title));
	    	anotherData.setBackgroundDrawable(getResources().getDrawable(R.drawable.serverrecordatalist_blood_pressure_dynamic));
	    }
	    
	    //另一個資料
	    anotherData.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.i(TAG, "anotherData onClick()");
				Intent intent = new Intent(ServiceRecordDataActivity.this, ServiceRecordDataActivity.class);
				Bundle bundle = new Bundle();
				if(dataType.equals("BP")){
					bundle.putString("dataType", "BG");
				}else{
					bundle.putString("dataType", "BP");
				}
				bundle.putString("beginDate", beginDate);
				bundle.putString("endDate", endDate);
				intent.putExtras(bundle);
				startActivity(intent);
				ScreenManager.getScreenManager().popActivity();
			}
		});
	    
	    //趨勢圖
	    ((Button) findViewById(R.id.googlechartbutton)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.i(TAG, "googlechartbutton onClick()");
				Intent intent = new Intent(ServiceRecordDataActivity.this, DrawGoogleChartActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("dataType", dataType);
				bundle.putString("beginDate", beginDate);
				bundle.putString("endDate", endDate);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	    
	    //隱藏本機資料注意事項
	    TextView textViewNotice = (TextView) findViewById(R.id.textViewNotice);
	    textViewNotice.setVisibility(4);
	    
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
        HisDataAdapter hisDataAdapter = new HisDataAdapter(getApplicationContext());
	    if(dataType.equals("BG")){
		    	//血糖
		    	List<HisData> listData = hisDataAdapter.getBloodGlucose();
		    	if( listData.size() > 0 )
		    	{
		    	
		        for(HisData hisData : listData){
		        	HashMap<String, String> map = new HashMap<String, String>();
		        	String deviceTime = hisData.getDeviceTime();
		        	
		        	String bgValues = "";
		        	if(hisData.getAc()!=null){
		        		bgValues = "\n飯前血糖:" + hisData.getAc();
		        	}else if(hisData.getPc()!=null){
		        		bgValues = "\n飯後血糖:" + hisData.getPc();
		        	}else if(hisData.getNm()!=null){
		        		bgValues = "\n隨機血糖:" + hisData.getNm();
		        	}
		        	map.put("col_1", deviceTime + bgValues);
		        	map.put("col_2", hisData.getInputType());
		        	if(bgValues.equals("")){
		        	}else{
		        		fillMaps.add(map);
		        	}
		        }
		    	}
		    	else
		    	{
		    		((LinearLayout)findViewById(R.id.linearLayout3)).setVisibility(8);
		        	((TextView)findViewById(R.id.nodatashow)).setText(R.string.no_data);
		    	}
	    }else{
	    	//血壓
	        List<HisData> listHisData = hisDataAdapter.getBloodPressure();
	        if( listHisData.size() > 0 )
		        {
		        //Log.i(TAG, "listBioData size : " + listBioData.size());
		        for(HisData hisData : listHisData){
		        	HashMap<String, String> map = new HashMap<String, String>();
		        	String deviceTime = hisData.getDeviceTime();
		        	map.put("col_1", deviceTime + "\n收縮壓:" + hisData.getBhp() + ",舒張壓:" + hisData.getBlp() + "\n脈搏:" + hisData.getPulse());
		        	map.put("col_2", hisData.getInputType());
		        	fillMaps.add(map);
		        }
	        }
	        else
	        {
		        	((LinearLayout)findViewById(R.id.linearLayout3)).setVisibility(8);
		        	((TextView)findViewById(R.id.nodatashow)).setText(R.string.no_data);
	        }
	    }
	    // fill in the grid_item layout
	    SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.serverrecord_datalist, from, to);
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
	
}
