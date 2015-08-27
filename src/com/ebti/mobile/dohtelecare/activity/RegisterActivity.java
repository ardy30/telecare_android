package com.ebti.mobile.dohtelecare.activity;

import idv.yuanyuchang.YuUtils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import com.ebti.mobile.dohtelecare.R;
import com.ebti.mobile.dohtelecare.activity.RegisterActivity;
import com.ebti.mobile.dohtelecare.constant.Constant;
import com.ebti.mobile.dohtelecare.helper.NetService;
import com.ebti.mobile.dohtelecare.model.AreaData;
import com.ebti.mobile.dohtelecare.model.User;
import com.ebti.mobile.dohtelecare.respository.AreaDataRepo;
import com.ebti.mobile.dohtelecare.util.ScreenManager;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

public class RegisterActivity extends Activity {
	public static final String TAG = "RegisterActivity";
	ProgressDialog pd;
	
	private NetService netService = null;
	private static ConnectivityManager connMgr;
	private Bundle bundle = null;
	private Bundle getAccountPsdBd = null;
	private String sex = "M";
	private String birthday = "";
	private String phone = "";
	private String area = "";
	private String areaName = "";
	private EditText birthdayET = null;
	private EditText phoneET = null;
	private Spinner areaSP = null;
	private ArrayAdapter<AreaData> areaAdapter =null;
	private ArrayList<AreaData> getAreaList = null;
	private AreaDataRepo areaRepo = null;
	private String areaResponse;
	private String errMessage = "";
	
	
	private final Handler areaHandler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				Log.i("info", "setAreaSpinner");
				  setAreaSpinner();
				pd.dismiss();
			}
			else if( msg.what == 2 )
			{
				
				pd.dismiss();
				
				getAlertDialog( getString(R.string.reg_catchAreaDataErr), "RegisterAccountActivity" ).show();
				
				
			}
			else if( msg.what == 3 )
			{
				pd.dismiss();
				getAlertDialog( getString(R.string.reg_jsonParseErr),"RegisterAccountActivity" ).show();
			}
		}
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register2);
        
        Log.i(TAG,"onCreate");
		findViews();
		
