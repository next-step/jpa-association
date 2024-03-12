package persistence.entity;

import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import persistence.sql.column.JoinEntityColumn;
import utils.CamelToSnakeCaseConverter;

import java.lang.reflect.Field;

public class OneToManyAssociationEntity implements AssociationEntity {
    private final JoinEntityColumn joinColumn;
    private final FetchType fetchType;
    private final Field field;


    public OneToManyAssociationEntity(Field field) {
        validateAnnotation(field);
        this.joinColumn = new JoinEntityColumn(field);
        this.fetchType = field.getDeclaredAnnotation(OneToMany.class).fetch();
        this.field = field;
    }

    private void validateAnnotation(Field field) {
        if(!field.isAnnotationPresent(OneToMany.class)){
            throw new IllegalArgumentException("[INFO] No @OneToMany annotation");
        }
    }

    @Override
    public String getJoinFieldName() {
        return joinColumn.getFieldName();
    }

    @Override
    public boolean isLazy() {
        return FetchType.LAZY.equals(fetchType);
    }

    @Override
    public <T> void setAssociationColumn(T entity, Object value) {
        field.setAccessible(true);
        try {
            field.set(entity, value);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("[INFO] Cannot set association column");
        } finally {
            field.setAccessible(false);
        }
    }

    @Override
    public JoinEntityColumn getJoinEntityColumn() {
        return joinColumn;
    }

}
