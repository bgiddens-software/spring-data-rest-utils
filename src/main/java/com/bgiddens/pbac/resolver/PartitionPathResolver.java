package com.bgiddens.pbac.resolver;

import com.bgiddens.pbac.exceptions.PartitionConfigurationException;
import com.querydsl.core.types.dsl.SimpleExpression;
import org.jspecify.annotations.NonNull;

import java.util.Collection;

public interface PartitionPathResolver {
	Collection<SimpleExpression<Object>> resolvePartitionExpressions(@NonNull String basis, @NonNull Class<?> domainType)
			throws PartitionConfigurationException;
}
