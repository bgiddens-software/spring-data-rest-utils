package com.bgiddens.impl.entities.projections;

import com.bgiddens.impl.entities.Department;
import com.bgiddens.impl.entities.Region;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "regionShallow", types = { Region.class })
public interface RegionShallow {
	String getName();
}
