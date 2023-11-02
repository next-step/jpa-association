package persistence.sql.dml;

import fixtures.EntityFixtures;
import org.junit.jupiter.api.Test;
import persistence.entity.attribute.EntityAttribute;

import java.util.HashSet;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class JoinClauseTest {

    @Test
    void toStringTest() {
        EntityAttribute entityAttribute = EntityAttribute.of(EntityFixtures.Order.class, new HashSet<>());
        JoinClause joinClause = new JoinClause(entityAttribute.getOneToManyFields());
        assertThat(joinClause.toString()).isEqualTo("join order_items as order_items on orders.id = order_items.id ");
    }

}
