package hibernate.entity.meta.column;

import hibernate.entity.meta.EntityClass;
import jakarta.persistence.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;
import java.util.Map;

import static hibernate.entity.meta.column.EntityJoinColumns.oneToManyColumns;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class EntityJoinColumnsTest {

    @ParameterizedTest
    @CsvSource(value = {
            "hibernate.entity.meta.column.EntityJoinColumnsTest$TestEntity,true",
            "hibernate.entity.meta.column.EntityJoinColumnsTest$NoChildEntity,false"
    })
    void EAGER가_컬럼에_있는지_확인한다(final String className, final boolean expected) throws ClassNotFoundException {
        Class<?> inputClass = Class.forName(className);
        boolean actual = oneToManyColumns(EntityClass.getInstance(inputClass))
                .hasEagerFetchType();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void oneToMany어노테이션이_달린_Eager컬럼만_가져온다() {
        List<EntityJoinColumn> actual = oneToManyColumns(EntityClass.getInstance(TestEntity.class))
                .getEagerValues();
        assertAll(
                () -> assertThat(actual).hasSize(1),
                () -> assertThat(actual.get(0).getFetchType()).isEqualTo(FetchType.EAGER)
        );
    }

    @ParameterizedTest
    @CsvSource(value = {
            "hibernate.entity.meta.column.EntityJoinColumnsTest$TestEntity,true",
            "hibernate.entity.meta.column.EntityJoinColumnsTest$NoChildEntity,false"
    })
    void LAZY가_컬럼에_있는지_확인한다(final String className, final boolean expected) throws ClassNotFoundException {
        Class<?> inputClass = Class.forName(className);
        boolean actual = oneToManyColumns(EntityClass.getInstance(inputClass))
                .hasLazyFetchType();
        assertThat(actual).isEqualTo(expected);
    }


    @Test
    void oneToMany어노테이션이_달린_LAZY컬럼만_가져온다() {
        List<EntityJoinColumn> actual = oneToManyColumns(EntityClass.getInstance(TestEntity.class))
                .getLazyValues();
        assertAll(
                () -> assertThat(actual).hasSize(1),
                () -> assertThat(actual.get(0).getFetchType()).isEqualTo(FetchType.LAZY)
        );
    }

    @Test
    void eager_join테이블의_필드를_가져온다() {
        Map<String, List<String>> actual = EntityJoinColumns.oneToManyColumns(EntityClass.getInstance(Order.class))
                .getEagerJoinTableFields();

        assertAll(
                () -> assertThat(actual).hasSize(1),
                () -> assertThat(actual.get("order_items")).containsAll(List.of("id", "product", "quantity"))
        );
    }

    @Test
    void eager_join테이블의_fk필드를_가져온다() {
        Map<String, Object> actual = EntityJoinColumns.oneToManyColumns(EntityClass.getInstance(Order.class))
                .getEagerJoinTableIds();

        assertAll(
                () -> assertThat(actual).hasSize(1),
                () -> assertThat(actual.get("order_items")).isEqualTo("order_id")
        );
    }

    @Entity
    static class NoChildEntity {
        @Id
        private Long id;
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

    @Entity
    @Table(name = "orders")
    static class Order {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String orderNumber;

        @OneToMany(fetch = FetchType.EAGER)
        @JoinColumn(name = "order_id")
        private List<OrderItem> orderItems;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "order_id2")
        private OrderItem2 orderItem2;
    }


    @Entity
    @Table(name = "order_items")
    static class OrderItem {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String product;

        private Integer quantity;
    }

    @Entity
    @Table(name = "order_items2")
    static class OrderItem2 {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String product;

        private Integer quantity;
    }
}
