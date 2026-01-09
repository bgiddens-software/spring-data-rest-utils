package com.bgiddens.sdr.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bgiddens.sdr.repository")
public class RepositoryConfig {
	private final String parameterOperationPrefix = "OP_";

	public String getParameterOperationPrefix() {
		return parameterOperationPrefix;
	}
}
