package hibernate.entity.meta.column;

import hibernate.entity.meta.EntityClass;
import jakarta.persistence.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class EntityOneToManyColumnTest {

    @Test
    void 생성_시_OneToMany어노테이션이_없으면_예외가_발생한다() {
        assertThatThrownBy(() -> new EntityOneToManyColumn(NoOneToManyEntity.class.getDeclaredField("childEntities")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("OneToMany 어노테이션이 없습니다.");
    }

    @Test
    void OneToMany어노테이션을_읽어_객체를_생성한다() throws NoSuchFieldException {
        EntityOneToManyColumn actual = new EntityOneToManyColumn(TestEntity.class.getDeclaredField("childEntities"));

        assertAll(
                () -> assertThat(actual.getJoinColumnName()).isEqualTo("child_id"),
                () -> assertThat(actual.getFetchType()).isEqualTo(FetchType.EAGER),
                () -> assertThat(actual.getEntityClass()).isEqualTo(EntityClass.getInstance(ChildEntity.class))
        );
    }

    static class NoOneToManyEntity {
        @Id
        private Long id;

        private List<ChildEntity> childEntities;
    }

    @Entity
    static class TestEntity {
        @Id
        private Long id;

        @OneToMany(fetch = FetchType.EAGER)
        @JoinColumn(name = "child_id")
        private List<ChildEntity> childEntities;
    }

    @Entity
    static class ChildEntity {
        @Id
        private Long id;

        private String name;
    }
}
