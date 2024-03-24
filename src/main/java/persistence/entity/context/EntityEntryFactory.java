package persistence.entity.context;

import persistence.entity.Status;

public interface EntityEntryFactory {
    EntityEntry createEntityEntry(Status status);
}
