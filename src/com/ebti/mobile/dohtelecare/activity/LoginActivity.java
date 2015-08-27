package com.ebti.mobile.dohtelecare.activity;

import static com.ebti.mobile.dohtelecare.gcm.CommonUtilities.DISPLAY_MESSAGE_ACTION;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.ebti.mobile.dohtelecare.R;
import com.ebti.mobile.dohtelecare.activity.ForgotPasswordActivity;
import com.ebti.mobile.dohtelecare.activity.LoginActivity;
import com.ebti.mobile.dohtelecare.activity.RegisterAccountActivity;
import com.ebti.mobile.dohtelecare.constant.Constant;
import com.ebti.mobile.dohtelecare.constant.SystemProperty;
import com.ebti.mobile.dohtelecare.helper.NetService;
import com.ebti.mobile.dohtelecare.model.SqlVersion;
import com.ebti.mobile.dohtelecare.model.User;
import com.ebti.mobile.dohtelecare.service.GetBlueToothDeviceDataService;
import com.ebti.mobile.dohtelecare.sqlite.BioDataAdapter;
import com.ebti.mobile.dohtelecare.sqlite.SqlVersionAdapter;
import com.ebti.mobile.dohtelecare.sqlite.UserAdapter;
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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
//import com.sriramramani.droid.inspector.server.ViewServer;


public class LoginActivity extends Activity {

    /** App初始畫面,提供 會員申請 忘記密碼 登入 功能 */

    public static LoginActivity instance = null;

    public static final String TAG = "LoginActivity";
    ProgressDialog pd;
    private String loginPassword = "";
    private static ConnectivityManager connMgr;
    private Bundle bundle = null;
//	private Bundle pwdBundle = new Bundle();

    @Override
    public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

//    		ViewServer.get(this).addWindow(this);

        setContentView(R.layout.login);
        Log.i(TAG, "onCreate");
        instance = this;
        //查看手機解析度
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidthPixel = metrics.widthPixels;
        int screenheightPixel = metrics.heightPixels;
        Log.i(TAG, "the screen pixel is : "+screenWidthPixel + " X " + screenheightPixel);

        int density = metrics.densityDpi;
        if(density==DisplayMetrics.DENSITY_LOW){
            Log.i(TAG, "ldpi");
        }else if(density==DisplayMetrics.DENSITY_MEDIUM){
            Log.i(TAG, "mdpi");
        }else if(density==DisplayMetrics.DENSITY_HIGH){
            Log.i(TAG, "hdpi");
        }else if(density==DisplayMetrics.DENSITY_XHIGH){
            Log.i(TAG, "xhdpi");
        }else{
            Log.i(TAG, "unknow");
        }

        //放入Activity Stack
        if(ScreenManager.getScreenManager().checkActivityStack() == false){
            ScreenManager.getScreenManager().pushActivity(this);
        }else if(ScreenManager.getScreenManager().currentActivity() == null){
            ScreenManager.getScreenManager().pushActivity(this);
        }else if(ScreenManager.getScreenManager().currentActivity() != LoginActivity.this){
            Log.i(TAG,"before popAllActivityExceptOne()");
            ScreenManager.getScreenManager().popAllActivityExceptOne(MainActivity.class);
            ScreenManager.getScreenManager().popActivity();
            ScreenManager.getScreenManager().pushActivity(this);
            Log.i(TAG,"after popAllActivityExceptOne()");
            //ScreenManager.getScreenManager().pushActivity(this);
        }

        //確認SQL 版本 , 若為舊則更新
        SqlVersionAdapter sqlVersionAdapter = new SqlVersionAdapter(getApplicationContext());
        ArrayList<SqlVersion> listSqlVersion = sqlVersionAdapter.getSqlVersion();
        String sqlVersionString = "";
        if(listSqlVersion!=null && listSqlVersion.size() > 0){
            sqlVersionString = listSqlVersion.get(0).getSql_Version();
            // 目前sqllite db SQL_VERSION欄位為3
            if(!sqlVersionString.equals("3")){
                sqlVersionAdapter.updateSqlite();
            }
        }else{
            sqlVersionAdapter.updateSqlite();
        }

