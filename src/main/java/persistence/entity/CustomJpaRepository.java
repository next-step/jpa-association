package persistence.entity;

import persistence.sql.metadata.Values;

import java.util.Objects;

public class CustomJpaRepository <T> {
	private final EntityManager entityManager;

	public CustomJpaRepository(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public T save(T t) {
		if(Objects.isNull(Values.from(t).getValue("id"))) {
			entityManager.persist(t);
			return t;
		}

		entityManager.merge(t);
		return t;
	}
}
