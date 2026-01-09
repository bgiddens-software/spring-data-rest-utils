package com.bgiddens.pbac.access;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public class PartitionedAccessLevel {
	private final AccessLevel accessLevel;
	private final Set<String> partitions;

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
