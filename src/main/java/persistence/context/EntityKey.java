package persistence.context;

import persistence.core.EntityIdColumn;
import persistence.core.EntityMetadata;
import persistence.exception.PersistenceException;

import java.util.Objects;

public class EntityKey {
    private final Object key;
    private final String tableName;
    private final EntityIdColumn idColumn;

    private EntityKey(final Object key, final String tableName, final EntityIdColumn idColumn) {
        validate(key);
        this.tableName = tableName;
        this.idColumn = idColumn;
        this.key = key;
    }

    public static EntityKey of(final EntityMetadata<?> entityMetadata, final Object key) {
        return new EntityKey(key, entityMetadata.getTableName(), entityMetadata.getIdColumn());
    }

    private void validate(final Object key) {
        if (Objects.isNull(key)) {
            throw new PersistenceException("Entity key 생성시 key value 는 null 일 수 없습니다.");
        }
    }

    public Object getKey() {
        return key;
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        final EntityKey entityKey = (EntityKey) object;
        return Objects.equals(key, entityKey.key) && Objects.equals(tableName, entityKey.tableName) && Objects.equals(idColumn, entityKey.idColumn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, tableName, idColumn);
    }
}
