package com.bgiddens.sdr.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class BindingCustomizationService {

	public BindingCustomizationService(ParamOperationCustomization paramOperationCustomization) {
		this.paramOperationMap = paramOperationCustomization;
	}

	private final ParamOperationCustomization paramOperationMap;

	public Optional<Predicate> processStringPath(StringPath path, Collection<? extends String> values) {
		final var ops = this.paramOperationMap.getOrDefault(path.getMetadata().getName().toLowerCase(), new ArrayList<>());
		if (values.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(expressionFor(path, ops, values, ParamOperationCustomization.OperationType.LIKE_IGNORE_CASE,
				this::expressionForString));
	}

	public <T extends Comparable> Optional<Predicate> processComparablePath(Path<T> rawPath,
			Collection<? extends T> values) {
		final var ops = this.paramOperationMap.getOrDefault(rawPath.getMetadata().getName().toLowerCase(),
				new ArrayList<>());
		if (values.isEmpty()) {
			return Optional.empty();
		}
		var path = (ComparableExpression) rawPath;
		return Optional.of(
				expressionFor(path, ops, values, ParamOperationCustomization.OperationType.EQ, this::expressionForComparable));
	}

	public <T extends Number & Comparable<?>> Optional<Predicate> processNumberPath(Path<T> rawPath,
			Collection<? extends T> values) {
		final var ops = this.paramOperationMap.getOrDefault(rawPath.getMetadata().getName().toLowerCase(),
				new ArrayList<>());
		if (values.isEmpty()) {
			return Optional.empty();
		}
		var path = (NumberExpression) rawPath;
		return Optional
				.of(expressionFor(path, ops, values, ParamOperationCustomization.OperationType.EQ, this::expressionForNumber));
	}

	protected <T, P extends Expression<T>> Predicate expressionFor(P path,
			List<ParamOperationCustomization.OperationType> ops, Collection<? extends T> values,
			ParamOperationCustomization.OperationType defaultOperation, ExpressionFunction<P, T> expressionFunction) {
		var res = new BooleanBuilder();
		var valuesAsList = (T[]) values.toArray();
		for (int i = 0; i < values.size(); i++) {
			final var op = (i < ops.size()) ? ops.get(i) : defaultOperation;
			expressionFunction.apply(op, path, valuesAsList[i]).ifPresent(res::and);
		}
		return res.getValue();
	}

	protected Optional<BooleanExpression> expressionForString(ParamOperationCustomization.OperationType op,
			StringExpression path, String value) {
		return switch (op) {
			case LIKE -> Optional.of(path.contains(value));
			case LIKE_IGNORE_CASE -> Optional.of(path.containsIgnoreCase(value));
			default -> expressionForComparable(op, path, value);
		};
	}

	protected <T extends Comparable<? super T>> Optional<BooleanExpression> expressionForComparable(
			ParamOperationCustomization.OperationType op, ComparableExpression<T> path, T value) {
		return switch (op) {
			case EQ -> Optional.of(path.eq(value));
			case GT -> Optional.of(path.gt(value));
			case LT -> Optional.of(path.lt(value));
			case GE -> Optional.of(path.goe(value));
			case LE -> Optional.of(path.loe(value));
			case EQ_OR_NULL -> Optional.of(path.isNull().or(path.eq(value)));
			case GT_OR_NULL -> Optional.of(path.isNull().or(path.gt(value)));
			case LT_OR_NULL -> Optional.of(path.isNull().or(path.lt(value)));
			case GE_OR_NULL -> Optional.of(path.isNull().or(path.goe(value)));
			case LE_OR_NULL -> Optional.of(path.isNull().or(path.loe(value)));
			default -> Optional.empty();
		};
	}

	protected <T extends Number & Comparable<?>> Optional<BooleanExpression> expressionForNumber(
			ParamOperationCustomization.OperationType op, NumberExpression<T> path, T value) {
		return switch (op) {
			case EQ -> Optional.of(path.eq(value));
			case GT -> Optional.of(path.gt(value));
			case LT -> Optional.of(path.lt(value));
			case GE -> Optional.of(path.goe(value));
			case LE -> Optional.of(path.loe(value));
			case EQ_OR_NULL -> Optional.of(path.isNull().or(path.eq(value)));
			case GT_OR_NULL -> Optional.of(path.isNull().or(path.gt(value)));
			case LT_OR_NULL -> Optional.of(path.isNull().or(path.lt(value)));
			case GE_OR_NULL -> Optional.of(path.isNull().or(path.goe(value)));
			case LE_OR_NULL -> Optional.of(path.isNull().or(path.loe(value)));
			default -> Optional.empty();
		};
	}

	@FunctionalInterface
	protected interface ExpressionFunction<P, T> {
		Optional<BooleanExpression> apply(ParamOperationCustomization.OperationType operationType, P path, T target);
	}
}
