package persistence.entity.exception;

import persistence.entity.context.EntityKey;

public class EntityAlreadyExistsException extends RuntimeException {
    public EntityAlreadyExistsException(EntityKey entityKey) {
        super(entityKey + " already exists");
    }
}
