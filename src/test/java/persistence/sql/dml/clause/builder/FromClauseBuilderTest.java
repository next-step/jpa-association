package persistence.sql.dml.clause.builder;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.dml.clause.operator.EqualOperator;
import persistence.sql.dml.clause.predicate.OnPredicate;

@DisplayName("From 절 생성 테스트")
class FromClauseBuilderTest {

    @Test
    @DisplayName("FROM 절에 테이블명을 지정할 수 있다.")
    void canBuildFromClause() {
        final String fromClause = FromClauseBuilder
            .builder("USERS")
            .build();

        assertThat(fromClause).isEqualTo("FROM USERS");
    }

    @Test
    @DisplayName("FROM 절에 JOIN 절을 추가할 수 있다.")
    void canBuildFromJoinClause() {
        final String fromJoinClause = FromClauseBuilder
            .builder("TEAM")
            .leftJoin("MEMBERS", OnPredicate.of("TEAM.id", "MEMBERS.team_id", new EqualOperator()))
            .build();

        assertThat(fromJoinClause).isEqualTo("FROM TEAM JOIN MEMBERS ON TEAM.id = MEMBERS.team_id");
    }
}
