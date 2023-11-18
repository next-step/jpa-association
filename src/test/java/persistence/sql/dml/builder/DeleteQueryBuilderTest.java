package persistence.sql.dml.builder;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.JdbcPersistenceContext;
import persistence.sql.ddl.builder.BuilderTest;
import persistence.sql.fixture.PersonFixtureStep3;

@DisplayName("4. 요구사항 DELETE 구현하기")
public class DeleteQueryBuilderTest extends BuilderTest {

  public static final long TARGET_VALUE = 1L;
  public static final long ID = 10L;
  public static final long ID1 = 11L;

  PersonFixtureStep3 첫번째사람 = new PersonFixtureStep3(ID, "제임스", 21, "sdafij@gmail.com");
  PersonFixtureStep3 두번째사람 = new PersonFixtureStep3(ID1, "사이먼", 23, "sdafij@gmail.com");

  @BeforeEach
  void insert() {
    persistenceContext = new JdbcPersistenceContext();

    String queryFirst = insertQueryBuilder.createInsertQuery(meta.getTableName(),
        meta.getColumnClauseWithId(),
        String.join(DELIMITER, String.valueOf(ID), meta.getValueClause(첫번째사람)));
    String querySecond = insertQueryBuilder.createInsertQuery(meta.getTableName(),
        meta.getColumnClauseWithId(),
        String.join(DELIMITER, String.valueOf(ID1), meta.getValueClause(두번째사람)));

    jdbcTemplate.execute(queryFirst);
    jdbcTemplate.execute(querySecond);
  }

  @Test
  @DisplayName("Delete SQL 구문을 생성합니다.")
  public void deleteDMLfromEntity() {
    DeleteQueryBuilder deleteQueryBuilder = new DeleteQueryBuilder();

    String query = deleteQueryBuilder.createDeleteQuery("USERS", "id", TARGET_VALUE);

    assertThat(query).isEqualTo("DELETE FROM USERS WHERE id = 1;");
  }


  @Test
  @DisplayName("Delete SQL 구문을 실행합니다.")
  public void deleteDMLfromEntitySQL() {
    DeleteQueryBuilder deleteQueryBuilder = new DeleteQueryBuilder();
    SelectQueryBuilder selectQueryBuilder = new SelectQueryBuilder();
    Long targetId = 10L;

    String queryDelete = deleteQueryBuilder.createDeleteQuery(meta.getTableName(),
        meta.getPrimaryKeyColumn().getDBColumnName(), targetId);
    jdbcTemplate.execute(queryDelete);

    String querySelect = selectQueryBuilder.createSelectByFieldQuery(meta.getColumnClause(),
        meta.getTableName(), meta.getPrimaryKeyColumn().getDBColumnName(), targetId);
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
