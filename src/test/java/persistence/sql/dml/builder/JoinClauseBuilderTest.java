package persistence.sql.dml.builder;

import domain.Order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.meta.MetaFactory;

import static org.assertj.core.api.Assertions.assertThat;

class JoinClauseBuilderTest {

    @Test
    @DisplayName("JOIN 절 생성 테스트")
    void buildJoinClause() {
        JoinClauseBuilder joinClauseBuilder = JoinClauseBuilder.of(MetaFactory.get(Order.class));
        String joinClause = joinClauseBuilder.build();
        assertThat(joinClause).isEqualTo(" JOIN order_items ON orders.id=order_items.order_id");
    }

}
