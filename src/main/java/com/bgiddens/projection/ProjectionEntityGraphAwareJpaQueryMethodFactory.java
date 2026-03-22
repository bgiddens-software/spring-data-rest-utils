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

    public ProjectionEntityGraphAwareJpaQueryMethodFactory(QueryExtractor extractor, ProjectionEntityGraphRegistry projectionEntityGraphRegistry) {
        Assert.notNull(extractor, "QueryExtractor must not be null");
        this.extractor = extractor;
        this.projectionEntityGraphRegistry = projectionEntityGraphRegistry;
    }

    @Override
    @NonNull
    public JpaQueryMethod build(@NonNull Method method, @NonNull RepositoryMetadata metadata, @NonNull ProjectionFactory factory) {
        // todo - get the projection from the request parameter
        return new DynamicEntityGraphJpaQueryMethod(method, metadata, factory, extractor, projectionEntityGraphRegistry.getEntityGraphForProjection(metadata.getDomainType(), method.getReturnType()));
    }
}
