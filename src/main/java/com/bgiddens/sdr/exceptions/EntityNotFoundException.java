package com.bgiddens.sdr.exceptions;

public class EntityNotFoundException extends RuntimeException {

	private EntityNotFoundException(String message) {
		super(message);
	}

	public static EntityNotFoundException fromRepository(Class<?> repositoryType, String basis) {
		return new EntityNotFoundException(
				String.format("Repository of type %s failed to find entity with: %s", repositoryType, basis));
	}

	public static EntityNotFoundException fromRepositoryWithId(Class<?> repositoryType, Object id) {
		return fromRepository(repositoryType, String.format("{ id: %s }", id));
	}
}
