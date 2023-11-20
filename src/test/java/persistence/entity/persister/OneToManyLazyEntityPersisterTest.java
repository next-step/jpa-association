package persistence.entity.persister;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.meta.EntityMeta;
import persistence.sql.QueryGenerator;
import persistence.testFixtures.assosiate.Order;
import util.DataBaseTestSetUp;

@DisplayName("연관관계 지연로딩 테스트")
class OneToManyLazyEntityPersisterTest extends DataBaseTestSetUp {

    @Test
    @DisplayName("OneToMany 관계의 Lazy엔티티를 조회한다.")
    void oneToManyLazy() {
        //given
        EntityMeta entityMeta = EntityMeta.from(Order.class);
        QueryGenerator queryGenerator = QueryGenerator.of(Order.class, dialect);
        OneToManyLazyEntityPersister persister = OneToManyLazyEntityPersister.create(jdbcTemplate, queryGenerator,
                entityMeta);

        //when
        final Order order = persister.find(Order.class, 1L);

        //then
        assertSoftly((it) -> {
            it.assertThat(order.getId()).isEqualTo(1L);
            it.assertThat(order.getOrderItems()).hasSize(2);
            it.assertThat(order.getOrderNumber()).isEqualTo("order-number-1");
        });
    }

    @Test
    @DisplayName("OneToMany Lazy관계의 전체를 엔티티를 조회한다.")
    void oneToManyLazyAll() {
        //given
        EntityMeta entityMeta = EntityMeta.from(Order.class);
        QueryGenerator queryGenerator = QueryGenerator.of(Order.class, dialect);
        OneToManyLazyEntityPersister persister = OneToManyLazyEntityPersister.create(jdbcTemplate, queryGenerator,
                entityMeta);

        //when
        final List<Order> orders = persister.findAll(Order.class);

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
