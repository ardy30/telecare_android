package com.ebti.mobile.dohtelecare.model;

public class User {
	// UID : 登入帳號
	String uid;
	// NAME : 用戶姓名
	String name;
	// PASSWORD: 密碼	
	String password;
	// NICK_NAME : 暱稱
	String nickname;
	// GENDER : 性別
	String gender;
	// BIRTHDAY: 生日	
	String birthday;
	/**
	 * autor james
	 * ====start======
	 */
	// PHONE :電話
	String phone;
	// AREA :居住縣市
	String area;
	//String 
	// MOBILE :行動電話	
	String mobile;
	// AC_HIGH: 飯前血糖	
	String acHigh;
	// AC_LOW: 飯後血糖	
	String acLow;
	// BHP: 收縮壓	
	String bhp;
	// BLP: 舒張壓	
	String blp;
	// HEIGHT:身高	
	String height;
	// WEIGHT:體重
	String weight;
	// REMEMBER_USER:記憶帳密
	String rememberUser;
	// TYPE
	String type;
	// UNIT
	String unit;
	
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	
	public String getPhone(){
		return phone;
	}
	public void setPhone( String phone){
		this.phone = phone;
	}
	
	public String getArea(){
		return area;
	}
	public void setArea( String area){
		this.area = area;
	}
	
	
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getAcHigh() {
		return acHigh;
	}
	public void setAcHigh(String acHigh) {
		this.acHigh = acHigh;
	}
	public String getAcLow() {
		return acLow;
	}
	public void setAcLow(String acLow) {
		this.acLow = acLow;
	}
	public String getBhp() {
		return bhp;
	}
	public void setBhp(String bhp) {
		this.bhp = bhp;
	}
	public String getBlp() {
		return blp;
	}
	public void setBlp(String blp) {
		this.blp = blp;
	}
	public String getHeight() {
		return height;
	}
	public void setHeight(String height) {
		this.height = height;
	}
	public String getWeight() {
		return weight;
	}
	public void setWeight(String weight) {
		this.weight = weight;
	}
	public String getRememberUser() {
		return rememberUser;
	}
	public void setRememberUser(String rememberUser) {
		this.rememberUser = rememberUser;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
}
