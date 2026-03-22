package com.bgiddens.projection;

import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.query.JpaEntityGraph;
import org.springframework.data.projection.EntityProjectionIntrospector;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;

import java.util.ArrayList;
import java.util.Set;

public class ProjectionEntityGraphFactory {

    private final EntityProjectionIntrospector entityProjectionIntrospector;

    public ProjectionEntityGraphFactory(EntityManager entityManager) {
        this.entityProjectionIntrospector = EntityProjectionIntrospector.create(
                new SpelAwareProxyProjectionFactory(),
                annotationMatchingProjectionPredicate(),
                new JpaMetamodelMappingContext(Set.of(entityManager.getMetamodel())));
    }

    private static EntityProjectionIntrospector.ProjectionPredicate annotationMatchingProjectionPredicate() {
        return (a, b) -> true; // todo
    }

    public JpaEntityGraph createEntityGraphForProjection(Class<?> domainType, Class<?> projectionType) {
        final var projectionIntrospection = this.entityProjectionIntrospector.introspect(projectionType, domainType);
        final var attributePaths = new ArrayList<String>();
        projectionIntrospection.forEach(propertyProjection -> {
            attributePaths.add(propertyProjection.getPropertyPath().toDotPath());
        });
        return new JpaEntityGraph(projectionType.getName(), EntityGraph.EntityGraphType.FETCH, attributePaths.toArray(String[]::new));
    }
}
