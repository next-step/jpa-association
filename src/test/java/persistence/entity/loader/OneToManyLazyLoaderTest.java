package persistence.entity.loader;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.EntityLoader;
import persistence.meta.EntityMeta;
import persistence.sql.QueryGenerator;
import persistence.testFixtures.assosiate.LazyLoadOrder;
import persistence.testFixtures.assosiate.Order;
import util.DataBaseTestSetUp;

class OneToManyLazyLoaderTest extends DataBaseTestSetUp {

    @Test
    @DisplayName("oneToMany 지연로딩 단건을 조회한다.")
    void findLazy() {
        //given
        EntityMeta entityMeta = EntityMeta.from(LazyLoadOrder.class);

        QueryGenerator queryGenerator = QueryGenerator.of(LazyLoadOrder.class, dialect);
        EntityLoader entityLoader = new EntityLoaderFactory(jdbcTemplate).create(entityMeta, queryGenerator);

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
    @DisplayName("One To Many 다건을 조회한다.")
    void resultSetToOneToManyEntity() {
        //given
        EntityMeta entityMeta = EntityMeta.from(LazyLoadOrder.class);

        QueryGenerator queryGenerator = QueryGenerator.of(LazyLoadOrder.class, dialect);
        EntityLoader entityLoader = new EntityLoaderFactory(jdbcTemplate).create(entityMeta, queryGenerator);

        //when
        final List<Order> orders = entityLoader.findAll(Order.class);

        //then
        assertSoftly((it) -> {
            it.assertThat(orders).hasSize(2);
            it.assertThat(orders).extracting("id").contains(1L, 2L);
            it.assertThat(orders).extracting("orderNumber").contains("order-number-1", "order-number-2");
            it.assertThat(orders).extracting("orderItems").hasSize(2);
            it.assertThat(orders.get(0).getOrderItems()).hasSize(2);
            it.assertThat(orders.get(1).getOrderItems()).hasSize(2);
        });
    }

}
