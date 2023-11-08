package persistence.entity.context;

import domain.FixtureEntity.Person;
import domain.FixtureEntity.WithId;
import extension.EntityMetadataExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import persistence.context.EntityKey;
import persistence.core.EntityMetadata;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(EntityMetadataExtension.class)
class EntityKeyTest {

    @Test
    @DisplayName("같은 클래스의 같은 Id 로 key 생성시 동등한 key 객체이다.")
    void entityKeyTest() {
        final EntityKey entityKeyV1 = EntityKey.of(EntityMetadata.from(Person.class), 1L);
        final EntityKey entityKeyV2 = EntityKey.of(EntityMetadata.from(Person.class), 1L);

        assertThat(entityKeyV1).isEqualTo(entityKeyV2);
    }

    @Test
    @DisplayName("같은 클래스의 다른 Id 로 key 생성시 서로 다른 key 객체이다.")
    void entityKeyDifferenceByIdTest() {
        final EntityKey entityKeyV1 = EntityKey.of(EntityMetadata.from(Person.class), 1L);
        final EntityKey entityKeyV2 = EntityKey.of(EntityMetadata.from(Person.class), 2L);

        assertThat(entityKeyV1).isNotEqualTo(entityKeyV2);
    }

    @Test
    @DisplayName("다른 클래스의 같은 Id 로 key 생성시 서로 다른 key 객체이다.")
    void entityKeyDifferenceByEntityTest() {
        final EntityKey entityKeyV1 = EntityKey.of(EntityMetadata.from(Person.class), 1L);
        final EntityKey entityKeyV2 = EntityKey.of(EntityMetadata.from(WithId.class), 1L);

        assertThat(entityKeyV1).isNotEqualTo(entityKeyV2);
    }

    @Test
    @DisplayName("다른 클래스의 다른 Id 로 key 생성시 서로 다른 key 객체이다.")
    void entityKeyDifferenceByIdAndEntityTest() {
        final EntityKey entityKeyV1 = EntityKey.of(EntityMetadata.from(Person.class), 1L);
        final EntityKey entityKeyV2 = EntityKey.of(EntityMetadata.from(WithId.class), 2L);

        assertThat(entityKeyV1).isNotEqualTo(entityKeyV2);
    }
}
