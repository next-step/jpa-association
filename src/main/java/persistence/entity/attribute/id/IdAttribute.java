package persistence.entity.attribute.id;

import jakarta.persistence.GenerationType;
import persistence.entity.attribute.Attribute;

import java.lang.reflect.Field;

public interface IdAttribute extends Attribute {
    Field getField();

    String getColumnName();

    String getFieldName();

    GenerationType getGenerationType();
}
