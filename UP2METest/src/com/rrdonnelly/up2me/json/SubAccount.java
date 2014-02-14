package com.rrdonnelly.up2me.json;

public class SubAccount {
	
	private String subAccountName;
	private String subAccountType;	
	private double endBalance;
	private String subAccountNumber;
	private boolean itemChecked;
	private long subAccountId;
	private String category;
	
	public String getSubAccountNumber() {
		return subAccountNumber;
	}
	public void setSubAccountNumber(String subAccountNumber) {
		this.subAccountNumber = subAccountNumber;
	}
	public String getSubAccountName() {
		return subAccountName;
	}
	public void setSubAccountName(String subAccountName) {
		this.subAccountName = subAccountName;
	}
	
	public String getSubAccountType() {
		return subAccountType;
	}
	public void setSubAccountType(String subAccountType) {
		this.subAccountType = subAccountType;
	}
	
	public double getEndBalance() {
		return endBalance;
	}
	public void setEndBalance(double endBalance) {
		this.endBalance = endBalance;
	}

	public long getSubAccountId() {
		return subAccountId;
	}
	public void setSubAccountId(long subAccountId) {
		this.subAccountId = subAccountId;
	}
	public boolean isItemChecked() {
		return itemChecked;
	}
	public void setItemChecked(boolean itemChecked) {
		this.itemChecked = itemChecked;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	
	
	
}
