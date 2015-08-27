package com.ebti.mobile.dohtelecare.activity;

import org.json.JSONException;
import org.json.JSONObject;

import com.ebti.mobile.dohtelecare.R;
import com.ebti.mobile.dohtelecare.constant.Constant;
import com.ebti.mobile.dohtelecare.constant.SystemProperty;
import com.ebti.mobile.dohtelecare.helper.NetService;

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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ForgotPasswordActivity extends Activity {
	public static final String TAG = "ForgotPasswordActivity";
	private static ConnectivityManager connMgr;
	ProgressDialog pd;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgotpassword);
        Log.i(TAG,"onCreate");
		
		Bundle bundle = this.getIntent().getExtras();
		if (bundle != null) {
			((EditText) findViewById(R.id.getaccount)).setText(bundle.getString(Constant.DELIVER_LOGIN_ACCOUNT));
		}
		
		//忘記密碼
		((Button) findViewById(R.id.sendregetpasswordmessage)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.i(TAG, "sendregetpasswordmessage onClick()");
				if(checkInternet())
				{
					String emailString = ((EditText)findViewById(R.id.getaccount)).getText().toString();
					if(!emailString.trim().equals("")){
						//新建排程  
						pd = ProgressDialog.show(ForgotPasswordActivity.this, "查詢中....", "與伺服器註冊中,請稍待!!", true, false);
				        new Thread(){
				        	@Override  
				            public void run() {
				        		if (!SystemProperty.isNonVerifyTest()) {
									Log.i(TAG, "!SystemProperty.isNonVerifyTest()");
									forgotPassword();
								}
							}
						}.start();
					}else{
						getAlertDialog("請輸入帳號以查詢密碼！").show();
					}
				}
				else
				{
					getAlertDialog("無法連上網路，請確認您的網路狀態！").show();
				}
			}
		});
		
		
		((Button) findViewById(R.id.backbutton)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
				Intent intent = new Intent();
				intent.setClass(ForgotPasswordActivity.this, LoginActivity.class);
				startActivity(intent);
				
			}
		});
	}
	
	public void forgotPassword(){
		String emailString = ((EditText)findViewById(R.id.getaccount)).getText().toString();
		NetService netService = new NetService();
		String response = netService.CallResetPassword(emailString);
		Message msg = new Message();
		if(response!=null){
			try {
				JSONObject sonResponse = new JSONObject(response);
				String messageCode = sonResponse.getString("Message");
				
				if(messageCode.equals("A01")){
					handler.sendEmptyMessage(0);
				}else{
					msg.what = 1;
					msg.obj = messageCode;
					handler.sendMessage(msg);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e(TAG, "JSONException : " + e);
				msg.what = 3;
				if(e.toString().indexOf("Timeout")>=0){
					msg.obj = getResources().getString(R.string.cannt_link_server);
				}else{
					msg.obj = e;
				}
				getAlertDialog(msg.obj.toString()).show();
			}
		}
	}
	
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
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putString(Constant.DELIVER_LOGIN_ACCOUNT, ((EditText) findViewById(R.id.getaccount)).getText().toString());
				bundle.putString(Constant.DELIVER_LOGIN_MESSAGE, "已將新密碼寄到您的信箱，請確認新密碼後以新密碼登入。");
				intent.putExtras(bundle);
				setResult(RESULT_OK, intent);
				finish();
			}
			if(msg.what==1){
				Log.i(TAG, "msg.what==1");
				if(msg.obj.equals("E01")){
					getAlertDialog("帳號不存在！").show();
				}else if(msg.obj.equals("E02")){
					getAlertDialog("密碼錯誤，身分驗證失敗！").show();
				}else if(msg.obj.equals("E03")){
					getAlertDialog("帳號格式錯誤！").show();
				}else if(msg.obj.equals("E04")){
					getAlertDialog("密碼格式錯誤！").show();
				}else if(msg.obj.equals("E05")){
					getAlertDialog("帳號已存在無法註冊！").show();
				}else if(msg.obj.equals("E11")){
					getAlertDialog("缺少必要資料！").show();
				}else if(msg.obj.equals("E12")){
					getAlertDialog("資料格式錯誤！").show();
				}else if(msg.obj.equals("E21")){
					getAlertDialog("生理資料格式錯誤！").show();
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
	
				// 檢測網路是否連上
				private boolean checkInternet() {
					boolean result = false;
					ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
					NetworkInfo info = connManager.getActiveNetworkInfo();
					if (info == null || !info.isConnected()) {
						result = false;
					} else {
						if (!info.isAvailable()) {
							result = false;
						} else {
							result = true;
						}
					}

					Log.d("info", "網路是否連上:" + result);

					return result;
				}
}
