package com.bgiddens.sdr.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpression;
import com.querydsl.core.types.dsl.ComparablePath;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;
import org.springframework.data.querydsl.binding.QuerydslBindings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import static com.querydsl.core.types.dsl.Expressions.TRUE;

public class QueryingBinderCustomizer {

	public static <Q extends EntityPathBase<?>> void  customize(QuerydslBindings bindings, ParameterOperationService parameterOperationService) {
		bindings.bind(String.class).all((StringPath path, Collection<? extends String> values) -> expressionFor(path, values, QueryingBinderCustomizer::expressionForString, parameterOperationService));
		bindings.bind(Comparable.class).all((ComparablePath<Comparable> path, Collection<? extends Comparable> values) -> expressionFor(path, values, QueryingBinderCustomizer::expressionForComparable, parameterOperationService));
	}

	protected static <T, P extends Path<T>> Optional<Predicate> expressionFor(P path,
			Collection<? extends T> values, ExpressionFunction<P, T> expressionFunction, ParameterOperationService parameterOperationService) {
		final var ops = parameterOperationService.get(path.getMetadata().getName().toLowerCase());
		if (values.isEmpty()) {
			return Optional.empty();
		}
		var res = new BooleanBuilder(TRUE);
		var valuesAsList = new ArrayList<T>(values);
		for (int i = 0; i < valuesAsList.size(); i++) {
			final var op = (i < ops.size()) ? ops.get(i) : OperationType.EQ;
			expressionFunction.apply(op, path, valuesAsList.get(i)).ifPresent(res::and);
		}
        assert res.getValue() != null;
        return Optional.of(res.getValue());
	}

	protected static Optional<BooleanExpression> expressionForString(OperationType op, StringExpression path,
			String value) {
		return switch (op) {
			case LIKE -> Optional.of(path.like(value));
			case LIKE_IGNORE_CASE -> Optional.of(path.likeIgnoreCase(value));
			default -> expressionForComparable(op, path, value);
		};
	}

	protected static <T extends Comparable<? super T>> Optional<BooleanExpression> expressionForComparable(
			OperationType op, ComparableExpression<T> path, T value) {
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
		Optional<BooleanExpression> apply(OperationType operationType, P path, T target);
	}
}
