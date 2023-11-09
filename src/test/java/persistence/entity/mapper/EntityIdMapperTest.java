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

class EntityIdMapperTest {

    @Test
    @DisplayName("EntityIdMapper 를 통해 ResultSet 들의 정보로 Entity 객체의 IdColumn 에 해당하는 필드에 값을 바인딩 할 수 있다.")
    void entityIdMapperTest() throws SQLException {
        final Class<Order> clazz = Order.class;
        final Order order = ReflectionUtils.createInstance(clazz);
        final EntityColumns entityColumns = new EntityColumns(clazz, "orders");
        final EntityColumnsMapper entityIdMapper = EntityIdMapper.of(entityColumns.getId());
        final SimpleResultSet rs = new SimpleResultSet();
        rs.addColumn("id", Types.BIGINT, 10, 0);
        rs.addRow(1L);
        rs.next();

        entityIdMapper.mapColumnsInternal(rs, order);

        assertThat(order.getId()).isEqualTo(1L);
    }
}
