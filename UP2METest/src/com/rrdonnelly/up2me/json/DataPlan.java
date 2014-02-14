package com.rrdonnelly.up2me.json;

import java.util.ArrayList;
import java.util.List;

public class DataPlan {	
	private String providerName;
	private String dataValue;
	private String dataUnit;
	private String planType;
	private List<Double> lines = new ArrayList<Double>();
	
	private String minutesValue;
	private String textMessageValue;
	
	public List<Double> getLines() {
		return lines;
	}
	public void setLines(List<Double> lines) {
		this.lines = lines;
	}
	public String getProviderName() {
		return providerName;
	}
	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}
	public String getDataValue() {
		return dataValue;
	}
	public void setDataValue(String dataValue) {
		this.dataValue = dataValue;
	}
	public String getDataUnit() {
		return dataUnit;
	}
	public void setDataUnit(String dataUnit) {
		this.dataUnit = dataUnit;
	}
	public String getPlanType() {
		return planType;
	}
	public void setPlanType(String planType) {
		this.planType = planType;
	}
	public String getMinutesValue() {
		return minutesValue;
	}
	public void setMinutesValue(String minutesValue) {
		this.minutesValue = minutesValue;
	}
	public String getTextMessageValue() {
		return textMessageValue;
	}
	public void setTextMessageValue(String textMessageValue) {
		this.textMessageValue = textMessageValue;
	}			
	
}	
