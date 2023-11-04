# jpa-association

## Association

### 1단계 - OneToMany (FetchType.EAGER)
```java
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderNumber;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    private List<OrderItem> orderItems;
}


@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String product;

    private Integer quantity;
}
```
> 하나의 주문(Order)에 여러 개의 주문 아이템(OrderItem)이 속할 수 있는 경우를 생각해보겠습니다.
이때, 주문과 주문 아이템은 One-to-Many 단방향 관계를 가지게 됩니다.
즉, 하나의 주문은 여러 개의 주문 아이템을 가질 수 있지만, 각 주문 아이템은 어떤 주문에 속해 있는지 알 수 없습니다.

- 요구사항 1 - Join Query 만들기

임의의 데이터를 넣어 데이터를 가져와보자 - Sql 쿼리문을 수정해보자
```java
public class CustomSelect {

}
```
- [x] @OneToMany(fetch) 정보를 Metadata 에 저장한다.
- [x] @JoinColumn 정보를 Metadata 에 저장한다.
- [x] @OneToMany Generic 클래스 정보를 Metadata 에 저장한다.
- [x] Entity `Order` 와 `OrderItem` 의 Metadata 를 이용해 create 쿼리를 만든다.
<br> 목표 쿼리문 : `create table order_items (id bigint not null auto_increment,product varchar(255),quantity int,order_id bigint,foreign key(order_id) references order (id),CONSTRAINT PK_order_items PRIMARY KEY (id))`
- [x] Entity `Order` 와 `OrderItem` 의 Metadata 를 이용해 select 쿼리를 만든다.
<br> 목표 쿼리문 : `select orders.id, orders.orderNumber, order_items.id, order_items.product, order_items.quantity from orders left join order_items on orders.id = order_items.order_id where orders.id=1`

- 요구사항 2 - Join Query 를 만들어 Entity 화 해보기
<br>`FetchType.EAGER 인 경우`
- [x] RowMapper 를 left join 시에도 적용 가능하게 변경
- [x] Order 조회해서 Entity 화 및 검증
