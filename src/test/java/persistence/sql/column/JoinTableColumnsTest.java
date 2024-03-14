package persistence.sql.column;

import domain.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class JoinTableColumnsTest {


    private JoinTableColumns joinTableColumns;

    @BeforeEach
    void setUp() {
        TableColumn tableColumn = new TableColumn(Order.class);
        joinTableColumns = tableColumn.getJoinTableColumns();
    }
    @DisplayName("연관된 엔티티 필드를 쿼리로 변환한다.")
    @Test
    void getAssociationColumnsDefinition() {


        //when
        String associationColumnsDefinition = joinTableColumns.getAssociationColumnsDefinition();

        //then
        assertThat(associationColumnsDefinition).isEqualTo("order_items.id, order_items.product, order_items.quantity");
    }

    @DisplayName("연관된 엔티티를 조인 쿼리로 변환한다.")
    @Test
    void getJoinDefinition() {
        //given
        TableColumn tableColumn = new TableColumn(Order.class);

        //when
        String joinDefinition = joinTableColumns.getJoinDefinition(tableColumn.getName());

        //then
        assertThat(joinDefinition).isEqualTo(" join order_items on orders.id = order_items.order_id");
    }

    @DisplayName("fetch type이 eager인 연관 엔티티를 반환한다.")
    @Test
    void getEagerJoinTables() {

        //when
        JoinTableColumns eagerJoinTables = joinTableColumns.getEagerJoinTables();

        //then
        assertThat(eagerJoinTables.getValues()).hasSize(0);

    }

    @DisplayName("fetch type이 lazy인 연관 엔티티를 반환한다.")
    @Test
    void getLazyJoinTables() {

        //when
        List<JoinTableColumn> lazyJoinTables = joinTableColumns.getLazyJoinTables();

        //then
        assertThat(lazyJoinTables).hasSize(1);
    }

    @DisplayName("fetch type이 eager인 연관 엔티티가 있는지 확인한다.")
    @Test
    void hasEager() {

            //when
            boolean hasEager = joinTableColumns.hasEager();

            //then
            assertThat(hasEager).isFalse();
    }
}
