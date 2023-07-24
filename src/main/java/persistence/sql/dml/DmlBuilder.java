package persistence.sql.dml;

import persistence.entity.EntityMeta;

public interface DmlBuilder {
    String getInsertQuery(Object entity);

    String getEagerJoinQuery(EntityMeta clazz);

    String getFindAllQuery(Class clazz);

    String getFindByIdQuery(Class clazz, Object id);

    String getDeleteByIdQuery(Class clazz, Object id);

    String getUpdateQuery(Object entity);
}
