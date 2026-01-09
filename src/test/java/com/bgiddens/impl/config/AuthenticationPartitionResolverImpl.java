package com.bgiddens.impl.config;

import com.bgiddens.pbac.access.AuthenticationPartitionResolver;
import com.bgiddens.impl.repos.UserRepo;
import com.bgiddens.pbac.resolver.PartitionResolver;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@AllArgsConstructor
public class AuthenticationPartitionResolverImpl implements AuthenticationPartitionResolver {

	private final PartitionResolver partitionResolver;
	private final UserRepo userRepo;

	@Override
	public Collection<Object> resolve(String basis, Authentication authentication) {
		return partitionResolver.resolvePartitions(basis,
				userRepo.findFirstByPrincipal(authentication.getName()).orElseThrow());
	}
}
