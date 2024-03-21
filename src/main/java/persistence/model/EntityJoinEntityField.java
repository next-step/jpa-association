package persistence.model;

import jakarta.persistence.*;
import util.StringUtils;

import java.lang.reflect.Field;

public class EntityJoinEntityField {

    private final EntityMetaData metaData;
    private final Field field;
    private final FetchType fetchType;

    public EntityJoinEntityField(final EntityMetaData metaData, final Field field, final FetchType fetchType) {
        this.metaData = metaData;
        this.field = field;
        this.fetchType = fetchType;
    }

    public Class<?> getFieldType() {
        return this.metaData.getEntityClass();
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

    public EntityMetaData getMetaData() {
        return this.metaData;
    }

    public Field getField() {
        return this.field;
    }
}
