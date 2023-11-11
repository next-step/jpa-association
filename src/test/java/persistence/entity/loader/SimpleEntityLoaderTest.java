package persistence.entity.loader;

import fixtures.EntityFixtures;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import persistence.DatabaseTest;
import persistence.entity.attribute.EntityAttribute;
import persistence.entity.attribute.EntityAttributes;
import persistence.entity.persister.SimpleEntityPersister;
import persistence.sql.infra.H2SqlConverter;

import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@Nested
@DisplayName("SimpleEntityLoader 클래스의")
public class SimpleEntityLoaderTest extends DatabaseTest {
    private final EntityAttributes entityAttributes = new EntityAttributes();

    public SimpleEntityLoaderTest() throws SQLException {
    }

    @Nested
    @DisplayName("load 메소드는")
    class load {
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

                EntityLoader entityLoader = new SimpleEntityLoader(jdbcTemplate, entityAttributes);
                SimpleEntityPersister simpleEntityPersister = new SimpleEntityPersister(jdbcTemplate, entityLoader, entityAttributes);

                EntityFixtures.SampleOneWithValidAnnotation inserted = simpleEntityPersister.insert(sample);

                SimpleEntityLoader simpleEntityLoader = new SimpleEntityLoader(jdbcTemplate, entityAttributes);

                EntityAttribute entityAttribute = entityAttributes.findEntityAttribute(EntityFixtures.SampleOneWithValidAnnotation.class);

                //when
                EntityFixtures.SampleOneWithValidAnnotation retrieved =
                        simpleEntityLoader.load(entityAttribute, "id", inserted.getId().toString());

                //then
                assertThat(retrieved.toString()).isEqualTo("SampleOneWithValidAnnotation{id=1, name='민준', age=29}");
            }
        }

        @Nested
        @DisplayName("@OneToMany(fetch = FetchType.EAGER)가 붙은 클래스정보와 아이디가 주어지면")
        public class test {
            @Test
            @DisplayName("연관관계가 매핑된 객체를 찾아온다.")
            void returnData() throws SQLException {
                //given
                EntityLoader entityLoader = new SimpleEntityLoader(new JdbcTemplate(server.getConnection()), entityAttributes);
                EntityFixtures.OrderItem orderItem = new EntityFixtures.OrderItem("티비", 1, 1L);
                EntityFixtures.OrderItem orderItem2 = new EntityFixtures.OrderItem("세탁기", 2, 1L);
                EntityFixtures.Order order = new EntityFixtures.Order("1324", List.of(orderItem, orderItem2));

                SimpleEntityPersister simpleEntityPersister = new SimpleEntityPersister(jdbcTemplate, entityLoader, entityAttributes);

                setUpFixtureTable(EntityFixtures.OrderItem.class, new H2SqlConverter());
                setUpFixtureTable(EntityFixtures.Order.class, new H2SqlConverter());

                simpleEntityPersister.insert(orderItem);
                simpleEntityPersister.insert(orderItem2);
                simpleEntityPersister.insert(order);

                //when
                EntityFixtures.Order retrievedOrder = simpleEntityPersister.load(EntityFixtures.Order.class, "1");

                //then
                assertThat(retrievedOrder.toString())
                        .isEqualTo("Order{id=1, orderNumber='1324', orderItems=[OrderItem{id=1, product='티비', quantity=1, orderId=1}, OrderItem{id=1, product='세탁기', quantity=2, orderId=1}]}");
            }
        }

        @Nested
        @DisplayName("@OneToMany(fetch = FetchType.LAZY)가 붙은 클래스정보와 아이디가 주어지면")
        public class withLazyProxy {
            @Test
            @DisplayName("연관관계가 매핑된 객체를 찾아온다.")
            void returnData() throws SQLException {
                //given
                EntityLoader entityLoader = new SimpleEntityLoader(new JdbcTemplate(server.getConnection()), entityAttributes);

                SimpleEntityPersister simpleEntityPersister = new SimpleEntityPersister(jdbcTemplate, entityLoader, entityAttributes);

                setUpFixtureTable(EntityFixtures.Member.class, new H2SqlConverter());
                setUpFixtureTable(EntityFixtures.Team.class, new H2SqlConverter());

                EntityFixtures.Member member1 = new EntityFixtures.Member("사람1", 1L);
                EntityFixtures.Member member2 = new EntityFixtures.Member("사람2", 1L);
                EntityFixtures.Member insertedMember1 = simpleEntityPersister.insert(member1);
                EntityFixtures.Member insertedMember2 = simpleEntityPersister.insert(member2);

                EntityFixtures.Team team = new EntityFixtures.Team(List.of(insertedMember1, insertedMember2));

                simpleEntityPersister.insert(team);

                //when
                EntityFixtures.Team loadedTeam = simpleEntityPersister.load(EntityFixtures.Team.class, "1");

                //then
                assertThat(loadedTeam.toString())
                        .isEqualTo("Team{id=1, members=[Member{id=1, name='사람1', teamId=1}, Member{id=2, name='사람2', teamId=1}]}");
            }
        }
    }
}
