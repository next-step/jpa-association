package persistence.meta;

import java.lang.reflect.Field;

public class ForeignerColumn extends AbstractColumn {
    private final EntityMeta referenceEntityMeta;
    private ForeignerColumn(Class<?> referenceClass, Field onePkField, String name) {
        super(onePkField);
        this.referenceEntityMeta = EntityMeta.from(referenceClass);
        this.name = name;

    }
    public static ForeignerColumn of(Class<?> referenceClass, Field onePkField, String name) {
        return new ForeignerColumn(referenceClass, onePkField, name);
    }
}
