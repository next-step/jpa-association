package persistence.entity;

import static org.assertj.core.api.Assertions.assertThat;

import database.DatabaseServer;
import database.H2;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


public class CollectionElementLazyLoaderTest {

  public static DatabaseServer server;
  public static JdbcTemplate jdbcTemplate;
  public static Connection connection;

  @BeforeAll
  static void setup() throws SQLException {
    server = new H2();
    server.start();
    connection = server.getConnection();
    jdbcTemplate = new JdbcTemplate(connection);
  }

  @BeforeEach
  void before() {
    jdbcTemplate.execute(
        "CREATE TABLE IF NOT EXISTS orders (id BIGINT PRIMARY KEY, number BIGINT)"
    );
    jdbcTemplate.execute(
        "CREATE TABLE IF NOT EXISTS order_items "
            + "(id BIGINT PRIMARY KEY, order_id BIGINT, number BIGINT, product VARCHAR)"
    );

    jdbcTemplate.execute(
        "insert into orders (id, number) values (1, 123)"
    );
    jdbcTemplate.execute(
        "insert into order_items (id, order_id, product, number) values (1, 1, '첫번째 음식', 2323)"
    );
    jdbcTemplate.execute(
        "insert into order_items (id, order_id, product, number) values (2, 1, '두번째 음식', 123)"
    );

  }

  @AfterEach
  void after() {

    jdbcTemplate.execute(
        "truncate table ORDERS RESTART IDENTITY"
    );
    jdbcTemplate.execute(
        "truncate table order_items RESTART IDENTITY"
    );


  }


  @Test
  @DisplayName("Fetch Type이 EAGER인 엔티티를 LOAD할 때, element 엔티티들도 Load 됩니다.")
  void entityLoadByIdsCollection() {
    CollectionElementLoader<CollectionElementLazyLoaderTest.Order> Loader = (CollectionElementLoader<CollectionElementLazyLoaderTest.Order>) CollectionElementLoader.of(
        CollectionElementLazyLoaderTest.Order.class, connection);
    List<CollectionElementLazyLoaderTest.Order> orders = Loader.loadByIds(List.of(1L, 2L));

    assertThat(orders).hasSize(1);
    assertThat(orders.get(0).orderItems.get(0).product).isEqualTo("첫번째 음식");
    assertThat(orders.get(0).orderItems.get(1).product).isEqualTo("두번째 음식");
  }

  @Test
  @DisplayName("Fetch Type이 Lazy인 엔티티를 LOAD할 때, element 엔티티들은 proxy가 됩니다.")
  void entityLoadByIdsLazyCollection() {
    CollectionElementLoader<CollectionElementLazyLoaderTest.Order> Loader = (CollectionElementLoader<CollectionElementLazyLoaderTest.Order>) CollectionElementLoader.of(
        CollectionElementLazyLoaderTest.Order.class, connection);
    List<CollectionElementLazyLoaderTest.Order> orders = Loader.loadByIds(List.of(1L, 2L));

    assertThat(orders).hasSize(1);
    assertThat(orders.get(0).orderItems.get(0).product).isEqualTo("첫번째 음식");
    assertThat(orders.get(0).orderItems.get(1).product).isEqualTo("두번째 음식");
  }

  @Entity
  @Table(name = "orders")
  public static class Order {

    @Id
    public Long id;

    public Long number;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    public List<CollectionElementLazyLoaderTest.OrderItem> orderItems;
  }

  @Entity
  @Table(name = "order_items")
  public static class OrderItem {

    @Id
    public Long id;

    public String product;

    public Long number;
  }
}
