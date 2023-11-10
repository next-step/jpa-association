package persistence.context;


import fixtures.EntityFixtures;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import persistence.DatabaseTest;
import persistence.entity.attribute.EntityAttributes;
import persistence.entity.loader.EntityLoader;
import persistence.entity.loader.SimpleEntityLoader;
import persistence.entity.persister.SimpleEntityPersister;
import persistence.sql.infra.H2SqlConverter;

import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Nested
@DisplayName("PersistenceContextImpl 클래스의")
class PersistenceContextImplTest extends DatabaseTest {
    private final EntityAttributes entityAttributes = new EntityAttributes();

    @Nested
    @DisplayName("getEntity 메소드는")
    class getEntity {
        @Nested
        @DisplayName("연관관계가 없는 클래스 타입과 아이디가 주어지고, 일차 캐시에 매핑된 객체가 존재하면 ")
        class ifExistsFirstCache {
            @Test
            @DisplayName("객체를 반환한다.")
            void returnFromFirstCache() throws SQLException {
                //given
                setUpFixtureTable(EntityFixtures.SampleOneWithValidAnnotation.class, new H2SqlConverter());

                EntityFixtures.SampleOneWithValidAnnotation sample =
                        new EntityFixtures.SampleOneWithValidAnnotation("민준", 29);

                EntityLoader entityLoader = new SimpleEntityLoader(new JdbcTemplate(server.getConnection()), entityAttributes);
                SimpleEntityPersister simpleEntityPersister = new SimpleEntityPersister(jdbcTemplate, entityLoader);
                EntityAttributes entityAttributes = new EntityAttributes();
                PersistenceContext persistenceContext = new PersistenceContextImpl(simpleEntityPersister, entityAttributes);

                persistenceContext.addEntity(sample);

                //when
                EntityFixtures.SampleOneWithValidAnnotation retrieved
                        = persistenceContext.getEntity(EntityFixtures.SampleOneWithValidAnnotation.class, "1");

                //then
                assertThat(retrieved.toString()).isEqualTo("SampleOneWithValidAnnotation{id=1, name='민준', age=29}");
            }
        }

        @Nested
        @DisplayName("연관관계가 없는 클래스타입과 아이디가 주어지고, 일차 캐시에 매핑된 객체가 없으면")
        class ifNotExistsFirstCache {
            @Test
            @DisplayName("null을 반환한다.")
            void returnNull() throws SQLException {
                //given
                setUpFixtureTable(EntityFixtures.SampleOneWithValidAnnotation.class, new H2SqlConverter());

                EntityLoader entityLoader = new SimpleEntityLoader(new JdbcTemplate(server.getConnection()), entityAttributes);
                SimpleEntityPersister simpleEntityPersister = new SimpleEntityPersister(jdbcTemplate, entityLoader);
                EntityAttributes entityAttributes = new EntityAttributes();
                PersistenceContext persistenceContext = new PersistenceContextImpl(simpleEntityPersister, entityAttributes);

                //when
                EntityFixtures.SampleOneWithValidAnnotation instance
                        = persistenceContext.getEntity(EntityFixtures.SampleOneWithValidAnnotation.class, "1");

                //then
                assertThat(instance).isNull();
            }
        }

        @Nested
        @DisplayName("FetchType.EAGER인 일대다 관계의 엔티티가 주어지고, 일차 캐시에 객체가 존재하면")
        class withOneToManyEagerFetchEntityAndExistsFirstCache {
            @Test
            @DisplayName("객체를 반환한다.")
            void returnFromFirstCache() throws SQLException {
                //given
                setUpFixtureTable(EntityFixtures.OrderItem.class, new H2SqlConverter());
                setUpFixtureTable(EntityFixtures.Order.class, new H2SqlConverter());

                EntityFixtures.OrderItem orderItem = new EntityFixtures.OrderItem("티비", 1);

                EntityLoader entityLoader = new SimpleEntityLoader(new JdbcTemplate(server.getConnection()), entityAttributes);
                SimpleEntityPersister simpleEntityPersister = new SimpleEntityPersister(jdbcTemplate, entityLoader);
                EntityAttributes entityAttributes = new EntityAttributes();
                PersistenceContext persistenceContext = new PersistenceContextImpl(simpleEntityPersister, entityAttributes);

                EntityFixtures.OrderItem insertedOrderItem = persistenceContext.addEntity(orderItem);
                EntityFixtures.Order order =
                        new EntityFixtures.Order("민준", List.of(insertedOrderItem));

                persistenceContext.addEntity(order);

                //when
                EntityFixtures.Order insertedOrder
                        = persistenceContext.getEntity(EntityFixtures.Order.class, "1");

                //then
                assertThat(insertedOrder.toString())
                        .isEqualTo("Order{id=1, orderNumber='민준', orderItems=[OrderItem{id=1, product='티비', quantity=1, orderId=null}]}");
            }

