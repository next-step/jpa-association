package persistence;

import database.DatabaseServer;
import database.H2;
import database.sql.dml.CustomSelect;
import entity.Order;
import entity.OrderItem;
import entity.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        logger.info("Starting application...");
        try {
            final DatabaseServer server = new H2();
            server.start();

//            final JdbcTemplate jdbcTemplate = new JdbcTemplate(server.getConnection());
            System.out.println(new CustomSelect(Order.class).buildQuery());
            System.out.println(new CustomSelect(Person.class).buildQuery());
            System.out.println(new CustomSelect(OrderItem.class).buildQuery());

//            MySQLDialect dialect = MySQLDialect.getInstance();
//            jdbcTemplate.execute(QueryBuilder.getInstance().buildCreateQuery(Order.class, dialect));
//            System.out.println(QueryBuilder.getInstance().buildCreateQuery(Order.class, dialect));
//            jdbcTemplate.execute(QueryBuilder.getInstance().buildCreateQuery(OrderItem.class, dialect));
//            System.out.println(QueryBuilder.getInstance().buildCreateQuery(OrderItem.class, dialect));
//
//            EntityManager entityManager = EntityManagerImpl.from(jdbcTemplate);
//            Order order = entityManager.persist(new Order("1234"));
//            OrderItem orderItem1 = entityManager.persist(new OrderItem("product1", 5));
//            OrderItem orderItem2 = entityManager.persist(new OrderItem("product2", 5));
//
//            System.out.println(order);
//            System.out.println(orderItem1);
//            System.out.println(orderItem2);
//
//            jdbcTemplate.execute(database.sql.dml.QueryBuilder.getInstance().buildCustomSelectQuery(Order.class));

            server.stop();
        } catch (Exception e) {
            logger.error("Error occurred", e);
        } finally {
            logger.info("Application finished");
        }
    }

}
