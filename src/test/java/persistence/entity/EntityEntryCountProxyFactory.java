package persistence.entity;

import persistence.entity.context.EntityEntry;
import persistence.entity.context.EntityEntryFactory;
import persistence.entity.context.EntityEntryImpl;

public class EntityEntryCountProxyFactory implements EntityEntryFactory {
    @Override
    public EntityEntry createEntityEntry(Status status) {
        return new EntityEntryCountProxy(new EntityEntryImpl(status));
    }
}
