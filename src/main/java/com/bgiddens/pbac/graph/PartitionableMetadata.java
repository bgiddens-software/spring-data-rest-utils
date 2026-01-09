package com.bgiddens.pbac.graph;

import com.bgiddens.reflection.ReflectiveAccessor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@AllArgsConstructor
@Getter
public class PartitionableMetadata {
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
