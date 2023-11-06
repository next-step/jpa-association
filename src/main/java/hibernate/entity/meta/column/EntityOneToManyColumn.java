package hibernate.entity.meta.column;

import hibernate.entity.meta.EntityClass;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

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
    public void addFieldValue(final Object entity, final Object value) {
        try {
            field.setAccessible(true);
            List<Object> objects = (List<Object>) field.get(entity);
            if (objects == null) {
                objects = new ArrayList<>();
                objects.add(value);
                field.set(entity, objects);
            } else {
                objects.add(value);
            }
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("필드값에 접근할 수 없습니다.");
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Entity 객체에 일치하는 필드값이 없습니다.");
        } finally {
            field.setAccessible(false);
        }
    }

    @Override
    public void assignFieldValue(Object entity, Object value) {
        try {
            field.setAccessible(true);
            field.set(entity, value);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("필드값에 접근할 수 없습니다.");
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Entity 객체에 일치하는 필드값이 없습니다.");
        } finally {
            field.setAccessible(false);
        }
    }
}
