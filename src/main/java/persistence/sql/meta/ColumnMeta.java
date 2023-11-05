package persistence.sql.meta;

import jakarta.persistence.*;
import persistence.sql.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

public class ColumnMeta {

    private static final int FIRST_INDEX = 0;

    private final Field field;

    private ColumnMeta(Field field) {
        this.field = field;
    }

    public static ColumnMeta of(Field field) {
        return new ColumnMeta(field);
    }

    public boolean isId() {
        return field.isAnnotationPresent(Id.class);
    }

    public boolean isTransient() {
        return field.isAnnotationPresent(Transient.class);
    }

    public boolean isGenerationTypeIdentity() {
        if (!field.isAnnotationPresent(GeneratedValue.class)) {
            return false;
        }
        GeneratedValue generatedValue = field.getDeclaredAnnotation(GeneratedValue.class);
        return generatedValue.strategy() == GenerationType.IDENTITY;
    }

    public boolean isNullable() {
        Column columnAnnotation = field.getDeclaredAnnotation(Column.class);
        return columnAnnotation == null || columnAnnotation.nullable();
    }

    public boolean isJoinColumn() {
        return field.isAnnotationPresent(OneToMany.class) || field.isAnnotationPresent(ManyToOne.class);
    }

    public String getColumnName() {
        Column columnAnnotation = field.getDeclaredAnnotation(Column.class);
        if (columnAnnotation == null || StringUtils.isNullOrEmpty(columnAnnotation.name())) {
            return field.getName().toLowerCase();
        }
        return columnAnnotation.name();
    }

    public String getJoinTableName() {
        Class<?> type = field.getType();
        if (List.class.isAssignableFrom(type)) {
            return getJoinTableNameFromGenericType();
        }
        EntityMeta entityMeta = MetaFactory.get(type);
        return entityMeta.getTableName();
    }

    private String getJoinTableNameFromGenericType() {
        EntityMeta entityMeta = getJoinTableEntityMeta();
        return entityMeta.getTableName();
    }

    public EntityMeta getJoinTableEntityMeta() {
        Type genericType = field.getGenericType();
        ParameterizedType parameterizedType = (ParameterizedType) genericType;
        Class<?> genericClass = (Class<?>) parameterizedType.getActualTypeArguments()[FIRST_INDEX];
        return MetaFactory.get(genericClass);
    }

    public String getJoinColumnName() {
        JoinColumn joinColumn = field.getDeclaredAnnotation(JoinColumn.class);
        if (joinColumn == null || StringUtils.isNullOrEmpty(joinColumn.name())) {
            return getColumnName();
        }
        return joinColumn.name();
    }

    public Class<?> getJavaType() {
        return field.getType();
    }

    public String getFieldName() {
        return field.getName();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        ColumnMeta that = (ColumnMeta) object;
        return Objects.equals(field, that.field);
    }

    @Override
    public int hashCode() {
        return Objects.hash(field);
    }
}
