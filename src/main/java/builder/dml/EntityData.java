package builder.dml;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import util.StringUtil;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EntityData {

    private static final String NOT_EXIST_ENTITY_ANNOTATION = "@Entity 어노테이션이 존재하지 않습니다.";

    private final Class<?> clazz;
    private final String tableName;
    private final String pkName;
    private Object id;
    private final EntityColumn entityColumn;
    private Object entityInstance;
    private final JoinEntity joinEntity;
    private final String alias;

    // Constructor
    private EntityData(Object entityInstance) {
        this.clazz = entityInstance.getClass();
        confirmEntityAnnotation(this.clazz);
        this.tableName = getTableName(this.clazz);
        this.entityColumn = new EntityColumn(entityInstance, this.clazz);
        this.id = this.entityColumn.getPkValue();
        this.pkName = this.entityColumn.getPkName();
        this.entityInstance = deepCopy(entityInstance);
        this.joinEntity = new JoinEntity(entityInstance, this.id);
        this.alias = QueryBuildUtil.getAlias(this.tableName);
    }

    private <T> EntityData(Class<T> clazz, Object id) {
        confirmEntityAnnotation(clazz);
        this.clazz = clazz;
        this.tableName = getTableName(clazz);
        this.entityColumn = new EntityColumn(clazz);
        this.id = id;
        this.pkName = this.entityColumn.getPkName();
        this.joinEntity = new JoinEntity(clazz, id);
        this.alias = QueryBuildUtil.getAlias(this.tableName);
    }

    private <T> EntityData(Class<T> clazz) {
        confirmEntityAnnotation(clazz);
        this.clazz = clazz;
        this.tableName = getTableName(clazz);
        this.entityColumn = new EntityColumn(clazz);
        this.pkName = this.entityColumn.getPkName();
        this.joinEntity = new JoinEntity(clazz, this.id);
        this.alias = QueryBuildUtil.getAlias(this.tableName);
    }

    // Static Factory Methods
    public static EntityData createEntityData(Object entityInstance) {
        return new EntityData(entityInstance);
    }

    public static <T> EntityData createEntityData(Class<T> clazz, Object id) {
        return new EntityData(clazz, id);
    }

    public static <T> EntityData createEntityData(Class<T> clazz) {
        return new EntityData(clazz);
    }

    public String getTableName() {
        return this.tableName;
    }

    public Object getId() {
        return id;
    }

    public String getPkNm() {
        return this.pkName;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public Object getEntityInstance() {
        return entityInstance;
    }

    public EntityColumn getEntityColumn() {
        return entityColumn;
    }

    public JoinEntity getJoinEntity() {
        return joinEntity;
    }

    public String wrapString() {
        return (this.id instanceof String) ? StringUtil.wrapSingleQuote(this.id) : String.valueOf(this.id);
    }

    public EntityData changeColumns(List<DMLColumnData> columns) {
        this.entityColumn.changeColumns(columns);
        return this;
    }

    public String getColumnDefinitions() {
        return this.entityColumn.getColumnDefinitions();
    }

    public String getAlias() {
        return alias;
    }

    public List<DMLColumnData> getDifferentColumns(EntityData snapshotEntityData) {
        return this.entityColumn.getDifferentColumns(snapshotEntityData);
    }

    public Map<String, DMLColumnData> convertDMLColumnDataMap() {
        return this.entityColumn.getColumns().stream()
                .collect(Collectors.toMap(DMLColumnData::getColumnName, Function.identity()));
    }

    public boolean checkJoin() {
        return this.joinEntity.checkJoin();
    }

    public boolean checkJoinAndEager() {
        return this.joinEntity.checkJoin() && this.joinEntity.checkFetchEager();
    }

    private void confirmEntityAnnotation(Class<?> entityClass) {
        if (!entityClass.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException(NOT_EXIST_ENTITY_ANNOTATION);
        }
    }

    private String getTableName(Class<?> entityClass) {
        if (entityClass.isAnnotationPresent(Table.class)) {
            Table table = entityClass.getAnnotation(Table.class);
            return table.name();
        }
        return entityClass.getSimpleName();
    }

    private Object deepCopy(Object original) {
        if (original == null) return null;

        try {
            Class<?> clazz = original.getClass();
            Object copy = clazz.getDeclaredConstructor().newInstance();

            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);

                Object value = field.get(original);
                field.set(copy, value);
            }
            return copy;
        } catch (Exception e) {
            throw new RuntimeException("Deep copy failed", e);
        }
    }

}
