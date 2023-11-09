package persistence.entity.mapper;


import domain.FixtureAssociatedEntity;
import domain.FixtureAssociatedEntity.Order;
import domain.FixtureEntity.Person;
import extension.EntityMetadataExtension;
import org.h2.tools.SimpleResultSet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import persistence.core.EntityMetadata;

import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

@ExtendWith(EntityMetadataExtension.class)
class EntityRowMapperTest {

    @Test
    @DisplayName("EntityRowMapper 를 이용해 Person 객체를 ResultSet 으로 부터 생성 할 수 있다.")
    void personRowMappingTest() throws SQLException {
        final Class<Person> clazz = Person.class;
        final EntityRowMapper<Person> entityRowMapper = EntityRowMapper.of(new EntityMetadata<>(clazz));
        final SimpleResultSet rs = new SimpleResultSet();
        rs.addColumn("id", Types.BIGINT, 10, 0);
        rs.addColumn("nick_name", Types.VARCHAR, 255, 0);
        rs.addColumn("old", Types.INTEGER, 10, 0);
        rs.addColumn("email", Types.VARCHAR, 255, 0);
        rs.addRow(1L, "min", 30, "jongmin4943@gmail.com");
        rs.next();

        final Person person = entityRowMapper.mapRow(rs);

        assertSoftly(softly -> {
            softly.assertThat(person.getId()).isEqualTo(1L);
            softly.assertThat(person.getName()).isEqualTo("min");
            softly.assertThat(person.getAge()).isEqualTo(30);
            softly.assertThat(person.getEmail()).isEqualTo("jongmin4943@gmail.com");
        });
    }

    @Test
    @DisplayName("EntityRowMapper 를 이용해 객체를 ResultSet 으로 부터 생성 할 수 있다.")
    void rowMappingTest() throws SQLException {
        final Class<Order> clazz = Order.class;
        final EntityRowMapper<Order> entityRowMapper = EntityRowMapper.of(new EntityMetadata<>(clazz));
        final SimpleResultSet rs = mockOrderSimpleResultSet();

        final Order order = entityRowMapper.mapRow(rs);
        final List<FixtureAssociatedEntity.OrderItem> orderItems = order.getOrderItems();
        assertSoftly(softly -> {
            softly.assertThat(order.getId()).isEqualTo(1L);
            softly.assertThat(order.getOrderNumber()).isEqualTo("1");
            softly.assertThat(orderItems).hasSize(4);
            softly.assertThat(orderItems).extracting(FixtureAssociatedEntity.OrderItem::getId)
                    .containsExactly(1L, 2L, 3L, 4L);
        });
    }

    private static SimpleResultSet mockOrderSimpleResultSet() throws SQLException {
        final SimpleResultSet rs = new SimpleResultSet();
        rs.addColumn("id", Types.BIGINT, 10, 0);
        rs.addColumn("orderNumber", Types.VARCHAR, 255, 0);
        rs.addColumn("order_items.id", Types.BIGINT, 10, 0);
        rs.addColumn("order_items.product", Types.VARCHAR, 255, 0);
        rs.addColumn("order_items.quantity", Types.INTEGER, 10, 0);
        rs.addColumn("order_items.order_id", Types.BIGINT, 10, 0);
        rs.addRow(1L, "1", 1L, "testProduct01", 10, 1L);
        rs.addRow(1L, "1", 2L, "testProduct02", 10, 1L);
        rs.addRow(1L, "1", 3L, "testProduct03", 10, 1L);
        rs.addRow(1L, "1", 4L, "testProduct04", 10, 1L);
        rs.next();
        return rs;
    }
}
