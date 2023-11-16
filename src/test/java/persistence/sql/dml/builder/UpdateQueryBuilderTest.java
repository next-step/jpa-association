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

public class UpdateQueryBuilderTest extends BuilderTest {

  public static final long ID = 41L;
  public static final long ID1 = 42L;
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
  @DisplayName("UPDATE SQL 구문을 생성합니다.")
  public void generateUpdateDML() {
    UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder();

    String query = updateQueryBuilder.createUpdateQuery("USERS", List.of("nick_name"),
        List.of("'value2'"), "ID", "41");

    assertThat(query).isEqualTo(
        "UPDATE USERS SET nick_name = 'value2' WHERE ID = 41;");
  }

  @Test
  @DisplayName("UPDATE 실행하고 SELECT 시에 Entity가 변경되어있습니다.")
  public void insertDMLfromEntity() {
    PersonFixtureStep3 afterUpdatedPerson = new PersonFixtureStep3(41L, "value2", 21,
        "sdafij@gmail.com");
    UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder();

    String query = updateQueryBuilder.createUpdateQuery("USERS", List.of("nick_name"),
        List.of("'value2'"), "ID", "41");

    jdbcTemplate.execute(query);

    List<Object> people = jdbcTemplate.query("SELECT id,nick_name,old,email FROM USERS WHERE id=41;",
        (rs) ->
            new PersonFixtureStep3(
                rs.getLong("id"),
                rs.getString("nick_name"),
                rs.getInt("old"),
                rs.getString("email")
            ));
    assertThat(people.get(0)).isEqualTo(afterUpdatedPerson);
  }
}
