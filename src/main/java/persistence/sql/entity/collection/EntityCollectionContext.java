package persistence.sql.entity.collection;

import java.util.ArrayList;
import java.util.List;

public class EntityCollectionContext {

    private final List<EntityCollection> entityCollections;

    public EntityCollectionContext() {
        this.entityCollections = new ArrayList<>();
    }

    public EntityCollection getCollection(final String className, final Object id) {
        return entityCollections.stream()
                .filter(entityCollection -> entityCollection.isEquals(className, id))
                .findFirst()
                .orElseGet(() -> null);
    }

    public void addCollection(final String className, final Object id, final List<?> collection) {
        entityCollections.add(new EntityCollection(className, id, collection));
    }
}
