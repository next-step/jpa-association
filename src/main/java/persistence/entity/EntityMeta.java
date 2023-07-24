package persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import persistence.exception.ChildClassNotFoundException;
import persistence.sql.exception.EntityIdNotFoundException;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EntityMeta {
    private final String tableName;
    private final List<String> columnNames;
    private final String pkName;
    private final String fkName;
    private final Class childClass;
    private final EntityMeta childMeta;

    public EntityMeta(Class clazz) {
        this.tableName = findTableName(clazz);
        this.pkName = findPkName(clazz);
        this.fkName = findFkName(clazz);
        this.columnNames = findColumnNames(clazz);
        this.childClass = findChildClass(clazz);
        this.childMeta = childClass == null
                ? null : new EntityMeta(childClass);
    }

    private static <T> String findTableName(Class<T> clazz) {
        Table table = clazz.getDeclaredAnnotation(Table.class);
        return table == null || table.name().isBlank()
                ? clazz.getSimpleName()
                : table.name();
    }

    private static List<String> findColumnNames(Class clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(
                        field -> !field.isAnnotationPresent(Transient.class)
                                && !field.isAnnotationPresent(OneToMany.class)
                ).map(EntityMeta::findColumnName)
                .collect(Collectors.toList());
    }

    private static String findColumnName(Field field) {
        final Column column = field.getDeclaredAnnotation(Column.class);
        return column == null || column.name().isBlank()
                ? field.getName()
                : column.name();
    }

    private static String findPkName(Class clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findAny()
                .map(EntityMeta::findColumnName)
                .orElseThrow(() -> new EntityIdNotFoundException());
    }

    private static String findFkName(Class clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(JoinColumn.class))
                .findAny()
                .map(field -> field.getDeclaredAnnotation(JoinColumn.class).name())
                .orElse(null);
    }

    private static Class findChildClass(Class clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(OneToMany.class))
                .findAny()
                .map(EntityMeta::findGenericType)
                .orElse(null);
    }

    private static Class findGenericType(Field childField) {
        Type type = childField.getGenericType();
        if (!(type instanceof ParameterizedType)) {
            return null;
        }
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Type[] typeArguments = parameterizedType.getActualTypeArguments();
        if (typeArguments == null || typeArguments.length == 0) {
            return null;
        }
        try {
            return Class.forName(typeArguments[0].getTypeName());
        } catch (ClassNotFoundException e) {
            throw new ChildClassNotFoundException(e);
        }
    }

    public String getTableName() {
        return tableName;
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public String joinColumnNames(String prefix) {
        return getColumnNames().stream().map(
                columnName -> String.format("%s.%s", prefix, columnName)
        ).collect(Collectors.joining(", "));
    }

    public String getPkName() {
        return pkName;
    }

    public String getFkName() {
        return fkName;
    }

    public String getFKCondition(String parentPrefix, String childPrefix) {
        return new StringBuilder()
                .append(parentPrefix)
                .append(".")
                .append(pkName)
                .append(" = ")
                .append(childPrefix)
                .append(".")
                .append(fkName)
                .toString();
    }

    public Class getChildClass() {
        return childClass;
    }

    public EntityMeta getChildMeta() {
        return childMeta;
    }
}
