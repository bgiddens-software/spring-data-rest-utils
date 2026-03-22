package com.bgiddens.projection;

import org.jspecify.annotations.Nullable;

/**
 * Provides the current expected entity projection for any given context.
 */
public interface ProjectionContextProvider {

	@Nullable
	Class<?> getCurrentProjection(Class<?> domainType);
}
