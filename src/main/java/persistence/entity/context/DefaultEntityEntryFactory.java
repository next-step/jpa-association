package persistence.entity.context;

import persistence.entity.Status;

public class DefaultEntityEntryFactory implements EntityEntryFactory {
    public EntityEntry createEntityEntry(Status status) {
        return new EntityEntryImpl(status);
    }
}
