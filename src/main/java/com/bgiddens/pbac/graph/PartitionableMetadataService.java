package com.bgiddens.pbac.graph;

import java.util.Collection;

public interface PartitionableMetadataService {
	Collection<PartitionableMetadata> getMetadataFor(Class<?> clazz, String basis);
}
