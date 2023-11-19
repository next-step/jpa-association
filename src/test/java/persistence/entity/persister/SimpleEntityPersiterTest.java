package persistence.entity.persister;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.meta.EntityMeta;
import persistence.sql.QueryGenerator;
import persistence.testFixtures.Person;
import util.DataBaseTestSetUp;

class SimpleEntityPersiterTest extends DataBaseTestSetUp {

    @Test
    @DisplayName("데이터 전체를 조회하고 엔티티에 맵핑한다")
    void findAll() {
        //given
        EntityMeta entityMeta = EntityMeta.from(Person.class);
        QueryGenerator queryGenerator = QueryGenerator.of(entityMeta, dialect);
        EntityPersister entityPersister = SimpleEntityPersister.create(jdbcTemplate, queryGenerator, entityMeta);

        //when
        final List<Person> personList = entityPersister.findAll(Person.class);

        //then
        assertSoftly((it) -> {
            it.assertThat(personList).hasSize(2);
            it.assertThat(personList).extracting("id").contains(-1L, -2L);
            it.assertThat(personList).extracting("name").contains("user-1", "user-2");
            it.assertThat(personList).extracting("age").contains(10, 20);
            it.assertThat(personList).extracting("email").contains("userEmail", "userEmail2");
        });
    }


    @Test
    @DisplayName("엔터티를 저장하고 조회한다")
    void find() {
        //given
        EntityMeta entityMeta = EntityMeta.from(Person.class);
        QueryGenerator queryGenerator = QueryGenerator.of(entityMeta, dialect);
        SimpleEntityPersister entityPersister = SimpleEntityPersister.create(jdbcTemplate, queryGenerator, entityMeta);

        Person person = new Person("이름", 19, "asd@gmail.com");
        final Person savedPerson = entityPersister.insert(person);

        //when
        final Person findPerson = entityPersister.find(Person.class, savedPerson.getId());

        //then
        assertThat(findPerson).isEqualTo(savedPerson);
    }


    @Test
    @DisplayName("없는 데이터를 조회하면 null을 반환한다")
    void noDataIsNull() {
        //given
        EntityMeta entityMeta = EntityMeta.from(Person.class);
        QueryGenerator queryGenerator = QueryGenerator.of(entityMeta, dialect);
        SimpleEntityPersister entityLoader = SimpleEntityPersister.create(jdbcTemplate, queryGenerator, entityMeta);

        //when
        Person person = entityLoader.find(Person.class, 0);

        //then
        assertThat(person).isNull();
    }
}
