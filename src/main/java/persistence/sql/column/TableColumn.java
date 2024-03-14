package persistence.sql.column;

import jakarta.persistence.Entity;
import utils.CamelToSnakeCaseConverter;

public class TableColumn implements TableEntity {

    private final TableName name;
    private final JoinTableColumns joinTableColumns;

    public TableColumn(Class<?> clazz) {
        validateEntityAnnotation(clazz);
        this.joinTableColumns = new JoinTableColumns(JoinTableColumn.fromOneToMany(clazz));
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

    public JoinTableColumns getJoinTableColumns () {
        return joinTableColumns;
    }

}
