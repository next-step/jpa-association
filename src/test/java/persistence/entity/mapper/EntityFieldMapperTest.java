package persistence.entity.mapper;

import domain.FixtureAssociatedEntity.Order;
import org.h2.tools.SimpleResultSet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.core.EntityColumns;
import persistence.util.ReflectionUtils;

import java.sql.SQLException;
import java.sql.Types;

import static org.assertj.core.api.Assertions.assertThat;

class EntityFieldMapperTest {

    @Test
    @DisplayName("EntityFieldMapper 를 통해 ResultSet 들의 정보로 Entity 객체의 EntityFieldColumn 에 해당하는 필드에 값을 바인딩 할 수 있다.")
    void entityFieldMapperTest() throws SQLException {
        final Class<Order> clazz = Order.class;
        final Order order = ReflectionUtils.createInstance(clazz);
        final EntityColumns entityColumns = new EntityColumns(clazz, "orders");
        final EntityColumnsMapper entityFieldMapper = EntityFieldMapper.of(entityColumns.getFieldColumns());
        final SimpleResultSet rs = new SimpleResultSet();
        rs.addColumn("orderNumber", Types.VARCHAR, 255, 0);
        rs.addRow("1");
        rs.next();

        entityFieldMapper.mapColumns(rs, order);

        assertThat(order.getOrderNumber()).isEqualTo("1");
    }
}
