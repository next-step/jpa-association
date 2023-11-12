package persistence.entity.repository;

import persistence.entity.attribute.EntityAttribute;
import persistence.entity.attribute.EntityAttributes;
import persistence.entity.manager.EntityManager;

import java.lang.reflect.Field;

public class CustomJpaRepository<T, ID> {
    private final EntityManager entityManager;
    private final EntityAttributes entityAttributes;

    public CustomJpaRepository(EntityManager entityManager, EntityAttributes entityAttributes) {
        this.entityManager = entityManager;
        this.entityAttributes = entityAttributes;
    }

    public T save(T instance) {
        EntityAttribute entityAttribute = entityAttributes.findEntityAttribute(instance.getClass());
        Field idField = entityAttribute.getIdAttribute().getField();
        idField.setAccessible(true);
        return entityManager.persist(instance);
    }
}
