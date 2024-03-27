package persistence.model;

import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;

import java.lang.reflect.Field;

public class EntityOneToManyFieldMapping extends EntityJoinFieldMapping {

    public EntityOneToManyFieldMapping() {
        this.annotationClass = OneToMany.class;
    }

    @Override
    protected FetchType getFetchType(final Field field) {
        return field.getDeclaredAnnotation(OneToMany.class).fetch();
    }
}
