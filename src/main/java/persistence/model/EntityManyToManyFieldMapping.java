package persistence.model;

import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;

import java.lang.reflect.Field;

public class EntityManyToManyFieldMapping extends EntityJoinFieldMapping {

    public EntityManyToManyFieldMapping() {
        this.annotationClass = ManyToMany.class;
    }

    @Override
    protected FetchType getFetchType(final Field field) {
        return field.getDeclaredAnnotation(ManyToMany.class).fetch();
    }
}
