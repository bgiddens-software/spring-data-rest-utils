package com.heb.driverpay.repos;

import com.heb.driverpay.exceptions.NotFoundException;
import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/** Helper interface for JpaRepository with convenience features. */
@NoRepositoryBean
public interface CustomJpaRepository<E, I> extends JpaRepository<E, I> {

  @Nonnull
  default E expectById(@Nonnull I id) {
    return this.findById(id).orElseThrow(() -> new NotFoundException(this.getClass(), id));
  }
}
