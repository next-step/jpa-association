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
import persistence.meta.MetaEntity;

class CollectionElementLoaderTest {
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
  void before(){
    MetaEntity<Order> parentMeta = MetaEntity.of(Order.class);
    MetaEntity<OrderItem> elementMeta = MetaEntity.of(OrderItem.class);

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
  void after(){

    jdbcTemplate.execute(
        "truncate table ORDERS RESTART IDENTITY"
    );
    jdbcTemplate.execute(
        "truncate table order_items RESTART IDENTITY"
    );


  }
  @Test
  @DisplayName("Fetch Type이 EAGER인 엔티티를 LOAD할 때, element 엔티티들도 Load 됩니다.")
  void entityLoadCollection(){
    CollectionElementLoader<Order> Loader = (CollectionElementLoader<Order>) CollectionElementLoader.of(Order.class, connection);
    Order order = Loader.load(1L).get();
    
    assertThat(order.orderItems).hasSize(2);
    assertThat(order.orderItems.get(0).product).isEqualTo("첫번째 음식");
    assertThat(order.orderItems.get(1).product).isEqualTo("두번째 음식");
  }

  @Entity
  @Table(name = "orders")
  public static class Order {

    @Id
    private Long id;

    private Long number;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    private List<OrderItem> orderItems;
  }

  @Entity
  @Table(name = "order_items")
  public static class OrderItem {

    @Id
    private Long id;

    private String product;

    private Long number;
  }
}


