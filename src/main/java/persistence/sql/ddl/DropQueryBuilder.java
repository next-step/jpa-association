package persistence.sql.ddl;

import persistence.CustomTable;

public abstract class DropQueryBuilder {

    public String createQueryBuild(Class<?> clazz) {
        CustomTable customTable = CustomTable.of(clazz);
        return String.format("drop table %s", customTable.name());
    }
}
