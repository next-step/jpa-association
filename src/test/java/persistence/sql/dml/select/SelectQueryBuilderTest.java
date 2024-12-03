package persistence.sql.dml.select;

import example.entity.Order;
import example.entity.OrderItem;
import example.entity.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.sql.component.ColumnInfo;
import persistence.sql.component.ConditionBuilder;
import persistence.sql.component.JoinCondition;
import persistence.sql.component.JoinConditionBuilder;
import persistence.sql.component.JoinInfo;
import persistence.sql.component.JoinType;
import persistence.sql.component.TableInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SelectQueryBuilderTest {
    private static final Logger logger = LoggerFactory.getLogger(SelectQueryBuilderTest.class);

    @Test
    @DisplayName("FindAll query 테스트")
    void findAllQueryTest() {
        Class<Person> personClass = Person.class;
        SelectQuery selectQuery = new SelectQueryBuilder()
                .fromTableInfo(TableInfo.from(personClass))
                .build();
        String query = selectQuery.toString();
        logger.debug(query);
        assertThat(query).isEqualTo("select * from users;");
    }

    @Test
    @DisplayName("FindById query 테스트")
    void findByIdTest() {
        Class<Person> personClass = Person.class;
        TableInfo personTable = TableInfo.from(personClass);
        ColumnInfo idColumn = personTable.getIdColumn();

        SelectQuery selectQuery = new SelectQueryBuilder()
                .fromTableInfo(personTable)
                .whereCondition(
                        new ConditionBuilder()
                                .columnInfo(idColumn)
                                .values(Collections.singletonList("1"))
                                .build()
                )
                .build();
        String query = selectQuery.toString();
        logger.debug(query);
        assertThat(query).isEqualTo("select * from users where users.id = 1;");
    }

    @Test
    @DisplayName("Find with join test")
    void findWithJoinTest() {
        Class<Order> orderClass = Order.class;
        Class<OrderItem> orderItemClass = OrderItem.class;

        TableInfo orderTableInfo = TableInfo.from(orderClass);

        List<JoinCondition> joinConditions = new ArrayList<>();

        List<JoinInfo> joinInfos = orderTableInfo.getJoinInfos();
        for (JoinInfo joinInfo : joinInfos) {
            JoinCondition joinCondition = new JoinConditionBuilder()
                    .joinType(JoinType.LEFT_JOIN)
                    .tableInfo(joinInfo.getTargetColumnInfo().getTableInfo())
                    .sourceColumnInfo(joinInfo.getSourceColumnInfo())
                    .targetColumnInfo(joinInfo.getTargetColumnInfo())
                    .build();
            joinConditions.add(joinCondition);
        }

        ColumnInfo orderId = orderTableInfo.getIdColumn();

        SelectQuery selectQuery = new SelectQueryBuilder()
                .fromTableInfo(orderTableInfo)
                .joinConditions(joinConditions)
                .whereCondition(
                        new ConditionBuilder()
                                .columnInfo(orderId)
                                .values(Collections.singletonList("1"))
                                .build()
                )
                .build();

        String query = selectQuery.toString();
        logger.debug(query);
        assertThat(query).isEqualTo("select * from orders left join order_items on orders.id = order_items.order_id where orders.id = 1;");
    }
}
