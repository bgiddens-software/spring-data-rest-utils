package com.bgiddens.projection;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.query.JpaEntityGraph;

import java.lang.annotation.Annotation;

public class DynamicEntityGraph implements EntityGraph {

	private final EntityGraphType entityGraphType;
	private final String[] attributePaths;

	public DynamicEntityGraph(@NonNull EntityGraphType entityGraphType, @NonNull String[] attributePaths) {
		this.entityGraphType = entityGraphType;
		this.attributePaths = attributePaths;
	}

	public static DynamicEntityGraph of(JpaEntityGraph entityGraph) {
		return new DynamicEntityGraph(EntityGraphType.FETCH, entityGraph.getAttributePaths().toArray(String[]::new));
	}

	@Override
	@NonNull
	public String value() {
		return "";
	}

	@Override
	@NonNull
	public EntityGraphType type() {
		return entityGraphType;
	}

	@Override
	@Nullable
	public String[] attributePaths() {
		return attributePaths;
	}

	@Override
	public Class<? extends Annotation> annotationType() {
		return EntityGraph.class;
	}
}
