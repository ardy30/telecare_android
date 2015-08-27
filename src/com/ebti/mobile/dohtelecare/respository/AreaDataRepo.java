package com.ebti.mobile.dohtelecare.respository;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import android.util.Log;
import com.ebti.mobile.dohtelecare.model.AreaData;

/**
 * 2013 08 01
 * @author james
 * 寫入縣市與代碼資料
 * 
 */

public class AreaDataRepo {

	/**
	 * 
	 * @param getJsonVal 從webservice取得縣市，代碼 json 內容
	 * @return 回傳arrayList
	 */
	public ArrayList<AreaData> getAreaDataList(String getJsonVal)
	{
		ArrayList< AreaData > areaDataList  = new ArrayList< AreaData >();
		AreaData areaData = null;
		try {
			
			Log.i("info", "getJsonVal:" + getJsonVal);
			JSONArray jsonAry = new JSONArray( getJsonVal );
			
			Log.i("info", jsonAry.length() +"");
			for( int i = 0 ; i < jsonAry.length() ; i++ )
			{
				areaData = new AreaData();
				
				areaData.setAreaCode( jsonAry.getJSONObject(i).getString("Code") );
				areaData.setAreaName( jsonAry.getJSONObject(i).getString("Name") );
				
				areaDataList.add(areaData);
			}
			
		} catch (JSONException e) {
			
			Log.i("info", e.getMessage());
			e.printStackTrace();
			
			return null;
		}
		return areaDataList;
	}
}
