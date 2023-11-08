package persistence;

import domain.FixtureAssociatedEntity.Order;
import domain.FixtureAssociatedEntity.OrderItem;
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

public class OrderApplicationTest extends IntegrationTestEnvironment {

    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        final EntityMetadataProvider entityMetadataProvider = EntityMetadataProvider.getInstance();
        final EntityMetadata<Order> orderEntityMetadata = entityMetadataProvider.getEntityMetadata(Order.class);
        final EntityMetadata<OrderItem> orderItemEntityMetadata = entityMetadataProvider.getEntityMetadata(OrderItem.class);
        final String createOrderDdl = ddlGenerator.generateCreateDdl(orderEntityMetadata);
        final String createOrderItemDdl = ddlGenerator.generateCreateDdl(orderItemEntityMetadata);
        jdbcTemplate.execute(createOrderDdl);
        jdbcTemplate.execute(createOrderItemDdl);
        saveDummyOrder();
        saveDummyOrderItems();
        final EntityManagerFactory entityManagerFactory = new SimpleEntityManagerFactory(entityMetadataProvider, new EntityScanner(Application.class), persistenceEnvironment);
        entityManager = entityManagerFactory.createEntityManager();

    }

    @Test
    @DisplayName("entityManager.find 를 통해 Order 를 OrderItem 과 함께 조회할 수 있다.")
    void orderFetchEagerFindTest() {
        final Order order = entityManager.find(Order.class, 1L);

        final List<OrderItem> orderItems = order.getOrderItems();
        assertSoftly(softly -> {
            softly.assertThat(order.getId()).isEqualTo(1L);
            softly.assertThat(order.getOrderNumber()).isEqualTo("1");
            softly.assertThat(orderItems).hasSize(4);
            softly.assertThat(orderItems).extracting(OrderItem::getId)
                    .containsExactly(1L, 2L, 3L, 4L);
        });
    }


    private void saveDummyOrder() {
        jdbcTemplate.execute("insert into orders (orderNumber) values ('1')");
        jdbcTemplate.execute("insert into orders (orderNumber) values ('2')");
        jdbcTemplate.execute("insert into orders (orderNumber) values ('3')");
        jdbcTemplate.execute("insert into orders (orderNumber) values ('4')");
    }

    private void saveDummyOrderItems() {
        jdbcTemplate.execute("insert into order_items (product, quantity, order_id) values ('테스트1','100',1)");
        jdbcTemplate.execute("insert into order_items (product, quantity, order_id) values ('테스트2','200',1)");
        jdbcTemplate.execute("insert into order_items (product, quantity, order_id) values ('테스트3','300',1)");
        jdbcTemplate.execute("insert into order_items (product, quantity, order_id) values ('테스트4','400',1)");
        jdbcTemplate.execute("insert into order_items (product, quantity, order_id) values ('테스트5','500',2)");
        jdbcTemplate.execute("insert into order_items (product, quantity, order_id) values ('테스트6','600',2)");
        jdbcTemplate.execute("insert into order_items (product, quantity, order_id) values ('테스트7','700',3)");
    }
}
