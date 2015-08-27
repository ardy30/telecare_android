/**
 * 
 */
package com.ebti.mobile.dohtelecare.activity;

import com.ebti.mobile.dohtelecare.R;
import com.ebti.mobile.dohtelecare.sqlite.UserAdapter;
import com.ebti.mobile.dohtelecare.util.ScreenManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author Administrator
 *
 */
public class ManualInputActivity extends Activity {
	public static final String TAG = "ManualInputActivity";
	public static ManualInputActivity instance = null;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manualinput);
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
//		TextView textViewUnit = (TextView) findViewById(R.id.userinformation);
//		StringBuilder userInformation = new StringBuilder();
//		UserAdapter userAdapter = new UserAdapter(getApplicationContext());
//		com.ebti.mobile.dohtelecare.model.User user = userAdapter
//				.getUIDUnitType();
//		userInformation.append(user.getUid() + "\n");
		/*
		if (user.getType().equals("Trial")) {
			userInformation.append(((String) getResources().getText(
					R.string.status_trial_user)));
		} else {
			userInformation.append(((String) getResources().getText(
					R.string.status_trsc_user))
					+ "(" + user.getUnit() + ")");
		}
		*/
		// set Information
//		textViewUnit.setText(userInformation.toString());

		// 返回
		((Button) findViewById(R.id.backbutton)).setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						Log.i(TAG, "backbutton onClick()");
//						((Button) findViewById(R.id.backbutton)).setBackgroundColor(getResources().getColor(R.color.click_color));
						ScreenManager.getScreenManager().popAllActivityExceptOne(ManualInputActivity.class);
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
										ManualInputActivity.class);
						ScreenManager.getScreenManager().popActivity();

						Intent intent = new Intent(ManualInputActivity.this,
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
//						Intent intent = new Intent(ManualInputActivity.this,
//								AboutAppActivity.class);
//						startActivity(intent);
//					}
//				});

		// 修改密碼
//		((Button) findViewById(R.id.changepasswordbutton))
//				.setOnClickListener(new View.OnClickListener() {
//					public void onClick(View v) {
//						Log.i(TAG, "changepasswordbutton onClick()");
//						Intent intent = new Intent(ManualInputActivity.this,
//								ModifyPasswordActivity.class);
//						startActivityForResult(intent, 0);
//					}
//				});
		// ====================common End===========================
        
        //點擊血壓
  		((Button) findViewById(R.id.bloodpressurebtn)).setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.i(TAG, "bloodglucosebtn onClick()");
				Intent intent = new Intent(ManualInputActivity.this, BloodPressureActivity.class);
				startActivity(intent);
			}
		});
  		
  		//點擊血糖
  		((Button) findViewById(R.id.bloodglucosebtn)).setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.i(TAG, "bloodglucosebtn onClick()");
				Intent intent = new Intent(ManualInputActivity.this, BloodGlucoseActivity.class);
				startActivity(intent);
			}
		});
    }
	
	
}
