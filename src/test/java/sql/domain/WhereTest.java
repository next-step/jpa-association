package sql.domain;

import org.junit.jupiter.api.Test;
import persistence.sql.domain.Condition;
import persistence.sql.domain.Where;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class WhereTest {

    @Test
    void should_return_where_clause() {
        String tableName = "users";
        Where where = new Where(tableName)
                .and(Condition.equal(new MockCondition("name", "'chansoo'")))
                .or(Condition.equal(new MockCondition("name", "'nextstep'")))
                .and(Condition.equal(new MockCondition("age", "29")));

        assertAll(
                () -> assertThat(where.getWhereClause()).isEqualTo("name='chansoo' or name='nextstep' and age=29"),
                () -> assertThat(where.getTableName()).isEqualTo(tableName)
        );
    }

    @Test
    void should_throw_exception_when_where_clause_not_valid() {
        assertThatThrownBy(() -> new Where("users").getWhereClause())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("where clause is empty");
    }
}
