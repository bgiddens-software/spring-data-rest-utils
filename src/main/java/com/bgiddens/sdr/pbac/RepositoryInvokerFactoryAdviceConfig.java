package com.bgiddens.sdr.pbac;

import com.bgiddens.pbac.PartitionAuthorizingRepositoryInvoker;
import com.bgiddens.pbac.PartitionSecurityContextHolder;
import com.bgiddens.pbac.access.AccessRegistry;
import com.bgiddens.pbac.resolver.PartitionResolver;
import com.bgiddens.reflection.ClassAndNameMethodMatcher;
import com.bgiddens.sdr.config.PartitionAuthorizationConfig;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.Advisor;
import org.springframework.aop.Pointcut;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.data.repository.support.RepositoryInvoker;
import org.springframework.data.repository.support.RepositoryInvokerFactory;

public class RepositoryInvokerFactoryAdviceConfig {

	public RepositoryInvokerFactoryAdviceConfig(ObjectFactory<AccessRegistry> accessRegistry,
			ObjectFactory<PartitionAuthorizationConfig> partitionAuthorizationConfig,
			ObjectFactory<PartitionSecurityContextHolder> partitionSecurityContextHolder,
			ObjectFactory<PartitionResolver> partitionResolver) {
		this.accessRegistry = accessRegistry;
		this.partitionAuthorizationConfig = partitionAuthorizationConfig;
		this.partitionSecurityContextHolder = partitionSecurityContextHolder;
		this.partitionResolver = partitionResolver;
	}

	private final ObjectFactory<AccessRegistry> accessRegistry;
	private final ObjectFactory<PartitionAuthorizationConfig> partitionAuthorizationConfig;
	private final ObjectFactory<PartitionSecurityContextHolder> partitionSecurityContextHolder;
	private final ObjectFactory<PartitionResolver> partitionResolver;

	private MethodInterceptor repositoryInvokerFactoryAdvice() {
		return (invocation) -> {
			var res = invocation.proceed();
			if (res instanceof RepositoryInvoker invoker && invocation.getArguments().length == 1
					&& invocation.getArguments()[0] instanceof Class<?> domainType) {
				return new PartitionAuthorizingRepositoryInvoker(invoker, accessRegistry.getObject(), domainType,
						partitionSecurityContextHolder.getObject(), partitionResolver.getObject(),
						partitionAuthorizationConfig.getObject().getCheckDelete());
			} else {
				throw new IllegalStateException(
						"Repository invoker factory advice intercepted an unexpected method invocation");
			}
		};
	}

	private Pointcut repositoryInvokerFactoryPointcut() {
		return new ComposablePointcut(RepositoryInvokerFactory.class::isAssignableFrom,
				new ClassAndNameMethodMatcher("getInvokerFor", RepositoryInvokerFactory.class));
	}

	public Advisor repositoryInvokerFactoryAdvisor() {
		return new PointcutAdvisor() {
			@Override
			public Pointcut getPointcut() {
				return repositoryInvokerFactoryPointcut();
			}

			@Override
			public Advice getAdvice() {
				return repositoryInvokerFactoryAdvice();
			}
		};
	}
}
