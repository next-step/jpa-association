package persistence.sql.column;

import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import persistence.sql.dialect.Dialect;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Columns {
    private static final String COMMA = ", ";

    private final List<GeneralColumn> values;

    public Columns(Object object) {
        Field[] fields = object.getClass().getDeclaredFields();
        this.values = createGeneralColumns(fields, (field) -> new GeneralColumn(object, field));
    }

    public Columns(Field[] fields) {
        this.values = createGeneralColumns(fields, GeneralColumn::new);
    }

    private List<GeneralColumn> createGeneralColumns(Field[] fields, Function<Field, GeneralColumn> columnCreator) {
        return Arrays.stream(fields)
                .filter(field -> !field.isAnnotationPresent(Transient.class))
                .filter(field -> !field.isAnnotationPresent(Id.class))
                .filter(field -> !field.isAnnotationPresent(OneToMany.class))
                .map(columnCreator)
                .collect(Collectors.toList());
    }

    public String getColumnsDefinition(Dialect dialect) {
        return this.values
                .stream()
                .map(column -> column.getDefinition(dialect))
                .collect(Collectors.joining(COMMA));
    }

    public String getColumnNames() {
        return this.values
                .stream()
                .filter(column -> !column.isAssociationEntity())
                .map(Column::getName)
                .collect(Collectors.joining(COMMA));
    }

    public List<GeneralColumn> getValues() {
        return Collections.unmodifiableList(values);
    }

    public boolean isDirty(Columns columns) {
        Map<String, Object> columnsNameValueMap = this.values.stream()
                .filter(this::isNotNull)
                .collect(Collectors.toMap(
                        GeneralColumn::getName,
                        GeneralColumn::getValue
                ));

        return columns.values.stream()
                .filter(this::isNotNull)
                .anyMatch(column -> !column.getValue().equals(columnsNameValueMap.get(column.getName())));
    }

    private boolean isNotNull(GeneralColumn column) {
        return column.getName() != null && column.getValue() != null;
    }
}