        //顯示版本資訊
        try {
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            int versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
            //((TextView)findViewById(R.id.appversion)).setText("版本資訊:V" + versionName);
        } catch (NameNotFoundException e) {
            Log.e(TAG, "NameNotFoundException : " + e);
            e.printStackTrace();
        }





        //記憶帳號
          useradapter = new UserAdapter(this);
          final String isRememberUser = useradapter.getRememberUser();
          if(isRememberUser!=null){
              if(isRememberUser.equals("1")){
                  User user = useradapter.getUserUIdAndPassword();
                  if(user!=null){
                      ((EditText) findViewById(R.id.inputaccount)).setText(user.getUid());
                      ((EditText) findViewById(R.id.inputpassword)).setText(user.getPassword());
                      ((CheckBox) findViewById(R.id.checkBoxRememberUser)).setChecked(true);

                  }
              }
          }


          bundle  = this.getIntent().getExtras();
        if( bundle != null )
        {
            ((EditText) findViewById(R.id.inputaccount)).setText( bundle.getString( "account" ));
            getAlertDialog("通知", bundle.getString( "message" )).show();
        }

          //登入功能
        ((Button) findViewById(R.id.loginbtn)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG,"progress dialog show");

                //停止藍芽設備自動讀取, 資料自動上傳
                // 檢查blue tooth service是否running
                  boolean isStartBlueTooth = MainActivity.isServiceRunning(getApplicationContext(), GetBlueToothDeviceDataService.class.getName());

                  if(isStartBlueTooth){

                        Intent inetnt= new Intent(LoginActivity.this, GetBlueToothDeviceDataService.class);
                    stopService(inetnt);

                }

                String account = ((EditText) findViewById(R.id.inputaccount)).getText().toString();
                String password = ((EditText) findViewById(R.id.inputpassword)).getText().toString();

