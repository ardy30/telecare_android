package com.ebti.mobile.dohtelecare.activity;

import static com.ebti.mobile.dohtelecare.gcm.CommonUtilities.DISPLAY_MESSAGE_ACTION;

import java.io.IOException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ebti.mobile.dohtelecare.util.ConnectionDetector;

import com.ebti.mobile.dohtelecare.R;
import com.ebti.mobile.dohtelecare.constant.Constant;
import com.ebti.mobile.dohtelecare.gcm.CommonUtilities;
import com.ebti.mobile.dohtelecare.helper.NetService;
import com.ebti.mobile.dohtelecare.service.GetBlueToothDeviceDataService;
import com.ebti.mobile.dohtelecare.service.MainService;
import com.ebti.mobile.dohtelecare.sqlite.UserAdapter;
import com.ebti.mobile.dohtelecare.util.ScreenManager;
import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings.Secure;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

public class HintSetActivity extends Activity {
    public static final String TAG = "HintSetActivity";
    public static HintSetActivity instance = null;
    private NetService netService = null;
    private Context  context = null;
    private Bundle getBundle = null;
    private Activity activity = this;


    private Button hintSetBtn = null;
    private CheckBox excepCB = null;
    private CheckBox noMeasureCB = null;
    private ProgressDialog pd = null;

    // 異常通知boolean 勾(true) 未勾(false)
    private Boolean exceptBool = false;
    // 未量測通知boolean 勾(true) 未勾(false)
    private Boolean noMeasureBool = false;
    private String notMeasureDay = "10";
    private String regId = "";
    private String loginID = "";
    private String pwd = "";
    private String initialNotifyResponse = "";
    private String setNotifyResponse = "";

    private GoogleCloudMessaging gcm;
    private GcmRegTask gcmRegTask;

    private ConnectionDetector cd ;

    // 初始進入取得設定值handler
    private final Handler getInitialServiceHandler =  new Handler(Looper.getMainLooper()) {
            @Override
        public void handleMessage(Message msg)
            {
                // 取得json為null or ""
                if (msg.what == 1)
                {
                    Log.i("info", "取得json為null or empty " );
                    getAlertDialog( getString(R.string.app_name) , getString(R.string.hint_loading_error) ).show();
                    pd.dismiss();
            }
                // parse notify preferences
                else if( msg.what == 2 )
                {
                    parseInitialNotifyPreferencs();
                     setCheckBoxPreferences();
                    pd.dismiss();
                }
            }

    };


    // 設定提示設定handler
    private final Handler setHintValueHandler =  new Handler(Looper.getMainLooper()) {
            @Override
        public void handleMessage(Message msg)
            {
                // 取得json為null or ""
                if (msg.what == 1)
                {
                    Log.i("info", "取得json為null or empty " );
                    getAlertDialog( getString(R.string.app_name) , getString(R.string.hint_setting_error) ).show();
                    pd.dismiss();
                    pd = null;
                }
                // parse notify preferences
                else if( msg.what == 2 )
                {
                    parseSetHintResult();
                    pd.dismiss();
                    pd = null;
                }
            }



    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifyset);

        instance = this;
        context = getApplicationContext();

        //放入Activity Stack
          ScreenManager.getScreenManager().pushActivity(this);

          // get loginId from mainActivity.java
          getBundle = this.getIntent().getExtras();
          loginID = (getBundle.getString( "id" )).toString();
          pwd = (getBundle.getString( "pwd" )).toString();


          cd = new ConnectionDetector(getApplicationContext());
          Log.i("info", "check network:" + cd.isConnectingToInternet() );

          findViews();

          netService = new NetService();
          checkBoxListener();
          if( cd.isConnectingToInternet() == true )
          {
              pd = new ProgressDialog(activity);
              pd = ProgressDialog.show( HintSetActivity.this , getString(R.string.webview_loading_title), getString(R.string.reg_loadArea), true, false);
              new Thread()
              {
                 @Override
                public void run()
                {

                     getInitNotifyPreferences();
                }
              }.start();




            //runGCM();
          }
          else
          {
              getAlertDialog( getString(R.string.app_name) , getString(R.string.hint_nonetwork_error) ).show();
          }


        hintSetBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.i("info" , "異常通知:" + exceptBool + "/ 未量測通知:" + noMeasureBool );
                if( cd.isConnectingToInternet() == true )
                {
                    if( regId == "" )
                    {
                        unregisterTask();
                        runGCM();
                    }
                    else
                    {
                        sendValueToService();
                    }

                }
                else
                {
                    getAlertDialog( getString(R.string.app_name) , getString(R.string.hint_nonetwork_error) ).show();
                }
            }});

        // 返回
        ((Button) findViewById(R.id.backbutton))
                .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Log.i(TAG, "backbutton onClick()");
//								((Button) findViewById(R.id.backbutton)).setBackgroundColor(getResources().getColor(R.color.click_color));
                        ScreenManager.getScreenManager()
                                .popAllActivityExceptOne(
                                        HintSetActivity.class);
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
                                        OpinionActivity.class);
                        ScreenManager.getScreenManager().popActivity();

                        Intent intent = new Intent(HintSetActivity.this,
                                LoginActivity.class);
                        startActivity(intent);

                        // Intent intent = new
                        // Intent(BloodPressureActivity.this,
                        // OptionActivity.class);
                        // startActivity(intent);
                    }
                });



    }


    // 送 設定值 to webservices
    private void sendValueToService() {
        pd = new ProgressDialog(activity);
          pd = ProgressDialog.show( HintSetActivity.this , getString(R.string.webview_loading_title), getString(R.string.reg_loadArea), true, false);
          new Thread()
          {
             @Override
            public void run()
            {
                 setHintValuePreferences();
                 //getInitNotifyPreferences();
            }
          }.start();
    }


    private void checkBoxListener() {
//	    	excepCB.setOnCheckedChangeListener(listener);
//	    	noMeasureCB.setOnCheckedChangeListener(listener);
    }


    private void setCheckBoxPreferences() {
            Log.i("info", "setCheckBoxPreferences");
            Log.i("info", "exceptBool: " + exceptBool +"/noMeasureBool:" + noMeasureBool);

//		excepCB.setChecked( exceptBool );
//		noMeasureCB.setChecked( noMeasureBool );
//    		exceptBool = true;
//    		noMeasureBool =true;
            noMeasureCB.setChecked( noMeasureBool );
        excepCB.setChecked( exceptBool );
        Log.i("info", "noMeasureBool==?" + noMeasureBool );

        noMeasureCB.setText( notMeasureDay+"天未量測通知");
    }

    private void findViews() {
        // TODO Auto-generated method stub
            hintSetBtn = (Button) findViewById(R.id.setBtn);
            excepCB = (CheckBox) findViewById(R.id.excepCB);
            noMeasureCB = (CheckBox) findViewById(R.id.noMeasureCB);
    }


    private void setHintValuePreferences()
    {

        if( excepCB.isChecked() == true )
        {
            exceptBool =  true;
        }
        else
        {
            exceptBool = false;
        }

        if( noMeasureCB.isChecked() == true)
        {
            noMeasureBool = true;
        }
        else
        {
            noMeasureBool = false;
        }

        // userID,  platform,  key,  unusal,  notMeasure
        setNotifyResponse = netService.SetNotifyPreferences( loginID, pwd, Constant.PLATFORM, regId, exceptBool, noMeasureBool );
        Log.i("info", "setNotifyResponse:" + setNotifyResponse);

        if( setNotifyResponse == null | setNotifyResponse == "")
        {
            setHintValueHandler.sendMessage(setHintValueHandler.obtainMessage(1));
        }
        else
        {
            setHintValueHandler.sendMessage(setHintValueHandler.obtainMessage(2));
        }
    }

    private void parseSetHintResult() {

        try
        {
            JSONObject jsonObj = new JSONObject(setNotifyResponse);
            Log.i("info", jsonObj.getString("Message") + "/" + jsonObj.getString("Description") );
            if( jsonObj.getString("Message").equals( Constant.MessageCodeSuccess ) )
            {
                getAlertDialog( getString(R.string.app_name) , getString(R.string.hint_setting_success) ).show();
            }
            else
            {
                Log.e("info", "parse set notify response message not A01");
                getAlertDialog( getString(R.string.app_name) , jsonObj.getString("Description")).show();
            }
        }
        catch (JSONException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e("info", "parse set notify response json error:" + e.getMessage() );
            getAlertDialog( getString(R.string.app_name) , getString(R.string.hint_setting_error) ).show();
        }

    }


    private void getInitNotifyPreferences()
    {
        initialNotifyResponse = netService.GetNotifyPreferences(loginID, pwd);
        if( initialNotifyResponse == null | initialNotifyResponse == "")
        {
            getInitialServiceHandler.sendMessage(getInitialServiceHandler.obtainMessage(1));
        }
        else
        {
            getInitialServiceHandler.sendMessage(getInitialServiceHandler.obtainMessage(2));
        }

    }

    private void parseInitialNotifyPreferencs() {
        // TODO Auto-generated method stub

        try {
            JSONObject jsonObj = new JSONObject( initialNotifyResponse );
            Log.i("info", "Message:" + jsonObj.getString("Message") );
            Log.i("info", "initialNotifyResponse:" + initialNotifyResponse );
            if( jsonObj.getString("Message").equals(Constant.MessageCodeSuccess) )
            {
                exceptBool = Boolean.parseBoolean( jsonObj.getString("UnusualNotify") );
                noMeasureBool =  Boolean.parseBoolean( jsonObj.getString("NotMeasureNotify") );

                notMeasureDay =   jsonObj.getString("NotMeasureDay") ;

            }
            else
            {
                Log.i("info", "error");
                getAlertDialog( getString(R.string.app_name) ,jsonObj.getString("Description") ).show();
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.i("info", "error: " + e.getMessage() );
            getAlertDialog( getString(R.string.app_name) , getString(R.string.hint_loading_error) ).show();
        }

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


//	private CheckBox.OnCheckedChangeListener listener = new CheckBox.OnCheckedChangeListener()
//	{
//
//
//		@Override
//		public void onCheckedChanged(CompoundButton buttonView,
//				boolean isChecked) {
//			// TODO Auto-generated method stub
//			Log.i("info"," = =  = = = 11111");
//			if( excepCB.isChecked() == true )
//			{
//
//				exceptBool = true;
//			}
//			else
//			{
//				exceptBool = false;
//			}
//
//			if( noMeasureCB.isChecked() == true)
//			{
//				noMeasureBool = true;
//			}
//			else
//			{
//				noMeasureBool = false;
//			}
//
//		}
//
//
//	};



    private void runGCM()
    {
           // gcm
        GCMRegistrar.checkDevice(this);
        GCMRegistrar.checkManifest(this);
        String android_id = Secure.getString(this.getBaseContext().getContentResolver(), Secure.ANDROID_ID);
         regId = GCMRegistrar.getRegistrationId(this);

        Log.i("info", "android_id:" + android_id);
        if( gcmRegTask != null )
        {
                gcmRegTask.cancel(true);
                gcmRegTask = null;
        }
        gcmRegTask =  new GcmRegTask();
        gcmRegTask.execute(null,null,null);
    }



    private class GcmRegTask extends AsyncTask<Void, String, String>
    {

        @Override
        protected String doInBackground(Void...params)
        {
            String msg = "";
            try{

                if( gcm == null )
                {
                     gcm = GoogleCloudMessaging.getInstance(context);
                }
                  if( regId.equals("") )
                    {
                       regId = gcm.register(CommonUtilities.SENDER_ID);
                            //GCMRegistrar.register(this, CommonUtilities.SENDER_ID);
                            Log.i("info", "reg ID:" + regId);
                    }
                    else
                    {
                            Log.i("info", "已經註冊過了 reg ID:" + regId);
                            Log.i("info", "已經註冊過了");
                    }
                    registerReceiver(mHandleMessageReceiver, new IntentFilter(
                            DISPLAY_MESSAGE_ACTION));

            }catch(IOException e)
            {
                msg = e.getMessage();
            }
            return msg;
        }

        @Override
        protected void onPostExecute(String msg)
        {
            sendValueToService();
            Log.i("info", " ============ get registered id ==========");
        }

    }


    @Override
    protected void onDestroy()
    {

        unregisterTask();
        super.onDestroy();
    }

    private void unregisterTask()
    {
        if( gcmRegTask != null )
        {
            gcmRegTask.cancel(true);
            unregisterReceiver(mHandleMessageReceiver);
        }
    }




    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bd_GCM = intent.getExtras();
            if (bd_GCM != null) {
//				String sGCM_Data = "name:" + bd_GCM.get("name").toString()
//						+ "\n";
//				sGCM_Data += "age:" + bd_GCM.get("age").toString() + "\n";
//
//				mTextView.setText(sGCM_Data);
            }
        }
    };

}
