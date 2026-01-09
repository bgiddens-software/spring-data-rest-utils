package com.bgiddens.pbac.graph;

import com.bgiddens.pbac.PartitionResolverConfig;
import com.bgiddens.pbac.Partitionable;
import com.bgiddens.pbac.resolver.PartitionAccessFunction;
import com.bgiddens.pbac.exceptions.PartitionConfigurationException;
import com.bgiddens.pbac.resolver.PartitionableClassScanner;
import com.bgiddens.reflection.FieldReflectiveAccessor;
import com.bgiddens.reflection.InferredMethodReflectiveAccessor;
import com.bgiddens.reflection.MethodReflectiveAccessor;
import com.bgiddens.reflection.ReflectionUtils;
import com.bgiddens.reflection.ReflectiveAccessor;
import com.mysema.commons.lang.Pair;
import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.SimpleExpression;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.data.querydsl.EntityPathResolver;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
public class CachingPartitionableMetadataService implements PartitionableMetadataService {

	private final PartitionableClassScanner scanner;
	private final PartitionResolverConfig partitionResolverConfig;
	private final Map<Class<?>, Map<String, Set<PartitionableMetadata>>> cache = new HashMap<>();

	@Override
	public Set<PartitionableMetadata> getMetadataFor(Class<?> clazz, String basis) {
		return (cache.containsKey(clazz)) ? cache.get(clazz).getOrDefault(basis, null) : null;
	}

	public void cachePartitionableObjects() throws PartitionConfigurationException {
		Arrays.stream(scanner.getPartitionableClasses()).forEach(clazz -> traverse(clazz, 0, null));
	}

	private void traverse(Class<?> clazz, int depth, PartitionableMetadata parent) {
		if (depth > partitionResolverConfig.getMaxDepth()) {
			throw new PartitionConfigurationException(String.format("Exceeded maximum partition depth (%s)", depth));
		} else {
			cache.putIfAbsent(clazz, new HashMap<>());
			getPartitionableMetadata(clazz).forEach((basis, metadataList) -> {
				metadataList.forEach(metadata -> {
					if (Optional.ofNullable(parent).map(p -> Objects.equals(basis, p.getBasis())).orElse(false)) {
						parent.setNext(metadata);
					}
					traverse(metadata.getType(), depth + 1, metadata);
				});
				cache.get(clazz).put(basis, new HashSet<>(metadataList));
			});
		}
	}

	private MultiValueMap<String, PartitionableMetadata> getPartitionableMetadata(Class<?> clazz) {
		var res = new HashMap<String, List<PartitionableMetadata>>();
		res.putAll(ReflectionUtils.getMethodsWithAnnotation(clazz, Partitionable.class).stream().flatMap(method -> {
			var annotation = method.getAnnotation(Partitionable.class);
			var basis = annotation.basis();
			var accessor = getReflectiveAccessor(method);
			return Arrays.stream(basis)
					.map(b -> new Pair<>(b,
							new PartitionableMetadata(clazz, accessor, method.getName(), getDomainType(method.getGenericReturnType()),
									annotation.qPath(), Collection.class.isAssignableFrom(method.getReturnType()), b, null)));
		}).collect(Collectors.groupingBy(Pair::getFirst, Collectors.mapping(Pair::getSecond, Collectors.toList()))));
		res.putAll(ReflectionUtils.getFieldsWithAnnotation(clazz, Partitionable.class).stream().flatMap(field -> {
			var annotation = field.getAnnotation(Partitionable.class);
			var basis = annotation.basis();
			var accessor = getReflectiveAccessor(field);
			return Arrays.stream(basis)
					.map(b -> new Pair<>(b,
							new PartitionableMetadata(clazz, accessor, field.getName(), getDomainType(field.getGenericType()),
									annotation.qPath(), Collection.class.isAssignableFrom(field.getType()), b, null)));
		}).collect(Collectors.groupingBy(Pair::getFirst, Collectors.mapping(Pair::getSecond, Collectors.toList()))));
		return new LinkedMultiValueMap<>(res);
	}

	private ReflectiveAccessor<Object, Object> getReflectiveAccessor(Method method) {
		return new MethodReflectiveAccessor<>(method, method.getDeclaringClass(), method.getReturnType(),
				partitionResolverConfig.getForceAccess());
	}

	private ReflectiveAccessor<Object, Object> getReflectiveAccessor(Field field) {
		try {
			return partitionResolverConfig.getUseInferredMethodAccessor()
					? new InferredMethodReflectiveAccessor<>(field, field.getDeclaringClass(), field.getType(),
							partitionResolverConfig.getForceAccess())
					: new FieldReflectiveAccessor<>(field, field.getDeclaringClass(), field.getType(),
							partitionResolverConfig.getForceAccess());
		} catch (NoSuchMethodException e) {
			throw new PartitionConfigurationException(e);
		}
	}

	private Class<?> getDomainType(Type type) {
		if (type instanceof ParameterizedType parameterizedType
				&& parameterizedType.getRawType() instanceof Class<?> rawType && Collection.class.isAssignableFrom(rawType)
				&& parameterizedType.getActualTypeArguments().length == 1
				&& parameterizedType.getActualTypeArguments()[0] instanceof Class<?> clazz) {
			return clazz;
		} else if (type instanceof Class<?> clazz) {
			return clazz;
		} else {
			throw new PartitionConfigurationException(String.format("Unable to resolve domain type for type %s", type));
		}
	}
}
