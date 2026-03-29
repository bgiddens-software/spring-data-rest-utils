package com.bgiddens.impl.repos;

import com.bgiddens.impl.entities.UserDepartment;
import com.bgiddens.impl.entities.projections.UserDepartmentShallow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@RepositoryRestResource(excerptProjection = UserDepartmentShallow.class)
public interface UserDepartmentRepo extends JpaRepository<UserDepartment, UUID> {}
