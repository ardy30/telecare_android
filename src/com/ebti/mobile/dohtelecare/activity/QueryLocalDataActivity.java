package com.ebti.mobile.dohtelecare.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.ebti.mobile.dohtelecare.R;
import com.ebti.mobile.dohtelecare.sqlite.UserAdapter;
import com.ebti.mobile.dohtelecare.util.ScreenManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.YuvImage;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

public class QueryLocalDataActivity extends Activity {
	public static final String TAG = "QueryLocalDataActivity";
	
	private int myYear, myMonth, myDay, myHour, myMinute;
	static final int BEGINDATE_DATEPICKER = 10;
	static final int ENDDATE_DATEPICKER = 11;
	static final int GET_SERVER_RECORDER = 12;
	
	final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
	
	String beginDateStr ="";
	String endDateStr ="";
	
	ProgressDialog pd;
	
	public static QueryLocalDataActivity instance = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.querylocaldata);
		
		instance = this;
        //===================將MainActivity之後的Activity 關閉=======================
      	Log.i(TAG,"before popAllActivityExceptOne()");
      	ScreenManager.getScreenManager().popAllActivityExceptOne(MainActivity.class);
      	Log.i(TAG,"after popAllActivityExceptOne()");
      	//=================將LoginActivity之後的Activity 關閉 End======================
      	
      	//放入Activity Stack
      	ScreenManager.getScreenManager().pushActivity(this);
		
		// ====================Common========================

		// Information
      	/*
		TextView textViewUnit = (TextView) findViewById(R.id.userinformation);
		StringBuilder userInformation = new StringBuilder();
		UserAdapter userAdapter = new UserAdapter(getApplicationContext());
		com.ebti.mobile.dohtelecare.model.User user = userAdapter
				.getUIDUnitType();
		userInformation.append("帳號：" + user.getUid() + "\n");
		if (user.getType().equals("Trial")) {
			userInformation.append(((String) getResources().getText(
					R.string.status_trial_user)));
		} else {
			userInformation.append(((String) getResources().getText(
					R.string.status_trsc_user))
					+ "(" + user.getUnit() + ")");
		}
		// set Information
		textViewUnit.setText(userInformation.toString());
*/
		// 返回
		((Button) findViewById(R.id.backbutton))
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						Log.i(TAG, "backbutton onClick()");
//						((Button) findViewById(R.id.backbutton)).setBackgroundColor(getResources().getColor(R.color.click_color));
						ScreenManager.getScreenManager()
								.popAllActivityExceptOne(
										QueryLocalDataActivity.class);
						ScreenManager.getScreenManager().popActivity();
					}
				});

		// 登出
		((Button) findViewById(R.id.logoutbutton))
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						Log.i(TAG, "logoutbutton onClick()");
						// 清除資料
						UserAdapter userAdapter = new UserAdapter(
								getApplicationContext());
						userAdapter.delAllUser();

						LoginActivity.instance.finish();

						ScreenManager.getScreenManager()
								.popAllActivityExceptOne(
										QueryLocalDataActivity.class);
						ScreenManager.getScreenManager().popActivity();

						Intent intent = new Intent(QueryLocalDataActivity.this,
								LoginActivity.class);
						startActivity(intent);

						// Intent intent = new
						// Intent(BloodPressureActivity.this,
						// OptionActivity.class);
						// startActivity(intent);
					}
				});

		// 關於本軟體
//		((Button) findViewById(R.id.aboutappbutton))
//				.setOnClickListener(new View.OnClickListener() {
//					public void onClick(View v) {
//						Log.i(TAG, "aboutappbutton onClick()");
//						Intent intent = new Intent(QueryLocalDataActivity.this,
//								AboutAppActivity.class);
//						startActivity(intent);
//					}
//				});

		// 修改密碼
