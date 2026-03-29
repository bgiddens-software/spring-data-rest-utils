package com.bgiddens.projection;

import jakarta.persistence.EntityManager;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.query.JpaEntityGraph;
import org.springframework.data.projection.EntityProjectionIntrospector;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.data.rest.core.config.Projection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

public class ProjectionEntityGraphFactory {

	private final EntityProjectionIntrospector entityProjectionIntrospector;

	public ProjectionEntityGraphFactory(EntityManager entityManager) {
		this.entityProjectionIntrospector = EntityProjectionIntrospector.create(new SpelAwareProxyProjectionFactory(),
				annotationMatchingProjectionPredicate(), new JpaMetamodelMappingContext(Set.of(entityManager.getMetamodel())));
	}

	private static EntityProjectionIntrospector.ProjectionPredicate annotationMatchingProjectionPredicate() {
		return (a, b) -> Optional.ofNullable(AnnotatedElementUtils.getMergedAnnotation(a, Projection.class))
				.filter(p -> Arrays.asList(p.types()).contains(b)).isPresent();
	}

	public EntityGraph createEntityGraphForProjection(Class<?> domainType, Class<?> projectionType) {
		final var projectionIntrospection = this.entityProjectionIntrospector.introspect(projectionType, domainType);
		final var attributePaths = new ArrayList<String>();
		projectionIntrospection
				.forEach(propertyProjection -> attributePaths.add(propertyProjection.getPropertyPath().toDotPath()));
		return new DynamicEntityGraph(EntityGraph.EntityGraphType.FETCH, attributePaths.toArray(String[]::new));
	}
}
