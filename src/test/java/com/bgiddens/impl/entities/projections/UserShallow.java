package com.bgiddens.impl.entities.projections;

import com.bgiddens.impl.entities.User;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "userShallow", types = { User.class })
public interface UserShallow {
	String getPrincipal();
}
