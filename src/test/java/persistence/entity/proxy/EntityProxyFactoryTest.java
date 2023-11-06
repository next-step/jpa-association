package persistence.entity.proxy;

import domain.FixtureAssociatedEntity.OrderLazy;
import domain.FixtureAssociatedEntity.OrderLazyItem;
import extension.EntityMetadataExtension;
import mock.MockDmlGenerator;
import mock.MockJdbcTemplate;
import net.sf.cglib.proxy.Enhancer;
import org.h2.tools.SimpleResultSet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import persistence.core.EntityOneToManyColumn;
import persistence.entity.loader.EntityLoader;
import persistence.entity.loader.EntityLoaders;
import persistence.util.ReflectionUtils;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.SoftAssertions.assertSoftly;


@ExtendWith(EntityMetadataExtension.class)
class EntityProxyFactoryTest {

    static class MockEntityLoaders extends EntityLoaders {

        public MockEntityLoaders() {
            super(Map.of(
                    OrderLazyItem.class, new EntityLoader<>(OrderLazyItem.class, new MockDmlGenerator(), new MockJdbcTemplate(createOrderItemResultSet())))
            );
        }
    }

    @Test
    @DisplayName("EntityOneToManyColumn(Lazy) 는 proxy 처리 되어 있으며 객체 메서드 호출시 값을 load 한다.")
    void shouldLoadLazyOneToManyCollection() throws NoSuchFieldException {
        final EntityProxyFactory entityProxyFactory = new EntityProxyFactory(new MockEntityLoaders());
        final Class<OrderLazy> ownerClass = OrderLazy.class;
        final EntityOneToManyColumn test = new EntityOneToManyColumn(ownerClass.getDeclaredField("orderItems"), "lazy_orders");
        final OrderLazy instance = ReflectionUtils.createInstance(ownerClass);
        entityProxyFactory.initProxy(1L, instance, test);


        assertSoftly(softly -> {
            final List<OrderLazyItem> orderItems = instance.getOrderItems();
            softly.assertThat(Enhancer.isEnhanced(orderItems.getClass())).isTrue();
            softly.assertThat(orderItems).extracting(OrderLazyItem::getId)
                    .containsExactly(1L, 2L, 3L, 4L);
            softly.assertThat(orderItems).extracting(OrderLazyItem::getProduct)
                    .containsExactly("testProduct1", "testProduct2", "testProduct3", "testProduct4");
            softly.assertThat(orderItems).extracting(OrderLazyItem::getQuantity)
                    .containsExactly(400, 300, 200, 100);
        });

    }


    private static SimpleResultSet createOrderItemResultSet() {
        final SimpleResultSet rs = new SimpleResultSet();
        rs.addColumn("id", Types.BIGINT, 10, 0);
        rs.addColumn("product", Types.VARCHAR, 255, 0);
        rs.addColumn("quantity", Types.INTEGER, 10, 0);
        rs.addColumn("lazy_order_id", Types.BIGINT, 255, 0);
        rs.addRow(1L, "testProduct1", 400, 777L);
        rs.addRow(2L, "testProduct2", 300, 777L);
        rs.addRow(3L, "testProduct3", 200, 777L);
        rs.addRow(4L, "testProduct4", 100, 777L);
        return rs;
    }
}
