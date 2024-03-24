package persistence.entity.exception;

import persistence.entity.context.EntityKey;

public class EntityReadOnlyException extends RuntimeException {
    public EntityReadOnlyException(EntityKey entityKey) {
        super("Entity with key " + entityKey + " is read-only");
    }
}
