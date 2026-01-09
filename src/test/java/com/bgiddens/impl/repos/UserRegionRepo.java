package com.bgiddens.impl.repos;

import com.bgiddens.impl.entities.UserRegion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRegionRepo extends JpaRepository<UserRegion, UUID> {}
