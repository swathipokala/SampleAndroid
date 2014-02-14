package com.rrdonnelly.up2me.valueobjects;

public class MarkReadOrUnRead {
	
	private Long id;
	private Boolean isRead;
	
	private Boolean docInBill;
	private Boolean docInUnPaid;
	private Boolean docInCashflow;
	private Boolean docInStatement;
	
	public Boolean isDocInBill() {
		return docInBill;
	}
	public void setDocInBill(Boolean docInBill) {
		this.docInBill = docInBill;
	}
	public Boolean isDocInUnPaid() {
		return docInUnPaid;
	}
	public void setDocInUnPaid(Boolean docInUnPaid) {
		this.docInUnPaid = docInUnPaid;
	}
	public Boolean isDocInCashflow() {
		return docInCashflow;
	}
	public void setDocInCashflow(Boolean docInCashflow) {
		this.docInCashflow = docInCashflow;
	}
	public Boolean isDocInStatement() {
		return docInStatement;
	}
	public void setDocInStatement(Boolean docInStatement) {
		this.docInStatement = docInStatement;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Boolean getIsRead() {
		return isRead;
	}
	public void setIsRead(Boolean isRead) {
		this.isRead = isRead;
	}
}
