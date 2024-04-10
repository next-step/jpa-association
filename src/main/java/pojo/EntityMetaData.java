package pojo;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static utils.StringUtils.isBlankOrEmpty;

//Order
public class EntityMetaData {

    private final Class<?> clazz;
    private final String entityName;
    private final FieldInfos fieldInfos;

    public EntityMetaData(Class<?> clazz) {
        if (!clazz.isAnnotationPresent(Entity.class)) {
            throw new IllegalStateException("Entity 클래스가 아닙니다.");
        }
        this.clazz = clazz;
        this.entityName = getEntityNameInfo();
        this.fieldInfos = new FieldInfos(clazz.getDeclaredFields());
    }

    public String getEntityName() {
        return entityName;
    }

    public FieldInfos getFieldInfos() {
        return fieldInfos;
    }

    private String getEntityNameInfo() {
        if (clazz.isAnnotationPresent(Table.class) && !isBlankOrEmpty(clazz.getAnnotation(Table.class).name())) {
            return clazz.getAnnotation(Table.class).name();
        }

        return clazz.getSimpleName().toLowerCase();
    }

    public List<EntityColumn> createEntityColumnsInfo(Object entity) {
        return fieldInfos.getIdAndColumnFields().stream()
                .map(field -> new EntityColumn(field, entity))
                .collect(Collectors.toList());
    }

    public EntityJoinMetaData createEntityJoinMetaDataInfo() {
        Optional<Field> joinColumnField = fieldInfos.getJoinColumnField();
        if (joinColumnField.isEmpty()) {
            return null;
        }

        Class<?> joinClass = (Class<?>) ((ParameterizedType) joinColumnField.get().getGenericType()).getActualTypeArguments()[0];
        return new EntityJoinMetaData(joinClass, joinColumnField.get());
    }
}
