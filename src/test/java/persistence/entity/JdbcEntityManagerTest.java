package persistence.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.ddl.builder.BuilderTest;
import persistence.sql.fixture.PersonFixtureStep3;

public class JdbcEntityManagerTest extends BuilderTest {
  public static final long ID = 60L;
  public static final long ID1 = 61L;
  public static final long ID2 = 62L;
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
  @DisplayName("EntityManager를 이용해서 find 합니다.")
  public void findEntity() {
    JdbcEntityManager jdbcEntityManager = new JdbcEntityManager(connection, persistenceContext,
        entityEntry);

    PersonFixtureStep3 person = jdbcEntityManager.find(PersonFixtureStep3.class, ID1);

    assertThat(person).isEqualTo(두번째사람);
    assertThat(entityEntry.getEntityStatus(두번째사람)).isEqualTo(EntityStatus.MANAGED);
  }

  @Test
  @DisplayName("EntityManager를 이용해서 persist 합니다.")
  public void persistEntity() {
    JdbcEntityManager jdbcEntityManager = new JdbcEntityManager(connection, persistenceContext, entityEntry);

    jdbcEntityManager.persist(세번째사람);

    assertThat(entityEntry.getEntityStatus(세번째사람)).isEqualTo(EntityStatus.MANAGED);

    PersonFixtureStep3 person = jdbcEntityManager.find(PersonFixtureStep3.class, ID2);

    assertThat(person).isEqualTo(세번째사람);
    assertThat(entityEntry.getEntityStatus(세번째사람)).isEqualTo(EntityStatus.LOADING);
  }

  @Test
  @DisplayName("EntityManager를 이용해서 remove 하고 조회하였을 때, 해당 row가 없습니다.")
  public void removeEntity() {
    JdbcEntityManager jdbcEntityManager = new JdbcEntityManager(connection, persistenceContext, entityEntry);

    jdbcEntityManager.remove(첫번째사람);
    PersonFixtureStep3 person = jdbcEntityManager.find(PersonFixtureStep3.class, ID);

    assertThat(person).isEqualTo(null);
    assertThat(entityEntry.getEntityStatus(첫번째사람)).isEqualTo(EntityStatus.GONE);
  }
  @Test
  @DisplayName("find시에 1차 캐시에 저장된 entity를 가져온다.")
  public void findEntityWithPersistenceContext() {
    JdbcEntityManager jdbcEntityManager = new JdbcEntityManager(connection, persistenceContext,
        entityEntry);

    jdbcEntityManager.persist(두번째사람);
    PersonFixtureStep3 person = jdbcEntityManager.find(PersonFixtureStep3.class, ID1);

    assertThat(person).isEqualTo(두번째사람);
    assertThat(person == 두번째사람).isEqualTo(true);
    assertThat(entityEntry.getEntityStatus(두번째사람)).isEqualTo(EntityStatus.LOADING);
  }

  @Test
  @DisplayName("persist시에 1차 캐시의 entity와 snapshot의 값이 다르면 더티체킹으로 업데이트한다.")
  public void persistDiffEntityWithPersistenceContext() {
    JdbcEntityManager jdbcEntityManager = new JdbcEntityManager(connection, persistenceContext,
        entityEntry);

    PersonFixtureStep3 person = jdbcEntityManager.find(PersonFixtureStep3.class, ID1);
    person.setName("Sichngpark");

    jdbcEntityManager.persist(person);
    
    PersonFixtureStep3 personInCache = jdbcEntityManager.find(PersonFixtureStep3.class, ID1);

    assertThat(personInCache.getName()).isEqualTo("Sichngpark");
  }
  
}
