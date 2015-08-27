package com.ebti.mobile.dohtelecare.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.ebti.mobile.dohtelecare.R;
import com.ebti.mobile.dohtelecare.activity.BloodGlucoseActivity;
import com.ebti.mobile.dohtelecare.activity.BloodPressureActivity;
import com.ebti.mobile.dohtelecare.activity.LoginActivity;
import com.ebti.mobile.dohtelecare.constant.Constant;
import com.ebti.mobile.dohtelecare.model.BioData;
import com.ebti.mobile.dohtelecare.sqlite.BioDataAdapter;
import com.ebti.mobile.dohtelecare.sqlite.UserAdapter;
import com.ebti.mobile.dohtelecare.util.ScreenManager;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
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
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class BloodGlucoseActivity extends Activity {
    public static final String TAG = "BloodGlucoseActivity";

    static final int RECORD_DATEPICKER = 10;

    Dialog recordDateTimeDialog;

    public static BloodGlucoseActivity instance = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bloodglucose);
        Log.i(TAG,"onCreate");

        instance = this;


        //放入Activity Stack
        ScreenManager.getScreenManager().pushActivity(this);

        //Mobile ID
        TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String imsi = mTelephonyMgr.getSubscriberId();
        String imei = mTelephonyMgr.getDeviceId();

        //預設為隨機血糖
        RadioButton rbUsually = (RadioButton) findViewById(R.id.usuallyradiobtn);
        rbUsually.setChecked(true);

        //====================Common========================
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

        //返回
        ((Button) findViewById(R.id.backbutton)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i(TAG, "backbutton onClick()");
//				((Button) findViewById(R.id.backbutton)).setBackgroundColor(getResources().getColor(R.color.click_color));
                ScreenManager.getScreenManager().popAllActivityExceptOne(BloodGlucoseActivity.class);
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

                ScreenManager.getScreenManager().popAllActivityExceptOne(BloodGlucoseActivity.class);
                ScreenManager.getScreenManager().popActivity();

                Intent intent = new Intent(BloodGlucoseActivity.this, LoginActivity.class);
                startActivity(intent);

                //Intent intent = new Intent(BloodPressureActivity.this, OptionActivity.class);
                //startActivity(intent);
            }
        });

        //關於本軟體
//  		((Button) findViewById(R.id.aboutappbutton)).setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
//				Log.i(TAG, "aboutappbutton onClick()");
//				Intent intent = new Intent(BloodGlucoseActivity.this, AboutAppActivity.class);
//				startActivity(intent);
//			}
//		});

        //修改密碼
//  		((Button) findViewById(R.id.changepasswordbutton)).setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
//				Log.i(TAG, "changepasswordbutton onClick()");
//				Intent intent = new Intent(BloodGlucoseActivity.this, ModifyPasswordActivity.class);
//				startActivityForResult(intent, 0);
//			}
//		});
        //====================common End===========================


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

    public void saveKeyInData(){
        Log.i(TAG, "uploadKeyInData()");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        //血糖類型
        RadioButton rbLimosis = (RadioButton) findViewById(R.id.limosisradiobtn);
        RadioButton rbAfterMeals = (RadioButton) findViewById(R.id.aftermealradiobtn);
        RadioButton rbUsually = (RadioButton) findViewById(R.id.usuallyradiobtn);

        //血糖值
        EditText etBloodGlucoseValue = (EditText) findViewById(R.id.editTextBloodGlucoseValue);
        EditText editTextRecordDateTime = (EditText) findViewById(R.id.editTextRecordDateTime);
        String recorderDateTime = editTextRecordDateTime.getText().toString();
        BioData bioData = new BioData();
        bioData.setDeviceTime(recorderDateTime);
        bioData.setDeviceType(Constant.BIODATA_DEVICE_TYPE_BLOOD_GLUCOSE);
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
        if(etBloodGlucoseValue.getText().toString().trim().equals("")){
            initToast("請輸入血糖數值");
            return;
        }else if(editTextRecordDateTime.getText().toString().trim().equals("")){
            initToast("請輸入量測時間");
            return;
        }else if(recordDate.after(new Date())){
            initToast("量測時間不能超過現在時間，\n請調整時間！");
            editTextRecordDateTime.setText(sdf.format(new Date()));
            return;
        }else if(rbLimosis.isChecked()){
            bioData.setAc(etBloodGlucoseValue.getText().toString());
        }else if(rbAfterMeals.isChecked()){
            bioData.setPc(etBloodGlucoseValue.getText().toString());
        }else if(rbUsually.isChecked()){
            //Toast.makeText(getApplicationContext(), "rbUsually", Toast.LENGTH_LONG).show();
            bioData.setNm(etBloodGlucoseValue.getText().toString());
        }else{
            initToast("請選擇血糖類型");
            return;
        }
        List<BioData> listioData = new ArrayList<BioData>();
        listioData.add(bioData);
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
        bioDataAdapter.createGlucose(bioData);
        getMessageDialog("訊息", "血糖資料已儲存").show();
        //清除edittext
        etBloodGlucoseValue.setText("");
        rbLimosis.setChecked(false);
        rbAfterMeals.setChecked(false);
        rbUsually.setChecked(true);
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
                recordDateTimeDialog = new Dialog(BloodGlucoseActivity.this);
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
