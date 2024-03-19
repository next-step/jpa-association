package persistence.sql.mapping;

import jakarta.persistence.Entity;
import persistence.model.EntityJoinEntityField;
import persistence.model.EntityMetaData;
import persistence.model.EntityMetaDataMapping;
import persistence.sql.QueryException;
import persistence.sql.dml.ComparisonOperator;

import java.util.List;

public class TableBinder {

    private final ColumnBinder columnBinder = new ColumnBinder(ColumnTypeMapper.getInstance());

    public Table createTable(final Object object) {
        final Class<?> entityClass = object.getClass();
        final Table table = new Table(toTableName(entityClass));
        final List<Column> columns = columnBinder.createColumns(EntityMetaDataMapping.getMetaData(entityClass.getName()), object);
        table.addColumns(columns);

        return table;
    }

    public Table createTable(final Class<?> clazz) {
        final Table table = new Table(toTableName(clazz));
        final EntityMetaData metaData = EntityMetaDataMapping.getMetaData(clazz.getName());
        final List<Column> columns = columnBinder.createColumns(metaData);
        table.addColumns(columns);
        metaData.getJoinFields()
                .stream().filter(EntityJoinEntityField::isNotLazy)
                .forEach(field -> {
                    final Table joinedTable = createTable(field.getFieldType());
                    final JoinColumn predicate = new JoinColumn(table.getPrimaryKey().getColumns().get(0).getName(), field.getJoinedColumnName(), ComparisonOperator.Comparisons.EQ);
                    final TableJoin tableJoin = new TableJoin(clazz.getName(), table.getName(), joinedTable, SqlAstJoinType.LEFT, predicate);
                    table.addTableJoin(tableJoin);
                });

        return table;
    }

    public Table createTable(final Class<?> clazz, final List<Column> columns) {
        final Table table = this.createTable(clazz);
        table.addColumns(columns);

        return table;
    }

    private String toTableName(final Class<?> clazz) {
        validationEntityClazz(clazz);

        final jakarta.persistence.Table tableAnnotation = clazz.getAnnotation(jakarta.persistence.Table.class);

        if (tableAnnotation == null || tableAnnotation.name().isBlank()) {
            return clazz.getSimpleName();
        }

        return tableAnnotation.name();
    }

    private void validationEntityClazz(final Class<?> clazz) {
        if (!clazz.isAnnotationPresent(Entity.class)) {
            throw new QueryException(clazz.getSimpleName() + " is not entity");
        }
    }

}
