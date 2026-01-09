package com.bgiddens.impl.repos;

import com.bgiddens.impl.entities.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

@RepositoryRestResource
public interface EmployeeRepo extends JpaRepository<Employee, UUID>, QuerydslPredicateExecutor<Employee> {}
