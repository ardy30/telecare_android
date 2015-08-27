package com.ebti.mobile.dohtelecare.util;


import java.util.HashMap;  
import java.util.List;  

import com.ebti.mobile.dohtelecare.R;

import android.content.Context;  
import android.graphics.Color;
import android.util.Log;
import android.view.View;  
import android.view.ViewGroup;  
import android.widget.SimpleAdapter;
import android.widget.TextView;  

public class LocalRecorderAdapter extends SimpleAdapter {
	private static final String TAG = "LocalRecorderAdapter";
	public LocalRecorderAdapter(Context context, List<HashMap<String, String>> items, int resource, String[] from, int[] to) {
		super(context, items, resource, from, to);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	  View view = super.getView(position, convertView, parent);
	  TextView tvRecorder = (TextView)view.findViewById(R.id.item2);
	  TextView tvUpload = (TextView)view.findViewById(R.id.item3);
	  String upload = tvUpload.getText().toString().trim();
	  if(upload.equals("未上傳")){
		  //Log.i(TAG, "position : " + position);
		  //view.setBackgroundColor(R.color.red);
		  //tvUpload.setBackgroundColor(R.color.redDark);
		  tvRecorder.setBackgroundColor(Color.RED);
		  tvUpload.setBackgroundColor(Color.RED);
	  }else{
		  tvRecorder.setBackgroundColor(Color.WHITE);
		  tvUpload.setBackgroundColor(Color.WHITE);
	  }
	  return view;
	}
}