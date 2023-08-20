package persistence.entity.model;

import model.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EntityMetaFactoryTest {

    @Test
    @DisplayName("엔티티 메타를 생성한다")
    void create() {
        // when then
        assertThat(EntityMetaFactory.INSTANCE.create(Person.class)).isNotNull();
    }
}