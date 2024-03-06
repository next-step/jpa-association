package persistence.sql.meta;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import persistence.exception.NotEntityException;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Table {
    private final static Map<Class<?>, Table> CACHE = new ConcurrentHashMap<>();

    private final Class<?> clazz;
    private final List<Column> columns;
    private final AssociationTables associationTables;

    public Table(Class<?> clazz, List<Column> columns, AssociationTables  associationTables) {
        this.clazz = clazz;
        this.columns = columns;
        this.associationTables = associationTables;
    }

    public static Table from(Class<?> target) {
        checkIsEntity(target);
        if (CACHE.containsKey(target)) {
            return CACHE.get(target);
        }
        List<Column> columns = getColumns(target);
        AssociationTables associationTables = new AssociationTables(getAssociationTables(target));
        return new Table(target, columns, associationTables);
    }

    private static void checkIsEntity(Class<?> target) {
        if (!target.isAnnotationPresent(Entity.class)) {
            throw new NotEntityException();
        }
    }

    private static List<Column> getColumns(Class<?> target) {
        return getTargetFields(target).stream()
                .map(Column::from)
                .collect(Collectors.toList());
    }

    private static List<Field> getTargetFields(Class<?> target) {
        return Arrays.stream(target.getDeclaredFields())
                .filter(field -> !field.isAnnotationPresent(Transient.class))
                .filter(field -> !field.isAnnotationPresent(JoinColumn.class))
                .collect(Collectors.toList());
    }

    private static List<AssociationTable> getAssociationTables(Class<?> target) {
        List<Field> oneToManyFields = getOneToManyFields(target);
        return createJoinTables(oneToManyFields);
    }

    private static List<AssociationTable> createJoinTables(List<Field> oneToManyFields) {
        return oneToManyFields.stream()
                .map(field -> new OneToManyTable(Table.from(getGenericTypeOfCollection(field)), getJoinColumnName(field), getFetchType(field)))
                .collect(Collectors.toList());
    }

    private static Class<?> getGenericTypeOfCollection(Field field) {
        return (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
    }

    private static String getJoinColumnName(Field field) {
        return field.getAnnotation(JoinColumn.class).name();
    }

    private static FetchType getFetchType(Field field) {
        return field.getAnnotation(OneToMany.class).fetch();
    }

    private static List<Field> getOneToManyFields(Class<?> target) {
        return Arrays.stream(target.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(OneToMany.class))
                .collect(Collectors.toList());
    }

    public String getName() {
        return Optional.ofNullable(clazz.getAnnotation(jakarta.persistence.Table.class))
                .map(jakarta.persistence.Table::name)
                .filter(name -> !name.isBlank())
                .orElse(clazz.getSimpleName());
    }

    public List<Column> getColumns() {
        return columns;
    }

    public IdColumn getIdColumn() {
        return (IdColumn) columns.stream()
                .filter(Column::isId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Id not found"));
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public boolean containsAssociation() {
        return !associationTables.isEmpty();
    }

    public List<AssociationTable> getAssociationTables() {
        return associationTables.getTables();
    }
}
