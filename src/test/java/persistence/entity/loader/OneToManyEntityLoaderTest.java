package persistence.entity.loader;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.meta.EntityMeta;
import persistence.sql.QueryGenerator;
import persistence.testFixtures.assosiate.Order;
import util.DataBaseTestSetUp;

class OneToManyEntityLoaderTest extends DataBaseTestSetUp {

    @Test
    @DisplayName("OneToMany 관계의 엔티티를 조회한다.")
    void oneToMany() {
        //given
        EntityMeta entityMeta = EntityMeta.from(Order.class);
        OneToManyEntityMapper entityMapper = new OneToManyEntityMapper(entityMeta);
        QueryGenerator queryGenerator = QueryGenerator.of(Order.class, dialect);
        OneToManyEntityLoader entityLoader = new OneToManyEntityLoader(jdbcTemplate, queryGenerator, entityMapper);

        //when
        final Order order = entityLoader.find(Order.class, 1L);

        //then
        assertSoftly((it) -> {
            it.assertThat(order.getId()).isEqualTo(1L);
            it.assertThat(order.getOrderItems()).hasSize(2);
            it.assertThat(order.getOrderNumber()).isEqualTo("order-number-1");
        });

    }

    @Test
    @DisplayName("OneToMany 관계의 엔티티를 조회한다.")
    void oneToManyFindALL() {
        //given
        EntityMeta entityMeta = EntityMeta.from(Order.class);
        OneToManyEntityMapper entityMapper = new OneToManyEntityMapper(entityMeta);
        QueryGenerator queryGenerator = QueryGenerator.of(Order.class, dialect);
        OneToManyEntityLoader entityLoader = new OneToManyEntityLoader(jdbcTemplate, queryGenerator, entityMapper);

        //when
        final List<Order> orders = entityLoader.findAll(Order.class);

        //then
        assertSoftly((it) -> {
            it.assertThat(orders).hasSize(2);
            it.assertThat(orders).extracting("id").contains(1L, 2L);
            it.assertThat(orders).extracting("orderNumber").contains("order-number-1", "order-number-2");
            it.assertThat(orders.get(0).getOrderItems()).hasSize(2);
            it.assertThat(orders.get(1).getOrderItems()).hasSize(2);
        });

    }
}

