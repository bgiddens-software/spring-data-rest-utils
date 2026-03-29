package com.bgiddens.impl.entities.projections;

import com.bgiddens.impl.entities.Department;
import com.bgiddens.impl.entities.User;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "departmentShallow", types = { Department.class })
public interface DepartmentShallow {
	String getName();
}
