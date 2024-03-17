package persistence;

import database.DatabaseServer;
import database.H2;
import database.dialect.MySQLDialect;
import database.sql.ddl.Create;
import entity.Order;
import entity.OrderItem;
import jdbc.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.entity.EntityManager;
import persistence.entity.EntityManagerImpl;

public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        logger.info("Starting application...");
        try {
            final DatabaseServer server = new H2();
            server.start();

            final JdbcTemplate jdbcTemplate = new JdbcTemplate(server.getConnection());
            MySQLDialect dialect = MySQLDialect.getInstance();

            jdbcTemplate.execute(new Create(Order.class, dialect).buildQuery());
            jdbcTemplate.execute(new Create(OrderItem.class, dialect).buildQuery());
//
            EntityManager entityManager = EntityManagerImpl.from(jdbcTemplate, dialect);

            Order order = entityManager.persist(new Order("1234"));
            OrderItem orderItem1 = entityManager.persist(new OrderItem("product1", 5, order.getId()));
            OrderItem orderItem2 = entityManager.persist(new OrderItem("product20", 50, order.getId()));

            System.out.println(order);
            System.out.println(orderItem1);
            System.out.println(orderItem2);

            Order res = entityManager.find(Order.class, order.getId());
            System.out.println("--------");
            System.out.println(res);

            server.stop();
        } catch (Exception e) {
            logger.error("Error occurred", e);
        } finally {
            logger.info("Application finished");
        }
    }

}
