package persistence.sql.column;

import jakarta.persistence.Entity;
import persistence.sql.type.TableName;
import utils.CamelToSnakeCaseConverter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TableColumn implements TableEntity {

    private final TableName name;
    private final List<JoinTableColumn> joinTableColumn;

    public TableColumn(Class<?> clazz) {
        validateEntityAnnotation(clazz);
        this.joinTableColumn = new ArrayList<>(JoinTableColumn.fromOneToMany(clazz));
        this.name = new TableName(clazz);
    }

    private void validateEntityAnnotation(Class<?> clazz) {
        if (!clazz.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("[INFO] No @Entity annotation");
        }
    }

    @Override
    public String getName() {
        return CamelToSnakeCaseConverter.convert(name.getValue());
    }

    public List<JoinTableColumn> getJoinTableColumn() {
        return joinTableColumn;
    }

}
