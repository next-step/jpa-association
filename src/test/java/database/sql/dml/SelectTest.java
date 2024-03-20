package database.sql.dml;

import database.mapping.EntityMetadata;
import database.mapping.EntityMetadataFactory;
import database.sql.dml.part.WhereMap;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SelectTest {

    @Test
    void buildSelectQuery() {
        String actual = newSelect().buildQuery();
        assertThat(actual).isEqualTo("SELECT id, nick_name, old, email FROM users");
    }

    @Test
    void buildSelectQueryWithCollection() {
        String query = newSelect().ids(List.of(1L, 2L)).buildQuery();
        assertThat(query).isEqualTo("SELECT id, nick_name, old, email FROM users WHERE id IN (1, 2)");

    }

    @Test
    void buildSelectQueryWithEmptyCollection() {
        String emptyArrayQuery = newSelect().ids(List.of()).buildQuery();
        assertThat(emptyArrayQuery).isEqualTo("SELECT id, nick_name, old, email FROM users WHERE id IN ()");
    }

    @Test
    void buildSelectQueryWithInvalidColumn() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                                                  () -> newSelect()
                                                          .where(WhereMap.of("aaaaa", List.of()))
                                                          .buildQuery());
        assertThat(exception.getMessage()).isEqualTo("Invalid query: aaaaa");
    }

    private Select newSelect() {
        EntityMetadata entityMetadata = EntityMetadataFactory.get(Person4.class);
        return new Select(
                entityMetadata.getTableName(),
                entityMetadata.getAllColumnNamesWithAssociations(),
                entityMetadata.getPrimaryKeyName(),
                entityMetadata.getGeneralColumnNames());
    }

}
