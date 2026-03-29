package com.bgiddens.impl.entities.projections;

import com.bgiddens.impl.entities.UserDepartment;
import com.bgiddens.impl.entities.UserRegion;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "userRegionShallow", types = { UserRegion.class })
public interface UserRegionShallow {}
