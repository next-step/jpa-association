package persistence.sql.dml.builder;

import fixture.PersonV3;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.model.EntityMeta;
import persistence.entity.model.EntityMetaFactory;

import static org.assertj.core.api.Assertions.assertThat;

class DeleteQueryBuilderTest {
    private final DeleteQueryBuilder deleteQueryBuilder = DeleteQueryBuilder.INSTANCE;

    @Test
    @DisplayName("객체를 전달하면 delete 문을 반환한다")
    public void delete() {
        PersonV3 person = new PersonV3(
                1L,
                "yohan",
                31,
                "yohan@google.com",
                1
        );
        EntityMeta entityMeta = EntityMetaFactory.INSTANCE.create(person.getClass());

        assertThat(deleteQueryBuilder.delete(entityMeta, person)).isEqualTo(
                "delete from users where id=1"
        );
    }
}