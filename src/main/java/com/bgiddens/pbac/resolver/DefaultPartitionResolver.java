package com.bgiddens.pbac.resolver;

import com.bgiddens.pbac.exceptions.PartitionConfigurationException;
import com.bgiddens.pbac.graph.PartitionableMetadata;
import com.bgiddens.pbac.graph.PartitionableMetadataService;
import com.bgiddens.reflection.ReflectiveAccessor;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DefaultPartitionResolver implements PartitionResolver {

	public DefaultPartitionResolver(PartitionableMetadataService partitionableMetadataService) {
		this.partitionableMetadataService = partitionableMetadataService;
	}

	private final PartitionableMetadataService partitionableMetadataService;

	private PartitionAccessFunction buildAccessor(PartitionableMetadata metadata) {
		final var accessor = getPartitionsInvoker(metadata.getAccessor());
		return metadata.getNext() == null ? accessor : compose(accessor, buildAccessor(metadata.getNext()));
	}

	private PartitionAccessFunction buildAccessor(Collection<PartitionableMetadata> metadataList) {
		return metadataList.stream().map(this::buildAccessor).reduce((ignored) -> new HashSet<>(),
				(accessor1, accessor2) -> (obj) -> {
					final var res = new HashSet<>();
					res.addAll(accessor1.apply(obj));
					res.addAll(accessor2.apply(obj));
					return res;
				});
	}

	private PartitionAccessFunction compose(PartitionAccessFunction step, PartitionAccessFunction next) {
		return obj -> next.apply(step.apply(obj));
	}

	public Collection<Object> resolvePartitions(String basis, Object entity) throws PartitionConfigurationException {
		return buildAccessor(Optional.ofNullable(partitionableMetadataService.getMetadataFor(entity.getClass(), basis))
				.orElseThrow(() -> new PartitionConfigurationException(
						String.format("Attempted to get partitionable metadata for basis %s and class %s, but none was found.",
								basis, entity.getClass()))))
				.apply(entity);
	}

	private PartitionAccessFunction getPartitionsInvoker(ReflectiveAccessor<Object, Object> accessor) {
		return obj -> {
			var args = (obj instanceof Collection<?> argCollection) ? argCollection.stream() : Stream.of(obj);
			return args.flatMap(arg -> {
				try {
					var res = accessor.get(arg);
					return (res == null) ? Stream.of()
							: (res instanceof Collection<?> collection) ? collection.stream() : Stream.of(res);
				} catch (IllegalAccessException | InvocationTargetException ex) {
					throw new PartitionConfigurationException(
							String.format("Failed to access partitions for object of type %s using accessor %s", obj.getClass(),
									accessor.getClass()),
							ex);
				}
			}).collect(Collectors.toSet());
		};
	}
}
