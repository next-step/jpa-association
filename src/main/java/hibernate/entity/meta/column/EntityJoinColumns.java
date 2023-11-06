package hibernate.entity.meta.column;

import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EntityJoinColumns {

    private final List<EntityJoinColumn> values;

    private EntityJoinColumns(final List<EntityJoinColumn> values) {
        this.values = values;
    }

    public static EntityJoinColumns oneToManyColumns(final Field[] fields) {
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
}
