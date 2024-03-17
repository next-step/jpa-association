package persistence.sql.ddl.query.builder;

import persistence.sql.entity.EntityMappingTable;
import persistence.sql.entity.model.DomainType;

import java.lang.reflect.ParameterizedType;
import java.util.stream.Collectors;

import static persistence.sql.constant.SqlConstant.COMMA;
import static persistence.sql.constant.SqlFormat.FOREIGN_KEY;

public class ForeignKeyBuilder {
    private final EntityMappingTable entityMappingTable;

    public ForeignKeyBuilder(final EntityMappingTable entityMappingTable) {
        this.entityMappingTable = entityMappingTable;
    }

    public String toSql() {
        return entityMappingTable.getDomainTypes().getDomainTypes()
                .stream()
                .filter(DomainType::isJoinColumn)
                .map(this::getForeignKey)
                .collect(Collectors.joining(COMMA.getValue()));
    }

    private String getForeignKey(final DomainType domainType) {
        Class<?> subClass = (Class<?>) ((ParameterizedType) domainType.getField().getGenericType()).getActualTypeArguments()[0];
        EntityMappingTable subEntity = EntityMappingTable.from(subClass);

        return String.format(FOREIGN_KEY.getFormat(),
                domainType.getColumnName(), subEntity.getTableName().getName());
    }

}
