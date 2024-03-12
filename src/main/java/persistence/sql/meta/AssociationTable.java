package persistence.sql.meta;

import java.lang.reflect.Field;
import java.util.List;

public interface AssociationTable {
    List<Column> getColumns();

    String getName();

    String getJoinColumn();

    boolean isEager();

    boolean isLazy();

    Class<?> getClazz();

    Field getField();
}
