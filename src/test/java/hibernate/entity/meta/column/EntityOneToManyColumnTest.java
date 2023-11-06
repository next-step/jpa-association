package hibernate.entity.meta.column;

import hibernate.entity.meta.EntityClass;
import jakarta.persistence.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
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

    @Test
    void entity의_list가_null인_경우_arraylist를_추가하여_assign한다() throws NoSuchFieldException {
        // given
        TestEntity givenEntity = new TestEntity();

        // when
        new EntityOneToManyColumn(TestEntity.class.getDeclaredField("childEntities"))
                .addFieldValue(givenEntity, new ChildEntity());

        // then
        assertThat(givenEntity.childEntities).hasSize(1);
    }

    @Test
    void entity의_list가_이미_있는_경우_값을_추가한다() throws NoSuchFieldException {
        // given
        TestEntity givenEntity = new TestEntity(new ArrayList<>());
        givenEntity.childEntities
                .add(new ChildEntity());

        // when
        new EntityOneToManyColumn(TestEntity.class.getDeclaredField("childEntities"))
                .addFieldValue(givenEntity, new ChildEntity());

        // then
        assertThat(givenEntity.childEntities).hasSize(2);
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

        public TestEntity() {
        }

        public TestEntity(List<ChildEntity> childEntities) {
            this.childEntities = childEntities;
        }
    }

    @Entity
    static class ChildEntity {
        @Id
        private Long id;

        private String name;

        public ChildEntity() {
        }
    }
}
