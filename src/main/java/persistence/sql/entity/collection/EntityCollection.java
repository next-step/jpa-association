package persistence.sql.entity.collection;

import java.util.List;

public class EntityCollection {

    private final String className;
    private final Object id;
    private final List<Object> collection;

    public EntityCollection(final String className,
                            final Object id,
                            final List<Object> collection) {
        this.className = className;
        this.id = id;
        this.collection = collection;
    }

    public boolean isEquals(final String className, final Object id) {
        return this.className.equals(className) && this.id.equals(id);
    }

    public List<Object> getCollection() {
        return this.collection;
    }

}
