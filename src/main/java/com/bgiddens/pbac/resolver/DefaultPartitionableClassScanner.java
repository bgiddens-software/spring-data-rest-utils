package com.bgiddens.pbac.resolver;

import com.bgiddens.pbac.Partitionable;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;

import java.lang.annotation.Annotation;
import java.util.Arrays;

public class DefaultPartitionableClassScanner implements PartitionableClassScanner {

	public DefaultPartitionableClassScanner(String packageName) {
		this.packageName = packageName;
	}

	private final String packageName;

	private Class<?> getClassForBeanDefinition(BeanDefinition beanDefinition) {
		try {
			return getClass().getClassLoader().loadClass(beanDefinition.getBeanClassName());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(
					String.format("Class %s not found by system class loader", beanDefinition.getBeanClassName()), e);
		}
	}

	private boolean doesClassHaveAnnotatedFieldOrMethod(Class<?> clazz, Class<? extends Annotation> annotationClass) {
		return Arrays.stream(clazz.getDeclaredMethods()).anyMatch(method -> method.getAnnotation(annotationClass) != null)
				|| Arrays.stream(clazz.getDeclaredFields()).anyMatch(field -> field.getAnnotation(annotationClass) != null);
	}

	public Class<?>[] getPartitionableClasses() {
		var provider = new ClassPathScanningCandidateComponentProvider(false);
		provider.addIncludeFilter((a, b) -> true);
		return provider.findCandidateComponents(packageName).stream().map(this::getClassForBeanDefinition)
				.filter(clazz -> doesClassHaveAnnotatedFieldOrMethod(clazz, Partitionable.class)).toArray(Class<?>[]::new);
	}
}
