package com.bgiddens.impl.config;

import com.bgiddens.pbac.access.AccessRegistry;
import com.bgiddens.pbac.access.PartitionedAccessLevel;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class AccessRegistryImpl implements AccessRegistry {
	@Override
	public PartitionedAccessLevel getAccessLevel(Authentication authentication, Class<?> domainType, Object target) {
		if (authentication != null && authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList()
				.contains("ROLE_ADMIN")) {
			return PartitionedAccessLevel.full();
		} else {
			return PartitionedAccessLevel.of("department", "region", "country");
		}
	}
}
