package persistence.sql.dml.builder;

import fixture.PersonV3;
import model.Order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.model.EntityMeta;
import persistence.entity.model.EntityMetaFactory;

import static org.assertj.core.api.Assertions.assertThat;

class SelectQueryBuilderTest {
    private final SelectQueryBuilder selectQueryBuilder = SelectQueryBuilder.INSTANCE;

    @Test
    @DisplayName("findAll 쿼리를 반환한다")
    public void findAll() {
        EntityMeta entityMeta = EntityMetaFactory.INSTANCE.create(PersonV3.class);
        String query = selectQueryBuilder.findAll(entityMeta);

        assertThat(query).isEqualTo("select users.id, users.nick_name, users.old, users.email from users");
    }

    @Test
    @DisplayName("findById 쿼리를 반환한다")
    void findByIdSql() {
        EntityMeta entityMeta = EntityMetaFactory.INSTANCE.create(PersonV3.class);
        String query = selectQueryBuilder.findById(entityMeta, 1L);

        assertThat(query).isEqualTo("select users.id, users.nick_name, users.old, users.email from users where id=1");
    }

    @Test
    @DisplayName("OneToMany 연관관계가 즉시 로딩일 경우 조인 쿼리를 반환한다")
    void associationFindByIdSql() {
        EntityMeta entityMeta = EntityMetaFactory.INSTANCE.create(Order.class);
        String query = selectQueryBuilder.findById(entityMeta, 1L);

        assertThat(query).isEqualTo(
                "select orders.id, orders.order_number, order_items.id, order_items.product, order_items.quantity " +
                        "from orders join order_items on orders.id = order_items.order_id " +
                        "where orders.id=1"
        );
    }

}