package com.bgiddens.sdr.repository;

import com.bgiddens.sdr.repository.operations.EqualTo;
import com.bgiddens.sdr.repository.operations.EqualToOrNull;
import com.bgiddens.sdr.repository.operations.GreaterThan;
import com.bgiddens.sdr.repository.operations.GreaterThanOrEqualTo;
import com.bgiddens.sdr.repository.operations.GreaterThanOrEqualToOrNull;
import com.bgiddens.sdr.repository.operations.GreaterThanOrNull;
import com.bgiddens.sdr.repository.operations.LessThan;
import com.bgiddens.sdr.repository.operations.LessThanOrEqualTo;
import com.bgiddens.sdr.repository.operations.LessThanOrEqualToOrNull;
import com.bgiddens.sdr.repository.operations.LessThanOrNull;
import com.bgiddens.sdr.repository.operations.Like;
import com.bgiddens.sdr.repository.operations.LikeIgnoreCase;
import com.bgiddens.sdr.repository.operations.Operation;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpression;
import com.querydsl.core.types.dsl.ComparablePath;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;
import org.jspecify.annotations.NonNull;
import org.springframework.data.querydsl.binding.QuerydslBindings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import static com.querydsl.core.types.dsl.Expressions.TRUE;

public class QueryingBindingsCustomizationService implements BindingsCustomizationService {

	public QueryingBindingsCustomizationService(ParameterOperationService parameterOperationService) {
		this.parameterOperationService = parameterOperationService;
	}

	private final ParameterOperationService parameterOperationService;

	@Override
	public <Q extends EntityPathBase<?>> void customize(@NonNull QuerydslBindings bindings, Q root) {
		bindings.bind(String.class).all((StringPath path, Collection<? extends String> values) -> expressionFor(path,
				values, QueryingBindingsCustomizationService::expressionForString));
		bindings.bind(Comparable.class)
				.all((ComparablePath<Comparable> path, Collection<? extends Comparable> values) -> expressionFor(path, values,
						QueryingBindingsCustomizationService::expressionForComparable));
	}

	protected <T, P extends Path<T>> Optional<Predicate> expressionFor(P path, Collection<? extends T> values,
			ExpressionFunction<P, T> expressionFunction) {
		if (values.isEmpty()) {
			return Optional.empty();
		}
		var res = new BooleanBuilder(TRUE);
		var valuesAsList = new ArrayList<T>(values);
		for (int i = 0; i < valuesAsList.size(); i++) {
			expressionFunction.apply(parameterOperationService.get(path.getMetadata().getName().toLowerCase(), i), path,
					valuesAsList.get(i)).ifPresent(res::and);
		}
		assert res.getValue() != null;
		return Optional.of(res.getValue());
	}

	protected static Optional<BooleanExpression> expressionForString(Operation op, StringExpression path, String value) {
		return switch (op) {
			case Like ignored -> Optional.of(path.like(value));
			case LikeIgnoreCase ignored -> Optional.of(path.likeIgnoreCase(value));
			default -> expressionForComparable(op, path, value);
		};
	}

	protected static <T extends Comparable<? super T>> Optional<BooleanExpression> expressionForComparable(Operation op,
			ComparableExpression<T> path, T value) {
		return switch (op) {
			case EqualTo ignored -> Optional.of(path.eq(value));
			case GreaterThan ignored -> Optional.of(path.gt(value));
			case LessThan ignored -> Optional.of(path.lt(value));
			case GreaterThanOrEqualTo ignored -> Optional.of(path.goe(value));
			case LessThanOrEqualTo ignored -> Optional.of(path.loe(value));
			case EqualToOrNull ignored -> Optional.of(path.isNull().or(path.eq(value)));
			case GreaterThanOrNull ignored -> Optional.of(path.isNull().or(path.gt(value)));
			case LessThanOrNull ignored -> Optional.of(path.isNull().or(path.lt(value)));
			case GreaterThanOrEqualToOrNull ignored -> Optional.of(path.isNull().or(path.goe(value)));
			case LessThanOrEqualToOrNull ignored -> Optional.of(path.isNull().or(path.loe(value)));
			default -> Optional.empty();
		};
	}

	@FunctionalInterface
	protected interface ExpressionFunction<P, T> {
		Optional<BooleanExpression> apply(Operation operation, P path, T target);
	}
}
