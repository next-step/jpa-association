package persistence.sql.dialect;

import jakarta.persistence.GenerationType;

public interface Dialect {
    String mapDataType(int type);
    String mapGenerationType(GenerationType strategy);
    String getGeneratedIdSelectQuery();
}
