package database.sql.dml.part;

import database.mapping.column.EntityColumn;
import database.mapping.column.GeneralEntityColumn;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValueClause {

    public static Map<String, Object> fromEntity(Object entity, List<GeneralEntityColumn> generalColumns) {
        // column.getValue(entity) 이 null일 경우가 있어서, Collectors.toMap 대신 for 를 사용합니다.
        Map<String, Object> map = new HashMap<>();
        for (EntityColumn column : generalColumns) {
            map.put(column.getColumnName(), column.getValue(entity));
        }
        return map;
    }
}
