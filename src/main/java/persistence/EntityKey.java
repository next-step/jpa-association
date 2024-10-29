package persistence;

import builder.dml.EntityData;

import java.util.Objects;

public class EntityKey {

    private final Object id;
    private final Class<?> clazz;

    public EntityKey(Object id, Class<?> clazz) {
        this.id = id;
        this.clazz = clazz;
    }

    public EntityKey(EntityData entityData) {
        this.id = entityData.getId();
        this.clazz = entityData.getClazz();
    }

    public Object getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof EntityKey other)) return false;
        return Objects.equals(id, other.id) && clazz.equals(other.clazz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, clazz);
    }
}
