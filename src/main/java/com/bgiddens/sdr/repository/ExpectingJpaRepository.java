package com.bgiddens.sdr.repository;

import com.bgiddens.sdr.exceptions.DataIntegrityException;
import com.bgiddens.sdr.exceptions.EntityNotFoundException;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Collection;

@NoRepositoryBean
public interface ExpectingJpaRepository<E, I> extends JpaRepository<E, I> {

	@NonNull
	default E expectById(@NonNull I id) {
		return this.findById(id).orElseThrow(() -> EntityNotFoundException.fromRepositoryWithId(this.getClass(), id));
	}

	@NonNull
	default <T> T expectSingular(Collection<T> entities) {
		if (entities.size() != 1) {
			throw new DataIntegrityException(
					String.format("Expected a singular collection but found %s entities", entities.size()));
		}
		return entities.stream().findFirst().orElseThrow(() -> new IllegalStateException("Unexpected empty stream"));
	}
}