                if(account.trim().equals("")){
                    getAlertDialog("警告", "請輸入帳號").show();
                }else if(password.trim().equals("")){
                    getAlertDialog("警告", "請輸入密碼").show();
                }
                else if(password.trim().length()<6){
                    getAlertDialog("警告", "您輸入的密碼不足6個字，請調整！").show();
                }/*else if(password.trim().length()>15){
                    getAlertDialog("警告", "您輸入的密碼超過15個字，請調整！").show();
                }*/else{
                    //boolean hasInternet = isMobileNetworkAvailable(LoginActivity.this);

                    //Log.i("info","hasInternet:"+hasInternet+"");

                    // if internet disconnect
                    if(checkInternet() == false){

                        Log.i("info", "internet disconnect");

                        if(isRememberUser!=null && isRememberUser.equals("1")){
                            User loginUser = useradapter.getUserUIdAndPassword();
                            String userId = ((EditText) findViewById(R.id.inputaccount)).getText().toString();
                            // 所輸入的帳號 與 sqlite內的帳號 是否相同
                            if(loginUser.getUid().equals(userId)){
                                EditText passwordET = (EditText) findViewById(R.id.inputpassword);
                                 loginPassword = passwordET.getText().toString();
                                // 所輸入的密碼 與 sqlite內的密碼 是否相同
                                if(loginPassword.equals(loginUser.getPassword())){

                                    ScreenManager.getScreenManager().popActivity();
                                    Intent inetntLogin = new Intent(LoginActivity.this, MainActivity.class);
                                      Log.i("info", "=====loginPassword:" + loginPassword);

//									pwdBundle.putString("pwd", loginPassword);
//				  					inetntLogin.putExtras(pwdBundle);

                                    startActivity(inetntLogin);

                                }else{

                                    getAlertDialog("警告", "密碼錯誤！").show();

                                }
                            }else{
                                getAlertDialog("警告", "無法連上網路，請確認您的網路狀態！").show();
                            }
                        }else{
                            getAlertDialog("警告", "無法連上網路，請確認您的網路狀態！").show();
                        }
                    // if internet is connect
                    }else{
                        Log.i("info", "do login" );
                        doLongin();
                    }
                }
            }
        });

        // 忘記密碼功能
        ((Button) findViewById(R.id.forgetpwedbtn)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i(TAG, "forgetpwedbtn onClick()");
//				((Button) findViewById(R.id.forgetpwedbtn)).setBackgroundColor(getResources().getColor(R.color.click_color));
                Intent intent = new Intent(LoginActivity.this,	ForgotPasswordActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(Constant.DELIVER_LOGIN_ACCOUNT,((EditText) findViewById(R.id.inputaccount)).getText().toString());
                intent.putExtras(bundle);
                startActivityForResult(intent, 0);
            }
        });

        //註冊
        ((Button) findViewById(R.id.goregisterpagebtn)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i(TAG, "goregisterpagebtn onClick()");
//				((Button) findViewById(R.id.goregisterpagebtn)).setBackgroundColor(getResources().getColor(R.color.click_color));
                Intent intent = new Intent(LoginActivity.this,	RegisterAccountActivity.class);
//				Bundle bundle = new Bundle();
//				bundle.putString(Constant.DELIVER_LOGIN_ACCOUNT,((EditText) findViewById(R.id.inputaccount)).getText().toString());
//				intent.putExtras(bundle);
//				startActivityForResult(intent, 0);
                startActivity(intent);
                ScreenManager.getScreenManager().pushActivity(LoginActivity.this);
            }
        });

        //清除 90 天前的資料
        BioDataAdapter bioDataAdapter = new BioDataAdapter(getApplicationContext());
        bioDataAdapter.delBefore90DaysData();
    }
    /*
    //顯示 AppInformation
    private void openInformationDialog(){
          RelativeLayout Layout1 = (RelativeLayout)findViewById(R.id.rlInformation);
          RelativeLayout Layout2 = (RelativeLayout)findViewById(R.id.relativeLayout1);
          Layout1.setVisibility(View.VISIBLE);
          Layout2.setVisibility(View.GONE);
    }
    //顯示登入畫面
    private void closeLoginDialog(){
          RelativeLayout Layout1 = (RelativeLayout)findViewById(R.id.rlInformation);
          RelativeLayout Layout2 = (RelativeLayout)findViewById(R.id.relativeLayout1);
          Layout1 .setVisibility(View.GONE);
          Layout2.setVisibility(View.VISIBLE);
    }
    */
    public void doLongin(){
        pd = ProgressDialog.show(LoginActivity.this, "登入中....", "系統登入中,需要較長時間等候.請稍待!!", true, false);
        //新建排程
        new Thread(){
            @Override
            public void run() {
                if (!SystemProperty.isNonVerifyTest()) {
                    Log.i(TAG, "!SystemProperty.isNonVerifyTest()");
                    showProgressDialog();
                }
            }
        }.start();
    }



    private UserAdapter useradapter;

    public void showProgressDialog(){
        Log.i(TAG, "LoginActivity showProgressDialog()");
        String account = ((EditText) findViewById(R.id.inputaccount)).getText().toString();
        String password = ((EditText) findViewById(R.id.inputpassword)).getText().toString();
        User user = new User();
        user.setUid(account);
        user.setPassword(password);
        NetService netService = new NetService();
        String response = netService.CallValidateUser(user);
        //String response = netService.CallUploadVitalSign(null, null, null, null);
        Log.i("info","response:" + response);
        Message msg = new Message();
        if(response!=null){
            try {
                JSONObject sonResponse = new JSONObject(response);
                String messageCode = sonResponse.getString("Message");

                // 成功
                if(messageCode.equals("A01")){
                    msg.what = 0;
                    msg.obj = messageCode;
                    handler.sendMessage(msg);
                    String type = sonResponse.getString("Type");
                    String unitName = sonResponse.getString("UnitName");
                    insertUserData(account, password, type, unitName);
                    Intent inetntLogin = new Intent(LoginActivity.this, MainActivity.class);

                    loginPassword = password;
//					pwdBundle.putString("pwd", loginPassword);

//					inetntLogin.putExtras(pwdBundle);

                    startActivity(inetntLogin);
                      finish();
                // 錯誤
                }else{
                    msg.what = 1;
                    msg.obj = messageCode;
                    handler.sendMessage(msg);
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.e(TAG, "JSONException : " + e);
                Log.e(TAG, "JSONException msg: " + e.getMessage());
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

    public void insertUserData(String account, String password, String type, String unitName){
        UserAdapter userAdapter = new UserAdapter(getApplicationContext());
        userAdapter.delAllUser();
        User user = new User();
        user.setUid(account);
        user.setType(type);
        user.setUnit(unitName);
        //CheckBox checkBox = (CheckBox) findViewById(R.id.checkBoxRememberUser);
        user.setRememberUser("1");
        user.setPassword(password);

        userAdapter.createtUser(user);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == Constant.REQUEST_CODE_SUCCESS) && (resultCode == RESULT_OK)) {
            Bundle bundle = data.getExtras();
            ((EditText) findViewById(R.id.inputaccount)).setText(bundle.getString(Constant.DELIVER_LOGIN_ACCOUNT));
            this.getAlertDialog("通知", bundle.getString(Constant.DELIVER_LOGIN_MESSAGE)).show();
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

          }
          if(msg.what==1){
              Log.i(TAG, "msg.what==1, msg.obj : " + msg.obj);

              if( msg.obj.equals("E01") )
              {

                  getAlertDialog("警告", "帳號不存在！").show();

              }else if( msg.obj.equals("E02") )
              {

                  getAlertDialog("警告", "密碼錯誤，身分驗證失敗！").show();

              }else if( msg.obj.equals("E99") )
              {

                  getAlertDialog("警告", "其他錯誤！").show();

              }
          }
          if(msg.what==2){
              Log.i(TAG, "msg.what==2");
              getAlertDialog("警告", getResources().getString(R.string.cannt_link_server)).show();
          }
          if(msg.what==3){
              //getAlertDialog("警告", msg.obj.toString()).show();
              // james modified
              getAlertDialog("警告",  getResources().getString(R.string.cannt_link_server2)).show();
          }
          //更新UI
         // statusTextView.setText("Completed!");
      }};

    //確認網路狀態
//  	public static boolean isMobileNetworkAvailable(Context con){
//  		if(null == connMgr){
//  			connMgr = (ConnectivityManager)con.getSystemService(Context.CONNECTIVITY_SERVICE);
//  		}
//  		NetworkInfo wifiInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//  		NetworkInfo mobileInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
//  		if(wifiInfo!=null && wifiInfo.isAvailable()){
//  			Log.i("info","wifiInfo!=null && wifiInfo.isAvailable()" );
//  			return true;
//  		}else if(mobileInfo!=null && mobileInfo.isAvailable()){
//  			Log.i("info","mobileInfo!=null && mobileInfo.isAvailable()" );
//  			return true;
//  		}else{
//  			return false;
//  		}
//  	}


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

    private AlertDialog getAlertDialog(String title, String message){
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

//	@Override
//    public void onResume() {
//        super.onResume();
//        ViewServer.get(this).setFocusedWindow(this);
//    }

//	@Override
//    public void onDestroy() {
//        super.onDestroy();
//        ViewServer.get(this).removeWindow(this);
//    }


//	@Override
//	protected void onDestroy()
//	{
//		 getAlertDialog("警告", getResources().getString(R.string.cannt_link_server)).dismiss();
//		//pd.dismiss();
//		super.onDestroy();
//	}



}
