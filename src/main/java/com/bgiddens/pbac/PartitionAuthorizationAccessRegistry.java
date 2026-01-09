package com.bgiddens.pbac;

import java.nio.file.AccessDeniedException;
import java.util.Set;

/**
 * A class to decide what partition authorization types to apply based on a user's granted authorities. Returning an
 * empty set allows access without checking any partitions. Throwing an AccessDeniedException prohibits access.
 */
public interface PartitionAuthorizationAccessRegistry {
	Set<String> getPartitionTypes(Set<String> authorities) throws AccessDeniedException;
}
