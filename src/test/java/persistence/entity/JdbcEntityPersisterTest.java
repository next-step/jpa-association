package persistence.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.ddl.builder.BuilderTest;
import persistence.sql.fixture.PersonFixtureStep3;

public class JdbcEntityPersisterTest extends BuilderTest {

  public static final long ID = 70L;
  public static final long ID1 = 71L;
  public static final long ID2 = 72L;
  PersonFixtureStep3 첫번째사람 = new PersonFixtureStep3(ID, "제임스", 21, "sdafij@gmail.com");
  PersonFixtureStep3 두번째사람 = new PersonFixtureStep3(ID1, "사이먼", 23, "sdafij@gmail.com");
  PersonFixtureStep3 세번째사람 = new PersonFixtureStep3(ID2, "사이먼", 23, "sdafij@gmail.com");
  @BeforeEach
  void insert() {
    persistenceContext = new JdbcPersistenceContext();

    String queryFirst = insertQueryBuilder.createInsertQuery(meta.getTableName(),
        meta.getColumnClauseWithId(), String.join(DELIMITER, String.valueOf(ID), meta.getValueClause(첫번째사람)));
    String querySecond = insertQueryBuilder.createInsertQuery(meta.getTableName(),
        meta.getColumnClauseWithId(), String.join(DELIMITER, String.valueOf(ID1), meta.getValueClause(두번째사람)));
    String queryThird = insertQueryBuilder.createInsertQuery(meta.getTableName(),
        meta.getColumnClauseWithId(), String.join(DELIMITER, String.valueOf(ID2), meta.getValueClause(세번째사람)));

    jdbcTemplate.execute(queryFirst);
    jdbcTemplate.execute(querySecond);
    jdbcTemplate.execute(queryThird);
  }
  @Test
  @DisplayName("Persister를 이용해서 insert 합니다.")
  public void persisterInsertEntity() {
    JdbcEntityManager jdbcEntityManager = new JdbcEntityManager(connection, persistenceContext,
        entityEntry);
    JdbcEntityPersister<PersonFixtureStep3> persister = new JdbcEntityPersister<>(PersonFixtureStep3.class, connection);
    PersonFixtureStep3 네번째사람 = new PersonFixtureStep3(3L, "헨드릭스", 24, "sdafij@gmail.com");

    persister.insert(네번째사람);

    PersonFixtureStep3 person = jdbcEntityManager.find(PersonFixtureStep3.class, 3L);

    assertThat(person).isEqualTo(네번째사람);
  }
  @Test
  @DisplayName("Persister를 이용해서 update 합니다.")
  public void persisterUpdateEntity() {
    JdbcEntityManager jdbcEntityManager = new JdbcEntityManager(connection, persistenceContext, entityEntry);
    JdbcEntityPersister<PersonFixtureStep3> persister = new JdbcEntityPersister<>(PersonFixtureStep3.class, connection);
    PersonFixtureStep3 업데이트된세번째사람 = new PersonFixtureStep3(ID2, "헨드릭스", 24, "sdafij@gmail.com");

    boolean execute = persister.update(업데이트된세번째사람);

    PersonFixtureStep3 person = jdbcEntityManager.find(PersonFixtureStep3.class, ID2);

    assertThat(execute).isEqualTo(false);
    assertThat(person).isEqualTo(업데이트된세번째사람);
  }

  @Test
  @DisplayName("Persister를 이용해서 delete 합니다.")
  public void persisterDeleteEntity() {
    JdbcEntityManager jdbcEntityManager = new JdbcEntityManager(connection, persistenceContext,
        entityEntry);
    JdbcEntityPersister<PersonFixtureStep3> persister = new JdbcEntityPersister<>(PersonFixtureStep3.class, connection);

    persister.delete(두번째사람);

    assertThat(jdbcEntityManager.find(PersonFixtureStep3.class, ID1)).isEqualTo(null);
  }

  @Test
  @DisplayName("Persister를 이용해서 존재하지 않는 entity를 remove 되지 않습니다.")
  public void removeNotExistingEntity() {
    JdbcEntityManager jdbcEntityManager = new JdbcEntityManager(connection, persistenceContext,
        entityEntry);
    JdbcEntityPersister<PersonFixtureStep3> persister = new JdbcEntityPersister<>(PersonFixtureStep3.class, connection);
    PersonFixtureStep3 다섯번째사람 = new PersonFixtureStep3(5L, "버락", 24, "sdafij@gmail.com");

    Throwable thrown = catchThrowable(() -> {
      persister.delete(다섯번째사람);
    });

    assertThat(thrown).isInstanceOf(RuntimeException.class);
    assertThat(thrown.getMessage()).isEqualTo("해당 객체는 존재 하지 않습니다.");
  }

  @Test
  @DisplayName("Persister를 이용해서 존재하지 않는 entity를 update 되지 않습니다.")
  public void updateNotExistingEntity() {
    JdbcEntityManager jdbcEntityManager = new JdbcEntityManager(connection, persistenceContext,
        entityEntry);
    JdbcEntityPersister<PersonFixtureStep3> persister = new JdbcEntityPersister<>(PersonFixtureStep3.class, connection);
    PersonFixtureStep3 다섯번째사람 = new PersonFixtureStep3(5L, "버락", 24, "sdafij@gmail.com");

    Throwable thrown = catchThrowable(() -> {
      persister.update(다섯번째사람);
    });

    assertThat(thrown).isInstanceOf(RuntimeException.class);
    assertThat(thrown.getMessage()).isEqualTo("해당 객체는 존재 하지 않습니다.");
  }
}
