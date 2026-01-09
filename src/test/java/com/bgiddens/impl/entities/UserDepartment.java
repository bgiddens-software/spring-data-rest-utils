package com.bgiddens.impl.entities;

import com.bgiddens.pbac.Partitionable;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserDepartment {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID) private UUID id;

	@ManyToOne(fetch = FetchType.LAZY) private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@Partitionable(basis = "department") private Department department;
}
