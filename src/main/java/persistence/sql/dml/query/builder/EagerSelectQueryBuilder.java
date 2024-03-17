package persistence.sql.dml.query.builder;

import persistence.sql.dml.query.clause.ColumnClause;
import persistence.sql.dml.query.clause.WhereClause;
import persistence.sql.entity.EntityMappingTable;
import persistence.sql.entity.model.DomainType;
import persistence.sql.entity.model.DomainTypes;
import persistence.sql.entity.model.SubEntityType;
import persistence.sql.entity.model.TableName;

import java.util.List;
import java.util.stream.Collectors;

import static persistence.sql.constant.SqlConstant.*;
import static persistence.sql.constant.SqlFormat.LEFT_JOIN;
import static persistence.sql.constant.SqlFormat.SELECT;

public class EagerSelectQueryBuilder {

    private static EagerSelectQueryBuilder instance;

    private EagerSelectQueryBuilder() {
    }

    public static EagerSelectQueryBuilder getInstance() {
        if (instance == null) {
            instance = new EagerSelectQueryBuilder();
        }
        return instance;
    }

    public String toSql(EntityMappingTable mainEntityMapping,
                        WhereClause whereClause) {

        String column = createColumn(mainEntityMapping);
        String from = createFrom(mainEntityMapping);
        String where = whereClause.toSql();

        return String.format(SELECT.getFormat(),
                column,
                from,
                where
        );
    }

    private String createColumn(final EntityMappingTable mainEntityMapping) {
        TableName tableName = mainEntityMapping.getTable();

        List<String> columnList = mainEntityMapping.getDomainTypeStream()
                .map(domainType -> {
                    if (domainType.isJoinColumn()) {
                        return createSubColumn(domainType);
                    }
                    return domainType.getAlias(tableName.getAlias());
                })
                .collect(Collectors.toList());

        return new ColumnClause(columnList).toSql();
    }

    private String createSubColumn(final DomainType domainType) {
        SubEntityType subEntityType = new SubEntityType(domainType);
        EntityMappingTable subEntityMappingTable = EntityMappingTable.from(subEntityType.getSubClass());
        TableName subTableName = subEntityMappingTable.getTable();

        return subEntityMappingTable.getColumnName()
                .stream()
                .map(column -> subTableName.getAlias() + DOT.getValue() + column)
                .collect(Collectors.joining(LINE_COMMA.getValue()));
    }

    private String createFrom(EntityMappingTable mainEntityMapping) {
        String mainTable = mainEntityMapping.getAliasAndTableName();

        String joinColumns = mainEntityMapping.getDomainTypeStream()
                .filter(DomainType::isJoinColumn)
                .map(domainType -> createJoinTable(mainEntityMapping, domainType))
                .collect(Collectors.joining(LINE_COMMA.getValue()));

        return mainTable + LINE.getValue() + joinColumns;
    }

    private String createJoinTable(final EntityMappingTable mainEntityMapping, final DomainType domainType) {
        SubEntityType subEntityType = new SubEntityType(domainType);
        EntityMappingTable subEntity = EntityMappingTable.from(subEntityType.getSubClass());

        return String.format(LEFT_JOIN.getFormat(),
                subEntity.getAliasAndTableName(),
                domainType.getAlias(mainEntityMapping.getAlias()),
                subEntity.getPkDomainTypes().getAlias(subEntity.getAlias()));
    }

}
