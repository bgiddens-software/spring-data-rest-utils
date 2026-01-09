package com.bgiddens.impl.entities;

import com.bgiddens.pbac.Partitionable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Set;
import java.util.UUID;

@Entity(name = "user_")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID) private UUID id;

	@NonNull private String principal;

	@OneToMany(mappedBy = "user")
	@Partitionable(basis = "department") private Set<UserDepartment> accessUserDepartments;

	@OneToMany(mappedBy = "user")
	@Partitionable(basis = "region") private Set<UserRegion> accessUserRegions;

	@OneToMany(mappedBy = "user")
	@Partitionable(basis = "country") private Set<UserCountry> accessUserCountries;
}
