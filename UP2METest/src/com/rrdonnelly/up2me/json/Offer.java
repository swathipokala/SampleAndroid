package com.rrdonnelly.up2me.json;

import java.util.List;

public class Offer {
	
	private Long id;
	private List<String> validZipCodes;
	private String name;
	private String expiresOnDisplayDate;
	private boolean isRead;
	private Image barcodeImagePath;
	private Image offerImagePath;
	private Image teaserImagePath;
	private Image displayImagePathSmall;
	private String offerText;
	private Long createdDate;
	
	//This tagId field is added for local purpose.
	private String displayImagePathSmallLocal;
	private String offerImagePathLocal;
	private int tagId;
	
	public String getOfferImagePathLocal() {
		return offerImagePathLocal;
	}
	public void setOfferImagePathLocal(String offerImagePathLocal) {
		this.offerImagePathLocal = offerImagePathLocal;
	}
	public String getTeaserImagePathLocal() {
		return teaserImagePathLocal;
	}
	public void setTeaserImagePathLocal(String teaserImagePathLocal) {
		this.teaserImagePathLocal = teaserImagePathLocal;
	}
	private String teaserImagePathLocal;
	
	public String getDisplayImagePathSmallLocal() {
		return displayImagePathSmallLocal;
	}
	public void setDisplayImagePathSmallLocal(String displayImagePathSmallLocal) {
		this.displayImagePathSmallLocal = displayImagePathSmallLocal;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long long1) {
		this.id = long1;
	}
	public List<String> getValidZipCodes() {
		return validZipCodes;
	}
	public void setValidZipCodes(List<String> string) {
		this.validZipCodes = string;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getExpiresOnDisplayDate() {
		return expiresOnDisplayDate;
	}
	public void setExpiresOnDisplayDate(String expiresOnDisplayDate) {
		this.expiresOnDisplayDate = expiresOnDisplayDate;
	}
	public boolean isRead() {
		return isRead;
	}
	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}
	public Image getBarcodeImagePath() {
		return barcodeImagePath;
	}
	public void setBarcodeImagePath(Image barcodeImagePath) {
		this.barcodeImagePath = barcodeImagePath;
	}
	public Image getOfferImagePath() {
		return offerImagePath;
	}
	public void setOfferImagePath(Image offerImagePath) {
		this.offerImagePath = offerImagePath;
	}
	public String getOfferText() {
		return offerText;
	}
	public void setOfferText(String offerText) {
		this.offerText = offerText;
	}
	
	public Long getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Long createdDate) {
		this.createdDate = createdDate;
	}
	public Image getTeaserImagePath() {
		return teaserImagePath;
	}
	public void setTeaserImagePath(Image teaserImagePath) {
		this.teaserImagePath = teaserImagePath;
	}
	public Image getDisplayImagePathSmall() {
		return displayImagePathSmall;
	}
	public void setDisplayImagePathSmall(Image displayImagePathSmall) {
		this.displayImagePathSmall = displayImagePathSmall;
	}
	public int getTagId() {
		return tagId;
	}
	public void setTagId(int tagId) {
		this.tagId = tagId;
	}
	
	
}
