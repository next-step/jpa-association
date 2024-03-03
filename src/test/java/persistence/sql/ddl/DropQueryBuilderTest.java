package persistence.sql.ddl;

import domain.Person;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import persistence.sql.meta.Table;

@DisplayName("DropQueryBuilder class 의")
class DropQueryBuilderTest {

    private DropQueryBuilder builder;

    @BeforeEach
    public void setup() {
        builder = DropQueryBuilder.from();
    }

    @DisplayName("generateQuery 메서드는")
    @Nested
    class GenerateQuery {

        @DisplayName("Person Entity의 테이블 삭제 ddl이 만들어지는지 확인한다.")
        @Test
        void testGenerateQuery_WhenPersonEntity_ThenGenerateDdl() {
            // given
            Class<?> clazz = Person.class;
            Table table = Table.getInstance(Person.class);

            // when
            String result = builder.generateQuery(table);

            // then
            String expectedQuery = "DROP TABLE " + table.getTableName();
            assertEquals(expectedQuery, result);
        }
    }
}
