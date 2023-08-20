package persistence.entity.model;

import model.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static fixture.PersonFixtures.createPerson;
import static org.assertj.core.api.Assertions.assertThat;

class EntityColumnsTest {

    @Test
    @DisplayName("엔티티 컬럼들의 이름 목록을 조회한다")
    void getNames() throws NoSuchFieldException {
        // given
        Class<Person> personClass = Person.class;
        EntityColumns entityColumns = new EntityColumns(Arrays.asList(
                new EntityColumn(personClass.getDeclaredField("age")),
                new EntityColumn(personClass.getDeclaredField("name"))
        ));

        // when
        List<String> columnsNames = entityColumns.getNames();

        // then
        assertThat(columnsNames).containsExactly("old", "nick_name");
    }

    @Test
    @DisplayName("엔티티 컬럼들의 값을 조회한다")
    void getValues() throws NoSuchFieldException {
        // given
        Class<Person> personClass = Person.class;
        EntityColumns entityColumns = new EntityColumns(Arrays.asList(
                new EntityColumn(personClass.getDeclaredField("age")),
                new EntityColumn(personClass.getDeclaredField("name"))
        ));

        // when
        List<Object> columnsValues = entityColumns.getValues(createPerson());

        // then
        assertThat(columnsValues).containsExactly(31, "yohan");
    }

    @Test
    @DisplayName("엔티티 컬럼들의 값을 쿼리 형태로 조회한다")
    void getValuesQuery() throws NoSuchFieldException {
        // given
        Class<Person> personClass = Person.class;
        EntityColumns entityColumns = new EntityColumns(Arrays.asList(
                new EntityColumn(personClass.getDeclaredField("age")),
                new EntityColumn(personClass.getDeclaredField("name"))
        ));

        // when
        String valuesQuery = entityColumns.getValuesQuery(createPerson());

        // then
        assertThat(valuesQuery).isEqualTo("31, 'yohan'");
    }
}