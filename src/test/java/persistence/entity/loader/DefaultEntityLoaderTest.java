package persistence.entity.loader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

import database.DatabaseServer;
import database.H2;
import java.lang.reflect.Proxy;
import java.sql.SQLException;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.loader.fixture.Child;
import persistence.entity.loader.fixture.EagerParent;
import persistence.entity.loader.fixture.LazyParent;
import persistence.entity.persister.DefaultEntityPersister;
import persistence.entity.persister.EntityPersister;

class DefaultEntityLoaderTest {

    private DatabaseServer server;
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void beforeEach() throws SQLException {
        server = new H2();
        server.start();

        jdbcTemplate = new JdbcTemplate(server.getConnection());
        jdbcTemplate.execute("create table eager_parents (id bigint auto_increment primary key, name varchar(255) not null);");
        jdbcTemplate.execute("create table lazy_parents (id bigint auto_increment primary key, name varchar(255) not null);");
        jdbcTemplate.execute("create table children (id bigint auto_increment primary key, parent_id bigint, name varchar(255) not null);");
    }

    @AfterEach
    void afterEach() {
        server.stop();

        jdbcTemplate.execute("drop table if exists eager_parents");
        jdbcTemplate.execute("drop table if exists lazy_parents");
        jdbcTemplate.execute("drop table if exists children");
    }

    @Test
    @DisplayName("[성공] EAGER 로딩 전략인 경우 실제 객체를 반환")
    void eagerLoad() {
        EagerParent parent = new EagerParent("eager parent");
        parent.addChild(new Child("childA"));
        parent.addChild(new Child("childB"));

        EntityPersister persister = new DefaultEntityPersister(jdbcTemplate);
        persister.insert(parent);

        EntityLoader entityLoader = new DefaultEntityLoader(jdbcTemplate);
        EagerParent loadParent = entityLoader.load(EagerParent.class, 1L);

        assertAll("조회한 엔티티 동일성 검증",
                () -> assertThat(loadParent.getChildren().get(0).getClass()).isEqualTo(Child.class),
                () -> assertThat(loadParent.getChildren().get(1).getClass()).isEqualTo(Child.class)
        );
    }

    @Test
    @DisplayName("[성공] LAZY 로딩 전략인 경우 프록시 객체를 반환")
    void lazyLoad() {
        LazyParent parent = new LazyParent("lazy parent");
        parent.addChild(new Child("childA"));
        parent.addChild(new Child("childB"));

        EntityPersister persister = new DefaultEntityPersister(jdbcTemplate);
        persister.insert(parent);

        EntityLoader entityLoader = new DefaultEntityLoader(jdbcTemplate);
        LazyParent loadParent = entityLoader.load(LazyParent.class, 1L);

        assertAll("조회한 엔티티 프록시 타입 검증",
                () -> assertTrue(Proxy.isProxyClass(loadParent.getChildren().getClass()))
        );
    }

}
