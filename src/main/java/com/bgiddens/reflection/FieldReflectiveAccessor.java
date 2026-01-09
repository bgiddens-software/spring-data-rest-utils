package com.bgiddens.reflection;

import java.lang.reflect.Field;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FieldReflectiveAccessor<P, T> implements ReflectiveAccessor<P, T> {

	private final Field field;

	public FieldReflectiveAccessor(Field field, Class<? extends P> parentClass, Class<? extends T> returnClass,
			boolean forceAccess) {
		if (!field.getDeclaringClass().isAssignableFrom(parentClass)) {
			throw new IllegalArgumentException(String.format(
					"Attempted to create method argument resolver with incompatible method and parent type (%s, %s)",
					field.getName(), parentClass.getName()));
		}
		if (!returnClass.isAssignableFrom(field.getType())) {
			throw new IllegalArgumentException(
					String.format("Attempted to create field argument resolver with incompatible field and return type (%s, %s)",
							field.getName(), returnClass.getName()));
		}
		this.field = field;
		if (forceAccess) {
			this.field.setAccessible(true);
		}
	}

	public T get(P obj) throws IllegalAccessException {
		return (T) field.get(obj);
	}
}
