package com.bgiddens.sdr.pbac;

import com.bgiddens.pbac.resolver.PartitionPredicateBuilder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.querydsl.QuerydslUtils;
import org.springframework.data.querydsl.binding.QuerydslBindingsFactory;
import org.springframework.data.querydsl.binding.QuerydslPredicateBuilder;
import org.springframework.data.repository.support.Repositories;
import org.springframework.data.repository.support.RepositoryInvokerFactory;
import org.springframework.data.rest.webmvc.config.ResourceMetadataHandlerMethodArgumentResolver;
import org.springframework.data.rest.webmvc.config.RootResourceInformationHandlerMethodArgumentResolver;

/**
 * For lack of a better integration interface, just replace the already-kind-of-klugey Querydsl-aware resolver with an
 * even klugier version. Maybe someday we can refactor the SDR interface to make this kind of extension cleaner.
 */
public class PartitioningQuerydslAwareRootResourceInformationArgumentResolverConfig implements BeanPostProcessor {

	public PartitioningQuerydslAwareRootResourceInformationArgumentResolverConfig(ApplicationContext applicationContext,
			Repositories repositories, RepositoryInvokerFactory repositoryInvokerFactory,
			ResourceMetadataHandlerMethodArgumentResolver resourceMetadataHandlerMethodArgumentResolver,
			ConversionService defaultConversionService, PartitionPredicateBuilder partitionPredicateBuilder) {
		this.applicationContext = applicationContext;
		this.repositories = repositories;
		this.repositoryInvokerFactory = repositoryInvokerFactory;
		this.resourceMetadataHandlerMethodArgumentResolver = resourceMetadataHandlerMethodArgumentResolver;
		this.defaultConversionService = defaultConversionService;
		this.partitionPredicateBuilder = partitionPredicateBuilder;
	}

	private final ApplicationContext applicationContext;
	private final Repositories repositories;
	private final RepositoryInvokerFactory repositoryInvokerFactory;
	private final ResourceMetadataHandlerMethodArgumentResolver resourceMetadataHandlerMethodArgumentResolver;
	private final ConversionService defaultConversionService;
	private final PartitionPredicateBuilder partitionPredicateBuilder;

	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		if (RootResourceInformationHandlerMethodArgumentResolver.class.isAssignableFrom(bean.getClass())) {
			if (QuerydslUtils.QUERY_DSL_PRESENT) {

				QuerydslBindingsFactory factory = applicationContext.getBean(QuerydslBindingsFactory.class);
				QuerydslPredicateBuilder predicateBuilder = new QuerydslPredicateBuilder(defaultConversionService,
						factory.getEntityPathResolver());

				return new PartitioningQuerydslAwareRootResourceInformationHandlerMethodArgumentResolver(repositories,
						repositoryInvokerFactory, resourceMetadataHandlerMethodArgumentResolver, predicateBuilder, factory,
						partitionPredicateBuilder);
			}
		}
		return bean;
	}
}
