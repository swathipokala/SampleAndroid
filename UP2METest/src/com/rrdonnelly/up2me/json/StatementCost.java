package com.rrdonnelly.up2me.json;

/**
 * 
 * Sep 11, 2013 5:01:25 PM
 * @author Ben
 * @version
 * @remark
 */
public class StatementCost 
{
	private int year;
	private String month;
	private Double dataCost;
	private Long textUsage;
	private Double totalCost;
	private Double minutesUsed;
	private int lineCount;
	private String contractStartDate;
	private String contractEndDate;
	private String providerName;
	private String planCost;
	private Double accessDiscount;
	private Double dataOverageCost;
	private Double minutesOverageCost;
	private Double textOverageCost;
	
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public Double getDataCost() {
		return dataCost;
	}
	public void setDataCost(Double dataCost) {
		this.dataCost = dataCost;
	}
	
	public Long getTextUsage() {
		return textUsage;
	}
	public void setTextUsage(Long textUsage) {
		this.textUsage = textUsage;
	}
	public Double getTotalCost() {
		return totalCost;
	}
	public void setTotalCost(Double totalCost) {
		this.totalCost = totalCost;
	}
	public Double getMinutesUsed() {
		return minutesUsed;
	}
	public void setMinutesUsed(Double minutesUsed) {
		this.minutesUsed = minutesUsed;
	}
	public int getLineCount() {
		return lineCount;
	}
	public void setLineCount(int lineCount) {
		this.lineCount = lineCount;
	}
	public String getContractEndDate() {
		return contractEndDate;
	}
	public void setContractEndDate(String contractEndDate) {
		this.contractEndDate = contractEndDate;
	}
	public String getContractStartDate() {
		return contractStartDate;
	}
	public void setContractStartDate(String contractStartDate) {
		this.contractStartDate = contractStartDate;
	}
	public String getProviderName() {
		return providerName;
	}
	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}
	public String getPlanCost() {
		return planCost;
	}
	public void setPlanCost(String planCost) {
		this.planCost = planCost;
	}
	public Double getAccessDiscount() {
		return accessDiscount;
	}
	public void setAccessDiscount(Double accessDiscount) {
		this.accessDiscount = accessDiscount;
	}
	public Double getDataOverageCost() {
		return dataOverageCost;
	}
	public void setDataOverageCost(Double dataOverageCost) {
		this.dataOverageCost = dataOverageCost;
	}
	public Double getMinutesOverageCost() {
		return minutesOverageCost;
	}
	public void setMinutesOverageCost(Double minutesOverageCost) {
		this.minutesOverageCost = minutesOverageCost;
	}
	public Double getTextOverageCost() {
		return textOverageCost;
	}
	public void setTextOverageCost(Double textOverageCost) {
		this.textOverageCost = textOverageCost;
	}
}