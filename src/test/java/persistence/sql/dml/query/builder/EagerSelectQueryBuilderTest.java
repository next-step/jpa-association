package persistence.sql.dml.query.builder;

import domain.Order;
import domain.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.dml.conditional.Criteria;
import persistence.sql.dml.conditional.Criterion;
import persistence.sql.dml.query.clause.WhereClause;
import persistence.sql.entity.EntityMappingTable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


class EagerSelectQueryBuilderTest {

    private EntityMappingTable orderEntityMappingTable;
    private EntityMappingTable personEntityMappingTable;
    private EagerSelectQueryBuilder eagerSelectQueryBuilder;

    @BeforeEach
    void setUp() {
        this.orderEntityMappingTable = EntityMappingTable.from(Order.class);
        this.personEntityMappingTable = EntityMappingTable.from(Person.class);
        this.eagerSelectQueryBuilder = EagerSelectQueryBuilder.getInstance();
    }

    @Test
    @DisplayName("joinColumn 이 있는 엔티티에서 쿼리문을 반환한다.")
    void eagerToSql() {
        String asName = orderEntityMappingTable.getAcronyms();
        String pkName = orderEntityMappingTable.getPkDomainTypes().getColumnName();

        Criteria criteria = Criteria.ofCriteria(List.of(Criterion.of(asName + "." + pkName, "1")));
        WhereClause whereClause = new WhereClause(criteria);

        String sql = eagerSelectQueryBuilder.toSql(orderEntityMappingTable, whereClause);

        assertThat(sql).isEqualTo("SELECT orders.id,\n" +
                "orders.orderNumber,\n" +
                "orderitems.id,\n" +
                "orderitems.product,\n" +
                "orderitems.quantity \n" +
                " FROM orders orders\n" +
                "LEFT OUTER JOIN order_items orderitems ON orders.order_id = orderitems.id \n" +
                " WHERE orders.id='1'");
    }

    @DisplayName("joinColunn이 없는 엔티티에서 쿼리문을 반환한다.")
    @Test
    void lazyToSql() {
        String asName = personEntityMappingTable.getAcronyms();
        String pkName = personEntityMappingTable.getPkDomainTypes().getColumnName();

        Criteria criteria = Criteria.ofCriteria(List.of(Criterion.of(asName + "." + pkName, "1")));
        WhereClause whereClause = new WhereClause(criteria);

        String sql = eagerSelectQueryBuilder.toSql(personEntityMappingTable, whereClause);

        assertThat(sql).isEqualTo("SELECT person.id,\n" +
                "person.nick_name,\n" +
                "person.old,\n" +
                "person.email \n" +
                " FROM Person person\n" +
                " \n" +
                " WHERE person.id='1'");
    }

}
