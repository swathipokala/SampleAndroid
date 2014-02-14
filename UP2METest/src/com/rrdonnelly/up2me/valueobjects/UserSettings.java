package com.rrdonnelly.up2me.valueobjects;

public class UserSettings {

	
	private long userId;
	private String password;
	private String confirmPassword;
	private String primaryEmail;
    private String userName;
	private boolean isShowBadgesStatements;
	private boolean isShowBadgesBills;	
	private boolean isShowBadgesOffers;
	private boolean isShowBadgesLibrary;
	private boolean isCalendarAlertsBills;
	private boolean isCalendarAlertsOffers;
	private boolean isCloudSyncStatements;
	private boolean isCloudSyncBills;
	private boolean isCloudSyncAll;
	private boolean isCloudSyncAppData;
	private boolean isPasswordChecked;
	private int setTimeOut = 0;
	
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getConfirmPassword() {
		return confirmPassword;
	}
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
	
	public String getPrimaryEmail() {
		return primaryEmail;
	}
	public void setPrimaryEmail(String primaryEmail) {
		this.primaryEmail = primaryEmail;
	}
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public int getSetTimeOut() {
		return setTimeOut;
	}
	public void setSetTimeOut(int setTimeOut) {
		this.setTimeOut = setTimeOut;
	}
	public boolean isPasswordChecked() {
		return isPasswordChecked;
	}
	public void setPasswordChecked(boolean isPasswordChecked) {
		this.isPasswordChecked = isPasswordChecked;
	}
	
	public boolean isShowBadgesStatements() {
		return isShowBadgesStatements;
	}
	public void setShowBadgesStatements(boolean isShowBadgesStatements) {
		this.isShowBadgesStatements = isShowBadgesStatements;
	}
	public boolean isShowBadgesBills() {
		return isShowBadgesBills;
	}
	public void setShowBadgesBills(boolean isShowBadgesBills) {
		this.isShowBadgesBills = isShowBadgesBills;
	}
	public boolean isShowBadgesOffers() {
		return isShowBadgesOffers;
	}
	public void setShowBadgesOffers(boolean isShowBadgesOffers) {
		this.isShowBadgesOffers = isShowBadgesOffers;
	}
	public boolean isShowBadgesLibrary() {
		return isShowBadgesLibrary;
	}
	public void setShowBadgesLibrary(boolean isShowBadgesLibrary) {
		this.isShowBadgesLibrary = isShowBadgesLibrary;
	}
	public boolean isCalendarAlertsBills() {
		return isCalendarAlertsBills;
	}
	public void setCalendarAlertsBills(boolean isCalendarAlertsBills) {
		this.isCalendarAlertsBills = isCalendarAlertsBills;
	}
	public boolean isCalendarAlertsOffers() {
		return isCalendarAlertsOffers;
	}
	public void setCalendarAlertsOffers(boolean isCalendarAlertsOffers) {
		this.isCalendarAlertsOffers = isCalendarAlertsOffers;
	}
	public boolean isCloudSyncStatements() {
		return isCloudSyncStatements;
	}
	public void setCloudSyncStatements(boolean isCloudSyncStatements) {
		this.isCloudSyncStatements = isCloudSyncStatements;
	}
	public boolean isCloudSyncBills() {
		return isCloudSyncBills;
	}
	public void setCloudSyncBills(boolean isCloudSyncBills) {
		this.isCloudSyncBills = isCloudSyncBills;
	}
	public boolean isCloudSyncAll() {
		return isCloudSyncAll;
	}
	public void setCloudSyncAll(boolean isCloudSyncAll) {
		this.isCloudSyncAll = isCloudSyncAll;
	}
	public boolean isCloudSyncAppData() {
		return isCloudSyncAppData;
	}
	public void setCloudSyncAppData(boolean isCloudSyncAppData) {
		this.isCloudSyncAppData = isCloudSyncAppData;
	}

	
}
