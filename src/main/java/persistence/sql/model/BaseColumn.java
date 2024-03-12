package persistence.sql.model;

import java.lang.reflect.Field;

public interface BaseColumn {

    Field getField();

    String getName();
}
