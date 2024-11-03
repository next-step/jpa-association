package builder;

import builder.dml.EntityData;
import builder.dml.builder.*;
import database.H2DBConnection;
import entity.Order;
import entity.Person;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.EntityManager;
import persistence.EntityManagerImpl;
import persistence.PersistenceContext;
import persistence.PersistenceContextImpl;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

/*
- insert 쿼리 문자열 생성하기
- findAll 쿼리 문자열 생성하기
- findById 쿼리 문자열 생성하기
- Object 인스턴스를 받아 findById 쿼리 문자열 생성한다.
- Object 인스턴스를 받아 UPDATE 쿼리 문자열 생성한다.
- deleteById 쿼리 문자열 생성하기
- Object를 받아 deleteById 쿼리 문자열 생성한다.
*/
class DMLBuilderTest {

    @BeforeEach
    void setUp() throws SQLException {
        H2DBConnection h2DBConnection = new H2DBConnection();
        JdbcTemplate jdbcTemplate = h2DBConnection.start();

        PersistenceContext persistenceContext = new PersistenceContextImpl();

        EntityManager entityManager = new EntityManagerImpl(persistenceContext, jdbcTemplate);
    }

    @DisplayName("Insert 쿼리 문자열 생성하기")
    @Test
    void buildInsertTest() {
        //given
        Person person = new Person(1L, "sangki", 29, "test@test.com", 1);

        InsertQueryBuilder queryBuilder = new InsertQueryBuilder();

        EntityData entityData = EntityData.createEntityData(person);

        //when, then
        assertThat(queryBuilder.buildQuery(entityData.getTableName(), entityData.getEntityColumn()))
                .isEqualTo("INSERT INTO users (id, nick_name, old, email) VALUES (1, 'sangki', 29, 'test@test.com');");
    }

    @DisplayName("findAll 쿼리 문자열 생성하기")
    @Test
    void buildFindAllTest() {
        //given
        Person person = new Person(1L, "sangki", 29, "test@test.com", 1);

        SelectAllQueryBuilder queryBuilder = new SelectAllQueryBuilder();

        //when, then
        assertThat(queryBuilder.buildQuery(EntityData.createEntityData(person)))
                .isEqualTo("SELECT id, nick_name, old, email FROM users;");
    }

    @DisplayName("findById 쿼리 문자열 생성하기")
    @Test
    void buildFindByIdTest() {
        //given
        SelectByIdQueryBuilder queryBuilder = new SelectByIdQueryBuilder();
        //when, then
        assertThat(queryBuilder.buildQuery(EntityData.createEntityData(Person.class, 1)))
                .isEqualTo("SELECT id, nick_name, old, email FROM users WHERE id = 1;");
    }

    @DisplayName("findById 쿼리 문자열 생성할시 id가 String이면 작은따옴표로 묶어준다.")
    @Test
    void buildFindByIdStringTest() {
        //given
        SelectByIdQueryBuilder queryBuilder = new SelectByIdQueryBuilder();
        //when, then
        assertThat(queryBuilder.buildQuery(EntityData.createEntityData(Person.class, "sangki")))
                .isEqualTo("SELECT id, nick_name, old, email FROM users WHERE id = 'sangki';");
    }

    @DisplayName("Object 인스턴스를 받아 findById 쿼리 문자열 생성한다.")
    @Test
    void buildFindObjectStringTest() {
        //given
        Person person = new Person(1L, "sangki", 29, "test@test.com", 1);

        SelectByIdQueryBuilder queryBuilder = new SelectByIdQueryBuilder();

        //when, then
        assertThat(queryBuilder.buildQuery(EntityData.createEntityData(person)))
                .isEqualTo("SELECT id, nick_name, old, email FROM users WHERE id = 1;");
    }

    @DisplayName("Object 인스턴스를 받아 Update 쿼리 문자열 생성한다.")
    @Test
    void buildUpdateObjectStringTest() {
        //given
        Person person = new Person(1L, "sangki", 29, "test@test.com", 1);

        UpdateQueryBuilder queryBuilder = new UpdateQueryBuilder();
        //when, then
        assertThat(queryBuilder.buildQuery(EntityData.createEntityData(person)))
                .isEqualTo("UPDATE users SET nick_name='sangki', old=29, email='test@test.com' WHERE id = 1;");
    }

    @DisplayName("deleteById 쿼리 문자열 생성한다.")
    @Test
    void buildDeleteByIdTest() {
        //given
        DeleteQueryBuilder queryBuilder = new DeleteQueryBuilder();
        //when, then
        assertThat(queryBuilder.buildQuery(EntityData.createEntityData(Person.class, "sangki")))
                .isEqualTo("DELETE FROM users WHERE id = 'sangki';");
    }

    @DisplayName("Object를 받아 deleteById 쿼리 문자열 생성한다.")
    @Test
    void buildDeleteObjectTest() {
        //given
        Person person = new Person(1L, "sangki", 29, "test@test.com", 1);

        DeleteQueryBuilder queryBuilder = new DeleteQueryBuilder();
        //when, then
        assertThat(queryBuilder.buildQuery(EntityData.createEntityData(person)))
                .isEqualTo("DELETE FROM users WHERE id = 1;");
    }

    @DisplayName("@OneToMany가 포함되어있는 Entity를 finAll 쿼리를생성한다.")
    @Test
    void buildDMLBuilderJoinFindAllTest() {
        //given
        EntityData entityData = EntityData.createEntityData(Order.class);

        SelectAllQueryBuilder queryBuilder = new SelectAllQueryBuilder();

        //when, then
        assertThat(queryBuilder.buildQuery(entityData))
                .isEqualTo(
                        "SELECT orders_.id, orders_.orderNumber, " +
                                "order_items_.id, order_items_.order_id, order_items_.product, order_items_.quantity " +
                                "FROM orders orders_ " +
                                "JOIN order_items order_items_ " +
                                "ON orders_.id = order_items_.order_id;"
                );
    }

    @DisplayName("@OneToMany가 포함되어있는 Entity를 findById 쿼리를 생성한다.")
    @Test
    void buildDMLBuilderJoinFindByIdTest() {
        //given
        EntityData entityData = EntityData.createEntityData(Order.class, 1L);

        SelectByIdQueryBuilder queryBuilder = new SelectByIdQueryBuilder();

        //when, then
        assertThat(queryBuilder.buildQuery(entityData))
                .isEqualTo(
                        "SELECT orders_.id, orders_.orderNumber, " +
                                "order_items_.id, order_items_.order_id, order_items_.product, order_items_.quantity " +
                                "FROM orders orders_ " +
                                "JOIN order_items order_items_ " +
                                "ON orders_.id = order_items_.order_id " +
                                "WHERE orders_.id = 1;"
                );
    }

}
