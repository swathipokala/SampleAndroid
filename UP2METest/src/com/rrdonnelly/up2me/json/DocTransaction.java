package com.rrdonnelly.up2me.json;

import java.util.Date;

public class DocTransaction {
	private Long transactionId;
	private Long docHeaderId;
	private String lineNumber;
	private String transType;
	//private Date transDate;
	private String transDate;
	private String time;
	private String number;
	private String rate;
	private String usageType;
	private String origination;
	private String destination;
	private Integer min;
	private Double airTimeCharges;
	private Double longDistOtherCharges;
	private Double totalAmount;
	//private Date dateOfPost;
	private String dateOfPost;
	
	private String referenceNumber;
	private String merchant;
	private String location;
	private String description;
	private Double applicationPrice;
	private String strTransDate;
	private String sicCode;
	private Double charge;
	private Double credit;

	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}

	public Long getDocHeaderId() {
		return docHeaderId;
	}

	public void setDocHeaderId(Long docHeaderId) {
		this.docHeaderId = docHeaderId;
	}

	public String getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(String lineNumber) {
		this.lineNumber = lineNumber;
	}

	public String getTransType() {
		return transType;
	}

	public void setTransType(String transType) {
		this.transType = transType;
	}

	public String getTransDate() {
		return transDate;
	}

	public void setTransDate(String transDate) {
		this.transDate = transDate;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public String getUsageType() {
		return usageType;
	}

	public void setUsageType(String usageType) {
		this.usageType = usageType;
	}

	public String getOrigination() {
		return origination;
	}

	public void setOrigination(String origination) {
		this.origination = origination;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public Integer getMin() {
		return min;
	}

	public void setMin(Integer min) {
		this.min = min;
	}

	public String getDateOfPost() {
		return dateOfPost;
	}

	public void setDateOfPost(String dateOfPost) {
		this.dateOfPost = dateOfPost;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public String getMerchant() {
		return merchant;
	}

	public void setMerchant(String merchant) {
		this.merchant = merchant;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Double getAirTimeCharges() {
		return airTimeCharges;
	}

	public void setAirTimeCharges(Double airTimeCharges) {
		this.airTimeCharges = airTimeCharges;
	}

	public Double getLongDistOtherCharges() {
		return longDistOtherCharges;
	}

	public void setLongDistOtherCharges(Double longDistOtherCharges) {
		this.longDistOtherCharges = longDistOtherCharges;
	}

	public Double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getApplicationPrice() {
		return applicationPrice;
	}

	public void setApplicationPrice(Double applicationPrice) {
		this.applicationPrice = applicationPrice;
	}

	public String getStrTransDate() {
		return strTransDate;
	}

	public void setStrTransDate(String strTransDate) {
		this.strTransDate = strTransDate;
	}

	public String getSicCode() {
		return sicCode;
	}

	public void setSicCode(String sicCode) {
		this.sicCode = sicCode;
	}

	public Double getCharge() {
		return charge;
	}

	public void setCharge(Double charge) {
		this.charge = charge;
	}

	public Double getCredit() {
		return credit;
	}

	public void setCredit(Double credit) {
		this.credit = credit;
	}

}
