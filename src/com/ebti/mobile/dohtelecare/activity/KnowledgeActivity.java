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
import android.net.Uri;
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
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class KnowledgeActivity extends Activity {
	public static final String TAG = "KnowledgeActivity";

	private static ConnectivityManager connMgr;

	private WebView myBrowser = null;
	private WebSettings browserSetting = null;
	private ProgressDialog pd = null;
	private Activity activity = this;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.knowledge);

		findViews();
		pd = new ProgressDialog(activity);
		//pd.setCancelable(false);
		//pd.setProgressStyle( ProgressDialog.STYLE_HORIZONTAL );
		
		// 放入Activity Stack
		ScreenManager.getScreenManager().pushActivity(this);
		/*
		TextView textViewUnit = (TextView) findViewById(R.id.userinformation);
		StringBuilder userInformation = new StringBuilder();
		UserAdapter userAdapter = new UserAdapter(getApplicationContext());
		com.ebti.mobile.dohtelecare.model.User user = userAdapter
				.getUIDUnitType();
		userInformation.append(user.getUid() + "\n");
		
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
		// 修改密碼
//		((Button) findViewById(R.id.changepasswordbutton))
//				.setOnClickListener(new View.OnClickListener() {
//					public void onClick(View v) {
//						Log.i(TAG, "changepasswordbutton onClick()");
//						Intent intent = new Intent(KnowledgeActivity.this,
//								ModifyPasswordActivity.class);
//						startActivityForResult(intent, 0);
//					}
//				});

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
										KnowledgeActivity.class);
						ScreenManager.getScreenManager().popActivity();

						Intent intent = new Intent(KnowledgeActivity.this,
								LoginActivity.class);
						startActivity(intent);

						// Intent intent = new
						// Intent(BloodPressureActivity.this,
						// OptionActivity.class);
						// startActivity(intent);
					}
				});

		// 返回
		((Button) findViewById(R.id.backbutton))
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						Log.i(TAG, "backbutton onClick()");
//						((Button) findViewById(R.id.backbutton)).setBackgroundColor(getResources().getColor(R.color.click_color));
						ScreenManager.getScreenManager()
								.popAllActivityExceptOne(
										KnowledgeActivity.class);
						ScreenManager.getScreenManager().popActivity();
					}
				});

		// 關於本軟體
//		((Button) findViewById(R.id.aboutappbutton))
//				.setOnClickListener(new View.OnClickListener() {
//					public void onClick(View v) {
//						Log.i(TAG, "aboutappbutton onClick()");
//						Intent intent = new Intent(KnowledgeActivity.this,
//								AboutAppActivity.class);
//						startActivity(intent);
//						// ScreenManager.getScreenManager().popActivity(KnowledgeActivity.this);
//					}
//				});

		setBrowserProperty();
		
		
		
		myBrowser.setWebViewClient(new WebViewClient() {
			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				Log.i("info", " web disconnect ");
				// loading error
				myBrowser.loadUrl(getString(R.string.webview_error_pageURL));
				pd.dismiss();
			}
			
		});
		
		myBrowser.setDownloadListener(new DownloadListener(){

			@Override
			public void onDownloadStart(String url, String userAgent,
					String contentDisposition, String mimetype,
					long contentLength) {
				// TODO Auto-generated method stub
				Log.i("info",url);
				Uri uri = Uri.parse(url);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri );
				startActivity(intent);
			}
			
		});
		
		myBrowser.setWebChromeClient(new WebChromeClient() {
			
			public void onProgressChanged(WebView view, int progress) {
//				pd.setProgress(0);
				//activity.setTitle("loading...");
				activity.setProgress(progress * 100);
				pd.setTitle( getString( R.string.webview_loading_title) );
				pd.setMessage(getString( R.string.webview_loading_body ));
				pd.show();
				Log.i("info", "progress:" + progress);
				//pd.incrementProgressBy(progress);
				if (progress == 100 /*&& pd.isShowing()*/ )
				{
					activity.setTitle(R.string.app_name);
					pd.dismiss();
//					
				}
					
			}
		});

		

			myBrowser.loadUrl("http://mohw.telecare.com.tw/PMOMobileApp/HealthInfo.aspx");
		
		
	}

	/*
	 * 設定瀏覽屬性
	 */
	private void setBrowserProperty() {

		browserSetting = myBrowser.getSettings();
		// 支援放大縮小
		browserSetting.setSupportZoom(true);
		// 顯示放踏大縮小工具
		browserSetting.setBuiltInZoomControls(false);
		// 支援javascript
		browserSetting.setJavaScriptEnabled(true);
		browserSetting.setSupportMultipleWindows(true);
		browserSetting.setJavaScriptCanOpenWindowsAutomatically(true);

	}

	private void findViews() {
		// TODO Auto-generated method stub
		myBrowser = (WebView) findViewById(R.id.knowledgeWV);
	}

	private AlertDialog getAlertDialog(String message) {
		// 產生一個Builder物件
		Builder builder = new AlertDialog.Builder(this);
		// 設定Dialog的標題
		builder.setIcon(R.drawable.alert_icon);
		builder.setTitle("警告");
		// 設定Dialog的內容
		builder.setMessage(message);
		// 設定Positive按鈕資料
		builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 按下按鈕時顯示快顯
			}
		});
		// 利用Builder物件建立AlertDialog
		return builder.create();
	}

	// 確認網路狀態
	public static boolean isMobileNetworkAvailable(Context con) {
		if (null == connMgr) {
			connMgr = (ConnectivityManager) con
					.getSystemService(Context.CONNECTIVITY_SERVICE);
		}
		NetworkInfo wifiInfo = connMgr
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo mobileInfo = connMgr
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (wifiInfo != null && wifiInfo.isAvailable()) {
			return true;
		} else if (mobileInfo != null && mobileInfo.isAvailable()) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * 按下back見回上頁，到第一頁則關閉app
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && myBrowser.canGoBack()) {
			myBrowser.goBack();
			return true;
		} else {
			Intent intent = new Intent(KnowledgeActivity.this, MainActivity.class);
			startActivity(intent);
		}
		return super.onKeyDown(keyCode, event);
	}
}
