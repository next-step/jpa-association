package hibernate.entity.meta.column;

import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EntityColumnTest {

    @Test
    void Transient_어노테이션이_있는_경우_생성불가로_판단한다() throws NoSuchFieldException {
        Field givenField = TestEntity.class.getDeclaredField("age");
        boolean actual = EntityColumn.isAvailableCreateEntityColumn(givenField);
        assertThat(actual).isFalse();
    }

    @Test
    void OneToMany_어노테이션이_있는_경우_생성불가로_판단한다() throws NoSuchFieldException {
        Field givenField = TestEntity.class.getDeclaredField("childEntities");
        boolean actual = EntityColumn.isAvailableCreateEntityColumn(givenField);
        assertThat(actual).isFalse();
    }

    @Test
    void ManyToOne_어노테이션이_있는_경우_생성불가로_판단한다() throws NoSuchFieldException {
        Field givenField = TestEntity.class.getDeclaredField("childEntity");
        boolean actual = EntityColumn.isAvailableCreateEntityColumn(givenField);
        assertThat(actual).isFalse();
    }

    @Test
    void 생성불가능_어노테이션이_없는_경우_생성가능으로_판단한다() throws NoSuchFieldException {
        Field givenField = TestEntity.class.getDeclaredField("id");
        boolean actual = EntityColumn.isAvailableCreateEntityColumn(givenField);
        assertThat(actual).isTrue();
    }

    @Test
    void Id_어노테이션이_있는_경우_EntityId를_생성한다() throws NoSuchFieldException {
        Field givenField = TestEntity.class.getDeclaredField("id");
        EntityColumn actual = EntityColumn.create(givenField);
        assertThat(actual).isInstanceOf(EntityId.class);
    }

    @Test
    void Id_어노테이션이_없는_경우_EntityField를_생성한다() throws NoSuchFieldException {
        Field givenField = TestEntity.class.getDeclaredField("name");
        EntityColumn actual = EntityColumn.create(givenField);
        assertThat(actual).isInstanceOf(EntityField.class);
    }

    static class TestEntity {
        @Id
        private Long id;

        private String name;

        @Transient
        private int age;

        @OneToMany
        private List<ChildEntity> childEntities;

        @ManyToOne
        private ChildEntity childEntity;
    }

    static class ChildEntity {
    }
}
