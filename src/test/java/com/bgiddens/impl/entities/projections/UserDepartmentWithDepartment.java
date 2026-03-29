package com.bgiddens.impl.entities.projections;

import com.bgiddens.impl.entities.UserDepartment;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "userDepartmentWithDepartment", types = { UserDepartment.class })
public interface UserDepartmentWithDepartment extends UserDepartmentShallow {
	DepartmentShallow getDepartment();
}
