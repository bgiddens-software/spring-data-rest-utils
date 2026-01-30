package com.bgiddens.pbac.resolver;

import com.bgiddens.pbac.exceptions.PartitionConfigurationException;
import org.jspecify.annotations.NonNull;

import java.util.Collection;

public interface PartitionResolver {
	Collection<Object> resolvePartitions(@NonNull String basis, @NonNull Object entity) throws PartitionConfigurationException;
}
