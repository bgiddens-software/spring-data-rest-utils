package com.bgiddens.reflection;

import java.lang.reflect.Method;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.aop.MethodMatcher;

@AllArgsConstructor
public class EqualityMethodMatcher implements MethodMatcher {

	private Method method;
	private Class<?> clazz;

	public EqualityMethodMatcher(@NonNull Method method) {
		this.method = method;
		this.clazz = method.getDeclaringClass();
	}

	@Override
	public boolean matches(@NonNull Method method, @NonNull Class<?> targetClass) {
		return clazz.isAssignableFrom(targetClass) && Objects.equals(method, this.method);
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
