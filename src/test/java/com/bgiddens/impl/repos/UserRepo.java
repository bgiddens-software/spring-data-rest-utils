package com.bgiddens.impl.repos;

import com.bgiddens.impl.entities.User;
import com.bgiddens.impl.entities.projections.UserShallow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@RepositoryRestResource(excerptProjection = UserShallow.class)
public interface UserRepo extends JpaRepository<User, UUID> {
	Optional<User> findFirstByPrincipal(String principal);
}
