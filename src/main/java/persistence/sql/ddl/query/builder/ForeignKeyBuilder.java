package persistence.sql.ddl.query.builder;

import persistence.sql.entity.EntityMappingTable;
import persistence.sql.entity.model.DomainType;
import persistence.sql.entity.model.SubEntityType;

import java.util.stream.Collectors;

import static persistence.sql.constant.SqlConstant.COMMA;
import static persistence.sql.constant.SqlFormat.FOREIGN_KEY;

public class ForeignKeyBuilder {
    private final EntityMappingTable entityMappingTable;

    public ForeignKeyBuilder(final EntityMappingTable entityMappingTable) {
        this.entityMappingTable = entityMappingTable;
    }

    public String toSql() {
        return entityMappingTable.getDomainTypeStream()
                .filter(DomainType::isJoinColumn)
                .map(this::getForeignKey)
                .collect(Collectors.joining(COMMA.getValue()));
    }

    private String getForeignKey(final DomainType domainType) {
        SubEntityType subEntityType = new SubEntityType(domainType);
        EntityMappingTable subEntity = EntityMappingTable.from(subEntityType.getSubClass());

        return String.format(FOREIGN_KEY.getFormat(),
                domainType.getColumnName(),
                subEntity.getTableName());
    }

}
