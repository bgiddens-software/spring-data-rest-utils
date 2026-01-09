package com.bgiddens.pbac.resolver;

import java.util.Collection;

public interface PartitionResolver {
	Collection<Object> resolvePartitions(String basis, Object entity) throws PartitionConfigurationException;
}
