package persistence.sql.dml.builder;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.ddl.builder.BuilderTest;
import persistence.sql.fixture.PersonFixtureStep3;

@DisplayName("4. 요구사항 DELETE 구현하기")
public class DeleteQueryBuilderTest extends BuilderTest {

  @Test
  @DisplayName("Delete SQL 구문을 생성합니다.")
  public void deleteDMLfromEntity() {
    DeleteQueryBuilder deleteQueryBuilder = new DeleteQueryBuilder();

    String query = deleteQueryBuilder.createDeleteQuery("USERS", "id", 1L);

    assertThat(query).isEqualTo("DELETE FROM USERS WHERE id = 1;");
  }


  @Test
  @DisplayName("Delete SQL 구문을 실행합니다.")
  public void deleteDMLfromEntitySQL() {
    DeleteQueryBuilder deleteQueryBuilder = new DeleteQueryBuilder();
    SelectQueryBuilder selectQueryBuilder = new SelectQueryBuilder();
    Long targetId = 1L;

    String queryDelete = deleteQueryBuilder.createDeleteQuery(meta.getTableName(), meta.getPrimaryKeyColumn().getDBColumnName(), targetId);
    jdbcTemplate.execute(queryDelete);

    String querySelect = selectQueryBuilder.createSelectByFieldQuery(meta.getColumnClause(), meta.getTableName(), meta.getPrimaryKeyColumn().getDBColumnName(), targetId);
    List<Object> people = jdbcTemplate.query(querySelect, (rs) ->
            new PersonFixtureStep3(
                    rs.getLong("id"),
                    rs.getString("nick_name"),
                    rs.getInt("old"),
                    rs.getString("email")
            ));

    assertThat(people).isEmpty();
  }
}
