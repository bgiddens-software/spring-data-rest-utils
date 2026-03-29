package com.bgiddens.projection;

import org.jspecify.annotations.NonNull;
import org.springframework.data.repository.core.support.RepositoryFactoryCustomizer;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

public class ProjectionEntityGraphAwareJpaRepositoryFactoryCustomizer implements RepositoryFactoryCustomizer {

	private final ProjectionEntityGraphRegistry projectionEntityGraphRegistry;
	private final ProjectionContextProvider projectionContextProvider;

	public ProjectionEntityGraphAwareJpaRepositoryFactoryCustomizer(
			ProjectionEntityGraphRegistry projectionEntityGraphRegistry,
			ProjectionContextProvider projectionContextProvider) {
		this.projectionEntityGraphRegistry = projectionEntityGraphRegistry;
		this.projectionContextProvider = projectionContextProvider;
	}

	@Override
	public void customize(@NonNull RepositoryFactorySupport repositoryFactory) {
		repositoryFactory.addRepositoryProxyPostProcessor(new ProjectionEntityGraphAwareCrudMethodMetadataPostProcessor(
				projectionEntityGraphRegistry, projectionContextProvider));
	}
}
