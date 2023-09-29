package persistence.entity.model;

import model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EntityMetaTest {
    private EntityMeta entityMeta;

    @BeforeEach
    void setUp() throws NoSuchFieldException {
        Class<Order> orderClass = Order.class;
        entityMeta = new EntityMeta(
                orderClass,
                "orders",
                new EntityColumn(orderClass.getDeclaredField("id")),
                new EntityColumns(Arrays.asList(new EntityColumn(orderClass.getDeclaredField("orderNumber")))),
                OneToManyColumn.of(orderClass.getDeclaredField("orderItems"))
        );
    }

    @Test
    @DisplayName("테이블 이름을 반환한다")
    void getTableName() {
        // when then
        assertThat(entityMeta.getTableName()).isEqualTo("orders");
    }

    @Test
    @DisplayName("엔티티 컬럼 이름 목록을 반환한다")
    void getColumNames() {
        // when
        List<String> columnNames = entityMeta.getColumnNames();

        // then
        assertThat(columnNames).containsExactly("orders.id", "orders.order_number");
    }

    @Test
    @DisplayName("엔티티 아이디 컬럼을 반환한다")
    void getIdColumn() {
        // when
        EntityColumn idColumn = entityMeta.getIdColumn();

        // then
        assertThat(idColumn.getName()).isEqualTo("id");
    }

    @Test
    @DisplayName("엔티티 일대다 컬럼을 반환한다")
    void getOneToManyColumn() {
        // when then
        assertThat(entityMeta.getForeignKeyName()).isEqualTo("order_id");
    }

    @Test
    @DisplayName("엔티티가 새로운지 확인한다")
    void isNew() {
        // when ten
        assertThat(entityMeta.isNew(new Order())).isTrue();
    }
}