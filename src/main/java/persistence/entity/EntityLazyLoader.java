package persistence.entity;

import net.sf.cglib.proxy.LazyLoader;

public class EntityLazyLoader implements LazyLoader {

    private final EntityManager entityManager;
    private final Class<?> clazz;
    private final EntityId id;

    public EntityLazyLoader(EntityManager entityManager, Class<?> clazz, EntityId id) {
        this.entityManager = entityManager;
        this.clazz = clazz;
        this.id = id;
    }

    @Override
    public Object loadObject() {
        return entityManager.find(clazz, id);
    }
}
