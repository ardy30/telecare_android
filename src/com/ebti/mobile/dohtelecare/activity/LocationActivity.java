package com.ebti.mobile.dohtelecare.activity;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import com.ebti.mobile.dohtelecare.R;
import com.ebti.mobile.dohtelecare.constant.Constant;
import com.ebti.mobile.dohtelecare.constant.SystemProperty;
import com.ebti.mobile.dohtelecare.helper.NetService;
import com.ebti.mobile.dohtelecare.model.AreaData;
import com.ebti.mobile.dohtelecare.model.CoordinatesData;
import com.ebti.mobile.dohtelecare.model.User;
import com.ebti.mobile.dohtelecare.respository.AreaDataRepo;
import com.ebti.mobile.dohtelecare.respository.CoordinatesRepo;
import com.ebti.mobile.dohtelecare.service.GetBlueToothDeviceDataService;
import com.ebti.mobile.dohtelecare.sqlite.UserAdapter;
import com.ebti.mobile.dohtelecare.util.ScreenManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LocationActivity extends FragmentActivity implements
		ConnectionCallbacks, OnConnectionFailedListener, LocationListener,
		OnMyLocationButtonClickListener {
	public static final String TAG = "KnowledgeActivity";

	private static ConnectivityManager connMgr;

	private WebSettings browserSetting = null;
	private ProgressDialog pd = null;
	private Activity activity = this;

	private NetService netService = null;
	private String marksResponse;

	private final static String INFO = "info";
	private GoogleMap mMap;
	private LocationClient mLocationClient;
	private LatLng tmhtPoint;
	// private Marker marker;
	private List<Marker> markers = new ArrayList<Marker>();
	private Timer timer = new Timer();

	private FragmentManager fragmentManager = getSupportFragmentManager();

	private double myLocationLat = 0, myLocationLong = 0;

	private CameraPosition cameraPosition;
	private CameraUpdate cameraUpdate;

	private double minDistanceLat = 0.0;
	private double minDistanceLong = 0.0;
	private Boolean isMarkInfoWindowOpen = false;

	// caculator min distance
	private double minVal = 0.0;
	private int remidx = 0;

	private ArrayList<CoordinatesData> coorList = null;
	// These settings are the same as the settings for the map. They will in
	// fact give you updates
	// at the maximal rates currently possible.
	private static final LocationRequest REQUEST = LocationRequest.create()
			.setInterval(15000) // 20 seconds
			.setFastestInterval(15000) // 16ms = 60fps
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	// my locaiton distance between with service location 6 kilemeter
	private int diatanceBetweenMyLocation = 10;

	private TextView minDistance = null;
	private TextView minDistanceAddress = null;

	private final Handler markersHandler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				Log.i("info", "get data");
				setMaps();
				pd.dismiss();
			} else if (msg.what == 2) {

				pd.dismiss();

				// getAlertDialog( getString(R.string.reg_catchAreaDataErr),
				// "RegisterAccountActivity" ).show();

			} else if (msg.what == 3) {
				pd.dismiss();
				// getAlertDialog(
				// getString(R.string.reg_jsonParseErr),"RegisterAccountActivity"
				// ).show();
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location);
		Log.i("info", "onCreate start");
		findViews();

		// 放入Activity Stack
		ScreenManager.getScreenManager().pushActivity(this);
		/*
		 * TextView textViewUnit = (TextView)
		 * findViewById(R.id.userinformation); StringBuilder userInformation =
		 * new StringBuilder(); UserAdapter userAdapter = new
		 * UserAdapter(getApplicationContext());
		 * com.ebti.mobile.dohtelecare.model.User user = userAdapter
		 * .getUIDUnitType(); userInformation.append(user.getUid() + "\n");
		 * 
		 * if (user.getType().equals("Trial")) {
		 * userInformation.append(((String) getResources().getText(
		 * R.string.status_trial_user))); } else {
		 * userInformation.append(((String) getResources().getText(
		 * R.string.status_trsc_user)) + "(" + user.getUnit() + ")"); }
		 * 
		 * // set Information textViewUnit.setText(userInformation.toString());
		 */
		// 修改密碼
		// ((Button) findViewById(R.id.changepasswordbutton))
		// .setOnClickListener(new View.OnClickListener() {
		// public void onClick(View v) {
		// Log.i(TAG, "changepasswordbutton onClick()");
		// Intent intent = new Intent(KnowledgeActivity.this,
		// ModifyPasswordActivity.class);
		// startActivityForResult(intent, 0);
		// }
		// });

		// if( checkInternet() == false )
		// {
		// getAlertDialog("此功能需使用網路功能，建議您先開啟網路再使用").show();
		// }
		// else
		// {
		// downloadMarks();
		// }
		//

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

						ScreenManager
								.getScreenManager()
								.popAllActivityExceptOne(LocationActivity.class);
						ScreenManager.getScreenManager().popActivity();

						Intent intent = new Intent(LocationActivity.this,
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
						// ((Button)
						// findViewById(R.id.backbutton)).setBackgroundColor(getResources().getColor(R.color.click_color));
						Intent intent = new Intent();
						intent.setClass(LocationActivity.this,
								MainActivity.class);
						startActivity(intent);

						// ScreenManager.getScreenManager()
						// .popAllActivityExceptOne(
						// LocationActivity.class);
						// ScreenManager.getScreenManager().popActivity();
					}
				});

		((Button) findViewById(R.id.locationListBtn))
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent();
						intent.setClass(LocationActivity.this,
								LocationListActivity.class);
						startActivity(intent);
					}
				});

		// 關於本軟體
		// ((Button) findViewById(R.id.aboutappbutton))
		// .setOnClickListener(new View.OnClickListener() {
		// public void onClick(View v) {
		// Log.i(TAG, "aboutappbutton onClick()");
		// Intent intent = new Intent(KnowledgeActivity.this,
		// AboutAppActivity.class);
		// startActivity(intent);
		// //
		// ScreenManager.getScreenManager().popActivity(KnowledgeActivity.this);
		// }
		// });

		Log.i("info", "onCreate middle");
		// if (checkGooglePlayServices() == ConnectionResult.SUCCESS) {
		// setUpMapIfNeeded();
		// }

		minDistanceAddress.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (minDistanceLat != 0.0 | minDistanceLong != 0.0) {
					setCameraToUserPoint(minDistanceLat, minDistanceLong);
				}
			}
		});

		Log.i("info", "onCreate end");
	}

	private void downloadMarks() {
		// TODO Auto-generated method stub
		pd = new ProgressDialog(activity);
		pd = ProgressDialog.show(LocationActivity.this,
				getString(R.string.webview_loading_title),
				getString(R.string.reg_loadArea), true, false);
		new Thread() {
			@Override
			public void run() {

				netService = new NetService();
				getMarksData();

			}
		}.start();
	}

	private void findViews() {
		// TODO Auto-generated method stub
		minDistance = (TextView) findViewById(R.id.minDistancePoint);
		minDistanceAddress = (TextView) findViewById(R.id.minDistanceAddress);
	}

	private void setMaps() {
		if (checkGooglePlayServices() == ConnectionResult.SUCCESS) {
			setMarksDataToRepo();
			setUpMapIfNeeded();
		}
	}

	private int checkGooglePlayServices() {
		int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

		switch (result) {
		case ConnectionResult.SUCCESS:
			Log.i(INFO, "SUCCESS");
			break;

		case ConnectionResult.SERVICE_INVALID:
			Log.i(INFO, "SERVICE_INVALID");
			GooglePlayServicesUtil.getErrorDialog(
					ConnectionResult.SERVICE_INVALID, this, 0).show();
			break;

		case ConnectionResult.SERVICE_MISSING:
			Log.i(INFO, "SERVICE_MISSING");
			GooglePlayServicesUtil.getErrorDialog(
					ConnectionResult.SERVICE_MISSING, this, 0).show();
			break;

		case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
			Log.i(INFO, "SERVICE_VERSION_UPDATE_REQUIRED");
			GooglePlayServicesUtil.getErrorDialog(
					ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED, this, 0)
					.show();
			break;

		case ConnectionResult.SERVICE_DISABLED:
			Log.i(INFO, "SERVICE_DISABLED");
			GooglePlayServicesUtil.getErrorDialog(
					ConnectionResult.SERVICE_DISABLED, this, 0).show();
			break;

		}
		return result;
	}

	private void getMarksData() {
		marksResponse = netService.GetMarksList();
		if (marksResponse.equals("")) {
			Log.i("info", "marksResponse == \"\" ");
			markersHandler.sendMessage(markersHandler.obtainMessage(2));
		} else if (marksResponse == null) {
			// areaRepo = new AreaDataRepo();
			Log.i("info", "marksResponse:" + marksResponse);
			// getAreaList = new ArrayList<AreaData>();
			// getAreaList = areaRepo.getAreaDataList(areaResponse );
			// if( getAreaList == null )
			// {
			// areaHandler.sendMessage( areaHandler.obtainMessage(3) );
			// }
		} else {
			markersHandler.sendMessage(markersHandler.obtainMessage(1));
		}

		Log.i("info", marksResponse);
	}

	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (mMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			mMap = ((SupportMapFragment) fragmentManager
					.findFragmentById(R.id.map)).getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				Log.i("info", "set marks");
				initPoints();

				// setupMarks();
				mMap.setInfoWindowAdapter(new MyInfoWindowAdapter());

				MyMarkerListener myMarkerListener = new MyMarkerListener();
				mMap.setOnMarkerClickListener(myMarkerListener);

				// marker點擊後再點擊infowindow事件
				// mMap.setOnInfoWindowClickListener(myMarkerListener);

				mMap.setMyLocationEnabled(true);
				mMap.setOnMyLocationButtonClickListener(this);
			}
		}
	}

	private void initPoints() {
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		String provider = locationManager.getBestProvider(criteria, true);
		
		Location myLocation = locationManager.getLastKnownLocation(provider);
		if( myLocation != null )
		{
			tmhtPoint = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
			setCameraToUserPoint(myLocation.getLatitude(), myLocation.getLongitude());
		}
		else
		{
			tmhtPoint = new LatLng(25.037744, 121.540933);
			setCameraToUserPoint(25.037744, 121.540933);
		}
	}

	private void setMarksDataToRepo() {
		CoordinatesRepo coorRepo = new CoordinatesRepo();
		coorList = new ArrayList<CoordinatesData>();
		coorList = coorRepo.getCoordinateData(marksResponse);
		if (coorList.size() == 0) {
			getAlertDialog("未抓取到據點資料").show();
		}
		diatanceBetweenMyLocation = SystemProperty.getInstance().MIN_DISTANCE;
	}

	private void setupMarks(double myLat, double myLong) {
		// TODO Auto-generated method stub
		Log.i("info", "==========setupMarks=========");
		int minimumIdx = 0;

		int showMarksNum = 0;

		if (markers.size() != 0) {
			removeAllMarkers();
			// markers.get(i).remove();
		}

		Log.i("info", "coorList:" + coorList  + " coorList != null : " + (coorList != null ) );
		if ( (coorList != null)  == true) {
			
			Log.i("info", "coorList.size():" + coorList.size());

			for (int i = 0; i < coorList.size(); i++) {
				double distanceResult = caclBetweenDistance(myLat, myLong,
						coorList.get(i).getLatLng().latitude, coorList.get(i)
								.getLatLng().longitude);

				minimumIdx = checkMinDisatancePoint(distanceResult, i);

				if (distanceResult < diatanceBetweenMyLocation) {

					// Marker marker;

					// if( markers.size() != 0 )
					// {
					// markers.get(i).remove();
					// }

					Log.i("info", "=====title:"
							+ coorList.get(i).getCoorTitle() + "\n" + "  ==="
							+ coorList.get(i).getLatLng().latitude + "/"
							+ coorList.get(i).getLatLng().longitude);
					Marker marker = mMap.addMarker(new MarkerOptions()
							.position(coorList.get(i).getLatLng())
							.title(coorList.get(i).getCoorTitle())
							.snippet(coorList.get(i).getSinppet()));
					markers.add(marker);
					// mMap.addMarker(
					// new MarkerOptions().position(
					// coorList.get(1).getLatLng())
					// .title(coorList.get(1).getCoorTitle())
					// .snippet(coorList.get(1).getSinppet()));
					showMarksNum++;
				}
				// Log.i("info", "markers:" + markers.get(i) );
				// markers.get(i).setVisible(false);
			}

			if (showMarksNum == 0) {
				for (int i = 0; i < coorList.size(); i++) {
					Marker marker = mMap.addMarker(new MarkerOptions()
							.position(coorList.get(i).getLatLng())
							.title(coorList.get(i).getCoorTitle())
							.snippet(coorList.get(i).getSinppet()));
					markers.add(marker);
				}
			}

			minDistance.setText(coorList.get(minimumIdx).getCoorTitle());
			minDistanceAddress.setText(coorList.get(minimumIdx).getAddress());
			minDistanceLat = coorList.get(minimumIdx).getLatLng().latitude;
			minDistanceLong = coorList.get(minimumIdx).getLatLng().longitude;

			minVal = 0;
			remidx = 0;
			Log.i("info", "最小距離地點:" + coorList.get(minimumIdx).getCoorTitle());

		}
	}

	private void removeAllMarkers() {
		for (int i = 0; i < markers.size(); i++) {
			markers.get(i).remove();
		}
	}

	private int checkMinDisatancePoint(double res, int idx) {

		BigDecimal data1 = new BigDecimal(minVal);
		BigDecimal data2 = new BigDecimal(res);

		Log.i("info", " before compareTo:" + data1.compareTo(data2)
				+ "/ minVal:" + minVal + "/ res:" + res + "/remidx:" + remidx);
		if (minVal == 0) {
			minVal = res;
		} else if ((data2.compareTo(data1)) == -1) {

			minVal = res;
			remidx = idx;
			Log.i("info", "******compareTo result => -1 / remidx: " + remidx);
		}
		Log.i("info", " after compareTo:" + data1.compareTo(data2)
				+ "/ minVal:" + minVal + "/ res:" + res + "/remidx:" + remidx);
		// Log.i("info", "compareTo:" + data1.compareTo(data2));
		// Log.i("info", "minVal:" + minVal + " res:" + res + "/" + "idx:" + idx
		// + "remidx: " + remidx);
		return remidx;
	}

	/**
	 * 計算兩點之間距離
	 * 
	 * @param startLat
	 *            起始位置緯度
	 * @param startLong
	 *            起始位置經度
	 * @param endLat
	 *            終點位置緯度
	 * @param endLong
	 *            終點位置經度
	 * @return 回傳之間距離(公里)
	 */
	private double caclBetweenDistance(double startLat, double startLong,
			double endLat, double endLong) {
		float[] results = new float[1];
		Location.distanceBetween(startLat, startLong, endLat, endLong, results);
		Log.i("info", " result:" + (results[0] / 1000));

		// meter convert to kilometer unit
		return ((double) results[0] / 1000);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mLocationClient != null) {
			mLocationClient.disconnect();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i("info", "onResume()");
		if (checkInternet() == false) {
			getAlertDialog("此功能需使用網路功能，建議您先開啟網路再使用").show();
		} else {
			Log.i("info", "coorList:" + coorList);
			if (coorList == null) {
				downloadMarks();
				// setUpMapIfNeeded();
				setUpLocationClientIfNeeded();
				mLocationClient.connect();
			}
		}

	}

	private void setUpLocationClientIfNeeded() {
		if (mLocationClient == null) {
			mLocationClient = new LocationClient(getApplicationContext(), this, // ConnectionCallbacks
					this); // OnConnectionFailedListener
		}
	}

	/**
	 * 回傳alert dialog object
	 * 
	 * @param message
	 *            顯示警告訊息內容
	 * @return dialog object
	 */
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

	/**
	 * 確認網路狀態
	 * 
	 * @param con
	 *            context
	 * @return 回傳flase未連線 true 已連線
	 */
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
	 * 按下back建回上頁
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		Intent intent = new Intent(LocationActivity.this, MainActivity.class);
		startActivity(intent);
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onMyLocationButtonClick() {
		// TODO Auto-generated method stub
		// Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT)
		// .show();
		return false;
	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		Log.i("info", "我的位置:" + arg0.getLatitude() + "/" + arg0.getLongitude());
		Log.i("info" , "==========isMarkInfoWindowOpen=========:" +  isMarkInfoWindowOpen );
		if (isMarkInfoWindowOpen == false) {
//			if (myLocationLat == 0.0 && myLocationLong == 0.0) {
				Log.i("info", " === isMarkInfoWindowOpen == false:" + (isMarkInfoWindowOpen == false) );
				myLocationLat = arg0.getLatitude();
				myLocationLong = arg0.getLongitude();

				setCameraToUserPoint(arg0.getLatitude(), arg0.getLongitude());

				// timer.schedule(new RunScheduleTask() , 1000, 5000);
//			}
			setupMarks(arg0.getLatitude(), arg0.getLongitude());
		}
	}

	// public class RunScheduleTask extends TimerTask
	// {
	// public void run()
	// {
	// Log.i("info", "run task");
	// setupMarks( LocationActivity.this.myLocationLat,
	// LocationActivity.this.myLocationLong );
	// }
	// }

	private void setCameraToUserPoint(double cameraToLat, double cameraToLong) {
		if (mMap != null) {
			Log.i("info",
					" ========== setCameraToUserPoint =======cameraToLat: "
							+ cameraToLat + "/ ccameraToLong: " + cameraToLong);
			cameraPosition = new CameraPosition.Builder()
					.target(new LatLng(cameraToLat, cameraToLong)).zoom(14)
					.build();
			Log.i("info", "==========22222222");
			cameraUpdate = CameraUpdateFactory
					.newCameraPosition(cameraPosition);
			Log.i("info", "==========3333333");
			mMap.animateCamera(cameraUpdate);
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		mLocationClient.requestLocationUpdates(REQUEST, this); // LocationListener
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}

	private class MyInfoWindowAdapter implements InfoWindowAdapter {
		private final View infoWindow;

		MyInfoWindowAdapter() {
			infoWindow = getLayoutInflater().inflate(
					R.layout.custom_info_window, null);
		}

		int logoId = 0;

		// 點擊到的marker會呼叫進來, 改外觀與內容
		@Override
		public View getInfoWindow(Marker marker) {
			// TODO Auto-generated method stub

			// CameraUpdate cu = CameraUpdateFactory.newLatLng(new
			// LatLng(marker.getPosition().latitude,
			// marker.getPosition().longitude));
			// CameraUpdate zoom = CameraUpdateFactory.zoomTo(14);
			// mMap.moveCamera(cu);
			// mMap.animateCamera(zoom);

			//
			if (marker.equals(tmhtPoint)) {
				// logoId = R.drawable.xxxx;
			} else {
				logoId = 0;
			}

			// 顯示圖示
			ImageView iv_logo = (ImageView) infoWindow.findViewById(R.id.badge);
			iv_logo.setImageResource(logoId);

			// 顯示標題
			String title = marker.getTitle();
			// 顯示title
			TextView tv_title = (TextView) infoWindow.findViewById(R.id.title);
			tv_title.setText(title);

			// 顯示描述
			String snippet = marker.getSnippet();
			TextView tv_snippet = (TextView) infoWindow
					.findViewById(R.id.snippet);
			tv_snippet.setText(snippet);

			return infoWindow;
		}

		// 只改內容不改外觀
		@Override
		public View getInfoContents(Marker arg0) {
			// TODO Auto-generated method stub
			return null;
		}
	}

	// marker相關的監聽方法
	private class MyMarkerListener implements OnMarkerClickListener,
			OnInfoWindowClickListener {

		// 點擊訊息視窗
		@Override
		public void onInfoWindowClick(Marker arg0) {
			// TODO Auto-generated method stub
			Log.i("info", arg0.getTitle());
		}

		// 點擊地圖上marker
		@Override
		public boolean onMarkerClick(Marker arg0) {
			// TODO Auto-generated method stub
			Log.i("info", " click mark coordinate:"
					+ arg0.getPosition().latitude + "/"
					+ arg0.getPosition().longitude);
			// setCameraToUserPoint( arg0.getPosition().latitude,
			// arg0.getPosition().longitude );
			// CameraUpdate center = CameraUpdateFactory.newLatLng(new
			// LatLng(arg0.getPosition().latitude,
			// arg0.getPosition().longitude));
			// CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
			// mMap.moveCamera(center);
			// mMap.animateCamera(zoom);
			if (arg0.isInfoWindowShown()) {
				Log.i("info", "====arg0 . is info window shown : " + isMarkInfoWindowOpen);
				isMarkInfoWindowOpen = false;
			} else {
				Log.i("info", "====arg0 . is info window shown fffff : " + isMarkInfoWindowOpen);
				isMarkInfoWindowOpen = true;
			}
			return false;
		}

	}

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
