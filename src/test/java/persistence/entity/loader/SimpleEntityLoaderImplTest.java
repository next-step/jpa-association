package persistence.entity.loader;

import fixtures.EntityFixtures;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import persistence.DatabaseTest;
import persistence.entity.attribute.EntityAttributes;
import persistence.entity.persister.SimpleEntityPersister;
import persistence.sql.infra.H2SqlConverter;

import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@Nested
@DisplayName("EntityLoader 클래스의")
public class SimpleEntityLoaderImplTest extends DatabaseTest {
    private final EntityAttributes entityAttributes = new EntityAttributes();

    public SimpleEntityLoaderImplTest() throws SQLException {
    }

    @Nested
    @DisplayName("load 메소드는")
    class findById {
        @Nested
        @DisplayName("클래스정보와 아이디가 주어지면")
        public class withInstance {
            @Test
            @DisplayName("객체를 찾아온다.")
            void returnData() {
                //given
                setUpFixtureTable(EntityFixtures.SampleOneWithValidAnnotation.class, new H2SqlConverter());
                EntityFixtures.SampleOneWithValidAnnotation sample
                        = new EntityFixtures.SampleOneWithValidAnnotation("민준", 29);

                EntityLoader entityLoader = new SimpleEntityLoaderImpl(jdbcTemplate, entityAttributes);
                SimpleEntityPersister simpleEntityPersister = new SimpleEntityPersister(jdbcTemplate, entityLoader);

                EntityFixtures.SampleOneWithValidAnnotation inserted = simpleEntityPersister.insert(sample);

                SimpleEntityLoaderImpl simpleEntityLoaderImpl = new SimpleEntityLoaderImpl(jdbcTemplate, entityAttributes);

                //when
                EntityFixtures.SampleOneWithValidAnnotation retrieved =
                        simpleEntityLoaderImpl.load(EntityFixtures.SampleOneWithValidAnnotation.class, inserted.getId().toString());

                //then
                assertThat(retrieved.toString()).isEqualTo("SampleOneWithValidAnnotation{id=1, name='민준', age=29}");
            }
        }

        @Nested
        @DisplayName("@OneToMany(fetch = FetchType.EAGER)가 붙은 클래스정보와 아이디가 주어지면")
        public class withOneToManyAnnotatedInstance {
            @Test
            @DisplayName("연관관계가 매핑된 객체를 찾아온다.")
            void returnData() throws SQLException {
                //given
                EntityLoader entityLoader = new SimpleEntityLoaderImpl(new JdbcTemplate(server.getConnection()), entityAttributes);
                EntityFixtures.OrderItem orderItem = new EntityFixtures.OrderItem("티비", 1);
                EntityFixtures.Order order = new EntityFixtures.Order("1324", List.of(orderItem));
                SimpleEntityPersister simpleEntityPersister = new SimpleEntityPersister(jdbcTemplate, entityLoader);

                setUpFixtureTable(EntityFixtures.OrderItem.class, new H2SqlConverter());
                setUpFixtureTable(EntityFixtures.Order.class, new H2SqlConverter());

                simpleEntityPersister.insert(orderItem);
                simpleEntityPersister.insert(order);

                //when
                EntityFixtures.Order retrievedOrder = simpleEntityPersister.load(EntityFixtures.Order.class, "1");

                //then
                assertThat(retrievedOrder.toString()).isEqualTo("Order{id=1, orderNumber='1324', orderItems=[OrderItem{id=1, product='티비', quantity=1}]}");
            }
        }
    }
}
