package com.bgiddens.pbac.resolver;

import com.bgiddens.pbac.exceptions.PartitionConfigurationException;
import com.bgiddens.pbac.graph.PartitionableMetadata;
import com.bgiddens.pbac.graph.PartitionableMetadataService;
import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.SimpleExpression;
import org.jspecify.annotations.NonNull;
import org.springframework.data.querydsl.EntityPathResolver;

import java.util.Collection;
import java.util.Optional;

public class DefaultPartitionPathResolver implements PartitionPathResolver {

	public DefaultPartitionPathResolver(PartitionableMetadataService partitionableMetadataService,
			EntityPathResolver entityPathResolver) {
		this.partitionableMetadataService = partitionableMetadataService;
		this.entityPathResolver = entityPathResolver;
	}

	private final PartitionableMetadataService partitionableMetadataService;
	private final EntityPathResolver entityPathResolver;

	private PathBuilder<?> buildPartitionExpression(PathBuilder<?> prior, PartitionableMetadata metadata) {
		final PathBuilder<?> next = (metadata.getIsCollection())
				? prior.getCollection(metadata.getQueryPath(), metadata.getClass()).any()
				: prior.get(metadata.getQueryPath(), metadata.getClass());
		return metadata.getNext() == null ? next : buildPartitionExpression(next, metadata.getNext());
	}

	private SimpleExpression<Object> buildPartitionExpression(PartitionableMetadata metadata) {
		try {
			final var qEntity = entityPathResolver.createPath(metadata.getParentClass());
			final var builder = new PathBuilder<>(metadata.getParentClass(), PathMetadataFactory.forDelegate(qEntity));
			return (SimpleExpression<Object>) buildPartitionExpression(builder, metadata);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	public Collection<SimpleExpression<Object>> resolvePartitionExpressions(@NonNull String basis,
			@NonNull Class<?> clazz) throws PartitionConfigurationException {
		return Optional.ofNullable(partitionableMetadataService.getMetadataFor(clazz, basis))
				.orElseThrow(() -> new PartitionConfigurationException(String.format(
						"Attempted to get partitionable metadata for basis %s and class %s, but none was found.", basis, clazz)))
				.stream().map(this::buildPartitionExpression).toList();
	}
}
