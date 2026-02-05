package com.bgiddens.sdr.repository;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.EntityPathBase;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;
import java.util.Optional;

/** Interface for REST repositories with querying functionality. */
@NoRepositoryBean
public interface QueryingRepository<E, Q extends EntityPathBase<E>, I>
		extends ExpectingJpaRepository<E, I>, QuerydslPredicateExecutor<E>, QueryingBinderCustomizer<Q> {
	@Override
	@RestResource(exported = true)
	@NonNull
	Page<E> findAll(@NonNull Predicate predicate, @NonNull Pageable pageable);

	@Override
	@RestResource(exported = true)
	@NonNull
	Page<E> findAll(@NonNull Pageable pageable);

	@Override
	@RestResource(exported = false)
	@NonNull
	List<E> findAll(@NonNull Sort sort);

	@Override
	@RestResource(exported = false)
	@NonNull
	List<E> findAll();

	@Override
	@RestResource(exported = true)
	@NonNull
	<S extends E> S save(@NonNull S entity);

	@Override
	@RestResource(exported = true)
	@NonNull
	Optional<E> findById(@NonNull I id);

	@Override
	@RestResource(exported = true)
	void delete(@NonNull E entity);

	@Override
	@RestResource(exported = true)
	void deleteById(@NonNull I id);
}
