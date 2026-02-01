package com.bgiddens.sdr.repository;

import com.bgiddens.sdr.repository.operations.Operation;
import org.springframework.data.util.Pair;

import java.util.List;

/** A service for determining the operation to apply for a request parameter. */
public interface ParameterOperationService {
	/**
	 * For any request parameter, returns a list of all values and the appropriate operation for each.
	 */
	List<Pair<String, Operation>> get(String parameter);
}
