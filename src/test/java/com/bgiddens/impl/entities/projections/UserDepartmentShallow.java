package com.bgiddens.impl.entities.projections;

import com.bgiddens.impl.entities.Department;
import com.bgiddens.impl.entities.UserDepartment;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "userDepartmentShallow", types = { UserDepartment.class })
public interface UserDepartmentShallow {}
