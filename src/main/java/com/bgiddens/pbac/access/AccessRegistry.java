package com.bgiddens.pbac.access;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.Authentication;

public interface AccessRegistry {

	PartitionedAccessLevel getAccessLevel(Authentication authentication, Class<?> domainType, @Nullable Object target);
}
