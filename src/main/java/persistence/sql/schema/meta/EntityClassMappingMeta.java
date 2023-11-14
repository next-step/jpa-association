package persistence.sql.schema.meta;

import jakarta.persistence.Entity;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import persistence.sql.dialect.ColumnType;
import persistence.sql.exception.ClassMappingException;
import persistence.sql.schema.constraint.PrimaryKeyConstraint;

public class EntityClassMappingMeta {

    private static final String COMMA = ",";
    private static final String SPACE = " ";

    private final TableMeta tableMeta;

    private final Map<Field, ColumnMeta> columnMetaMap = new LinkedHashMap<>();

    private EntityClassMappingMeta(TableMeta tableMeta, Map<Field, ColumnMeta> columnMetaMap) {
        this.tableMeta = tableMeta;
        this.columnMetaMap.putAll(columnMetaMap);
    }

    public static EntityClassMappingMeta of(Class<?> entityClazz, ColumnType columnType) {
        validateEntityAnnotationIsPresent(entityClazz);
        validateHasIdAnnotation(entityClazz);

        return new EntityClassMappingMeta(TableMeta.of(entityClazz), getColumnMetasFromEntity(entityClazz, columnType));
    }

    public TableMeta getTableMeta() {
        return tableMeta;
    }

    public String tableClause() {
        return this.tableMeta.getTableName();
    }

    public String fieldClause() {
        final List<Field> fieldList = getMappingFieldList();

        return fieldList.stream()
            .map(targetField -> columnMetaMap.get(targetField).getColumn())
            .collect(Collectors.joining(COMMA + SPACE));
    }

    public List<Field> getMappingFieldList() {
        return columnMetaMap.keySet().stream()
            .filter(ColumnMeta::isNotTransient)
            .filter(ColumnMeta::isNotCollection)
            .collect(Collectors.toList());
    }

    public boolean hasRelation() {
        return columnMetaMap.values().stream().anyMatch(ColumnMeta::hasRelation);
    }

    public boolean hasNoRelation() {
        return !hasRelation();
    }

    public List<ColumnMeta> getRelationColumnMetaList() {
        return columnMetaMap.values().stream()
            .filter(ColumnMeta::hasRelation)
            .collect(Collectors.toList());
    }

    public List<ColumnMeta> getMappingColumnMetaList() {
        return new ArrayList<>(columnMetaMap.values());
    }

    public ColumnMeta getColumnMeta(Field field) {
        return columnMetaMap.get(field);
    }

    public ColumnMeta getIdColumnMeta() {
        return columnMetaMap.entrySet().stream()
            .filter(entry -> PrimaryKeyConstraint.isPrimaryKey(entry.getKey()))
            .map(Entry::getValue)
            .findAny()
            .orElseThrow(() -> ClassMappingException.requiredAnnotation(getTableMeta().getType(), "@Id"));
    }

    public String getIdFieldColumnName() {
        return getIdColumnMeta().getColumnName();
    }

    public String getMappingColumnName(Field field) {
        return columnMetaMap.get(field).getColumnName();
    }

    public Constructor<?> getDefaultConstructor() {
        return Arrays.stream(tableMeta.getType().getDeclaredConstructors())
            .filter(constructor -> constructor.getParameterCount() == 0)
            .findAny()
            .orElseThrow(ClassMappingException::defaultConstructorRequired);
    }

    private static Map<Field, ColumnMeta> getColumnMetasFromEntity(Class<?> entityClazz, ColumnType columnType) {
        return Arrays.stream(entityClazz.getDeclaredFields())
            .collect(
                Collectors.toMap(
                    field -> field,
                    field -> ColumnMeta.of(field, columnType),
                    (v1, v2) -> v2,
                    LinkedHashMap::new
                )
            );
    }

    private static void validateEntityAnnotationIsPresent(Class<?> entityClazz) {
        if (entityClazz.isAnnotationPresent(Entity.class) == Boolean.FALSE) {
            throw ClassMappingException.requiredAnnotation(entityClazz, "@Entity");
        }
    }

    private static void validateHasIdAnnotation(Class<?> entityClazz) {
        final boolean hasIdAnnotation = Arrays.stream(entityClazz.getDeclaredFields())
            .anyMatch(PrimaryKeyConstraint::isPrimaryKey);

        if (!hasIdAnnotation) {
            throw ClassMappingException.requiredAnnotation(entityClazz, "@Id");
        }
    }
}
