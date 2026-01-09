package com.bgiddens.pbac;

import com.bgiddens.pbac.access.AuthenticationPartitionResolver;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class PartitionSecurityContextHolder {

	private final AuthenticationPartitionResolver authenticationPartitionResolver;
	private final Map<String, Collection<Object>> partitions = new HashMap<>();

	private Collection<Object> lookupPartitionsForAuthorizedUser(String basis) {
		var securityContext = SecurityContextHolder.getContext().getAuthentication();
		return authenticationPartitionResolver.resolve(basis, securityContext);
	}

	public Collection<Object> getPartitionsForAuthorizedUser(String basis) {
		if (!partitions.containsKey(basis)) {
			partitions.put(basis, lookupPartitionsForAuthorizedUser(basis));
		}
		return partitions.get(basis);
	}
}
