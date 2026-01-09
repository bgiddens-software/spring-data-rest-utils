package com.bgiddens.sdr.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bgiddens.sdr.pbac")
public class PartitionAuthorizationConfig {
	/**
	 * If true, applies authorization to invocation of repository delete methods. This incurs an additional database
	 * request and is generally unnecessary because SDR invokes findById first.
	 */
	private Boolean checkDelete = false;

	public Boolean getCheckDelete() {
		return checkDelete;
	}

	public void setCheckDelete(Boolean checkDelete) {
		this.checkDelete = checkDelete;
	}
}
