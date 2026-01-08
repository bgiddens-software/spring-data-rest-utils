package com.bgiddens.sdr.repository;

import com.querydsl.core.types.dsl.EntityPathBase;
import java.time.LocalDate;
import java.util.Objects;

import org.jspecify.annotations.NonNull;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.web.context.ContextLoader;

public interface CustomBinderCustomizer<Q extends EntityPathBase<?>> extends QuerydslBinderCustomizer<@NonNull Q> {

	@Override
	default void customize(QuerydslBindings bindings, Q root) {
		final var bindingCustomizationService = Objects.requireNonNull(ContextLoader.getCurrentWebApplicationContext())
				.getBean(BindingCustomizationService.class);
		bindings.bind(String.class).all(bindingCustomizationService::processStringPath);
		bindings.bind(LocalDate.class).all(bindingCustomizationService::processComparablePath);
		bindings.bind(Integer.class).all(bindingCustomizationService::processNumberPath);
		bindings.bind(Float.class).all(bindingCustomizationService::processNumberPath);
		bindings.bind(Double.class).all(bindingCustomizationService::processNumberPath);
	}
}
