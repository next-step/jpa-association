package persistence.entity;

import domain.FixtureAssociatedEntity.OrderLazy;
import domain.FixtureAssociatedEntity.OrderLazyItem;
import extension.EntityMetadataExtension;
import jdbc.JdbcTemplate;
import jdbc.RowMapper;
import mock.MockDatabaseServer;
import mock.MockDmlGenerator;
import net.sf.cglib.proxy.Enhancer;
import org.h2.tools.SimpleResultSet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import persistence.core.EntityOneToManyColumn;
import persistence.util.ReflectionUtils;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.SoftAssertions.assertSoftly;


@ExtendWith(EntityMetadataExtension.class)
class EntityCollectionLoaderTest {

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
    @DisplayName("EntityOneToManyColumn(Lazy) 는 proxy 처리 되어 있으며 객체 메서드 호출시 값을 load 한다.")
    void name() throws NoSuchFieldException {
        final SimpleResultSet rs = createBaseResultSet();
        final EntityCollectionLoader entityCollectionLoader = new EntityCollectionLoader(new MockDmlGenerator(), new MockJdbcTemplate(rs));
        final Class<OrderLazy> targetClass = OrderLazy.class;
        final EntityOneToManyColumn oneToManyColumn = new EntityOneToManyColumn(targetClass.getDeclaredField("orderItems"), "WithOneToMany");
        final OrderLazy instance = ReflectionUtils.createInstance(targetClass);

        entityCollectionLoader.initLazyOneToMany(oneToManyColumn, instance, 777L);

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

    private SimpleResultSet createBaseResultSet() {
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
