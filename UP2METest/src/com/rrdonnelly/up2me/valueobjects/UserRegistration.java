package com.rrdonnelly.up2me.valueobjects;

import java.io.Serializable;
import java.util.ArrayList;

public class UserRegistration implements Serializable {
	
	private long userId;
	
	private String userName;
	private String password;
	
	private String firstname;
	private String lastname;
	private String streetaddress1;
	private String streetaddress2;
	private String city;
	private String state;
	private String zip;
	private String mobileno1;
	
	//private String email;
	//private String pwd;
	private String confirmpwd;
	
	private int stateId;
	
	private int cloudProviderId;
	private String cloudProviderUserName;
	private String cloudProviderPassword;
	
	private ArrayList<Integer> selectedDocumentProviders;
	private ArrayList<Integer> selectedOfferProviders;
	private ArrayList<Integer> selectedCloudProviders;
	
	public ArrayList<Integer> getSelectedCloudProviders() {
		return selectedCloudProviders;
	}
	public void setSelectedCloudProviders(ArrayList<Integer> selectedCloudProviders) {
		this.selectedCloudProviders = selectedCloudProviders;
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
	
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	
	public String getStreetaddress1() {
		return streetaddress1;
	}
	public void setStreetaddress1(String streetaddress1) {
		this.streetaddress1 = streetaddress1;
	}
	
	public String getStreetaddress2() {
		return streetaddress2;
	}
	public void setStreetaddress2(String streetaddress2) {
		this.streetaddress2 = streetaddress2;
	}
	
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	
	public String getMobileno1() {
		return mobileno1;
	}
	public void setMobileno1(String mobileno1) {
		this.mobileno1 = mobileno1;
	}
	
	/*public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}*/
	
	public String getConfirmpwd() {
		return confirmpwd;
	}
	public void setConfirmpwd(String confirmpwd) {
		this.confirmpwd = confirmpwd;
	}
	
	public int getCloudProviderId() {
		return cloudProviderId;
	}
	public void setCloudProviderId(int cloudProviderId) {
		this.cloudProviderId = cloudProviderId;
	}
	
	public String getCloudProviderUserName() {
		return cloudProviderUserName;
	}
	public void setCloudProviderUserName(String cloudProviderUserName) {
		this.cloudProviderUserName = cloudProviderUserName;
	}
	
	public String getCloudProviderPassword() {
		return cloudProviderPassword;
	}
	public void setCloudProviderPassword(String cloudProviderPassword) {
		this.cloudProviderPassword = cloudProviderPassword;
	}
	
	public ArrayList<Integer> getSelectedDocumentProviders() {
		return selectedDocumentProviders;
	}
	public void setSelectedDocumentProviders(
			ArrayList<Integer> selectedDocumentProviders) {
		this.selectedDocumentProviders = selectedDocumentProviders;
	}
	public ArrayList<Integer> getSelectedOfferProviders() {
		return selectedOfferProviders;
	}
	public void setSelectedOfferProviders(ArrayList<Integer> selectedOfferProviders) {
		this.selectedOfferProviders = selectedOfferProviders;
	}
	
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	
	public int getStateId() {
		return stateId;
	}
	public void setStateId(int stateId) {
		this.stateId = stateId;
	}
}
