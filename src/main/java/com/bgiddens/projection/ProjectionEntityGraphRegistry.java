package com.bgiddens.projection;

import org.springframework.data.jpa.repository.query.JpaEntityGraph;

public interface ProjectionEntityGraphRegistry {

    JpaEntityGraph getEntityGraphForProjection(Class<?> domainType, Class<?> projectionType);
}
