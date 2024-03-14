package persistence.sql.meta;

import jakarta.persistence.FetchType;

import java.lang.reflect.Field;
import java.util.List;

public class OneToManyTable implements AssociationTable {
    private final Table table;
    private final String joinColumnName;
    private final FetchType fetchType;
    private final Field field;

    public OneToManyTable(Table table, String joinColumnName, FetchType fetchType, Field field) {
        this.table = table;
        this.joinColumnName = joinColumnName;
        this.fetchType = fetchType;
        this.field = field;
    }

    @Override
    public List<Column> getColumns() {
        return table.getColumns();
    }

    @Override
    public String getName() {
        return table.getName();
    }

    @Override
    public String getJoinColumn() {
        return joinColumnName;
    }

    @Override
    public boolean isEager() {
        return fetchType.equals(FetchType.EAGER);
    }

    @Override
    public boolean isLazy() {
        return fetchType.equals(FetchType.LAZY);
    }

    @Override
    public Class<?> getClazz() {
        return table.getClazz();
    }

    @Override
    public Field getField() {
        return field;
    }
}
