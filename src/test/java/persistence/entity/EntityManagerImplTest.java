package persistence.entity;

import database.DatabaseServer;
import database.H2;
import example.entity.Order;
import example.entity.OrderItem;
import example.entity.Person;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.EntityScanner;

import java.sql.SQLException;
import java.util.UUID;

public class EntityManagerImplTest {
    private static final Logger logger = LoggerFactory.getLogger(EntityManagerImplTest.class);

    private static DatabaseServer server;
    private static JdbcTemplate jdbcTemplate;
    private static EntityScanner entityScanner;

    @BeforeEach
    void init() throws SQLException {
        server = new H2();
        server.start();

        jdbcTemplate = new JdbcTemplate(server.getConnection());

        entityScanner = new EntityScanner();
        entityScanner.scan("example.entity");
        entityScanner.getDdlCreateQueries().forEach(jdbcTemplate::execute);
    }

    @AfterEach
    void teardown() {
        entityScanner.getDdlDropQueries().forEach(jdbcTemplate::execute);
        server.stop();
    }

    @Test
    @DisplayName("USERS 테이블 생성 > 데이터 저장 > 조회 테스트")
    void persistAndFindAndRemoveTest() {
        EntityManagerImpl entityManagerImpl = new EntityManagerImpl(jdbcTemplate);
        entityManagerImpl.beginTransaction();

        Person inserting = new Person();
        inserting.setName("이름");
        inserting.setAge(15);
        inserting.setEmail("이메일@이메일.이메일");

        entityManagerImpl.persist(inserting);

        Person found = (Person) entityManagerImpl.find(Person.class, 1L);

        logger.debug("Found : {}", found);

        entityManagerImpl.remove(found);
        entityManagerImpl.commitTransaction();
    }

    @Test
    @DisplayName("USERS 테이블 생성 > 데이터 저장 > 조회 테스트")
    void persistAndUpdateTest() {
        EntityManagerImpl entityManagerImpl = new EntityManagerImpl(jdbcTemplate);
        entityManagerImpl.beginTransaction();

        Person inserting = new Person();
        inserting.setName("이름");
        inserting.setAge(15);
        inserting.setEmail("이메일@이메일.이메일");

        entityManagerImpl.persist(inserting);

        Person found = (Person) entityManagerImpl.find(Person.class, 1L);

        logger.debug("Found : {}", found);

        found.setName("뉴이름");
        found.setAge(1500);
        found.setEmail("뉴이메일@뉴이메일.뉴이메일");

        entityManagerImpl.persist(found);

        Person reFound = (Person) entityManagerImpl.find(Person.class, 1L);

        logger.debug("Re found : {}", reFound);
        entityManagerImpl.commitTransaction();
    }

    @Test
    @DisplayName("generated key 테스트")
    void generatedKeyTest() {
        EntityManagerImpl entityManager = new EntityManagerImpl(jdbcTemplate);
        entityManager.beginTransaction();

        Person person = new Person();
        person.setName("김이박");
        person.setAge(17);
        person.setEmail("email@email.email");

        entityManager.persist(person);

        Object found = entityManager.find(Person.class, 1L);

        logger.debug("Found by generated key : {}", found);

        entityManager.commitTransaction();
    }

    @Test
    @DisplayName("트랜잭션 커밋 후 업데이트 쿼리 실행 여부 테스트")
    void updateInTransactionTest() {
        EntityManagerImpl entityManagerImpl = new EntityManagerImpl(jdbcTemplate);
        entityManagerImpl.beginTransaction();

        Person inserting = new Person();
        inserting.setName("업데이트 대상");
        inserting.setAge(99);
        inserting.setEmail("업데이트@대상.이메일");

        logger.debug("(1) insert...");
        entityManagerImpl.persist(inserting);

        Person inContextPerson = (Person) entityManagerImpl.find(Person.class, 1L);
        logger.debug("(2) find from persistence context : {}", inContextPerson);

        inContextPerson.setName("업데이트가 됐다");
        inContextPerson.setAge(999);
        inContextPerson.setEmail("업데이트가@됐다.이메일");

        logger.debug("(3) update in transaction : {}", inContextPerson);

        logger.debug("(4) transation commit...");
        entityManagerImpl.commitTransaction();

        EntityLoader entityLoader = new EntityLoader(jdbcTemplate);
        Object updatedPersonFromDB = entityLoader.find(Person.class, 1L);
        logger.debug("(5) find from db directly after commit : {}", updatedPersonFromDB);
    }

    @Test
    @DisplayName("트랜잭션 커밋 후 삭제 쿼리 실행 여부 테스트")
    void removeInTransactionTest() {
        EntityManagerImpl entityManagerImpl = new EntityManagerImpl(jdbcTemplate);
        entityManagerImpl.beginTransaction();

        Person inserting = new Person();
        inserting.setName("삭제 대상");
        inserting.setAge(99);
        inserting.setEmail("삭제@대상.이메일");

        logger.debug("(1) insert...");
        entityManagerImpl.persist(inserting);

        Person inContextPerson = (Person) entityManagerImpl.find(Person.class, 1L);
        logger.debug("(2) find from persistence context : {}", inContextPerson);

        logger.debug("(3) remove from persistence context...");
        entityManagerImpl.remove(inContextPerson);

        logger.debug("(4) transation commit...");
        entityManagerImpl.commitTransaction();

        logger.debug("(5) find after transaction commit...");
        EntityLoader entityLoader = new EntityLoader(jdbcTemplate);
        Assertions.assertThrows(RuntimeException.class, () -> entityLoader.find(Person.class, 1L));
    }

    @Test
    @DisplayName("test")
    void test() {
        EntityManagerImpl entityManagerImpl = new EntityManagerImpl(jdbcTemplate);
        entityManagerImpl.beginTransaction();

        Order insertingOrder = new Order();
        insertingOrder.setOrderNumber(UUID.randomUUID().toString());

        OrderItem insertingOrderItem1 = new OrderItem();
        insertingOrderItem1.setProduct("P-001");
        insertingOrderItem1.setQuantity(10);
        insertingOrderItem1.setOrderId(1L);

        OrderItem insertingOrderItem2 = new OrderItem();
        insertingOrderItem2.setProduct("P-002");
        insertingOrderItem2.setQuantity(15);
        insertingOrderItem2.setOrderId(1L);

        entityManagerImpl.persist(insertingOrder);
        entityManagerImpl.persist(insertingOrderItem1);
        entityManagerImpl.persist(insertingOrderItem2);

        Object o = entityManagerImpl.find(Order.class, 1L);

        logger.debug("{}", o);
    }
}
