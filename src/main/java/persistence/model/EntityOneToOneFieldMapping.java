package persistence.model;

import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;

import java.lang.reflect.Field;

public class EntityOneToOneFieldMapping extends EntityOneToXFieldMapping {

    public EntityOneToOneFieldMapping() {
        this.annotationClass = OneToOne.class;
    }

    @Override
    protected FetchType getFetchType(final Field field) {
        return field.getDeclaredAnnotation(OneToOne.class).fetch();
    }
}
