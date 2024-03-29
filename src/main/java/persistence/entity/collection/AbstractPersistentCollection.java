package persistence.entity.collection;

import persistence.entity.loader.EntityLoader;
import persistence.model.PersistentClass;

import java.util.Collection;

public abstract class AbstractPersistentCollection<T> implements Collection<T> {

    private boolean initialized = false;
    private final PersistentClass<T> persistentClass;
    private final EntityLoader entityLoader;

    protected AbstractPersistentCollection(PersistentClass<T> persistentClass, EntityLoader entityLoader) {
        this.persistentClass = persistentClass;
        this.entityLoader = entityLoader;
    }

    protected void initialize() {
        if (initialized) return;


    }
}
