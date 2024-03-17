package persistence.sql.ddl.query.builder;

import persistence.sql.dialect.database.ConstraintsMapper;
import persistence.sql.dialect.database.TypeMapper;
import persistence.sql.entity.EntityMappingTable;
import persistence.sql.entity.model.DomainType;
import persistence.sql.entity.model.DomainTypes;
import persistence.sql.entity.model.TableName;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static persistence.sql.constant.SqlConstant.LINE_COMMA;
import static persistence.sql.constant.SqlFormat.CREATE;

public class CreateQueryBuilder {

    private final TableName tableName;
    private final List<ColumnBuilder> columnBuilders;
    private final ForeignKeyBuilder foreignKeyBuilder;

    private CreateQueryBuilder(final TableName tableName,
                               final List<ColumnBuilder> columnBuilders,
                               final ForeignKeyBuilder foreignKeyBuilder) {
        this.tableName = tableName;
        this.columnBuilders = columnBuilders;
        this.foreignKeyBuilder = foreignKeyBuilder;
    }

    public static CreateQueryBuilder of(final EntityMappingTable entityMappingTable,
                                        final TypeMapper typeMapper,
                                        final ConstraintsMapper constantTypeMapper) {
        List<ColumnBuilder> columnBuilders = getColumnBuilders(
                entityMappingTable.getDomainTypes(),
                typeMapper,
                constantTypeMapper);
        ForeignKeyBuilder foreignKeyBuilder = new ForeignKeyBuilder(entityMappingTable);
        return new CreateQueryBuilder(entityMappingTable.getTable(), columnBuilders, foreignKeyBuilder);
    }

    private static List<ColumnBuilder> getColumnBuilders(final DomainTypes domainTypes,
                                                         final TypeMapper typeMapper,
                                                         final ConstraintsMapper constantTypeMapper) {
        return domainTypes.getDomainTypeStream()
                .filter(DomainType::isEntityColumn)
                .map(domainType -> new ColumnBuilder(domainType, typeMapper, constantTypeMapper))
                .collect(Collectors.toList());
    }

    public String toSql() {
        String columns = columnBuilders.stream()
                .map(ColumnBuilder::build)
                .collect(Collectors.joining(LINE_COMMA.getValue()));

        String columnSql = Stream.of(columns, foreignKeyBuilder.toSql())
                .filter(column -> !column.isEmpty())
                .collect(Collectors.joining(LINE_COMMA.getValue()));

        return String.format(CREATE.getFormat(),
                tableName.getName(),
                columnSql);
    }

}
