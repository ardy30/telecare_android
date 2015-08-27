package com.ebti.mobile.dohtelecare.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ebti.mobile.dohtelecare.R;
import com.ebti.mobile.dohtelecare.constant.Constant;
import com.ebti.mobile.dohtelecare.helper.NetService;
import com.ebti.mobile.dohtelecare.model.HisData;
import com.ebti.mobile.dohtelecare.model.User;
import com.ebti.mobile.dohtelecare.service.GetBlueToothDeviceDataService;
import com.ebti.mobile.dohtelecare.sqlite.HisDataAdapter;
import com.ebti.mobile.dohtelecare.sqlite.UserAdapter;
import com.ebti.mobile.dohtelecare.util.ScreenManager;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

public class QueryRecordDataActivity extends Activity {
	public static final String TAG = "QueryRecordDataActivity";

	ProgressDialog pd;
	
	public String beginDate = "";
	public String endDate = "";
	
	private int myYear, myMonth, myDay, myHour, myMinute;
	static final int BEGINDATE_DATEPICKER = 10;
	static final int ENDDATE_DATEPICKER = 11;
	static final int GET_SERVER_RECORDER = 12;
	
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
        setContentView(R.layout.queryrecorddata);
        
        //===================將LoginActivity之後的Activity 關閉=======================
		Log.i(TAG,"before popAllActivityExceptOne()");
		ScreenManager.getScreenManager().popAllActivityExceptOne(MainActivity.class);
		Log.i(TAG,"after popAllActivityExceptOne()");
		//=================將LoginActivity之後的Activity 關閉 End======================
		//放入Activity Stack
		ScreenManager.getScreenManager().pushActivity(this);
		
