package persistence.dialect.h2;

import java.lang.reflect.Field;
import java.sql.Types;
import java.util.Collection;
import org.h2.value.DataType;
import org.h2.value.Value;
import persistence.dialect.Dialect;

public class H2Dialect extends Dialect {

  public H2Dialect() {
    this.registerColumnType(String.class, Types.VARCHAR);
    this.registerColumnType(Integer.class, Types.INTEGER);
    this.registerColumnType(Long.class, Types.BIGINT);
  }

  public String convertToColumn(Field field) {
    if (Collection.class.isAssignableFrom(field.getType())) {
      return field.getType().getName();
    }
    Integer type = getJavaSqlType(field);
    return Value.getTypeName(DataType.convertSQLTypeToValueType(type));
  }
}
