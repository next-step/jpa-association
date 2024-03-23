package persistence.sql.mapping;

import jakarta.persistence.Entity;
import persistence.model.EntityJoinEntityField;
import persistence.model.EntityMetaData;
import persistence.model.EntityMetaDataMapping;
import persistence.sql.QueryException;
import persistence.sql.dml.ComparisonOperator;

import java.util.List;
import java.util.stream.Collectors;

public class TableBinder {

    private final ColumnBinder columnBinder = new ColumnBinder(ColumnTypeMapper.getInstance());

    public Table createTable(final Object object) {
        final Class<?> entityClass = object.getClass();
        final Table table = new Table(toTableName(entityClass));
        final List<Column> columns = columnBinder.createColumns(table.getName(), EntityMetaDataMapping.getMetaData(entityClass.getName()), object);
        table.addColumns(columns);
        final EntityMetaData metaData = EntityMetaDataMapping.getMetaData(entityClass.getName());
        final List<TableJoin> tableJoins = extractTableJoins(metaData, table, entityClass);
        table.addTableJoins(tableJoins);

        return table;
    }

    public Table createTable(final Class<?> clazz) {
        final Table table = new Table(toTableName(clazz));
        final EntityMetaData metaData = EntityMetaDataMapping.getMetaData(clazz.getName());
        final List<Column> columns = columnBinder.createColumns(table.getName(), metaData);
        table.addColumns(columns);
        final List<TableJoin> tableJoins = extractTableJoins(metaData, table, clazz);
        table.addTableJoins(tableJoins);

        return table;
    }

    private List<TableJoin> extractTableJoins(final EntityMetaData metaData, final Table table, final Class<?> entityClass) {
        return metaData.getJoinFields()
                .stream().filter(EntityJoinEntityField::isNotLazy)
                .map(field -> {
                    final Table joinedTable = createTable(field.getFieldType());
                    final JoinColumn predicate = new JoinColumn(table.getPrimaryKey().getColumns().get(0).getName(), field.getJoinedColumnName(), ComparisonOperator.Comparisons.EQ);
                    return new TableJoin(entityClass.getName(), table.getName(), joinedTable, SqlAstJoinType.LEFT, predicate);
                }).collect(Collectors.toList());
    }

    public Table createTable(final Class<?> clazz, final List<Column> columns) {
        final Table table = this.createTable(clazz);
        table.addColumns(columns);

        return table;
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
