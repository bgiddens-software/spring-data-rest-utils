package com.bgiddens.reflection;

import java.lang.reflect.InvocationTargetException;

public interface ReflectiveAccessor<P, T> {
	T get(P obj) throws IllegalAccessException, InvocationTargetException;
}
