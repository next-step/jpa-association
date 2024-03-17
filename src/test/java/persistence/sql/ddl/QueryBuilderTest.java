package persistence.sql.ddl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.ddl.domain.Person1;
import persistence.sql.ddl.domain.Person2;
import persistence.sql.ddl.domain.Person3;
import persistence.sql.dialect.h2.H2Dialect;
import persistence.sql.meta.EntityMetaCreator;
import persistence.sql.meta.Table;
import persistence.sql.meta.simple.SimpleEntityMetaCreator;

import static org.assertj.core.api.Assertions.assertThat;

class QueryBuilderTest {
    private EntityMetaCreator entityMetaCreator;

    @BeforeEach
    void init() {
        entityMetaCreator = new SimpleEntityMetaCreator();
    }

    @DisplayName("요구사항1, @Id, @Entity가 존재하는 Person객체의 create ddl문을 생성한다.")
    @Test
    void persion_ddl_create1() {
        final DdlCreateQueryBuilder queryBuilder = new DdlCreateQueryBuilder(new H2Dialect());
        String expectedDDL = "create table person1 (id bigint not null, age integer , name varchar(255) , primary key (id))";

        final Table table = entityMetaCreator.createByClass(Person1.class);
        String actualDDL = queryBuilder.createDdl(table);

        assertThat(actualDDL).isEqualTo(expectedDDL);
    }

    @DisplayName("요구사항2, @GeneratedValue와 @Column문이 추가된 Person객체의 create ddl문을 생성한다.")
    @Test
    void persion_ddl_create2() {
        final DdlCreateQueryBuilder queryBuilder = new DdlCreateQueryBuilder(new H2Dialect());
        String expectedDDL = "create table person2 (id bigint generated by default as identity, old integer , email varchar(255) not null, nick_name varchar(255) , primary key (id))";

        final Table table = entityMetaCreator.createByClass(Person2.class);
        String actualDDL = queryBuilder.createDdl(table);

        assertThat(actualDDL).isEqualTo(expectedDDL);
    }

    @DisplayName("요구사항3, @Table과 @Transient이 추가된 Person객체의 create ddl문을 생성한다.")
    @Test
    void persion_ddl_create3() {
        final DdlCreateQueryBuilder queryBuilder = new DdlCreateQueryBuilder(new H2Dialect());
        String expectedDDL = "create table users (id bigint generated by default as identity, old integer , email varchar(255) not null, nick_name varchar(255) , primary key (id))";

        final Table table = entityMetaCreator.createByClass(Person3.class);
        String actualDDL = queryBuilder.createDdl(table);

        assertThat(actualDDL).isEqualTo(expectedDDL);
    }

    @DisplayName("요구사항4, Person객체의 drop ddl문을 생성한다.")
    @Test
    void persion_ddl_drop() {
        final DdlDropQueryBuilder queryBuilder = new DdlDropQueryBuilder();
        String expectedDDL = "drop table if exists users CASCADE";
        final Table table = entityMetaCreator.createByClass(Person3.class);

        String actualDDL = queryBuilder.dropDdl(table);

        assertThat(actualDDL).isEqualTo(expectedDDL);
    }
}