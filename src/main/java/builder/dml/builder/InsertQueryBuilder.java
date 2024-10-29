package builder.dml.builder;

import builder.dml.EntityData;

public class InsertQueryBuilder {

    private final static String INSERT_QUERY = "INSERT INTO {tableName} ({columnNames}) VALUES ({values});";
    private final static String TABLE_NAME = "{tableName}";
    private final static String COLUMN_NAMES = "{columnNames}";
    private final static String VALUES = "{values}";

    //insert 쿼리를 생성한다. Insert 쿼리는 인스턴스의 데이터를 받아야함
    public String buildQuery(EntityData EntityData) {
        return insertQuery(EntityData);
    }

    //insert쿼리문을 생성한다.
    private String insertQuery(EntityData EntityData) {
        return INSERT_QUERY.replace(TABLE_NAME, EntityData.getTableName())
                .replace(COLUMN_NAMES, EntityData.getColumnNames())
                .replace(VALUES, EntityData.getColumnValues());
    }

}
