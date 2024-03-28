package persistence.sql.mapping;

import jakarta.persistence.Entity;
import persistence.ReflectionUtils;
import persistence.model.*;
import persistence.sql.QueryException;
import persistence.sql.dml.ComparisonOperator;

import java.util.List;
import java.util.stream.Collectors;

public class TableBinder {

    private final ColumnBinder columnBinder = new ColumnBinder(ColumnTypeMapper.getInstance());

    public Table createTable(final Object object) {
        final Class<?> entityClass = object.getClass();
        final Table table = new Table(toTableName(entityClass));
        final List<Column> columns = columnBinder.createColumns(table.getName(), PersistentClassMapping.getPersistentClass(entityClass.getName()), object);
        table.addColumns(columns);

        return table;
    }

    public Table createTable(final Class<?> clazz) {
        final Table table = new Table(toTableName(clazz));
        final PersistentClass<?> persistentClass = PersistentClassMapping.getPersistentClass(clazz.getName());
        final List<Column> columns = columnBinder.createColumns(table.getName(), persistentClass);
        table.addColumns(columns);

        return table;
    }

    public Table createTable(final Class<?> clazz, final CollectionPersistentClassBinder collectionPersistentClassBinder) {
        final Table table = this.createTable(clazz);
        final PersistentClass<?> persistentClass = PersistentClassMapping.getPersistentClass(clazz.getName());
        final List<TableJoin> tableJoins = extractTableJoins(table, persistentClass, collectionPersistentClassBinder);
        table.addTableJoins(tableJoins);

        return table;
    }

    private List<TableJoin> extractTableJoins(final Table table, final PersistentClass<?> persistentClass, final CollectionPersistentClassBinder collectionPersistentClassBinder) {
        return persistentClass.getFields()
                .stream()
                .filter(AbstractEntityField::isJoinField)
                .map(field -> {
                    final EntityJoinField joinField = (EntityJoinField) field;
                    final Table joinedTable = createTable(collectionPersistentClassBinder.getCollectionPersistentClass(ReflectionUtils.mapToGenericClassName(joinField.getField())).getEntityClass());
                    final JoinColumn predicate = new JoinColumn(table.getPrimaryKey().getColumns().get(0).getName(), joinField.getJoinedColumnName(), ComparisonOperator.Comparisons.EQ);
                    return new TableJoin(persistentClass.getEntityName(), table.getName(), joinedTable, SqlAstJoinType.LEFT, predicate);
                }).collect(Collectors.toList());
    }

    public static String toTableName(final Class<?> clazz) {
        validationEntityClazz(clazz);

        final jakarta.persistence.Table tableAnnotation = clazz.getAnnotation(jakarta.persistence.Table.class);

        if (tableAnnotation == null || tableAnnotation.name().isBlank()) {
            return clazz.getSimpleName();
        }

        return tableAnnotation.name();
    }

    private static void validationEntityClazz(final Class<?> clazz) {
        if (!clazz.isAnnotationPresent(Entity.class)) {
            throw new QueryException(clazz.getSimpleName() + " is not entity");
        }
    }

}
