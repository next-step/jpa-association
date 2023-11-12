package persistence.sql.dml;

import fixtures.EntityFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import persistence.entity.attribute.EntityAttribute;
import persistence.entity.attribute.EntityAttributes;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Nested
@DisplayName("JoinClause 클래스의")
class JoinClauseTest {
    private final EntityAttributes entityAttributes = new EntityAttributes();

    @Nested
    @DisplayName("생성자는")
    class constructor {
        @Nested
        @DisplayName("연관관계가 맺어진 엔티티의 연관관계 메타정보가 주어지면")
        class withRelationMeta {
            @Test
            @DisplayName("적절한 조인 DML을 제공한다.")
            void returnJoinDML() {
                //given
                EntityAttribute owner = entityAttributes.findEntityAttribute(EntityFixtures.Order.class);

                //when
                JoinClause joinClause = new JoinClause(owner.getTableName(), owner.getIdAttribute(), owner.getOneToManyFields());

                //then
                assertThat(joinClause.prepareDML()).isEqualTo(" join order_items as order_items on orders.id = order_items.order_id");
            }
        }
    }
}
