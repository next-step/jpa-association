package persistence.sql.dml.builder;

import persistence.entity.attribute.EntityAttribute;
import persistence.sql.dml.JoinClause;
import persistence.sql.dml.WhereClause;

public class SelectQueryBuilder {
    private final String tableName;
    private final WhereClause whereClause;
    private final JoinClause joinClause;
    private final EntityAttribute entityAttribute;

    private SelectQueryBuilder(EntityAttribute entityAttribute, JoinClause joinClause, WhereClause whereClause) {
        this.entityAttribute = entityAttribute;
        this.tableName = entityAttribute.getTableName();
        this.joinClause = joinClause;
        this.whereClause = whereClause;
    }

    public static SelectQueryBuilder of(EntityAttribute entityAttribute) {
        JoinClause joinClause = new JoinClause(entityAttribute.getOneToManyFields());
        WhereClause whereClause = new WhereClause();
        return new SelectQueryBuilder(entityAttribute, joinClause, whereClause);
    }

    public SelectQueryBuilder where(String fieldName, String value) {
        whereClause.and(fieldName, value);
        return this;
    }

    public SelectQueryBuilder where(String alias, String fieldName, String value) {
        whereClause.and(alias, fieldName, value);
        return this;
    }

    public String prepareStatement() {
        return String.format("SELECT * FROM %s as %s%s%s", tableName, tableName, joinClause, whereClause);
    }
}
