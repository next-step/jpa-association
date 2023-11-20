package persistence.meta;

import jakarta.persistence.Entity;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import persistence.entity.OneToManyAssociation;
import persistence.exception.NoEntityException;

public class EntityMeta {
    private final TableName tableName;
    private final EntityColumns entityColumns;
    private final EntityColumn pkColumn;
    private final Class<?> entityClass;
    private OneToManyAssociation oneToManyAssociation;
    private ForeignerColumn foreignerColumn;

    private EntityMeta(Class<?> entityClass) {
        if (entityClass == null || entityClass.getAnnotation(Entity.class) == null) {
            throw new NoEntityException();
        }
        this.tableName = TableName.from(entityClass);
        this.entityColumns = new EntityColumns(entityClass.getDeclaredFields());
        this.pkColumn = entityColumns.pkColumn();
        this.entityClass = entityClass;

        findOneToManyFiled(entityClass).ifPresent(field ->
                this.oneToManyAssociation = OneToManyAssociation
                        .createOneToMayAssociationByField(field, pkColumn));
    }

    private EntityMeta(Class<?> entityClass, ForeignerColumn foreignerColumn) {
        this(entityClass);
        this.foreignerColumn = foreignerColumn;
    }

    public static EntityMeta from(Class<?> entityClass) {
        return new EntityMeta(entityClass);
    }

    public static EntityMeta createManyEntityMeta(Class<?> entityClass, ForeignerColumn foreignerColumn) {
        return new EntityMeta(entityClass, foreignerColumn);
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

    public boolean hasOneToManyAssociation() {
        return oneToManyAssociation != null;
    }

    public boolean hasLazyOneToMayAssociation() {
        return hasOneToManyAssociation() && oneToManyAssociation.isLazy();
    }

    public boolean hasEagerOneToMayAssociation() {
        return hasOneToManyAssociation() && !oneToManyAssociation.isLazy();
    }


    public OneToManyAssociation getOneToManyAssociation() {
        return oneToManyAssociation;
    }

    private Optional<Field> findOneToManyFiled(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(OneToMany.class))
                .findFirst();
    }

    public ForeignerColumn getForeignerColumn() {
        return foreignerColumn;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public boolean hasForeignerColumn() {
        return foreignerColumn != null;
    }

}
