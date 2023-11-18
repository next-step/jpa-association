package persistence.sql.dml.builder;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.JdbcPersistenceContext;
import persistence.sql.ddl.builder.BuilderTest;
import persistence.sql.fixture.PersonFixtureStep3;

@DisplayName("1.요구사항 Insert 구현하기")
public class InsertQueryBuilderTest extends BuilderTest {


  public static final long ID = 21L;
  public static final long ID1 = 22L;

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
  @DisplayName("Insert SQL 구문을 생성합니다.")
  public void insertDMLfromEntity() {
    InsertQueryBuilder insertQueryBuilder = new InsertQueryBuilder();

    String query = insertQueryBuilder.createInsertQuery("USERS", "nick_name,old,email",
        "'제임스',21,'sdafij@gmail.com'");

    assertThat(query).isEqualTo(
        "INSERT INTO USERS (nick_name,old,email) values ('제임스',21,'sdafij@gmail.com');");
  }

  @Test
  @DisplayName("Insert SQL 구문을 생성하고 Select 쿼리 실행시에 Entity들이 반환됩니다.")
  public void insertDMLfromEntityDatabase() {

    List<Object> people = jdbcTemplate.query("select * from users", (rs) ->
        new PersonFixtureStep3(
            rs.getLong("id"),
            rs.getString("nick_name"),
            rs.getInt("old"),
            rs.getString("email")
        ));

    assertThat(people).contains(첫번째사람);
  }

}
