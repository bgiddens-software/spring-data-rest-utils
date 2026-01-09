package com.bgiddens.invoker;

import com.bgiddens.impl.entities.Employee;
import com.bgiddens.impl.repos.EmployeeRepo;
import com.bgiddens.pbac.PartitionSecurityContextHolder;
import com.bgiddens.pbac.access.AccessRegistry;
import com.bgiddens.pbac.access.PartitionedAccessLevel;
import com.bgiddens.pbac.resolver.PartitionResolver;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.support.RepositoryInvokerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@SpringBootTest
public class RepositoryInvokerAuthorizationTest {

	@Autowired private RepositoryInvokerFactory repositoryInvokerFactory;

	@MockitoBean private PartitionSecurityContextHolder partitionSecurityContextHolder;

	@MockitoBean private AccessRegistry accessRegistry;

	@MockitoBean private PartitionResolver partitionResolver;

	@MockitoSpyBean private EmployeeRepo employeeRepo;

	static Stream<Arguments> paramsForTestInvokeSingle() {
		return Stream.of(
				Arguments.of(Map.of("basis1", List.of()), Map.of("basis1", List.of("partition1")),
						PartitionedAccessLevel.of("basis1"), false),
				Arguments.of(Map.of("basis1", List.of("partition1")), Map.of("basis1", List.of("partition1")),
						PartitionedAccessLevel.of("basis1"), true),
				Arguments.of(Map.of("basis1", List.of("partition1")),
						Map.of("basis1", List.of("partition1"), "basis2", List.of("partition2")),
						PartitionedAccessLevel.of("basis1", "basis2"), false),
				Arguments.of(Map.of("basis1", List.of("partition1", "partition2")),
						Map.of("basis1", List.of("partition1"), "basis2", List.of("partition2")),
						PartitionedAccessLevel.of("basis1", "basis2"), false),
				Arguments.of(Map.of("basis1", List.of("partition1")),
						Map.of("basis1", List.of("partition1"), "basis2", List.of("partition2")),
						PartitionedAccessLevel.of("basis1"), true),
				Arguments.of(Map.of("basis1", List.of("partition1", "partition3"), "basis2", List.of("partition2")),
						Map.of("basis1", List.of("partition1"), "basis2", List.of("partition2")),
						PartitionedAccessLevel.of("basis1", "basis2"), true));
	}

	@ParameterizedTest
	@MethodSource("paramsForTestInvokeSingle")
	void testInvokeSave(Map<String, Collection<?>> userPartitions, Map<String, Collection<?>> entityPartitions,
			PartitionedAccessLevel access, Boolean authorizationExpected) {
		var invoker = repositoryInvokerFactory.getInvokerFor(Employee.class);
		var entity = mock(Employee.class);
		userPartitions.forEach(
				(key, value) -> doReturn(value).when(partitionSecurityContextHolder).getPartitionsForAuthorizedUser(key));
		entityPartitions.forEach((key, value) -> doReturn(value).when(partitionResolver).resolvePartitions(key, entity));
		doReturn(access).when(accessRegistry).getAccessLevel(any(), eq(Employee.class), any());
		try {
			invoker.invokeSave(entity);
			assertThat(authorizationExpected, equalTo(true));
		} catch (AccessDeniedException ex) {
			assertThat(authorizationExpected, equalTo(false));
		}
	}

	@ParameterizedTest
	@MethodSource("paramsForTestInvokeSingle")
	void testInvokeFindById(Map<String, Collection<?>> userPartitions, Map<String, Collection<?>> entityPartitions,
			PartitionedAccessLevel access, Boolean authorizationExpected) {
		var invoker = repositoryInvokerFactory.getInvokerFor(Employee.class);
		var entity = mock(Employee.class);
		userPartitions.forEach(
				(key, value) -> doReturn(value).when(partitionSecurityContextHolder).getPartitionsForAuthorizedUser(key));
		entityPartitions.forEach((key, value) -> doReturn(value).when(partitionResolver).resolvePartitions(key, entity));
		doReturn(access).when(accessRegistry).getAccessLevel(any(), any(), any());
		var id = UUID.randomUUID();
		doReturn(Optional.of(entity)).when(employeeRepo).findById(id);
		try {
			invoker.invokeFindById(id);
			assertThat(authorizationExpected, equalTo(true));
		} catch (AccessDeniedException ex) {
			assertThat(authorizationExpected, equalTo(false));
		}
	}

	@ParameterizedTest
	@MethodSource("paramsForTestInvokeSingle")
	void testInvokeDeleteById(Map<String, Collection<?>> userPartitions, Map<String, Collection<?>> entityPartitions,
			PartitionedAccessLevel access, Boolean authorizationExpected) {
		var invoker = repositoryInvokerFactory.getInvokerFor(Employee.class);
		var entity = mock(Employee.class);
		userPartitions.forEach(
				(key, value) -> doReturn(value).when(partitionSecurityContextHolder).getPartitionsForAuthorizedUser(key));
		entityPartitions.forEach((key, value) -> doReturn(value).when(partitionResolver).resolvePartitions(key, entity));
		doReturn(access).when(accessRegistry).getAccessLevel(any(), any(), any());
		var id = UUID.randomUUID();
		doReturn(Optional.of(entity)).when(employeeRepo).findById(id);
		try {
			invoker.invokeDeleteById(id);
			assertThat(authorizationExpected, equalTo(true));
		} catch (AccessDeniedException ex) {
			assertThat(authorizationExpected, equalTo(false));
		}
	}

