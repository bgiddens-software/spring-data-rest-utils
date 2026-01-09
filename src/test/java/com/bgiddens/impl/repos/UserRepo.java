package com.bgiddens.impl.repos;

import com.bgiddens.impl.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepo extends JpaRepository<User, UUID> {
	Optional<User> findFirstByPrincipal(String principal);
}
