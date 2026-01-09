package com.bgiddens.impl.repos;

import com.bgiddens.impl.entities.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

@RepositoryRestResource
public interface RegionRepo extends JpaRepository<Region, UUID> {}
