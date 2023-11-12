package persistence.entity.loader;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import persistence.entity.EntityLoader;
import persistence.fake.FakeDialect;
import persistence.fake.MockJdbcTemplate;
import persistence.meta.EntityMeta;
import persistence.sql.QueryGenerator;
import persistence.testFixtures.assosiate.LazyLoadOrder;
import persistence.testFixtures.assosiate.NoOneToManyOrder;
import persistence.testFixtures.assosiate.Order;

@DisplayName("EntityLoaderFactory 테스트")
class EntityLoaderFactoryTest {

    @ParameterizedTest
    @MethodSource("펙토리에_맞는_로더")
    @DisplayName("EntityLoaderFactory를 상황에 맞게 생성된다.")
    void create(EntityMeta entityMeta, Class<?> type) {
        //given
        EntityLoaderFactory entityLoaderFactory = new EntityLoaderFactory(new MockJdbcTemplate());

        //when
        EntityLoader entityLoader = entityLoaderFactory.create(entityMeta,
                QueryGenerator.of(entityMeta, new FakeDialect()));


        //then
        assertThat(entityLoader).isInstanceOf(type);
    }

    private static Stream<Arguments> 펙토리에_맞는_로더() {
        return Stream.of(
                Arguments.of(EntityMeta.from(Order.class), OneToManyEntityLoader.class),
                Arguments.of(EntityMeta.from(NoOneToManyOrder.class), SimpleEntityLoader.class),
                Arguments.of(EntityMeta.from(LazyLoadOrder.class), OneToManyLazyLoader.class)
        );
    }

}
