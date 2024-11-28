package persistence.entity.loader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import database.DatabaseServer;
import database.H2;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.persister.DefaultEntityPersister;
import persistence.entity.persister.EntityPersister;
import persistence.meta.ColumnMeta;
import persistence.meta.SchemaMeta;

class EntityCollectionLoaderTest {

    private DatabaseServer server;
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void beforeEach() throws SQLException {
        server = new H2();
        server.start();

        jdbcTemplate = new JdbcTemplate(server.getConnection());
        jdbcTemplate.execute("create table parents (id bigint auto_increment primary key, name varchar(255) not null);");
        jdbcTemplate.execute("create table children (id bigint auto_increment primary key, parent_id bigint, name varchar(255) not null);");
    }

    @AfterEach
    void afterEach() {
        server.stop();

        jdbcTemplate.execute("drop table if exists parent");
        jdbcTemplate.execute("drop table if exists children");
    }

    @Test
    @DisplayName("[성공] OneToMany 연관 관계가 있는 Entity 를 조회한다.")
    void loadCollection() {
        Parent parent = new Parent("parent");
        parent.addChild(new Child("childA"));
        parent.addChild(new Child("childB"));

        EntityPersister persister = new DefaultEntityPersister(jdbcTemplate);
        persister.insert(parent);

        SchemaMeta schemaMeta = new SchemaMeta(parent);
        List<ColumnMeta> columnMetas = schemaMeta.columnMetasHasRelation();

        EntityCollectionLoader collectionLoader = new EntityCollectionLoader(jdbcTemplate);
        ColumnMeta columnMeta = columnMetas.get(0);
        List<Child> loadChildren = collectionLoader.load(Child.class, columnMeta.field(), 1L);

        assertAll("연관관계 엔티티 조회 검증",
                () -> assertThat(loadChildren.get(0).name).isEqualTo("childA"),
                () -> assertThat(loadChildren.get(0).id).isEqualTo(1L),
                () -> assertThat(loadChildren.get(1).name).isEqualTo("childB"),
                () -> assertThat(loadChildren.get(1).id).isEqualTo(2L)
        );

    }

    @Entity
    @Table(name = "parents")
    static class Parent {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String name;

        @OneToMany(fetch = FetchType.EAGER)
        @JoinColumn(name = "parent_id")
        private List<Child> children;

        public Parent() {

        }

        public Parent(String name) {
            this(name, new ArrayList<>());
        }

        private Parent(String name, List<Child> children) {
            this.name = name;
            this.children = children;
        }

        public void addChild(Child child) {
            children.add(child);
        }

    }


    @Entity
    @Table(name = "children")
    static class Child {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String name;

        public Child() {

        }

        public Child(String name) {
            this.name = name;
        }

    }

}
