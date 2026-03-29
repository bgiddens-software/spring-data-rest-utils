package com.bgiddens.impl.repos;

import com.bgiddens.impl.entities.UserRegion;
import com.bgiddens.impl.entities.projections.RegionShallow;
import com.bgiddens.impl.entities.projections.UserRegionShallow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@RepositoryRestResource(excerptProjection = UserRegionShallow.class)
public interface UserRegionRepo extends JpaRepository<UserRegion, UUID> {}
