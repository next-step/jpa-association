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
    private final Object entity;
    private final String entityName;
    private final List<EntityColumn> entityColumns;
    private final EntityJoinMetaData entityJoinMetaData; //OrderItem

    public EntityMetaData(Class<?> clazz, Object entity) {
        if (!clazz.isAnnotationPresent(Entity.class)) {
            throw new IllegalStateException("Entity 클래스가 아닙니다.");
        }
        this.clazz = clazz;
        this.entity = entity;
        this.entityName = getEntityNameInfo();
        this.entityColumns = getEntityColumnsInfo();
        this.entityJoinMetaData = getEntityJoinMetaDataInfo();
    }

    public String getEntityName() {
        return entityName;
    }

    public List<EntityColumn> getEntityColumns() {
        return entityColumns;
    }

    public EntityJoinMetaData getEntityJoinMetaData() {
        return entityJoinMetaData;
    }

    private String getEntityNameInfo() {
        return clazz.isAnnotationPresent(Table.class) && !isBlankOrEmpty(clazz.getAnnotation(Table.class).name())
                ? clazz.getAnnotation(Table.class).name() : clazz.getSimpleName().toLowerCase();
    }

    private List<EntityColumn> getEntityColumnsInfo() {
        return new FieldInfos(clazz.getDeclaredFields()).getIdAndColumnFields().stream()
                .map(field -> new EntityColumn(field, entity))
                .collect(Collectors.toList());
    }

    private EntityJoinMetaData getEntityJoinMetaDataInfo() {
        Optional<Field> joinColumnField = new FieldInfos(clazz.getDeclaredFields()).getJoinColumnField();
        if (joinColumnField.isEmpty()) {
            return null;
        }

        Class<?> joinClass = (Class<?>) ((ParameterizedType) joinColumnField.get().getGenericType()).getActualTypeArguments()[0];
        return new EntityJoinMetaData(joinClass, null, joinColumnField.get());
    }
}
