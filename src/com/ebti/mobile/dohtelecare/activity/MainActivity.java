package com.ebti.mobile.dohtelecare.activity;

import java.util.List;

import com.ebti.mobile.dohtelecare.R;
import com.ebti.mobile.dohtelecare.constant.Constant;
import com.ebti.mobile.dohtelecare.gcm.CommonUtilities;
import com.ebti.mobile.dohtelecare.service.GetBlueToothDeviceDataService;
import com.ebti.mobile.dohtelecare.service.MainService;
import com.ebti.mobile.dohtelecare.sqlite.UserAdapter;
import com.ebti.mobile.dohtelecare.util.ScreenManager;
import com.google.android.gcm.GCMRegistrar;

import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
    public static final String TAG = "MainActivity";

    public static MainActivity instance = null;
    private String accountID;
    private String pwd;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        instance = this;

        //放入Activity Stack
          ScreenManager.getScreenManager().pushActivity(this);

        //Information textfield
          TextView textViewUnit = (TextView)findViewById(R.id.userinformation);
          StringBuilder userInformation = new StringBuilder();
          UserAdapter userAdapter = new UserAdapter(getApplicationContext());
          com.ebti.mobile.dohtelecare.model.User user = userAdapter.getUIDUnitType();
          accountID = user.getUid() ;
          user = userAdapter.getUserUIdAndPassword();
          pwd = user.getPassword();
          Log.i("info", "=user.getPassword()===:" + user.getPassword());

          userInformation.append(user.getUid() );


//  		Bundle getBundle = this.getIntent().getExtras();
//  		Log.i("info", "=====1=====getBundle:" + getBundle);

//  		if( getBundle != null )
//  		{
//  			Log.i("info", "=====2=====getBundle:" + getBundle.getString( "pwd" ));
//  			pwd = (getBundle.getString( "pwd" )).toString();
//  			Log.i("info", "=====pwd=====" + pwd);
//  		}

//  		if(user.getType().equals("Trial")){
//  			userInformation.append(((String)getResources().getText(R.string.status_trial_user)));
//  		}else{
//  			userInformation.append(((String)getResources().getText(R.string.status_trsc_user)) + "(" + user.getUnit() + ")");
//  		}

          //set Information
          textViewUnit.setText(userInformation.toString());

          //TextView textviewmessagearea = (TextView)findViewById(R.id.textviewmessagearea);
          //textviewmessagearea.setText("歡迎使用本軟體");

          //==========================common=========================

        //返回鍵隱藏
        //((Button) findViewById(R.id.backbutton)).setVisibility(4);

        //登出
        ((Button) findViewById(R.id.logoutbutton)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i(TAG, "logoutbutton onClick()");
                //清除資料
                UserAdapter userAdapter = new UserAdapter(getApplicationContext());
                userAdapter.delAllUser();

                LoginActivity.instance.finish();

                ScreenManager.getScreenManager().popAllActivityExceptOne(MainActivity.class);
                ScreenManager.getScreenManager().popActivity();

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);

                //Intent intent = new Intent(BloodPressureActivity.this, OptionActivity.class);
                //startActivity(intent);
            }
        });

        //修改密碼
          ((Button) findViewById(R.id.changepasswordbutton)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i(TAG, "changepasswordbutton onClick()");
                    // 跳至修改密碼activity
                Intent intent = new Intent(MainActivity.this, ModifyPasswordActivity.class);
                startActivityForResult(intent, 0);
            }
        });

          //關於本軟體
