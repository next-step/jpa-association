package persistence.entity.loader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.meta.EntityMeta;
import persistence.sql.QueryGenerator;
import persistence.testFixtures.Person;
import util.DataBaseTestSetUp;

class SimpleEntityLoaderTest extends DataBaseTestSetUp {

    @Test
    @DisplayName("데이터를 전체를 조회하고 엔티티에 맵핑한다")
    void findAll() {
        //given
        EntityMeta entityMeta = EntityMeta.from(Person.class);
        QueryGenerator queryGenerator = QueryGenerator.of(entityMeta, dialect);
        SimpleEntityMapper entityMapper = new SimpleEntityMapper(entityMeta);
        SimpleEntityLoader entityLoader = new SimpleEntityLoader(jdbcTemplate, queryGenerator, entityMapper);


        Person person = new Person("이름", 19, "asd@gmail.com");
        jdbcTemplate.execute(queryGenerator.insert().build(person));

        //when
        final List<Person> personList = entityLoader.findAll(Person.class);
        Person firstPerson = personList.get(0);
        final Person findPerson = entityLoader.find(Person.class, firstPerson.getId());

        //then
        assertThat(firstPerson).isEqualTo(findPerson);
    }

    @Test
    @DisplayName("데이터를 조회하고 엔티티에 맵핑한다")
    void find() {
        //given
        EntityMeta entityMeta = EntityMeta.from(Person.class);
        QueryGenerator queryGenerator = QueryGenerator.of(entityMeta, dialect);
        SimpleEntityMapper entityMapper = new SimpleEntityMapper(entityMeta);
        SimpleEntityLoader entityLoader = new SimpleEntityLoader(jdbcTemplate, queryGenerator, entityMapper);


        //when
        final List<Person> personList = entityLoader.findAll(Person.class);

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
    @DisplayName("없는 데이터를 조회하면 null을 반환한다")
    void noDataIsNull() {
        //given
        EntityMeta entityMeta = EntityMeta.from(Person.class);
        QueryGenerator queryGenerator = QueryGenerator.of(entityMeta, dialect);
        SimpleEntityMapper entityMapper = new SimpleEntityMapper(entityMeta);
        SimpleEntityLoader entityLoader = new SimpleEntityLoader(jdbcTemplate, queryGenerator, entityMapper);


        //when
        Person person = entityLoader.find(Person.class, 0);

        //then
        assertThat(person).isNull();
    }

    @Test
    @DisplayName("데이터를 저장하고 키값을 가져온다.")
    void save() {
        //given
        EntityMeta entityMeta = EntityMeta.from(Person.class);
        QueryGenerator queryGenerator = QueryGenerator.of(entityMeta, dialect);

        //when
        Person person = new Person("이름", 19, "asd");
        final Long l = jdbcTemplate.insertForGenerateKey(queryGenerator.insert().build(person));
        final Long l2 = jdbcTemplate.insertForGenerateKey(queryGenerator.insert().build(person));
        final Long l3 = jdbcTemplate.insertForGenerateKey(queryGenerator.insert().build(person));

        //then
        assertSoftly((it) -> {
            it.assertThat(l).isEqualTo(1L);
            it.assertThat(l2).isEqualTo(2L);
            it.assertThat(l3).isEqualTo(3L);
        });
    }

}
