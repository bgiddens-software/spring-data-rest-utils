package com.bgiddens.pbac.resolver;

import com.bgiddens.pbac.Partitionable;
import com.bgiddens.pbac.PartitionResolverConfig;
import com.bgiddens.reflection.FieldReflectiveAccessor;
import com.bgiddens.reflection.InferredMethodReflectiveAccessor;
import com.bgiddens.reflection.MethodReflectiveAccessor;
import com.bgiddens.reflection.ReflectionUtils;
import com.bgiddens.reflection.ReflectiveAccessor;
import com.mysema.commons.lang.Pair;
import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.SimpleExpression;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.data.querydsl.EntityPathResolver;
import org.springframework.data.querydsl.SimpleEntityPathResolver;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
public class CachingRecursivePartitionResolver implements PartitionResolver, PartitionPathResolver {

	private final PartitionableClassScanner scanner;
	private final PartitionResolverConfig partitionResolverConfig;
	private final EntityPathResolver entityPathResolver = new SimpleEntityPathResolver("");

	@AllArgsConstructor
	@Getter
	protected static final class PartitionableMetadata {
		@NonNull private Class<?> parentClass;
		@NonNull private ReflectiveAccessor<Object, Object> accessor;
		@NonNull private String name;
		@NonNull private Class<?> type;
		@NonNull private String queryPath;
		@NonNull private Boolean isCollection;
		@NonNull private String basis;
		@Nullable
		@Setter private PartitionableMetadata next;

		public String getQueryPath() {
			return this.queryPath.isBlank() ? this.name : this.queryPath;
		}
	}

	@AllArgsConstructor
	@Getter
	protected static final class PartitionAccessorAndExpressions {
		@NonNull private PartitionAccessFunction partitionAccessor;
		@Nullable private Collection<SimpleExpression<Object>> partitionExpressions;

		protected static PartitionAccessorAndExpressions of(PartitionAccessFunction partitionAccessFunction,
				Collection<SimpleExpression<Object>> expressions) {
			return new PartitionAccessorAndExpressions(partitionAccessFunction, expressions);
		}
	}

	private final Map<Class<?>, Map<String, PartitionAccessorAndExpressions>> cache = new HashMap<>();

	private PathBuilder<Object> buildPartitionExpression(PathBuilder<?> prior, PartitionableMetadata metadata) {
		final PathBuilder<Object> next = (metadata.getIsCollection())
				? prior.getCollection(metadata.getQueryPath(), Object.class).any()
				: prior.get(metadata.getQueryPath(), Object.class);
		return metadata.getNext() == null ? next : buildPartitionExpression(next, metadata.getNext());
	}

	private SimpleExpression<Object> buildPartitionExpression(PartitionableMetadata metadata) {
		try {
			final var qEntity = entityPathResolver.createPath(metadata.getParentClass());
			final var builder = new PathBuilder<>(metadata.getParentClass(), PathMetadataFactory.forDelegate(qEntity));
			return buildPartitionExpression(builder, metadata);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	private PartitionAccessFunction buildAccessor(PartitionableMetadata metadata) {
		final var accessor = getPartitionsInvoker(metadata.getAccessor());
		return metadata.getNext() == null ? accessor : compose(accessor, buildAccessor(metadata.getNext()));
	}

	private PartitionAccessFunction buildAccessor(List<PartitionableMetadata> metadataList) {
		return metadataList.stream().map(this::buildAccessor).reduce((ignored) -> new HashSet<Object>(),
				(accessor1, accessor2) -> (obj) -> {
					final var res = new HashSet<>();
					res.addAll(accessor1.apply(obj));
					res.addAll(accessor2.apply(obj));
					return res;
				});
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
				cache.get(clazz).put(basis,
						PartitionAccessorAndExpressions.of(buildAccessor(metadataList), metadataList.stream().map(this::buildPartitionExpression).toList()));
			});
		}
	}

	private PartitionAccessFunction compose(PartitionAccessFunction step, PartitionAccessFunction next) {
		return obj -> next.apply(step.apply(obj));
	}

	public Collection<Object> resolvePartitions(String basis, Object entity) throws PartitionConfigurationException {
		return cache.get(entity.getClass()).get(basis).getPartitionAccessor().apply(entity);
	}

	public Collection<SimpleExpression<Object>> resolvePartitionExpressions(String basis, Class<?> clazz)
			throws PartitionConfigurationException {
		return cache.get(clazz).get(basis).getPartitionExpressions();
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

	private PartitionAccessFunction getPartitionsInvoker(ReflectiveAccessor<Object, Object> accessor) {
		return obj -> {
			var args = (obj instanceof Collection<?> argCollection) ? argCollection.stream() : Stream.of(obj);
			return args.flatMap(arg -> {
				try {
					var res = accessor.get(arg);
					return (res instanceof Collection<?> collection) ? collection.stream() : Stream.of(res);
				} catch (IllegalAccessException | InvocationTargetException ex) {
					throw new PartitionConfigurationException(
							String.format("Failed to access partitions for object of type %s using accessor %s", obj.getClass(),
									accessor.getClass()),
							ex);
				}
			}).collect(Collectors.toSet());
		};
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
