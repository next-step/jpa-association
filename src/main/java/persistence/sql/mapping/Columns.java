package persistence.sql.mapping;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import persistence.sql.ddl.exception.AnnotationMissingException;
import persistence.sql.ddl.exception.IdAnnotationMissingException;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Columns implements Iterable<ColumnData> {

    private final List<ColumnData> columns;
    private final List<OneToManyData> associations;

    private Columns(List<ColumnData> columns, List<OneToManyData> associations) {
        this.columns = columns;
        this.associations = associations;
    }

    @Override
    public Iterator<ColumnData> iterator() {
        return columns.iterator();
    }

    public static Columns createColumns(Class<?> clazz) {
        checkIsEntity(clazz);
        TableData table = TableData.from(clazz);
        List<ColumnData> columns = Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> !field.isAnnotationPresent(Transient.class))
                .filter(field -> !field.isAnnotationPresent(OneToMany.class))
                .map(field -> ColumnData.createColumn(table.getName(), field))
                .collect(Collectors.toList());

        checkHasPrimaryKey(columns);
        return new Columns(columns, extractAssociations(clazz));
    }

    public static Columns createColumnsWithValue(Object entity) {
        Class<?> clazz = entity.getClass();
        checkIsEntity(clazz);
        TableData table = TableData.from(clazz);

        List<ColumnData> columns = Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> !field.isAnnotationPresent(Transient.class))
                .filter(field -> !field.isAnnotationPresent(OneToMany.class))
                .map(field -> ColumnData.createColumnWithValue(table.getName(), field, entity))
                .collect(Collectors.toList());

        checkHasPrimaryKey(columns);
        return new Columns(columns, extractAssociations(clazz));
    }

    public List<String> getNames() {
        return columns.stream()
                .filter(ColumnData::isNotPrimaryKey)
                .map(ColumnData::getName)
                .collect(Collectors.toList());
    }

    public List<String> getNamesWithTableName() {
        return columns.stream()
                .filter(ColumnData::isNotPrimaryKey)
                .map(ColumnData::getNameWithTable)
                .collect(Collectors.toList());
    }

    public List<Object> getValues() {
        return columns.stream()
                .filter(ColumnData::isNotPrimaryKey)
                .map(ColumnData::getValue)
                .collect(Collectors.toList());
    }

    public Map<String, Object> getValuesMap() {
        return columns.stream()
                .filter(ColumnData::isNotPrimaryKey)
                .collect(Collectors.toMap(ColumnData::getName, ColumnData::getValue));
    }

    public ColumnData getPkColumn() {
        return columns.stream()
                .filter(ColumnData::isPrimaryKey)
                .findFirst()
                .orElseThrow(IdAnnotationMissingException::new);
    }

    public String getPkColumnName() {
        return getPkColumn().getNameWithTable();
    }

    private static void checkIsEntity(Class<?> entityClazz) {
        if (!entityClazz.isAnnotationPresent(Entity.class)) {
            throw new AnnotationMissingException("Entity 어노테이션이 없습니다.");
        }
    }

    private static void checkHasPrimaryKey(List<ColumnData> columns) {
        if (columns.stream().noneMatch(ColumnData::isPrimaryKey)) {
            throw new IdAnnotationMissingException();
        }
    }

    private static List<OneToManyData> extractAssociations(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(OneToMany.class))
                .map(OneToManyData::from)
                .collect(Collectors.toList());
    }

    public List<OneToManyData> getEagerAssociations() {
        return associations.stream()
                .filter(OneToManyData::isEagerLoad)
                .collect(Collectors.toList());
    }

    public boolean hasAssociation() {
        return !associations.isEmpty();
    }
}
