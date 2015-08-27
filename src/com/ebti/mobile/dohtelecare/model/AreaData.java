package com.ebti.mobile.dohtelecare.model;

/**
 * 2013.08.01
 * @author james
 * 地區 modle 地區代碼，縣市名稱
 */
public class AreaData {

	private String areaCode;
	private String areaName;
	
	public void setAreaCode(String str)
	{
		this.areaCode = str;
	}
	public String getAreaCode()
	{
		return areaCode;
	}
	
	public void setAreaName(String str)
	{
		this.areaName = str;
	}
	public String getAreaName()
	{
		return areaName;
	}
	
	@Override
	public String toString()
	{
		return areaName;
	}
}
