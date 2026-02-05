/*
 * Copyright 2015-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bgiddens.sdr.pbac;

import com.bgiddens.pbac.resolver.PartitionPredicateBuilder;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.springframework.core.MethodParameter;
import org.springframework.data.core.TypeInformation;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.QuerydslRepositoryInvokerAdapter;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.QuerydslBindingsFactory;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.querydsl.binding.QuerydslPredicateBuilder;
import org.springframework.data.repository.support.Repositories;
import org.springframework.data.repository.support.RepositoryInvoker;
import org.springframework.data.repository.support.RepositoryInvokerFactory;
import org.springframework.data.rest.webmvc.RootResourceInformation;
import org.springframework.data.rest.webmvc.config.ResourceMetadataHandlerMethodArgumentResolver;
import org.springframework.data.rest.webmvc.config.RootResourceInformationHandlerMethodArgumentResolver;
import org.springframework.data.util.Pair;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

/**
 * {@link HandlerMethodArgumentResolver} to create {@link RootResourceInformation} for injection into Spring MVC
 * controller methods. This class is largely redundant with the version in SDR, but functionality had to be duplicated
 * to extend its predicate-building features.
 *
 * @author Oliver Gierke
 * @author Mark Paluch
 * @author Ben Giddens
 */
public class PartitioningQuerydslAwareRootResourceInformationHandlerMethodArgumentResolver
		extends RootResourceInformationHandlerMethodArgumentResolver {

	private final Repositories repositories;
	private final QuerydslPredicateBuilder predicateBuilder;
	private final QuerydslBindingsFactory factory;
	private final PartitionPredicateBuilder partitionPredicateBuilder;

	/**
	 * Creates a new {@link PartitioningQuerydslAwareRootResourceInformationHandlerMethodArgumentResolver} using the given
	 * {@link Repositories}, {@link RepositoryInvokerFactory} and {@link ResourceMetadataHandlerMethodArgumentResolver}.
	 *
	 * @param repositories must not be {@literal null}.
	 * @param invokerFactory must not be {@literal null}.
	 * @param resourceMetadataResolver must not be {@literal null}.
	 */
	public PartitioningQuerydslAwareRootResourceInformationHandlerMethodArgumentResolver(Repositories repositories,
			RepositoryInvokerFactory invokerFactory, ResourceMetadataHandlerMethodArgumentResolver resourceMetadataResolver,
			QuerydslPredicateBuilder predicateBuilder, QuerydslBindingsFactory factory,
			PartitionPredicateBuilder partitionPredicateBuilder) {

		super(repositories, invokerFactory, resourceMetadataResolver);

		this.repositories = repositories;
		this.predicateBuilder = predicateBuilder;
		this.factory = factory;
		this.partitionPredicateBuilder = partitionPredicateBuilder;
	}

	@Override
	protected RepositoryInvoker postProcess(MethodParameter parameter, RepositoryInvoker invoker, Class<?> domainType,
			Map<String, String[]> parameters) {

		if (!parameter.hasParameterAnnotation(QuerydslPredicate.class)) {
			return invoker;
		}

		return repositories.getRepositoryFor(domainType) //
				.filter(it -> QuerydslPredicateExecutor.class.isInstance(it)) //
				.map(it -> QuerydslPredicateExecutor.class.cast(it)) //
				.flatMap(it -> getRepositoryAndPredicate(it, domainType, parameters)) //
				.map(it -> getQuerydslAdapter(invoker, it.getFirst(), it.getSecond())) //
				.orElse(invoker);
	}

	private Optional<Pair<QuerydslPredicateExecutor<?>, Predicate>> getRepositoryAndPredicate(
			QuerydslPredicateExecutor<?> repository, Class<?> domainType, Map<String, String[]> parameters) {

		TypeInformation<?> type = TypeInformation.of(domainType);

		QuerydslBindings bindings = factory.createBindingsFor(type);
		Predicate predicate = predicateBuilder.getPredicate(type, toMultiValueMap(parameters), bindings);
		var partitionPredicate = partitionPredicateBuilder.buildPredicate(domainType);

		return Optional.of(new BooleanBuilder(partitionPredicate).and(predicate)).map(it -> Pair.of(repository, it));
	}

	@SuppressWarnings("unchecked")
	private static RepositoryInvoker getQuerydslAdapter(RepositoryInvoker invoker,
			QuerydslPredicateExecutor<?> repository, Predicate predicate) {
		return new QuerydslRepositoryInvokerAdapter(invoker, (QuerydslPredicateExecutor<Object>) repository, predicate);
	}

	/**
	 * Converts the given Map into a {@link MultiValueMap}.
	 *
	 * @param source must not be {@literal null}.
	 * @return
	 */
	private static MultiValueMap<String, String> toMultiValueMap(Map<String, String[]> source) {

		MultiValueMap<String, String> result = new LinkedMultiValueMap<String, String>();

		for (Entry<String, String[]> entry : source.entrySet()) {
			result.put(entry.getKey(), Arrays.asList(entry.getValue()));
		}

		return result;
	}
}
