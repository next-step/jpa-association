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
- [X] Join Query 만들기
  - [X] 전체 조회 쿼리 작성
    - [X] JoinColumn 대응 
      - [X] JoinColumn의 ID 값으로 조인을 한다.
  - [X] id값을 가지고 조회하는 쿼리 작성 
- [X] Join Query 를 만들어 Entity화 해보기

--- 
### 2단계 - LazyLoading by Proxy
- [X] 요구사항 1 - CGLib Proxy 적용
  - [X] 대문자로 반환하도록 한다.
  
- [ ] 요구사항 2 - 조회 시 프록시 객체를 사용해 적용해보자
  - 테스트 코드를 통해서 프록시 객체를 호출 했을 때와 하지 않았을 때를 비교해보자
