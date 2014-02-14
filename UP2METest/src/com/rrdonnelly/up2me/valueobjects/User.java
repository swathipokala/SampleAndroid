package com.rrdonnelly.up2me.valueobjects;

public class User {
	
	public long id;
	public String userName;
	public String password;
	public String firsName;
	public String lastName;
	public String streeAddr1;
	public String streeAddr2;
	public String city;
	public String state;
	public String zipCode;
	public String mobileNo;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getFirsName() {
		if(firsName != null && "null".equals(firsName)){
			return "";
		}
		return firsName;
	}
	public void setFirsName(String firsName) {
		this.firsName = firsName;
	}
	public String getLastName() {
		if(lastName != null && "null".equals(lastName)){
			return "";
		}
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getStreeAddr1() {
		if(streeAddr1 != null && "null".equals(streeAddr1)){
			return "";
		}
		return streeAddr1;
	}
	public void setStreeAddr1(String streeAddr1) {
		this.streeAddr1 = streeAddr1;
	}
	public String getStreeAddr2() {
		if(streeAddr2 != null && "null".equals(streeAddr2)){
			return "";
		}
		return streeAddr2;
	}
	public void setStreeAddr2(String streeAddr2) {
		this.streeAddr2 = streeAddr2;
	}
	public String getCity() {
		if(city != null && "null".equals(city)){
			return "";
		}
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		if(state != null && "null".equals(state)){
			return "";
		}
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getMobileNo() {
		if(mobileNo != null && "null".equals(mobileNo)){
			return "";
		}
		return mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	public String getZipCode() {
		if(zipCode != null && "null".equals(zipCode)){
			return "";
		}
		return zipCode;
	}
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	
	

}
