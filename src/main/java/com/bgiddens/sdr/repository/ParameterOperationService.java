package com.bgiddens.sdr.repository;

import java.util.List;

/** A service for determining the operation to apply for a request parameter. */
public interface ParameterOperationService {
	List<OperationType> get(String parameter);
}
