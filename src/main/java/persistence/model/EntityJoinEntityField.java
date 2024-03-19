package persistence.model;

import jakarta.persistence.*;
import util.StringUtils;

import java.lang.reflect.Field;

public class EntityJoinEntityField {

    protected final Class<?> entityClass;
    protected final Field field;
    protected final FetchType fetchType;

    public EntityJoinEntityField(final Class<?> entityClass, final Field field, final FetchType fetchType) {
        this.entityClass = entityClass;
        this.field = field;
        this.fetchType = fetchType;
    }

    public Class<?> getFieldType() {
        return this.entityClass;
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
}
