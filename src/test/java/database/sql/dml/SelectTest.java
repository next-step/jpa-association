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
        String actual = newSelect(Person4.class).buildQuery();
        assertThat(actual).isEqualTo("SELECT id, nick_name, old, email FROM users");
    }

    @Test
    void buildSelectQueryWithCollection() {
        String query = newSelect(Person4.class).ids(List.of(1L, 2L)).buildQuery();
        assertThat(query).isEqualTo("SELECT id, nick_name, old, email FROM users WHERE id IN (1, 2)");

    }

    @Test
    void buildSelectQueryWithEmptyCollection() {
        String emptyArrayQuery = newSelect(Person4.class).ids(List.of()).buildQuery();
        assertThat(emptyArrayQuery).isEqualTo("SELECT id, nick_name, old, email FROM users WHERE id IN ()");
    }

    @Test
    void buildSelectQueryWithInvalidColumn() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                                                  () -> newSelect(Person4.class)
                                                          .where(WhereMap.of("aaaaa", List.of()))
                                                          .buildQuery());
        assertThat(exception.getMessage()).isEqualTo("Invalid query: aaaaa");
    }

    private Select newSelect(Class<?> entityClass) {
        EntityMetadata entityMetadata = EntityMetadataFactory.get(entityClass);
        return new Select(
                entityMetadata.getTableName(),
                entityMetadata.getAllFieldNames());
    }

}
