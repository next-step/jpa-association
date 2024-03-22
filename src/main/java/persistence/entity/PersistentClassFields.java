package persistence.entity;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class PersistentClassFields {

    private final Map<String, Object> valueMap = new LinkedHashMap<>();

    public void init() {
        this.valueMap.clear();
    }

    public void setFieldValue(final String fieldName, final Object value) {
        valueMap.put(fieldName.toLowerCase(), value);
    }

    public Object getFieldValue(final String fieldName) {
        return this.valueMap.get(fieldName.toLowerCase());
    }

    public boolean isEmpty() {
        return this.valueMap.entrySet().stream().allMatch((entry) -> Objects.isNull(entry.getValue()));
    }
}
