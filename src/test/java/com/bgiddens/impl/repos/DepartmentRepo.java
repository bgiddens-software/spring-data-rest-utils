package com.bgiddens.impl.repos;

import com.bgiddens.impl.entities.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

@RepositoryRestResource
public interface DepartmentRepo extends JpaRepository<Department, UUID> {}
