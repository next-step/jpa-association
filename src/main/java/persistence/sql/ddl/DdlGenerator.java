package persistence.sql.ddl;

import persistence.core.EntityColumn;
import persistence.core.EntityIdColumn;
import persistence.core.EntityMetadata;
import persistence.core.EntityMetadataProvider;
import persistence.dialect.Dialect;

import java.util.Set;

public class DdlGenerator {

    private final Dialect dialect;

    public DdlGenerator(final Dialect dialect) {
        this.dialect = dialect;
    }

    public String generateCreateDdl(final EntityMetadata<?> entityMetadata) {
        final StringBuilder builder = new StringBuilder();

        final String tableName = entityMetadata.getTableName();
        builder.append("create table ")
                .append(tableName)
                .append(" ")
                .append(generateColumnsClause(entityMetadata));

        return builder.toString();
    }

    private String generateColumnsClause(final EntityMetadata<?> entityMetadata) {
        final StringBuilder builder = new StringBuilder();
        builder.append("(");

        entityMetadata.getColumns().forEach(column -> {
                    if (column.isOneToMany()) {
                        return;
                    }
                    builder.append(generateColumnDefinition(column))
                            .append(",");
                }
        );

        builder.append(generateAssociatedColumnsClause(entityMetadata));

        builder.append(generatePKConstraintClause(entityMetadata));
        builder.append(")");
        return builder.toString();
    }

    private String generateAssociatedColumnsClause(final EntityMetadata<?> entityMetadata) {
        final StringBuilder builder = new StringBuilder();
        final Set<EntityMetadata<?>> allAssociatedEntitiesMetadata = EntityMetadataProvider.getInstance().getAllAssociatedEntitiesMetadata(entityMetadata);
        allAssociatedEntitiesMetadata.forEach(associatedEntityMetadata -> {
            final EntityIdColumn associatedEntityIdColumn = associatedEntityMetadata.getIdColumn();
            associatedEntityMetadata.getOneToManyColumns().forEach(entityOneToManyColumn ->
                    builder.append(entityOneToManyColumn.getName())
                            .append(" ")
                            .append(dialect.getColumnTypeMapper().getColumnName(associatedEntityIdColumn.getType()))
                            .append(generateNotNullClause(entityOneToManyColumn))
                            .append(",")
                            .append("foreign key(")
                            .append(entityOneToManyColumn.getName())
                            .append(") references order (")
                            .append(associatedEntityIdColumn.getName())
                            .append(")")
                            .append(",")
            );
        });
        return builder.toString();
    }

    private String generateColumnDefinition(final EntityColumn column) {
        final StringBuilder builder = new StringBuilder();
        builder.append(column.getName())
                .append(" ")
                .append(generateColumnTypeClause(column))
                .append(generateNotNullClause(column))
                .append(generateAutoIncrementClause(column));
        return builder.toString();
    }

    private String generateColumnTypeClause(final EntityColumn column) {
        final StringBuilder builder = new StringBuilder();
        builder.append(dialect.getColumnTypeMapper().getColumnName(column.getType()));
        if (column.isStringValued()) {
            builder.append("(")
                    .append(column.getStringLength())
                    .append(")");
        }
        return builder.toString();
    }

    private String generateAutoIncrementClause(final EntityColumn column) {
        if (column.isAutoIncrement()) {
            return " auto_increment";
        }

        return "";
    }

    private String generateNotNullClause(final EntityColumn column) {
        if (column.isNotNull()) {
            return " not null";
        }

        return "";
    }

    private String generatePKConstraintClause(final EntityMetadata<?> entityMetadata) {
        final StringBuilder builder = new StringBuilder();
        builder.append("CONSTRAINT PK_")
                .append(entityMetadata.getTableName())
                .append(" PRIMARY KEY (")
                .append(entityMetadata.getIdColumnName())
                .append(")");
        return builder.toString();
    }

    public String generateDropDdl(final EntityMetadata<?> entityMetadata) {
        final StringBuilder builder = new StringBuilder();
        builder.append("drop table ")
                .append(entityMetadata.getTableName());

        return builder.toString();
    }
}
