package persistence.sql.dml.builder;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.meta.MetaEntity;
import persistence.sql.ddl.builder.BuilderTest;
import persistence.sql.fixture.PersonFixtureStep3;
import persistence.sql.fixture.PersonInstances;

@DisplayName("1.요구사항 Insert 구현하기")
public class InsertQueryBuilderTest extends BuilderTest {

  @Test
  @DisplayName("Insert SQL 구문을 생성합니다.")
  public void insertDMLfromEntity() {
    InsertQueryBuilder insertQueryBuilder = new InsertQueryBuilder();

    String query = insertQueryBuilder.createInsertQuery("USERS", "nick_name,old,email", "'제임스',21,'sdafij@gmail.com'");

    assertThat(query).isEqualTo(
        "INSERT INTO USERS (nick_name,old,email) values ('제임스',21,'sdafij@gmail.com');");
  }

  @Test
  @DisplayName("Insert SQL 구문을 생성하고 Select 쿼리 실행시에 Entity들이 반환됩니다.")
  public void insertDMLfromEntityDatabase() throws SQLException {

    InsertQueryBuilder insertQueryBuilder = new InsertQueryBuilder();
    person = PersonFixtureStep3.class;
    meta = MetaEntity.of(person);
    String query = insertQueryBuilder.createInsertQuery(meta.getTableName(), meta.getColumnClause(), meta.getValueClause(PersonInstances.첫번째사람));

    jdbcTemplate.execute(query);

    List<Object> people = jdbcTemplate.query("select * from users", (rs) ->
        new PersonFixtureStep3(
            rs.getLong("id"),
            rs.getString("nick_name"),
            rs.getInt("old"),
            rs.getString("email")
        ));
    assertThat(people).contains(PersonInstances.첫번째사람);

  }

}
