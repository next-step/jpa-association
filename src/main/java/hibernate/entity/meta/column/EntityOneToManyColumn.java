package hibernate.entity.meta.column;

import hibernate.entity.meta.EntityClass;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

public class EntityOneToManyColumn implements EntityJoinColumn {

    private final String fieldName;
    private final FetchType fetchType;
    private final EntityClass<?> entityClass;
    private final Field field;

    public EntityOneToManyColumn(final Field field) {
        if (!field.isAnnotationPresent(OneToMany.class)) {
            throw new IllegalArgumentException("OneToMany 어노테이션이 없습니다.");
        }
        this.fieldName = parseFieldName(field);
        this.fetchType = field.getAnnotation(OneToMany.class)
                .fetch();
        this.entityClass = EntityClass.getInstance((Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0]);
        this.field = field;
    }

    private String parseFieldName(final Field field) {
        if (!field.isAnnotationPresent(JoinColumn.class)) {
            return field.getName();
        }
        String fieldName = field.getAnnotation(JoinColumn.class).name();
        if (fieldName.isEmpty()) {
            return field.getName();
        }
        return fieldName;
    }

    @Override
    public String getJoinColumnName() {
        return fieldName;
    }

    @Override
    public FetchType getFetchType() {
        return fetchType;
    }

    @Override
    public EntityClass<?> getEntityClass() {
        return entityClass;
    }

    @Override
    public void assignFieldValue(final Object entity, final Object value) {
    }
}
