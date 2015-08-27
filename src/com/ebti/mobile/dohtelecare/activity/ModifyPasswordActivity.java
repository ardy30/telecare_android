package com.ebti.mobile.dohtelecare.activity;

import org.json.JSONException;
import org.json.JSONObject;

import com.ebti.mobile.dohtelecare.R;
import com.ebti.mobile.dohtelecare.constant.Constant;
import com.ebti.mobile.dohtelecare.helper.NetService;
import com.ebti.mobile.dohtelecare.model.User;
import com.ebti.mobile.dohtelecare.service.GetBlueToothDeviceDataService;
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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ModifyPasswordActivity extends Activity {
	public static final String TAG = "ModifyPasswordActivity";

	private static ConnectivityManager connMgr;
	
	ProgressDialog pd;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modifypassword);
        
        //放入Activity Stack
      	ScreenManager.getScreenManager().pushActivity(this);
      	
        //Information
//  		TextView textViewUnit = (TextView)findViewById(R.id.userinformation);
//  		StringBuilder userInformation = new StringBuilder();
//  		UserAdapter userAdapter = new UserAdapter(getApplicationContext());
//  		com.ebti.mobile.dohtelecare.model.User user = userAdapter.getUIDUnitType();
//  		userInformation.append("帳號：" + user.getUid() + "\n");
//  		if(user.getType().equals("Trial")){
//  			userInformation.append(((String)getResources().getText(R.string.status_trial_user)));
//  		}else{
//  			userInformation.append(((String)getResources().getText(R.string.status_trsc_user)) + "(" + user.getUnit() + ")");
//  		}
//  		//set Information
//  		textViewUnit.setText(userInformation.toString());
  		
  		//==========================common=========================
  		
		
		
		//登出
//		((Button) findViewById(R.id.logoutbutton)).setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
//				Log.i(TAG, "logoutbutton onClick()");
//				//清除資料
//				UserAdapter userAdapter = new UserAdapter(getApplicationContext());
//				userAdapter.delAllUser();
//				
//				LoginActivity.instance.finish();
//
//			    ScreenManager.getScreenManager().popAllActivityExceptOne(ModifyPasswordActivity.class);
//				ScreenManager.getScreenManager().popActivity();
//				
//				Intent intent = new Intent(ModifyPasswordActivity.this, LoginActivity.class);
//				startActivity(intent);
//				
//				//Intent intent = new Intent(BloodPressureActivity.this, OptionActivity.class);
//				//startActivity(intent);
//			}
//		});
		
		//返回
		((Button) findViewById(R.id.backbutton)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.i(TAG, "backbutton onClick()");
				Intent intent = new Intent( ModifyPasswordActivity.this, LoginActivity.class );
				ScreenManager.getScreenManager().popAllActivityExceptOne(ModifyPasswordActivity.class);
				ScreenManager.getScreenManager().popActivity();
			}
		});
		
		//關於本軟體
