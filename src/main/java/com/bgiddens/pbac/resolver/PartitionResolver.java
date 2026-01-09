package com.bgiddens.pbac.resolver;

import com.bgiddens.pbac.exceptions.PartitionConfigurationException;

import java.util.Collection;

public interface PartitionResolver {
	Collection<Object> resolvePartitions(String basis, Object entity) throws PartitionConfigurationException;
}