	static Stream<Arguments> paramsForTestInvokeMultiple() {
		return Stream.of(
				Arguments.of(Map.of("basis1", List.of()), Map.of("basis1", List.of("partition1")),
						Map.of("basis1", List.of("partition1")), PartitionedAccessLevel.of("basis1"), false),
				Arguments.of(Map.of("basis1", List.of("partition1")), Map.of("basis1", List.of("partition1")),
						Map.of("basis1", List.of("partition1")), PartitionedAccessLevel.of("basis1"), false),
				Arguments.of(Map.of("basis1", List.of("partition1")), Map.of("basis1", List.of("partition1")),
						Map.of("basis1", List.of("partition2")), PartitionedAccessLevel.of("basis1"), false),
				Arguments.of(Map.of("basis1", List.of("partition1")), Map.of("basis1", List.of("partition1")),
						Map.of("basis1", List.of("partition1", "partition2")), PartitionedAccessLevel.of("basis1"), false),
				Arguments.of(Map.of("basis1", List.of("partition1")), Map.of("basis1", List.of("partition1")),
						Map.of("basis2", List.of("partition1", "partition2")), PartitionedAccessLevel.of("basis1"), false),
				Arguments.of(Map.of("basis1", List.of("partition1")), Map.of("basis1", List.of("partition1")),
						Map.of("basis1", List.of("partition1"), "basis2", List.of("partition2")),
						PartitionedAccessLevel.of("basis1"), false),
				Arguments.of(Map.of("basis1", List.of("partition1", "partition2")), Map.of("basis1", List.of("partition1")),
						Map.of("basis1", List.of("partition1")), PartitionedAccessLevel.of("basis1"), false),
				Arguments.of(Map.of("basis1", List.of("partition1"), "basis2", List.of("partition2")),
						Map.of("basis1", List.of("partition1"), "basis2", List.of("partition2")),
						Map.of("basis1", List.of("partition1"), "basis2", List.of("partition2")),
						PartitionedAccessLevel.of("basis1"), false));
	}

	@ParameterizedTest
	@MethodSource("paramsForTestInvokeMultiple")
	void testInvokeFindAllByPageable(Map<String, Collection<?>> userPartitions,
			Map<String, Collection<?>> entity1Partitions, Map<String, Collection<?>> entity2Partitions,
			PartitionedAccessLevel access, Boolean authorizationExpected) {
		var invoker = repositoryInvokerFactory.getInvokerFor(Employee.class);
		var entity1 = mock(Employee.class);
		var entity2 = mock(Employee.class);
		var pageable = PageRequest.of(0, 10);
		userPartitions.forEach(
				(key, value) -> doReturn(value).when(partitionSecurityContextHolder).getPartitionsForAuthorizedUser(key));
		entity1Partitions.forEach((key, value) -> doReturn(value).when(partitionResolver).resolvePartitions(key, entity1));
		entity2Partitions.forEach((key, value) -> doReturn(value).when(partitionResolver).resolvePartitions(key, entity2));
		doReturn(access).when(accessRegistry).getAccessLevel(any(), any(), any());
		doReturn(new PageImpl<>(List.of(entity1, entity2), pageable, 2)).when(employeeRepo).findAll(eq(pageable));
		try {
			invoker.invokeFindAll(pageable);
			assertThat(authorizationExpected, equalTo(true));
		} catch (AccessDeniedException ex) {
			assertThat(authorizationExpected, equalTo(false));
		}
	}

	@ParameterizedTest
	@MethodSource("paramsForTestInvokeMultiple")
	void testInvokeFindAllBySort(Map<String, Collection<?>> userPartitions, Map<String, Collection<?>> entity1Partitions,
			Map<String, Collection<?>> entity2Partitions, PartitionedAccessLevel access, Boolean authorizationExpected) {
		var invoker = repositoryInvokerFactory.getInvokerFor(Employee.class);
		var entity1 = mock(Employee.class);
		var entity2 = mock(Employee.class);
		var sort = Sort.unsorted();
		userPartitions.forEach(
				(key, value) -> doReturn(value).when(partitionSecurityContextHolder).getPartitionsForAuthorizedUser(key));
		entity1Partitions.forEach((key, value) -> doReturn(value).when(partitionResolver).resolvePartitions(key, entity1));
		entity2Partitions.forEach((key, value) -> doReturn(value).when(partitionResolver).resolvePartitions(key, entity2));
		doReturn(access).when(accessRegistry).getAccessLevel(any(), any(), any());
		doReturn(List.of(entity1, entity2)).when(employeeRepo).findAll(eq(sort));
		try {
			invoker.invokeFindAll(sort);
			assertThat(authorizationExpected, equalTo(true));
		} catch (AccessDeniedException ex) {
			assertThat(authorizationExpected, equalTo(false));
		}
	}
}
