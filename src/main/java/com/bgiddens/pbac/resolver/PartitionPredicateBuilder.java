package com.bgiddens.pbac.resolver;

import com.bgiddens.pbac.access.AccessLevel;
import com.bgiddens.pbac.access.AccessRegistry;
import com.bgiddens.pbac.PartitionSecurityContextHolder;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;

@RequiredArgsConstructor
public class PartitionPredicateBuilder {

	private final PartitionPathResolver partitionPathResolver;
	private final AccessRegistry accessRegistry;
	private final PartitionSecurityContextHolder partitionSecurityContextHolder;

	public Predicate buildPredicate(Class<?> domainType) {
		var accessLevel = accessRegistry.getAccessLevel(SecurityContextHolder.getContext().getAuthentication(), domainType,
				null);
		if (accessLevel.getAccessLevel() == AccessLevel.FULL) {
			return new BooleanBuilder();
		} else if (accessLevel.getAccessLevel() == AccessLevel.NONE) {
			throw new AccessDeniedException("Access Denied");
		} else {
			var predicate = new BooleanBuilder();
			accessLevel.getPartitions()
					.forEach(partition -> partitionPathResolver.resolvePartitionExpressions(partition, domainType)
							.forEach(path -> predicate
									.and(path.in(partitionSecurityContextHolder.getPartitionsForAuthorizedUser(partition)))));
			return predicate;
		}
	}
}
