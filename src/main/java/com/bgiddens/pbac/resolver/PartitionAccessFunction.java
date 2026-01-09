package com.bgiddens.pbac.resolver;

import com.bgiddens.pbac.exceptions.PartitionConfigurationException;

import java.util.Set;

@FunctionalInterface
public interface PartitionAccessFunction {
	Set<Object> apply(Object t) throws PartitionConfigurationException;
}
