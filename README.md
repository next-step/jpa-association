# jpa-association

### step 1 - OneToMany (FetchType.EAGER)

- [x] 요구사항 1 - Join Query 만들기
  - ```angular2html
    select orders.id, orders.order_number, order_items.id, order_items.produce, order_items.quantity
    from orders
    join orderItem on orders.id = order_items.order_id
    where orders.id = ?;
    ```
- [x] 요구사항 2 - Join Query 를 만들어 Entity 화 해보기

### step 2 - LazyLoading by Proxy

- [x] 요구사항 1 - CGLib Proxy 적용
  - build.gradle 파일에 implementation 'cglib:cglib:3.3.0' 를 추가
  - Hello 예제에서 Hello 인터페이스가 없고 구현체밖에 없다. 이에 대한 Proxy를 생성해 대문자로 반환하도록 한다.
- [ ] 요구사항 2 - 조회 시 프록시 객체를 사용해 적용해보자
  - cglib proxy에서 제공해주는 다양한 콜백중 적절한 콜백 메서드를 찾아서 사용해보자
  - 테스트 코드를 통해서 프록시 객체를 호출 했을 때와 하지 않았을 때를 비교해보자
