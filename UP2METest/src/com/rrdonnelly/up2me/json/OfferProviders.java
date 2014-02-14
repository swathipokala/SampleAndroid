package com.rrdonnelly.up2me.json;

public class OfferProviders {
	private long providerId;
	private String providerName;
	private boolean isActive;
	private String imageUrlSmallStr;
	private boolean isFavorite;
	private long userId;
	private boolean isDirty;
	
	public boolean isFavorite() {
		return isFavorite;
	}
	public void setFavorite(boolean isFavorite) {
		this.isFavorite = isFavorite;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public String getImageUrlSmallStr() {
		return imageUrlSmallStr;
	}
	public void setImageUrlSmallStr(String imageUrlSmallStr) {
		this.imageUrlSmallStr = imageUrlSmallStr;
	}
	public long getProviderId() {
		return providerId;
	}
	public void setProviderId(long providerId) {
		this.providerId = providerId;
	}
	public String getProviderName() {
		return providerName;
	}
	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	public boolean isDirty() {
		return isDirty;
	}
	public void setDirty(boolean isDirty) {
		this.isDirty = isDirty;
	}
	
}
