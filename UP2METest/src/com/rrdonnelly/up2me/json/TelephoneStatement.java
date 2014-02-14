package com.rrdonnelly.up2me.json;

import java.util.List;

public class TelephoneStatement 
{
	private Long telephoneStatementId;	
	private String planName;
	private List<String> accountPlan;
	public Long getTelephoneStatementId() {
		return telephoneStatementId;
	}
	public void setTelephoneStatementid(Long telephoneStatementId) {
		this.telephoneStatementId = telephoneStatementId;
	}
	public String getPlanName() {
		return planName;
	}
	public void setPlanName(String planName) {
		this.planName = planName;
	}
	public List<String> getAccountPlan() {
		return accountPlan;
	}
	public void setAccountPlan(List<String> accountPlan) {
		this.accountPlan = accountPlan;
	}
	
}