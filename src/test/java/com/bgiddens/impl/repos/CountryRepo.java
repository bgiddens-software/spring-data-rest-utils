package com.bgiddens.impl.repos;

import com.bgiddens.impl.entities.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

@RepositoryRestResource
public interface CountryRepo extends JpaRepository<Country, UUID> {}
