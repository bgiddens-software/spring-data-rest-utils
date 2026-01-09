package com.bgiddens.pbac.resolver;

import com.querydsl.core.types.dsl.SimpleExpression;

import java.util.Collection;

public interface PartitionPathResolver {
	Collection<SimpleExpression<Object>> resolvePartitionExpressions(String basis, Class<?> domainType)
			throws PartitionConfigurationException;
}
