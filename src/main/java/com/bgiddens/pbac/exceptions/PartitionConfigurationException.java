package com.bgiddens.pbac.exceptions;

public class PartitionConfigurationException extends RuntimeException {
	public PartitionConfigurationException(String message) {
		super(message);
	}

	public PartitionConfigurationException(Throwable cause) {
		super(cause);
	}

	public PartitionConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}
}
