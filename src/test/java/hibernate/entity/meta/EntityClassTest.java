package hibernate.entity.meta;

import jakarta.persistence.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class EntityClassTest {

    @Test
    void Entity_어노테이션이_없으면_생성_시_예외가_발생한다() {
        assertThatThrownBy(() -> EntityClass.getInstance(EntityClass.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Entity 어노테이션이 없는 클래스는 입력될 수 없습니다.");
    }

    @Test
    void Table_어노테이션의_name이_있으면_생성_시_tableName이_된다() {
        String actual = EntityClass.getInstance(TableEntity.class).tableName();
        assertThat(actual).isEqualTo("new_table");
    }

    @Test
    void Table_어노테이션의_name이_없으면_tableName은_클래스명이_된다() {
        String actual = EntityClass.getInstance(NoTableEntity.class).tableName();
        assertThat(actual).isEqualTo("NoTableEntity");
    }

    @Test
    void 새로운_인스턴스를_생성한다()  {
        Object actual = EntityClass.getInstance(TableEntity.class).newInstance();
        assertThat(actual).isInstanceOf(TableEntity.class);
    }

    @Test
    void 인스턴스_생성_시_기본생성자가_존재하지않으면_예외가_발생한다() {
        assertThatThrownBy(() -> EntityClass.getInstance(NoConstructorEntity.class).newInstance())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("기본 생성자가 존재하지 않습니다.");
    }

    @Test
    void eager_join테이블의_필드를_가져온다() {
        Map<String, List<String>> actual = EntityClass.getInstance(Order.class)
                .getEagerJoinTableFields();

        assertAll(
                () -> assertThat(actual).hasSize(1),
                () -> assertThat(actual.get("order_items")).containsAll(List.of("id", "product", "quantity"))
        );
    }

    @Test
    void eager_join테이블의_fk필드를_가져온다() {
        Map<String, Object> actual = EntityClass.getInstance(Order.class)
                .getEagerJoinTableIds();

        assertAll(
                () -> assertThat(actual).hasSize(1),
                () -> assertThat(actual.get("order_items")).isEqualTo("order_id")
        );
    }

    @Entity
    @Table(name = "new_table")
    static class TableEntity {
        @Id
        private Long id;

        public TableEntity() {
        }

        public TableEntity(Long id) {
            this.id = id;
        }
    }

    @Entity
    static class NoTableEntity {
        @Id
        private Long id;
    }

    @Entity
    static class NoConstructorEntity {
        @Id
        private Long id;

        public NoConstructorEntity(Long id) {
            this.id = id;
        }
    }

    @Entity
    static class TestEntity {
        @Id
        private Long id;

        @Column(name = "nick_name")
        private String name;

        @Transient
        private String email;

        public TestEntity() {
        }

        public TestEntity(Long id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }
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
