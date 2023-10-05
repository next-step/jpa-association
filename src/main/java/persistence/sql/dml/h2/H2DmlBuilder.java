package persistence.sql.dml.h2;

import persistence.entity.EntityMeta;
import persistence.sql.dml.DmlBuilder;

import java.util.Map;

public final class H2DmlBuilder implements DmlBuilder {
    private H2DmlBuilder() {}

    public static H2DmlBuilder getInstance() {
        return SingletonHelper.INSTANCE;
    }

    @Override
    public String getInsertQuery(Object entity) {
        return H2InsertQuery.build(entity);
    }

    @Override
    public String getEagerJoinQuery(EntityMeta meta) {
        return H2EagerJoinQuery.build(meta);
    }

    @Override
    public String getFindAllQuery(Class clazz) {
        return H2FindAllQuery.build(clazz);
    }

    @Override
    public String getWhereQuery(Map<String, Object> condition) {
        return H2WhereQuery.build(condition);
    }

    @Override
    public String getFindByIdQuery(Class clazz, Object id) {
        return H2FindByIdQuery.build(clazz, id);
    }

    @Override
    public String getDeleteByIdQuery(Class clazz, Object id) {
        return H2DeleteByIdQuery.build(clazz, id);
    }

    @Override
    public String getUpdateQuery(Object entity) {
        return H2UpdateQuery.build(entity);
    }

    private static class SingletonHelper {
        private static final H2DmlBuilder INSTANCE = new H2DmlBuilder();
    }
}
