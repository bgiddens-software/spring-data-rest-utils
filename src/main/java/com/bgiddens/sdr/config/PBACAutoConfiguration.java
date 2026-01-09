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
import com.bgiddens.sdr.pbac.PartitioningQuerydslAwareRootResourceInformationArgumentResolverConfig;
import com.bgiddens.sdr.pbac.RepositoryInvokerFactoryAdviceConfig;
import org.springframework.aop.Advisor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.querydsl.SimpleEntityPathResolver;
import org.springframework.data.repository.support.Repositories;
import org.springframework.data.repository.support.RepositoryInvokerFactory;
import org.springframework.data.rest.webmvc.config.ResourceMetadataHandlerMethodArgumentResolver;

@AutoConfiguration
@EnableConfigurationProperties({ PartitionAuthorizationConfig.class, PartitionResolverConfig.class })
@ConditionalOnProperty(value = "bgiddens.spring-data-rest-pbac.enabled", matchIfMissing = true)
public class PBACAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public PartitionableClassScanner defaultPartitionableClassScanner(PartitionResolverConfig config) {
		return new DefaultPartitionableClassScanner(config.getPackages());
	}

	@Bean
	@ConditionalOnMissingBean
	public PartitionableMetadataService cachingPartitionableMetadataService(
			PartitionableClassScanner partitionableClassScanner, PartitionResolverConfig partitionResolverConfig) {
		var service = new CachingPartitionableMetadataService(partitionableClassScanner, partitionResolverConfig);
		service.cachePartitionableObjects();
		return service;
	}

	@Bean
	@ConditionalOnMissingBean
	public PartitionResolver defaultPartitionResolver(PartitionableMetadataService partitionableMetadataService) {
		return new DefaultPartitionResolver(partitionableMetadataService);
	}

	@Bean
	@ConditionalOnMissingBean
	public PartitionPathResolver defaultPartitionPathResolver(PartitionableMetadataService partitionableMetadataService) {
		return new DefaultPartitionPathResolver(partitionableMetadataService, new SimpleEntityPathResolver(""));
	}

	@Bean
	@ConditionalOnMissingBean
	public PartitionPredicateBuilder partitionPredicateBuilder(PartitionPathResolver partitionPathResolver,
			AccessRegistry accessRegistry, PartitionSecurityContextHolder partitionSecurityContextHolder) {
		return new PartitionPredicateBuilder(partitionPathResolver, accessRegistry, partitionSecurityContextHolder);
	}

	@Bean
	@ConditionalOnMissingBean
	@Scope(scopeName = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
	public PartitionSecurityContextHolder partitionSecurityContextHolder(
			AuthenticationPartitionResolver authenticationPartitionResolver) {
		return new PartitionSecurityContextHolder(authenticationPartitionResolver);
	}

	@Bean
	@ConditionalOnMissingBean
	public PartitioningQuerydslAwareRootResourceInformationArgumentResolverConfig partitioningQuerydslAwareRootResourceInformationArgumentResolverConfig(
			ApplicationContext applicationContext, Repositories repositories,
			RepositoryInvokerFactory repositoryInvokerFactory,
			ResourceMetadataHandlerMethodArgumentResolver resourceMetadataHandlerMethodArgumentResolver,
			ConversionService defaultConversionService, PartitionPredicateBuilder partitionPredicateBuilder) {
		return new PartitioningQuerydslAwareRootResourceInformationArgumentResolverConfig(applicationContext, repositories,
				repositoryInvokerFactory, resourceMetadataHandlerMethodArgumentResolver, defaultConversionService,
				partitionPredicateBuilder);
	}

	@Bean
	@ConditionalOnMissingBean
	public Advisor repositoryInvokerFactoryAdvisor(AccessRegistry accessRegistry,
			PartitionAuthorizationConfig partitionAuthorizationConfig,
			PartitionSecurityContextHolder partitionSecurityContextHolder, PartitionResolver partitionResolver) {
		return new RepositoryInvokerFactoryAdviceConfig(accessRegistry, partitionAuthorizationConfig,
				partitionSecurityContextHolder, partitionResolver).repositoryInvokerFactoryAdvisor();
	}
}
