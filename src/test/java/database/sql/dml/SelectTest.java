package database.sql.dml;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SelectTest {
    private final Select select = new Select(Person4.class);

    @Test
    void buildSelectQuery() {
        String actual = select.buildQuery();
        assertThat(actual).isEqualTo("SELECT id, nick_name, old, email FROM users");
    }

    @Test
    void buildSelectQueryWithCollection() {
        String query = select.buildQuery(Map.of("id", List.of(1L, 2L)));
        assertThat(query).isEqualTo("SELECT id, nick_name, old, email FROM users WHERE id IN (1, 2)");

    }

    @Test
    void buildSelectQueryWithEmptyCollection() {
        String emptyArrayQuery = select.buildQuery(Map.of("id", List.of()));
        assertThat(emptyArrayQuery).isEqualTo("SELECT id, nick_name, old, email FROM users WHERE id IN ()");
    }

    @Test
    void buildSelectQueryWithInvalidColumn() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                                                  () -> select.buildQuery(Map.of("aaaaa", List.of())));
        assertThat(exception.getMessage()).isEqualTo("Invalid query: aaaaa");
    }
}
