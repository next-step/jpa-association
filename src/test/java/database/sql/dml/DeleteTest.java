package database.sql.dml;

import database.mapping.EntityMetadata;
import database.mapping.EntityMetadataFactory;
import database.sql.dml.part.WhereMap;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

class DeleteTest {
    private final Delete delete;

    {
        EntityMetadata entityMetadata = EntityMetadataFactory.get(Person4.class);
        delete = new Delete(entityMetadata.getTableName(),
                            entityMetadata.getAllFieldNames(),
                            entityMetadata.getPrimaryKey()
        );
    }

    enum TestCases {
        BY_PRIMARY_KEY(WhereMap.of("nick_name", "foo"),
                       "DELETE FROM users WHERE nick_name = 'foo'"),
        TWO_CONDITIONS1(WhereMap.of("old", 18, "email", "example@email.com"),
                        "DELETE FROM users WHERE old = 18 AND email = 'example@email.com'"),
        TWO_CONDITIONS2(WhereMap.of("nick_name", "foo"),
                        "DELETE FROM users WHERE nick_name = 'foo'"),
        ONE_CONDITIONS1(WhereMap.of("old", 18),
                        "DELETE FROM users WHERE old = 18"),
        ONE_CONDITIONS2(WhereMap.of("nick_name", "foo"),
                        "DELETE FROM users WHERE nick_name = 'foo'");

        private final String expectedQuery;
        private final WhereMap whereMap;

        TestCases(WhereMap whereMap, String expectedQuery) {
            this.whereMap = whereMap;
            this.expectedQuery = expectedQuery;
        }
    }

    @ParameterizedTest
    @EnumSource(TestCases.class)
    void assertDeleteQuery(TestCases testCases) {
        WhereMap whereMap = testCases.whereMap;
        String expectedQuery = testCases.expectedQuery;

        assertThat(delete.where(whereMap).buildQuery()).isEqualTo(expectedQuery);
    }
}