            @Nested
            @DisplayName("FetchType.EAGER인 일대다 관계의 엔티티가 주어지고, 일차 캐시에 객체가 없으면")
            class withOneToManyEagerFetchEntityAndNotExistsFirstCache {
                @Test
                @DisplayName("null을 반환한다.")
                void returnNull() throws SQLException {
                    //given
                    setUpFixtureTable(EntityFixtures.OrderItem.class, new H2SqlConverter());
                    setUpFixtureTable(EntityFixtures.Order.class, new H2SqlConverter());

                    EntityLoader entityLoader = new SimpleEntityLoader(new JdbcTemplate(server.getConnection()), entityAttributes);
                    SimpleEntityPersister simpleEntityPersister = new SimpleEntityPersister(jdbcTemplate, entityLoader);
                    EntityAttributes entityAttributes = new EntityAttributes();
                    PersistenceContext persistenceContext = new PersistenceContextImpl(simpleEntityPersister, entityAttributes);

                    //when
                    EntityFixtures.Order insertedOrder
                            = persistenceContext.getEntity(EntityFixtures.Order.class, "1");

                    //then
                    assertThat(insertedOrder).isNull();
                }
            }
        }
    }

    @Nested
    @DisplayName("addEntity 메소드는")
    class addEntity {
        @Nested
        @DisplayName("클래스타입과 필드가 주어지면")
        class withValidArgs {

            @Test
            @DisplayName("영속성 컨택스트에 객체를 넣는다")
            void putInstanceToFirstCache() throws SQLException {
                //given
                setUpFixtureTable(EntityFixtures.SampleOneWithValidAnnotation.class, new H2SqlConverter());

                EntityFixtures.SampleOneWithValidAnnotation sample =
                        new EntityFixtures.SampleOneWithValidAnnotation("민준", 29);

                EntityLoader entityLoader = new SimpleEntityLoader(new JdbcTemplate(server.getConnection()), entityAttributes);
                SimpleEntityPersister simpleEntityPersister = new SimpleEntityPersister(jdbcTemplate, entityLoader);
                EntityAttributes entityAttributes = new EntityAttributes();
                PersistenceContext persistenceContext = new PersistenceContextImpl(simpleEntityPersister, entityAttributes);

                //when
                EntityFixtures.SampleOneWithValidAnnotation sampleOneWithValidAnnotation = persistenceContext.addEntity(sample);

                //then
                assertThat(sampleOneWithValidAnnotation.toString()).isEqualTo("SampleOneWithValidAnnotation{id=1, name='민준', age=29}");
            }
        }
    }

    @Nested
    @DisplayName("removeEntity 메소드는")
    class removeEntity {
        @Nested
        @DisplayName("인스턴스와 필드가 주어지면")
        class withValidArgs {

            @Test
            @DisplayName("엔트리 상태를 REMOVED로 변경한다.")
            void removeInstanceInFirstCache() throws SQLException {
                //given
                setUpFixtureTable(EntityFixtures.SampleOneWithValidAnnotation.class, new H2SqlConverter());

                EntityFixtures.SampleOneWithValidAnnotation sample =
                        new EntityFixtures.SampleOneWithValidAnnotation("민준", 29);

                EntityLoader entityLoader = new SimpleEntityLoader(new JdbcTemplate(server.getConnection()), entityAttributes);
                SimpleEntityPersister simpleEntityPersister = new SimpleEntityPersister(jdbcTemplate, entityLoader);
                EntityAttributes entityAttributes = new EntityAttributes();
                PersistenceContext persistenceContext = new PersistenceContextImpl(simpleEntityPersister, entityAttributes);

                persistenceContext.addEntity(sample);

                EntityFixtures.SampleOneWithValidAnnotation retrieved
                        = persistenceContext.getEntity(EntityFixtures.SampleOneWithValidAnnotation.class, "1");

                //when
                //then
                Assertions.assertDoesNotThrow(() -> persistenceContext.removeEntity(retrieved));
            }
        }
    }

