package persistence.entity.loader;

import domain.FixtureAssociatedEntity.Order;
import domain.FixtureAssociatedEntity.OrderLazyItem;
import domain.FixtureEntity.Person;
import extension.EntityMetadataExtension;
import jdbc.JdbcTemplate;
import jdbc.RowMapper;
import mock.MockDatabaseServer;
import mock.MockDmlGenerator;
import org.h2.tools.SimpleResultSet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import persistence.exception.PersistenceException;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@ExtendWith(EntityMetadataExtension.class)
class EntityLoaderTest {

    static class MockJdbcTemplate extends JdbcTemplate {
        private final SimpleResultSet rs;

        public MockJdbcTemplate(final SimpleResultSet rs) {
            super(new MockDatabaseServer().getConnection());
            this.rs = rs;
        }

        @Override
        public <T> List<T> query(final String sql, final RowMapper<T> rowMapper) {
            try (rs) {
                final List<T> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(rowMapper.mapRow(rs));
                }
                return result;
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    @DisplayName("loadById 를 통해 객체를 조회할 수 있다.")
    void loadByIdTest() {
        final Class<Person> clazz = Person.class;
        final SimpleResultSet rs = createBaseResultSet();
        rs.addRow(1L, "min", 30, "jongmin4943@gmail.com");
        final EntityLoader<Person> entityLoader = new EntityLoader<>(clazz, new MockDmlGenerator(), new MockJdbcTemplate(rs));

        final Optional<Person> result = entityLoader.loadById(1L);

        assertSoftly(softly -> {
            softly.assertThat(result).isNotEmpty();
            final Person person = result.get();
            softly.assertThat(person.getId()).isEqualTo(1L);
            softly.assertThat(person.getName()).isEqualTo("min");
            softly.assertThat(person.getAge()).isEqualTo(30);
            softly.assertThat(person.getEmail()).isEqualTo("jongmin4943@gmail.com");
        });
    }

    @Test
    @DisplayName("loadById 를 통해 객체를 조회시 없는 Id 를 조회하면 Optional.Empty 가 반환된다.")
    void loadByIdEmptyTest() {
        final Class<Person> clazz = Person.class;
        final SimpleResultSet rs = createBaseResultSet();
        final EntityLoader<Person> entityLoader = new EntityLoader<>(clazz, new MockDmlGenerator(), new MockJdbcTemplate(rs));

        final Optional<Person> result = entityLoader.loadById(Integer.MAX_VALUE);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("loadById 를 통해 객체를 조회시 row 가 2개 이상 반환되면 Exception 이 던져진다.")
    void loadByIdDuplicateTest() {
        final Class<Person> clazz = Person.class;
        final SimpleResultSet rs = createBaseResultSet();
        rs.addRow(1L, "min", 30, "jongmin4943@gmail.com");
        rs.addRow(1L, "test", 20, "test@test.com");
        final EntityLoader<Person> entityLoader = new EntityLoader<>(clazz, new MockDmlGenerator(), new MockJdbcTemplate(rs));

        assertThatThrownBy(() -> entityLoader.loadById(1L)).isInstanceOf(PersistenceException.class);
    }

    @Test
    @DisplayName("Order 클래스를 이용해 left join orderItems select query를 만들 수 있다.")
    void renderSelectTest() {
        final Class<Order> clazz = Order.class;
        final SimpleResultSet rs = createBaseResultSet();
        final EntityLoader<Order> entityLoader = new EntityLoader<>(clazz, new MockDmlGenerator(), new MockJdbcTemplate(rs));

        assertThat(entityLoader.renderSelect(1L)).isEqualTo("select orders.id, orders.orderNumber, order_items.id, order_items.product, order_items.quantity from orders left join order_items on orders.id = order_items.order_id where orders.id=1");
    }

    @Test
    @DisplayName("loadAllByOwnerId 를 통해 owner id 를 가진 객체들을 조회할 수 있다.")
    void loadAllByOwnerIdTest() {
        final SimpleResultSet rs = createLazyOrderItemResultSet();
        final EntityLoader<OrderLazyItem> entityLoader = new EntityLoader<>(OrderLazyItem.class, new MockDmlGenerator(), new MockJdbcTemplate(rs));

        final List<OrderLazyItem> orderItems = entityLoader.loadAllByOwnerId("lazy_order_id", 777L);

        assertSoftly(softly -> {
            softly.assertThat(orderItems).extracting(OrderLazyItem::getId)
                    .containsExactly(1L, 2L, 3L, 4L);
            softly.assertThat(orderItems).extracting(OrderLazyItem::getProduct)
                    .containsExactly("testProduct1", "testProduct2", "testProduct3", "testProduct4");
            softly.assertThat(orderItems).extracting(OrderLazyItem::getQuantity)
                    .containsExactly(400, 300, 200, 100);
        });
    }

    @Test
    @DisplayName("loadByOwnerId 를 통해 owner id where 조건문이 추가된 query 를 생성 할 수 있다.")
    void renderSelectByOwnerIdTest() {
        final SimpleResultSet rs = createLazyOrderItemResultSet();
        final EntityLoader<OrderLazyItem> entityLoader = new EntityLoader<>(OrderLazyItem.class, new MockDmlGenerator(), new MockJdbcTemplate(rs));

        final String query = entityLoader.renderSelectByOwnerId("lazy_order_id", 1L);

        assertThat(query)
                .isEqualTo("select lazy_order_items.id, lazy_order_items.product, lazy_order_items.quantity from lazy_order_items where lazy_order_id=1");
    }


    private SimpleResultSet createBaseResultSet() {
        final SimpleResultSet rs = new SimpleResultSet();
        rs.addColumn("id", Types.BIGINT, 10, 0);
        rs.addColumn("nick_name", Types.VARCHAR, 255, 0);
        rs.addColumn("old", Types.INTEGER, 10, 0);
        rs.addColumn("email", Types.VARCHAR, 255, 0);
        return rs;
    }

    private SimpleResultSet createLazyOrderItemResultSet() {
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
