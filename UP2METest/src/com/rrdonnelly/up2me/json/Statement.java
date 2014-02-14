package com.rrdonnelly.up2me.json;

import com.rrdonnelly.up2me.json.Image;
import com.rrdonnelly.up2me.json.SubAccount;
import java.util.List;

public class Statement 
{

	private String accountNumber;
	private double balance;
	private String documentCoverPath;
	private Image displayImagePathBig;
	private Image displayImagePathSmall;
	private String documentPath;
	private String dueDate;
	private String dueAmount;

	private Image entityPreviewImagePath;	

	private long Id;
	private boolean bill;
	private boolean paid;
	private boolean read;
	
	private boolean itemChecked;

	
	private String message;
	private String messageType;	
	private double minAmountDue;
	
	private String name;	
	private String periodEndDate;
	private String providerName;
	private String periodStartDate;

	
	private String statementDisplayDate;	
	private List<SubAccount> subAccount;  
	private String strCreatedDate;
	private String userPrimaryEmail;

	private boolean valid;
	private List<TelephoneStatement> telephoneStatements;
	
	private boolean isPDFDeleted;
	
	private int tagId;
	
	private long subAccountId;
	
	private boolean docInBill;
	private boolean docInUnPaid;
	private boolean docInCashflow;
	private boolean docInStatement;
	private String category;	
	public boolean isDocInBill() {
		return docInBill;
	}

	public void setDocInBill(boolean docInBill) {
		this.docInBill = docInBill;
	}

	public boolean isDocInUnPaid() {
		return docInUnPaid;
	}

	public void setDocInUnPaid(boolean docInUnPaid) {
		this.docInUnPaid = docInUnPaid;
	}

	public boolean isDocInCashflow() {
		return docInCashflow;
	}

	public void setDocInCashflow(boolean docInCashflow) {
		this.docInCashflow = docInCashflow;
	}

	public boolean isDocInStatement() {
		return docInStatement;
	}

	public void setDocInStatement(boolean docInStatement) {
		this.docInStatement = docInStatement;
	}

	public String getDocumentCoverPath() {
		return documentCoverPath;
	}

	public void setDocumentCoverPath(String documentCoverPath) {
		this.documentCoverPath = documentCoverPath;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public Image getDisplayImagePathBig() {
		return displayImagePathBig;
	}

	public void setDisplayImagePathBig(Image displayImagePathBig) {
		this.displayImagePathBig = displayImagePathBig;
	}

	public Image getDisplayImagePathSmall() {
		return displayImagePathSmall;
	}

	public void setDisplayImagePathSmall(Image displayImagePathSmall) {
		this.displayImagePathSmall = displayImagePathSmall;
	}

	public String getDocumentPath() {
		return documentPath;
	}

	public void setDocumentPath(String documentPath) {
		this.documentPath = documentPath;
	}

	public String getDueDate() {
		return dueDate;
	}

	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}

	public Image getEntityPreviewImagePath() {
		return entityPreviewImagePath;
	}

	public void setEntityPreviewImagePath(Image entityPreviewImagePath) {
		this.entityPreviewImagePath = entityPreviewImagePath;
	}

	public long getId() {
		return Id;
	}

	public void setId(long id) {
		Id = id;
	}

	public boolean isBill() {
		return bill;
	}

	public void setBill(boolean bill) {
		this.bill = bill;
	}

	public boolean isPaid() {
		return paid;
	}

	public void setPaid(boolean paid) {
		this.paid = paid;
	}

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public double getMinAmountDue() {
		return minAmountDue;
	}

	public void setMinAmountDue(double minAmountDue) {
		this.minAmountDue = minAmountDue;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPeriodEndDate() {
		return periodEndDate;
	}

	public void setPeriodEndDate(String periodEndDate) {
		this.periodEndDate = periodEndDate;
	}

	public String getProviderName() {
		return providerName;
	}

	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}

	public String getPeriodStartDate() {
		return periodStartDate;
	}

	public void setPeriodStartDate(String periodStartDate) {
		this.periodStartDate = periodStartDate;
	}

	public String getStatementDisplayDate() {
		return statementDisplayDate;
	}

	public void setStatementDisplayDate(String statementDisplayDate) {
		this.statementDisplayDate = statementDisplayDate;
	}

	public List<SubAccount> getSubAccount() {
		return subAccount;
	}

	public void setSubAccount(List<SubAccount> subAccount) {
		this.subAccount = subAccount;
	}

	public String getStrCreatedDate() {
		return strCreatedDate;
	}

	public void setStrCreatedDate(String strCreatedDate) {
		this.strCreatedDate = strCreatedDate;
	}

	public String getUserPrimaryEmail() {
		return userPrimaryEmail;
	}

	public void setUserPrimaryEmail(String userPrimaryEmail) {
		this.userPrimaryEmail = userPrimaryEmail;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public List<TelephoneStatement> getTelephoneStatements() {
		return telephoneStatements;
	}

	public void setTelephoneStatements(List<TelephoneStatement> telephoneStatements) {
		this.telephoneStatements = telephoneStatements;
	}

	public String getDueAmount() {
		return dueAmount;
	}

	public void setDueAmount(String dueAmount) {
		this.dueAmount = dueAmount;
	}

	public int getTagId() {
		return tagId;
	}

	public void setTagId(int tagId) {
		this.tagId = tagId;
	}
	
	public boolean isPDFDeleted() {
		return isPDFDeleted;
	}

	public void setPDFDeleted(boolean isPDFDeleted) {
		this.isPDFDeleted = isPDFDeleted;
	}


	public boolean isItemChecked() {
		return itemChecked;
	}

	public void setItemChecked(boolean itemChecked) {
		this.itemChecked = itemChecked;
	}

	public long getSubAccountId() {
		return subAccountId;
	}

	public void setSubAccountId(long subAccountId) {
		this.subAccountId = subAccountId;
	}
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
}
