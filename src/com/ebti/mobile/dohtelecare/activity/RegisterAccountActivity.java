package com.ebti.mobile.dohtelecare.activity;

import idv.yuanyuchang.YuUtils;

import org.json.JSONObject;

import com.ebti.mobile.dohtelecare.R;
import com.ebti.mobile.dohtelecare.activity.RegisterAccountActivity;
import com.ebti.mobile.dohtelecare.constant.Constant;
import com.ebti.mobile.dohtelecare.helper.NetService;
import com.ebti.mobile.dohtelecare.model.User;
import com.ebti.mobile.dohtelecare.util.ScreenManager;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegisterAccountActivity extends Activity {
	public static final String TAG = "RegisterActivity";
	ProgressDialog pd;
	
	private static ConnectivityManager connMgr;
	private Bundle getBackBundle = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        
        Log.i(TAG,"onCreate");
		
		final EditText editTextAccount = (EditText) findViewById(R.id.inputnewaccount);
		final EditText editTextPassword = (EditText) findViewById(R.id.inputpwd);
		final EditText editTextCheckPassword = (EditText) findViewById(R.id.inputcheckpwd);
		Bundle bundle = this.getIntent().getExtras();
		if (bundle != null) {
			editTextAccount.setText(bundle.getString(Constant.DELIVER_LOGIN_ACCOUNT));
		}
		
		getBackBundle =  this.getIntent().getExtras();
		if( getBackBundle != null )
		{
			editTextAccount.setText( getBackBundle.getString( "account" ));
			editTextPassword.setText( getBackBundle.getString( "password" ));
			editTextCheckPassword.setText( getBackBundle.getString( "password" ));
		}
		
		// 上一步按鈕
        ((Button) findViewById(R.id.regBackBtn)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ScreenManager.getScreenManager().popActivity();
				ScreenManager.getScreenManager().popAllActivityExceptOne(RegisterActivity.class);
				Intent intent = new Intent( RegisterAccountActivity.this, LoginActivity.class);
				startActivity(intent);
				
			}
		});
        
		// 下一步
		((Button) findViewById(R.id.sendregisterbtn)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.i("info", "email:" + YuUtils.isValidateEmail(editTextAccount.getText().toString().trim()) );
				if(editTextAccount.getText().toString().trim().equals("")){
					getAlertDialog("請輸入帳號").show();
				}
				else if( !YuUtils.isValidateEmail(editTextAccount.getText().toString().trim()) )
				{
					getAlertDialog("請輸入正確的E-MAIL").show();
				}
				else if(editTextPassword.getText().toString().trim().equals("")){
					getAlertDialog("請輸入密碼").show();
				}else if(editTextCheckPassword.getText().toString().trim().equals("")){
					getAlertDialog("請確認密碼").show();
				}else if(editTextPassword.getText().toString().trim().length()<6){
					getAlertDialog("您輸入的密碼不足6個字，請調整！").show();
				}else if(editTextPassword.getText().toString().trim().length()>15){
					getAlertDialog("您輸入的密碼超過15個字，請調整！").show();
				}else if(!editTextCheckPassword.getText().toString().trim().equals(editTextPassword.getText().toString().trim())){
					getAlertDialog("您輸入的新密碼不相符，請確認！").show();
				}else{
					if(isMobileNetworkAvailable(getApplicationContext())){
						
						/*pd = ProgressDialog.show(RegisterAccountActivity.this, "註冊中....", "與伺服器註冊中,請稍待!!", true, false);
						new Thread(){
							@Override  
				            public void run() {
								Log.i(TAG, "!SystemProperty.isNonVerifyTest()");
								doRegister();
							}
						}.start();*/
						
						Intent intent = new Intent( RegisterAccountActivity.this, RegisterActivity.class);
						Bundle bundle = new Bundle();
						bundle.putString("account",  editTextAccount.getText().toString().trim() );
						bundle.putString("password", editTextPassword.getText().toString().trim() );
						intent.putExtras(bundle);
						startActivity( intent );
						ScreenManager.getScreenManager().pushActivity(RegisterAccountActivity.this);
					}else{
						getAlertDialog("無法連上網路").show();
					}
				}
			}
		});
	}
    
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
/*	
	protected void doRegister(){
		try {
			NetService netService = new NetService();
			EditText accountEditText = (EditText) findViewById(R.id.inputnewaccount);
			EditText passwordEditText = (EditText) findViewById(R.id.inputpwd);
			User user = new User();
			user.setUid(accountEditText.getText().toString());
			user.setPassword(passwordEditText.getText().toString());
			String response = netService.CallRegisterUser(user);
			if(response!=null){
				JSONObject jsonResponse = new JSONObject(response);
				String messageCode = jsonResponse.getString("Message");
				if(messageCode.equals("A01")){
					handler.sendEmptyMessage(0);
				}else{
					handler.sendMessage(handler.obtainMessage(1, messageCode));
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			Log.e(TAG, "doRegister Error : " + ex);
			handler.sendMessage(handler.obtainMessage(10, ex));
		}
	}
	*/
	/** 
	  * 用Handler 更新UI 
	  */
/*	private Handler handler = new Handler(){
		@Override  
		public void handleMessage(Message msg) {
			Log.i(TAG, "msg : " + msg.toString());
			pd.dismiss();
			if (msg.what==0){
				Log.i(TAG, "msg.what==0");
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putString(Constant.DELIVER_LOGIN_ACCOUNT, ((EditText) findViewById(R.id.inputnewaccount)).getText().toString());
				bundle.putString(Constant.DELIVER_LOGIN_MESSAGE, "電子郵件信箱驗證中，請至電子郵件信箱收信，並重新登入。");
				intent.putExtras(bundle);
				setResult(RESULT_OK, intent);
				finish();
			}
			if(msg.what==1){
				Log.i(TAG, "msg.what==2");
				if(msg.obj.equals("E03")){
					getAlertDialog("帳號格式錯誤！").show();
				}else if(msg.obj.equals("E04")){
					getAlertDialog("密碼格式錯誤！").show();
				}else if(msg.obj.equals("E05")){
					getAlertDialog("帳號已存在無法註冊！").show();
				}else if(msg.obj.equals("E99")){
					getAlertDialog("其他錯誤！").show();
				}
			}
			if(msg.what==10){
				Log.i(TAG, "msg.what==10");
				if(msg.obj.toString().indexOf("Timeout")>=0){
					getAlertDialog(getResources().getString(R.string.cannt_link_server)).show();
				}else{
					getAlertDialog(msg.obj.toString()).show();
				}
			}
		}
	};
	*/
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
	
	 //按手機上一頁按鈕事件
	 @Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			 if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
					Intent intent = new Intent();
					intent.setClass(RegisterAccountActivity.this, LoginActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				 return true;
			 }
		return super.onKeyDown(keyCode, event);
		}
}
