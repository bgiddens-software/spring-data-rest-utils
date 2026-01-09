package com.bgiddens.pbac.graph;

import com.bgiddens.reflection.ReflectiveAccessor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class PartitionableMetadata {

	public PartitionableMetadata(@NonNull Class<?> parentClass, @NonNull ReflectiveAccessor<Object, Object> accessor,
			@NonNull String name, @NonNull Class<?> type, @NonNull String queryPath, @NonNull Boolean isCollection,
			@NonNull String basis, @Nullable PartitionableMetadata next) {
		this.parentClass = parentClass;
		this.accessor = accessor;
		this.name = name;
		this.type = type;
		this.queryPath = queryPath;
		this.isCollection = isCollection;
		this.basis = basis;
		this.next = next;
	}

	@NonNull private final Class<?> parentClass;
	@NonNull private final ReflectiveAccessor<Object, Object> accessor;
	@NonNull private final String name;
	@NonNull private final Class<?> type;
	@NonNull private final String queryPath;
	@NonNull private final Boolean isCollection;
	@NonNull private final String basis;
	@Nullable private PartitionableMetadata next;

	public @NonNull Class<?> getParentClass() {
		return parentClass;
	}

	public @NonNull ReflectiveAccessor<Object, Object> getAccessor() {
		return accessor;
	}

	public @NonNull String getName() {
		return name;
	}

	public @NonNull Class<?> getType() {
		return type;
	}

	public @NonNull Boolean getIsCollection() {
		return isCollection;
	}

	public @NonNull String getBasis() {
		return basis;
	}

	public @Nullable PartitionableMetadata getNext() {
		return next;
	}

	public void setNext(@Nullable PartitionableMetadata next) {
		this.next = next;
	}

	public String getQueryPath() {
		return this.queryPath.isBlank() ? this.name : this.queryPath;
	}
}
