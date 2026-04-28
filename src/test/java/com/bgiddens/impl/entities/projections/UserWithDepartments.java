package com.bgiddens.impl.entities.projections;

import com.bgiddens.impl.entities.User;
import org.springframework.data.rest.core.config.Projection;

import java.util.Set;

@Projection(name = "userWithDepartments", types = { User.class })
public interface UserWithDepartments extends UserShallow {
	Set<UserDepartmentWithDepartment> getAccessUserDepartments();
}
