package pojo;

import jakarta.persistence.GeneratedValue;

import java.lang.reflect.Field;
import java.util.Objects;

public class IdField implements FieldData {

    private final EntityColumn entityColumn;
    private final H2GenerationType generationType;

    public IdField(Field field, Object entity) {
        this.entityColumn = new EntityColumn(field, entity);
        this.generationType = getGenerationType();
    }

    public EntityColumn getEntityColumn() {
        return entityColumn;
    }

    public String getGenerationTypeStrategy() {
        return Objects.nonNull(generationType) ? generationType.getStrategy() : null;
    }

    public boolean isGenerationTypeAutoOrIdentity() {
        return Objects.nonNull(generationType) && (generationType.equals(H2GenerationType.AUTO)
                || generationType.equals(H2GenerationType.IDENTITY));
    }

    private H2GenerationType getGenerationType() {
        if (entityColumn.hasGenerationType()) {
            return H2GenerationType.from(entityColumn.getField().getAnnotation(GeneratedValue.class).strategy());
        }
        return null;
    }

    @Override
    public boolean isIdField() {
        return true;
    }

    @Override
    public boolean isColumnField() {
        return false;
    }

    @Override
    public boolean isNotTransientField() {
        return true;
    }

    @Override
    public boolean isNullableField() {
        return false;
    }

    @Override
    public String getFieldNameData() {
        return entityColumn.getFieldName().getName();
    }

    @Override
    public Object getFieldValueData() {
        return entityColumn.getFieldValue().getValue();
    }
}
