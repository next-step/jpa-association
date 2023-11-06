package persistence.meta;

import jakarta.persistence.Entity;
import jakarta.persistence.GenerationType;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;
import persistence.association.OneToManyAssociation;
import persistence.exception.NoEntityException;

public class EntityMeta {
    private final TableName tableName;
    private final EntityColumns entityColumns;
    private final EntityColumn pkColumn;
    private final Class<?> entityClass;

    private final Optional<OneToManyAssociation> oneToManyAssociation;

    private EntityMeta(Class<?> entityClass) {
        if (entityClass == null || entityClass.getAnnotation(Entity.class) == null) {
            throw new NoEntityException();
        }

        this.tableName = TableName.from(entityClass);
        this.entityColumns = new EntityColumns(entityClass.getDeclaredFields());
        this.pkColumn = entityColumns.pkColumn();
        this.entityClass = entityClass;
        this.oneToManyAssociation = OneToManyAssociation.from(entityClass);
    }

    public static EntityMeta from(Class<?> entityClass) {
        return new EntityMeta(entityClass);
    }

    public <T> T createCopyEntity(T entity) {
        try {
            T newEntity = (T) entityClass.getDeclaredConstructor().newInstance();
            for (EntityColumn entityColumn : entityColumns.getEntityColumns()) {
                final Field declaredField = entity.getClass().getDeclaredField(entityColumn.getFieldName());
                declaredField.setAccessible(true);
                declaredField.set(newEntity, entityColumn.getFieldValue(entity));
            }
            return newEntity;
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException |
                 NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }


    public String getTableName() {
        return tableName.getValue();
    }

    public List<EntityColumn> getEntityColumns() {
        return entityColumns.getEntityColumns();
    }

    public Object getPkValue(Object entity) {
        return pkColumn.getFieldValue(entity);
    }

    public EntityColumn getPkColumn() {
        return pkColumn;
    }

    public boolean isAutoIncrement() {
        final EntityColumn pkColumn = entityColumns.pkColumn();
        return GenerationType.IDENTITY.equals(pkColumn.getGenerationType());
    }

    public boolean hasOneToManyAssociate() {
        return oneToManyAssociation.isPresent();
    }

    public OneToManyAssociation getOneToManyAssociation() {
        if (oneToManyAssociation.isEmpty()) {
            throw new IllegalArgumentException("OneToMany 필드가 없습니다.");
        }
        return oneToManyAssociation.get();
    }

}