    @Nested
    @DisplayName("getDatabaseSnapshot 메소드는")
    class getDatabaseSnapshot {
        @Nested
        @DisplayName("인스턴스와 아이디가 주어지면")
        class withInstanceAndId {

            @Test
            @DisplayName("스냅샷을 반환한다.")
            void returnIfExists() throws SQLException {
                //given
                setUpFixtureTable(EntityFixtures.SampleOneWithValidAnnotation.class, new H2SqlConverter());

                EntityFixtures.SampleOneWithValidAnnotation sample =
                        new EntityFixtures.SampleOneWithValidAnnotation("민준", 29);

                EntityLoader entityLoader = new SimpleEntityLoader(new JdbcTemplate(server.getConnection()), entityAttributes);
                SimpleEntityPersister simpleEntityPersister = new SimpleEntityPersister(jdbcTemplate, entityLoader);
                EntityAttributes entityAttributes = new EntityAttributes();
                PersistenceContext persistenceContext = new PersistenceContextImpl(simpleEntityPersister, entityAttributes);

                persistenceContext.addEntity(sample);

                //when
                //then
                assertThat(persistenceContext.getDatabaseSnapshot(sample, "1").toString())
                        .isEqualTo("SampleOneWithValidAnnotation{id=1, name='민준', age=29}");
            }
        }

        @Nested
        @DisplayName("일대다 관계에 FetchType.EAGER인 인스턴스와 아이디가 주어지면")
        class withOneToManyEagerFetchEntity {

            @Nested
            @DisplayName("일차 캐시에 객체가 존재하면")
            class existsInFirstCache {
                @Test
                @DisplayName("일차 캐시를 반환한다.")
                void returnFromFirstCache() throws SQLException {
                    //given
                    setUpFixtureTable(EntityFixtures.OrderItem.class, new H2SqlConverter());
                    setUpFixtureTable(EntityFixtures.Order.class, new H2SqlConverter());

                    EntityFixtures.OrderItem orderItem = new EntityFixtures.OrderItem("티비", 1);

                    EntityLoader entityLoader = new SimpleEntityLoader(new JdbcTemplate(server.getConnection()), entityAttributes);
                    SimpleEntityPersister simpleEntityPersister = new SimpleEntityPersister(jdbcTemplate, entityLoader);
                    EntityAttributes entityAttributes = new EntityAttributes();
                    PersistenceContext persistenceContext = new PersistenceContextImpl(simpleEntityPersister, entityAttributes);

                    EntityFixtures.OrderItem insertedOrderItem = persistenceContext.addEntity(orderItem);
                    EntityFixtures.Order order =
                            new EntityFixtures.Order("민준", List.of(insertedOrderItem));

                    EntityFixtures.Order insertedOrder = persistenceContext.addEntity(order);

                    //when
                    //then
                    assertThat(persistenceContext.getDatabaseSnapshot(insertedOrder, "1").toString())
                            .isEqualTo("Order{id=1, orderNumber='민준', orderItems=[OrderItem{id=1, product='티비', quantity=1, orderId=null}]}");
                }
            }

            @Nested
            @DisplayName("일차 캐시에 객체가 없으면")
            class notExistsInFirstCache {
                @Test
                @DisplayName("null을 반환한다.")
                void returnNull() throws SQLException {
                    //given
                    setUpFixtureTable(EntityFixtures.OrderItem.class, new H2SqlConverter());
                    setUpFixtureTable(EntityFixtures.Order.class, new H2SqlConverter());

                    EntityFixtures.OrderItem orderItem = new EntityFixtures.OrderItem("티비", 1);

                    EntityLoader entityLoader = new SimpleEntityLoader(new JdbcTemplate(server.getConnection()), entityAttributes);
                    SimpleEntityPersister simpleEntityPersister = new SimpleEntityPersister(jdbcTemplate, entityLoader);
                    EntityAttributes entityAttributes = new EntityAttributes();
                    PersistenceContext persistenceContext = new PersistenceContextImpl(simpleEntityPersister, entityAttributes);

                    EntityFixtures.OrderItem insertedOrderItem = persistenceContext.addEntity(orderItem);
                    EntityFixtures.Order order =
                            new EntityFixtures.Order("민준", List.of(insertedOrderItem));

                    //when
                    EntityFixtures.Order notInsertedOrder
                            = persistenceContext.getDatabaseSnapshot(order, "1");

                    //then
                    assertThat(notInsertedOrder).isEqualTo(null);
                }
            }
        }
    }
}
