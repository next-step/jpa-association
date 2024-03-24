package persistence.entity.exception;

import persistence.entity.context.EntityKey;

public class EntityNotExistsException extends RuntimeException {
    public EntityNotExistsException(EntityKey entityKey) {
        super(entityKey + " does not exist");
    }
}
