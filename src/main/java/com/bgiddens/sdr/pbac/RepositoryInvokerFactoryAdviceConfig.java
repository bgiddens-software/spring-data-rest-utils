package com.bgiddens.sdr.pbac;

import com.bgiddens.pbac.access.AccessRegistry;
import com.bgiddens.sdr.config.PartitionAuthorizationConfig;
import com.bgiddens.pbac.PartitionAuthorizingRepositoryInvoker;
import com.bgiddens.pbac.PartitionSecurityContextHolder;
import com.bgiddens.pbac.resolver.PartitionResolver;
import com.bgiddens.reflection.ClassAndNameMethodMatcher;
import lombok.AllArgsConstructor;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.Advisor;
import org.springframework.aop.Pointcut;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.data.repository.support.RepositoryInvoker;
import org.springframework.data.repository.support.RepositoryInvokerFactory;

@AllArgsConstructor
public class RepositoryInvokerFactoryAdviceConfig {

	private final AccessRegistry accessRegistry;
	private final PartitionAuthorizationConfig partitionAuthorizationConfig;
	private final PartitionSecurityContextHolder partitionSecurityContextHolder;
	private final PartitionResolver partitionResolver;

	private MethodInterceptor repositoryInvokerFactoryAdvice() {
		return (invocation) -> {
			var res = invocation.proceed();
			if (res instanceof RepositoryInvoker invoker && invocation.getArguments().length == 1
					&& invocation.getArguments()[0] instanceof Class<?> domainType) {
				return new PartitionAuthorizingRepositoryInvoker(invoker, accessRegistry, domainType,
						partitionSecurityContextHolder, partitionResolver, partitionAuthorizationConfig.getCheckDelete());
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
