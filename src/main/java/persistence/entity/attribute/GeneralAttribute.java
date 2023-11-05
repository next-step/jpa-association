package persistence.entity.attribute;

import java.lang.reflect.Field;

public interface GeneralAttribute {
    String getColumnName();

    String getFieldName();

    Field getField();
}
