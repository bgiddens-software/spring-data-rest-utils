package com.bgiddens.sdr.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bgiddens.sdr.repository")
public class RepositoryConfig {
	private String parameterOperationPrefix = "OP_";

	public String getParameterOperationPrefix() {
		return parameterOperationPrefix;
	}

	public void setParameterOperationPrefix(String parameterOperationPrefix) {
		this.parameterOperationPrefix = parameterOperationPrefix;
	}
}
