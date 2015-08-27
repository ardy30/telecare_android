package com.ebti.mobile.dohtelecare.model;

public class DeviceMapping {
	//設備編號
	String deviceId;
	//設備序號(可由設備上讀取)
	String deviceSn;
	//設備名稱規格
	String deviceDesc;
	//設備Mac Address
	String deviceMac;
	public DeviceMapping(){
		
	}
	//取得設備編號
	public String getDeviceId() {
		return deviceId;
	}
	//設備編號
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	/**
	 * 取得設備序號
	 * @return
	 */
	public String getDeviceSn() {
		return deviceSn;
	}
	/**
	 * 設定設備序號
	 * @param deviceSn
	 */
	public void setDeviceSn(String deviceSn) {
		this.deviceSn = deviceSn;
	}
	/**
	 * 取得設備說明
	 * @return
	 */
	public String getDeviceDesc() {
		return deviceDesc;
	}
	/**
	 * 設定設備說明
	 * @param deviceDesc
	 */
	public void setDeviceDesc(String deviceDesc) {
		this.deviceDesc = deviceDesc;
	}
	/**
	 * 取得設備Mac
	 * @return
	 */
	public String getDeviceMac() {
		return deviceMac;
	}
	/**
	 * 設定設備Mac
	 * @param deviceMac
	 */
	public void setDeviceMac(String deviceMac) {
		this.deviceMac = deviceMac;
	}
}
