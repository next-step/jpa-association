package persistence.entity.proxy;

import net.sf.cglib.proxy.LazyLoader;
import persistence.entity.common.EntityId;
import persistence.entity.entitymanager.EntityManager;

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
