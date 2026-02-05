package com.bgiddens.sdr.config;

import com.bgiddens.pbac.PartitionSecurityContextHolder;
import com.bgiddens.pbac.access.AccessRegistry;
import com.bgiddens.pbac.resolver.PartitionResolver;
import com.bgiddens.sdr.pbac.RepositoryInvokerFactoryAdviceConfig;
import org.springframework.aop.Advisor;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

@Configuration(proxyBeanMethods = false)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class PBACAdvisorConfiguration {
	@Bean
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	Advisor repositoryInvokerFactoryAdvisor(ObjectFactory<AccessRegistry> accessRegistry,
			ObjectFactory<PartitionAuthorizationConfig> partitionAuthorizationConfig,
			ObjectFactory<PartitionSecurityContextHolder> partitionSecurityContextHolder,
			ObjectFactory<PartitionResolver> partitionResolver) {
		return new RepositoryInvokerFactoryAdviceConfig(accessRegistry, partitionAuthorizationConfig,
				partitionSecurityContextHolder, partitionResolver).repositoryInvokerFactoryAdvisor();
	}
}
