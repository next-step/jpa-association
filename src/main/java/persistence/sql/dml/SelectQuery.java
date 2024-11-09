package persistence.sql.dml;

import persistence.sql.entity.EntityJoin;
import persistence.sql.entity.EntityJoinInfo;
import persistence.sql.exception.ExceptionMessage;
import persistence.sql.exception.RequiredClassException;
import persistence.sql.model.*;

public class SelectQuery {

    private static final String SPACE = " ";
    private SelectQuery() {
    }

    private static class SelectQueryHolder {
        public static final SelectQuery INSTANCE = new SelectQuery();
    }

    public static SelectQuery getInstance() {
        return SelectQueryHolder.INSTANCE;
    }

    public String findAll(Class<?> clazz) {
        if (clazz == null) {
            throw new RequiredClassException(ExceptionMessage.REQUIRED_CLASS);
        }

        TableName tableName = new TableName(clazz);
        EntityColumnNames entityColumnNames = new EntityColumnNames(clazz);
        return String.format("SELECT %s FROM %s %s", entityColumnNames.getColumnNames(), tableName.getValue(), tableName.getAlias());
    }


    public String findById(Class<?> clazz, Object id) {
        if (clazz == null) {
            throw new RequiredClassException(ExceptionMessage.REQUIRED_CLASS);
        }

        if (id == null) {
            throw new IllegalArgumentException("id가 존재하지 않습니다.");
        }

        TableName tableName = new TableName(clazz);
        EntityColumnNames entityColumnNames = new EntityColumnNames(clazz);
        EntityJoin entityJoin = new EntityJoin(clazz);


        StringBuilder findByIdQueryStringBuilder = new StringBuilder();
        findByIdQueryStringBuilder.append("SELECT");
        findByIdQueryStringBuilder.append(SPACE);
        findByIdQueryStringBuilder.append(entityColumnNames.getColumnNames());

        for (Class<?> joinClass : entityJoin.getJoinClasses()) {
            EntityColumnNames joinEntityColumnNames = new EntityColumnNames(joinClass);
            findByIdQueryStringBuilder.append(",");
            findByIdQueryStringBuilder.append(SPACE);
            findByIdQueryStringBuilder.append(joinEntityColumnNames.getColumnNames());
        }

        findByIdQueryStringBuilder.append(SPACE);
        findByIdQueryStringBuilder.append("FROM");
        findByIdQueryStringBuilder.append(SPACE);
        findByIdQueryStringBuilder.append(String.format("%s %s", tableName.getValue(), tableName.getAlias()));

        if (entityJoin.isEntityJoin()) {
            findByIdQueryStringBuilder.append(SPACE);
            findByIdQueryStringBuilder.append(entityJoin.makeJoinTableQuery());
            findByIdQueryStringBuilder.append(SPACE);
        }

        findByIdQueryStringBuilder.append(SPACE);
        WhereClause whereClause = new WhereClause("id", Operator.equals, id, tableName.getAlias());
        findByIdQueryStringBuilder.append(whereClause.makeWhereQuery());
        return findByIdQueryStringBuilder.toString();
    }
}
