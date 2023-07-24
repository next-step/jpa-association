package persistence.entity;

import domain.Order;
import domain.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class EntityMetaTest {
    private EntityMeta meta;

    @BeforeEach
    void setUp() {
        meta = new EntityMeta(Order.class);
    }


    @DisplayName("테이블 이름 정보를 가져올 수 있다.")
    @Test
    void getTableName() {
        assertThat(meta.getTableName())
                .isEqualTo("orders");
    }

    @DisplayName("칼럼 이름 정보들을 가져올 수 있다.")
    @Test
    void getColumnNames() {
        assertThat(meta.getColumnNames())
                .isEqualTo(List.of(
                        "id",
                        "order_number"
                ));
    }

    @DisplayName("칼럼 이름들을 조합해서 하나의 String 으로 만들 수 있다.")
    @Test
    void joinColumnNames() {
        assertThat(meta.joinColumnNames("t1"))
                .isEqualTo("t1.id, t1.order_number");
    }

    @DisplayName("테이블의 PK 이름을 가져올 수 있다.")
    @Test
    void getPkName() {
        assertThat(meta.getPkName())
                .isEqualTo("id");
    }

    @DisplayName("테이블의 FK 이름을 가져올 수 있다.")
    @Test
    void getFkName() {
        assertThat(meta.getFkName())
                .isEqualTo("order_id");
    }

    @DisplayName("테이블 조인을 위한 조건문을 가져올 수 있다.")
    @Test
    void getFkCondition() {
        assertThat(meta.getFKCondition("t1", "t2"))
                .isEqualTo("t1.id = t2.order_id");
    }

    @DisplayName("조인되는 자식의 클래스 정보를 가져올 수 있다.")
    @Test
    void getChildClass() {
        assertThat(meta.getChildClass())
                .isEqualTo(OrderItem.class);
    }

    @DisplayName("조인되는 자식의 클래스 정보를 Meta 객체 형태로 가공할 수 있다.")
    @Test
    void getChildMeta() {
        EntityMeta childMeta = meta.getChildMeta();
        assertAll(
                () -> assertThat(childMeta.getTableName())
                        .isEqualTo("order_items"),
                () -> assertThat(childMeta.getColumnNames())
                        .isEqualTo(List.of(
                                "id",
                                "product",
                                "quantity",
                                "order_id"
                        )),
                () -> assertThat(childMeta.joinColumnNames("t2"))
                        .isEqualTo("t2.id, t2.product, t2.quantity, t2.order_id"),
                () -> assertThat(childMeta.getPkName())
                        .isEqualTo("id"),
                () -> assertThat(childMeta.getFkName())
                        .isEqualTo(null)
        );
    }
}
