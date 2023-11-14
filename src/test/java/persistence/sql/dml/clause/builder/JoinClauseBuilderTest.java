package persistence.sql.dml.clause.builder;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.dml.clause.operator.EqualOperator;
import persistence.sql.dml.clause.predicate.OnPredicate;

@DisplayName("JoinClauseBuilder 생성 테스트")
class JoinClauseBuilderTest {

    @Test
    @DisplayName("JOIN 절에 조건을 지정할 수 있다.")
    void canBuildJoinClause() {
        final String fromClause = JoinClauseBuilder
            .builder("USERS", OnPredicate.of("user_id", "member_id", new EqualOperator()))
            .build();

        assertThat(fromClause).isEqualTo("JOIN USERS ON user_id = member_id");
    }
}
