package pojo;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ex) Order
 */
public class EntityJoinMetaData implements EntityClass {

    private final EntityMetaData owner; //orderItem
    private final Class<?> clazz;
    private final Object entity;
    private final String entityName;
    private final List<EntityColumn> entityColumns;
    private final boolean lazy;

    public EntityJoinMetaData(EntityMetaData owner, Class<?> clazz, Object entity) {
        if (!clazz.isAnnotationPresent(Entity.class)) {
            throw new IllegalStateException("Entity 클래스가 아닙니다.");
        }
        this.owner = owner;
        this.clazz = clazz;
        this.entity = entity;
        this.entityName = getEntityNameInfo();
        this.entityColumns = getEntityColumnsInfo();
        this.lazy = isLazy();
    }

    public EntityMetaData getOwner() {
        return owner;
    }

    @Override
    public String getEntityName() {
        return entityName;
    }

    @Override
    public List<EntityColumn> getEntityColumns() {
        return entityColumns;
    }

    public String joinColumnName() {
        return new FieldInfos(clazz.getDeclaredFields()).getJoinColumnField().getAnnotation(JoinColumn.class).name();
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

    private boolean isLazy() {
        Field joinColumnField = new FieldInfos(clazz.getDeclaredFields()).getJoinColumnField();

        //일단 OneToMany 만 고려
        FetchType fetchType = joinColumnField.getAnnotation(OneToMany.class).fetch();
        return !fetchType.equals(FetchType.EAGER);
    }
}
