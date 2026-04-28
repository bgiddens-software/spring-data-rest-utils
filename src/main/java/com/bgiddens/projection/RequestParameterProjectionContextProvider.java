package com.bgiddens.projection;

import org.jspecify.annotations.Nullable;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * Provides the projection specified by a query parameter on the active request.
 */
public class RequestParameterProjectionContextProvider implements ProjectionContextProvider {

	private final NativeWebRequest nativeWebRequest;
	private final RepositoryRestConfiguration repositoryRestConfiguration;
	private final String parameterName;

	public RequestParameterProjectionContextProvider(NativeWebRequest nativeWebRequest,
			RepositoryRestConfiguration repositoryRestConfiguration, String parameterName) {
		this.nativeWebRequest = nativeWebRequest;
		this.repositoryRestConfiguration = repositoryRestConfiguration;
		this.parameterName = parameterName;
	}

	@Nullable
	public Class<?> getCurrentProjection(Class<?> domainType) {
		if (RequestContextHolder.getRequestAttributes() == null) {
			return null;
		}
		final String projectionName = nativeWebRequest.getParameter(parameterName);
		// todo - handle excerpt projections not specified in request
		if (projectionName == null) {
			return null;
		} else {
			return this.repositoryRestConfiguration.getProjectionConfiguration().getProjectionType(domainType,
					projectionName);
		}
	}
}
