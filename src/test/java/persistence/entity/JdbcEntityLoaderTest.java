package persistence.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.ddl.builder.BuilderTest;
import persistence.sql.fixture.PersonFixtureStep3;

public class JdbcEntityLoaderTest extends BuilderTest {

  public static final long ID = 50L;
  public static final long ID1 = 51L;
  PersonFixtureStep3 첫번째사람 = new PersonFixtureStep3(ID, "제임스", 21, "sdafij@gmail.com");
  PersonFixtureStep3 두번째사람 = new PersonFixtureStep3(ID1, "사이먼", 23, "sdafij@gmail.com");

  @BeforeEach
  void insert() {
    persistenceContext = new JdbcPersistenceContext();

    String queryFirst = insertQueryBuilder.createInsertQuery(meta.getTableName(),
        meta.getColumnClauseWithId(), String.join(DELIMITER, String.valueOf(ID), meta.getValueClause(첫번째사람)));
    String querySecond = insertQueryBuilder.createInsertQuery(meta.getTableName(),
        meta.getColumnClauseWithId(), String.join(DELIMITER, String.valueOf(ID1), meta.getValueClause(두번째사람)));

    jdbcTemplate.execute(queryFirst);
    jdbcTemplate.execute(querySecond);
  }

  @Test
  @DisplayName("Loader를 이용해서 find 합니다.")
  public void findEntity() {
    JdbcEntityLoader<PersonFixtureStep3> jdbcEntityLoader = new JdbcEntityLoader<>(PersonFixtureStep3.class, connection);
    PersonFixtureStep3 person = jdbcEntityLoader.load(ID1).get();

    assertThat(person).isEqualTo(두번째사람);
  }

  @Test
  @DisplayName("Loader를 이용해서 findAll 합니다.")
  public void findAllEntity() {
    JdbcEntityLoader<PersonFixtureStep3> jdbcEntityLoader = new JdbcEntityLoader<>(PersonFixtureStep3.class, connection);

    List<PersonFixtureStep3> people = jdbcEntityLoader.findAll();

    assertThat(people).hasSize(2);
  }
}
