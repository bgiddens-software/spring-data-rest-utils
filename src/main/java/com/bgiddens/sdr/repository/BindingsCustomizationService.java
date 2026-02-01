package com.bgiddens.sdr.repository;

import com.querydsl.core.types.dsl.EntityPathBase;
import org.jspecify.annotations.NonNull;
import org.springframework.data.querydsl.binding.QuerydslBindings;

public interface BindingsCustomizationService {
	<Q extends EntityPathBase<?>> void customize(@NonNull QuerydslBindings bindings, Q root);
}
