package com.ebti.mobile.dohtelecare.constant;


public interface Constant extends com.vizego.mobile.android.constant.Constant {
	public static final int PLATFORM = 1;
	//遠距照護使用
	public static final int DATA_IS_NOT_UPLOAD = 0;
	public static final int DATA_ALREADY_UPLOAD = 1;
	//ActivityResult
	public static final String DELIVER_LOGIN_MESSAGE = "DELIVER_LOGIN_MESSAGE";
	public static final int REQUEST_CODE_SUCCESS = 0;
	//Account Text
	public static final String DELIVER_LOGIN_ACCOUNT = "DELIVER_LOGIN_ACCOUNT";
	//BIODATA
	public static final String BIODATA_DEVICE_TYPE_WEIGHT = "1";
	public static final String BIODATA_DEVICE_TYPE_BLOOD_GLUCOSE = "2";
	public static final String BIODATA_DEVICE_TYPE_BLOOD_PRESSURE = "3";
	//Upload inputType
	public static final String UPLOAD_INPUT_TYPE_DEVICE = "Device";
	public static final String UPLOAD_INPUT_TYPE_MANUAL = "Manual";
	/*Message Code一覽*/
	public static final String MessageCodeSuccess = "A01";//成功。帳號存在或動作執行成功。
	public static final String MessageCodeDataExist = "A11";//成功。資料已存在
	public static final String MessageCodeAccountNotExist = "E01";//帳號不存在。
	public static final String MessageCodePasswordWrong = "E02";//密碼錯誤，身分驗證失敗。
	public static final String MessageCodeAccountFormatWrong = "E03";//帳號格式錯誤
	public static final String MessageCodePasswordFormatWrong = "E04";//密碼格式錯誤
	public static final String MessageCodeAccountExist = "E05";//帳號已存在無法註冊
	public static final String MessageCodeLackData = "E11";//缺少必要資料
	public static final String MessageCodeDataFormatWrong = "E12";//資料格式錯誤
	public static final String MessageCodePhyDataFormatWrong = "E21";//生理資料格式錯誤
	public static final String MessageCodeError = "E99";//其他錯誤
	
	// sender ID: 554604635753
	// services API key:	 AIzaSyDIgxXS5tgCfj7Bty4RSCervUlS533PHPI

	
}
