package persistence.sql.dml;

import org.junit.jupiter.api.Test;
import persistence.entity.common.EntityId;
import persistence.sql.model.Table;
import persistence.study.sql.ddl.Person3;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateQueryBuilderTest {

    @Test
    void buildById() {
        Table table = new Table(Person3.class);
        Person3 person = new Person3(1L, "qwer", 123, "qwe@qwe.com");
        UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder(table, person);

        EntityId id = new EntityId(1L);
        String result = updateQueryBuilder.buildById(id);

        assertThat(result).isEqualTo("UPDATE users SET (id,nick_name,old,email) = (1,'qwer',123,'qwe@qwe.com') WHERE users.id=1;");
    }
}
