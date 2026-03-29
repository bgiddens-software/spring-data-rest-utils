package com.bgiddens.impl.repos;

import com.bgiddens.impl.entities.Region;
import com.bgiddens.impl.entities.projections.RegionShallow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

@RepositoryRestResource(excerptProjection = RegionShallow.class)
public interface RegionRepo extends JpaRepository<Region, UUID> {}
