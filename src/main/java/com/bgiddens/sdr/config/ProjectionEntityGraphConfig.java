package com.bgiddens.sdr.config;

import com.bgiddens.projection.LazyCachingProjectionEntityGraphRegistry;
import com.bgiddens.projection.ProjectionContextProvider;
import com.bgiddens.projection.ProjectionEntityGraphAwareJpaRepositoryFactoryBeanPostProcessor;
import com.bgiddens.projection.ProjectionEntityGraphFactory;
import com.bgiddens.projection.ProjectionEntityGraphRegistry;
import com.bgiddens.projection.RequestParameterProjectionContextProvider;
import jakarta.persistence.EntityManager;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.web.context.request.NativeWebRequest;

@AutoConfiguration
@ConditionalOnProperty(value = "bgiddens.sdr.projection-entity-graphs.enabled", matchIfMissing = true)
public class ProjectionEntityGraphConfig {
	@Bean
	@ConditionalOnMissingBean
	public ProjectionContextProvider defaultProjectionContextProvider(NativeWebRequest webRequest,
			@Lazy RepositoryRestConfiguration repositoryRestConfiguration) {
		return new RequestParameterProjectionContextProvider(webRequest, repositoryRestConfiguration, "projection");
	}

	@Bean
	@ConditionalOnMissingBean
	public ProjectionEntityGraphRegistry defaultProjectionEntityGraphRegistry(EntityManager entityManager) {
		return new LazyCachingProjectionEntityGraphRegistry(new ProjectionEntityGraphFactory(entityManager));
	}

	@Bean
	public ProjectionEntityGraphAwareJpaRepositoryFactoryBeanPostProcessor projectionEntityGraphAwareJpaRepositoryFactoryBeanPostProcessor(
			@Lazy ProjectionEntityGraphRegistry projectionEntityGraphRegistry,
			@Lazy ProjectionContextProvider projectionContextProvider) {
		return new ProjectionEntityGraphAwareJpaRepositoryFactoryBeanPostProcessor(projectionEntityGraphRegistry,
				projectionContextProvider);
	}
}
