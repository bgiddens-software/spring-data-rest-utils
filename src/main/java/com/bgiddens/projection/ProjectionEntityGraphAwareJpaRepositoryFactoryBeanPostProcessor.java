package com.bgiddens.projection;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.support.RepositoryFactoryCustomizer;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

public class ProjectionEntityGraphAwareJpaRepositoryFactoryBeanPostProcessor implements BeanPostProcessor {

	private final ProjectionEntityGraphAwareJpaRepositoryFactoryCustomizer customizer;

	public ProjectionEntityGraphAwareJpaRepositoryFactoryBeanPostProcessor(
			ProjectionEntityGraphRegistry projectionEntityGraphRegistry,
			ProjectionContextProvider projectionContextProvider) {
		this.customizer = new ProjectionEntityGraphAwareJpaRepositoryFactoryCustomizer(projectionEntityGraphRegistry,
				projectionContextProvider);
	}

	public @Nullable Object postProcessBeforeInitialization(@NonNull Object bean, @NonNull String beanName)
			throws BeansException {
		if (bean instanceof JpaRepositoryFactoryBean<?, ?, ?> factory) {
			factory.addRepositoryFactoryCustomizer(customizer);
		}
		return bean;
	}
}
