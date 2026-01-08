package com.heb.driverpay.repos;

import com.heb.driverpay.utils.ApplicationContextProvider;
import com.querydsl.core.types.dsl.EntityPathBase;
import jakarta.annotation.Nonnull;
import java.time.LocalDate;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

public interface CustomBinderCustomizer<Q extends EntityPathBase<?>>
    extends QuerydslBinderCustomizer<Q> {
  @Override
  default void customize(QuerydslBindings bindings, @Nonnull Q root) {
    var bindingService = ApplicationContextProvider.getBean(BindingCustomizationService.class);
    bindings.bind(String.class).all(bindingService::processStringPath);
    bindings.bind(LocalDate.class).all(bindingService::processComparablePath);
    bindings.bind(Integer.class).all(bindingService::processNumberPath);
    bindings.bind(Float.class).all(bindingService::processNumberPath);
    bindings.bind(Double.class).all(bindingService::processNumberPath);
  }
}
