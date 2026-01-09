package com.bgiddens.reflection;

import java.lang.reflect.Method;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.aop.MethodMatcher;

@AllArgsConstructor
public class ClassAndNameMethodMatcher implements MethodMatcher {

	private String methodName;
	private Class<?> clazz;

	@Override
	public boolean matches(@NonNull Method method, @NonNull Class<?> targetClass) {
		return clazz.isAssignableFrom(targetClass) && Objects.equals(method.getName(), this.methodName);
	}

	@Override
	public boolean isRuntime() {
		return false;
	}

	@Override
	public boolean matches(@NonNull Method method, @NonNull Class<?> targetClass, Object... args) {
		throw new RuntimeException("Method not implemented and not expected to be called");
	}
}
