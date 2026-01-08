package com.bgiddens.sdr.repository;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.EntityPathBase;
import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.rest.core.annotation.RestResource;

/** Interface for REST repositories with querying functionality. */
public interface QueryingRepository<E, Q extends EntityPathBase<E>, I>
		extends CustomJpaRepository<E, I>, QuerydslPredicateExecutor<E>, CustomBinderCustomizer<Q> {
	@Override
	@RestResource(exported = true)
	@Nonnull
	Page<E> findAll(@Nonnull Predicate predicate, @Nonnull Pageable pageable);

	@Override
	@RestResource(exported = true)
	@Nonnull
	Page<E> findAll(@Nonnull Pageable pageable);

	@Override
	@RestResource(exported = false)
	@Nonnull
	List<E> findAll(@Nonnull Sort sort);

	@Override
	@RestResource(exported = false)
	@Nonnull
	List<E> findAll();

	@Override
	@RestResource(exported = true)
	@Nonnull
	<S extends E> S save(@Nonnull S entity);

	@Override
	@RestResource(exported = true)
	@Nonnull
	Optional<E> findById(@Nonnull I id);

	@Override
	@RestResource(exported = true)
	void delete(@Nonnull E entity);

	@Override
	@RestResource(exported = true)
	void deleteById(@Nonnull I id);
}
