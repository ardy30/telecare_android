package com.ebti.mobile.dohtelecare.respository;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ebti.mobile.dohtelecare.constant.Constant;
import com.ebti.mobile.dohtelecare.constant.SystemProperty;
import com.ebti.mobile.dohtelecare.model.CoordinatesData;

public class CoordinatesRepo {

	public ArrayList<CoordinatesData> getCoordinateData( String getJsonVal )
	{
		
		ArrayList< CoordinatesData > coorDataList = new ArrayList<CoordinatesData>();
		CoordinatesData coorData = null;
		
		JSONObject jsonResponse;
		int minDistance;
		JSONArray jsonArrayPlaceList;
		try {
			jsonResponse = new JSONObject(getJsonVal);
			minDistance = Integer.valueOf( jsonResponse.getString("minDistance") );
			
			SystemProperty.getInstance().MIN_DISTANCE = minDistance;
			jsonArrayPlaceList = jsonResponse.getJSONArray("placeList");
			
			for( int i = 0 ; i < jsonArrayPlaceList.length() ; i++ )
			{
				coorData = new CoordinatesData();
				if( !(jsonArrayPlaceList.getJSONObject(i).get("lat").toString() == "" | jsonArrayPlaceList.getJSONObject(i).get("lat").toString() ==null 
					| jsonArrayPlaceList.getJSONObject(i).get("lng").toString() =="" | jsonArrayPlaceList.getJSONObject(i).get("lng").toString() == null) ) 
				{
					double getLat = Double.valueOf( jsonArrayPlaceList.getJSONObject(i).get("lat").toString() );
					double getLng = Double.valueOf( jsonArrayPlaceList.getJSONObject(i).get("lng").toString() );
					String getTitle = jsonArrayPlaceList.getJSONObject(i).get("title").toString();
					String getAddress = jsonArrayPlaceList.getJSONObject(i).get("address").toString();
					String getPhone = jsonArrayPlaceList.getJSONObject(i).get("phone").toString();
					
					coorData.setLatLng(getLng, getLat);
					coorData.setCoorTitle(getTitle);
					coorData.setSnippet(getAddress, getPhone);
					coorDataList.add(coorData);
				}
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	/*	
				
		coorData = new CoordinatesData();
//		coorData.setCoorLatitude(121.540933);
//		coorData.setCoorLongitude(25.037744);
		coorData.setLatLng(121.540933, 25.037744);
		coorData.setCoorTitle("大同醫護公司");
		coorData.setSnippet("台北市仁愛路三段136號4樓", "0987654321");
		coorDataList.add(coorData);

		coorData = new CoordinatesData();
//		coorData.setCoorLatitude(121.540933);
//		coorData.setCoorLongitude(25.037744);
		coorData.setLatLng(121.543550, 25.025962);
		coorData.setCoorTitle("科技大樓捷運站");
		coorData.setSnippet("科技大樓捷運站地址");
		coorDataList.add(coorData);
		
		coorData = new CoordinatesData();
		coorData.setLatLng(121.445532, 25.167970);
		coorData.setCoorTitle("淡水站");
		coorData.setSnippet("新北市淡水區中正路1號");
		coorDataList.add(coorData);
		
		coorData = new CoordinatesData();
		coorData.setLatLng(121.473410, 25.130969);
		coorData.setCoorTitle("忠義站");
		coorData.setSnippet("臺北市北投區中央北路4段301號", "0987654321");
		coorDataList.add(coorData);

		coorData = new CoordinatesData();
		coorData.setLatLng(121.502530, 25.136933);
		coorData.setCoorTitle("新北投站");
		coorData.setSnippet("臺北市北投區大業路700號");
		coorDataList.add(coorData);

		coorData = new CoordinatesData();
		coorData.setLatLng(121.464471, 25.091554);
		coorData.setCoorTitle("蘆洲站");
		coorData.setSnippet("新北市蘆洲區三民路386號B1");
		coorDataList.add(coorData);
		
		coorData = new CoordinatesData();
		coorData.setLatLng(121.509237, 24.990045);
		coorData.setCoorTitle("南勢角站");
		coorData.setSnippet("新北市中和區捷運路6號");
		coorDataList.add(coorData);
		
		coorData = new CoordinatesData();
		coorData.setLatLng(121.537584, 24.957855);
		coorData.setCoorTitle("新店站");
		coorData.setSnippet("新北市新店區北宜路1段2號 ");
		coorDataList.add(coorData);
	
		coorData = new CoordinatesData();
		coorData.setLatLng(121.517081, 25.047924);
		coorData.setCoorTitle("台北火車站");
		coorData.setSnippet("臺北市中正區忠孝西路1段49號");
		coorDataList.add(coorData);


		coorData = new CoordinatesData();
		coorData.setLatLng(121.543767, 25.041629);
		coorData.setCoorTitle("忠孝復興站");
		coorData.setSnippet("臺北市中正區忠孝西路1段49號");
		coorDataList.add(coorData);

		coorData = new CoordinatesData();
		coorData.setLatLng(121.543551, 25.032943);
		coorData.setCoorTitle("大安站");
		coorData.setSnippet("臺北市中正區忠孝西路1段49號");
		coorDataList.add(coorData);
	*/	
		return coorDataList;
	}
}
