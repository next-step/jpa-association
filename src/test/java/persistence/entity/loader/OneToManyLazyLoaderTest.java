package persistence.entity.loader;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.persister.OneToManyEntityPersister;
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
        OneToManyEntityPersister persister = OneToManyEntityPersister.create(jdbcTemplate, queryGenerator,
                entityMeta);

        OneToManyLazyLoader loader = OneToManyLazyLoader.create(EntityMeta.from(Order.class), persister);
        final String query = queryGenerator.select()
                .findByIdQuery(1L);

        //when
        final LazyLoadOrder order = jdbcTemplate.queryForObject(query,
                (resultSet) -> loader.load(LazyLoadOrder.class, resultSet));


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
        OneToManyEntityPersister persister = OneToManyEntityPersister.create(jdbcTemplate, queryGenerator,
                entityMeta);

        OneToManyLazyLoader loader = OneToManyLazyLoader.create(EntityMeta.from(Order.class), persister);
        final String query = queryGenerator.select().findAllQuery();


        //when
        final List<LazyLoadOrder> orders = jdbcTemplate.query(query,
                (resultSet) -> loader.load(LazyLoadOrder.class, resultSet));


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
