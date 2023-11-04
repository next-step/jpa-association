package jdbc;

import hibernate.entity.meta.EntityClass;
import jakarta.persistence.*;
import org.h2.tools.SimpleResultSet;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class ReflectionRowMapperTest {

    @Test
    void ResultSet의_결과로_객체를_매핑한다() throws SQLException {
        // given
        SimpleResultSet givenResultSet = new SimpleResultSet();
        givenResultSet.addColumn("test_entity.id", Types.BIGINT, 0, 0);
        givenResultSet.addColumn("test_entity.nick_name", Types.VARCHAR, 0, 0);
        givenResultSet.addColumn("child_entity.id", Types.BIGINT, 0, 0);
        givenResultSet.addColumn("child_entity.age", Types.INTEGER, 0, 0);
        givenResultSet.addRow(1L, "최진영", 3L, 19);
        givenResultSet.next();
        RowMapper<TestEntity> rowMapper = ReflectionRowMapper.getInstance(EntityClass.getInstance(TestEntity.class));

        // when
        TestEntity actual = rowMapper.mapRow(givenResultSet);

        // then
        assertAll(
                () -> assertThat(actual.id).isEqualTo(1L),
                () -> assertThat(actual.name).isEqualTo("최진영"),
                () -> assertThat(actual.childEntities).hasSize(1),
                () -> assertThat(actual.childEntities.get(0).id).isEqualTo(3L),
                () -> assertThat(actual.childEntities.get(0).age).isEqualTo(19)
        );
    }

    @Entity
    @Table(name = "test_entity")
    static class TestEntity {
        @Id
        private Long id;

        @Column(name = "nick_name")
        private String name;

        @OneToMany(fetch = FetchType.EAGER)
        @JoinColumn(name = "child_entity_id")
        private List<ChildEntity> childEntities;

        @Transient
        private String email;
    }

    @Entity
    @Table(name = "child_entity")
    static class ChildEntity {
        @Id
        private Long id;

        private Integer age;
    }
}
