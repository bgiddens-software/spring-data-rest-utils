package com.bgiddens.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MethodReflectiveAccessor<P, T> implements ReflectiveAccessor<P, T> {

	public MethodReflectiveAccessor(Method method) {
		this.method = method;
	}

	private final Method method;

	public MethodReflectiveAccessor(Method method, Class<? extends P> parentClass, Class<? extends T> returnClass,
			boolean forceAccess) {
		if (!method.getDeclaringClass().isAssignableFrom(parentClass)) {
			throw new IllegalArgumentException(String.format(
					"Attempted to create method argument resolver with incompatible method and parent type (%s, %s)",
					method.getName(), parentClass.getName()));
		}
		if (!returnClass.isAssignableFrom(method.getReturnType())) {
			throw new IllegalArgumentException(String.format(
					"Attempted to create method argument resolver with incompatible method and return type (%s, %s)",
					method.getName(), returnClass.getName()));
		}
		this.method = method;
		if (forceAccess) {
			this.method.setAccessible(true);
		}
	}

	public T get(P obj) throws IllegalAccessException, InvocationTargetException {
		return (T) method.invoke(obj);
	}
}
