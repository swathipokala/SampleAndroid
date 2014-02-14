package com.rrdonnelly.up2me.json;

import java.util.List;

public class ContractTerminationFee {

	private Long providerId;
	private String providerName;
	
	private List<Double> fees;

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

	public List<Double> getFees() {
		return fees;
	}

	public void setFees(List<Double> fees) {
		this.fees = fees;
	}
}