//		((Button) findViewById(R.id.changepasswordbutton))
//				.setOnClickListener(new View.OnClickListener() {
//					public void onClick(View v) {
//						Log.i(TAG, "changepasswordbutton onClick()");
//						Intent intent = new Intent(QueryLocalDataActivity.this,
//								ModifyPasswordActivity.class);
//						startActivityForResult(intent, 0);
//					}
//				});
		// ====================common End===========================
		
		//結束日期edittext
  		EditText editLocalEndDate = (EditText)findViewById(R.id.editLocalEndDate);
		//取得預設時間區間
		Calendar calendar = Calendar.getInstance();		
		//結束時間限制，必須小於等於此時間
		final String endDateLimitStr = sdf.format(calendar.getTime());
		editLocalEndDate.setText(endDateLimitStr);
		
		editLocalEndDate.setOnTouchListener(new View.OnTouchListener() {
  			@Override
  			public boolean onTouch(View v, MotionEvent event) {
  				Log.i(TAG, "editLocalEndDate onTouch");
  				// 關閉軟鍵盤
  				((EditText)findViewById(R.id.editLocalEndDate)).setInputType(InputType.TYPE_NULL); 
  				final Calendar c = Calendar.getInstance();
  				myYear = c.get(Calendar.YEAR);
  				myMonth = c.get(Calendar.MONTH);
  				myDay = c.get(Calendar.DAY_OF_MONTH);
  				showDialog(ENDDATE_DATEPICKER);
  				return false;
  			}
  			
  		});
		
		//起始日期edittext
  		EditText editLocalBeginDate = (EditText)findViewById(R.id.editLocalBeginDate);
		
		//本地查詢時間區間為90天內
		calendar.add(Calendar.DAY_OF_YEAR,-90);
		//開始時間限制，必須大於等於此時間		
		final String beginDateLimitStr =  sdf.format(calendar.getTime());
		editLocalBeginDate.setText(beginDateLimitStr);
		editLocalBeginDate.setOnTouchListener(new View.OnTouchListener() {
  			@Override
  			public boolean onTouch(View v, MotionEvent event) {
  				Log.i(TAG, "editLocalBeginDate onTouch");
  				// 關閉軟鍵盤
  				((EditText)findViewById(R.id.editLocalBeginDate)).setInputType(InputType.TYPE_NULL); 
  				final Calendar c = Calendar.getInstance();
  				c.add(Calendar.DAY_OF_YEAR,-90);
  				myYear = c.get(Calendar.YEAR);
  				myMonth = c.get(Calendar.MONTH);
  				myDay = c.get(Calendar.DAY_OF_MONTH);
  				showDialog(BEGINDATE_DATEPICKER);
  				return false;
  			}
  		});
		
		//本機血糖資料查詢
  		((Button) findViewById(R.id.localBloodGlucoseData)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.i(TAG, "localBloodGlucoseData onClick()");
				
				EditText editLocalBeginDate = (EditText)findViewById(R.id.editLocalBeginDate);
				EditText editLocalEndDate = (EditText)findViewById(R.id.editLocalEndDate);
				beginDateStr = editLocalBeginDate.getText().toString().trim();
				endDateStr = editLocalEndDate.getText().toString().trim();
				
				try {
					Date beginDate = sdf.parse(beginDateStr);
					Date endDate = sdf.parse(endDateStr);
					Date beginDateLimit = sdf.parse(beginDateLimitStr);
					Date endDateLimit =sdf.parse(endDateLimitStr);
					
					if(((EditText)findViewById(R.id.editLocalBeginDate)).getText().toString().trim().equals("")){
						getMessageDialog("警告", "請輸入起始日期！").show();
						editLocalBeginDate.setText(beginDateLimitStr);
					}else if(((EditText)findViewById(R.id.editLocalEndDate)).getText().toString().trim().equals("")){
						getMessageDialog("警告", "請輸入結束日期！").show();
						editLocalEndDate.setText(endDateLimitStr);
					}else if(beginDate.after(endDate) ){
						getMessageDialog("警告", "起始日期不可大於結束日期！").show();
					}else if(beginDate.after(endDateLimit) ){
						getMessageDialog("警告", "查詢起始日期不可大於今天！").show();
						editLocalBeginDate.setText(beginDateLimitStr);
					}else if(endDate.after(endDateLimit) ){
						getMessageDialog("警告", "查詢結束日期不可大於今天！").show();
						editLocalEndDate.setText(endDateLimitStr);
					}else if(beginDate.before(beginDateLimit)){
						getMessageDialog("警告", "查詢起始日期不可小於90日前！").show();
						editLocalBeginDate.setText(beginDateLimitStr);						
					}else{
						//showDialog(GET_SERVER_RECORDER);
						Intent intent = new Intent(QueryLocalDataActivity.this, LocalRecordDataActivity.class);
						Bundle bundle = new Bundle();
						bundle.putString("dataType", "BG");
						bundle.putString("beginDateStr", beginDateStr);
						bundle.putString("endDateStr", endDateStr);
						intent.putExtras(bundle);
						startActivity(intent);
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				
				
			}
		});
  		
  		//本機血壓資料查詢
  		((Button) findViewById(R.id.localBloodPressureData)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.i(TAG, "localBloodPressureData onClick()");
				
				EditText editLocalBeginDate = (EditText)findViewById(R.id.editLocalBeginDate);
				EditText editLocalEndDate = (EditText)findViewById(R.id.editLocalEndDate);
				String beginDateStr = editLocalBeginDate.getText().toString().trim();
				String endDateStr = editLocalEndDate.getText().toString().trim();
				
				try {
					Date beginDate = sdf.parse(beginDateStr);
					Date endDate = sdf.parse(endDateStr);
					Date beginDateLimit = sdf.parse(beginDateLimitStr);
					Date endDateLimit =sdf.parse(endDateLimitStr);
					
					if(((EditText)findViewById(R.id.editLocalBeginDate)).getText().toString().trim().equals("")){
						getMessageDialog("警告", "請輸入起始日期！").show();
						editLocalBeginDate.setText(beginDateLimitStr);
					}else if(((EditText)findViewById(R.id.editLocalEndDate)).getText().toString().trim().equals("")){
						getMessageDialog("警告", "請輸入結束日期！").show();
						editLocalEndDate.setText(endDateLimitStr);
					}else if(beginDate.after(endDate) ){
						getMessageDialog("警告", "起始日期不可大於結束日期！").show();
					}else if(beginDate.after(endDateLimit) ){
						getMessageDialog("警告", "查詢起始日期不可大於今天！").show();
						editLocalBeginDate.setText(sdf.format(beginDateLimitStr));
					}else if(endDate.after(endDateLimit) ){
						getMessageDialog("警告", "查詢結束日期不可大於今天！").show();
						editLocalEndDate.setText(endDateLimitStr);
					}else if(beginDate.before(beginDateLimit)){
						getMessageDialog("警告", "查詢起始日期不可小於90日前！").show();
						editLocalBeginDate.setText(beginDateLimitStr);						
					}else{
						//showDialog(GET_SERVER_RECORDER);
						Intent intent = new Intent(QueryLocalDataActivity.this, LocalRecordDataActivity.class);
						Bundle bundle = new Bundle();
						bundle.putString("dataType", "BP");
						bundle.putString("beginDateStr", beginDateStr);
						bundle.putString("endDateStr", endDateStr);
						intent.putExtras(bundle);
						startActivity(intent);
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
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
			EditText editLocalBeginDate = (EditText)findViewById(R.id.editLocalBeginDate);
			int month = monthOfYear + 1;
			String dateformat = year + "/" + (month>9?month:"0"+month) + "/" + (dayOfMonth>9?dayOfMonth:"0"+dayOfMonth);
			editLocalBeginDate.setText(dateformat);
			beginDateStr = dateformat;
		}
	};
	
	private DatePickerDialog.OnDateSetListener endDateSetListener = new DatePickerDialog.OnDateSetListener(){
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			// TODO Auto-generated method stub
			EditText editLocalEndDate = (EditText)findViewById(R.id.editLocalEndDate);
			int month = monthOfYear + 1;
			String dateformat = year + "/" + (month>9?month:"0"+month) + "/" + (dayOfMonth>9?dayOfMonth:"0"+dayOfMonth);
			editLocalEndDate.setText(dateformat);
			endDateStr = dateformat;
		}
	};

	
	
}
