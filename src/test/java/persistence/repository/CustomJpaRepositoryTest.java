package persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.JdbcEntityManager;
import persistence.entity.JdbcPersistenceContext;
import persistence.sql.ddl.builder.BuilderTest;
import persistence.sql.fixture.PersonFixtureStep3;
import persistence.sql.fixture.PersonInstances;

public class CustomJpaRepositoryTest extends BuilderTest {
  public static final long ID = 80L;
  public static final long ID1 = 81L;
  public static final long ID2 = 82L;
  PersonFixtureStep3 첫번째사람 = new PersonFixtureStep3(ID, "제임스", 21, "sdafij@gmail.com");
  PersonFixtureStep3 두번째사람 = new PersonFixtureStep3(ID1, "사이먼", 23, "sdafij@gmail.com");
  PersonFixtureStep3 세번째사람 = new PersonFixtureStep3(ID2, "사이먼", 23, "sdafij@gmail.com");

  @BeforeEach
  void insert() {
    persistenceContext = new JdbcPersistenceContext();

  }
  private CustomJpaRepository<PersonFixtureStep3, Long> customJpaRepository;
  @Test
  @DisplayName("entity를 저장합니다.")
  void saveEntity(){
    customJpaRepository = new CustomJpaRepository<>(new JdbcEntityManager(connection, persistenceContext,
        entityEntry));
    PersonFixtureStep3 person = customJpaRepository.save(세번째사람);

    PersonFixtureStep3 personFound = customJpaRepository.findById(person, person.getId());

    assertThat(personFound.getId()).isEqualTo(person.getId());
    assertThat(personFound.getName()).isEqualTo(person.getName());
  }

  @Test
  @DisplayName("영속화된 entity를 변경감지로 수정된 entity로 persist 합니다.")
  void persistUpdatedEntity(){
    customJpaRepository = new CustomJpaRepository<>(new JdbcEntityManager(connection, persistenceContext,
        entityEntry));
    PersonFixtureStep3 person = customJpaRepository.save(세번째사람);

    customJpaRepository.save(person);
    person.setName("사이먼팍");
    customJpaRepository.save(person);

    PersonFixtureStep3 personFound = customJpaRepository.findById(person, person.getId());
    assertThat(personFound.getId()).isEqualTo(person.getId());
    assertThat(personFound.getName()).isEqualTo(person.getName());
  }
}
