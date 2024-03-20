package pojo;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.List;
import java.util.stream.Collectors;

public class EntityMetaData implements EntityClass {

    private final Class<?> clazz;
    private final Object entity;
    private final String entityName;
    private final List<EntityColumn> entityColumns;

    public EntityMetaData(Class<?> clazz, Object entity) {
        if (!clazz.isAnnotationPresent(Entity.class)) {
            throw new IllegalStateException("Entity 클래스가 아닙니다.");
        }
        this.clazz = clazz;
        this.entity = entity;
        this.entityName = getEntityNameInfo();
        this.entityColumns = getEntityColumnsInfo();
    }

    @Override
    public String getEntityName() {
        return entityName;
    }

    @Override
    public List<EntityColumn> getEntityColumns() {
        return entityColumns;
    }

    private String getEntityNameInfo() {
        return clazz.isAnnotationPresent(Table.class) ? clazz.getAnnotation(Table.class).name()
                : clazz.getSimpleName().toLowerCase();
    }

    private List<EntityColumn> getEntityColumnsInfo() {
        return new FieldInfos(clazz.getDeclaredFields()).getIdAndColumnFields().stream()
                .map(field -> new EntityColumn(field, entity))
                .collect(Collectors.toList());
    }
}
