package database.mapping;

import database.mapping.column.EntityColumn;
import database.mapping.column.GeneralEntityColumn;
import database.mapping.column.PrimaryKeyEntityColumn;
import database.sql.ddl.OldPerson1;
import entity.Person;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ColumnsMetadataTest {
    private final ColumnsMetadata columnsMetadata = ColumnsMetadata.fromClass(Person.class);

    @Test
    void getTableNameWithoutTableAnnotation() {
        EntityMetadata entityMetadata = EntityMetadataFactory.get(OldPerson1.class);
        String tableName = entityMetadata.getTableName();
        assertThat(tableName).isEqualTo("OldPerson1");
    }

    @Test
    void getAllColumnNames() {
        List<EntityColumn> allEntityColumns = columnsMetadata.getAllEntityColumns();
        List<String> allColumnNames = allEntityColumns.stream().map(EntityColumn::getColumnName)
                .collect(Collectors.toList());

        assertThat(allColumnNames).containsExactly("id", "nick_name", "old", "email");
    }

    @Test
    void getPrimaryKeyColumnName() {
        PrimaryKeyEntityColumn primaryKey = columnsMetadata.getPrimaryKey();
        String primaryKeyColumnName = primaryKey.getColumnName();

        assertThat(primaryKeyColumnName).isEqualTo("id");
    }

    @Test
    void getPrimaryKeyValue() {
        Person person = new Person(1020L, "abc", 10, "def@example.com");
        assertThat(columnsMetadata.getPrimaryKeyValue(person)).isEqualTo(1020L);
    }

    @Test
    void getGeneralColumns() {
        List<GeneralEntityColumn> generalColumns = columnsMetadata.getGeneralColumns();
        List<String> columnNames = generalColumns.stream().map(GeneralEntityColumn::getColumnName)
                .collect(Collectors.toList());

        assertThat(columnNames).containsExactly("nick_name", "old", "email");
    }
}
