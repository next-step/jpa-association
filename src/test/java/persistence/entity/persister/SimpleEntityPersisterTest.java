package persistence.entity.persister;

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
import persistence.sql.infra.H2SqlConverter;

import java.sql.SQLException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Nested
@DisplayName("SimpleEntityPersister 클래스의")
public class SimpleEntityPersisterTest extends DatabaseTest {
    private final EntityAttributes entityAttributes = new EntityAttributes();
    EntityFixtures.SampleOneWithValidAnnotation sample
            = new EntityFixtures.SampleOneWithValidAnnotation(1, "test_nick_name", 29);

    @Nested
    @DisplayName("insert 메소드는")
    class insert {
        @Nested
        @DisplayName("적절한 인스턴스가 주어지면")
        public class withInstance {
            @Test
            @DisplayName("객체를 데이터베이스에 저장하고, 아이디가 매핑된 객체를 반환한다.")
            void returnInstanceWithIdMapping() throws SQLException {
                //given
                setUpFixtureTable(EntityFixtures.SampleOneWithValidAnnotation.class, new H2SqlConverter());
                JdbcTemplate jdbcTemplate = new JdbcTemplate(server.getConnection());
                EntityLoader entityLoader = new SimpleEntityLoader(jdbcTemplate, entityAttributes);
                SimpleEntityPersister simpleEntityPersister = new SimpleEntityPersister(jdbcTemplate, entityLoader);

                //when
                EntityFixtures.SampleOneWithValidAnnotation inserted = simpleEntityPersister.insert(sample);

                //then
                assertThat(inserted.toString())
                        .isEqualTo("SampleOneWithValidAnnotation{id=1, name='test_nick_name', age=29}");
            }
        }
    }

    @Nested
    @DisplayName("update 메소드는")
    class update {
        @Nested
        @DisplayName("적절한 인스턴스가 주어지면")
        public class withInstance {
            @Test
            @DisplayName("객체를 데이터베이스에 저장하고, 아이디가 매핑된 객체를 반환한다.")
            void returnInstanceWithIdMapping() throws SQLException {
                //given
                setUpFixtureTable(EntityFixtures.SampleOneWithValidAnnotation.class, new H2SqlConverter());
                JdbcTemplate jdbcTemplate = new JdbcTemplate(server.getConnection());
                EntityLoader entityLoader = new SimpleEntityLoader(jdbcTemplate, entityAttributes);
                SimpleEntityPersister simpleEntityPersister = new SimpleEntityPersister(jdbcTemplate, entityLoader);
                EntityFixtures.SampleOneWithValidAnnotation inserted = simpleEntityPersister.insert(sample);

                EntityFixtures.SampleOneWithValidAnnotation updatedSample
                        = new EntityFixtures.SampleOneWithValidAnnotation(1, "test_nick_name_updated", 29);

                //when
                EntityFixtures.SampleOneWithValidAnnotation updated = simpleEntityPersister.update(inserted, updatedSample);

                //then
                assertThat(updated.toString())
                        .isEqualTo("SampleOneWithValidAnnotation{id=1, name='test_nick_name_updated', age=29}");
            }
        }
    }

    @Nested
    @DisplayName("remove 메소드는")
    class remove {
        @Nested
        @DisplayName("적절한 인스턴스가 주어지면")
        public class withInstance {
            @Test
            @DisplayName("데이터베이스에서 객체에 해당하는 로우를 삭제한다.")
            void returnInstanceWithIdMapping() throws SQLException {
                //given
                setUpFixtureTable(EntityFixtures.SampleOneWithValidAnnotation.class, new H2SqlConverter());
                JdbcTemplate jdbcTemplate = new JdbcTemplate(server.getConnection());
                EntityLoader entityLoader = new SimpleEntityLoader(jdbcTemplate, entityAttributes);
                SimpleEntityPersister simpleEntityPersister = new SimpleEntityPersister(jdbcTemplate, entityLoader);

                EntityFixtures.SampleOneWithValidAnnotation inserted = simpleEntityPersister.insert(sample);

                //when
                //then
                Assertions.assertDoesNotThrow(() -> simpleEntityPersister.remove(inserted, inserted.getId().toString()));
            }
        }
    }

    @Nested
    @DisplayName("load 메소드는")
    class load {
        @Nested
        @DisplayName("적절한 클래스 타입과 아이디가 주어지면")
        public class withClassTypeAndId {
            @Test
            @DisplayName("적절한 객체를 로드한다.")
            void returnObject() throws SQLException {
                //given
                setUpFixtureTable(EntityFixtures.SampleOneWithValidAnnotation.class, new H2SqlConverter());
                JdbcTemplate jdbcTemplate = new JdbcTemplate(server.getConnection());
                EntityLoader entityLoader = new SimpleEntityLoader(jdbcTemplate, entityAttributes);
                SimpleEntityPersister simpleEntityPersister = new SimpleEntityPersister(jdbcTemplate, entityLoader);

                simpleEntityPersister.insert(sample);

                //when
                EntityFixtures.SampleOneWithValidAnnotation loaded = simpleEntityPersister.load(EntityFixtures.SampleOneWithValidAnnotation.class, "1");

                //then
                assertThat(loaded.toString()).isEqualTo("SampleOneWithValidAnnotation{id=1, name='test_nick_name', age=29}");
            }
        }
    }
}
