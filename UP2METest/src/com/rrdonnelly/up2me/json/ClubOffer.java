package com.rrdonnelly.up2me.json;

public class ClubOffer {

	private Long id;
	private Long clientId;
	private Image displayImagePath;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
