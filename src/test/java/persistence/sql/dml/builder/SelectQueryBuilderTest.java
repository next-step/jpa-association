package persistence.sql.dml.builder;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.ddl.builder.BuilderTest;
import persistence.sql.fixture.PersonFixtureStep3;
import persistence.sql.fixture.PersonInstances;

@DisplayName("2. 요구사항 SELECT 구현하기, 3. 요구사항 WHERE 구현하기")
public class SelectQueryBuilderTest extends BuilderTest {

  @Test
  @DisplayName("SELECT SQL 구문을 생성합니다.")
  public void selectDMLfromEntity() {
    SelectQueryBuilder selectQueryBuilder = new SelectQueryBuilder();

    String query = selectQueryBuilder.createSelectQuery("id,nick_name,old,email", "USERS");

    assertThat(query).isEqualTo("SELECT id,nick_name,old,email FROM USERS;");
  }

  @Test
  @DisplayName("SELECT SQL 구문을 Where 문과 함께 생성합니다.")
  public void selectDMLWithWherefromEntity() {
    SelectQueryBuilder selectQueryBuilder = new SelectQueryBuilder();
    Long targetValue = 1L;

    String query = selectQueryBuilder.createSelectByFieldQuery(meta.getColumnClauseWithId(), meta.getTableName(), meta.getPrimaryKeyColumn().getDBColumnName(), targetValue);

    assertThat(query).isEqualTo("SELECT id,nick_name,old,email FROM USERS WHERE id=1;");
  }

  @Test
  @DisplayName("Select 쿼리 실행시에 Entity들이 반환됩니다.")
  public void selectDMLfromEntityWhereDatabase() {
    SelectQueryBuilder selectQueryBuilder = new SelectQueryBuilder();
    Long targetValue = 1L;

    String query = selectQueryBuilder.createSelectByFieldQuery(meta.getColumnClauseWithId(), meta.getTableName(), meta.getPrimaryKeyColumn().getDBColumnName(), targetValue);

    List<Object> people = jdbcTemplate.query(query, (rs) ->
            new PersonFixtureStep3(
                    rs.getLong("id"),
                    rs.getString("nick_name"),
                    rs.getInt("old"),
                    rs.getString("email")
            ));
    assertThat(people).contains(PersonInstances.첫번째사람);

  }
}
