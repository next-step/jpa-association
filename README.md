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
  - [ ] 전체 조회 쿼리 작성
    - [ ] JoinColumn 대응 
      - [ ] JoinColumn의 ID 값으로 조인을 한다.
  - [ ] id값을 가지고 조회하는 쿼리 작성 
- [ ] Join Query 를 만들어 Entity화 해보기
