package com.rrdonnelly.up2me.json;

import java.util.List;
import java.util.Set;

public class PhoneByProvider {
	
	private Long providerId;
	private String providerName;
	
	private List<Phone> phones;
	
	public List<Phone> getPhones() {
		return phones;
	}
	public void setPhones(List<Phone> phones) {
		this.phones = phones;
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
}
