package com.rrdonnelly.up2me.valueobjects;

public class UserFavorite {

	private Long userId;
	private Long providerId;
	private String providerName;
	private String providerIconPath;
	private Boolean favorite;
	private Boolean isOffer;	
	private Boolean isDocument;
	
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getProviderId() {
		return providerId;
	}
	public void setProviderId(Long providerId) {
		this.providerId = providerId;
	}
	public String getProviderName() {
		return providerName;
	}
	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}
	public String getProviderIconPath() {
		return providerIconPath;
	}
	public void setProviderIconPath(String providerIconPath) {
		this.providerIconPath = providerIconPath;
	}
	public Boolean getFavorite() {
		return favorite;
	}
	public void setFavorite(Boolean favorite) {
		this.favorite = favorite;
	}
	public Boolean getIsOffer() {
		return isOffer;
	}
	public void setIsOffer(Boolean isOffer) {
		this.isOffer = isOffer;
	}
	public Boolean getIsDocument() {
		return isDocument;
	}
	public void setIsDocument(Boolean isDocument) {
		this.isDocument = isDocument;
	}

}
