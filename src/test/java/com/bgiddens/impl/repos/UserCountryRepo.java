package com.bgiddens.impl.repos;

import com.bgiddens.impl.entities.UserCountry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserCountryRepo extends JpaRepository<UserCountry, UUID> {}
