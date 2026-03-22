package com.bgiddens.projection;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.provider.QueryExtractor;
import org.springframework.data.jpa.repository.query.DynamicEntityGraphJpaQueryMethod;
import org.springframework.data.jpa.repository.query.JpaQueryMethod;
import org.springframework.data.jpa.repository.query.JpaQueryMethodFactory;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.util.Assert;

import java.lang.reflect.Method;

public class ProjectionEntityGraphAwareJpaQueryMethodFactory implements JpaQueryMethodFactory {

	private final QueryExtractor extractor;
	private final ProjectionEntityGraphRegistry projectionEntityGraphRegistry;
	private final ProjectionContextProvider projectionContextProvider;

	public ProjectionEntityGraphAwareJpaQueryMethodFactory(QueryExtractor extractor,
			ProjectionEntityGraphRegistry projectionEntityGraphRegistry,
			ProjectionContextProvider projectionContextProvider) {
		Assert.notNull(extractor, "QueryExtractor must not be null");
		this.extractor = extractor;
		this.projectionEntityGraphRegistry = projectionEntityGraphRegistry;
		this.projectionContextProvider = projectionContextProvider;
	}

	@Override
	@NonNull
	public JpaQueryMethod build(@NonNull Method method, @NonNull RepositoryMetadata metadata,
			@NonNull ProjectionFactory factory) {
		final var projectionClass = projectionContextProvider.getCurrentProjection(metadata.getDomainType());
		if (projectionClass != null) {
			return new DynamicEntityGraphJpaQueryMethod(method, metadata, factory, extractor,
					projectionEntityGraphRegistry.getEntityGraphForProjection(metadata.getDomainType(), projectionClass));
		} else {
			return new JpaQueryMethod(method, metadata, factory, extractor);
		}
	}
}
