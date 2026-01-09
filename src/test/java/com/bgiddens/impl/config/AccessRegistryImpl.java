package com.bgiddens.impl.config;

import com.bgiddens.pbac.access.AccessRegistry;
import com.bgiddens.pbac.access.PartitionedAccessLevel;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class AccessRegistryImpl implements AccessRegistry {
	@Override
	public PartitionedAccessLevel getAccessLevel(Authentication authentication, Class<?> domainType, Object target) {
		return PartitionedAccessLevel.of("department", "region", "country");
	}
}
