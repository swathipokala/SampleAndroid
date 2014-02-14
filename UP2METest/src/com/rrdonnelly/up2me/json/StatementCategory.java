package com.rrdonnelly.up2me.json;

public class StatementCategory {
	private long accountTypeId;
	private String accountType;
	private String displayName;
	private String categoryType;
	private String category;
	private boolean isItemSelected;

	public boolean isItemSelected() {
		return isItemSelected;
	}

	public void setItemSelected(boolean isItemSelected) {
		this.isItemSelected = isItemSelected;
	}

	public long getAccountTypeId() {
		return accountTypeId;
	}

	public void setAccountTypeId(long accountTypeId) {
		this.accountTypeId = accountTypeId;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getCategoryType() {
		return categoryType;
	}

	public void setCategoryType(String categoryType) {
		this.categoryType = categoryType;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
}
