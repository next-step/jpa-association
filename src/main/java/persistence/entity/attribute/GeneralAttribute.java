package persistence.entity.attribute;

import java.lang.reflect.Field;

public interface GeneralAttribute extends Attribute {
    String getColumnName();

    String getFieldName();

    Field getField();
}
