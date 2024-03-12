package persistence.sql.meta;

import jakarta.persistence.FetchType;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.h2.util.StringUtils;

public class Column {

    private static final Pattern CAMEL_CASE_FIELD_NAME_PATTERN = Pattern.compile("([a-z])([A-Z])");
    private static final String SNAKE_CASE_FORMAT = "%s_%s";

    private final Field field;

    protected Column(Field field) {
        this.field = field;
    }

    public static Column from(Field field) {
        return new Column(field);
    }

    public String getColumnName() {
        JoinColumn joinColumn = field.getDeclaredAnnotation(JoinColumn.class);
        if (joinColumn != null) {
            return joinColumn.name();
        }
        jakarta.persistence.Column column = field.getDeclaredAnnotation(jakarta.persistence.Column.class);
        if (column == null || StringUtils.isNullOrEmpty(column.name())) {
            return convertCamelToSnakeString(field.getName());
        }

        return column.name();
    }

    public Class<?> getType() {
        return field.getType();
    }

    public Type getGenericType() {
        return field.getGenericType();
    }

    public boolean isIdAnnotation() {
        return field.isAnnotationPresent(Id.class);
    }

    public boolean isGeneratedValueAnnotation() {
        jakarta.persistence.GeneratedValue generatedValue = field.getDeclaredAnnotation(jakarta.persistence.GeneratedValue.class);

        return generatedValue != null && generatedValue.strategy() == GenerationType.IDENTITY;
    }

    public boolean isEagerRelationColumn() {
        if (field.isAnnotationPresent(OneToOne.class)) {
            return FetchType.EAGER.equals(field.getAnnotation(OneToOne.class).fetch());
        }
        if (field.isAnnotationPresent(OneToMany.class)) {
            return FetchType.EAGER.equals(field.getAnnotation(OneToMany.class).fetch());
        }
        if (field.isAnnotationPresent(ManyToOne.class)) {
            return FetchType.EAGER.equals(field.getAnnotation(ManyToOne.class).fetch());
        }
        if (field.isAnnotationPresent(ManyToMany.class)) {
            return FetchType.EAGER.equals(field.getAnnotation(ManyToMany.class).fetch());
        }
        return false;
    }

    public boolean isRelationColumn() {
        return field.isAnnotationPresent(OneToOne.class)
            || field.isAnnotationPresent(OneToMany.class)
            || field.isAnnotationPresent(ManyToOne.class)
            || field.isAnnotationPresent(ManyToMany.class);
    }

    public boolean isNullable() {
        jakarta.persistence.Column column = field.getDeclaredAnnotation(jakarta.persistence.Column.class);
        return column == null || column.nullable();
    }

    public Object getFieldValue(Object object) {
        try {
            field.setAccessible(true);
            return valueOf(field.get(object));
        } catch (IllegalAccessException e) {
            return "";
        }
    }

    public void setFieldValue(Object object, Object value) {
        try {
            field.setAccessible(true);
            if (Collection.class.isAssignableFrom(value.getClass())) {
                field.set(object, value);
                return;
            }

            if (Collection.class.isAssignableFrom(field.getType())) {
                ((Collection) getFieldValue(object)).add(value);
                return;
            }
            field.set(object, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isOneToMany() {
        return field.isAnnotationPresent(OneToMany.class) && field.isAnnotationPresent(JoinColumn.class);
    }

    public Table getRelationTable()  {
        Type genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericType;
            Class<?> relationClass = (Class<?>) parameterizedType.getActualTypeArguments()[0];
            return Table.getInstance(relationClass);
        }

        return Table.getInstance(field.getType());
    }

    private Object valueOf(Object object) {

        if (object instanceof String) {
            return String.format("'%s'", object);
        }
        return object;
    }

    private String convertCamelToSnakeString(String str) {
        Matcher matcher = CAMEL_CASE_FIELD_NAME_PATTERN.matcher(str);
        return matcher.replaceAll(matchResult -> String.format(
            SNAKE_CASE_FORMAT,
            matchResult.group(1).toLowerCase(),
            matchResult.group(2).toUpperCase()
        )).toLowerCase();
    }
}
