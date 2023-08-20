package persistence.sql.ddl.column;

import jakarta.persistence.Column;
import persistence.entity.model.EntityColumn;
import persistence.entity.model.EntityMeta;
import persistence.sql.ddl.column.option.ColumnOptionStrategy;
import persistence.sql.ddl.column.option.IdOptionStrategy;
import persistence.sql.ddl.column.option.NoneOptionStrategy;
import persistence.sql.ddl.column.option.OptionStrategy;
import persistence.sql.ddl.column.type.Dialect;
import persistence.sql.ddl.column.type.H2Dialect;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DdlColumn {
    private static final String LENGTH_FORMAT = "%s(%d)";
    private static final Integer DEFAULT_LENGTH = 255;

    private final EntityColumn entityColumn;
    private final Dialect dialect;
    private final List<OptionStrategy> optionStrategies;

    public DdlColumn(EntityColumn entityColumn, Dialect dialect, List<OptionStrategy> optionStrategies) {
        this.entityColumn = entityColumn;
        this.dialect = dialect;
        this.optionStrategies = optionStrategies;
    }

    public static DdlColumn of(EntityColumn entityColumn) {
        return new DdlColumn(
                entityColumn,
                new H2Dialect(),
                Arrays.asList(new ColumnOptionStrategy(), new IdOptionStrategy())
        );
    }

    public static List<DdlColumn> ofList(EntityMeta entityMeta) {
        List<EntityColumn> ddlColumns = new ArrayList<>();
        ddlColumns.add(entityMeta.getIdColumn());
        ddlColumns.addAll(entityMeta.getNormalColumns().getEntityColumns());

        return ddlColumns.stream()
                .map(DdlColumn::of)
                .collect(Collectors.toList());
    }

    public String name() {
        return entityColumn.getName();
    }

    public String type() {
        Class<?> fieldType = entityColumn.getField().getType();
        String columnType = dialect.columnType(fieldType);

        if (fieldType.equals(String.class)) {
            return addLength(columnType);
        }

        return columnType;
    }

    private String addLength(String columnType) {
        Field field = entityColumn.getField();
        Column column = field.getAnnotation(Column.class);
        if (column == null) {
            return String.format(LENGTH_FORMAT, columnType, DEFAULT_LENGTH);
        }

        return String.format(LENGTH_FORMAT, columnType, column.length());
    }

    public String options() {
        Field field = entityColumn.getField();
        OptionStrategy optionStrategy = optionStrategies.stream()
                .filter(strategy -> strategy.supports(field))
                .findFirst()
                .orElse(NoneOptionStrategy.INSTANCE);

        return optionStrategy.options(field);
    }
}
