package com.bgiddens.pbac.resolver;

public class PartitionConfigurationException extends RuntimeException {
	PartitionConfigurationException(String message) {
		super(message);
	}

	PartitionConfigurationException(Throwable cause) {
		super(cause);
	}

	PartitionConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}
}
