package hibernate.entity.meta.column;

import jakarta.persistence.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static hibernate.entity.meta.column.EntityJoinColumns.oneToManyColumns;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class EntityJoinColumnsTest {


    @Test
    void oneToMany어노테이션이_달린_Eager컬럼만_가져온다() {
        List<EntityJoinColumn> actual = oneToManyColumns(TestEntity.class.getDeclaredFields())
                .getEagerValues();
        assertAll(
                () -> assertThat(actual).hasSize(1),
                () -> assertThat(actual.get(0).getFetchType()).isEqualTo(FetchType.EAGER)
        );
    }

    @Entity
    static class TestEntity {
        @Id
        private Long id;

        @OneToMany(fetch = FetchType.EAGER)
        private List<ChildEntity1> childEntity1s;

        @OneToMany(fetch = FetchType.LAZY)
        private List<ChildEntity2> childEntity2s;

        @ManyToOne
        private ChildEntity3 childEntity3s;
    }

    @Entity
    static class ChildEntity1 {
        @Id
        private Long id;
    }

    @Entity
    static class ChildEntity2 {
        @Id
        private Long id;
    }

    @Entity
    static class ChildEntity3 {
        @Id
        private Long id;
    }
}
