package com.ebti.mobile.dohtelecare.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.ebti.mobile.dohtelecare.R;
import com.ebti.mobile.dohtelecare.activity.BloodPressureActivity;
import com.ebti.mobile.dohtelecare.constant.Constant;
import com.ebti.mobile.dohtelecare.model.BioData;
import com.ebti.mobile.dohtelecare.sqlite.BioDataAdapter;
import com.ebti.mobile.dohtelecare.sqlite.UserAdapter;
import com.ebti.mobile.dohtelecare.util.ScreenManager;

import android.os.Bundle;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class BloodPressureActivity extends Activity {

	public static final String TAG = "BloodPressureActivity";

	static final int RECORD_DATEPICKER = 10;
	
	Dialog recordDateTimeDialog;
	
	public static BloodPressureActivity instance = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bloodpressure);
        Log.i(TAG,"onCreate");
		
		instance = this;
		
		
		//放入Activity Stack
		ScreenManager.getScreenManager().pushActivity(this);
		
		//====================Common========================
		//Information
		/*
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
		
		//返回
		((Button) findViewById(R.id.backbutton)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.i(TAG, "backbutton onClick()");
//				((Button) findViewById(R.id.backbutton)).setBackgroundColor(getResources().getColor(R.color.click_color));
				ScreenManager.getScreenManager().popAllActivityExceptOne(BloodPressureActivity.class);
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

			    ScreenManager.getScreenManager().popAllActivityExceptOne(BloodPressureActivity.class);
				ScreenManager.getScreenManager().popActivity();
				
				Intent intent = new Intent(BloodPressureActivity.this, LoginActivity.class);
				startActivity(intent);
				
				//Intent intent = new Intent(BloodPressureActivity.this, OptionActivity.class);
				//startActivity(intent);
			}
		});
		
		//關於本軟體
//  		((Button) findViewById(R.id.aboutappbutton)).setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
//				Log.i(TAG, "aboutappbutton onClick()");
//				Intent intent = new Intent(BloodPressureActivity.this, AboutAppActivity.class);
//				startActivity(intent);
//			}
//		});
		
		//修改密碼
//  		((Button) findViewById(R.id.changepasswordbutton)).setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
//				Log.i(TAG, "changepasswordbutton onClick()");
//				Intent intent = new Intent(BloodPressureActivity.this, ModifyPasswordActivity.class);
//				startActivityForResult(intent, 0);
//			}
//		});
		//====================Common End===========================

  		//取消輸入資料
		((Button) findViewById(R.id.datacancelbtn)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.i(TAG, "datacancelbtn onClick()");
				ScreenManager.getScreenManager().popActivity();
			}
		});
		
		//手動輸入存進sqlite
		((Button) findViewById(R.id.datauploadbtn)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.i(TAG, "datauploadbtn onClick()");
				saveKeyInData();
			}
		});
		
		//輸入量測日期
		final EditText editTextRecordDateTime = (EditText) findViewById(R.id.editTextRecordDateTime);
		//先帶入現在時間
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		editTextRecordDateTime.setText(sdf.format(new Date()));
		editTextRecordDateTime.setInputType(InputType.TYPE_NULL); // 關閉軟鍵盤
		editTextRecordDateTime.setOnTouchListener(new View.OnTouchListener() {
  			@Override
  			public boolean onTouch(View v, MotionEvent event) {
				Log.i(TAG, "editTextRecordDateTime onClick()");
				showDialog(RECORD_DATEPICKER);
				return false;
			}
		});
	    /*
	    boolean isStartListenDevice = isServiceRunning(getApplicationContext(), MainService.class.getName());
	    if(!isStartListenDevice){
	    	if (BluetoothAdapter.getDefaultAdapter().isEnabled())
			{
				StartMainService();	
			}else
			{
				Toast.makeText(getApplicationContext(), "Bluetooth not in service.", Toast.LENGTH_LONG).show();
			}
	    }
	    */
	    
	    
	    
	}
	
	public void saveKeyInData(){
		Log.i(TAG, "uploadKeyInData()");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		//血壓資料
		EditText editTextRecordDateTime = (EditText) findViewById(R.id.editTextRecordDateTime);
		EditText editTextDiastolicBloodPressure = (EditText) findViewById(R.id.editTextDiastolicBloodPressure);
		EditText editTextSystolicBloodPressure = (EditText) findViewById(R.id.editTextSystolicBloodPressure);
		EditText editTextPulse = (EditText) findViewById(R.id.editTextPulse);
		String recorderDateTime = editTextRecordDateTime.getText().toString();
		BioData bioData = new BioData();
		bioData.setDeviceTime(recorderDateTime);
		bioData.setDeviceType(Constant.BIODATA_DEVICE_TYPE_BLOOD_PRESSURE);
		
		Date recordDate;
		try {
			recordDate = sdf.parse(recorderDateTime);
		} catch (ParseException e) {
			Log.e(TAG, "recorderDateTime ParseException : " + e);
			// TODO Auto-generated catch block
			e.printStackTrace();
			initToast("時間格式錯誤，請調整！");
			return;
		}
		if(editTextDiastolicBloodPressure.getText().toString().trim().equals("")){
			initToast("請輸入舒張壓");
			return;
		}else if(editTextSystolicBloodPressure.getText().toString().trim().equals("")){
			initToast("請輸入收縮壓");
			return;
		}else if(editTextPulse.getText().toString().trim().equals("")){
			initToast("請輸入脈搏");
			return;
		}else if(editTextRecordDateTime.getText().toString().trim().equals("")){
			initToast("請輸入量測時間");
			return;
		}else if(recordDate.after(new Date())){
			initToast("量測時間不能超過現在時間，\n請調整時間！");
			editTextRecordDateTime.setText(sdf.format(new Date()));
			return;
		}else{
			bioData.setBhp(editTextSystolicBloodPressure.getText().toString());
			bioData.setBlp(editTextDiastolicBloodPressure.getText().toString());
			bioData.setPulse(editTextPulse.getText().toString());
		}
		
		//UserData
		UserAdapter userAdapter = new UserAdapter(getApplicationContext());
		com.ebti.mobile.dohtelecare.model.User user = userAdapter.getUserUIdAndPassword();
		//手機資訊
		TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		//String imsi = mTelephonyMgr.getSubscriberId();
		String imei = mTelephonyMgr.getDeviceId();
		BioDataAdapter bioDataAdapter = new BioDataAdapter(getApplicationContext());
		bioData.set_id(recorderDateTime + user.getUid());
		bioData.setUserId(user.getUid());
		bioData.setInputType(Constant.UPLOAD_INPUT_TYPE_MANUAL);
		bioDataAdapter.createBloodPressure(bioData);
		getMessageDialog("訊息", "血壓資料已儲存").show();
		//清除edittext
		editTextDiastolicBloodPressure.setText("");
		editTextSystolicBloodPressure.setText("");
		editTextPulse.setText("");
		editTextRecordDateTime.setText(sdf.format(new Date()));
	}
	
	/*
	//Service丟來的訊息
	private String msg="";  
    private UpdateReceiver receiver;  
    // 实现一个 BroadcastReceiver，用于接收指定的 Broadcast  
    public class UpdateReceiver extends BroadcastReceiver{  
  
        @Override
        public void onReceive(Context context, Intent intent) {
        	Log.i(TAG, "BloodPressureActivity UpdateReceiver");
            msg = intent.getStringExtra("msg");
            CheckBox cbBloodglucoseConnetced = (CheckBox)findViewById(R.id.bloodglucoseConnetced);
            if(msg.equals("bloodglucose")){
            	cbBloodglucoseConnetced.setChecked(true);
            }else{
            	cbBloodglucoseConnetced.setChecked(false);
            }
        }  
          
    }
    */
	
	/**
     * 用来判断服务是否运行.
     * @param context
     * @param className 判断的服务名字：包名+类名
     * @return true 在运行, false 不在运行
     */
    public static boolean isServiceRunning(Context context,String className) {
        boolean isRunning = false;
        Log.i(TAG, className);
        ActivityManager activityManager = (ActivityManager)context.getSystemService(ACTIVITY_SERVICE); 
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);
        if (!(serviceList.size()>0)) {
            return false;
        }
        for (int i=0; i<serviceList.size(); i++) {
        	//Log.i(TAG, "Service Name : " + serviceList.get(i).service.getClassName());
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        Log.i(TAG,"service is running?=="+isRunning);
        return isRunning;
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
    
	private void initToast(String message)
	{
		LinearLayout ll = new LinearLayout(this);
		ll.setBackgroundColor(Color.BLACK);
		TextView tv = new TextView(this);
        tv.setTextSize(20);
        tv.setPadding(10, 5, 10, 5);
        tv.setTextColor(Color.WHITE);
        tv.setText(message);
        
        ll.addView(tv);

		Toast toastStart = new Toast(this);
		toastStart.setGravity(Gravity.CENTER, 0, 0);
		toastStart.setDuration(Toast.LENGTH_LONG);
		toastStart.setView(ll);
		toastStart.show();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		switch(id){
			case RECORD_DATEPICKER:
				Log.i(TAG, "open BEGINDATE_DATEPICKER");
				final EditText editTextRecordDateTime = (EditText) findViewById(R.id.editTextRecordDateTime);
				recordDateTimeDialog = new Dialog(BloodPressureActivity.this);
				recordDateTimeDialog.setContentView(R.layout.datetimepicker);
				recordDateTimeDialog.setTitle("請輸入量測日期");
				recordDateTimeDialog.setCancelable(true);
				TimePicker timePickerRecord = (TimePicker) recordDateTimeDialog.findViewById(R.id.timePickerRecord);
				timePickerRecord.setIs24HourView(true);
				timePickerRecord.setCurrentHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
				
				//確定
				((Button) recordDateTimeDialog.findViewById(R.id.recordDateTimeCheck)).setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						DatePicker datePickerRecord = (DatePicker) recordDateTimeDialog.findViewById(R.id.datePickerRecord);
						String yearString = String.valueOf(datePickerRecord.getYear());
						String monthString = String.valueOf((datePickerRecord.getMonth()+1)>9?(datePickerRecord.getMonth()+1):"0" + (datePickerRecord.getMonth()+1));
						String dayString = String.valueOf(datePickerRecord.getDayOfMonth()>9?datePickerRecord.getDayOfMonth():"0" + datePickerRecord.getDayOfMonth());
						TimePicker timePickerRecord = (TimePicker) recordDateTimeDialog.findViewById(R.id.timePickerRecord);
						String hourString = String.valueOf(timePickerRecord.getCurrentHour()>9?timePickerRecord.getCurrentHour():"0" + timePickerRecord.getCurrentHour());
						String minuteString = String.valueOf(timePickerRecord.getCurrentMinute()>9?timePickerRecord.getCurrentMinute():"0" + timePickerRecord.getCurrentMinute());
						String recorderDateTime = yearString + "/" + monthString + "/" + dayString + " " + hourString + ":" + minuteString + ":00";
						Log.i(TAG, "Datetime : " + recorderDateTime);
						editTextRecordDateTime.setText(recorderDateTime);
						recordDateTimeDialog.dismiss();
						//讓Focus離開
						editTextRecordDateTime.setSelected(false);
					}
				});
				return recordDateTimeDialog;
			default:
				return new Dialog(getApplicationContext());
		}
	}
}
