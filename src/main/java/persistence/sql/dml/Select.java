package persistence.sql.dml;

import persistence.entity.metadata.EntityColumns;
import persistence.entity.metadata.EntityMetadata;

public interface Select {

    String selectByIdQuery(String tableName, EntityColumns columns, Object id);

    String selectJoinQuery(EntityMetadata mainEntity, EntityMetadata joinEntity, String joinColumn, Object id);

}
