package builder;

import builder.dml.EntityData;
import entity.Person;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EntityDataTest {

    @DisplayName("Class를 입력받아 EntityData를 생성한다.")
    @Test
    void createEntityDataInputClassTest() {
        EntityData entityData = EntityData.createEntityData(Person.class);
        assertThat(entityData)
                .extracting("clazz", "tableName", "pkName", "alias")
                .containsExactly(Person.class, "users", "id", "users_");
    }

    @DisplayName("entityInstance 를 입력받아 EntityData를 생성한다.")
    @Test
    void createEntityDataInputEntityInstanceTest() {
        Person person = new Person(1L, "테스트", 10, "test@test.com");
        EntityData entityData = EntityData.createEntityData(person);
        assertThat(entityData)
                .extracting("clazz", "tableName", "pkName", "id", "entityInstance", "alias")
                .containsExactly(Person.class, "users", "id", 1L, person, "users_");
    }

    @DisplayName("Class와 key값을 입력받아 EntityData를 생성한다.")
    @Test
    void createEntityDataInputClassKeyTest() {
        EntityData entityData = EntityData.createEntityData(Person.class, 1L);
        assertThat(entityData)
                .extracting("clazz", "tableName", "pkName", "id", "alias")
                .containsExactly(Person.class, "users", "id", 1L, "users_");
    }

}
