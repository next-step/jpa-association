package persistence.sql.ddl;

import domain.Order;
import domain.OrderItem;
import domain.Person;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import persistence.sql.meta.Table;
import persistence.sql.dialect.h2.H2Dialect;

@DisplayName("CreateQueryBuilder class 의")
class CreateQueryBuilderTest {

    private CreateQueryBuilder builder;

    @BeforeEach
    public void setup() {
        builder = CreateQueryBuilder.from(H2Dialect.getInstance());
    }


    @DisplayName("generateQuery 메서드는")
    @Nested
    class GenerateQuery {
        @DisplayName("Person Entity로 테이블 생성 ddl이 만들어지는지 확인한다.")
        @Test
        void testGenerateQuery_WhenPersonEntity_ThenGenerateDdl() {
            // given
            Table table = Table.getInstance(Person.class);

            //when
            String ddl = builder.generateQuery(table);

            //then
            assertThat(ddl).isEqualTo("CREATE TABLE users (id BIGINT AUTO_INCREMENT PRIMARY KEY,nick_name VARCHAR,old INTEGER,email VARCHAR NOT NULL)");
        }

        @DisplayName("Order Entity와 OrderItem Entity 테이블 생성 ddl이 만들어지는지 확인한다.")
        @Test
        void testGenerateQuery_WhenOrderEntity_ThenGenerateDdl() {
            // given
            Table table = Table.getInstance(Order.class);
            Table table1 = Table.getInstance(OrderItem.class);

            //when
            String ddl = builder.generateQuery(table);
            String ddl2 = builder.generateQuery(table1);

            //then
            assertThat(ddl).isEqualTo("CREATE TABLE orders (id BIGINT AUTO_INCREMENT PRIMARY KEY,order_number VARCHAR)");
            assertThat(ddl2).isEqualTo("CREATE TABLE order_items (id BIGINT AUTO_INCREMENT PRIMARY KEY,product VARCHAR,quantity INTEGER,order_id BIGINT,FOREIGN KEY (order_id) REFERENCES order_items(id))");
        }
    }
}
