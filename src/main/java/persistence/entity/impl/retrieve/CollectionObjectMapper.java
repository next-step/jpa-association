package persistence.entity.impl.retrieve;

import java.lang.reflect.Field;
import persistence.sql.exception.ClassMappingException;
import persistence.sql.schema.meta.ColumnMeta;

public class CollectionObjectMapper {

    public void mappingFieldRelation(Object instance, ColumnMeta columnMeta, Object loadedRelation) {
        try {
            final Field field = instance.getClass().getDeclaredField(columnMeta.getFieldName());
            field.setAccessible(true);
            field.set(instance, loadedRelation);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw ClassMappingException.mappingFail(columnMeta.getFieldName());
        }
    }
}
