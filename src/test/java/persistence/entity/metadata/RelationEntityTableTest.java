package persistence.entity.metadata;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.Order;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class RelationEntityTableTest {

    @Test
    @DisplayName("RelationEntityTable 생성 테스트")
    public void createRelationEntityTable() throws NoSuchFieldException {
        Class<Order> orderClass = Order.class;
        Field field = orderClass.getDeclaredField("orderItems");
        RelationEntityTable relationEntityTable = new RelationEntityTable(RelationType.ONE_TO_MANY, orderClass, field);
        JoinColumn annotation = relationEntityTable.getRootField().getAnnotation(JoinColumn.class);

        System.out.println(annotation.name());


        assertNotNull(relationEntityTable);
    }
}
