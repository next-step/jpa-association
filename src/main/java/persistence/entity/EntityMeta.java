package persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static persistence.sql.util.StringConstant.DELIMITER;

public class EntityMeta {
    private final Class parentClass;
    private final String tableName;
    private final List<Field> columnFields;
    private final Field pkField;
    private final Field fkField;
    private final Class childClass;
    private final EntityMeta childMeta;

    public EntityMeta(Class clazz) {
        this.parentClass = clazz;
        this.tableName = findTableName(clazz);
        this.pkField = findPkField(clazz);
        this.fkField = findFkField(clazz);
        this.columnFields = findColumnFields(clazz);
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

    private static List<Field> findColumnFields(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(
                        field -> !field.isAnnotationPresent(Transient.class)
                                && !field.isAnnotationPresent(OneToMany.class)
                ).collect(Collectors.toList());
    }

    private static String findColumnName(Field field) {
        final Column column = field.getDeclaredAnnotation(Column.class);
        return column == null || column.name().isBlank()
                ? field.getName()
                : column.name();
    }

    private static Field findPkField(Class clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findAny()
                .orElseThrow(() -> new EntityIdNotFoundException());
    }

    private static Field findFkField(Class clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(JoinColumn.class))
                .findAny().orElse(null);
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

    public Class getParentClass() {
        return parentClass;
    }

    public String getTableName() {
        return tableName;
    }

    public Map<String, Field> collectColumnFields() {
        return columnFields.stream().collect(Collectors.toMap(
                EntityMeta::findColumnName,
                Function.identity()
        ));
    }

    public Map<String, Field> collectColumnFields(String prefix) {
        return columnFields.stream().collect(Collectors.toMap(
                column -> formatAlias(prefix, findColumnName(column)),
                Function.identity()
        ));
    }

    public String joinColumnNames(String prefix) {
        return columnFields.stream()
                .map(field -> format(prefix, field))
                .collect(Collectors.joining(DELIMITER));
    }

    public String getPkName() {
        return findColumnName(pkField);
    }

    public Field getPkField() {
        return pkField;
    }

    public Field getFkField() {
        return fkField;
    }

    public String getFkName() {
        return fkField == null
                ? null
                : fkField.getDeclaredAnnotation(JoinColumn.class).name();
    }

    public void initOneToMany(Object obj) throws IllegalAccessException {
        if (!isOneToMany()) {
            return;
        }
        fkField.setAccessible(true);
        fkField.set(obj, new ArrayList<>());
    }

    public void addChild(Object parent, Object child) throws IllegalAccessException {
        if (!isOneToMany()) {
            return;
        }
        ((List) fkField.get(parent)).add(child);
    }

    public String getFKCondition(String parentPrefix, String childPrefix) {
        return new StringBuilder()
                .append(formatColumn(parentPrefix, getPkName()))
                .append(" = ")
                .append(formatColumn(childPrefix, getFkName()))
                .toString();
    }

    public Class getChildClass() {
        return childClass;
    }

    public EntityMeta getChildMeta() {
        return childMeta;
    }

    public boolean isEagerOneToMany() {
        return isOneToMany() && fkField.getDeclaredAnnotation(OneToMany.class)
                .fetch() == FetchType.EAGER;
    }

    public boolean isLazyOneToMany() {
        return isOneToMany() && fkField.getDeclaredAnnotation(OneToMany.class)
                .fetch() == FetchType.LAZY;
    }

    private boolean isOneToMany() {
        return fkField != null
                && childClass != null
                && childMeta != null
                && fkField.getDeclaredAnnotation(OneToMany.class) != null
                && fkField.getDeclaredAnnotation(JoinColumn.class) != null;
    }

    private String format(String prefix, Field field) {
        final String columnName = findColumnName(field);
        return String.format(
                "%s AS %s",
                formatColumn(prefix, columnName),
                formatAlias(prefix, columnName)
        );
    }

    private String formatColumn(String prefix, String columnName) {
        return String.format("%s.%s", prefix, columnName);
    }

    private String formatAlias(String prefix, String columnName) {
        return String.format("%s_%s", prefix, columnName);
    }
}
