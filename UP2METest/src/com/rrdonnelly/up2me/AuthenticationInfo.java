package com.rrdonnelly.up2me;

public class AuthenticationInfo {
	private String userToken;
	private String userName;
	private String salt;
	
	public AuthenticationInfo(String userToken, String userName, String salt){
		this.userToken = userToken;
		this.userName = userName;
		this.salt = salt;
	}
	
	public String getUserToken() {
		return userToken;
	}
	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getSalt() {
		return salt;
	}
	public void setSalt(String salt) {
		this.salt = salt;
	}
}