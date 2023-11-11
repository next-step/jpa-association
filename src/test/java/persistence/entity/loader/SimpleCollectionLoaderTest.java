package persistence.entity.loader;

import fixtures.EntityFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import persistence.DatabaseTest;
import persistence.entity.attribute.EntityAttributes;
import persistence.entity.persister.EntityPersister;
import persistence.entity.persister.SimpleEntityPersister;
import persistence.sql.infra.H2SqlConverter;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Nested
@DisplayName("SimpleCollectionLoader 클래스의")
class SimpleCollectionLoaderTest extends DatabaseTest {
    EntityAttributes entityAttributes = new EntityAttributes();

    @Nested
    @DisplayName("loadCollection 메소드는")
    class loadCollection {

        @Nested
        @DisplayName("클래스 정보와 쿼리하려는 기준 칼럼과 값이 주어지면")
        public class withValidArgs {

            @Test
            @DisplayName("적절한 객체 리스트를 반환한다.")
            void returnListObject() {
                setUpFixtureTable(EntityFixtures.Order.class, new H2SqlConverter());
                setUpFixtureTable(EntityFixtures.OrderItem.class, new H2SqlConverter());

                EntityLoader entityLoader = new SimpleEntityLoader(jdbcTemplate, entityAttributes);
                CollectionLoader collectionLoader = new SimpleCollectionLoader(jdbcTemplate);
                EntityPersister entityPersister = new SimpleEntityPersister(jdbcTemplate, entityLoader, entityAttributes);
                EntityFixtures.OrderItem orderItemOne = new EntityFixtures.OrderItem("티비", 1, 1L);
                EntityFixtures.OrderItem orderItemTwo = new EntityFixtures.OrderItem("세탁기", 3, 1L);
                EntityFixtures.OrderItem insertedOrderItemOne = entityPersister.insert(orderItemOne);
                EntityFixtures.OrderItem insertedOrderItemTwo = entityPersister.insert(orderItemTwo);
                EntityFixtures.Order order = new EntityFixtures.Order("1324", List.of(insertedOrderItemOne, insertedOrderItemTwo));
                entityPersister.insert(order);

                List<EntityFixtures.OrderItem> orderItems = collectionLoader.loadCollection(
                        entityAttributes.findEntityAttribute(EntityFixtures.OrderItem.class), "order_id", "1");

                assertThat(orderItems.toString()).isEqualTo(
                        "[OrderItem{id=1, product='티비', quantity=1, orderId=1}, OrderItem{id=2, product='세탁기', quantity=3, orderId=1}]");
            }
        }
    }
}
