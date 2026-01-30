package com.bgiddens.sdr.repository;

import com.bgiddens.reflection.ApplicationContextHolder;
import com.querydsl.core.types.dsl.EntityPathBase;
import org.jspecify.annotations.NonNull;
import org.springframework.data.querydsl.binding.QuerydslBindings;

public interface CustomBinderCustomizer<Q extends EntityPathBase<?>> extends org.springframework.data.querydsl.binding.QuerydslBinderCustomizer<@NonNull Q> {

	@Override
	default void customize(@NonNull QuerydslBindings bindings, Q root) {
		QueryingBinderCustomizer.customize(bindings, ApplicationContextHolder.getBean(ParameterOperationService.class));
	}
}
