package com.bgiddens.sdr.repository;

import com.bgiddens.sdr.exceptions.DataIntegrityException;
import com.bgiddens.sdr.exceptions.EntityNotFoundException;
import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Collection;

@NoRepositoryBean
public interface ExpectingJpaRepository<E, I> extends JpaRepository<E, I> {

	@Nonnull
	default E expectById(@Nonnull I id) {
		return this.findById(id).orElseThrow(() -> EntityNotFoundException.fromRepositoryWithId(this.getClass(), id));
	}

	@Nonnull
	default <T> T expectSingular(Collection<T> entities) {
		if (entities.size() != 1) {
			throw new DataIntegrityException(
					String.format("Expected a singular collection but found %s entities", entities.size()));
		}
		return entities.stream().findFirst().orElseThrow(() -> new IllegalStateException("Unexpected empty stream"));
	}
}
