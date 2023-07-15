package persistence.sql.ddl;

import domain.Order;
import org.junit.jupiter.api.Test;
import persistence.DatabaseTest;
import persistence.EntityMeta;
import persistence.sql.ddl.h2.H2SelectQueryBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SelectQueryBuilderTest extends DatabaseTest {
    @Test
    void findAll() {
        SelectQueryBuilder selectQueryBuilder = new H2SelectQueryBuilder();
        insertDb();

        String actual = selectQueryBuilder.findAll("users");

        assertAll(
                () -> assertThat(actual).isEqualTo("select * from users"),
                () -> assertThat(query(actual)).hasSize(1)
        );
    }

    @Test
    void findById() {
        SelectQueryBuilder selectQueryBuilder = new H2SelectQueryBuilder();
        insertDb();

        String actual = selectQueryBuilder.findById("users", "id", "1");

        assertAll(
                () -> assertThat(actual).isEqualTo("select * from users where id=1"),
                () -> assertNotNull(queryForObject(actual))
        );
    }

    @Test
    void findById_hasJoin() {
        SelectQueryBuilder selectQueryBuilder = new H2SelectQueryBuilder();
        EntityMeta entityMeta = EntityMeta.ofJoin(Order.class);

        String actual = selectQueryBuilder.findByIdByJoin(entityMeta, 1L);

        assertThat(actual).isEqualTo("select orders.* from orders join order_items on orders.id=order_items.order_id where orders.id=1");
    }
}
