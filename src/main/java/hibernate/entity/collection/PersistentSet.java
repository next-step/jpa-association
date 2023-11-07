package hibernate.entity.collection;

import hibernate.entity.EntityLoader;
import hibernate.entity.meta.EntityClass;

import java.util.Set;

public class PersistentSet<T> extends AbstractPersistCollection<T> implements Set<T> {

    protected PersistentSet(final EntityClass<T> entityClass, final EntityLoader entityLoader) {
        super(entityClass, entityLoader);
    }
}
