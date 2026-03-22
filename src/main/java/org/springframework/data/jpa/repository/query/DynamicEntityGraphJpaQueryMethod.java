package org.springframework.data.jpa.repository.query;

import org.springframework.data.jpa.provider.QueryExtractor;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.RepositoryMetadata;

import java.lang.reflect.Method;

public class DynamicEntityGraphJpaQueryMethod extends JpaQueryMethod {

	private final JpaEntityGraph entityGraph;

	public DynamicEntityGraphJpaQueryMethod(Method method, RepositoryMetadata metadata, ProjectionFactory factory,
			QueryExtractor extractor, JpaEntityGraph entityGraph) {
		super(method, metadata, factory, extractor);
		this.entityGraph = entityGraph;
	}

	@Override
	JpaEntityGraph getEntityGraph() {
		return (this.entityGraph != null) ? this.entityGraph : super.getEntityGraph();
	}
}
