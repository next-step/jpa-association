package database.sql.dml.part;

import java.util.Map;
import java.util.Set;

public class WhereMap {
    private final Map<String, Object> source;

    public static WhereMap of(String key, Object value) {
        return from(Map.of(key, value));
    }

    public static WhereMap of(String key1, Object value1, String key2, Object value2) {
        return from(Map.of(key1, value1, key2, value2));
    }

    public static WhereMap from(Map<String, Object> map) {
        return new WhereMap(map);
    }

    private WhereMap(Map<String, Object> source) {
        this.source = source;
    }

    public Set<String> keySet() {
        return source.keySet();
    }

    public boolean isEmpty() {
        return source.isEmpty();
    }

    public boolean containsKey(String key) {
        return source.containsKey(key);
    }

    public Object get(String key) {
        return source.get(key);
    }
}
