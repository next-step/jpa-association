package persistence.entity.mapper;

import domain.FixtureAssociatedEntity;
import domain.FixtureAssociatedEntity.Order;
import extension.EntityMetadataExtension;
import org.h2.tools.SimpleResultSet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import persistence.core.EntityColumns;
import persistence.util.ReflectionUtils;

import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

@ExtendWith(EntityMetadataExtension.class)
class EntityOneToManyMapperTest {

    @Test
    @DisplayName("EntityOneToManyMapper 를 통해 ResultSet 들의 정보로 Entity 객체의 OneToManyColumn 에 해당하는 필드에 값을 바인딩 할 수 있다.")
    void entityOneToManyMapperTest() throws SQLException {
        final Class<Order> clazz = Order.class;
        final Order order = ReflectionUtils.createInstance(clazz);
        final EntityColumns entityColumns = new EntityColumns(clazz);
        final EntityOneToManyMapper entityOneToManyMapper = new EntityOneToManyMapper(entityColumns.getOneToManyColumns());
        final SimpleResultSet rs = new SimpleResultSet();
        rs.addColumn("order_items.id", Types.BIGINT, 10, 0);
        rs.addColumn("order_items.product", Types.VARCHAR, 255, 0);
        rs.addColumn("order_items.quantity", Types.INTEGER, 10, 0);
        rs.addRow(1L, "testProduct01", 10);
        rs.addRow(2L, "testProduct02", 20);
        rs.addRow(3L, "testProduct03", 30);
        rs.addRow(4L, "testProduct04", 40);
        rs.addRow(5L, "testProduct05", 50);
        rs.next();

        entityOneToManyMapper.mapColumns(rs, order);

        final List<FixtureAssociatedEntity.OrderItem> orderItems = order.getOrderItems();
        assertSoftly(softly -> {
            softly.assertThat(orderItems).hasSize(5);
            softly.assertThat(orderItems).extracting(FixtureAssociatedEntity.OrderItem::getId)
                    .containsExactly(1L, 2L, 3L, 4L, 5L);
            softly.assertThat(orderItems).extracting(FixtureAssociatedEntity.OrderItem::getProduct)
                    .containsExactly("testProduct01", "testProduct02", "testProduct03", "testProduct04", "testProduct05");
            softly.assertThat(orderItems).extracting(FixtureAssociatedEntity.OrderItem::getQuantity)
                    .containsExactly(10, 20, 30, 40, 50);
        });
    }
}
