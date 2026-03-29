package com.bgiddens.projection;

import org.springframework.data.jpa.repository.EntityGraph;

public interface ProjectionEntityGraphRegistry {

	EntityGraph getEntityGraphForProjection(Class<?> domainType, Class<?> projectionType);
}
