package com.bgiddens.sdr.repository;

import com.querydsl.core.types.dsl.EntityPathBase;
import java.time.LocalDate;

import org.jspecify.annotations.NonNull;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

public interface CustomBinderCustomizer<Q extends EntityPathBase<?>> extends QuerydslBinderCustomizer<@NonNull Q> {

	@Override
	default void customize(QuerydslBindings bindings, Q root) {
		bindings.bind(String.class).all(BindingCustomizationService::processStringPath);
		bindings.bind(LocalDate.class).all(BindingCustomizationService::processComparablePath);
		bindings.bind(Integer.class).all(BindingCustomizationService::processNumberPath);
		bindings.bind(Float.class).all(BindingCustomizationService::processNumberPath);
		bindings.bind(Double.class).all(BindingCustomizationService::processNumberPath);
	}
}
