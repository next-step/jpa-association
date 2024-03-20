package persistence.sql.entity.model;

import java.util.List;

import static persistence.sql.constant.SqlFormat.STRING_FORMAT;

public class EntityColumn {

    private final String name;
    private final Class<?> classType;
    private final Object value;

    public EntityColumn(final String name,
                        final Class<?> classType,
                        final Object value) {
        this.name = name;
        this.classType = classType;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Class<?> getClassType() {
        return classType;
    }

    public Object getValue() {
        return value;
    }

    public String getStringValue() {
        if(classType == List.class) {
            List<Object> subEntity = (List<Object>) this.value;
            if(subEntity == null) {
                return null;
            }
            return subEntity.get(0).toString();
        }

        if (classType == String.class) {
            return String.format(STRING_FORMAT.getFormat(), value);
        }

        if (value == null) {
            return null;
        }

        return value.toString();
    }
}
