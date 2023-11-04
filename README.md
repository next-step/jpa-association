# jpa-association


## 0단계 - 기본 코드 준비
- [X] 기존 코드 가져오기
- [X] 기존 코드 개선점 수정

## 1단계 - OneToMany (FetchType.EAGER)

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
- [ ] Join Query 만들기
- [ ] Join Query 를 만들어 Entity 화 해보기