//  		((Button) findViewById(R.id.aboutappbutton)).setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
//				Log.i(TAG, "aboutappbutton onClick()");
//				Intent intent = new Intent(MainActivity.this, AboutAppActivity.class);
//				startActivity(intent);
//			}
//		});
        //====================common End===========================
        // 點擊健康新知
        ((Button) findViewById(R.id.health_knowledgebtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, KnowledgeActivity.class);
                startActivity(intent);
            }
        });

        // 點擊最新消息
        ((Button) findViewById(R.id.news_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NewsActivity.class);
                startActivity(intent);
            }
        });

        // 點擊 服務據點
        ((Button) findViewById(R.id.locationbtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 檢測GPS是否開啟
                    LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if( !manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("訊息");
                    builder.setMessage("此功能需使用GPS，請先開啓GPS功能。");
                    builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(i);

                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               //No location service, no Activity

                           }
                       });
                       builder.create().show();
                }
                else
                {
                    Intent intent = new Intent(MainActivity.this, LocationActivity.class);
                    startActivity(intent);
                }


            }
        });

        // 點擊 意見回饋
        ((Button) findViewById(R.id.opinionbtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                Intent intent = new Intent(MainActivity.this, OpinionActivity.class);
                bundle.putString("id", accountID);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        //點擊自動量測上傳彈出提示視窗
          ((Button) findViewById(R.id.autoupdatebtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMessageDialog("訊息","請先開啟手機藍芽功能，血壓/血糖量測完畢後，將量測設備切換至藍芽功能並靠近手機，量測資訊將自動上傳紀錄。").show();
            }
        });

          //點擊手動輸入上傳
          ((Button) findViewById(R.id.manualinputbtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.i(TAG, "bloodglucosebtn onClick()");
                Intent intent = new Intent(MainActivity.this, ManualInputActivity.class);
                startActivity(intent);
            }
        });

          //點擊雲端量測資訊查詢
          ((Button) findViewById(R.id.queryclouddatabtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.i(TAG, "bloodglucosebtn onClick()");
                Intent intent = new Intent(MainActivity.this, QueryRecordDataActivity.class);
                startActivity(intent);
            }
        });

          //點擊本機量測紀錄查詢
          ((Button) findViewById(R.id.querylocaldatabtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.i(TAG, "bloodglucosebtn onClick()");
                Intent intent = new Intent(MainActivity.this, QueryLocalDataActivity.class);
                startActivity(intent);
            }
        });


          // 點擊提示設定
          ((Button) findViewById(R.id.notifybtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent();
//				if( accountID != null || pwd != null )
//				{
                    Bundle bundle = new Bundle();
                    bundle.putString("id", accountID);
                    bundle.putString("pwd", pwd);
                    intent.putExtras(bundle);

//				}
                Log.i("info", "send ID:" + accountID + "/ pwd:" + pwd);
                intent.setClass(MainActivity.this, HintSetActivity.class);
                startActivity(intent);
            }
        });


          //藍芽設備自動讀取, 資料自動上傳
          boolean isStartBlueTooth = isServiceRunning(getApplicationContext(), GetBlueToothDeviceDataService.class.getName());
        Log.i(TAG, " isStartBlueTooth : " + isStartBlueTooth );
          if(!isStartBlueTooth){
                Intent inetnt= new Intent(MainActivity.this, GetBlueToothDeviceDataService.class);
                startService(inetnt);
        }

        // james

        if (BluetoothAdapter.getDefaultAdapter().isEnabled()){
            boolean isStartMainService = isServiceRunning(getApplicationContext(), MainService.class.getName());
            if(!isStartMainService){
                startMainService();
            }
        }




    }

    protected void onDestroy(){
            Log.i(TAG, "onDestroy()");
            boolean isStartBlueTooth = isServiceRunning(getApplicationContext(), GetBlueToothDeviceDataService.class.getName());
        if(isStartBlueTooth){
            Intent inetnt= new Intent(MainActivity.this, GetBlueToothDeviceDataService.class);
            stopService(inetnt);
        }

        // james
        if (BluetoothAdapter.getDefaultAdapter().isEnabled()){
            boolean isStartMainService = isServiceRunning(getApplicationContext(), MainService.class.getName());
            if(isStartMainService){
                stopMainService();
            }
        }

        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult(), requestCode : " + requestCode + ", resultCode : " + resultCode);
        if ((requestCode == Constant.REQUEST_CODE_SUCCESS) && (resultCode == RESULT_OK)) {
            Log.i(TAG, "show message");
            Bundle bundle = data.getExtras();
            this.getMessageDialog("通知", bundle.getString(Constant.DELIVER_LOGIN_MESSAGE)).show();
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

    /**
     * 用来判断服务是否运行.
     * @param context
     * @param className 判断的服务名字：包名+类名
     * @return true 在运行, false 不在运行
     */
    public static boolean isServiceRunning(Context context,String className) {
        boolean isRunning = false;
        Log.i(TAG, " className : " +className);
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

    //YMU modify
    public void startMainService()
    {
            Intent i = new Intent(MainActivity.this,MainService.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            ComponentName CN = startService(i);
            Log.e( TAG, "ComponentName CN:" + CN );
            if ( CN == null )
            {
                Log.e(TAG, "Start Bluetooth Server Failed");
            }
            else
            {
                Log.e(TAG, "Start Bluetooth Server Succeed");
            }
    }

    //YMU
    public void stopMainService()
    {
            Intent i = new Intent(MainActivity.this,MainService.class);
            //stop the Service
            if (stopService(i))
            {
                Log.e(TAG, "stopService is true");
            }
            else
            {
                Log.e(TAG, "stopService is false");
            }

    }

}
