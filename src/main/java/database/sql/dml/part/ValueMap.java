package database.sql.dml.part;

import database.mapping.column.GeneralEntityColumn;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO: Map 말고 배열로 변경
public class ValueMap {
    private final Map<String, Object> source;

    public ValueMap(Map<String, Object> source) {
        this.source = new HashMap<>(source);
    }

    public static ValueMap of(String key, Object value) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(key, value);
        return from(map);
    }

    public static ValueMap of(String key1, Object value1, String key2, Object value2) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        return from(map);
    }

    public static ValueMap of(String key1, Object value1, String key2, Object value2, String key3, Object value3) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        return from(map);
    }

    public static ValueMap fromEntity(Object entity, List<GeneralEntityColumn> generalColumns) {
        Map<String, Object> map = new HashMap<>();
        for (GeneralEntityColumn column : generalColumns) {
            map.put(column.getColumnName(), column.getValue(entity));
        }
        return from(map);
    }

    public static ValueMap empty() {
        return from(Map.of());
    }

    public static ValueMap from(Map<String, Object> map) {
        return new ValueMap(map);
    }

    public Object get(String key) {
        return source.get(key);
    }

    public boolean containsKey(String key) {
        return source.containsKey(key);
    }

    public boolean isEmpty() {
        return source.isEmpty();
    }

    @Override
    public int hashCode() {
        return source.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ValueMap && source.equals(((ValueMap) obj).source);
    }
}
