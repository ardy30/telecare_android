package com.ebti.mobile.dohtelecare.model;

import com.google.android.gms.maps.model.LatLng;

public class CoordinatesData {

	// 緯度
//	private double coorLatitude;
	// 經度
//	private double coorLongitude;
	// title
	private String coorTitle;
	// snippet
	private String coorSinppet;
	private LatLng latLng = null;
	// address
	private String mAddress;
	// phone
	private String mPhone;
	
//	public double getCoorLatitude()
//	{
//		return coorLatitude;
//	}
//	public void setCoorLatitude( double mLat )
//	{
//		this.coorLatitude = mLat;
//	}
//	
//	public double getCoorLongitude()
//	{
//		return coorLongitude;
//	}
//	public void setCoorLongitude( double mLong )
//	{
//		this.coorLongitude = mLong;
//	}
	
	
	public void setAddress(String mAddr)
	{
		this.mAddress = mAddr;
	}
	public String getAddress()
	{
		return mAddress;
	}
	
	public void setPhone(String mPhone)
	{
		this.mPhone = mPhone;
	}
	public String getPhone()
	{
		return mPhone;
	}
	
	public String getCoorTitle()
	{
		return coorTitle;
	}
	public void setCoorTitle( String mTitle)
	{
		this.coorTitle = mTitle;
	}

	public void setLatLng( double mLong, double mLat)
	{
		latLng = new LatLng(mLat, mLong);
	}
	public LatLng getLatLng()
	{
		return latLng;
	}
	
	
	public String getSinppet()
	{
		return coorSinppet;
	}
	public void setSnippet( String mAddress)
	{
		this.coorSinppet = mAddress;
		setAddress( mAddress );
	}
	public void setSnippet( String mAddress, String mPhone )
	{
		this.coorSinppet = mAddress + "\n" + 
						   "電話:" + mPhone;
		
		setAddress( mAddress );
		setPhone(mPhone);
	}
	
}
