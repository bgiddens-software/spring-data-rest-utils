package com.bgiddens.sdr.config;

import com.bgiddens.pbac.PartitionResolverConfig;
import com.bgiddens.pbac.PartitionSecurityContextHolder;
import com.bgiddens.pbac.access.AccessRegistry;
import com.bgiddens.pbac.access.AuthenticationPartitionResolver;
import com.bgiddens.pbac.graph.CachingPartitionableMetadataService;
import com.bgiddens.pbac.graph.PartitionableMetadataService;
import com.bgiddens.pbac.resolver.DefaultPartitionPathResolver;
import com.bgiddens.pbac.resolver.DefaultPartitionResolver;
import com.bgiddens.pbac.resolver.DefaultPartitionableClassScanner;
import com.bgiddens.pbac.resolver.PartitionPathResolver;
import com.bgiddens.pbac.resolver.PartitionPredicateBuilder;
import com.bgiddens.pbac.resolver.PartitionResolver;
import com.bgiddens.pbac.resolver.PartitionableClassScanner;
import com.bgiddens.sdr.pbac.PartitioningQuerydslAwareRootResourceInformationHandlerMethodArgumentResolver;
import com.bgiddens.sdr.pbac.RepositoryInvokerFactoryAdviceConfig;
import org.springframework.aop.Advisor;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.querydsl.QuerydslUtils;
import org.springframework.data.querydsl.SimpleEntityPathResolver;
import org.springframework.data.querydsl.binding.QuerydslBindingsFactory;
import org.springframework.data.querydsl.binding.QuerydslPredicateBuilder;
import org.springframework.data.repository.support.Repositories;
import org.springframework.data.repository.support.RepositoryInvokerFactory;
import org.springframework.data.rest.webmvc.config.ResourceMetadataHandlerMethodArgumentResolver;
import org.springframework.data.rest.webmvc.config.RootResourceInformationHandlerMethodArgumentResolver;

@AutoConfiguration
@EnableConfigurationProperties({ PartitionAuthorizationConfig.class, PartitionResolverConfig.class })
@ConditionalOnProperty(value = "bgiddens.spring-data-rest-pbac.enabled", matchIfMissing = true)
public final class PBACAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	PartitionableClassScanner defaultPartitionableClassScanner(PartitionResolverConfig config) {
		return new DefaultPartitionableClassScanner(config.getPackages());
	}

	@Bean
	@ConditionalOnMissingBean
	PartitionableMetadataService cachingPartitionableMetadataService(PartitionableClassScanner partitionableClassScanner,
			PartitionResolverConfig partitionResolverConfig) {
		var service = new CachingPartitionableMetadataService(partitionableClassScanner, partitionResolverConfig);
		service.cachePartitionableObjects();
		return service;
	}

	@Bean
	@ConditionalOnMissingBean
	PartitionResolver defaultPartitionResolver(PartitionableMetadataService partitionableMetadataService) {
		return new DefaultPartitionResolver(partitionableMetadataService);
	}

	@Bean
	@ConditionalOnMissingBean
	PartitionPathResolver defaultPartitionPathResolver(PartitionableMetadataService partitionableMetadataService) {
		return new DefaultPartitionPathResolver(partitionableMetadataService, new SimpleEntityPathResolver(""));
	}

	@Bean
	@ConditionalOnMissingBean
	PartitionPredicateBuilder partitionPredicateBuilder(PartitionPathResolver partitionPathResolver,
			AccessRegistry accessRegistry, PartitionSecurityContextHolder partitionSecurityContextHolder) {
		return new PartitionPredicateBuilder(partitionPathResolver, accessRegistry, partitionSecurityContextHolder);
	}

	@Bean
	@ConditionalOnMissingBean
	@Scope(scopeName = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
	PartitionSecurityContextHolder partitionSecurityContextHolder(
			AuthenticationPartitionResolver authenticationPartitionResolver) {
		return new PartitionSecurityContextHolder(authenticationPartitionResolver);
	}

	@Bean
	@Primary
	@ConditionalOnProperty(
			value = "bgiddens.spring-data-rest-pbac.replace-root-resource-information-handler-method-argument-resolver",
			matchIfMissing = true)
	RootResourceInformationHandlerMethodArgumentResolver partitioningQuerydslAwareRootResourceInformationHandlerMethodArgumentResolver(
			QuerydslBindingsFactory querydslBindingsFactory, Repositories repositories,
			RepositoryInvokerFactory repositoryInvokerFactory,
			ResourceMetadataHandlerMethodArgumentResolver resourceMetadataHandlerMethodArgumentResolver,
			ConversionService defaultConversionService, PartitionPredicateBuilder partitionPredicateBuilder) {
		if (QuerydslUtils.QUERY_DSL_PRESENT) {
			QuerydslPredicateBuilder predicateBuilder = new QuerydslPredicateBuilder(defaultConversionService,
					querydslBindingsFactory.getEntityPathResolver());

			return new PartitioningQuerydslAwareRootResourceInformationHandlerMethodArgumentResolver(repositories,
					repositoryInvokerFactory, resourceMetadataHandlerMethodArgumentResolver, predicateBuilder,
					querydslBindingsFactory, partitionPredicateBuilder);
		}
		return null;
	}

	@Bean
	@ConditionalOnMissingBean
	Advisor repositoryInvokerFactoryAdvisor(ObjectFactory<AccessRegistry> accessRegistry,
			ObjectFactory<PartitionAuthorizationConfig> partitionAuthorizationConfig,
			ObjectFactory<PartitionSecurityContextHolder> partitionSecurityContextHolder,
			ObjectFactory<PartitionResolver> partitionResolver) {
		return new RepositoryInvokerFactoryAdviceConfig(accessRegistry, partitionAuthorizationConfig,
				partitionSecurityContextHolder, partitionResolver).repositoryInvokerFactoryAdvisor();
	}
}
