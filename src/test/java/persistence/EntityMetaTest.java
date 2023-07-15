package persistence;

import domain.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EntityMetaTest {

    @Test
    void find_column() {
        EntityMeta entityMeta = EntityMeta.of(Person.class);

        String actual = entityMeta.column("ID");

        assertThat(actual).isEqualTo("id");
    }

    @Test
    @DisplayName("지정된 이름의 컬럼의 필드이름을 가져옴")
    void specific_column() {
        EntityMeta entityMeta = EntityMeta.of(Person.class);

        String actual = entityMeta.column("nick_name");

        assertThat(actual).isEqualTo("name");
    }

    @Test
    void unique_id() {
        EntityMeta entityMeta = EntityMeta.of(Person.class);

        String uniqueColumn = entityMeta.uniqueColumn();

        assertThat(uniqueColumn).isEqualTo("id");
    }

    @Test
    void table_name() {
        EntityMeta entityMeta = EntityMeta.of(Person.class);

        String tableName = entityMeta.tableName();

        assertThat(tableName).isEqualTo("users");
    }
}