//  		((Button) findViewById(R.id.aboutappbutton)).setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
//				Log.i(TAG, "aboutappbutton onClick()");
//				Intent intent = new Intent(ModifyPasswordActivity.this, AboutAppActivity.class);
//				startActivity(intent);
//				ScreenManager.getScreenManager().popActivity(ModifyPasswordActivity.this);
//			}
//		});
		
		//====================common End===========================
  		//修改密碼submit Button
		((Button) findViewById(R.id.passwordcheckbtn)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.i(TAG, "passwordcheckbtn onClick()");
				//確認輸入的兩次新密碼是否一樣
				final String oldPassword = ((EditText)findViewById(R.id.inputoldpwd)).getText().toString();
				final String newPassword1 = ((EditText)findViewById(R.id.inputnewpwd)).getText().toString();
				final String newPassword2 = ((EditText)findViewById(R.id.inputcheckpwd)).getText().toString();
				if(oldPassword.equals("") || newPassword1.equals("") || newPassword2.equals("")){
					getAlertDialog("請輸入密碼！").show();
				}else if(oldPassword.trim().length()<6){
					getAlertDialog("您輸入的舊密碼不足6個字，請調整！").show();
				}else if(newPassword1.trim().length()>15){
					getAlertDialog("您輸入的舊密碼超過15個字，請調整！").show();
				}else if(newPassword1.trim().length()<6){
					getAlertDialog("您輸入的新密碼不足6個字，請調整！").show();
				}else if(newPassword1.trim().length()>15){
					getAlertDialog("您輸入的新密碼超過15個字，請調整！").show();
				}else if(!newPassword1.equals(newPassword2)){
					getAlertDialog("您輸入的新密碼不相符，請確認！").show();
				}else{
					//停止藍芽設備自動讀取, 資料自動上傳
			  		boolean isStartBlueTooth = MainActivity.isServiceRunning(getApplicationContext(), GetBlueToothDeviceDataService.class.getName());
				    if(isStartBlueTooth){
				    	Intent inetnt= new Intent(ModifyPasswordActivity.this, GetBlueToothDeviceDataService.class);
					    stopService(inetnt);
				    }
					//新建排程 
					if(isMobileNetworkAvailable(getApplicationContext())){
						pd = ProgressDialog.show(ModifyPasswordActivity.this, "傳送中....", "資料傳送中,需要較長時間等候.請稍待!!", true, false);
				        new Thread(){
				        	@Override  
				            public void run() {
				        		resetPassword(oldPassword, newPassword1, newPassword2);
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
					}else{
						getAlertDialog("無法連上網路").show();
					}
				}
			}
		});
	}
	
	public void resetPassword(String oldPassword, String newPassword1, String newPassword2){
		Log.i(TAG, "resetPassword()");
		NetService netService = new NetService();
		//取得帳號
		UserAdapter userAdapter = new UserAdapter(getApplicationContext());
		String userId = userAdapter.getUID();
		User user = new User();
		user.setUid(userId);
		user.setPassword(oldPassword);
		String response = netService.CallChangePassword(user, newPassword1);
		Message msg = new Message();
		if(response!=null){
			try {
				JSONObject sonResponse = new JSONObject(response);
				String messageCode = sonResponse.getString("Message");
				pd.dismiss();
				if(messageCode.equals("A01")){
					//update password
					user.setPassword(newPassword1);
					userAdapter.updateUserPassword(user);
					msg.what = 0;
					msg.obj = messageCode;
					handler.sendMessage(msg);
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
				pd.dismiss();
				handler.sendMessage(msg);
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
	
	/** 
	   * 用Handler 更新UI 
	   */  
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			pd.dismiss();
			Log.i(TAG, "msg : " + msg.toString());
			if (msg.what==0){
				Log.i(TAG, "msg.what==0, success");
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putString(Constant.DELIVER_LOGIN_MESSAGE, "密碼修改成功，下次請使用新密碼登入。");
				intent.putExtras(bundle);
				setResult(RESULT_OK, intent);
				
				ScreenManager.getScreenManager().popActivity(ModifyPasswordActivity.this);
			}
			if(msg.what==1){
				Log.i(TAG, "msg.what==1, msg.obj : " + msg.obj);
				if(msg.obj.equals("E01")){
					getAlertDialog("帳號不存在！").show();
				}else if(msg.obj.equals("E02")){
					getAlertDialog("密碼錯誤，身分驗證失敗！").show();
				}else if(msg.obj.equals("E99")){
					getAlertDialog("其他錯誤！").show();
				}
			}
			if(msg.what==2){
				Log.i(TAG, "msg.what==2");
				getAlertDialog(getResources().getString(R.string.cannt_link_server)).show();
			}
			if(msg.what==3){
				getAlertDialog(msg.obj.toString()).show();
			}
			if(msg.what==99){
				boolean isStartBlueTooth = MainActivity.isServiceRunning(getApplicationContext(), GetBlueToothDeviceDataService.class.getName());
			    if(!isStartBlueTooth){
			    	Intent inetnt= new Intent(ModifyPasswordActivity.this, GetBlueToothDeviceDataService.class);
				    startService(inetnt);
			    }
			}
			  //更新UI  
			 // statusTextView.setText("Completed!");  
		}
	};

}
