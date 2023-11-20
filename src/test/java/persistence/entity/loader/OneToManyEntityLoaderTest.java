package persistence.entity.loader;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.meta.EntityMeta;
import persistence.sql.QueryGenerator;
import persistence.testFixtures.assosiate.Order;
import util.DataBaseTestSetUp;

public class OneToManyEntityLoaderTest extends DataBaseTestSetUp {

    @Test
    @DisplayName("oneToMany 엔터티를 로드한다.")
    void load() {
        //given
        OneToManyEntityLoader loader = OneToManyEntityLoader.create(EntityMeta.from(Order.class));

        //when
        final Order order = jdbcTemplate.queryForObject(
                QueryGenerator.of(Order.class, dialect).select().findByIdOneToManyQuery(1L),
                (rs) -> loader.load(Order.class, rs));

        //then
        assertSoftly((it) -> {
            it.assertThat(order.getId()).isEqualTo(1L);
            it.assertThat(order.getOrderNumber()).isEqualTo("order-number-1");
            it.assertThat(order.getOrderItems()).hasSize(2);
        });
    }

    @Test
    @DisplayName("다건 oneToMany 엔터티를 로드한다.")
    void resultSetToOneToManyEntityMany() {
        //given
        OneToManyEntityLoader loader = OneToManyEntityLoader.create(EntityMeta.from(Order.class));
        QueryGenerator queryGenerator = QueryGenerator.of(Order.class, dialect);


        //when
        final List<Order> orders = jdbcTemplate.queryForAll(queryGenerator.select().findAllOneToManyQuery(),
                (rs) -> loader.loadAll(Order.class, rs));

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
