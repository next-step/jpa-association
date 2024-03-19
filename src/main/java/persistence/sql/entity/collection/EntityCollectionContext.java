package persistence.sql.entity.collection;

import java.util.ArrayList;
import java.util.List;

public class EntityCollectionContext {

    private final List<EntityCollection> entityCollections;

    public EntityCollectionContext() {
        this.entityCollections = new ArrayList<>();
    }

    public List<Object> getCollection(final String className, final Object id) {
        return entityCollections.stream()
                .filter(entityCollection -> entityCollection.isEquals(className, id))
                .findFirst()
                .map(EntityCollection::getCollection)
                .orElseGet(() -> null);
    }

    public void addCollection(final String className, final Object id, final List<Object> collection) {
        entityCollections.add(new EntityCollection(className, id, collection));
    }
}
