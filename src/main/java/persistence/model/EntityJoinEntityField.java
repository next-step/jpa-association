package persistence.model;

import jakarta.persistence.*;
import util.StringUtils;

import java.lang.reflect.Field;

public class EntityJoinEntityField {

    private final Class<?> clazz;
    private final Field field;
    private final FetchType fetchType;

    public EntityJoinEntityField(final Class<?> clazz, final Field field, final FetchType fetchType) {
        this.clazz = clazz;
        this.field = field;
        this.fetchType = fetchType;
    }

    public Class<?> getFieldType() {
        return this.clazz;
    }

    public String getJoinedColumnName() {
        final JoinColumn annotation = this.field.getAnnotation(JoinColumn.class);
        if (annotation != null && StringUtils.isNotBlank(annotation.name())) {
            return annotation.name();
        }

        return this.field.getName();
    }

    public boolean isNotLazy() {
        return this.fetchType == FetchType.EAGER;
    }

    public Field getField() {
        return this.field;
    }
}
