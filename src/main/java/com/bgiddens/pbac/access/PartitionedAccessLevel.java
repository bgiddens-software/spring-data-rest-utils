package com.bgiddens.pbac.access;

import java.util.Set;

public class PartitionedAccessLevel {

	public PartitionedAccessLevel(AccessLevel accessLevel, Set<String> partitions) {
		this.accessLevel = accessLevel;
		this.partitions = partitions;
	}

	private final AccessLevel accessLevel;
	private final Set<String> partitions;

	public AccessLevel getAccessLevel() {
		return accessLevel;
	}

	public Set<String> getPartitions() {
		return partitions;
	}

	public static PartitionedAccessLevel full() {
		return new PartitionedAccessLevel(AccessLevel.FULL, Set.of());
	}

	public static PartitionedAccessLevel none() {
		return new PartitionedAccessLevel(AccessLevel.NONE, Set.of());
	}

	public static PartitionedAccessLevel of(String... partitions) {
		return new PartitionedAccessLevel(AccessLevel.BY_PARTITION, Set.of(partitions));
	}
}
