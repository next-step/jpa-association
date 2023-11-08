package persistence.sql.dml.builder;

import fixtures.EntityFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import persistence.entity.attribute.EntityAttribute;

import java.util.HashSet;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Nested
@DisplayName("SelectQueryBuilder 클래스의")
public class SelectQueryBuilderTest {

    @Nested
    @DisplayName("prepareStatement 메소드는")
    public class prepareStatement {
        @Nested
        @DisplayName("where절이 주어지면")
        public class withWhereClause {

            @Test
            @DisplayName("적절한 DML을 리턴한다.")
            void returnDmlWithWhereClause() {
                //given
                //when
                String dml = SelectQueryBuilder.of(EntityAttribute.of(EntityFixtures.SampleOneWithValidAnnotation.class, new HashSet<>()))
                        .where("id", "1").prepareStatement();

                //then
                assertThat(dml).isEqualTo("SELECT * FROM entity_name as entity_name WHERE id = '1'");
            }
        }

        @Nested
        @DisplayName("@OneToMany 어노테이션이 있는 엔티티가 주어지면")
        public class withOneToManyAnnotatedEntity {

            @Test
            @DisplayName("적절한 DML을 리턴한다.")
            void returnDmlWithWhereClause() {
                //given
                //when
                String dml = SelectQueryBuilder.of(EntityAttribute.of(EntityFixtures.Order.class, new HashSet<>()))
                        .where("orders", "id", "1")
                        .prepareStatement();

                //then
                assertThat(dml).isEqualTo("SELECT * FROM orders as orders join order_items as order_items on orders.id = order_items.order_id WHERE orders.id = '1'");
            }
        }
    }
}
