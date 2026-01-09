package com.bgiddens.pbac;

import com.bgiddens.pbac.access.AccessLevel;
import com.bgiddens.pbac.access.AccessRegistry;
import com.bgiddens.pbac.resolver.PartitionResolver;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.support.RepositoryInvoker;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.Method;
import java.util.Optional;

public class PartitionAuthorizingRepositoryInvoker implements RepositoryInvoker {

	public PartitionAuthorizingRepositoryInvoker(RepositoryInvoker delegate, AccessRegistry accessRegistry,
			Class<?> domainType, PartitionSecurityContextHolder partitionSecurityContextHolder,
			PartitionResolver partitionResolver, boolean reverifyDelete) {
		this.delegate = delegate;
		this.accessRegistry = accessRegistry;
		this.domainType = domainType;
		this.partitionSecurityContextHolder = partitionSecurityContextHolder;
		this.partitionResolver = partitionResolver;
		this.reverifyDelete = reverifyDelete;
	}

	private final RepositoryInvoker delegate;
	private final AccessRegistry accessRegistry;
	private final Class<?> domainType;
	private final PartitionSecurityContextHolder partitionSecurityContextHolder;
	private final PartitionResolver partitionResolver;
	private final boolean reverifyDelete;

	private Boolean authorizeEntity(Object object) {
		final var authentication = SecurityContextHolder.getContext().getAuthentication();
		var accessLevel = accessRegistry.getAccessLevel(authentication, object.getClass(), object);
		if (accessLevel.getAccessLevel() == AccessLevel.NONE) {
			return false;
		} else if (accessLevel.getAccessLevel() == AccessLevel.FULL) {
			return true;
		}
		return accessLevel.getPartitions().stream()
				.allMatch(basis -> partitionSecurityContextHolder.getPartitionsForAuthorizedUser(basis).stream()
						.anyMatch(partition -> partitionResolver.resolvePartitions(basis, object).contains(partition)));
	}

	@Override
	public <T> T invokeSave(T object) {
		if (!authorizeEntity(object)) {
			throw new AccessDeniedException("Access denied");
		}
		return delegate.invokeSave(object);
	}

	@Override
	public <T> Optional<T> invokeFindById(Object id) {
		Optional<T> entity = delegate.invokeFindById(id);
		if (entity.isPresent() && !authorizeEntity(entity.get())) {
			throw new AccessDeniedException("Access denied");
		}
		return entity;
	}

	@Override
	public Iterable<Object> invokeFindAll(Pageable pageable) {
		var authentication = SecurityContextHolder.getContext().getAuthentication();
		var accessLevel = accessRegistry.getAccessLevel(authentication, domainType, null);
		if (accessLevel.getAccessLevel() == AccessLevel.FULL) {
			return delegate.invokeFindAll(pageable);
		} else {
			throw new AccessDeniedException("Access denied");
		}
	}

	@Override
	public Iterable<Object> invokeFindAll(Sort sort) {
		var authentication = SecurityContextHolder.getContext().getAuthentication();
		var accessLevel = accessRegistry.getAccessLevel(authentication, domainType, null);
		if (accessLevel.getAccessLevel() == AccessLevel.FULL) {
			return delegate.invokeFindAll(sort);
		} else {
			throw new AccessDeniedException("Access denied");
		}
	}

	@Override
	public void invokeDeleteById(Object id) {
		if (this.reverifyDelete) {
			this.invokeFindById(id);
		}
		delegate.invokeDeleteById(id);
	}

	@Override
	public Optional<Object> invokeQueryMethod(Method method, MultiValueMap<String, ?> parameters, Pageable pageable,
			Sort sort) {
		return delegate.invokeQueryMethod(method, parameters, pageable, sort);
	}

	@Override
	public boolean hasSaveMethod() {
		return delegate.hasSaveMethod();
	}

	@Override
	public boolean hasDeleteMethod() {
		return delegate.hasDeleteMethod();
	}

	@Override
	public boolean hasFindOneMethod() {
		return delegate.hasFindOneMethod();
	}

	@Override
	public boolean hasFindAllMethod() {
		return delegate.hasFindAllMethod();
	}
}
