package database.mapping;

import database.mapping.column.EntityColumn;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO: 얘랑 RowMapperFactory 랑 로직이 겹치나?
// TODO: 객체화될 일이 아예 없을까?
public class ColumnValueMap {

    private ColumnValueMap() {
    }

    public static Map<String, Object> valueMapFromEntity(Object entity) {
        return extractValues(entity);
    }

    private static Map<String, Object> extractValues(Object entity) {
        EntityMetadata entityMetadata = EntityMetadataFactory.get(entity.getClass());
        List<EntityColumn> generalColumns = entityMetadata.getGeneralColumns();

        // column.getValue(entity) 이 null일 경우가 있어서, Collectors.toMap 대신 for 를 사용합니다.
        Map<String, Object> map = new HashMap<>();
        for (EntityColumn column : generalColumns) {
            map.put(column.getColumnName(), column.getValue(entity));
        }
        return map;
    }
}
