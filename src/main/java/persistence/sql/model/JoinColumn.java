package persistence.sql.model;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import persistence.entity.common.EntityMetaCache;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class JoinColumn implements BaseColumn {


    private final FetchType fetchType;

    private final Table table;

    private final Column column;

    public JoinColumn(Field field) {
        this.fetchType = buildFetchType(field);
        this.table = buildTable(field);
        this.column = new Column(field);
    }

    private FetchType buildFetchType(Field field) {
        OneToMany oneToMany = field.getDeclaredAnnotation(OneToMany.class);
        return oneToMany.fetch();
    }

    private Table buildTable(Field field) {
        EntityMetaCache entityMetaCache = EntityMetaCache.INSTANCE;

        ParameterizedType collectionType = (ParameterizedType) field.getGenericType();
        Type entityType = collectionType.getActualTypeArguments()[0];
        String entityName = entityType.getTypeName();
        try {
            Class<?> clazz = Class.forName(entityName);
            return entityMetaCache.getTable(clazz);
        } catch (ClassNotFoundException ignored) {
            throw new EntityNotFoundException();
        }
    }

    public boolean isLazy() {
        return fetchType == FetchType.LAZY;
    }

    public Table getTable() {
        return table;
    }

    @Override
    public Field getField() {
        return column.getField();
    }

    @Override
    public String getName() {
        return column.getName();
    }
}
