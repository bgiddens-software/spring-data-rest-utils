package com.bgiddens.pbac.access;

import org.springframework.security.core.Authentication;

import java.util.Collection;

public interface AuthenticationPartitionResolver {
	Collection<Object> resolve(String basis, Authentication authentication);
}
