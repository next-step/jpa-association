package database.sql.dml;

import database.mapping.EntityMetadata;
import database.mapping.EntityMetadataFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DeleteTest {
    private final Delete delete;

    {
        EntityMetadata entityMetadata = EntityMetadataFactory.get(Person4.class);
        delete = new Delete(entityMetadata.getTableName(),
                            entityMetadata.getAllEntityColumns(), entityMetadata.getPrimaryKey()
        );
    }

    enum TestCases {
        BY_PRIMARY_KEY(Map.of("nick_name", "foo"),
                       "DELETE FROM users WHERE nick_name = 'foo'"),
        TWO_CONDITIONS1(Map.of("old", 18, "email", "example@email.com"),
                        "DELETE FROM users WHERE old = 18 AND email = 'example@email.com'"),
        TWO_CONDITIONS2(Map.of("nick_name", "foo"),
                        "DELETE FROM users WHERE nick_name = 'foo'"),
        ONE_CONDITIONS1(Map.of("old", 18),
                        "DELETE FROM users WHERE old = 18"),
        ONE_CONDITIONS2(Map.of("nick_name", "foo"),
                        "DELETE FROM users WHERE nick_name = 'foo'");

        final Map<String, Object> conditionMap;
        final String expectedQuery;

        TestCases(Map<String, Object> conditionMap, String expectedQuery) {
            this.conditionMap = conditionMap;
            this.expectedQuery = expectedQuery;
        }
    }

    @ParameterizedTest
    @EnumSource(TestCases.class)
    void assertDeleteQuery(TestCases testCases) {
        Map<String, Object> where = testCases.conditionMap;
        String expectedQuery = testCases.expectedQuery;

        assertThat(delete.where(where).buildQuery()).isEqualTo(expectedQuery);
    }
}
