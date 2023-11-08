package persistence.sql.dml;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.meta.EntityMeta;
import persistence.testFixtures.assosiate.NoHasJoinColumnOrder;
import persistence.testFixtures.assosiate.NoOneToManyOrder;
import persistence.testFixtures.assosiate.Order;
import persistence.testFixtures.assosiate.OrderOneToMany2Dept;

@DisplayName("OneToMany의 Join 구문을 만드는 클래스 테스트")
class OneToManyJoinQueryBuilderTest {

    @Test
    @DisplayName("oneToMany 연관관계가 없으면 예외가 생한다.")
    void noJoinQuery() {
        assertThatIllegalArgumentException().isThrownBy(() -> {
            new OneToManyJoinQueryBuilder(EntityMeta.from(NoOneToManyOrder.class));
        });
    }

    @Test
    @DisplayName("OneToMany 조인 쿼리를 생성한다.")
    void joinQuery() {
        //given
        OneToManyJoinQueryBuilder oneToMany = new OneToManyJoinQueryBuilder(EntityMeta.from(Order.class));

        //when
        String query = oneToMany.build();

        //then
        assertThat(query).isEqualTo(" LEFT JOIN order_items order_items_1 ON orders_0.id = order_items_1.order_id");
    }

    @Test
    @DisplayName("OneToMany Many쪽의 객체가 OneToMany를 가질 경우에 조인 쿼리를 생성한다.")
    void joinQueryDept2() {
        OneToManyJoinQueryBuilder oneToMany = new OneToManyJoinQueryBuilder(EntityMeta.from(OrderOneToMany2Dept.class));

        //when
        String query = oneToMany.build();

        //then
        assertThat(query).isEqualTo(" LEFT JOIN order_items2_dept order_items2_dept_1 ON orders_0.id = order_items2_dept_1.order_id LEFT JOIN order_items order_items_2 ON order_items2_dept_1.id = order_items_2.order_item_id");
    }

    @Test
    @DisplayName("joinQuery의 이름값이 없는 경우에는 테이블명_id가 값이 된다. ")
    void joinQueryTable() {
        OneToManyJoinQueryBuilder oneToMany = new OneToManyJoinQueryBuilder(EntityMeta.from(NoHasJoinColumnOrder.class));

        //when
        String query = oneToMany.build();

        //then
        assertThat(query).isEqualTo(" LEFT JOIN order_items order_items_1 ON orders_0.id = order_items_1.id");
    }




}
