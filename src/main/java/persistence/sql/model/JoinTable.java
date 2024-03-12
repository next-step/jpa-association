package persistence.sql.model;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import persistence.entity.EntityMetaCache;
import util.CaseConverter;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class JoinTable implements BaseTable {

    private final Class<?> clazz;

    private final Table table;

    private final FetchType fetchType;

    private final String joinColumnName;

    public JoinTable(Field field) {
        this.clazz = buildClazz(field);
        this.table = buildTable();
        this.fetchType = buildFetchType(field);
        this.joinColumnName = buildJoinColumnName(field);
    }

    private Class<?> buildClazz(Field field) {
        ParameterizedType collectionType = (ParameterizedType) field.getGenericType();
        Type entityType = collectionType.getActualTypeArguments()[0];
        String entityName = entityType.getTypeName();
        try {
            return Class.forName(entityName);
        } catch (ClassNotFoundException ignored) {
            throw new EntityNotFoundException();
        }
    }

    private FetchType buildFetchType(Field field) {
        OneToMany oneToMany = field.getDeclaredAnnotation(OneToMany.class);
        return oneToMany.fetch();
    }

    private Table buildTable() {
        EntityMetaCache entityMetaCache = EntityMetaCache.INSTANCE;
        return entityMetaCache.getTable(clazz);
    }

    private String buildJoinColumnName(Field field) {
        JoinColumn joinColumn = field.getDeclaredAnnotation(JoinColumn.class);

        if (joinColumn != null && hasName(joinColumn)) {
            return joinColumn.name();
        }

        String fieldName = field.getName();
        return CaseConverter.camelToSnake(fieldName);
    }

    private boolean hasName(JoinColumn joinColumn) {
        String name = joinColumn.name();
        return !name.isEmpty();
    }

    public FetchType getFetchType() {
        return fetchType;
    }

    public String getJoinColumnName() {
        return joinColumnName;
    }

    @Override
    public String getName() {
        return table.getName();
    }

    @Override
    public PKColumn getPKColumn() {
        return table.getPKColumn();
    }

    @Override
    public String getPKColumnName() {
        return table.getPKColumnName();
    }

    @Override
    public Columns getColumns() {
        return table.getColumns();
    }

    @Override
    public List<JoinTable> getJoinTables() {
        return table.getJoinTables();
    }

    @Override
    public List<String> getAllColumnNames() {
        return table.getAllColumnNames();
    }
}
