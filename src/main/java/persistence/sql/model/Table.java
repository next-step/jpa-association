package persistence.sql.model;

import jakarta.persistence.Id;
import util.CaseConverter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Table implements BaseTable {

    private final Class<?> entity;

    private final String name;

    private final PKColumn pkColumn;

    private final Columns columns;

    private final List<JoinColumn> joinColumns;

    public Table(Class<?> entity) {
        validateEntity(entity);

        this.entity = entity;
        this.name = buildTableName(entity);
        this.pkColumn = buildPKColumn(entity);
        this.columns = buildColumns(entity);
        this.joinColumns = buildJoinColumns(entity);
    }

    private void validateEntity(Class<?> entity) {
        if (!entity.isAnnotationPresent(jakarta.persistence.Entity.class)) {
            throw new IllegalArgumentException("This class is not an entity: " + entity.getSimpleName());
        }
    }

    private String buildTableName(Class<?> entity) {
        jakarta.persistence.Table table = entity.getAnnotation(jakarta.persistence.Table.class);

        if (entity.isAnnotationPresent(jakarta.persistence.Table.class) && hasName(table)) {
            return table.name();
        }

        String className = entity.getSimpleName();
        return CaseConverter.pascalToSnake(className);
    }

    private boolean hasName(jakarta.persistence.Table table) {
        String name = table.name();
        return !name.isEmpty();
    }

    private PKColumn buildPKColumn(Class<?> entity) {
        Field[] fields = entity.getDeclaredFields();
        Field pkField = Arrays.stream(fields)
                .filter(this::hasIdAnnotation)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("The entity does not contain a PK field.: " + entity.getSimpleName()));

        return new PKColumn(pkField);
    }

    private Columns buildColumns(Class<?> entity) {
        Field[] fields = entity.getDeclaredFields();
        List<Field> columnFields = Arrays.stream(fields)
                .filter(field -> !hasIdAnnotation(field) && !hasJoinColumnAnnotation(field))
                .collect(Collectors.toList());
        return new Columns(columnFields);
    }

    private boolean hasIdAnnotation(Field field) {
        return field.isAnnotationPresent(Id.class);
    }

    private List<JoinColumn> buildJoinColumns(Class<?> entity) {
        Field[] fields = entity.getDeclaredFields();
        return Arrays.stream(fields)
                .filter(this::hasJoinColumnAnnotation)
                .map(JoinColumn::new)
                .collect(Collectors.toList());
    }

    private boolean hasJoinColumnAnnotation(Field field) {
        return field.isAnnotationPresent(jakarta.persistence.JoinColumn.class);
    }

    public List<JoinColumn> getJoinColumns() {
        return Collections.unmodifiableList(joinColumns);
    }

    public boolean hasJoinColumn() {
        return !joinColumns.isEmpty();
    }

    @Override
    public Class<?> getEntity() {
        return entity;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public PKColumn getPKColumn() {
        return pkColumn;
    }

    @Override
    public Columns getColumns() {
        return columns;
    }

    @Override
    public String getPKColumnName() {
        return pkColumn.getName();
    }

    @Override
    public List<String> getAllColumnNames() {
        List<String> allColumnNames = new ArrayList<>();

        String pkColumnName = pkColumn.getName();
        allColumnNames.add(pkColumnName);

        List<String> columnNames = columns.getColumnNames();
        allColumnNames.addAll(columnNames);

        return Collections.unmodifiableList(allColumnNames);
    }
}
