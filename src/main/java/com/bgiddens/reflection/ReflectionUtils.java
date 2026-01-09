package com.bgiddens.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class ReflectionUtils {

	public static <A extends Annotation> List<Method> getMethodsWithAnnotation(Class<?> clazz, Class<A> annotation) {
		return getMethodsWithAnnotation(clazz, annotation, a -> true);
	}

	public static <A extends Annotation> List<Method> getMethodsWithAnnotation(Class<?> clazz, Class<A> annotation,
			Function<A, Boolean> filter) {
		return Arrays.stream(clazz.getDeclaredMethods())
				.filter(m -> m.isAnnotationPresent(annotation) && filter.apply(m.getAnnotation(annotation))).toList();
	}

	public static <A extends Annotation> List<Field> getFieldsWithAnnotation(Class<?> clazz, Class<A> annotation) {
		return getFieldsWithAnnotation(clazz, annotation, a -> true);
	}

	public static <A extends Annotation> List<Field> getFieldsWithAnnotation(Class<?> clazz, Class<A> annotation,
			Function<A, Boolean> filter) {
		return Arrays.stream(clazz.getDeclaredFields())
				.filter(f -> f.isAnnotationPresent(annotation) && filter.apply(f.getAnnotation(annotation))).toList();
	}
}
