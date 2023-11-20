package persistence.entity.persister;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import persistence.fake.FakeDialect;
import persistence.fake.MockJdbcTemplate;
import persistence.testFixtures.assosiate.LazyLoadOrder;
import persistence.testFixtures.assosiate.NoOneToManyOrder;
import persistence.testFixtures.assosiate.Order;

@DisplayName("EntityPersisterFactory 테스트")
class EntityPersisterFactoryTest {

    @ParameterizedTest
    @MethodSource("펙토리에_맞는_Persiter")
    @DisplayName("EntityPersister를 상황에 맞게 생성된다.")
    void create(Class<?> entityType, Class<?> resultType) {
        //given
        EntityPersisterFactory entityPersisterFactory = new EntityPersisterFactory(new MockJdbcTemplate());

        //when
        EntityPersister entityPersister = entityPersisterFactory.create(entityType, new FakeDialect());

        //then
        assertThat(entityPersister).isInstanceOf(resultType);
    }

    private static Stream<Arguments> 펙토리에_맞는_Persiter() {
        return Stream.of(
                Arguments.of(Order.class, OneToManyEntityPersister.class),
                Arguments.of(NoOneToManyOrder.class, SimpleEntityPersister.class),
                Arguments.of(LazyLoadOrder.class, OneToManyLazyEntityPersister.class)
        );
    }

}
