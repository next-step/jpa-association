package persistence.meta;

import java.lang.reflect.Field;

public class EntityColumn extends AbstractColumn {
    public EntityColumn(Field field) {
        super(field);
    }
}
