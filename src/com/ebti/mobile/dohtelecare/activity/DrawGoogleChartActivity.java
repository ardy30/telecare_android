package com.ebti.mobile.dohtelecare.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.util.EncodingUtils;

import com.ebti.mobile.dohtelecare.R;
import com.ebti.mobile.dohtelecare.model.HisData;
import com.ebti.mobile.dohtelecare.sqlite.HisDataAdapter;
import com.ebti.mobile.dohtelecare.sqlite.UserAdapter;
import com.ebti.mobile.dohtelecare.util.ScreenManager;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DrawGoogleChartActivity extends Activity {
	public static final String TAG = "LocalRecordDataActivity";
	
	private static ConnectivityManager connMgr;

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
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.serverrecorddatatogooglechart);
        
		//放入Activity Stack
		ScreenManager.getScreenManager().pushActivity(this);
  		
  		//==========================common=========================
		
		//登出
		((Button) findViewById(R.id.logoutbutton)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.i(TAG, "logoutbutton onClick()");
				//清除資料
				UserAdapter userAdapter = new UserAdapter(getApplicationContext());
				userAdapter.delAllUser();
				
				LoginActivity.instance.finish();

			    ScreenManager.getScreenManager().popAllActivityExceptOne(DrawGoogleChartActivity.class);
				ScreenManager.getScreenManager().popActivity();
				
				Intent intent = new Intent(DrawGoogleChartActivity.this, LoginActivity.class);
				startActivity(intent);
				
				//Intent intent = new Intent(BloodPressureActivity.this, OptionActivity.class);
				//startActivity(intent);
			}
		});
		
		//返回
		((Button) findViewById(R.id.backbutton)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.i(TAG, "backbutton onClick()");
//				((Button) findViewById(R.id.backbutton)).setBackgroundColor(getResources().getColor(R.color.click_color));
				ScreenManager.getScreenManager().popAllActivityExceptOne(DrawGoogleChartActivity.class);
				ScreenManager.getScreenManager().popActivity();
			}
		});
		
		//====================common End===========================
		
	    Bundle bundle = this.getIntent().getExtras();
	    final String dataType = bundle.getString("dataType");
	    final String beginDate = bundle.get("beginDate").toString();
	    final String endDate = bundle.get("endDate").toString();
	    Log.i(TAG, "beginDate : " + beginDate + ", endDate : " + endDate);
	    
	    //背景設定
	    //RelativeLayout relativeLayout2 = (RelativeLayout) findViewById(R.id.relativeLayout2);
	    Button anotherData = (Button) findViewById(R.id.anotherData);
	    TextView textViewDataDefult = (TextView) findViewById(R.id.textViewDataDefault);
	    if(dataType.equals("BP")){
	    //	relativeLayout2.setBackgroundDrawable(getResources().getDrawable(R.drawable.blood_pressure_record));
	    	anotherData.setBackgroundDrawable(getResources().getDrawable(R.drawable.serverrecordatalist_blood_sugar_dynamic));
	    	textViewDataDefult.setText(getString(R.string.bp_data_default));
	    }else{
	    //	relativeLayout2.setBackgroundDrawable(getResources().getDrawable(R.drawable.blood_glucose_record));
	    	anotherData.setBackgroundDrawable(getResources().getDrawable(R.drawable.serverrecordatalist_blood_pressure_dynamic));
	    	textViewDataDefult.setText(getString(R.string.bg_data_default));
	    }
	    
	    //另一個資料
	    anotherData.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.i(TAG, "anotherData onClick()");
				Intent intent = new Intent(DrawGoogleChartActivity.this, DrawGoogleChartActivity.class);
				Bundle bundle = new Bundle();
				if(dataType.equals("BP")){
					bundle.putString("dataType", "BG");
				}else{
					bundle.putString("dataType", "BP");
				}
				bundle.putString("beginDate", beginDate);
				bundle.putString("endDate", endDate);
				intent.putExtras(bundle);
				startActivity(intent);
				ScreenManager.getScreenManager().popActivity();
			}
		});
	    
    	//查看手機解析度
  		DisplayMetrics metrics = new DisplayMetrics();
  		getWindowManager().getDefaultDisplay().getMetrics(metrics);
  		final int screenWidthPixel = metrics.widthPixels;
  		int screenheightPixel = metrics.heightPixels;
  		Log.i(TAG, "the screen pixel is : "+screenWidthPixel + " X " + screenheightPixel);
  		
  		WebView webViewGoogleChart = (WebView) findViewById(R.id.webViewGoogleChart);
  		//縮放
  		webViewGoogleChart.getSettings().setBuiltInZoomControls(true);
  		webViewGoogleChart.getSettings().setSupportZoom(true);
  		String url = "http://chart.apis.google.com/chart";
  		if(dataType.equals("BG")){
  			//Post
  			webViewGoogleChart.postUrl(url, EncodingUtils.getBytes(createBGGoogleChartString(screenWidthPixel, beginDate, endDate), "BASE64"));
  			//webViewGoogleChart.loadUrl(createBGGoogleChartString(screenWidthPixel, beginDate, endDate));
  		}else{
  			//Post
  			webViewGoogleChart.postUrl(url, EncodingUtils.getBytes(createBPGoogleChartString(screenWidthPixel, beginDate, endDate), "BASE64"));
  			//webViewGoogleChart.loadUrl(createBPGoogleChartString(screenWidthPixel, beginDate, endDate));
  		}
  		
  		Toast.makeText(getApplicationContext(), "圖片讀取中，請稍後", Toast.LENGTH_LONG).show();
	    
    }

    public String createBGGoogleChartString(int screenWidthPixel, String beginDateStr, String endDateStr){
		Log.i(TAG, "createGoogleChartString(), beginDateStr : " + beginDateStr + ", endDateStr : " + endDateStr);
		//StringBuilder chartUrl = new StringBuilder("http://chart.apis.google.com/chart?");
		StringBuilder chartUrl = new StringBuilder();
        chartUrl.append("cht=lxy");                                                                             //使用折線圖
        chartUrl.append("&chs=500x420");                                                                        //圖像大小
        chartUrl.append("&chxt=x,y,x,y,r,r");                                                                   //x,y軸圖樣
        chartUrl.append("&chls=1|1");
        chartUrl.append("&chdlp=t");                                                                            //文字標籤位置
        chartUrl.append("&chma=5,5,5,40|20,20");                                                                //與邊界間隔(margin)            
        chartUrl.append("&chco=3072F3,FF0000,000000");                                                          //折線顏色
        chartUrl.append("&chxr=0,0,120|1,0,250|4,0,250");                                                       //各軸最大值
        chartUrl.append("&chds=0,100,0,250,0,100,0,250,0,100,0,250");                                           //各資料最大值
        chartUrl.append("&chm=o,3072F3,0,-1,3|o,FF0000,1,-1,3|o,000000,2,-1,3");                                //mark出各資料點位置
        chartUrl.append("&chxp=2,100|3,100|5,100,90");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Date beginDate = null;
        Date endDate = null;
        try {
			beginDate = sdf.parse(beginDateStr);
			endDate = sdf.parse(endDateStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "ParseException Error : " + e);
			e.printStackTrace();
		}
        Calendar calBeginDate = Calendar.getInstance();
        calBeginDate.setTime(beginDate);
        Calendar calEndDate = Calendar.getInstance();
        calEndDate.setTime(endDate);
        int cutDate = calEndDate.get(Calendar.DAY_OF_YEAR) - calBeginDate.get(Calendar.DAY_OF_YEAR);
        Log.i(TAG, "cutDate : " + cutDate);
        int days = 0;
        
        NumberFormat numberFormat = new DecimalFormat("0.###");
        
        if(cutDate > 9){
        	Log.i(TAG, "cutDate : " + cutDate);
        	chartUrl.append("&chg=" + numberFormat.format((100 / ((double)cutDate + 1))) + ",8");                                                                       //間隔:%
            chartUrl.append("&chxl=0:");
            Calendar calStart = calBeginDate;
            for(int i=0;i<=(cutDate + 1);i++){
            	if(i % 5 ==0){
            		chartUrl.append("|" + (calStart.get(Calendar.MONTH) + 1) + "/" + calStart.get(Calendar.DAY_OF_MONTH));
            	}else{
            		chartUrl.append("|%20");
            	}
            	calStart.add(Calendar.DAY_OF_YEAR, 1);
            }
            chartUrl.append("|2:|Date");
        }else if(cutDate <= 3){
        	Log.i(TAG, "dateCut <= three days");
        	chartUrl.append("&chg=8.333,8");                                                                       //間隔:%
            chartUrl.append("&chxl=0:");
            for(int i=0;i<=12;i++){
            	if(i!=0){
            		days += (cutDate + 1) * 2;
            	}
            	if (days == 24)
                {
                    days = 0;
                }
                chartUrl.append("|" + (days>9?days:"0"+days));
            }
            chartUrl.append("|2:|Hours");
        }else{
        	Log.i(TAG, "dateCut > three days");
        	
        	chartUrl.append("&chg=" + numberFormat.format((100 / ((double)cutDate + 1))) + ",8");              //間隔:%
            chartUrl.append("&chxl=0:");
            Calendar calStart = calBeginDate;
            Log.i(TAG, "calStart : " + sdf.format(calStart.getTime()));
            for(int i=0;i<=cutDate + 1;i++){
            	Log.i(TAG, "start Month : " + (calStart.get(Calendar.MONTH) + 1) + ", Day : " + calStart.get(Calendar.DAY_OF_MONTH));
            	chartUrl.append("|" + (calStart.get(Calendar.MONTH) + 1) + "/" + calStart.get(Calendar.DAY_OF_MONTH));
            	calStart.add(Calendar.DAY_OF_YEAR, 1);
            }
            chartUrl.append("|2:|Date");
        }
        
        chartUrl.append("|3:|mg/dL|5:|mg/dL| ");
        try {
			chartUrl.append("&chdl=" + URLEncoder.encode("飯前血糖","UTF-8") + "|" + URLEncoder.encode("飯後血糖","UTF-8") + "|" + URLEncoder.encode("隨機血糖","UTF-8"));
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, "UnsupportedEncodingException : " + e);
			e.printStackTrace();
		}//註解標籤                        
        chartUrl.append("&chd=t:");
        String acScale = "";
        String ac = "|";
        String pcScale = "|";
        String pc = "|";
        String nmScale = "|";
        String nm = "|";
        HisDataAdapter hisDataAdapter = new HisDataAdapter(getApplicationContext());
        List<HisData> listAC = hisDataAdapter.getBloodGlucoseAC();
        Log.i(TAG, "listAC size : " + listAC.size());
        Calendar calStartDate = Calendar.getInstance();
        calStartDate.setTime(beginDate);
        Log.i(TAG, "calStartDate : " + sdf.format(calStartDate.getTime()));
        String[] tempAc = BGChartDataString(listAC, cutDate, calStartDate, "AC");
        
        acScale += tempAc[0];
        ac += tempAc[1];

        List<HisData> listPC = hisDataAdapter.getBloodGlucosePC();
        Log.i(TAG, "listPC size : " + listPC.size());
        String[] tempPc = BGChartDataString(listPC, cutDate, calStartDate, "PC");
        pcScale += tempPc[0];
        pc += tempPc[1];

        List<HisData> listNM = hisDataAdapter.getBloodGlucoseNM();
        Log.i(TAG, "listNM size : " + listNM.size());
        String[] tempNm = BGChartDataString(listNM, cutDate, calStartDate, "NM");
        nmScale += tempNm[0];
        nm += tempNm[1];
        chartUrl.append(acScale);
        chartUrl.append(ac);
        chartUrl.append(pcScale);
        chartUrl.append(pc);
        chartUrl.append(nmScale);
        chartUrl.append(nm);
        Log.i(TAG, "toString() : " + chartUrl.toString());
        return chartUrl.toString();
	}
	
	private String[] BGChartDataString(List<HisData> list, int totalSpan, Calendar calStartDate, String type)
    {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		SimpleDateFormat sdfRecord = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		NumberFormat numberFormat = new DecimalFormat("0.###");
		
		Log.i(TAG, "calStartDate : " + sdfRecord.format(calStartDate.getTime()));
		
        String[] resultString = new String[4];
        String data = "", scale = "";                                                           //血糖用
        int count = list.size();
        if (count != 0)
        {
            for (int i = 0; i < count; i++)
            {
            	HisData hisData = list.get(i);
                Date recordDate = null;
                try {
                	recordDate = sdfRecord.parse(hisData.getDeviceTime());
				} catch (Exception e) {
					Log.e(TAG, "parse Exception : " + e);
				}
                Calendar calRecordDate = Calendar.getInstance();
                calRecordDate.setTime(recordDate);
                
                Log.i(TAG, "calRecordDate DAY OF YEAR : " + calRecordDate.get(Calendar.DAY_OF_YEAR) + ", calStartDate DAY OF YEAR : " + calStartDate.get(Calendar.DAY_OF_YEAR));
                int cutDays = calRecordDate.get(Calendar.DAY_OF_YEAR) - calStartDate.get(Calendar.DAY_OF_YEAR);
                if(cutDays<0){
                	cutDays = -cutDays;
                }
                int cutHours = calRecordDate.get(Calendar.HOUR_OF_DAY) - calStartDate.get(Calendar.HOUR_OF_DAY);
                int cutMinutes = calRecordDate.get(Calendar.MINUTE) - calStartDate.get(Calendar.MINUTE);
                Log.i(TAG, "cutDays : " + cutDays + ", cutHours : " + cutHours + ", cutMinutes : " + cutMinutes);
                Log.i(TAG, sdfRecord.format(calRecordDate.getTime()) + " / " + cutDays + " / " + cutHours + " / " + cutMinutes);
                double temp = (((cutDays * 24 + cutHours) * 60 + cutMinutes) / (((double)totalSpan + 1) * 24 * 60)) * 100;
                Log.i(TAG, "temp : " + temp + ", numberFormat.format(temp) : " + numberFormat.format(temp));
                scale += numberFormat.format(temp);
                Log.i(TAG, "scale : " + scale);
                if(type.equals("AC")){
                	data += hisData.getAc();
                }else if(type.equals("PC")){
                	data += hisData.getPc();
                }else if(type.equals("NM")){
                	data += hisData.getNm();
                }

                if (i != count - 1)
                {
                    data += ",";
                    scale += ",";
                }
            }
        }
        else
        {
            data += "_";
            scale += "_";
        }

        if (count == 1)
        {
            data = data + "," + data;
            scale = scale + "," + scale;
        }

        resultString[0] = scale;
        resultString[1] = data;

        return resultString;
    }
	
	public String createBPGoogleChartString(int screenWidthPixel, String beginDateStr, String endDateStr){
		Log.i(TAG, "createGoogleChartString()");
		//StringBuilder chartUrl = new StringBuilder("http://chart.apis.google.com/chart?");
		StringBuilder chartUrl = new StringBuilder();
        chartUrl.append("cht=lxy");                                                                             //使用折線圖
        chartUrl.append("&chs=500x420");                                                                        //圖像大小
        chartUrl.append("&chxt=x,y,x,y,r,r");                                                                   //x,y軸圖樣
        chartUrl.append("&chls=1|1");
        chartUrl.append("&chdlp=t");                                                                            //文字標籤位置
        chartUrl.append("&chma=5,5,5,40|20,20");                                                                //與邊界間隔(margin)            
        chartUrl.append("&chco=3072F3,FF0000,000000");                                                          //折線顏色
        chartUrl.append("&chxr=0,0,120|1,0,250|4,0,200");                                                       //各軸最大值
        chartUrl.append("&chds=0,100,0,250,0,100,0,250,0,100,0,250");                                           //各資料最大值
        chartUrl.append("&chm=o,3072F3,0,-1,3|o,FF0000,1,-1,3|o,000000,2,-1,3");                                //mark出各資料點位置
        chartUrl.append("&chxp=2,100|3,100|5,100,90");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Date beginDate = null;
        Date endDate = null;
        try {
			beginDate = sdf.parse(beginDateStr);
			endDate = sdf.parse(endDateStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "ParseException Error : " + e);
			e.printStackTrace();
		}
        Calendar calBeginDate = Calendar.getInstance();
        calBeginDate.setTime(beginDate);
        Calendar calEndDate = Calendar.getInstance();
        calEndDate.setTime(endDate);
        int cutDate = calEndDate.get(Calendar.DAY_OF_YEAR) - calBeginDate.get(Calendar.DAY_OF_YEAR);
        Log.i(TAG, "cutDate : " + cutDate);
        int days = 0;
        
        NumberFormat numberFormat = new DecimalFormat("0.###");
        
        if(cutDate > 9){
        	Log.i(TAG, "cutDate : " + cutDate);
        	chartUrl.append("&chg=" + numberFormat.format((100 / ((double)cutDate + 1))) + ",10");              //間隔:%
            chartUrl.append("&chxl=0:");
            Calendar calStart = Calendar.getInstance();
            calStart.setTime(beginDate);
            for(int i=0;i<=(cutDate + 1);i++){
            	if(i % 5 ==0){
            		chartUrl.append("|" + (calStart.get(Calendar.MONTH) + 1) + "/" + calStart.get(Calendar.DAY_OF_MONTH));
            	}else{
            		chartUrl.append("|%20");
            	}
            	calStart.add(Calendar.DAY_OF_YEAR, 1);
            }
            chartUrl.append("|2:|Date");
        }else if(cutDate <= 3){
        	Log.i(TAG, "dateCut <= three days");
        	chartUrl.append("&chg=8.333,10");                                                                       //間隔:%
            chartUrl.append("&chxl=0:");
            for(int i=0;i<=12;i++){
            	if(i!=0){
            		days += (cutDate + 1) * 2;
            	}
            	if (days == 24)
                {
                    days = 0;
                }
                chartUrl.append("|" + (days>9?days:"0"+days));
            }
            chartUrl.append("|2:|Hours");
        }else{
        	Log.i(TAG, "dateCut > three days");
        	chartUrl.append("&chg=" + numberFormat.format((100 / ((double)cutDate + 1))) + ",10");              //間隔:%
            chartUrl.append("&chxl=0:");
            Calendar calStart = Calendar.getInstance();
            calStart.setTime(beginDate);
            for(int i=0;i<=cutDate + 1;i++){
            	Log.i(TAG, "start Month : " + (calStart.get(Calendar.MONTH) + 1) + ", Day : " + calStart.get(Calendar.DAY_OF_MONTH));
            	chartUrl.append("|" + (calStart.get(Calendar.MONTH) + 1) + "/" + calStart.get(Calendar.DAY_OF_MONTH));
            	calStart.add(Calendar.DAY_OF_YEAR, 1);
            }
            chartUrl.append("|2:|Date");
        }
        
        chartUrl.append("|3:|mg/dL|5:|mg/dL| ");
        try {
        	chartUrl.append("|3:|mmHg|5:|" + URLEncoder.encode("脈搏","UTF-8") + "|" + URLEncoder.encode("次數","UTF-8") + "/" + URLEncoder.encode("分鐘","UTF-8"));
            chartUrl.append("&chdl=" + URLEncoder.encode("收縮壓","UTF-8") + "|" + URLEncoder.encode("舒張壓","UTF-8") + "|" + URLEncoder.encode("脈搏","UTF-8"));//註解標籤
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, "UnsupportedEncodingException : " + e);
			e.printStackTrace();
		}//註解標籤                        
        chartUrl.append("&chd=t:");
        String bhpScale = "";
        String bhp = "|";
        String blpScale = "|";
        String blp = "|";
        String pulseScale = "|";
        String pulse = "|";
        HisDataAdapter hisDataAdapter = new HisDataAdapter(getApplicationContext());
        List<HisData> list = hisDataAdapter.getBloodPressure();
        Log.i(TAG, "list size : " + list.size());
        Calendar calStartDate = calBeginDate;
        String[] temp = BPChartDataString(list, cutDate, calStartDate);

        bhpScale += temp[0];
        bhp += temp[1];
        blpScale += temp[0];
        blp += temp[2];
        pulseScale += temp[0];
        pulse += temp[3];
        
        chartUrl.append(bhpScale);
        chartUrl.append(bhp);
        chartUrl.append(blpScale);
        chartUrl.append(blp);
        chartUrl.append(pulseScale);
        chartUrl.append(pulse);
        Log.i(TAG, "toString() : " + chartUrl.toString());
        return chartUrl.toString();
	}
	
	private String[] BPChartDataString(List<HisData> list, int totalSpan, Calendar calStartDate)
    {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		Log.i(TAG, "calStartDate : " + sdf.format(calStartDate.getTime()));
		SimpleDateFormat sdfRecord = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		NumberFormat numberFormat = new DecimalFormat("0.###");
        String[] resultString = new String[4];
        String bhp = "", blp = "", pulse = "", scale = "";//血壓用
        int count = list.size();
        if (count != 0)
        {
            for (int i = 0; i < count; i++)
            {
            	HisData hisData = list.get(i);
                Date recordDate = null;
                try {
                	recordDate = sdfRecord.parse(hisData.getDeviceTime());
				} catch (Exception e) {
					Log.e(TAG, "parse Exception : " + e);
				}
                Calendar calRecordDate = Calendar.getInstance();
                calRecordDate.setTime(recordDate);
                
                int cutDays = calRecordDate.get(Calendar.DAY_OF_YEAR) - calStartDate.get(Calendar.DAY_OF_YEAR);
                int cutHours = calRecordDate.get(Calendar.HOUR_OF_DAY) - calStartDate.get(Calendar.HOUR_OF_DAY);
                int cutMinutes = calRecordDate.get(Calendar.MINUTE) - calStartDate.get(Calendar.MINUTE);
                Log.i(TAG, "cutDays : " + cutDays + ", cutHours : " + cutHours + ", cutMinutes : " + cutMinutes);
                Log.i(TAG, sdfRecord.format(calRecordDate.getTime()) + " / " + cutDays + " / " + cutHours + " / " + cutMinutes);
                double temp = (((cutDays * 24 + cutHours) * 60 + cutMinutes) / (((double)totalSpan + 1) * 24 * 60)) * 100;
                Log.i(TAG, "temp : " + temp + ", numberFormat.format(temp) : " + numberFormat.format(temp));
                scale += numberFormat.format(temp);
                Log.i(TAG, "scale : " + scale);
                bhp += hisData.getBhp();
                blp += hisData.getBlp();
                pulse += hisData.getPulse();

                if (i != count - 1)
                {
                	scale += ",";
                    bhp += ",";
                    blp += ",";
                    pulse += ",";
                }
            }
        }
        else
        {
        	scale += "_";
            bhp += "_";
            blp += "_";
            pulse += "_";
        }

        if (count == 1)
        {
        	scale = scale + "," + scale;
            bhp = bhp + "," + bhp;
            blp = blp + "," + blp;
            pulse = pulse + "," + pulse;
        }

        resultString[0] = scale;
        resultString[1] = bhp;
        resultString[2] = blp;
        resultString[3] = pulse;

        return resultString;
    }
	
}