		//預設一天
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
      /*	
        //Information
  		TextView textViewUnit = (TextView)findViewById(R.id.userinformation);
  		StringBuilder userInformation = new StringBuilder();
  		UserAdapter userAdapter = new UserAdapter(getApplicationContext());
  		com.ebti.mobile.dohtelecare.model.User user = userAdapter.getUIDUnitType();
  		userInformation.append("帳號：" + user.getUid() + "\n");
  		if(user.getType().equals("Trial")){
  			userInformation.append(((String)getResources().getText(R.string.status_trial_user)));
  		}else{
  			userInformation.append(((String)getResources().getText(R.string.status_trsc_user)) + "(" + user.getUnit() + ")");
  		}
  		//set Information
  		textViewUnit.setText(userInformation.toString());
  		*/
  		//==========================common=========================
  	
		
		//返回
		((Button) findViewById(R.id.backbutton)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.i(TAG, "backbutton onClick()");
//				((Button) findViewById(R.id.backbutton)).setBackgroundColor(getResources().getColor(R.color.click_color));
				ScreenManager.getScreenManager().popAllActivityExceptOne(QueryRecordDataActivity.class);
				ScreenManager.getScreenManager().popActivity();
			}
		});
		
		//登出
		((Button) findViewById(R.id.logoutbutton)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.i(TAG, "logoutbutton onClick()");
				//清除資料
				UserAdapter userAdapter = new UserAdapter(getApplicationContext());
				userAdapter.delAllUser();
				
				LoginActivity.instance.finish();

			    ScreenManager.getScreenManager().popAllActivityExceptOne(QueryRecordDataActivity.class);
				ScreenManager.getScreenManager().popActivity();
				
				Intent intent = new Intent(QueryRecordDataActivity.this, LoginActivity.class);
				startActivity(intent);
				
				//Intent intent = new Intent(BloodPressureActivity.this, OptionActivity.class);
				//startActivity(intent);
			}
		});

		/*
		//關於本軟體
  		((Button) findViewById(R.id.aboutappbutton)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.i(TAG, "aboutappbutton onClick()");
				Intent intent = new Intent(QueryRecordDataActivity.this, AboutAppActivity.class);
				startActivity(intent);
			}
		});
		
		//修改密碼
  		((Button) findViewById(R.id.changepasswordbutton)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.i(TAG, "changepasswordbutton onClick()");
				Intent intent = new Intent(QueryRecordDataActivity.this, ModifyPasswordActivity.class);
				startActivityForResult(intent, 0);
			}
		});
		*/
		//====================common End===========================
	    
	    //起始日期edittext
  		EditText editTextBeginDate = (EditText)findViewById(R.id.editTextBeginDate);
  		Calendar c = Calendar.getInstance();
  		c.add(Calendar.DAY_OF_YEAR, -1);
  		
  		beginDate = sdf.format(c.getTime());
  		editTextBeginDate.setText(beginDate);

  		editTextBeginDate.setOnTouchListener(new View.OnTouchListener() {
  			@Override
  			public boolean onTouch(View v, MotionEvent event) {
  				Log.i(TAG, "editTextBeginDate onTouch");
  				((EditText)findViewById(R.id.editTextBeginDate)).setInputType(InputType.TYPE_NULL); // 關閉軟鍵盤
  				final Calendar c = Calendar.getInstance();
  				myYear = c.get(Calendar.YEAR);
  				myMonth = c.get(Calendar.MONTH);
  				myDay = c.get(Calendar.DAY_OF_MONTH);
  				showDialog(BEGINDATE_DATEPICKER);
  				return false;
  			}
  			
  		});
  		
  		//結束日期edittext
  		EditText editTextEndDate = (EditText)findViewById(R.id.editTextEndDate);

  		endDate = sdf.format(new Date());
  		editTextEndDate.setText(endDate);
  		
  		editTextEndDate.setOnTouchListener(new View.OnTouchListener() {
  			@Override
  			public boolean onTouch(View v, MotionEvent event) {
  				Log.i(TAG, "editTextEndDate onTouch");
  				((EditText)findViewById(R.id.editTextEndDate)).setInputType(InputType.TYPE_NULL); // 關閉軟鍵盤
  				final Calendar c = Calendar.getInstance();
  				myYear = c.get(Calendar.YEAR);
  				myMonth = c.get(Calendar.MONTH);
  				myDay = c.get(Calendar.DAY_OF_MONTH);
  				showDialog(ENDDATE_DATEPICKER);
  				return false;
  			}
  		});
  		
  		
  		
  		//雲端血糖
  		((Button)findViewById(R.id.serverBloodGlucoseData)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.i(TAG, "queryserverrecordbtn onClick()");
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
				EditText editTextBeginDate = (EditText)findViewById(R.id.editTextBeginDate);
				EditText editTextWndDate = (EditText)findViewById(R.id.editTextEndDate);
				String beginDateStr = editTextBeginDate.getText().toString().trim();
				String endDateStr = editTextWndDate.getText().toString().trim();
				try {
					Date beginDate = sdf.parse(beginDateStr);
					Date endDate = sdf.parse(endDateStr);
					Date now = new Date();
					if(isMobileNetworkAvailable(getApplicationContext())){
						if(((EditText)findViewById(R.id.editTextBeginDate)).getText().toString().trim().equals("")){
							getMessageDialog("警告", "請輸入起始日期！").show();
						}else if(((EditText)findViewById(R.id.editTextEndDate)).getText().toString().trim().equals("")){
							getMessageDialog("警告", "請輸入結束日期！").show();
						}else if(beginDate.after(endDate) ){
							getMessageDialog("警告", "起始日期不可大於結束日期！").show();
						}else if(beginDate.after(now) ){
							getMessageDialog("警告", "查詢起始日期不可大於今天！").show();
							editTextBeginDate.setText(sdf.format(new Date()));
						}else if(endDate.after(now) ){
							getMessageDialog("警告", "查詢結束日期不可大於今天！").show();
							editTextWndDate.setText(sdf.format(new Date()));
						}else{
							//停止藍芽設備自動讀取, 資料自動上傳
//					  		boolean isStartBlueTooth = MainActivity.isServiceRunning(getApplicationContext(), GetBlueToothDeviceDataService.class.getName());
//						    if(isStartBlueTooth){
//						    	Intent inetnt= new Intent(QueryRecordDataActivity.this, GetBlueToothDeviceDataService.class);
//							    stopService(inetnt);
//						    }
							showDialog(GET_SERVER_RECORDER);
							new Thread(){
					        	@Override
					            public void run() {
					        		Log.i(TAG, "Thread run");
									getServerData("BG", null, null);
									try {
					        			Log.i(TAG, "sleep");
										sleep(5000);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
										Log.e(TAG, "InterruptedException : " + e);
									}
					        		handler.sendEmptyMessage(99);

								}
							}.start();
						}
					}else{
						getMessageDialog("警告", "無法連上網路，請確認您的網路狀態！").show();
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
  		
  		//雲端血壓
  		((Button)findViewById(R.id.serverBloodPressureData)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.i(TAG, "queryserverrecordbtn onClick()");
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
				EditText editTextBeginDate = (EditText)findViewById(R.id.editTextBeginDate);
				EditText editTextEndDate = (EditText)findViewById(R.id.editTextEndDate);
				String beginDateStr = ((EditText)findViewById(R.id.editTextBeginDate)).getText().toString().trim();
				String endDateStr = ((EditText)findViewById(R.id.editTextEndDate)).getText().toString().trim();
				try {
					Date beginDate = sdf.parse(beginDateStr);
					Date endDate = sdf.parse(endDateStr);
					Date now = new Date();
					if(isMobileNetworkAvailable(getApplicationContext())){
						if(((EditText)findViewById(R.id.editTextBeginDate)).getText().toString().trim().equals("")){
							getMessageDialog("警告", "請輸入起始日期！").show();
						}else if(((EditText)findViewById(R.id.editTextEndDate)).getText().toString().trim().equals("")){
							getMessageDialog("警告", "請輸入結束日期！").show();
						}else if(beginDate.after(endDate) ){
							getMessageDialog("警告", "起始日期不可大於結束日期！").show();
						}else if(beginDate.after(now) ){
							getMessageDialog("警告", "查詢起始日期不可大於今天！").show();
							editTextBeginDate.setText(sdf.format(new Date()));
						}else if(endDate.after(now) ){
							getMessageDialog("警告", "查詢結束日期不可大於今天！").show();
							editTextEndDate.setText(sdf.format(new Date()));
						}else{
							showDialog(GET_SERVER_RECORDER);
							new Thread(){
					        	@Override
					            public void run() {
					        		Log.i(TAG, "Thread run");
									getServerData("BP", null, null);
								}
							}.start();
						}
					}else{
						getMessageDialog("警告", "無法連上網路，請確認您的網路狀態！").show();
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
  		
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
    
    @Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		switch(id){
			case BEGINDATE_DATEPICKER:
				Log.i(TAG, "open BEGINDATE_DATEPICKER");
				//Toast.makeText(ServerRecordQueryDateActivity.this, "- onCreateDialog(ID_DATEPICKER) -", Toast.LENGTH_LONG).show();
				return new DatePickerDialog(this, beginDateSetListener, myYear, myMonth, myDay);
			case ENDDATE_DATEPICKER:
				Log.i(TAG, "open ENDDATE_DATEPICKER");
				//Toast.makeText(ServerRecordQueryDateActivity.this, "- onCreateDialog(ID_DATEPICKER) -", Toast.LENGTH_LONG).show();
				return new DatePickerDialog(this, endDateSetListener, myYear, myMonth, myDay);
			case GET_SERVER_RECORDER:
				Log.i(TAG, "open GET_SERVER_RECORDER");
				pd = ProgressDialog.show(this, "讀取中....", "資料讀取中,需要較長時間等候.請稍待!!", true, false);
				return pd;
			default:
				return new Dialog(getApplicationContext());
		}
	}
	
	private DatePickerDialog.OnDateSetListener beginDateSetListener = new DatePickerDialog.OnDateSetListener(){
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			// TODO Auto-generated method stub
			EditText editTextBeginDate = (EditText)findViewById(R.id.editTextBeginDate);
			int month = monthOfYear + 1;
			String dateformat = year + "/" + (month>9?month:"0"+month) + "/" + (dayOfMonth>9?dayOfMonth:"0"+dayOfMonth);
			editTextBeginDate.setText(dateformat);
			beginDate = dateformat;
		}
	};
	
	private DatePickerDialog.OnDateSetListener endDateSetListener = new DatePickerDialog.OnDateSetListener(){
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			// TODO Auto-generated method stub
			EditText editTextEndDate = (EditText)findViewById(R.id.editTextEndDate);
			int month = monthOfYear + 1;
			String dateformat = year + "/" + (month>9?month:"0"+month) + "/" + (dayOfMonth>9?dayOfMonth:"0"+dayOfMonth);
			editTextEndDate.setText(dateformat);
			endDate = dateformat;
		}
	};
	
	public void getServerData(String dataType, String beginDateStr, String endDateStr){
		Log.i(TAG, "getServerData()");
		UserAdapter userAdapter = new UserAdapter(getApplicationContext());
		User user = userAdapter.getUserUIdAndPassword();
		NetService netService = new NetService();
		EditText editTextBeginDate = (EditText)findViewById(R.id.editTextBeginDate);
		EditText editTextEndDate = (EditText)findViewById(R.id.editTextEndDate);
		String response = null;
		if(beginDateStr==null && endDateStr==null){
			response = netService.CallGetVitalSign(user, editTextBeginDate.getText().toString(), editTextEndDate.getText().toString());
		}else{
			beginDate = beginDateStr;
			endDate = endDateStr;
			response = netService.CallGetVitalSign(user, beginDateStr, endDateStr);
		}
		Message msg = new Message();
		if(response!=null){
			try {
				JSONObject sonResponse = new JSONObject(response);
				String messageCode = sonResponse.getString("Message");
				
				if(messageCode.equals("A01")){
					HisDataAdapter hisDataAdapter = new HisDataAdapter(getApplicationContext());
					hisDataAdapter.delAll();
					String MemberID = sonResponse.getString("MemberID");
					JSONArray jsonArrayVitalSign = sonResponse.getJSONArray("VitalSign");
					Log.i(TAG, "jsonArrayVitalSign length : " + jsonArrayVitalSign.length());
					for(int i=0;i<jsonArrayVitalSign.length();i++){
						String recordType = jsonArrayVitalSign.getJSONObject(i).get("Type").toString();
						String recordMTime = jsonArrayVitalSign.getJSONObject(i).get("MTime").toString();
						String recordInputType = jsonArrayVitalSign.getJSONObject(i).get("InputType").toString();
						HisData hisData = new HisData();
						hisData.set_id(recordMTime + recordType);
						//recordMTime = recordMTime.replace(" ", "\n");
						hisData.setDeviceTime(recordMTime);
						hisData.setUserId(MemberID);
						if(recordInputType.equals("Manual")){
							hisData.setInputType("手動\n輸入");
						}else{
							hisData.setInputType("儀器\n輸入");
						}
						String recordValues = jsonArrayVitalSign.getJSONObject(i).get("Values").toString();
						recordValues = recordValues.replace("[", "");
						recordValues = recordValues.replace("]", "");
						if(recordType.equals("BP")){
							//血壓
							String[] bpData = recordValues.split(",");
							hisData.setBhp(bpData[0]);
							hisData.setBlp(bpData[1]);
							hisData.setPulse(bpData[2]);
							hisDataAdapter.createBloodPressure(hisData);
							
						}else if(recordType.equals("BG")){
							//血糖
							String recordMark = jsonArrayVitalSign.getJSONObject(i).get("Mark").toString();
							if(recordMark.equals("AC")){
								hisData.setAc(recordValues);
							}else if(recordMark.equals("PC")){
								hisData.setPc(recordValues);
							}else{
								hisData.setNm(recordValues);
							}
							hisDataAdapter.createGlucose(hisData);
						}
					}
					msg.what = 0;
					msg.obj = dataType;
					handler.sendMessage(msg);
					
					//Intent intent = new Intent(LoginActivity.this, PhysiologicalMeasureActivity.class);
					//startActivity(intent);
				}else{
					msg.what = 1;
					msg.obj = messageCode;
					handler.sendMessage(msg);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e(TAG, "JSONException : " + e);
				if(e.toString().indexOf("Timeout")>=0){
					handler.sendEmptyMessage(2);
				}else{
					msg.what = 3;
					msg.obj = e;
					handler.sendMessage(msg);
				}
			}
		}
	}
	
	/** 
	  * 用Handler 更新UI 
	  */  
	private Handler handler = new Handler(){
		@Override  
	    public void handleMessage(Message msg) {
			Log.i(TAG, "msg : " + msg.toString());
			Bundle bundle = new Bundle();
			bundle.putString("beginDate", beginDate);
			bundle.putString("endDate", endDate);
	    	if (msg.what==0){
	    		Log.i(TAG, "msg.what==0, success");
	    		pd.dismiss();
	    		
//	    		boolean isStartBlueTooth = MainActivity.isServiceRunning(getApplicationContext(), GetBlueToothDeviceDataService.class.getName());
//			    if(!isStartBlueTooth){
//			    	Intent inetnt= new Intent(QueryRecordDataActivity.this, GetBlueToothDeviceDataService.class);
//				    startService(inetnt);
//			    }
	    		Intent intent = new Intent(QueryRecordDataActivity.this, ServiceRecordDataActivity.class);
	    		if(msg.obj.equals("BP")){
	    			bundle.putString("dataType", "BP");
	    		}else{
	    			bundle.putString("dataType", "BG");
	    		}
	    		intent.putExtras(bundle);
    			startActivity(intent);
	    	}
	    	if(msg.what==1){
	    		pd.dismiss();
	    		Log.i(TAG, "msg.what==1, msg.obj : " + msg.obj);
	    		if(msg.obj.equals("E01")){
	    			getMessageDialog("警告", "帳號不存在！").show();
	    		}else if(msg.obj.equals("E02")){
	    			getMessageDialog("警告", "密碼錯誤，身分驗證失敗！").show();
	    		}else if(msg.obj.equals("E11")){
	    			getMessageDialog("警告", "缺少必要資料！").show();
	    		}else if(msg.obj.equals("E12")){
	    			getMessageDialog("警告", "資料格式錯誤！").show();
	    		}else if(msg.obj.equals("E99")){
	    			getMessageDialog("警告", "其他錯誤！").show();
	    		}
	    	}
	    	if(msg.what==2){
	    		Log.i(TAG, "msg.what==2");
	    		pd.dismiss();
	    		getMessageDialog("警告", getResources().getString(R.string.cannt_link_server)).show();
	    	}
	    	if(msg.what==3){
	    		pd.dismiss();
	    		getMessageDialog("警告", msg.obj.toString()).show();
	    	}
	    	if(msg.what==99){
//				boolean isStartBlueTooth = MainActivity.isServiceRunning(getApplicationContext(), GetBlueToothDeviceDataService.class.getName());
//			    if(!isStartBlueTooth){
//			    	Intent inetnt= new Intent(QueryRecordDataActivity.this, GetBlueToothDeviceDataService.class);
//				    startService(inetnt);
//			    }
			}
	    }
	};
	
	
    
}