//		getAccountPsdBd  =  this.getIntent().getExtras();
        bundle = this.getIntent().getExtras();
        
        // 上一步按鈕
        ((Button) findViewById(R.id.regBackBtn)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ScreenManager.getScreenManager().popActivity();
				ScreenManager.getScreenManager().popAllActivityExceptOne(RegisterActivity.class);
				Intent intent = new Intent( RegisterActivity.this, RegisterAccountActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
				
			}
		});
        
        // 設定 性別 radio button
        ((RadioGroup) findViewById( R.id.sexRadioGroup )).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if( checkedId == R.id.rbFemale )
				{
					sex = "F";
//					Toast.makeText(getApplicationContext(), "F", Toast.LENGTH_SHORT).show();
				}
				else
				{
					sex = "M";
//					Toast.makeText(getApplicationContext(), "M", Toast.LENGTH_SHORT).show();
				}
			}
		});
        
        
        // 設定 生日 datePicker
        birthdayET.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDialog(0);
			}
		});
        
        // 電話
        phoneET.setHint(getString(R.string.reg_phoneHint));
        phoneET.setRawInputType( InputType.TYPE_CLASS_PHONE );
        
        
      
       
        
		
		// 設定註冊按鈕點擊事件
		((Button) findViewById(R.id.sendregisterbtn)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
				Log.i("info", "birthdayET:" + birthdayET.getText().toString() );
				Log.i("info", "phoneET:" + phoneET.getText().toString() );
				//Toast.makeText(getApplicationContext(), YuUtils.isValidateEnglishLEtter( phoneET.getText().toString() ) + "", Toast.LENGTH_SHORT).show();
				phone = phoneET.getText().toString().trim();
				
				if(!isMobileNetworkAvailable(getApplicationContext())){
					getAlertDialog( getString(R.string.networkDisconnect)).show();
				}
				else if( birthday.equals("") )
				{
					getAlertDialog( getString(R.string.reg_birthEmpty)).show();
				}
				else	 if( phone.equals("") )
				{
					getAlertDialog( getString(R.string.reg_phoneEmpty)).show();
				}
				// 檢測是電話欄位否有英文
				else if( YuUtils.isValidateEnglishLEtter( phone )  )
				{
					getAlertDialog( getString(R.string.reg_phoneErr)).show();
				}
				else
				{
				
					Log.i("info","sex:" + sex);
					new AlertDialog.Builder(RegisterActivity.this)
					.setTitle("您的註冊資訊")
					.setMessage( "您的帳號：" +  bundle.getString("account") + "\n性別：" + YuUtils.genderCodeTransferName( sex ) + "\n" + "生日: " + YuUtils.birthdayTransfer( birthday ) + "\n" + "電話："+ phone + "\n" + "居住地: "+ areaName  )
					.setPositiveButton("確定註冊",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									
									pd = ProgressDialog.show(RegisterActivity.this, "註冊中....", "與伺服器註冊中,請稍待!!", true, false);
									new Thread(){
										@Override  
							            public void run() {
											Log.i(TAG, "!SystemProperty.isNonVerifyTest()");
											doRegister();
										}
									}.start();
									
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									
									
								}
							}).show();
					Log.i("info", "account:" + bundle.getString("account"));
					Log.i("info", "psd:" + bundle.getString("password"));
					
					
				}
			}
		});
		
		pd = ProgressDialog.show(RegisterActivity.this, getString(R.string.webview_loading_title), getString(R.string.reg_loadArea), true, false);
		new Thread()
	      { 
			 @Override  
	        public void run()
	        { 
				 
				netService = new NetService();
				getAreaData();
				
	        }
	      }.start();
        
	}
   
    
 
    /**
     * 設定 縣市內容
     */
    private void setAreaSpinner() {
    	 areaAdapter = new ArrayAdapter<AreaData> (this, android.R.layout.simple_spinner_item, getAreaList);
         areaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
         
         areaSP.setAdapter(areaAdapter);
         areaSP.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

 			@Override
 			public void onItemSelected(AdapterView<?> arg0, View arg1,
 					int arg2, long arg3) {
 				// TODO Auto-generated method stub
 				area = getAreaList.get(arg2).getAreaCode();
 				areaName = getAreaList.get(arg2).getAreaName();
 				
 				//Toast.makeText(getApplicationContext(), areaName + "", Toast.LENGTH_SHORT).show();
 				//Toast.makeText(getApplicationContext(), area + "", Toast.LENGTH_SHORT).show();
 				
 			}

 			@Override
 			public void onNothingSelected(AdapterView<?> arg0) {
 				// TODO Auto-generated method stub
 				
 			}});		
	}



	private void getAreaData() {
    		areaResponse = netService.GetAreaList();
    		if( areaResponse.equals("") )
    		{
    			Log.i("info", "areaResponse == \"\" ");
    			areaHandler.sendMessage( areaHandler.obtainMessage(2) );
    		}
    		else if( getAreaList == null )
    		{
    			areaRepo = new AreaDataRepo();
        		Log.i("info", "areaResponse:"+ areaResponse);
        		getAreaList = new ArrayList<AreaData>();
        		getAreaList = areaRepo.getAreaDataList(areaResponse );
        		if( getAreaList == null )
        		{
        			areaHandler.sendMessage( areaHandler.obtainMessage(3) );
        		}
        		else
        		{
        			areaHandler.sendMessage( areaHandler.obtainMessage(1) );
        		}
        		
    		}
    	
    		
    		Log.i("info", areaResponse);
	}



    private void findViews() {
    		birthdayET = (EditText) findViewById(R.id.birthdayET);
    		phoneET = (EditText) findViewById(R.id.phoneET);
    		areaSP = (Spinner) findViewById(R.id.areaSP);
	}
    
    
    @Override
    protected Dialog onCreateDialog(int id)
    {
    		if( id == 0 )
    		{
    			DatePickerDialog dlg = new DatePickerDialog( this, dsl, 1999, 6, 20 );
    			return dlg;
    		}
    		else 
    		{
    			return null;
    		}
    }
    
    
	// set date dialog
	private OnDateSetListener dsl = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {

			int newYear;
			String newMonth;
			String newday;

			newYear = year;
			if ((monthOfYear + 1) < 10) {

				newMonth = "0" + (monthOfYear + 1);

			} else {

				newMonth = "" + (monthOfYear + 1);

			}
			if (dayOfMonth < 10) {

				newday = "0" + dayOfMonth;

			} else {

				newday = "" + dayOfMonth;

			}

			birthdayET.setText(newYear + "/" + newMonth + "/" + newday);
			birthday = newYear + newMonth + newday;
		}
	};
	
    //確認網路狀態
  	public static boolean isMobileNetworkAvailable(Context con){
  		if(null == connMgr){
  			connMgr = (ConnectivityManager)con.getSystemService(Context.CONNECTIVITY_SERVICE);
  		}
  		NetworkInfo wifiInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
  		NetworkInfo mobileInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
  		if(wifiInfo!= null && wifiInfo.isAvailable()){
  			return true;
  		}else if(mobileInfo!=null && mobileInfo.isAvailable()){
  			return true;
  		}else{
  			return false;
  		}
  	}
	
	protected void doRegister(){
		try {
//			NetService netService = new NetService();
//			EditText accountEditText = (EditText) findViewById(R.id.inputnewaccount);
//			EditText passwordEditText = (EditText) findViewById(R.id.inputpwd);
			User user = new User();
			Log.i("info", "account:" + bundle.getString("account"));
			Log.i("info", "psd:" + bundle.getString("password"));
			Log.i("info", "sex:" + sex	);
			Log.i("info", "birthday:" + birthday );
			Log.i("info", "phone:" + phone );
			Log.i("info", "area:" + area );
			user.setUid(bundle.getString("account"));
			user.setPassword(bundle.getString("password"));
			user.setGender(sex);
			user.setBirthday(birthday);
			user.setPhone(phone);
			user.setArea(area);
			
			
			String response = netService.CallRegisterUser(user);
			
			if(response!=null){
				JSONObject jsonResponse = new JSONObject(response);
				String messageCode = jsonResponse.getString("Message");
				String messageDescript = jsonResponse.getString("Description");
				Log.i("info", "messageCode:" + messageCode);
				Log.i("info", "messageDescript:" + messageDescript);
				if(messageCode.equals("A01")){
					
					handler.sendEmptyMessage(0);
					
				}else{
					errMessage = messageDescript;
					handler.sendMessage(handler.obtainMessage(1, messageCode));
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			Log.e(TAG, "doRegister Error : " + ex);
			Log.e(TAG, "doRegister Error : " + ex.getMessage());
			handler.sendMessage(handler.obtainMessage(10, ex));
		}
	}
	
	/** 
	  * 用Handler 更新UI 
	  */
	private Handler handler = new Handler(){
		@Override  
		public void handleMessage(Message msg) {
			Log.i(TAG, "msg : " + msg.toString());
			pd.dismiss();
			if (msg.what==0){
				Log.i(TAG, "msg.what==0");
				Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
				Bundle regCompleteBundle = new Bundle();
				regCompleteBundle.putString( "account" , bundle.getString("account") );
				regCompleteBundle.putString( "message" , "電子郵件信箱驗證中，請至電子郵件信箱收信，並重新登入。");
				intent.putExtras(regCompleteBundle);
				startActivity(intent);
//				bundle.putString(Constant.DELIVER_LOGIN_ACCOUNT, bundle.getString("account") );
//				bundle.putString(Constant.DELIVER_LOGIN_MESSAGE, "電子郵件信箱驗證中，請至電子郵件信箱收信，並重新登入。");
//				intent.putExtras(bundle);
//				setResult(RESULT_OK, intent);
//				finish();
			}
			if(msg.what==1){
				Log.i(TAG, "msg.what==2");
				getAlertDialog(errMessage).show();
				/*
				if(msg.obj.equals("E03")){
					getAlertDialog("帳號格式錯誤！").show();
				}else if(msg.obj.equals("E04")){
					getAlertDialog("密碼格式錯誤！").show();
				}else if(msg.obj.equals("E05")){
					getAlertDialog("帳號已存在無法註冊！").show();
				}else if(msg.obj.equals("E99")){
					getAlertDialog("其他錯誤！").show();
				}*/
			}
			if(msg.what==10){
				Log.i(TAG, "msg.what==10");
				if(msg.obj.toString().indexOf("Timeout")>=0){
					getAlertDialog(getResources().getString(R.string.cannt_link_server)).show();
				}else{
					//getAlertDialog(msg.obj.toString()).show();
					getAlertDialog(errMessage).show();
				}
			}
		}
	};
	
	private AlertDialog getAlertDialog(String message){
        //產生一個Builder物件
        Builder builder = new AlertDialog.Builder(this);
        //設定Dialog的標題
        builder.setIcon(R.drawable.alert_icon);
        builder.setTitle("警告");
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
	
	private AlertDialog getAlertDialog(String message, String acty){
        //產生一個Builder物件
        Builder builder = new AlertDialog.Builder(this);
        //設定Dialog的標題
        builder.setIcon(R.drawable.alert_icon);
        builder.setTitle("警告");
        //設定Dialog的內容
        builder.setMessage(message);
        //設定Positive按鈕資料
        builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //按下按鈕時顯示快顯
            		Intent intent = new Intent(RegisterActivity.this, RegisterAccountActivity.class);
            		intent.putExtras(bundle);
            		startActivity(intent);
            }
        });
        //利用Builder物件建立AlertDialog
        return builder.create();
	}
	
	 //按手機上一頁按鈕事件
	 @Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			 if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
				 Intent intent = new Intent( RegisterActivity.this, RegisterAccountActivity.class);
					intent.putExtras(bundle);
					startActivity(intent);
				 return true;
			 }
		return super.onKeyDown(keyCode, event);
		}
}
