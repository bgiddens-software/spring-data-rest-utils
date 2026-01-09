package com.bgiddens.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import lombok.AllArgsConstructor;

/**
 * An accessor that reflectively retrieves a value via a standard accessor method - e.g., getValue() for a field named
 * value.
 */
@AllArgsConstructor
public class InferredMethodReflectiveAccessor<P, T> implements ReflectiveAccessor<P, T> {

	private final Method method;

	public InferredMethodReflectiveAccessor(Field field, Class<? extends P> parentClass, Class<? extends T> returnClass,
			boolean forceAccess) throws NoSuchMethodException {
		var methodName = String.format("get%s%s", field.getName().substring(0, 1).toUpperCase(),
				field.getName().substring(1));
		var method = field.getDeclaringClass().getDeclaredMethod(methodName);
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
