package com.bgiddens.projection;

import org.springframework.data.jpa.repository.query.JpaEntityGraph;

import java.util.HashMap;
import java.util.Map;

/** This implementation of a projection entity graph registry creates graphs only when requested
 * and stores them in memory thereafter for faster subsequent access. */
public class LazyCachingProjectionEntityGraphRegistry implements ProjectionEntityGraphRegistry {

    private final ProjectionEntityGraphFactory projectionEntityGraphFactory;
    private final Map<Class<?>, JpaEntityGraph> cache = new HashMap<>();

    public LazyCachingProjectionEntityGraphRegistry(ProjectionEntityGraphFactory projectionEntityGraphFactory) {
        this.projectionEntityGraphFactory = projectionEntityGraphFactory;
    }

    @Override
    public JpaEntityGraph getEntityGraphForProjection(Class<?> domainType, Class<?> projectionType) {
        if (this.cache.containsKey(projectionType)) {
            return this.cache.get(projectionType);
        } else {
            final var entityGraph = projectionEntityGraphFactory.createEntityGraphForProjection(domainType, projectionType);
            this.cache.put(projectionType, entityGraph);
            return entityGraph;
        }
    }
}
