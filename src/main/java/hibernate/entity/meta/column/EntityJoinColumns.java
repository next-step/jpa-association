package hibernate.entity.meta.column;

import hibernate.entity.meta.EntityClass;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class EntityJoinColumns {

    private static Map<EntityClass<?>, EntityJoinColumns> CACHE = new ConcurrentHashMap<>();

    private final List<EntityJoinColumn> values;

    private EntityJoinColumns(final List<EntityJoinColumn> values) {
        this.values = values;
    }

    public static EntityJoinColumns oneToManyColumns(final EntityClass<?> clazz) {
        return CACHE.computeIfAbsent(clazz, EntityJoinColumns::generateOneToManyColumns);
    }

    private static EntityJoinColumns generateOneToManyColumns(final EntityClass<?> entityClass) {
        Field[] fields = entityClass.getFields();
        return new EntityJoinColumns(
                Arrays.stream(fields)
                        .filter(field -> field.isAnnotationPresent(OneToMany.class))
                        .map(EntityOneToManyColumn::new)
                        .collect(Collectors.toList())
        );
    }

    public List<EntityJoinColumn> getEagerValues() {
        return values.stream()
                .filter(value -> value.getFetchType() == FetchType.EAGER)
                .collect(Collectors.toList());
    }

    public List<EntityJoinColumn> getLazyValues() {
        return values.stream()
                .filter(value -> value.getFetchType() == FetchType.LAZY)
                .collect(Collectors.toList());
    }

    public Map<String, List<String>> getEagerJoinTableFields() {
        return getEagerValues()
                .stream()
                .map(EntityJoinColumn::getEntityClass)
                .collect(Collectors.toMap(
                        EntityClass::tableName,
                        EntityClass::getFieldNames
                ));
    }

    public Map<String, Object> getEagerJoinTableIds() {
        return getEagerValues()
                .stream()
                .collect(Collectors.toMap(
                        entityJoinColumn -> entityJoinColumn.getEntityClass().tableName(),
                        EntityJoinColumn::getJoinColumnName
                ));
    }
}
