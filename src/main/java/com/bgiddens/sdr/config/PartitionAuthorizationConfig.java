package com.bgiddens.sdr.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "bgiddens.sdr.pbac")
public class PartitionAuthorizationConfig {
	/**
	 * If true, applies authorization to invocation of repository delete methods. This incurs an additional database
	 * request and is generally unnecessary because SDR invokes findById first.
	 */
	private Boolean checkDelete = false;
}
