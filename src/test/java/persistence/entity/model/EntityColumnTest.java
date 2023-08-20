package persistence.entity.model;

import model.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static fixture.PersonFixtures.createPerson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EntityColumnTest {

    @Test
    @DisplayName("@Transient 필드는 컬럼을 생성할 수 없다")
    void constructor_validate() throws NoSuchFieldException {
        // given
        Class<Person> personClass = Person.class;
        Field field = personClass.getDeclaredField("index");

        // when / then
        assertThrows(IllegalArgumentException.class, () -> new EntityColumn(field));
    }

    @Test
    @DisplayName("컬럼의 필드를 반환한다")
    void getField() throws NoSuchFieldException {
        // given
        Class<Person> personClass = Person.class;
        Field idField = personClass.getDeclaredField("id");
        EntityColumn entityColumn = new EntityColumn(idField);

        // when
        Field field = entityColumn.getField();

        // then
        assertThat(field).isEqualTo(idField);
    }

    @Test
    @DisplayName("컬럼의 이름을 반환한다")
    void getName() throws NoSuchFieldException {
        // given
        Class<Person> personClass = Person.class;
        Field idField = personClass.getDeclaredField("name");
        EntityColumn entityColumn = new EntityColumn(idField);

        // when
        String columnName = entityColumn.getName();

        // then
        assertThat(columnName).isEqualTo("nick_name");
    }

    @Test
    @DisplayName("컬럼의 값을 반환한다")
    void getValue() throws NoSuchFieldException {
        // given
        Person person = createPerson();
        EntityColumn entityColumn = new EntityColumn(person.getClass().getDeclaredField("name"));

        // when
        Object value = entityColumn.getValue(person);

        // then
        assertThat(value).isEqualTo("yohan");
    }

    @Test
    @DisplayName("컬럼의 값을 설정한다")
    void setName() throws NoSuchFieldException {
        // given
        Person person = createPerson();
        EntityColumn entityColumn = new EntityColumn(person.getClass().getDeclaredField("name"));

        // when
        entityColumn.setValue(person, "unkwnown");

        // then
        Object value = entityColumn.getValue(person);
        assertThat(value).isEqualTo("unkwnown");
    }

    @Test
    @DisplayName("컬럼의 값을 쿼리 형태로 반환한다")
    void queryValue() throws NoSuchFieldException {
        // given
        Person person = createPerson();
        EntityColumn entityColumn = new EntityColumn(person.getClass().getDeclaredField("name"));

        // when
        String valueQuery = entityColumn.getValueQuery(person);

        // then
        assertThat(valueQuery).isEqualTo("'yohan'");
    }
}