package com.rrdonnelly.up2me.json;

public class CloudProvider {

	private Long clientId;
	private String providerName;
	private Image displayImagePath;
	private String imageText;
	private String strImagePath;
	
	public String getProviderName() {
		return providerName;
	}
	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}
	
	public String getStrImagePath() {
		return strImagePath;
	}
	public void setStrImagePath(String strImagePath) {
		this.strImagePath = strImagePath;
	}
	public String getImageText() {
		return imageText;
	}
	public void setImageText(String imageText) {
		this.imageText = imageText;
	}
	public Long getClientId() {
		return clientId;
	}
	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}
	public Image getDisplayImagePath() {
		return displayImagePath;
	}
	public void setDisplayImagePath(Image displayImagePath) {
		this.displayImagePath = displayImagePath;
	}
	
	
}
