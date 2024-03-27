package persistence.model;

import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

import java.lang.reflect.Field;

public class EntityManyToOneFieldMapping extends EntityJoinFieldMapping {

    public EntityManyToOneFieldMapping() {
        this.annotationClass = ManyToOne.class;
    }

    @Override
    protected FetchType getFetchType(final Field field) {
        return field.getDeclaredAnnotation(ManyToOne.class).fetch();
    }
}
