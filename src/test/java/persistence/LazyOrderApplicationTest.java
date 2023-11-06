package persistence;

import domain.FixtureAssociatedEntity;
import domain.FixtureAssociatedEntity.OrderLazy;
import domain.FixtureAssociatedEntity.OrderLazyItem;
import net.sf.cglib.proxy.Enhancer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.core.EntityMetadata;
import persistence.core.EntityMetadataProvider;
import persistence.entity.manager.EntityManager;
import persistence.entity.manager.EntityManagerFactory;
import persistence.core.EntityScanner;
import persistence.entity.manager.SimpleEntityManagerFactory;

import java.util.List;

import static org.assertj.core.api.SoftAssertions.assertSoftly;


public class LazyOrderApplicationTest extends IntegrationTestEnvironment {

    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        final EntityMetadata<OrderLazy> lazyOrderEntityMetadata = EntityMetadataProvider.getInstance().getEntityMetadata(OrderLazy.class);
        final EntityMetadata<OrderLazyItem> lazyOrderItemEntityMetadata = EntityMetadataProvider.getInstance().getEntityMetadata(OrderLazyItem.class);
        final String createLazyOrderDdl = ddlGenerator.generateCreateDdl(lazyOrderEntityMetadata);
        final String createOrderItemDdl = ddlGenerator.generateCreateDdl(lazyOrderItemEntityMetadata);
        jdbcTemplate.execute(createLazyOrderDdl);
        jdbcTemplate.execute(createOrderItemDdl);
        saveDummyOrder();
        saveDummyOrderItems();
        final EntityManagerFactory entityManagerFactory = new SimpleEntityManagerFactory(new EntityScanner(Application.class), persistenceEnvironment);
        entityManager = entityManagerFactory.createEntityManager();

    }

    @Test
    @DisplayName("entityManager.find 를 통해 Order 조회시 OrderItem 들을 Lazy 하게 조회할 수 있다.")
    void orderFetchLazyFindTest() {
        final OrderLazy order = entityManager.find(OrderLazy.class, 1L);
        final List<OrderLazyItem> orderItems = order.getOrderItems();

        assertSoftly(softly -> {
            softly.assertThat(Enhancer.isEnhanced(orderItems.getClass())).isTrue();
            softly.assertThat(order.getId()).isEqualTo(1L);
            softly.assertThat(order.getOrderNumber()).isEqualTo("1");
            softly.assertThat(orderItems).hasSize(4);
            softly.assertThat(orderItems).extracting(FixtureAssociatedEntity.OrderLazyItem::getId)
                    .containsExactly(1L, 2L, 3L, 4L);
        });
    }


    private void saveDummyOrder() {
        jdbcTemplate.execute("insert into lazy_orders (orderNumber) values ('1')");
        jdbcTemplate.execute("insert into lazy_orders (orderNumber) values ('2')");
        jdbcTemplate.execute("insert into lazy_orders (orderNumber) values ('3')");
        jdbcTemplate.execute("insert into lazy_orders (orderNumber) values ('4')");
    }

    private void saveDummyOrderItems() {
        jdbcTemplate.execute("insert into lazy_order_items (product, quantity, lazy_order_id) values ('테스트1','100',1)");
        jdbcTemplate.execute("insert into lazy_order_items (product, quantity, lazy_order_id) values ('테스트2','200',1)");
        jdbcTemplate.execute("insert into lazy_order_items (product, quantity, lazy_order_id) values ('테스트3','300',1)");
        jdbcTemplate.execute("insert into lazy_order_items (product, quantity, lazy_order_id) values ('테스트4','400',1)");
        jdbcTemplate.execute("insert into lazy_order_items (product, quantity, lazy_order_id) values ('테스트5','500',2)");
        jdbcTemplate.execute("insert into lazy_order_items (product, quantity, lazy_order_id) values ('테스트6','600',2)");
        jdbcTemplate.execute("insert into lazy_order_items (product, quantity, lazy_order_id) values ('테스트7','700',3)");
    }
}
