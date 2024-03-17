package database.mapping;

import database.mapping.column.EntityColumn;
import database.sql.dml.Person4;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class EntityMetadataTest {
    private final EntityMetadata entityMetadata = EntityMetadataFactory.get(Person4.class);

    @Test
    void getTableName() {
        String tableName = entityMetadata.getTableName();
        assertThat(tableName).isEqualTo("users");
    }

    @Test
    void getAllColumnNames() {
        List<String> allColumnNames = entityMetadata.getAllEntityColumns().stream().map(EntityColumn::getColumnName)
                .collect(Collectors.toList());
        assertThat(allColumnNames).containsExactly("id", "nick_name", "old", "email");
    }
}
