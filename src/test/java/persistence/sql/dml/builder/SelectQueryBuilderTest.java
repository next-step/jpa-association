package persistence.sql.dml.builder;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.JdbcPersistenceContext;
import persistence.sql.ddl.builder.BuilderTest;
import persistence.sql.fixture.PersonFixtureStep3;
import persistence.sql.fixture.PersonInstances;

@DisplayName("2. 요구사항 SELECT 구현하기, 3. 요구사항 WHERE 구현하기")
public class SelectQueryBuilderTest extends BuilderTest {

  public static final long ID = 31L;
  public static final long ID1 = 32L;
  PersonFixtureStep3 첫번째사람 = new PersonFixtureStep3(ID, "제임스", 21, "sdafij@gmail.com");
  PersonFixtureStep3 두번째사람 = new PersonFixtureStep3(ID1, "사이먼", 23, "sdafij@gmail.com");
  @BeforeEach
  void insert() {
    persistenceContext = new JdbcPersistenceContext();

    String queryFirst = insertQueryBuilder.createInsertQuery(meta.getTableName(),
        meta.getColumnClauseWithId(), String.join(DELIMITER,String.valueOf(ID), meta.getValueClause(첫번째사람)));
    String querySecond = insertQueryBuilder.createInsertQuery(meta.getTableName(),
        meta.getColumnClauseWithId(), String.join(DELIMITER,String.valueOf(ID1), meta.getValueClause(두번째사람)));

    jdbcTemplate.execute(queryFirst);
    jdbcTemplate.execute(querySecond);
  }
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
    Long targetValue = 31L;

    String query = selectQueryBuilder.createSelectByFieldQuery(meta.getColumnClauseWithId(), meta.getTableName(), meta.getPrimaryKeyColumn().getDBColumnName(), targetValue);

    assertThat(query).isEqualTo("SELECT id,nick_name,old,email FROM USERS WHERE id=31;");
  }

  @Test
  @DisplayName("Select 쿼리 실행시에 Entity들이 반환됩니다.")
  public void selectDMLfromEntityWhereDatabase() {
    SelectQueryBuilder selectQueryBuilder = new SelectQueryBuilder();
    Long targetValue = 31L;

    String query = selectQueryBuilder.createSelectByFieldQuery(meta.getColumnClauseWithId(), meta.getTableName(), meta.getPrimaryKeyColumn().getDBColumnName(), targetValue);

    List<Object> people = jdbcTemplate.query(query, (rs) ->
            new PersonFixtureStep3(
                    rs.getLong("id"),
                    rs.getString("nick_name"),
                    rs.getInt("old"),
                    rs.getString("email")
            ));
    assertThat(people).contains(첫번째사람);

  }
}
