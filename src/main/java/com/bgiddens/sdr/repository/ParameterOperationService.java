package com.bgiddens.sdr.repository;

import com.bgiddens.sdr.repository.operations.Operation;
import org.jspecify.annotations.NonNull;

/** A service for determining the operation to apply for a request parameter. */
public interface ParameterOperationService {
	/**
	 * For any request parameter, returns a list of all values and the appropriate operation for each.
	 */
	@NonNull
	Operation get(@NonNull String parameter, @NonNull Integer valueIndex);
}
