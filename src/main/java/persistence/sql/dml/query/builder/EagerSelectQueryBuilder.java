package persistence.sql.dml.query.builder;

import persistence.sql.dml.query.clause.ColumnClause;
import persistence.sql.dml.query.clause.WhereClause;
import persistence.sql.entity.EntityMappingTable;
import persistence.sql.entity.model.DomainType;
import persistence.sql.entity.model.TableName;

import java.lang.reflect.ParameterizedType;
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
        TableName tableName = mainEntityMapping.getTableName();

        List<String> columnList = mainEntityMapping.getDomainTypes()
                .getDomainTypes()
                .stream()
                .map(domainType -> {
                    if (domainType.isJoinColumn()) {
                        return createSubColumn(domainType);
                    }
                    return domainType.getAcronyms(tableName.getAcronyms());
                })
                .collect(Collectors.toList());

        return new ColumnClause(columnList).toSql();
    }

    private String createSubColumn(final DomainType domainType) {
        Class<?> subClass = (Class<?>) ((ParameterizedType) domainType.getField().getGenericType()).getActualTypeArguments()[0];
        EntityMappingTable subEntityMappingTable = EntityMappingTable.from(subClass);
        TableName subTableName = subEntityMappingTable.getTableName();

        return subEntityMappingTable.getDomainTypes().getColumnName()
                .stream()
                .map(column -> subTableName.getAcronyms() + DOT.getValue() + column)
                .collect(Collectors.joining(LINE_COMMA.getValue()));
    }

    private String createFrom(EntityMappingTable mainEntityMapping) {
        String mainTable = mainEntityMapping.getAcronymsAndTableName();

        String joinColumns = mainEntityMapping.getDomainTypes()
                .getDomainTypes()
                .stream()
                .filter(DomainType::isJoinColumn)
                .map(domainType -> createJoinTable(mainEntityMapping, domainType))
                .collect(Collectors.joining(LINE_COMMA.getValue()));

        return mainTable + LINE.getValue() + joinColumns;
    }

    private String createJoinTable(final EntityMappingTable mainEntityMapping, final DomainType domainType) {
        Class<?> subClass = (Class<?>) ((ParameterizedType) domainType.getField().getGenericType()).getActualTypeArguments()[0];
        EntityMappingTable subEntity = EntityMappingTable.from(subClass);

        return String.format(LEFT_JOIN.getFormat(),
                subEntity.getAcronymsAndTableName(),
                domainType.getAcronyms(mainEntityMapping.getAcronyms()),
                subEntity.getPkDomainTypes().getAcronyms(subEntity.getAcronyms()));
    }

}
