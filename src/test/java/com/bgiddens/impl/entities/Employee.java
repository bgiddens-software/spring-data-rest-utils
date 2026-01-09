package com.bgiddens.impl.entities;

import com.bgiddens.pbac.Partitionable;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID) private UUID id;

	private String name;

	@ManyToOne(fetch = FetchType.LAZY)
	@Partitionable(basis = "department") private Department department;

	@ManyToMany(fetch = FetchType.LAZY)
	@Partitionable(basis = { "country", "region" }) private Set<Country> countries;
}
